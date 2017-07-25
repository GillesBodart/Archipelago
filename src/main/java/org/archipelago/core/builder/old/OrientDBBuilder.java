package org.archipelago.core.builder.old;

import com.orientechnologies.orient.core.metadata.schema.OType;
import org.archipelago.core.annotations.ArchipelId;
import org.archipelago.core.annotations.Bridge;
import org.archipelago.core.domain.GeneratedScript;
import org.archipelago.core.util.ArchipelagoUtils;
import org.archipelago.core.util.StringTemplateFactory;
import org.stringtemplate.v4.ST;
import org.stringtemplate.v4.STGroup;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.WildcardType;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

/**
 * @author Gilles Bodart
 */
public class OrientDBBuilder extends ArchipelagoScriptBuilder {

    private static final String ORIENT_FOLDER = "\\OrientDB";
    private static final String CLASS_FILE_NAME_CLASS_FORMAT = "OrientClass%s.txt";
    private static final String CLASS_FILE_NAME_RELATION_FORMAT = "OrientRelations%s.txt";

    private List<PropertyWrapper> relations = new ArrayList<>();

    @Override
    public List<GeneratedScript> makeScript(final Class<?> clazz) {
        return makeScript(clazz, null);
    }

    @Override
    public List<GeneratedScript> makeScript(Class<?> clazz, List<Class<?>> archipels) {
        List<GeneratedScript> scripts = new ArrayList<>();
        scripts.add(makeClasses(clazz));
        scripts.add(makeRelations(clazz));
        return scripts;

    }

    private GeneratedScript makeClasses(final Class<?> clazz) {
        LOGGER.debug(String.format("Generation of script for class %s [START]", clazz.getSimpleName()));
        final String classTemplatePath = TEMPLATE_ROOT_PATH + ORIENT_FOLDER + "\\class.stg";
        final STGroup group = StringTemplateFactory.buildSTGroup(classTemplatePath);
        final ST st = group.getInstanceOf("ClassOrientDB");
        st.add("clazz", clazz);
        if (!clazz.getSuperclass().equals(Object.class)) {
            st.add("parentClazz", clazz.getSuperclass());
        }
        st.add("abstract", Modifier.isAbstract(clazz.getModifiers()));
        for (Field field : clazz.getDeclaredFields()) {
            OType type = OType.getTypeByClass(field.getType());
            PropertyWrapper prop = null;
            if (null == type || type.isEmbedded()) {
                if (Collection.class.isAssignableFrom(field.getType())) {
                    ParameterizedType genericType = (ParameterizedType) field.getGenericType();
                    Class<?> genericClass = null;
                    if (genericType.getActualTypeArguments()[0] instanceof WildcardType) {
                        genericClass = (Class<?>) ((WildcardType) genericType.getActualTypeArguments()[0]).getUpperBounds()[0];
                    } else {
                        genericClass = (Class<?>) genericType.getActualTypeArguments()[0];
                    }
                    relations.add(new PropertyWrapper(clazz.getSimpleName(), field.getName(),
                            String.format("%s %s", OType.LINKLIST, genericClass.getSimpleName()), genericClass));
                } else {
                    relations.add(
                            new PropertyWrapper(clazz.getSimpleName(), field.getName(), String.format("%s %s", OType.LINK, field.getType().getSimpleName()),
                                    field.getType()));
                }
            } else {
                prop = new PropertyWrapper(clazz.getSimpleName(), field.getName(), type, null);
            }
            st.add("properties", prop);
        }
        LOGGER.debug(String.format("Generation of script for class %s [END]", clazz.getSimpleName()));
        return new GeneratedScript(String.format(CLASS_FILE_NAME_CLASS_FORMAT, clazz.getSimpleName()), st.render());
    }

    private GeneratedScript makeRelations(final Class<?> clazz) {
        LOGGER.debug(String.format("Generation of script for relation %s [START]", clazz.getSimpleName()));
        final String relationTemplatePath = TEMPLATE_ROOT_PATH + ORIENT_FOLDER + "/relations.stg";
        final STGroup group = StringTemplateFactory.buildSTGroup(relationTemplatePath);
        final ST st = group.getInstanceOf("RelationsOrientDB");
        for (PropertyWrapper relation : relations) {
            st.add("property", relation);
        }
        LOGGER.debug(String.format("Generation of script for relation %s [START]", clazz.getSimpleName()));
        return new GeneratedScript(String.format(CLASS_FILE_NAME_RELATION_FORMAT, clazz.getSimpleName()), st.render());
    }

    @Override
    public String makeCreate(Object object) {
        final StringBuilder sb = new StringBuilder();
        sb.append(String.format("{\"@class\":\"%s\"", object.getClass().getSimpleName()));
        Iterator<Object> iterator = fillCreate(object).iterator();
        while (iterator.hasNext()) {
            sb.append(",");
            sb.append(String.format("\"%s\":%s", iterator.next(), iterator.next()));
        }
        sb.append("}");
        LOGGER.debug(String.format("CREATE for class %s : [%s]", object.getClass().getSimpleName(), sb.toString()));
        return sb.toString();
    }

    public String makeMatch(Object object) {
        Class<?> clazz = object.getClass();
        final String nodeTemplatePath = String.format("%s/%s/%s", TEMPLATE_ROOT_PATH, ORIENT_FOLDER, "\\match.stg");
        final STGroup group = StringTemplateFactory.buildSTGroup(nodeTemplatePath);
        final ST st = group.getInstanceOf("MatchOrientDB");
        st.add("clazz", clazz);
        if (String.class.equals(clazz)) {
            st.add("props", "name");
        } else {
            for (Field field : ArchipelagoUtils.getAllFields(clazz)) {
                if (!field.isAnnotationPresent(Bridge.class) && !field.isAnnotationPresent(ArchipelId.class)) {
                    Object o = ArchipelagoUtils.get(clazz, field, object);
                    if (null != o) {
                        st.add("props", new ValueWrapper(field.getName(), ArchipelagoUtils.formatQueryValue(o)));
                    }
                }
            }
        }
        String match = st.render();
        LOGGER.debug(String.format("MATCH for class %s : [%s]", clazz.getSimpleName(), match));
        return match;
    }

    private class ValueWrapper {
        private String name;
        private Object value;

        public ValueWrapper(String name, Object value) {
            this.name = name;
            this.value = value;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public Object getValue() {
            return value;
        }

        public void setValue(Object value) {
            this.value = value;
        }
    }

    private class PropertyWrapper {
        private String parentClass;
        private String name;
        private Object type;
        private Object genType;

        public PropertyWrapper(String parentClass, String name, Object type, Object genType) {
            super();
            this.parentClass = parentClass;
            this.name = name;
            this.type = type;
            this.genType = genType;
        }

        public String getParentClass() {
            return parentClass;
        }

        public void setParentClass(String parentClass) {
            this.parentClass = parentClass;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public Object getType() {
            return type;
        }

        public void setType(Object type) {
            this.type = type;
        }

        public Object getGenType() {
            return genType;
        }

        public void setGenType(Object genType) {
            this.genType = genType;
        }

    }

    private class RelationWrapper {
        private String from;
        private String to;
        private String fromProp;
        private Object type;

        public RelationWrapper(String from, String to, String fromProp, Object type) {
            this.from = from;
            this.to = to;
            this.fromProp = fromProp;
            this.type = type;
        }

        public String getFrom() {
            return from;
        }

        public void setFrom(String from) {
            this.from = from;
        }

        public String getTo() {
            return to;
        }

        public void setTo(String to) {
            this.to = to;
        }

        public String getFromProp() {
            return fromProp;
        }

        public void setFromProp(String fromProp) {
            this.fromProp = fromProp;
        }

        public Object getType() {
            return type;
        }

        public void setType(Object type) {
            this.type = type;
        }

    }
}
