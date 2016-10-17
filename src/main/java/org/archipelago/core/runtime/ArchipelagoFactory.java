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
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedList;
import java.util.List;

import javax.tools.JavaCompiler;
import javax.tools.ToolProvider;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.AnnotationUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Created by GJULESGB on 18/08/2016.
 */
public class ArchipelagoFactory {

    public static final String JAVA_EXTENSION = "java";
    public static final String CLASS_EXTENSION = "class";

    private static final Logger LOGGER = LogManager.getLogger();

    public static void generate(Path domainFolder) throws ClassNotFoundException, IOException {
        List<File> javaClasses = analyse(domainFolder, JAVA_EXTENSION);
        URL[] urls = new URL[javaClasses.size()];
        for (int i = 0; i < javaClasses.size(); i++) {
            urls[i] = javaClasses.get(i).toURI().toURL();
        }


        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
        for (File javaClass : javaClasses) {
            compiler.run(null, null, null, javaClass.getPath());
        }
        URLClassLoader classLoader = URLClassLoader.newInstance(urls);
        for (File javaClass : javaClasses) {
            String pack = FileUtils.readLines(javaClass, Charset.forName("UTF-8")).get(0).replaceAll("package", "").replaceAll("[; ]", "");
            String className = String.format("%s.%s", pack, FilenameUtils.getBaseName(javaClass.toString()));
            LOGGER.debug(String.format("class to load : %s", className));
            Class clazz = classLoader.loadClass(className);
           if (LOGGER.isDebugEnabled()){
               scan(clazz);
           }
        }
        List<File> javaCompiledClasses = analyse(domainFolder, CLASS_EXTENSION);
        for(File compiledClass : javaCompiledClasses){
            Files.deleteIfExists(Paths.get(compiledClass.toURI()));
        }

    }

    private static List<File> analyse(Path folder, String extension) {
        assert (folder.toFile().isDirectory());
        List<File> paths = new LinkedList<>();

        for (File element : folder.toFile().listFiles()) {
            if (FilenameUtils.getExtension(element.getName()).equalsIgnoreCase(extension)) {
                paths.add(element);
            }
        }
        return paths;
    }


    private static void scan(Class clazz){
            for (Annotation x : clazz.getAnnotations()){

                LOGGER.debug(String.format("Annotation : %s", AnnotationUtils.toString(x)));
            }
            for (Field x : clazz.getDeclaredFields()){
                LOGGER.debug(String.format("Field : %s",x.getName()));
            }
            for (Method x : clazz.getDeclaredMethods()){
                LOGGER.debug(String.format("Method : %s",x.getName()));
            }
            for (Constructor x : clazz.getDeclaredConstructors()){
                LOGGER.debug(String.format("Constructor : %s",x.getName()));
            }
    }
}
