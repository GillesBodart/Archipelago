package org.archipelago.core.runtime;

import java.io.File;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import javax.tools.JavaCompiler;
import javax.tools.ToolProvider;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.AnnotationUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.archipelago.core.annotations.Garland;
import org.archipelago.core.annotations.Island;
import org.archipelago.core.domain.GeneratedScript;
import org.archipelago.core.domain.types.ArchipelagoGenerationType;
import org.archipelago.core.util.ArchipelagoUtils;

/**
 * Created by GJULESGB on 18/08/2016.
 */
public class ArchipelagoFactory {

    public static final String JAVA_EXTENSION = "java";
    public static final String CLASS_EXTENSION = "class";

    private static final Logger LOGGER = LogManager.getLogger();

    public static List<GeneratedScript> generate(final Path domainFolder, ArchipelagoGenerationType generationType) throws ClassNotFoundException, IOException {

        final List<File> javaClasses = analyse(domainFolder, JAVA_EXTENSION);
        compile(javaClasses);
        List<GeneratedScript> scripts = new LinkedList<>();
        final URL[] urls = retriveURL(domainFolder, javaClasses);
        final URLClassLoader classLoader = URLClassLoader.newInstance(urls);
        for (File javaClass : javaClasses) {
            List<GeneratedScript> generated = execute(classLoader, javaClass, generationType);

            if (null != generated) {
                scripts.addAll(generated);
            }
        }
        // Path outputClassFile = outputDir.resolve(clazz.getName().replace(".",
        // "\\") + "TestBuilder.java");
        // Files.createDirectories(outputClassFile.getParent());
        // Files.deleteIfExists(outputClassFile);
        // Files.createFile(outputClassFile);
        // Files.write(outputClassFile, result.getBytes(),
        // StandardOpenOption.CREATE);
        clean(domainFolder);
        return scripts;
    }

    private static List<GeneratedScript> execute(final ClassLoader classLoader, final File javaClass, ArchipelagoGenerationType generationType)
            throws IOException, ClassNotFoundException {
        final String pack = FileUtils.readLines(javaClass, Charset.forName("UTF-8")).get(0).replaceAll("package", "").replaceAll("[; ]", "");
        final String className = String.format("%s.%s", pack, FilenameUtils.getBaseName(javaClass.toString()));
        LOGGER.debug(String.format("class to load : %s", className));
        final Class<?> clazz = classLoader.loadClass(className);
        if (LOGGER.isDebugEnabled()) {
            scan(clazz);
        }
        List<Annotation> annotations = Arrays.asList(clazz.getAnnotations());
        List<GeneratedScript> scripts = null;
        if (ArchipelagoUtils.doesContainsAnnotation(annotations, Island.class, Garland.class)) {
            scripts = generationType.getBuilder().makeScript(clazz);
        }
        return scripts;
    }

    private static List<File> analyse(final Path folder, final String extension) {
        assert (folder.toFile().isDirectory());
        final List<File> files = new LinkedList<>();

        for (File element : folder.toFile().listFiles()) {
            if (FilenameUtils.getExtension(element.getName()).equalsIgnoreCase(extension)) {
                files.add(element);
            }
        }
        return files;
    }

    private static void scan(final Class<?> clazz) {
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

    private static void compile(final List<File> javaClasses) {
        final JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
        for (File javaClass : javaClasses) {
            compiler.run(null, null, null, javaClass.getPath());
        }
    }

    private static void clean(final Path domainFolder) throws IOException {
        final List<File> javaCompiledClasses = analyse(domainFolder, CLASS_EXTENSION);
        for (File compiledClass : javaCompiledClasses) {
            Files.deleteIfExists(Paths.get(compiledClass.toURI()));
        }
    }

    private static URL[] retriveURL(final Path domainFolder, final List<File> javaClasses) throws MalformedURLException {
        final URL[] urls = new URL[javaClasses.size()];
        for (int i = 0; i < javaClasses.size(); i++) {
            urls[i] = javaClasses.get(i).toURI().toURL();
        }
        return urls;
    }

}
