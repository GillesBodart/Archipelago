package org.archipelago.test.domain.school;

import java.util.ArrayList;
import java.util.List;

import org.archipelago.core.annotations.Island;

@Island
public class School {
    
    private String name;
    private Person director;
    private List<Person> teachers = new ArrayList<>();
    private List<Person> students = new ArrayList<>();
    private List<Person> workers = new ArrayList<>();
    private List<Room> rooms = new ArrayList<>();

    public Person getDirector() {
        return director;
    }

    public void setDirector(Person director) {
        this.director = director;
    }

    public List<Person> getTeachers() {
        return teachers;
    }

    public void setTeachers(List<Person> teachers) {
        this.teachers = teachers;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Person> getStudents() {
        return students;
    }

    public void setStudents(List<Person> students) {
        this.students = students;
    }

    public List<Room> getRooms() {
        return rooms;
    }

    public void setRooms(List<Room> rooms) {
        this.rooms = rooms;
    }

    public List<Person> getWorkers() {
        return workers;
    }

    public void setWorkers(List<Person> workers) {
        this.workers = workers;
    }


}
