package org.archipelago.core.builder.old;

import java.lang.reflect.Field;
import java.sql.JDBCType;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.archipelago.core.annotations.Archipel;
import org.archipelago.core.annotations.Island;
import org.archipelago.core.domain.GeneratedScript;
import org.archipelago.core.util.ArchipelagoUtils;
import org.archipelago.core.util.StringTemplateFactory;
import org.hibernate.type.descriptor.sql.JdbcTypeJavaClassMappings;
import org.stringtemplate.v4.ST;
import org.stringtemplate.v4.STGroup;

/**
 * 
 * @author Gilles Bodart
 *
 */
public class RelationalSQLBuilder extends ArchipelagoScriptBuilder {

    private static final String RELATIONAL_FOLDER = "\\RelationalSQL";
    private static final String JAVA_FOLDER = "\\java";
    private static final String SQL_FOLDER = "\\sql";
    private static final String CLASS_FILE_NAME_META_FORMAT = "Meta%s.java";
    private static final String CLASS_FILE_NAME_UNDER_META_FORMAT = "%s.java";
    private static final String CLASS_FILE_NAME_GARLAND_FORMAT = "%s.java";

    private static final String SQL_FILE_NAME_META_FORMAT = "Meta%s.sql";
    private static final String SQL_FILE_NAME_UNDER_META_FORMAT = "%s.sql";
    private static final String SQL_FILE_NAME_GARLAND_FORMAT = "%s.sql";

    private Map<Class<?>, List<Field>> newMetaClassesMap = new HashMap<>();
    private Map<Class<?>, List<Field>> newUnderMetaClassesMap = new HashMap<>();
    private Map<Class<?>, List<Field>> newIslandClassesMap = new HashMap<>();

    @Override
    public List<GeneratedScript> makeScript(final Class<?> clazz) {
        return makeScript(clazz, null);
    }

    @Override
    public List<GeneratedScript> makeScript(Class<?> clazz, List<Class<?>> archipels) {
        List<GeneratedScript> scripts = new ArrayList<>();
        generateNewClasses(clazz, archipels);
        scripts.addAll(refactorClasses(clazz, archipels));
        // TODO Generate Table from new class not old one ...
        scripts.addAll(generateTables(clazz, archipels));
        return scripts;

    }

    private void generateNewClasses(Class<?> clazz, List<Class<?>> archipels) {

        if (ArchipelagoUtils.doesContainsAnnotation(clazz.getAnnotations(), Archipel.class)) {
            generateNewMetaClasses(clazz);
            generateNewUnderMetaClasses(clazz);
        } else if (ArchipelagoUtils.doesContainsAnnotation(clazz.getAnnotations(), Island.class)) {
            generateNewIslandClasses(clazz, archipels);
        }

    }

    private void generateNewIslandClasses(Class<?> clazz, List<Class<?>> archipels) {
        // TODO Auto-generated method stub

    }

    private void generateNewUnderMetaClasses(Class<?> clazz) {
        // TODO Auto-generated method stub

    }

    private void generateNewMetaClasses(Class<?> clazz) {

    }

    private Collection<? extends GeneratedScript> generateTables(Class<?> clazz, List<Class<?>> archipels) {
        LOGGER.debug(String.format("generation of the sql table for class %s [START]", clazz.getSimpleName()));
        List<GeneratedScript> scripts = new LinkedList<>();

        // Metaisation of the model
        if (ArchipelagoUtils.doesContainsAnnotation(clazz.getAnnotations(), Archipel.class)) {
            generateMetaTable(clazz, scripts);
            generateUnderMetaTable(clazz, scripts);
        } else if (ArchipelagoUtils.doesContainsAnnotation(clazz.getAnnotations(), Island.class)) {
            generateIslandTable(clazz, archipels, scripts);
        }
        LOGGER.debug(String.format("generation of the sql table for class %s [END]", clazz.getSimpleName()));
        return scripts;
    }

    private void generateIslandTable(Class<?> clazz, List<Class<?>> islands, List<GeneratedScript> scripts) {
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
        st = group.getInstanceOf("SQLTable");
        TableGeneratorWrapper table = scanTable(clazz);
        st.add("tableWrapper", table);
        scripts.add(new GeneratedScript(String.format(SQL_FILE_NAME_UNDER_META_FORMAT, clazz.getSimpleName()), st.render()));
        LOGGER.debug(String.format("generate UnderMetaclass %s [END]", clazz.getSimpleName()));

    }

    private TableGeneratorWrapper scanTable(Class<?> clazz) {
        List<SQLPropertyWrapper> properties = new ArrayList<>();
        for (Field field : clazz.getDeclaredFields()) {
            if (Collection.class.isAssignableFrom(field.getType())) {
                // TODO List, Set, ... collection case (Link into the other
                // Table hint go to Map representation;
                continue;
            }
            try {
                JDBCType type = JDBCType.valueOf(JdbcTypeJavaClassMappings.INSTANCE.determineJdbcTypeCodeForJavaClass(field.getType()));
                Integer size = null;
                switch (type) {
                    case CHAR:
                    case VARCHAR:
                    case NCHAR:
                    case NVARCHAR:
                        size = 255;
                        break;
                    default:
                        break;
                }
                properties.add(new SQLPropertyWrapper(field.getName(), type.getName(), size));
            } catch (IllegalArgumentException e) {
            }
        }
        if (properties.size() > 0) {
            properties.get(properties.size() - 1).setLast(true);
        }
        return new TableGeneratorWrapper(clazz.getSimpleName().toUpperCase(), properties);
    }

    private List<GeneratedScript> refactorClasses(Class<?> clazz, List<Class<?>> archipels) {
        LOGGER.debug(String.format("refactoring of the data model for class %s [START]", clazz.getSimpleName()));
        List<GeneratedScript> scripts = new LinkedList<>();

        // Metaisation of the model
        if (ArchipelagoUtils.doesContainsAnnotation(clazz.getAnnotations(), Archipel.class)) {
            generateMetaClass(clazz, scripts);
            generateUnderMetaClass(clazz, scripts);
        } else if (ArchipelagoUtils.doesContainsAnnotation(clazz.getAnnotations(), Island.class)) {
            generateIslandClass(clazz, archipels, scripts);
        }
        LOGGER.debug(String.format("refactoring of the data model for class %s [END]", clazz.getSimpleName()));
        return scripts;
    }

    private void generateIslandClass(Class<?> clazz, List<Class<?>> islands, List<GeneratedScript> scripts) {
        String classTemplatePath;
        STGroup group;
        ST st;
        LOGGER.debug(String.format("generate UnderMetaclass %s [START]", clazz.getSimpleName()));
        classTemplatePath = TEMPLATE_ROOT_PATH + RELATIONAL_FOLDER + JAVA_FOLDER + "\\IslandClass.stg";
        group = StringTemplateFactory.buildSTGroup(classTemplatePath);
        st = group.getInstanceOf("IslandClass");
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
        classTemplatePath = TEMPLATE_ROOT_PATH + RELATIONAL_FOLDER + JAVA_FOLDER + "\\UnderMetaClass.stg";
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
        String classTemplatePath = TEMPLATE_ROOT_PATH + RELATIONAL_FOLDER + JAVA_FOLDER + "\\MetaClass.stg";
        STGroup group = StringTemplateFactory.buildSTGroup(classTemplatePath);
        ST st = group.getInstanceOf("MetaClass");
        st.add("className", clazz.getSimpleName());
        st.add("package", clazz.getPackage());
        scripts.add(new GeneratedScript(String.format(CLASS_FILE_NAME_META_FORMAT, clazz.getSimpleName()), st.render()));
        LOGGER.debug(String.format("generate MetaClass %s [END]", clazz.getSimpleName()));
    }

    private class TableGeneratorWrapper {
        private final String tableName;
        private final List<SQLPropertyWrapper> fields;

        public TableGeneratorWrapper(final String tableName, final List<SQLPropertyWrapper> fields) {
            super();
            this.tableName = tableName;
            this.fields = fields;
        }

        public List<SQLPropertyWrapper> getFields() {
            return fields;
        }

        public String getTableName() {
            return tableName;
        }

    }

    private class SQLPropertyWrapper {
        private String fieldName;
        private String fieldType;
        private Integer fieldSize;
        private boolean last = false;

        public SQLPropertyWrapper(String fieldName, String fieldType, Integer fieldSize) {
            super();
            this.fieldName = fieldName;
            this.fieldType = fieldType;
            this.fieldSize = fieldSize;
        }

        public String getFieldName() {
            return fieldName;
        }

        public void setFieldName(String fieldName) {
            this.fieldName = fieldName;
        }

        public String getFieldType() {
            return fieldType;
        }

        public void setFieldType(String fieldType) {
            this.fieldType = fieldType;
        }

        public Integer getFieldSize() {
            return fieldSize;
        }

        public void setFieldSize(Integer fieldSize) {
            this.fieldSize = fieldSize;
        }

        public boolean isLast() {
            return last;
        }

        public void setLast(boolean last) {
            this.last = last;
        }

    }
}
