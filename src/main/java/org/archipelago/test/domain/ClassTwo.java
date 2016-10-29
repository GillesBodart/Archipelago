package org.archipelago.test.domain;

import org.archipelago.core.annotations.Garland;

/**
 * Created by GJULESGB on 19/08/2016.
 */
@Garland
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
