package org.archipelago.core.domain;

import org.archipelago.core.interfaces.MetaElement;
import org.archipelago.core.interfaces.UnderMetaElement;

/**
 * Created by GJULESGB on 18/08/2016.
 */
public abstract class UnderMeta<T extends MetaElement> extends MetaControl
        implements UnderMetaElement {

    private T parentMeta;

    public T getParentMeta() {
        return parentMeta;
    }

    public void setParentMeta(T parentMeta) {
        this.parentMeta = parentMeta;
    }

}
