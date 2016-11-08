package org.archipelago.test.domain;

import java.util.Date;

import org.archipelago.core.annotations.Archipel;

@Archipel
public class Book {
    private Author author;
    private int amountPages;
    private Date publishDate;

    public Author getAuthor() {
        return author;
    }

    public void setAuthor(Author author) {
        this.author = author;
    }

    public int getAmountPages() {
        return amountPages;
    }

    public void setAmountPages(int amountPages) {
        this.amountPages = amountPages;
    }

    public Date getPublishDate() {
        return publishDate;
    }

    public void setPublishDate(Date publishDate) {
        this.publishDate = publishDate;
    }

    public Book(Author author, int amountPages, Date publishDate) {
        super();
        this.author = author;
        this.amountPages = amountPages;
        this.publishDate = publishDate;
    }

}
