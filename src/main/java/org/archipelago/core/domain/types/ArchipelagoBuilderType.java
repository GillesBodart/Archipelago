package org.archipelago.core.domain.types;

import org.archipelago.core.builder.old.ArchipelagoScriptBuilder;
import org.archipelago.core.builder.old.Neo4JBuilder;
import org.archipelago.core.builder.old.OrientDBBuilder;
import org.archipelago.core.builder.old.RelationalSQLBuilder;

/**
 * 
 * @author Gilles Bodart
 *
 */
public enum ArchipelagoBuilderType {

    ORIENT_DB {
        @Override
        public ArchipelagoScriptBuilder getBuilder() {
            return new OrientDBBuilder();
        }
    },
    RELATIONAL_SQL {
        @Override
        public ArchipelagoScriptBuilder getBuilder() {
            return new RelationalSQLBuilder();
        }
    },
    NEO4J {
        @Override
        public ArchipelagoScriptBuilder getBuilder() {
            return new Neo4JBuilder();
        }
    };

    public abstract ArchipelagoScriptBuilder getBuilder();
}
