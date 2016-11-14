package org.archipelago.core.runtime;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.archipelago.core.domain.GeneratedScript;
import org.archipelago.core.domain.types.ArchipelagoFeederType;
import org.archipelago.core.util.ArchipelagoUtils;

/**
 * Created by Gilles Bodart on 18/08/2016.
 */
public class ArchipelagoFeederFactory {

    private static final Logger LOGGER = LogManager.getLogger();

    public static <T extends Object> List<GeneratedScript> generate(final Path outputFolder, final List<T> objects, ArchipelagoFeederType generationType)
            throws ClassNotFoundException,
            IOException {
        List<GeneratedScript> scripts = generationType.getFeeder().makeScript(objects);
        ArchipelagoUtils.generateFile(scripts, outputFolder);
        return scripts;
    }


}
