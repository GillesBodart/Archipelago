package org.archipelago.core.builder;

import com.google.common.collect.Lists;
import org.archipelago.core.domain.GeneratedScript;
import org.archipelago.core.util.ArchipelagoUtils;
import org.archipelago.core.util.StringTemplateFactory;
import org.stringtemplate.v4.ST;
import org.stringtemplate.v4.STGroup;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
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

    public String makeCreate(Class<?> clazz) {
        final String nodeTemplatePath = TEMPLATE_ROOT_PATH + NEO4J_FOLDER + "\\node.stg";
        final STGroup group = StringTemplateFactory.buildSTGroup(nodeTemplatePath);
        final ST st = group.getInstanceOf("ClassNeo4J");
        st.add("clazz", clazz);
        for (Field field : ArchipelagoUtils.getAllFields(clazz)) {
            st.add("props", field.getName());
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
        for (Field field : fields) {
            try {
                Method getter = null;
                if (field.getType().equals(boolean.class)) {
                    getter = clazz.getMethod(String.format("is%s%s", ("" + field.getName().charAt(0)).toUpperCase(), field.getName().substring(1, field
                            .getName().length())));
                } else {
                    getter = clazz.getMethod(String.format("get%s%s", ("" + field.getName().charAt(0)).toUpperCase(), field.getName().substring(1, field
                            .getName().length())));
                }
                Object prop = getter.invoke(object);
                if (null != prop) {
                    parameters.add(field.getName());
                    switch (field.getType().getSimpleName().toLowerCase()) {
                        case "date":
                            parameters.add(sdf.format((Date) getter.invoke(object)));
                            break;
                        case "localdate":
                            parameters.add(((LocalDate) getter.invoke(object)).format(DateTimeFormatter.ISO_LOCAL_DATE));
                            break;
                        case "localtime":
                            parameters.add(((LocalTime) getter.invoke(object)).format(DateTimeFormatter.ISO_LOCAL_DATE));
                            break;
                        case "localdatetime":
                            parameters.add(((LocalDateTime) getter.invoke(object)).format(DateTimeFormatter.ISO_LOCAL_DATE));
                            break;
                        default: {
                            parameters.add(getter.invoke(object));
                        }
                    }
                }
            } catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
                LOGGER.debug(String.format("No usual getter for %s found", field.getName()), e);
            }
        }
        return parameters;
    }

    public String makeMatch(Class<?> clazz) {
        return makeMatch(clazz, true);
    }

    public String makeMatch(Class<?> clazz, boolean allObject) {
        final String nodeTemplatePath = String.format("%s/%s/%s%s", TEMPLATE_ROOT_PATH, NEO4J_FOLDER, "\\match", allObject ? ".stg" : "Id.stg");
        final STGroup group = StringTemplateFactory.buildSTGroup(nodeTemplatePath);
        final ST st = group.getInstanceOf("MatchNeo4J");
        st.add("clazz", clazz);
        for (Field field : ArchipelagoUtils.getAllFields(clazz)) {
            st.add("props", field.getName());
        }
        String match = st.render();
        LOGGER.debug(String.format("MATCH for class %s : [%s]", clazz.getSimpleName(), match));
        return match;
    }

    public String makeRelation(int idA, int idB, String name, Class<?> descriptor) {
        boolean haveProp = null != descriptor;

        final String nodeTemplatePath = String.format("%s/%s/%s%s", TEMPLATE_ROOT_PATH, NEO4J_FOLDER, "\\relation", haveProp ? "WithProp.stg" : ".stg");
        final STGroup group = StringTemplateFactory.buildSTGroup(nodeTemplatePath);
        final ST st = group.getInstanceOf("RelationNeo4J");
        st.add("idA", idA);
        st.add("idB", idB);
        st.add("name", name);
        if (haveProp) {
            for (Field field : ArchipelagoUtils.getAllFields(descriptor)) {
                st.add("properties", field.getName());
            }
        }
        String relationQuery = st.render();
        LOGGER.debug(String.format("CREATE Relation from %d to %d : [%s]", idA, idB, relationQuery));
        return relationQuery;
    }

}
