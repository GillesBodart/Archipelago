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
 * Created by GJULESGB on 19/08/2016.
 */
public class MainTest {

    protected final static Logger LOGGER = LogManager.getLogger(MainTest.class);

    public static void main(String[] args)
            throws ClassNotFoundException, IOException {
        List<GeneratedScript> scripts = ArchipelagoFactory.generate(
                Paths.get("C:\\Sand\\IdeaProjects\\Archipelago\\src\\main\\java\\org\\archipelago\\test\\domain"),
                ArchipelagoGenerationType.RELATIONAL_SQL);
        for (GeneratedScript script : scripts) {
            LOGGER.info(script);
        }
        // final ArchipelagoScriptBuilder builder = new OrientDBBuilder();
        // final String script = builder.makeScript(Form.class);
        // LOGGER.info(script);

    }

}
