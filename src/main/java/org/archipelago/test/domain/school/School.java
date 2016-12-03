package org.archipelago.test.domain.school;

import java.util.ArrayList;
import java.util.List;

import org.archipelago.core.annotations.Island;

@Island
public class School {
    
    private String name;
    private Person director;
    private List<? extends Person> teachers = new ArrayList<>();
    private List<? extends Person> students = new ArrayList<>();
    private List<? extends Person> workers = new ArrayList<>();
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


}
