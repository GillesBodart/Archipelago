package org.archipelago.core.builder;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import org.archipelago.core.annotations.Garland;
import org.archipelago.core.annotations.Island;
import org.archipelago.core.domain.GeneratedScript;
import org.archipelago.core.util.ArchipelagoUtils;
import org.archipelago.core.util.StringTemplateFactory;
import org.stringtemplate.v4.ST;
import org.stringtemplate.v4.STGroup;

public class RelationalSQLBuilder extends ArchipelagoScriptBuilder {

    private static final String RELATIONAL_FOLDER = "\\RelationalSQL";
    private static final String JAVA_FOLDER = "\\java";
    private static final String SQL_FOLDER = "\\sql";
    private static final String CLASS_FILE_NAME_META_FORMAT = "Meta%s.java";
    private static final String CLASS_FILE_NAME_UNDER_META_FORMAT = "%s.java";
    private static final String CLASS_FILE_NAME_GARLAND_FORMAT = "%s.java";

    @Override
    public List<GeneratedScript> makeScript(final Class<?> clazz) {
        return makeScript(clazz, null);
    }

    @Override
    public List<GeneratedScript> makeScript(Class<?> clazz, List<Class<?>> islands) {
        List<GeneratedScript> scripts = new ArrayList<>();
        scripts.addAll(refactorClasses(clazz, islands));
        scripts.addAll(generateTables(clazz, islands));
        return scripts;

    }

    private Collection<? extends GeneratedScript> generateTables(Class<?> clazz, List<Class<?>> islands) {
        LOGGER.debug(String.format("generation of the sql table for class %s [START]", clazz.getSimpleName()));
        List<GeneratedScript> scripts = new LinkedList<>();

        // Metaisation of the model
        if (ArchipelagoUtils.doesContainsAnnotation(clazz.getAnnotations(), Island.class)) {
            generateMetaTable(clazz, scripts);
            generateUnderMetaTable(clazz, scripts);
        } else if (ArchipelagoUtils.doesContainsAnnotation(clazz.getAnnotations(), Garland.class)) {
            generateGarlandTable(clazz, islands, scripts);
        }
        LOGGER.debug(String.format("generation of the sql table for class %s [END]", clazz.getSimpleName()));
        return scripts;
    }

    private void generateGarlandTable(Class<?> clazz, List<Class<?>> islands, List<GeneratedScript> scripts) {
        // TODO Auto-generated method stub

    }

    private void generateUnderMetaTable(Class<?> clazz, List<GeneratedScript> scripts) {
        // TODO Auto-generated method stub

    }

    private void generateMetaTable(Class<?> clazz, List<GeneratedScript> scripts) {
        String classTemplatePath;
        STGroup group;
        ST st;
        LOGGER.debug(String.format("generate UnderMetaclass %s [START]", clazz.getSimpleName()));
        classTemplatePath = TEMPLATE_ROOT_PATH + RELATIONAL_FOLDER + SQL_FOLDER + "\\Table.stg";
        group = StringTemplateFactory.buildSTGroup(classTemplatePath);
        st = group.getInstanceOf("GarlandClass");
        st.add("className", clazz.getSimpleName());
        st.add("package", clazz.getPackage());
        for (Field field : clazz.getDeclaredFields()) {
            st.add("properties", field);
        }
        scripts.add(new GeneratedScript(String.format(CLASS_FILE_NAME_UNDER_META_FORMAT, clazz.getSimpleName()), st.render()));
        LOGGER.debug(String.format("generate UnderMetaclass %s [END]", clazz.getSimpleName()));

    }

    private List<GeneratedScript> refactorClasses(Class<?> clazz, List<Class<?>> islands) {
        LOGGER.debug(String.format("refactoring of the data model for class %s [START]", clazz.getSimpleName()));
        List<GeneratedScript> scripts = new LinkedList<>();

        // Metaisation of the model
        if (ArchipelagoUtils.doesContainsAnnotation(clazz.getAnnotations(), Island.class)) {
            generateMetaClass(clazz, scripts);
            generateUnderMetaClass(clazz, scripts);
        } else if (ArchipelagoUtils.doesContainsAnnotation(clazz.getAnnotations(), Garland.class)) {
            generateGarlandClass(clazz, islands, scripts);
        }
        LOGGER.debug(String.format("refactoring of the data model for class %s [END]", clazz.getSimpleName()));
        return scripts;
    }

    private void generateGarlandClass(Class<?> clazz, List<Class<?>> islands, List<GeneratedScript> scripts) {
        String classTemplatePath;
        STGroup group;
        ST st;
        LOGGER.debug(String.format("generate UnderMetaclass %s [START]", clazz.getSimpleName()));
        classTemplatePath = TEMPLATE_ROOT_PATH + JAVA_FOLDER + "\\GarlandClass.stg";
        group = StringTemplateFactory.buildSTGroup(classTemplatePath);
        st = group.getInstanceOf("GarlandClass");
        st.add("className", clazz.getSimpleName());
        st.add("package", clazz.getPackage());
        for (Field field : clazz.getDeclaredFields()) {
            st.add("properties", field);
        }
        scripts.add(new GeneratedScript(String.format(CLASS_FILE_NAME_UNDER_META_FORMAT, clazz.getSimpleName()), st.render()));
        LOGGER.debug(String.format("generate UnderMetaclass %s [END]", clazz.getSimpleName()));
    }

    private void generateUnderMetaClass(Class<?> clazz, List<GeneratedScript> scripts) {
        String classTemplatePath;
        STGroup group;
        ST st;
        LOGGER.debug(String.format("generate UnderMetaclass %s [START]", clazz.getSimpleName()));
        classTemplatePath = TEMPLATE_ROOT_PATH + JAVA_FOLDER + "\\UnderMetaClass.stg";
        group = StringTemplateFactory.buildSTGroup(classTemplatePath);
        st = group.getInstanceOf("UnderMetaClass");
        st.add("className", clazz.getSimpleName());
        st.add("package", clazz.getPackage());
        for (Field field : clazz.getDeclaredFields()) {
            st.add("properties", field);
        } 
        scripts.add(new GeneratedScript(String.format(CLASS_FILE_NAME_UNDER_META_FORMAT, clazz.getSimpleName()), st.render()));
        LOGGER.debug(String.format("generate UnderMetaclass %s [END]", clazz.getSimpleName()));
    }

    private void generateMetaClass(Class<?> clazz, List<GeneratedScript> scripts) {
        LOGGER.debug(String.format("generate Metaclass %s [START]", clazz.getSimpleName()));
        String classTemplatePath = TEMPLATE_ROOT_PATH + JAVA_FOLDER + "\\MetaClass.stg";
        STGroup group = StringTemplateFactory.buildSTGroup(classTemplatePath);
        ST st = group.getInstanceOf("MetaClass");
        st.add("className", clazz.getSimpleName());
        st.add("package", clazz.getPackage());
        scripts.add(new GeneratedScript(String.format(CLASS_FILE_NAME_META_FORMAT, clazz.getSimpleName()), st.render()));
        LOGGER.debug(String.format("generate MetaClass %s [END]", clazz.getSimpleName()));
    }

}
