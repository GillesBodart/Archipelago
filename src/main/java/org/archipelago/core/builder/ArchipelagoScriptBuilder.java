package org.archipelago.core.builder;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.archipelago.core.domain.GeneratedScript;

/**
 * 
 * @author Gilles Bodart
 *
 */
public abstract class ArchipelagoScriptBuilder {

    public static final String TEMPLATE_ROOT_PATH = "StringTemplate";
    protected final static Logger LOGGER = LogManager.getLogger(ArchipelagoScriptBuilder.class);

    public abstract List<GeneratedScript> makeScript(final Class<?> clazz);

    public abstract List<GeneratedScript> makeScript(final Class<?> clazz, final List<Class<?>> islands);

}
