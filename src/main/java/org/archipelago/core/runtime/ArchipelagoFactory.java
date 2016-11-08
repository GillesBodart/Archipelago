package org.archipelago.core.runtime;

import java.io.File;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.charset.Charset;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.AnnotationUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.archipelago.core.annotations.Archipel;
import org.archipelago.core.annotations.Continent;
import org.archipelago.core.annotations.Island;
import org.archipelago.core.domain.GeneratedScript;
import org.archipelago.core.domain.types.ArchipelagoGenerationType;
import org.archipelago.core.util.ArchipelagoUtils;

/**
 * Created by Gilles Bodart on 18/08/2016.
 */
public class ArchipelagoFactory {

    private static final Logger LOGGER = LogManager.getLogger();

    public static List<GeneratedScript> generate(final Path domainFolder, ArchipelagoGenerationType generationType) throws ClassNotFoundException, IOException {

        final List<File> javaClasses = ArchipelagoUtils.analyse(domainFolder, ArchipelagoUtils.JAVA_EXTENSION);
        ArchipelagoUtils.compile(javaClasses);
        List<GeneratedScript> scripts = new LinkedList<>();
        final URL[] urls = ArchipelagoUtils.retriveURL(domainFolder, javaClasses);
        final URLClassLoader classLoader = URLClassLoader.newInstance(urls);
        for (File javaClass : javaClasses) {
            List<GeneratedScript> generated = execute(classLoader, javaClass, generationType);
            if (null != generated) {
                scripts.addAll(generated);
            }
        }
        ArchipelagoUtils.generateFile(scripts, domainFolder.resolve("archipelago"));
        ArchipelagoUtils.clean(domainFolder);
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
        if (ArchipelagoUtils.doesContainsAnnotation(annotations, Archipel.class, Island.class, Continent.class)) {
            scripts = generationType.getBuilder().makeScript(clazz);
        }
        return scripts;
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

}
