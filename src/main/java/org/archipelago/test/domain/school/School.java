package org.archipelago.test.domain.school;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.archipelago.core.annotations.Bridge;
import org.archipelago.core.annotations.Island;

import java.util.ArrayList;
import java.util.List;

@Island
public class School {

    private String name;
    @Bridge(descriptor = "HeldBy")
    private Person director;
    @Bridge(descriptor = "ComposedOf")
    private List<? extends Person> teachers = new ArrayList<>();
    @Bridge(descriptor = "TeachTo")
    private List<? extends Person> students = new ArrayList<>();
    @Bridge(descriptor = "Employ")
    private List<? extends Person> workers = new ArrayList<>();
    @Bridge(descriptor = "Contains")
    private List<Room> rooms = new ArrayList<>();

    public School() {
    }

    public School(String name, Person director, List<Person> teachers, List<Person> students, List<Person> workers, List<Room> rooms) {
        super();
        this.name = name;
        this.director = director;
        this.teachers = teachers;
        this.students = students;
        this.workers = workers;
        this.rooms = rooms;
    }

    public Person getDirector() {
        return director;
    }

    public <T extends Person> void setDirector(T director) {
        this.director = director;
    }

    public List<? extends Person> getTeachers() {
        return teachers;
    }

    public void setTeachers(List<? extends Person> teachers) {
        this.teachers = teachers;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<? extends Person> getStudents() {
        return students;
    }

    public void setStudents(List<? extends Person> students) {
        this.students = students;
    }

    public List<Room> getRooms() {
        return rooms;
    }

    public void setRooms(List<Room> rooms) {
        this.rooms = rooms;
    }

    public List<? extends Person> getWorkers() {
        return workers;
    }

    public void setWorkers(List<? extends Person> workers) {
        this.workers = workers;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("name", name)
                .append("director", director)
                .append("teachers", teachers)
                .append("students", students)
                .append("workers", workers)
                .append("rooms", rooms)
                .toString();
    }
}
