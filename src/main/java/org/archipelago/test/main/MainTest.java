package org.archipelago.test.main;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.archipelago.core.domain.GeneratedScript;
import org.archipelago.core.domain.types.ArchipelagoGenerationType;
import org.archipelago.core.runtime.ArchipelagoFactory;

/**
 * Created by Gilles Bodart on 19/08/2016.
 */
public class MainTest {

    protected final static Logger LOGGER = LogManager.getLogger(MainTest.class);

    private final static ArchipelagoGenerationType TEST_TYPE = ArchipelagoGenerationType.ORIENT_DB;
    private final static String TEST_PATH = "C:\\Sand\\IdeaProjects\\Archipelago\\src\\main\\java\\org\\archipelago\\test\\domain";

    public static void main(String[] args) throws ClassNotFoundException, IOException {

        List<GeneratedScript> scripts = ArchipelagoFactory.generate(Paths.get(TEST_PATH), TEST_TYPE);
        // Show scripts
        for (GeneratedScript script : scripts) {
            LOGGER.info(script);
        }
    }

}
