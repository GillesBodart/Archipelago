package org.archipelago.core.builder;

import org.archipelago.core.annotations.Bridge;
import org.archipelago.core.util.ArchipelagoUtils;
import org.archipelago.core.util.StringTemplateFactory;
import org.stringtemplate.v4.ST;
import org.stringtemplate.v4.STGroup;

import java.lang.reflect.Field;
import java.util.List;

/**
 * @author Gilles Bodart
 */
public class Neo4JBuilder extends ArchipelagoScriptBuilder {

    private static final String NEO4J_FOLDER = "\\Neo4J";

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
                if (!field.isAnnotationPresent(Bridge.class)) {
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
                if (!field.isAnnotationPresent(Bridge.class)) {
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

    @Override
    public String makeRelation(Object idA, Object idB, String name, Object descriptor) {
        final String nodeTemplatePath = String.format("%s/%s/%s", TEMPLATE_ROOT_PATH, NEO4J_FOLDER, "\\relationWithProp.stg");
        final STGroup group = StringTemplateFactory.buildSTGroup(nodeTemplatePath);
        final ST st = group.getInstanceOf("RelationNeo4J");
        st.add("idA", idA);
        st.add("idB", idB);
        st.add("name", name);
        for (Field field : ArchipelagoUtils.getAllFields(descriptor.getClass())) {
            st.add("properties", field.getName());
        }
        String relationQuery = st.render();
        LOGGER.debug(String.format("CREATE Relation from %d to %d : [%s]", idA, idB, relationQuery));
        return relationQuery;
    }

    @Override
    public String makeRelation(Object idA, Object idB, String name, List<String> props) {
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
