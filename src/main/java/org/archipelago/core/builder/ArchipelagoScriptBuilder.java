package org.archipelago.core.builder;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.archipelago.core.domain.GeneratedScript;

import java.util.List;

/**
 * @author Gilles Bodart
 */
public abstract class ArchipelagoScriptBuilder {

    public static final String TEMPLATE_ROOT_PATH = "StringTemplate";
    protected final static Logger LOGGER = LogManager.getLogger(ArchipelagoScriptBuilder.class);

    public abstract List<GeneratedScript> makeScript(final Class<?> clazz);

    public abstract List<GeneratedScript> makeScript(final Class<?> clazz, final List<Class<?>> archipels);

    public String makeCreate(Class<?> aClass) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    public List<Object> fillCreate(Object object) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    public String makeMatch(Class<?> clazz) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    public String makeMatch(Class<?> clazz, boolean allObject) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    public String makeRelation(int idA, int idB, String name, Class<?> descriptor) {
        throw new UnsupportedOperationException("Not implemented yet");
    }
}
