package org.archipelago.core.util;

import java.util.Date;

import org.stringtemplate.v4.DateRenderer;
import org.stringtemplate.v4.NumberRenderer;
import org.stringtemplate.v4.STGroup;
import org.stringtemplate.v4.STGroupFile;
import org.stringtemplate.v4.StringRenderer;
import org.stringtemplate.v4.misc.ErrorBuffer;

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
