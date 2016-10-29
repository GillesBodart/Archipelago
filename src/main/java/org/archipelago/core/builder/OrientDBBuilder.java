package org.archipelago.core.builder;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

import org.archipelago.core.domain.GeneratedScript;
import org.archipelago.core.util.StringTemplateFactory;
import org.stringtemplate.v4.ST;
import org.stringtemplate.v4.STGroup;

import com.orientechnologies.orient.core.metadata.schema.OType;

public class OrientDBBuilder extends ArchipelagoScriptBuilder {

    private static final String ORIENT_FOLDER = "\\OrientDB";
    private static final String CLASS_FILE_NAME_CLASS_FORMAT = "OrientClass%s";
    private static final String CLASS_FILE_NAME_RELATION_FORMAT = "OrientRelations%s";

    @Override
    public List<GeneratedScript> makeScript(final Class<?> clazz) {
        return makeScript(clazz, null);
    }

    @Override
    public List<GeneratedScript> makeScript(Class<?> clazz, List<Class<?>> islands) {
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
            st.add("properties", new PropertyWrapper(clazz.getSimpleName(), field.getName(), null == type?field.getType().getSimpleName():type));
        } 
        LOGGER.debug(String.format("Generation of script for class %s [END]", clazz.getSimpleName()));
        return new GeneratedScript(String.format(CLASS_FILE_NAME_CLASS_FORMAT, clazz.getSimpleName()), st.render());
    }

    private GeneratedScript makeRelations(final Class<?> clazz) {
        LOGGER.debug(String.format("Generation of script for relation %s [START]", clazz.getSimpleName()));
        final String relationTemplatePath = TEMPLATE_ROOT_PATH + ORIENT_FOLDER + "/relations.stg";
        final STGroup group = StringTemplateFactory.buildSTGroup(relationTemplatePath);
        final ST st = group.getInstanceOf("RelationsOrientDB");
        LOGGER.debug(String.format("Generation of script for relation %s [START]", clazz.getSimpleName()));
        return new GeneratedScript(String.format(CLASS_FILE_NAME_RELATION_FORMAT, clazz.getSimpleName()), st.render());
    }

    private class PropertyWrapper {
        private String parentClass;
        private String name;
        private Object type;

        public PropertyWrapper(String parentClass, String name, Object type) {
            super();
            this.parentClass = parentClass;
            this.name = name;
            this.type = type;
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

        /*
         * [<link-type>|<link-class>] 
( <property constraint> [, <property-constraint>]* ) 
[UNSAFE] 
         */


    }
}
