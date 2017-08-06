package org.archipelago.core.util;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.AnnotationUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.archipelago.core.annotations.Bridge;
import org.archipelago.core.domain.RelationWrapper;

import javax.tools.JavaCompiler;
import javax.tools.ToolProvider;
import java.io.File;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.archipelago.core.framework.Archipelago.ARCHIPELAGO_ID;

/**
 * @author Gilles Bodart
 */
public class ArchipelagoUtils {

    public static final String JAVA_EXTENSION = "java";
    public static final String CLASS_EXTENSION = "class";
    private static final Pattern pattern = Pattern.compile("\\w+To(\\w+)");
    private final static Logger LOGGER = LogManager.getLogger(ArchipelagoUtils.class);

    public static boolean doesContainsAnnotation(List<Annotation> annotations, Class<? extends Annotation>... classes) {
        for (Annotation annotation : annotations) {
            for (Class<?> clazz : classes) {
                if (clazz.equals(annotation.annotationType())) {
                    return true;
                }
            }
        }
        return false;
    }

    public static boolean doesContainsAnnotation(Annotation[] annotations, Class<? extends Annotation>... classes) {
        return doesContainsAnnotation(Arrays.asList(annotations), classes);
    }

    public static void compile(final List<File> javaClasses) {
        final JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
        if (null != javaClasses) {
            for (File javaClass : javaClasses) {
                compiler.run(null, null, null, javaClass.getPath());
            }
        }
    }

    public static void clean(final Path domainFolder) throws IOException {
        final List<File> javaCompiledClasses = analyse(domainFolder, CLASS_EXTENSION);
        if (null != domainFolder) {
            for (File compiledClass : javaCompiledClasses) {

                Files.deleteIfExists(Paths.get(compiledClass.toURI()));
            }
        }
    }

    public static URL[] retriveURL(final Path domainFolder, final List<File> javaClasses) throws MalformedURLException {
        final URL[] urls = new URL[javaClasses.size()];
        for (int i = 0; i < javaClasses.size(); i++) {
            urls[i] = javaClasses.get(i).toURI().toURL();
        }
        return urls;
    }

    public static List<File> analyse(final Path folder, final String extension) {
        assert (folder.toFile().isDirectory());
        final List<File> files = new LinkedList<>();

        for (File element : folder.toFile().listFiles()) {
            if (FilenameUtils.getExtension(element.getName()).equalsIgnoreCase(extension)) {
                files.add(element);
            }
        }
        return files;
    }

    public static void scan(final Class<?> clazz) {
        for (Annotation x : clazz.getAnnotations()) {
            LOGGER.debug(String.format("Annotation : %s", AnnotationUtils.toString(x)));
        }
        for (Field x : clazz.getDeclaredFields()) {
            LOGGER.debug(String.format("Field : %s", x.getName()));
        }
        for (Method x : clazz.getDeclaredMethods()) {
            LOGGER.debug(String.format("Method : %s", x.getName()));
        }
        for (Constructor<?> x : clazz.getDeclaredConstructors()) {
            LOGGER.debug(String.format("Constructor : %s", x.getName()));
        }
    }

    public static Set<Field> getAllFields(Class<?> clazz) {
        Set<Field> fields = new HashSet<>();

        fields.addAll(Arrays.asList(clazz.getDeclaredFields()));

        Class<?> superClazz = clazz.getSuperclass();
        if (superClazz != null) {
            fields.addAll(getAllFields(superClazz));
        }

        return fields;
    }


    public static void feedObject(Object o, Map<String, Object> properties) {
        for (Map.Entry<String, Object> entry : properties.entrySet()) {
            String fieldName = entry.getKey();
            if (!ARCHIPELAGO_ID.equalsIgnoreCase(fieldName) && null != entry.getValue() && !"@".equalsIgnoreCase("" + fieldName.charAt(0))) {
                Method setter = null;
                String methodName = String.format("set%s%s", ("" + fieldName.charAt(0)).toUpperCase(), fieldName.substring(1, fieldName.length()));
                try {
                    if (Integer.class.equals(entry.getValue().getClass())) {
                        try {
                            setter = o.getClass().getMethod(methodName, Integer.class);
                            setter.invoke(o, Integer.valueOf("" + entry.getValue()));
                        } catch (NoSuchMethodException e) {
                            setter = o.getClass().getMethod(methodName, Long.class);
                            setter.invoke(o, Long.valueOf("" + entry.getValue()));
                        }
                    } else {
                        setter = o.getClass().getMethod(methodName, entry.getValue().getClass());
                        setter.invoke(o, entry.getValue());
                    }
                } catch (Exception e) {
                    LOGGER.debug(String.format("Method not found %s with param %s", methodName, entry.getValue().getClass()));
                }
            }
        }
    }

    public static Object formatQueryValue(Object o) {
        return formatQueryValue(o, false);
    }

    public static Object formatQueryValue(Object o, boolean stringDate) {
        return formatQueryValue(o, stringDate, false);
    }
    public static Object formatQueryValue(Object o, boolean stringDate, boolean appendString) {
        Object formated = o;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-mm-dd");
        if (formated instanceof String && appendString) {
            formated = String.format("\"%s\"", formated);
        } else {
            String elm = null;
            switch (o.getClass().getSimpleName().toLowerCase()) {
                case "date":
                    elm = sdf.format((Date) o);
                    formated = stringDate ? String.format("\"%s\"", elm) : elm;
                    break;
                case "localdate":
                    elm = ((LocalDate) o).format(DateTimeFormatter.ISO_LOCAL_DATE);
                    formated = stringDate ? String.format("\"%s\"", elm) : elm;
                    break;
                case "localtime":
                    elm = ((LocalTime) o).format(DateTimeFormatter.ISO_LOCAL_DATE);
                    formated = stringDate ? String.format("\"%s\"", elm) : elm;
                    break;
                case "localdatetime":
                    elm = ((LocalDateTime) o).format(DateTimeFormatter.ISO_LOCAL_DATE);
                    formated = stringDate ? String.format("\"%s\"", elm) : elm;
                    break;
                default: {
                    formated = o;
                }
            }
        }
        return formated;
    }


    public static List<RelationWrapper> getChilds(Object object) {
        List<RelationWrapper> relations = new ArrayList<>();
        Class<?> clazz = object.getClass();
        for (Field field : getAllFields(clazz)) {
            if (field.isAnnotationPresent(Bridge.class)) {
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
                    List<Object> props = new ArrayList<>();
                    if (null != prop) {
                        if (prop instanceof Collection) {
                            Iterator i = ((Collection) prop).iterator();
                            while (i.hasNext()) {
                                Object child = i.next();
                                props.add(child);
                            }
                        } else {
                            props.add(prop);
                        }
                    }
                    props.stream().forEach(p -> {
                        RelationWrapper rw = new RelationWrapper();
                        rw.setName(field.getAnnotation(Bridge.class).descriptor());
                        rw.setTo(p);
                        rw.setBiDirectionnal(field.getAnnotation(Bridge.class).biDirectionnal());
                        relations.add(rw);
                    });
                } catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
                    LOGGER.debug(String.format("No usual getter for %s found", field.getName()), e);
                }
            }
        }
        return relations;
    }

    public static Object get(Class<?> clazz, Field field, Object object) {
        Method getter = null;
        try {
            if (field.getType().equals(boolean.class) || field.getType().equals(Boolean.class)) {
                getter = clazz.getMethod(String.format("is%s%s", ("" + field.getName().charAt(0)).toUpperCase(), field.getName().substring(1, field
                        .getName().length())));
            } else {
                getter = clazz.getMethod(String.format("get%s%s", ("" + field.getName().charAt(0)).toUpperCase(), field.getName().substring(1, field
                        .getName().length())));
            }
            return getter.invoke(object);
        } catch (Exception e) {
            LOGGER.debug(String.format("No usual getter for %s found", field.getName()), e);
            LOGGER.error(e.getMessage(), e);
        }
        return null;
    }


    public static Class getClassOf(Field field) {
        Class clazz;
        if (Collection.class.isAssignableFrom(field.getType())) {
            ParameterizedType genericType = (ParameterizedType) field.getGenericType();
            if (genericType.getActualTypeArguments()[0] instanceof WildcardType) {
                clazz = (Class<?>) ((WildcardType) genericType.getActualTypeArguments()[0]).getUpperBounds()[0];
            } else {
                clazz = (Class<?>) genericType.getActualTypeArguments()[0];
            }
        } else {
            clazz = field.getType();
        }
        return clazz;
    }

    public static Class<?> getClassOf(Set<Class<?>> classes, String className) {
        for (Class clazz : classes) {
            if (clazz.getSimpleName().equalsIgnoreCase(className)) {
                return clazz;
            }
        }
        return null;
    }

    public static Field getFieldFromBridgeName(Class<?> clazz, String type) {
        Field field = null;
        Matcher m = pattern.matcher(type);
        Set<Field> fields = getAllFields(clazz);

        for (Field f : fields) {
            if (f.isAnnotationPresent(Bridge.class)
                    && type.equalsIgnoreCase(f.getAnnotation(Bridge.class).descriptor())) {
                return f;
            }
        }
        return field;
    }
}
