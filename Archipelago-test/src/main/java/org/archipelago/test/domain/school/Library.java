package org.archipelago.test.domain.school;

import org.archipelago.core.annotations.Bridge;

import java.util.List;

public class Library extends Room {

    @Bridge(descriptor = "Have")
    private List<String> books;
    @Bridge(descriptor = "ManagedBy")
    private Person worker;

    public Library() {
    }

    public Library(String roomName, List<String> books, Person worker) {
        super(roomName);
        this.books = books;
        this.worker = worker;
    }

    public List<String> getBooks() {
        return books;
    }

    public void setBooks(List<String> books) {
        this.books = books;
    }

    public Person getWorker() {
        return worker;
    }

    public void setWorker(Person worker) {
        this.worker = worker;
    }

}
