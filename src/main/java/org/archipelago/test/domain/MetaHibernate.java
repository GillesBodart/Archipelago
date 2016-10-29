package org.archipelago.test.domain;

import java.util.SortedSet;
import java.util.TreeSet;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

/**
 * Created by GJULESGB on 19/08/2016.
 */
@Entity
@Table(name = "DB_TAB")
public class MetaHibernate {
    @Id
    @GeneratedValue(generator = "my_sec")
    @SequenceGenerator(name = "my_sec", sequenceName = "my_seq_in_db")
    @Column(name = "")
    private Long id;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "otherFieldName")
    private SortedSet<ClassOne> ones = new TreeSet<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "STOCK_ID", nullable = false)
    private ClassTwo meta;
}
