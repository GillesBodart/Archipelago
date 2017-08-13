package org.archipelago.core.builder.OrientDB;

import org.archipelago.core.annotations.Bridge;
import org.archipelago.core.builder.abstraction.ArchipelagoScriptBuilder;
import org.archipelago.core.domain.DescriptorWrapper;
import org.archipelago.core.util.ArchipelagoUtils;
import org.archipelago.core.util.StringTemplateFactory;
import org.stringtemplate.v4.ST;
import org.stringtemplate.v4.STGroup;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Gilles Bodart
 */
public class OrientDBBuilder extends ArchipelagoScriptBuilder {

    private static final String ORIENT_FOLDER = "\\OrientDB";

    @Override
    public String makeCreate(Object object) {
        final StringBuilder sb = new StringBuilder();
        sb.append(String.format("{\"@class\":\"%s\"", object.getClass().getSimpleName()));
        Iterator<Object> iterator = fillCreate(object).iterator();
        while (iterator.hasNext()) {
            sb.append(",");
            sb.append(String.format("\"%s\":%s", iterator.next(), iterator.next()));
        }
        sb.append("}");
        LOGGER.debug(String.format("CREATE for class %s : [%s]", object.getClass().getSimpleName(), sb.toString()));
        return sb.toString();
    }

    @Override
    public String makeRelation(Object idA, Object idB, DescriptorWrapper description) {
        return String.format("CREATE EDGE %s FROM %s TO %s SET created = %s",
                description.getName(), idA, idB, ArchipelagoUtils.formatQueryValue(description.getCreated(), true));
    }

    @Override
    public String makeRelation(Object idA, Object idB, String name, Object descriptor) {
        StringBuilder sb = new StringBuilder(String.format("CREATE EDGE %s FROM %s TO %s ", name,
                idA, idB));
        sb.append("SET ");
        List<String> props = new ArrayList<>();
        for (Field field : ArchipelagoUtils.getAllFields(descriptor.getClass())) {
            Object o = ArchipelagoUtils.get(descriptor.getClass(), field, descriptor);
            if (null != o) {
                props.add(String.format("%s = %s", field.getName(), ArchipelagoUtils.formatQueryValue(o, true, true)));
            }
        }
        sb.append(props.stream().collect(Collectors.joining(", ")));
        return sb.toString();
    }

    public String makeMatch(Object object) {
        Class<?> clazz = object.getClass();
        final String nodeTemplatePath = String.format("%s/%s/%s", TEMPLATE_ROOT_PATH, ORIENT_FOLDER, "\\match.stg");
        final STGroup group = StringTemplateFactory.buildSTGroup(nodeTemplatePath);
        final ST st = group.getInstanceOf("MatchOrientDB");
        st.add("clazz", clazz);
        if (String.class.equals(clazz)) {
            st.add("props", "name");
        } else {
            for (Field field : ArchipelagoUtils.getAllFields(clazz)) {
                if (!field.isAnnotationPresent(Bridge.class)) {
                    Object o = ArchipelagoUtils.get(clazz, field, object);
                    if (null != o) {
                        st.add("props", new ValueWrapper(field.getName(), ArchipelagoUtils.formatQueryValue(o, true, true)));
                    }
                }
            }
        }
        String match = st.render();
        LOGGER.debug(String.format("MATCH for class %s : [%s]", clazz.getSimpleName(), match));
        return match;
    }

    private class ValueWrapper {
        private String name;
        private Object value;

        public ValueWrapper(String name, Object value) {
            this.name = name;
            this.value = value;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public Object getValue() {
            return value;
        }

        public void setValue(Object value) {
            this.value = value;
        }
    }
}
