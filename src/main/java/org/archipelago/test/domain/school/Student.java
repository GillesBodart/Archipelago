package org.archipelago.test.domain.school;

import java.util.ArrayList;
import java.util.List;

import org.archipelago.core.annotations.Archipel;

@Archipel
public class Student extends Person {

    private List<Lesson> lessons = new ArrayList<>();
    private List<Person> friends = new ArrayList<>();
    private List<Person> familyMember = new ArrayList<>();
    private Promotion prom;

    public List<Lesson> getLessons() {
        return lessons;
    }

    public void setLessons(List<Lesson> lessons) {
        this.lessons = lessons;
    }

    public List<Person> getFriends() {
        return friends;
    }

    public void setFriends(List<Person> friends) {
        this.friends = friends;
    }

    public List<Person> getFamilyMember() {
        return familyMember;
    }

    public void setFamilyMember(List<Person> familyMember) {
        this.familyMember = familyMember;
    }

    public Promotion getProm() {
        return prom;
    }

    public void setProm(Promotion prom) {
        this.prom = prom;
    }

    public Student(List<Lesson> lessons, List<Person> friends, List<Person> familyMember, Promotion prom) {
        super();
        this.lessons = lessons;
        this.friends = friends;
        this.familyMember = familyMember;
        this.prom = prom;
    }

}
