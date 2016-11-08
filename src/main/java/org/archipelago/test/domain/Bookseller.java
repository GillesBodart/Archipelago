package org.archipelago.test.domain;

import org.archipelago.core.annotations.Island;

@Island
public class Bookseller extends Person {

    private Library library;

    public Bookseller() {
        super();
    }

    public Library getLibrary() {
        return library;
    }

    public void setLibrary(Library library) {
        this.library = library;
    }

    public Bookseller(Library library) {
        super();
        this.library = library;
    }

}
