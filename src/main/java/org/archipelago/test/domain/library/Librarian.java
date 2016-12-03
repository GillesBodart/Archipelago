package org.archipelago.test.domain.library;

import org.archipelago.core.annotations.Island;

@Island
public class Librarian extends Person {

    private Library library;

    public Librarian() {
        super();
    }

    public Library getLibrary() {
        return library;
    }

    public void setLibrary(Library library) {
        this.library = library;
    }

    public Librarian(Library library) {
        super();
        this.library = library;
    }

}
