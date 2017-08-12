package org.archipelago.core.framework;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.archipelago.core.builder.*;
import org.archipelago.core.configurator.ArchipelagoConfig;
import org.archipelago.core.configurator.DatabaseConfig;
import org.archipelago.core.configurator.DatabaseType;
import org.archipelago.core.domain.*;
import org.archipelago.core.exception.CheckException;
import org.archipelago.core.util.ArchipelagoUtils;
import org.glassfish.jersey.client.authentication.HttpAuthenticationFeature;
import org.neo4j.driver.v1.*;
import org.neo4j.driver.v1.types.Node;
import org.neo4j.driver.v1.types.Path;
import org.neo4j.driver.v1.types.Relationship;
import org.reflections.ReflectionUtils;
import org.reflections.Reflections;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.net.URLEncoder;
import java.util.*;

import static org.neo4j.driver.v1.Values.parameters;
import static org.reflections.ReflectionUtils.withModifier;
import static org.reflections.ReflectionUtils.withPrefix;

/**
 * @author Gilles Bodart
 */
public class Archipelago implements AutoCloseable {

    public static final String CONFIG_LOCATION = "config.archipelago.json";
    public static final String ARCHIPELAGO_ID = "ArchipelagoId";

    public static final Logger LOGGER = LogManager.getLogger();

    private static Archipelago instance;

    private final ArchipelagoConfig archipelagoConfig;
    private final ObjectMapper OM = new ObjectMapper();
    private DatabaseType databaseType;
    private Driver driver;
    private ArchipelagoScriptBuilder builder;
    private Client jerseyClient;
    private WebTarget rootTarget;
    private Reflections reflections;
    private Map<Object, Object> sessionObject = new HashMap<>();

    private Archipelago() throws CheckException {
        // Initialization of the configuration
        this.archipelagoConfig = getConfig();
        DatabaseConfig dc = archipelagoConfig.getDatabase();
        // Personalisation of the connection

        this.databaseType = dc.getType();
        this.reflections = new Reflections(archipelagoConfig.getDomainRootPackage());
        switch (dc.getType()) {
            case NEO4J:
                if (!dc.getEmbedded()) {
                    String url = String.format("bolt://%s:%d", dc.getUrl(), dc.getPort());
                    LOGGER.info("Connecting to Neo4J remote server on " + url);
                    this.driver = GraphDatabase.driver(url, AuthTokens.basic(dc.getUsername(), dc.getPassword()));
                    LOGGER.info("Connected to Neo4J remote server");
                    this.builder = new Neo4JBuilder();
                } else {
                    //TODO EMBEDDED Neo4J
                }
                break;
            case ORIENT_DB:
                HttpAuthenticationFeature feature = HttpAuthenticationFeature.basic(dc.getUsername(), dc.getPassword());
                this.jerseyClient = ClientBuilder.newClient();
                this.jerseyClient.register(feature);
                String url = String.format("http://%s:%d/", dc.getUrl(), dc.getPort());
                LOGGER.info("Connecting to Orient remote server on " + url);
                this.rootTarget = jerseyClient.target(url);
                this.builder = new OrientDBBuilder();
                Response response = this.rootTarget.path(String.format("connect/%s", dc.getName())).request(MediaType.APPLICATION_JSON).get();
                if (response.getStatus() != 204) {
                    throw new CheckException("Unable to connect to Orient Remote server");
                } else {
                    LOGGER.info("Connected to Orient remote server");
                }
                break;
            default:
                throw new CheckException("Only NEO4J and ORIENT_DB are supported for the moment!");
        }
    }

    public static Archipelago getInstance() throws CheckException {
        if (null == instance) {
            instance = new Archipelago();
        }
        return instance;
    }

    public QueryBuilder getQueryBuilder() throws CheckException {
        switch (databaseType) {
            case NEO4J:
                return Neo4JQueryImpl.init();
            case ORIENT_DB:
                return OrientDBQueryImpl.init();
            default:
                throw new CheckException("Only NEO4J and ORIENT_DB are supported for the moment!");

        }
    }

    private ArchipelagoConfig getConfig() throws CheckException {
        ArchipelagoConfig ac = null;
        ObjectMapper om = new ObjectMapper();
        try {
            ac = om.readValue(new File(getClass().getClassLoader().getResource(CONFIG_LOCATION).getFile()),
                    ArchipelagoConfig.class);
        } catch (IOException e) {
            LOGGER.error(e.getMessage(), e);
            throw new CheckException("The config file have not been found, please have a 'config.archipelago.json' in your resource folder", e);
        }
        return ac;
    }

    /**
     * Persist an object into the database
     *
     * @param object the object to persist
     * @return the Id of the persisted object
     * @throws CheckException Object can't be null;
     */
    public void persist(Object object) throws CheckException {
        persist(object, 0);
    }

    /**
     * Persist an object into the database
     *
     * @param objects the objects to persist
     * @return the Id of the persisted object
     * @throws CheckException Object can't be null;
     */
    public void persist(List<Object> objects) throws CheckException {
        for (Object object : objects) {
            persist(object, 0);
        }
    }

    /**
     * Flush the objects stored in the session
     *
     * @return the amount of flushed elements
     */
    public int flushSessionObject() {
        int size = sessionObject.size();
        sessionObject.clear();
        return size;
    }

    /**
     * Persist an object in the database
     *
     * @param object   the object to persist
     * @param deepness current deepness of the introspection
     * @return the internal Id of the object (null in case or error)
     */
    private void persist(Object object, Integer deepness) throws CheckException {
        //TODO Transaction
        switch (databaseType) {
            case NEO4J:
                Object[] params = builder.fillCreate(object).toArray();
                try (Session s = this.driver.session()) {
                    s.run(builder.makeCreate(object), parameters(builder.fillCreate(object).toArray()));
                    //TODO Duplicate with scanForLink
                    if (deepness < archipelagoConfig.getDeepness()) {
                        List<RelationWrapper> relations = ArchipelagoUtils.getChilds(object);
                        relations.stream().forEach((relationWrapper -> {
                            Object to = relationWrapper.getTo();
                            if (null == getId(to)) {
                                try {
                                    persist(to, deepness + 1);
                                } catch (CheckException e) {
                                    LOGGER.error(e.getMessage(), e);
                                }
                            } else {
                                scanForLink(to, deepness + 1);
                            }
                            link(object, relationWrapper.getTo(), relationWrapper.getName(), relationWrapper.isBiDirectionnal());
                        }));
                    }
                } catch (Exception e) {
                    LOGGER.debug(String.format("Param [%s]", StringUtils.join(params, ';')));
                    throw e;
                }
                break;
            case ORIENT_DB:
                if (null == getId(object)) {
                    String json = builder.makeCreate(object);
                    Entity<?> entity = Entity.entity(json, MediaType.TEXT_PLAIN);
                    Response response = this.rootTarget
                            .path(String.format("document/%s", getConfig().getDatabase().getName()))
                            .request(MediaType.TEXT_PLAIN)
                            .post(entity);
                    String jsonResp = response.readEntity(String.class);
                    try {
                        OrientDBResponseWrapper odbrw = OM.readValue(jsonResp, OrientDBResponseWrapper.class);
                        sessionObject.put(object, odbrw.getId());
                    } catch (IOException e) {
                        try {
                            LOGGER.info(OM.readValue(jsonResp, OrientDBErrorWrapper.class));
                        } catch (IOException e1) {
                            LOGGER.error(e.getMessage(), e);
                            throw new CheckException(e1);
                        }
                    }
                }
                if (deepness < archipelagoConfig.getDeepness()) {
                    List<RelationWrapper> relations = ArchipelagoUtils.getChilds(object);
                    relations.stream().forEach((relationWrapper -> {
                        Object to = relationWrapper.getTo();
                        if (null == getId(to)) {
                            try {
                                persist(to, deepness + 1);
                            } catch (CheckException e) {
                                LOGGER.error(e.getMessage(), e);
                            }
                        } else {
                            scanForLink(to, deepness + 1);
                        }
                        link(object, relationWrapper.getTo(), relationWrapper.getName(), relationWrapper.isBiDirectionnal());
                    }));

                }
                break;
        }
    }

    private void scanForLink(Object object, Integer deepness) {
        if (deepness < archipelagoConfig.getDeepness()) {
            List<RelationWrapper> relations = ArchipelagoUtils.getChilds(object);
            relations.stream().forEach((relationWrapper -> {
                Object to = relationWrapper.getTo();
                if (null == getId(to)) {
                    try {
                        persist(to, deepness + 1);
                    } catch (CheckException e) {
                        LOGGER.error(e.getMessage(), e);
                    }
                } else {
                    scanForLink(to, deepness + 1);
                }
                link(object, relationWrapper.getTo(), relationWrapper.getName(), relationWrapper.isBiDirectionnal());
            }));
        }
    }

    public void link(Object first, Object second, Object descriptor, boolean biDirectional) {
        boolean haveDescriptor = null != descriptor;
        String name;
        if (haveDescriptor) {
            name = descriptor.getClass().getSimpleName();
        } else {
            name = String.format("%sTo%s", first.getClass().getSimpleName(), second.getClass().getSimpleName());
        }
        switch (databaseType) {
            case NEO4J:
                try (Session s = this.driver.session()) {
                    Integer idA = (Integer) getId(first);
                    Integer idB = (Integer) getId(second);
                    if (idA != null && idB != null) {
                        if (haveDescriptor) {
                            s.run(builder.makeRelation(idA, idB, name, descriptor), parameters(builder.fillCreate(descriptor).toArray()));
                            if (biDirectional) {
                                s.run(builder.makeRelation(idB, idA, name, descriptor), parameters(builder.fillCreate(descriptor).toArray()));
                            }
                        } else {
                            s.run(builder.makeRelation(idA, idB, name));
                            if (biDirectional) {
                                s.run(builder.makeRelation(idB, idA, name));
                            }
                        }
                    }
                }
                break;
            case ORIENT_DB:
                try {
                    String idA = (String) getId(first);
                    String idB = (String) getId(second);
                    if (idA != null && idB != null) {
                        String query = builder.makeRelation(idA, idB, name, descriptor);
                        LOGGER.debug(String.format("CREATE Relation from %s to %s : [%s]", idA, idB, query));
                        this.rootTarget
                                .path(String.format("command/%s/sql/%s", archipelagoConfig.getDatabase().getName(),
                                        URLEncoder.encode(query, "UTF-8")))
                                .request(MediaType.TEXT_PLAIN)
                                .post(Entity.entity("", MediaType.TEXT_PLAIN));
                        if (biDirectional) {
                            query = builder.makeRelation(idB, idA, name, descriptor);
                            LOGGER.debug(String.format("CREATE Relation from %s to %s : [%s]", idA, idB, query));
                            this.rootTarget
                                    .path(String.format("command/%s/sql/%s", archipelagoConfig.getDatabase().getName(),
                                            URLEncoder.encode(query, "UTF-8")))
                                    .request(MediaType.TEXT_PLAIN)
                                    .post(Entity.entity("", MediaType.TEXT_PLAIN));
                        }
                    }
                } catch (UnsupportedEncodingException e) {
                    LOGGER.error(e.getMessage(), e);
                }
                break;
        }

    }

    public void link(Object first, Object second, String descriptorName, boolean biDirectional) {
        DescriptorWrapper dw = new DescriptorWrapper(descriptorName);
        switch (databaseType) {
            case NEO4J:
                dw.setName("".equalsIgnoreCase(descriptorName) ? String.format("%sTo%s", first.getClass().getSimpleName(), second.getClass().getSimpleName()) : descriptorName);
                try (Session s = this.driver.session()) {
                    Integer idA = (Integer) getId(first);
                    Integer idB = (Integer) getId(second);
                    if (idA != null && idB != null) {
                        List<String> props = new ArrayList<>();
                        props.add("created");
                        s.run(builder.makeRelation(idA, idB, dw.getName(), props), parameters(builder.fillCreate(dw).toArray()));
                        if (biDirectional) {
                            s.run(builder.makeRelation(idB, idA, dw.getName(), props), parameters(builder.fillCreate(dw).toArray()));
                        }
                    }
                }
                break;
            case ORIENT_DB:
                try {
                    dw.setName("".equalsIgnoreCase(descriptorName) ? String.format("%sTo%s", first.getClass().getSimpleName(), second.getClass().getSimpleName()) : descriptorName);
                    String idA = (String) getId(first);
                    String idB = (String) getId(second);
                    if (idA != null && idB != null && !alreadyLinked(first, second, dw)) {
                        String query = builder.makeRelation(idA, idB, dw);
                        LOGGER.debug(String.format("CREATE Relation from %s to %s : [%s]", idA, idB, query));
                        this.rootTarget
                                .path(String.format("command/%s/sql/%s", archipelagoConfig.getDatabase().getName(),
                                        URLEncoder.encode(query, "UTF-8")))
                                .request(MediaType.TEXT_PLAIN)
                                .post(Entity.entity("", MediaType.TEXT_PLAIN));
                        if (biDirectional) {
                            query = builder.makeRelation(idB, idA, dw);
                            LOGGER.debug(String.format("CREATE Relation from %s to %s : [%s]", idA, idB, query));
                            this.rootTarget
                                    .path(String.format("command/%s/sql/%s", archipelagoConfig.getDatabase().getName(),
                                            URLEncoder.encode(query, "UTF-8")))
                                    .request(MediaType.TEXT_PLAIN)
                                    .post(Entity.entity("", MediaType.TEXT_PLAIN));
                        }
                    }
                } catch (UnsupportedEncodingException e) {
                    LOGGER.error(e.getMessage(), e);
                }
                break;
        }
    }

    private boolean alreadyLinked(Object first, Object second, DescriptorWrapper dw) {
        switch (databaseType) {
            case NEO4J:
                break;
            case ORIENT_DB:
                try {
                    String firstJson = this.rootTarget
                            .path(String.format("command/%s/sql/%s", archipelagoConfig.getDatabase().getName(),
                                    URLEncoder.encode(builder.makeMatch(first), "UTF-8")))
                            .request(MediaType.APPLICATION_JSON)
                            .get(String.class);
                    String idSecond = (String) getId(second);
                    OrientDBResultWrapper firstResult = null;

                    firstResult = OM.readValue(firstJson, OrientDBResultWrapper.class);

                    for (Map<String, Object> map : firstResult.getResult()) {
                        List<String> links = (List<String>) map.get(String.format("out_%s", dw.getName()));
                        if (null != links) {
                            for (String linked : links) {
                                String linkJson = this.rootTarget
                                        .path(String.format("command/%s/sql/%s", archipelagoConfig.getDatabase().getName(),
                                                URLEncoder.encode(String.format("SELECT FROM %s", linked), "UTF-8")))
                                        .request(MediaType.APPLICATION_JSON)
                                        .get(String.class);
                                OrientDBResultWrapper odbrw = OM.readValue(linkJson, OrientDBResultWrapper.class);
                                for (Map<String, Object> props : odbrw.getResult()) {
                                    if (idSecond.equalsIgnoreCase((String) props.get("in"))) {
                                        LOGGER.debug(String.format("%s and %s are already linked", map.get("@rid"), idSecond));
                                        return true;
                                    }
                                }
                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
        }
        return false;
    }

    /**
     * Persits an object in the database
     *
     * @param object the object to persist
     * @return the internal Id of the object
     */
    public Object getId(Object object) {
        if (sessionObject.containsKey(object)) return sessionObject.get(object);
        Object id = null;
        Object[] params = builder.fillCreate(object).toArray();
        switch (databaseType) {
            case NEO4J:

                try (Session s = this.driver.session()) {
                    String stmt = builder.makeMatch(object, false);
                    StatementResult result = s.run(stmt, parameters(params));
                    while (result.hasNext()) {
                        Record record = result.next();
                        id = record.get("InternalId").asInt();
                    }
                } catch (Exception e) {
                    LOGGER.error(String.format("Params [%s]", StringUtils.join(params, ',')));
                    LOGGER.error(e.getMessage(), e);
                    throw e;
                }
                break;
            case ORIENT_DB:
                try {
                    String json = this.rootTarget
                            .path(String.format("command/%s/sql/%s", archipelagoConfig.getDatabase().getName(),
                                    URLEncoder.encode(builder.makeMatch(object), "UTF-8")))
                            .request(MediaType.APPLICATION_JSON)
                            .get(String.class);
                    OrientDBResultWrapper resultWrapper = OM.readValue(json, OrientDBResultWrapper.class);
                    for (Map<String, Object> map : resultWrapper.getResult()) {
                        id = map.get("@rid");
                    }
                } catch (Exception e) {
                    LOGGER.error(String.format("Params [%s]", StringUtils.join(params, ',')));
                    LOGGER.error(e.getMessage(), e);
                }
                break;
            default:
        }
        if (null != id) sessionObject.put(object, id);
        return id;
    }

    @Override
    public void close() throws Exception {
        driver.close();
    }

    public List<Object> execute(ArchipelagoQuery aq) throws CheckException, IOException {
        List<Object> nodes = new LinkedList<>();
        LOGGER.debug(String.format("Query : %s", aq.getQuery()));
        final Map<Long, Object> startNodes = new HashMap<>();
        switch (databaseType) {
            case NEO4J:
                try (Session s = this.driver.session()) {
                    if (aq.isRelation()) {
                        link(aq.getFrom(), aq.getTo(), aq.getDescriptor(), aq.isBiDirectionnal());
                    } else {
                        StatementResult result = s.run(aq.getQuery());
                        while (result.hasNext()) {
                            Record record = result.next();
                            Path p = record.get("p").asPath();
                            p.forEach(segment -> {
                                try {
                                    Object startNode = null;
                                    Node start = segment.start();
                                    if (!startNodes.containsKey(start.id())) {
                                        startNode = aq.getTarget().getConstructor().newInstance();
                                        ArchipelagoUtils.feedObject(startNode, start.asMap());
                                        startNodes.put(start.id(), startNode);

                                    } else {
                                        startNode = startNodes.get(start.id());
                                    }
                                    Relationship rel = segment.relationship();
                                    Field field = ArchipelagoUtils.getFieldFromBridgeName(startNode.getClass(), rel.type());
                                    //Get the label as the ClassName
                                    Set<Class<?>> supers = this.reflections.getSubTypesOf(ArchipelagoUtils.getClassOf(field));
                                    Class<?> endClass = null;
                                    if (supers.size() > 0) {
                                        endClass = ArchipelagoUtils
                                                .getClassOf(supers, segment.end().labels().iterator().next());
                                    } else {
                                        endClass = ArchipelagoUtils.getClassOf(field);
                                    }
                                    Object endNode = endClass
                                            .getConstructor()
                                            .newInstance();
                                    ArchipelagoUtils.feedObject(endNode, segment.end().asMap());
                                    if (Collection.class.isAssignableFrom(field.getType())) {
                                        ((Collection) ArchipelagoUtils.get(aq.getTarget(), field, startNode)).add(endNode);
                                    } else {
                                        String methodName = String.format("set%s%s", ("" + field.getName().charAt(0)).toUpperCase(), field.getName().substring(1, field.getName().length()));
                                        //Must have exactly one match
                                        Method method = ReflectionUtils.getAllMethods(aq.getTarget(),
                                                withModifier(Modifier.PUBLIC), withPrefix(String.format(methodName)))
                                                .stream()
                                                .findFirst()
                                                .get();
                                        method.invoke(startNode, endNode);
                                    }
                                } catch (Exception e) {
                                    LOGGER.error(e.getMessage(), e);
                                }
                            });
                        }
                    }
                } catch (Exception e) {
                    LOGGER.error(e.getMessage(), e);
                    throw new CheckException(e.getMessage());
                }
                nodes.addAll(startNodes.values());
                break;
            case ORIENT_DB:
                try {
                    String json;
                    if (aq.isRelation()) {
                        json = this.rootTarget
                                .path(String.format("command/%s/sql/%s", archipelagoConfig.getDatabase().getName(),
                                        URLEncoder.encode(aq.getQuery(), "UTF-8")))
                                .request(MediaType.TEXT_PLAIN)
                                .post(Entity.entity("", MediaType.TEXT_PLAIN), String.class);
                    } else {
                        json = this.rootTarget
                                .path(String.format("command/%s/sql/%s", archipelagoConfig.getDatabase().getName(),
                                        URLEncoder.encode(aq.getQuery(), "UTF-8")))
                                .request(MediaType.APPLICATION_JSON)
                                .get(String.class);
                    }

                    OrientDBResultWrapper resultWrapper = OM.readValue(json, OrientDBResultWrapper.class);
                    //TODO Too complicatted O(n^5) omg
                    for (Map<String, Object> map : resultWrapper.getResult()) {
                        Object node = aq.getTarget().getConstructor().newInstance();
                        ArchipelagoUtils.feedObject(node, map);
                        nodes.add(node);
                        for (Map.Entry<String, Object> prop : map.entrySet()) {
                            if (StringUtils.startsWith(prop.getKey(), "out_")) {
                                String name = prop.getKey().substring(4);
                                Field field = ArchipelagoUtils.getFieldFromBridgeName(aq.getTarget(), name);
                                //Get the label as the ClassName
                                Set<Class<?>> supers = this.reflections.getSubTypesOf(ArchipelagoUtils.getClassOf(field));
                                for (String relations : (List<String>) prop.getValue()) {
                                    String relJson = this.rootTarget
                                            .path(String.format("command/%s/sql/%s", archipelagoConfig.getDatabase().getName(),
                                                    URLEncoder.encode(String.format("SELECT FROM %s", relations), "UTF-8")))
                                            .request(MediaType.APPLICATION_JSON)
                                            .get(String.class);
                                    OrientDBResultWrapper odbrw = OM.readValue(relJson, OrientDBResultWrapper.class);
                                    for (Map<String, Object> props : odbrw.getResult()) {
                                        String linked = (String) props.get("in");
                                        String linkedJson = this.rootTarget
                                                .path(String.format("command/%s/sql/%s", archipelagoConfig.getDatabase().getName(),
                                                        URLEncoder.encode(String.format("SELECT FROM %s", linked), "UTF-8")))
                                                .request(MediaType.APPLICATION_JSON)
                                                .get(String.class);
                                        OrientDBResultWrapper link = OM.readValue(linkedJson, OrientDBResultWrapper.class);
                                        for (Map<String, Object> linkedProps : link.getResult()) {
                                            Class<?> endClass = null;
                                            if (supers.size() > 0) {
                                                endClass = ArchipelagoUtils
                                                        .getClassOf(supers, (String) linkedProps.get("@class"));
                                            } else {

                                                // TODO reTest
                                                endClass = ArchipelagoUtils.getClassOf(field);
                                            }
                                            Object endNode = endClass
                                                    .getConstructor()
                                                    .newInstance();
                                            ArchipelagoUtils.feedObject(endNode, linkedProps);
                                            if (Collection.class.isAssignableFrom(field.getType())) {
                                                ((Collection) ArchipelagoUtils.get(aq.getTarget(), field, node)).add(endNode);
                                            } else {
                                                String methodName = String.format("set%s%s", ("" + field.getName().charAt(0)).toUpperCase(), field.getName().substring(1, field.getName().length()));
                                                //Must have exactly one match
                                                Method method = ReflectionUtils.getAllMethods(aq.getTarget(),
                                                        withModifier(Modifier.PUBLIC), withPrefix(String.format(methodName)))
                                                        .stream()
                                                        .findFirst()
                                                        .get();
                                                method.invoke(node, endNode);
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                } catch (Exception e) {
                    LOGGER.error(e.getMessage(), e);
                    throw new CheckException(e.getMessage());
                }
                break;
        }
        return nodes;
    }
}
