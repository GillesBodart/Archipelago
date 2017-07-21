package org.archipelago.core.util;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.AnnotationUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.archipelago.core.annotations.ArchipelId;
import org.archipelago.core.domain.GeneratedScript;

import javax.tools.JavaCompiler;
import javax.tools.ToolProvider;
import java.io.File;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.*;

import static org.archipelago.core.connection.Archipelago.ARCHIPELAGO_ID;

/**
 * @author Gilles Bodart
 */
public class ArchipelagoUtils {

    public static final String JAVA_EXTENSION = "java";
    public static final String CLASS_EXTENSION = "class";
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

    public static void generateFile(List<GeneratedScript> generated, Path outputDir) throws IOException {
        Files.createDirectories(outputDir);
        Path classPath = outputDir;
        for (GeneratedScript script : generated) {
            classPath = outputDir.resolve(script.getScriptName());
            Files.deleteIfExists(classPath);
            Files.createFile(classPath);
            Files.write(classPath, script.getContent().getBytes(), StandardOpenOption.CREATE);
        }
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
        Object formated = o;
        if (formated instanceof String) {
            formated = String.format("\"%s\"", formated);
        }
        return formated;
    }

    public static void feedId(Object node, Object value) throws InvocationTargetException, IllegalAccessException, NoSuchMethodException {
        Optional<Field> archipelagoId = getAllFields(node.getClass())
                .stream()
                .filter(field -> field.isAnnotationPresent(ArchipelId.class))
                .findFirst();
        if (archipelagoId.isPresent()) {
            String fieldName = archipelagoId.get().getName();
            Method setter = node.getClass()
                    .getMethod(String.format("set%s%s", ("" + fieldName.charAt(0)).toUpperCase(), fieldName.substring(1, fieldName.length())), value.getClass());
            setter.invoke(node, value);
        } else {
            LOGGER.error(String.format("No ArchipelId annotation in the class %s", node.getClass()));
        }

    }
}
