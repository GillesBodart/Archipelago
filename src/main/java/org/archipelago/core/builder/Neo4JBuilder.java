package org.archipelago.core.builder;

import com.google.common.collect.Lists;
import org.archipelago.core.domain.GeneratedScript;
import org.archipelago.core.util.StringTemplateFactory;
import org.stringtemplate.v4.ST;
import org.stringtemplate.v4.STGroup;

import java.lang.reflect.Field;
import java.util.List;

/**
 * @author Gilles Bodart
 */
public class Neo4JBuilder extends ArchipelagoScriptBuilder {

    private static final String NEO4J_FOLDER = "\\Neo4J";

    @Override
    public List<GeneratedScript> makeScript(final Class<?> clazz) {
        return makeScript(clazz, null);
    }

    @Override
    public List<GeneratedScript> makeScript(Class<?> clazz, List<Class<?>> archipels) {
        return null;
    }

    public String makeCreate(Class<?> clazz) {
        final String nodeTemplatePath = TEMPLATE_ROOT_PATH + NEO4J_FOLDER + "\\node.stg";
        final STGroup group = StringTemplateFactory.buildSTGroup(nodeTemplatePath);
        final ST st = group.getInstanceOf("ClassNeo4J");
        st.add("clazz", clazz);
        for (Field field : clazz.getDeclaredFields()) {
            st.add("props", field.getName());
        }
        String create = st.render();
        LOGGER.debug(String.format("CREATE for class %s : [%s]", clazz.getSimpleName(), create));
        return create;
    }

    public List<String> fillCreate(Object o) {
        final List<String> parameters = Lists.newArrayList();
        for (Field field : o.getClass().getDeclaredFields()) {
            o.
            parameters.add()
        }
        String create = st.render();
        LOGGER.debug(String.format("CREATE for class %s : [%s]", clazz.getSimpleName(), create));
        return create;
    }

}
