package org.archipelago.test.domain.school;

import java.time.LocalDate;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.archipelago.core.annotations.Continent;

@Continent
public abstract class Person {

    private String firstName;
    private String lastName;
    private LocalDate dateOfBirth;
    private String sexe;

    public Person() {
    }

    public Person(String firstName, String lastName, LocalDate dateOfBirth, String sexe) {
        super();
        this.firstName = firstName;
        this.lastName = lastName;
        this.dateOfBirth = dateOfBirth;
        this.sexe = sexe;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(LocalDate dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public String getSexe() {
        return sexe;
    }

    public void setSexe(String sexe) {
        this.sexe = sexe;
    }


    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("firstName", firstName)
                .append("lastName", lastName)
                .append("dateOfBirth", dateOfBirth)
                .append("sexe", sexe)
                .toString();
    }
}
