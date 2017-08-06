package org.archipelago.core.builder;

import com.google.common.collect.Lists;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.archipelago.core.annotations.Bridge;
import org.archipelago.core.domain.DescriptorWrapper;
import org.archipelago.core.util.ArchipelagoUtils;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Set;

/**
 * @author Gilles Bodart
 */
public abstract class ArchipelagoScriptBuilder {

    public static final String TEMPLATE_ROOT_PATH = "StringTemplate";
    protected final static Logger LOGGER = LogManager.getLogger(ArchipelagoScriptBuilder.class);


    public abstract String makeCreate(Object object);

    public List<Object> fillCreate(Object object) {
        final List<Object> parameters = Lists.newArrayList();
        Class<?> clazz = object.getClass();
        Set<Field> fields = ArchipelagoUtils.getAllFields(clazz);
        if (String.class.equals(clazz)) {
            parameters.add("name");
            parameters.add(object);
        } else {
            for (Field field : fields) {
                if (!field.isAnnotationPresent(Bridge.class)) {
                    Object prop = ArchipelagoUtils.get(clazz, field, object);
                    if (null != prop) {
                        parameters.add(field.getName());
                        parameters.add(ArchipelagoUtils.formatQueryValue(prop, true));
                    }
                }
            }
        }

        return parameters;
    }

    public String makeMatch(Object object) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    public String makeMatch(Object object, boolean allObject) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    public String makeRelation(int idA, int idB, String name, Class<?> descriptor) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    public String makeRelation(int idA, int idB, String name, List<String> props) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    public String makeRelation(Object idA, Object idB, DescriptorWrapper description) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    public String makeRelation(Object idA, Object idB, String description) {
        throw new UnsupportedOperationException("Not implemented yet");
    }
}
