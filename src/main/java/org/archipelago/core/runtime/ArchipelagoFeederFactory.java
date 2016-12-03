package org.archipelago.core.runtime;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.WildcardType;
import java.nio.file.Path;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.archipelago.core.annotations.Archipel;
import org.archipelago.core.annotations.Continent;
import org.archipelago.core.annotations.Island;
import org.archipelago.core.domain.GeneratedScript;
import org.archipelago.core.domain.types.ArchipelagoFeederType;
import org.archipelago.core.util.ArchipelagoUtils;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

/**
 * Created by Gilles Bodart on 18/08/2016.
 */
public class ArchipelagoFeederFactory {

    private static final Logger LOGGER = LogManager.getLogger();

    private static final Map<Class<?>, Set<Field>> fieldOfClass = new HashMap<>();

    public static <T extends Object> List<GeneratedScript> generate(final Path outputFolder, final List<T> objects, ArchipelagoFeederType generationType)
            throws ClassNotFoundException, IOException {
        List<GeneratedScript> scripts = generationType.getFeeder().makeScript(objects);
        ArchipelagoUtils.generateFile(scripts, outputFolder);
        return scripts;
    }

    public static <T extends Object> List<GeneratedScript> generate(final Path outputFolder, final T object, ArchipelagoFeederType generationType)
            throws ClassNotFoundException, IOException {
        List<Object> objects = Lists.newArrayList();
        objects.addAll(getObjectUnder(object, Sets.newHashSet()));
        List<GeneratedScript> scripts = generationType.getFeeder().makeScript(objects);
        ArchipelagoUtils.generateFile(scripts, outputFolder);
        return scripts;
    }

    private static <T extends Object> Set<Object> getObjectUnder(T object, Set<Object> objects) {
        Class<?> clazz = object.getClass();
        if (ArchipelagoUtils.doesContainsAnnotation(clazz.getAnnotations(), Archipel.class, Island.class, Continent.class)) {
            objects.add(object);
            Set<Field> fields = ArchipelagoUtils.getAllFields(clazz);
            if (fields == null) {
                fields = Sets.newHashSet(clazz.getFields());
                fieldOfClass.put(object.getClass(), fields);
            }
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
                    if (Collection.class.isAssignableFrom(field.getType())) {
                        ParameterizedType genericType = (ParameterizedType) field.getGenericType();
                        Class<?> genericClass = null;
                        if (genericType.getActualTypeArguments()[0] instanceof WildcardType) {
                            genericClass = (Class<?>) ((WildcardType) genericType.getActualTypeArguments()[0]).getUpperBounds()[0];
                        } else {
                            genericClass = (Class<?>) genericType.getActualTypeArguments()[0];
                        }
                        if (ArchipelagoUtils.doesContainsAnnotation(genericClass.getAnnotations(), Archipel.class, Island.class, Continent.class)) {
                            if (null != getter.invoke(object)) {
                                for (Object o : (Collection<?>) getter.invoke(object)) {
                                    if (!objects.contains(o)) {
                                        objects.add(o);
                                        objects.addAll(getObjectUnder(o, objects));
                                    }
                                }
                            }
                        }
                    } else if (ArchipelagoUtils.doesContainsAnnotation(field.getType().getAnnotations(), Archipel.class, Island.class, Continent.class)) {
                        Object node = getter.invoke(object);
                        if (!objects.contains(node)) {
                            objects.addAll(getObjectUnder(node, objects));
                        }
                    }
                } catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
                    LOGGER.debug(String.format("No usual getter for %s", field.getName()), e);
                }
            }
        }
        return objects;

    }

}
