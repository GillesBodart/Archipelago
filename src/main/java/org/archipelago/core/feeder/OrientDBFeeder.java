/**
 * 
 */
package org.archipelago.core.feeder;

import java.util.List;

import org.archipelago.core.domain.GeneratedScript;

/**
 * @author Gilles Bodart
 *
 */
public class OrientDBFeeder extends ArchipelagoScriptFeeder {


    @Override
    public List<GeneratedScript> makeScript(Class<?> clazz) {

        return null;
    }


    @Override
    public List<GeneratedScript> makeScript(Class<?> clazz, List<Class<?>> archipels) {

        return null;
    }
    // INSERT INTO Author (ArchipelID,name,lastName) VALUES
    // (0,"Gilles","Bodart") RETURN @this
    // http://orientdb.com/docs/2.0/orientdb.wiki/SQL-Insert.html

}
