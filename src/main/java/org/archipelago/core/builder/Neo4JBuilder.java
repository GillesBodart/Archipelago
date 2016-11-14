package org.archipelago.core.builder;

import java.util.List;

import org.archipelago.core.domain.GeneratedScript;

/**
 * 
 * @author Gilles Bodart
 *
 */
public class Neo4JBuilder extends ArchipelagoScriptBuilder {

    @Override
    public List<GeneratedScript> makeScript(final Class<?> clazz) {
        return makeScript(clazz, null);
    }

    @Override
    public List<GeneratedScript> makeScript(Class<?> clazz, List<Class<?>> archipels) {
        return null;
    }

}
