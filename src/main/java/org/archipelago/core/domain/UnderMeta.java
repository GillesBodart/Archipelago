package org.archipelago.core.domain;

import org.archipelago.core.interfaces.MetaElement;
import org.archipelago.core.interfaces.UnderMetaElement;

/**
 * Created by GJULESGB on 18/08/2016.
 */
public class UnderMeta<T extends MetaElement,K> extends MetaControl implements UnderMetaElement {

    private T parentMeta;
    private K detail;

}
