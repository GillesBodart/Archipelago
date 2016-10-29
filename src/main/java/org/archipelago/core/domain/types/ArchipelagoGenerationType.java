package org.archipelago.core.domain.types;

import org.archipelago.core.builder.ArchipelagoScriptBuilder;
import org.archipelago.core.builder.Neo4JBuilder;
import org.archipelago.core.builder.OrientDBBuilder;
import org.archipelago.core.builder.RelationalSQLBuilder;

public enum ArchipelagoGenerationType {

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
