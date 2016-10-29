package org.archipelago.core.util;

import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.List;

public class ArchipelagoUtils {

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

}
