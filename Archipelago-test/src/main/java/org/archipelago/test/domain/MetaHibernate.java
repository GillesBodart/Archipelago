package org.archipelago.test.domain;

import javax.persistence.*;
import java.util.SortedSet;
import java.util.TreeSet;

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
    private SortedSet<Integer> ones = new TreeSet<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "STOCK_ID", nullable = false)
    private String meta;
}
