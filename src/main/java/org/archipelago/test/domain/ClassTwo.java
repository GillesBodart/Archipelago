package org.archipelago.test.domain;

import org.archipelago.core.annotations.SimpleComponent;

/**
 * Created by GJULESGB on 19/08/2016.
 */
@SimpleComponent
public class ClassTwo {

    private ClassOne link;
    private Long id;

    public ClassOne getLink() {
        return link;
    }

    public void setLink(ClassOne link) {
        this.link = link;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
