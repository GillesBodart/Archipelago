package org.archipelago.test.domain.school;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.archipelago.core.annotations.Bridge;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Student extends Person {

    @Bridge(descriptor = "Follow")
    private List<Lesson> lessons = new ArrayList<>();
    @Bridge(descriptor = "FriendOf")
    private List<Student> friends = new ArrayList<>();
    @Bridge(descriptor = "FamilyMember", biDirectionnal = true)
    private List<? extends Person> familyMember = new ArrayList<>();
    @Bridge(descriptor = "GraduatedIn")
    private Promotion prom;

    public Student() {
        super();
    }

    public Student(String firstName, String lastName, LocalDate dateOfBirth,
                   String sexe,
                   List<Lesson> lessons,
                   List<Student> friends,
                   List<Person> familyMember,
                   Promotion prom) {
        super(firstName, lastName, dateOfBirth, sexe);
        this.lessons = lessons;
        this.friends = friends;
        this.familyMember = familyMember;
        this.prom = prom;
    }

    public List<Lesson> getLessons() {
        return lessons;
    }

    public void setLessons(List<Lesson> lessons) {
        this.lessons = lessons;
    }

    public List<Student> getFriends() {
        return friends;
    }

    public void setFriends(List<Student> friends) {
        this.friends = friends;
    }

    public List<? extends Person> getFamilyMember() {
        return familyMember;
    }

    public void setFamilyMember(List<? extends Person> familyMember) {
        this.familyMember = familyMember;
    }

    public Promotion getProm() {
        return prom;
    }

    public void setProm(Promotion prom) {
        this.prom = prom;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
}
