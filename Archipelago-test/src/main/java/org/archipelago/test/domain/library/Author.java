package org.archipelago.test.domain.library;

import java.util.List;



public class Author extends Person {

    private List<Book> books;

    public Author() {
        super();
    }

    public Author(List<Book> books) {
        super();
        this.books = books;
    }

    public List<Book> getBooks() {
        return books;
    }

    public void setBooks(List<Book> books) {
        this.books = books;
    }

}
