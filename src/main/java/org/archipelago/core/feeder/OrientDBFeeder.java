/**
 * 
 */
package org.archipelago.core.feeder;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.archipelago.core.domain.GeneratedScript;
import org.archipelago.core.util.StringTemplateFactory;
import org.stringtemplate.v4.ST;
import org.stringtemplate.v4.STGroup;

/**
 * @author Gilles Bodart
 *
 */
public class OrientDBFeeder extends ArchipelagoScriptFeeder {

    private static final String ORIENT_FOLDER = "\\OrientDB";
    private static final String OBJECT_FILE_NAME = "OrientObject%s.txt";

    @Override
    public List<GeneratedScript> makeScript(Object object) {
        List<GeneratedScript> scripts = new ArrayList<>();
        List<InsertionWrapper> inserts = new ArrayList<>();
        final String classTemplatePath = TEMPLATE_ROOT_PATH + ORIENT_FOLDER + "\\class.stg";
        Class<?> clazz = object.getClass();
        for (Field field : clazz.getFields()) {
            try {
                Method getter = clazz.getMethod(String.format("get%s%s", field.getName().charAt(0), field.getName().substring(1, field.getName().length())));
                if (field.getType().isPrimitive()) {
                    inserts.add(new InsertionWrapper(field.getName(), clazz.getSimpleName(), getter.invoke(object)));
                } else if (field.getType().getPackage().getName().equalsIgnoreCase("java.lang")) {
                    // TODO insert native Java Lang objects ...
                    inserts.add(new InsertionWrapper(field.getName(), clazz.getSimpleName(), getter.invoke(object)));
                }else {
                    // TODO insert objects ...
                }
            } catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
                LOGGER.debug(String.format("No usual getter for %s", field.getName()), e);
            }
        }
        final STGroup group = StringTemplateFactory.buildSTGroup(classTemplatePath);
        final ST st = group.getInstanceOf("ClassOrientDB");
        return scripts;
    }

    // INSERT INTO Author (ArchipelID,name,lastName) VALUES (sequence('AuthorArchipelagoSeq').next(),"Gilles","Bodart") RETURN @this
    // http://orientdb.com/docs/2.0/orientdb.wiki/SQL-Insert.html

    // INSERT INTO account SET id = sequence('idseq').next()

    private class InsertionWrapper {

        private String property;
        private String parent;
        private Object value;

        public InsertionWrapper(String property, String parent, Object value) {
            super();
            this.property = property;
            this.parent = parent;
            this.value = value;
        }

        public String getProperty() {
            return property;
        }

        public void setProperty(String property) {
            this.property = property;
        }

        public Object getValue() {
            return value;
        }

        public void setValue(Object value) {
            this.value = value;
        }

        public String getParent() {
            return parent;
        }

        public void setParent(String parent) {
            this.parent = parent;
        }

    }

}
