package org.archipelago.test.domain;

import java.util.List;

import org.archipelago.core.annotations.Archipel;

@Archipel
public class Library {

    private List<Book> books;

    public List<Book> getBooks() {
        return books;
    }

    public void setBooks(List<Book> books) {
        this.books = books;
    }

    public Library(List<Book> books) {
        super();
        this.books = books;
    }

    public Library() {
        super();
    }

}
