package org.archipelago.core.runtime;

import java.io.File;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.charset.Charset;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.archipelago.core.annotations.Archipel;
import org.archipelago.core.annotations.Continent;
import org.archipelago.core.annotations.Island;
import org.archipelago.core.domain.GeneratedScript;
import org.archipelago.core.domain.types.ArchipelagoBuilderType;
import org.archipelago.core.util.ArchipelagoUtils;

/**
 * Created by Gilles Bodart on 18/08/2016.
 */
public class ArchipelagoBuilderFactory {

    private static final Logger LOGGER = LogManager.getLogger();

    public static List<GeneratedScript> generate(final Path domainFolder, ArchipelagoBuilderType generationType) throws ClassNotFoundException, IOException {

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

    private static List<GeneratedScript> execute(final ClassLoader classLoader, final File javaClass, ArchipelagoBuilderType generationType)
            throws IOException, ClassNotFoundException {
        final String pack = FileUtils.readLines(javaClass, Charset.forName("UTF-8")).get(0).replaceAll("package", "").replaceAll("[; ]", "");
        final String className = String.format("%s.%s", pack, FilenameUtils.getBaseName(javaClass.toString()));
        LOGGER.debug(String.format("class to load : %s", className));
        final Class<?> clazz = classLoader.loadClass(className);
        if (LOGGER.isDebugEnabled()) {
            ArchipelagoUtils.scan(clazz);
        }
        List<Annotation> annotations = Arrays.asList(clazz.getAnnotations());
        List<GeneratedScript> scripts = null;
        if (ArchipelagoUtils.doesContainsAnnotation(annotations, Archipel.class, Island.class, Continent.class)) {
            scripts = generationType.getBuilder().makeScript(clazz);
        }
        return scripts;
    }

}
