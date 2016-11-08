package org.archipelago.core.util;

import java.io.File;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import javax.tools.JavaCompiler;
import javax.tools.ToolProvider;

import org.apache.commons.io.FilenameUtils;
import org.archipelago.core.domain.GeneratedScript;

/**
 * 
 * @author Gilles Bodart
 *
 */
public class ArchipelagoUtils {

    public static final String JAVA_EXTENSION = "java";
    public static final String CLASS_EXTENSION = "class";

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
        for (File javaClass : javaClasses) {
            compiler.run(null, null, null, javaClass.getPath());
        }
    }

    public static void clean(final Path domainFolder) throws IOException {
        final List<File> javaCompiledClasses = analyse(domainFolder, CLASS_EXTENSION);
        for (File compiledClass : javaCompiledClasses) {
            Files.deleteIfExists(Paths.get(compiledClass.toURI()));
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

}
