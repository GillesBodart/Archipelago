package org.archipelago.core.domain.types;

import org.archipelago.core.feeder.ArchipelagoScriptFeeder;
import org.archipelago.core.feeder.Neo4JFeeder;
import org.archipelago.core.feeder.OrientDBFeeder;
import org.archipelago.core.feeder.RelationalSQLFeeder;

/**
 * 
 * @author Gilles Bodart
 *
 */
public enum ArchipelagoFeederType {

    ORIENT_DB {
        @Override
        public ArchipelagoScriptFeeder getFeeder() {
            return new OrientDBFeeder();
        }
    },
    RELATIONAL_SQL {
        @Override
        public ArchipelagoScriptFeeder getFeeder() {
            return new RelationalSQLFeeder();
        }
    },
    NEO4J {
        @Override
        public ArchipelagoScriptFeeder getFeeder() {
            return new Neo4JFeeder();
        }
    };

    public abstract ArchipelagoScriptFeeder getFeeder();
}
