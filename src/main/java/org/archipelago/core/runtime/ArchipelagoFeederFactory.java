package org.archipelago.core.runtime;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.archipelago.core.domain.GeneratedScript;
import org.archipelago.core.domain.types.ArchipelagoFeederType;

/**
 * Created by Gilles Bodart on 18/08/2016.
 */
public class ArchipelagoFeederFactory {

    private static final Logger LOGGER = LogManager.getLogger();

    public static List<GeneratedScript> generate(final List<Object> objects, ArchipelagoFeederType generationType) throws ClassNotFoundException, IOException {
        List<GeneratedScript> scripts = new ArrayList<>();
        for (Object o : objects) {
            scripts.addAll(generationType.getFeeder().makeScript(o));
        }
        return scripts;
    }


}
