package org.archipelago.core.util;

import org.stringtemplate.v4.*;
import org.stringtemplate.v4.misc.ErrorBuffer;

import java.util.Date;

/**
 * 
 * @author Gilles Bodart
 *
 */
public class StringTemplateFactory {

    public static STGroup buildSTGroup(final String ressourcePath) {
        final STGroup group = new STGroupFile(ressourcePath);
        group.registerRenderer(String.class, new StringRenderer());
        group.registerRenderer(Integer.class, new NumberRenderer());
        group.registerRenderer(Date.class, new DateRenderer());
        final ErrorBuffer errors = new ErrorBuffer();
        group.setListener(errors);
        group.load();
        return group;
    }
}
