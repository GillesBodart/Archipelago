package org.archipelago.core.builder.old;

import com.google.common.collect.Lists;
import org.archipelago.core.annotations.ArchipelId;
import org.archipelago.core.annotations.Bridge;
import org.archipelago.core.domain.GeneratedScript;
import org.archipelago.core.util.ArchipelagoUtils;
import org.archipelago.core.util.StringTemplateFactory;
import org.stringtemplate.v4.ST;
import org.stringtemplate.v4.STGroup;

import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;
import java.util.Set;

/**
 * @author Gilles Bodart
 */
public class Neo4JBuilder extends ArchipelagoScriptBuilder {

    private static final String NEO4J_FOLDER = "\\Neo4J";

    @Override
    public List<GeneratedScript> makeScript(final Class<?> clazz) {
        return makeScript(clazz, null);
    }

    @Override
    public List<GeneratedScript> makeScript(Class<?> clazz, List<Class<?>> archipels) {
        return null;
    }

    public String makeCreate(Object object) {
        Class<?> clazz = object.getClass();
        final String nodeTemplatePath = TEMPLATE_ROOT_PATH + NEO4J_FOLDER + "\\node.stg";
        final STGroup group = StringTemplateFactory.buildSTGroup(nodeTemplatePath);
        final ST st = group.getInstanceOf("ClassNeo4J");
        st.add("clazz", clazz);
        if (String.class.equals(clazz)) {
            st.add("props", "name");
        } else {
            for (Field field : ArchipelagoUtils.getAllFields(clazz)) {
                if (!field.isAnnotationPresent(Bridge.class) && !field.isAnnotationPresent(ArchipelId.class)) {
                    if (null != ArchipelagoUtils.get(clazz, field, object)) {
                        st.add("props", field.getName());
                    }
                }
            }
        }
        String create = st.render();
        LOGGER.debug(String.format("CREATE for class %s : [%s]", clazz.getSimpleName(), create));
        return create;
    }

    public List<Object> fillCreate(Object object) {
        final List<Object> parameters = Lists.newArrayList();
        Class<?> clazz = object.getClass();
        Set<Field> fields = ArchipelagoUtils.getAllFields(clazz);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-mm-dd");
        if (String.class.equals(clazz)) {
            parameters.add("name");
            parameters.add(object);
        } else {
            for (Field field : fields) {
                if (!field.isAnnotationPresent(Bridge.class) && !field.isAnnotationPresent(ArchipelId.class)) {
                    Object prop = ArchipelagoUtils.get(clazz, field, object);
                    if (null != prop) {
                        parameters.add(field.getName());
                        switch (field.getType().getSimpleName().toLowerCase()) {
                            case "date":
                                parameters.add(sdf.format((Date) prop));
                                break;
                            case "localdate":
                                parameters.add(((LocalDate) prop).format(DateTimeFormatter.ISO_LOCAL_DATE));
                                break;
                            case "localtime":
                                parameters.add(((LocalTime) prop).format(DateTimeFormatter.ISO_LOCAL_DATE));
                                break;
                            case "localdatetime":
                                parameters.add(((LocalDateTime) prop).format(DateTimeFormatter.ISO_LOCAL_DATE));
                                break;
                            default: {
                                parameters.add(prop);
                            }
                        }
                    }

                }
            }
        }

        return parameters;
    }

    public String makeMatch(Object object) {
        return makeMatch(object, true);
    }

    public String makeMatch(Object object, boolean allObject) {
        Class<?> clazz = object.getClass();
        final String nodeTemplatePath = String.format("%s/%s/%s%s", TEMPLATE_ROOT_PATH, NEO4J_FOLDER, "\\match", allObject ? ".stg" : "Id.stg");
        final STGroup group = StringTemplateFactory.buildSTGroup(nodeTemplatePath);
        final ST st = group.getInstanceOf("MatchNeo4J");
        st.add("clazz", clazz);
        if (String.class.equals(clazz)) {
            st.add("props", "name");
        } else {
            for (Field field : ArchipelagoUtils.getAllFields(clazz)) {
                if (!field.isAnnotationPresent(Bridge.class) && !field.isAnnotationPresent(ArchipelId.class)) {
                    if (null != ArchipelagoUtils.get(clazz, field, object)) {
                        st.add("props", field.getName());
                    }
                }
            }
        }
        String match = st.render();
        LOGGER.debug(String.format("MATCH for class %s : [%s]", clazz.getSimpleName(), match));
        return match;
    }

    public String makeRelation(int idA, int idB, String name) {
        final String nodeTemplatePath = String.format("%s/%s/%s", TEMPLATE_ROOT_PATH, NEO4J_FOLDER, "\\relation.stg");
        final STGroup group = StringTemplateFactory.buildSTGroup(nodeTemplatePath);
        final ST st = group.getInstanceOf("RelationNeo4J");
        st.add("idA", idA);
        st.add("idB", idB);
        st.add("name", name);
        String relationQuery = st.render();
        LOGGER.debug(String.format("CREATE Relation from %d to %d : [%s]", idA, idB, relationQuery));
        return relationQuery;
    }

    public String makeRelation(int idA, int idB, String name, Class<?> descriptor) {

        final String nodeTemplatePath = String.format("%s/%s/%s", TEMPLATE_ROOT_PATH, NEO4J_FOLDER, "\\relationWithProp.stg");
        final STGroup group = StringTemplateFactory.buildSTGroup(nodeTemplatePath);
        final ST st = group.getInstanceOf("RelationNeo4J");
        st.add("idA", idA);
        st.add("idB", idB);
        st.add("name", name);
        for (Field field : ArchipelagoUtils.getAllFields(descriptor)) {
            st.add("properties", field.getName());
        }
        String relationQuery = st.render();
        LOGGER.debug(String.format("CREATE Relation from %d to %d : [%s]", idA, idB, relationQuery));
        return relationQuery;
    }

    public String makeRelation(int idA, int idB, String name, List<String> props) {
        final String nodeTemplatePath = String.format("%s/%s/%s", TEMPLATE_ROOT_PATH, NEO4J_FOLDER, "\\relationWithProp.stg");
        final STGroup group = StringTemplateFactory.buildSTGroup(nodeTemplatePath);
        final ST st = group.getInstanceOf("RelationNeo4J");
        st.add("idA", idA);
        st.add("idB", idB);
        st.add("name", name);
        for (String prop : props) {
            st.add("properties", prop);
        }
        String relationQuery = st.render();
        LOGGER.debug(String.format("CREATE Relation from %d to %d : [%s]", idA, idB, relationQuery));
        return relationQuery;
    }

}
