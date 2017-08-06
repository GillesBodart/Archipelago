package org.archipelago.test.domain.school;

import org.archipelago.core.annotations.Bridge;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Teacher extends Person {
    @Bridge(descriptor = "CanTeach")
    private List<Lesson> capabilities = new ArrayList<>();
    @Bridge(descriptor = "Teach")
    private List<Lesson> dispensed = new ArrayList<>();

    private String diploma;

    public Teacher() {
        super();
    }

    public Teacher(String firstName, String lastName, LocalDate dateOfBirth, String sexe, List<Lesson> capabilities, List<Lesson> dispensed, String diploma) {
        super(firstName, lastName, dateOfBirth, sexe);
        this.capabilities = capabilities;
        this.dispensed = dispensed;
        this.diploma = diploma;
    }

    public List<Lesson> getCapabilities() {
        return capabilities;
    }

    public void setCapabilities(List<Lesson> capabilities) {
        this.capabilities = capabilities;
    }

    public List<Lesson> getDispensed() {
        return dispensed;
    }

    public void setDispensed(List<Lesson> dispensed) {
        this.dispensed = dispensed;
    }

    public String getDiploma() {
        return diploma;
    }

    public void setDiploma(String diploma) {
        this.diploma = diploma;
    }


}
