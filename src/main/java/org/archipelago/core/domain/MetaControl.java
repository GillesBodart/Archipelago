package org.archipelago.core.domain;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;

/**
 * Created by Gilles Bodart on 19/08/2016.
 */
@MappedSuperclass
public abstract class MetaControl implements Comparable<MetaControl> {
    @Column(name = "INSERTED_META_DATE")
    private Date insertedMeta;
    @Column(name = "DELETED_META_DATE")
    private Date deletedMeta;

    public Date getInsertedMeta() {
        return insertedMeta;
    }

    public void setInsertedMeta(final Date insertedMeta) {
        this.insertedMeta = insertedMeta;
    }

    public Date getDeletedMeta() {
        return deletedMeta;
    }

    public void setDeletedMeta(final Date deletedMeta) {
        this.deletedMeta = deletedMeta;
    }

    @Override
    public int compareTo(final MetaControl o) {
        final int delComp = deletedMeta.compareTo(o.deletedMeta);
        if (delComp == 0) {
            return insertedMeta.compareTo(o.insertedMeta);
        }
        return delComp;
    }
}
