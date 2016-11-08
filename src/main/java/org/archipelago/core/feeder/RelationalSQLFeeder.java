package org.archipelago.core.feeder;

import java.util.List;

import org.archipelago.core.domain.GeneratedScript;

public class RelationalSQLFeeder extends ArchipelagoScriptFeeder {

    @Override
    public List<GeneratedScript> makeScript(Class<?> clazz) {
        return null;
    }

    @Override
    public List<GeneratedScript> makeScript(Class<?> clazz, List<Class<?>> archipels) {
        return null;
    }

}
