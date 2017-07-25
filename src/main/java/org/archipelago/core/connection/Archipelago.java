package org.archipelago.core.connection;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.archipelago.core.builder.ArchipelagoQuery;
import org.archipelago.core.builder.Neo4JQueryImpl;
import org.archipelago.core.builder.OrientDBQueryImpl;
import org.archipelago.core.builder.QueryBuilder;
import org.archipelago.core.builder.old.ArchipelagoScriptBuilder;
import org.archipelago.core.builder.old.Neo4JBuilder;
import org.archipelago.core.builder.old.OrientDBBuilder;
import org.archipelago.core.configurator.ArchipelagoConfig;
import org.archipelago.core.configurator.DatabaseConfig;
import org.archipelago.core.configurator.DatabaseType;
import org.archipelago.core.domain.DescriptorWrapper;
import org.archipelago.core.domain.OrientDBResultWrapper;
import org.archipelago.core.domain.RelationWrapper;
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
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
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
                jerseyClient.register(feature);
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

    public Integer persist(Object object) throws CheckException {
        return persist(object, 0);
    }

    /**
     * Persist an object in the database
     *
     * @param object   the object to persist
     * @param deepness current deepness of the introspection
     * @return the internal Id of the object (null in case or error)
     */
    private Integer persist(Object object, Integer deepness) throws CheckException {
        //TODO Transaction
        Integer id = null;
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
                    id = (Integer) getId(object);
                } catch (Exception e) {
                    LOGGER.debug(String.format("Param [%s]", StringUtils.join(params, ';')));
                    throw e;
                }
                break;
            case ORIENT_DB:
                Integer nextId = this.rootTarget.path(String.format("function/%s/sequence(\"%sArchipelagoSeq\"\")", getConfig().getDatabase().getName(), object.getClass().getSimpleName()))
                        .request(MediaType.APPLICATION_JSON)
                        .get(Integer.class);

                Response response = this.rootTarget.path(String.format("document/%s", getConfig().getDatabase().getName()))
                        .request(MediaType.APPLICATION_JSON)
                        .post(Entity.entity(object, MediaType.APPLICATION_JSON));
                break;
        }

        return id;
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

    public void link(Object first, Object second, Object descriptor, boolean biDirectionnal) {
        switch (databaseType) {
            case NEO4J:
                boolean haveDescriptor = null != descriptor;
                try (Session s = this.driver.session()) {
                    Integer idA = (Integer) getId(first);
                    Integer idB = (Integer) getId(second);
                    if (idA != null && idB != null) {
                        String name;
                        if (haveDescriptor) {
                            name = descriptor.getClass().getSimpleName();
                        } else {
                            name = String.format("%sTo%s", first.getClass().getSimpleName(), second.getClass().getSimpleName());
                        }
                        if (haveDescriptor) {
                            s.run(builder.makeRelation(idA, idB, name, descriptor.getClass()), parameters(builder.fillCreate(descriptor).toArray()));
                            if (biDirectionnal) {
                                s.run(builder.makeRelation(idB, idA, name, descriptor.getClass()), parameters(builder.fillCreate(descriptor).toArray()));
                            }
                        } else {
                            s.run(builder.makeRelation(idA, idB, name));
                            if (biDirectionnal) {
                                s.run(builder.makeRelation(idB, idA, name));
                            }
                        }
                    }
                }
                break;
            case ORIENT_DB:
                break;
        }

    }

    public void link(Object first, Object second, String descriptorName, boolean biDirectional) {
        switch (databaseType) {
            case NEO4J:
                DescriptorWrapper dw = new DescriptorWrapper(descriptorName);
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
                break;
        }
    }

    /**
     * Persits an object in the database
     *
     * @param object the object to persist
     * @return the internal Id of the object
     */
    public Object getId(Object object) {
        if (sessionObject.containsKey(object)) return sessionObject.get(object);
        switch (databaseType) {
            case NEO4J:
                Integer id = null;
                Object[] params = builder.fillCreate(object).toArray();
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
                if (null != id) sessionObject.put(object, id);
                return id;
            case ORIENT_DB:
                break;
            default:
        }
        return null;
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
                                    Class<?> endClass = ArchipelagoUtils
                                            .getClassOf(supers, segment.end().labels().iterator().next());
                                    Object endNode = endClass
                                            .getConstructor()
                                            .newInstance();
                                    ArchipelagoUtils.feedObject(endNode, segment.end().asMap());
                                    LOGGER.info(endNode);
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
                    String json = this.rootTarget.path(String.format("command/%s/sql/%s", getConfig().getDatabase().getName(), aq.getQuery()))
                            .request(MediaType.APPLICATION_JSON)
                            .get(String.class);
                    ObjectMapper om = new ObjectMapper();
                    OrientDBResultWrapper resultWrapper = om.readValue(json, OrientDBResultWrapper.class);
                    for (Map<String, Object> map : resultWrapper.getResult()) {
                        Object node = aq.getTarget().getConstructor().newInstance();
                        ArchipelagoUtils.feedObject(node, map);
                        nodes.add(node);
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
