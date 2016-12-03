/**
 * 
 */
package org.archipelago.core.feeder;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.archipelago.core.domain.GeneratedScript;
import org.archipelago.core.util.ArchipelagoUtils;
import org.archipelago.core.util.StringTemplateFactory;
import org.stringtemplate.v4.ST;
import org.stringtemplate.v4.STGroup;

/**
 * @author Gilles Bodart
 *
 */
public class OrientDBFeeder extends ArchipelagoScriptFeeder {

    private static final String ORIENT_FOLDER = "\\OrientDB";
    private static final String OBJECT_FILE_NAME = "OrientObjectInsert.txt";

    @Override
    public <T extends Object> List<GeneratedScript> makeScript(final List<T> objects) {
        List<GeneratedScript> scripts = new ArrayList<>();

        final String classTemplatePath = TEMPLATE_ROOT_PATH + ORIENT_FOLDER + "\\insert.stg";

        final STGroup group = StringTemplateFactory.buildSTGroup(classTemplatePath);
        final ST st = group.getInstanceOf("insertOrientDB");
        for (T object : objects) {
            List<String> properties = new ArrayList<>();
            List<Object> values = new ArrayList<>();
            Class<?> clazz = object.getClass();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            Set<Field> fields = ArchipelagoUtils.getAllFields(clazz);
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
                    if (field.getType().isPrimitive()) {
                        properties.add(field.getName());
                        values.add(getter.invoke(object));
                    } else if (field.getType().getSimpleName().equalsIgnoreCase("String")) {
                        // TODO insert native Java Lang objects ...
                        properties.add(field.getName());
                        values.add(String.format("\'%s\'", getter.invoke(object)));
                    } else if (field.getType().getSimpleName().equalsIgnoreCase("Date")) {
                        // TODO insert native Java Lang objects ...
                        properties.add(String.format("%s", field.getName()));
                        values.add(String.format("\'%s\'", sdf.format(getter.invoke(object))));
                    } else {
                        // TODO insert objects ...
                    }
                } catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
                    LOGGER.debug(String.format("No usual getter for %s", field.getName()), e);
                }
            }
            String superParent = null;
            if (!clazz.getSuperclass().equals(Object.class)) {
                superParent = clazz.getSuperclass().getSimpleName();
            }
            st.add("insert", new InsertionWrapper(properties, clazz.getSimpleName(), superParent, values));
        }
        scripts.add(new GeneratedScript(OBJECT_FILE_NAME, st.render()));
        return scripts;
    }

    // INSERT INTO Author (ArchipelID,name,lastName) VALUES (sequence('AuthorArchipelagoSeq').next(),"Gilles","Bodart") RETURN @this
    // http://orientdb.com/docs/2.0/orientdb.wiki/SQL-Insert.html

    // INSERT INTO account SET id = sequence('idseq').next()

    private class InsertionWrapper {

        private List<String> properties = new ArrayList<>();
        private String parent;
        private String superParent;
        private List<Object> values = new ArrayList<>();

        public InsertionWrapper(List<String> properties, String parent, String superParent, List<Object> values) {
            super();
            this.properties = properties;
            this.parent = parent;
            this.superParent = superParent;
            this.values = values;
        }

        public List<String> getProperties() {
            return properties;
        }

        public void setProperties(List<String> properties) {
            this.properties = properties;
        }

        public String getParent() {
            return parent;
        }

        public void setParent(String parent) {
            this.parent = parent;
        }

        public List<Object> getValues() {
            return values;
        }

        public void setValues(List<Object> values) {
            this.values = values;
        }

        public String getSuperParent() {
            return superParent;
        }

        public void setSuperParent(String superParent) {
            this.superParent = superParent;
        }

    }

}
