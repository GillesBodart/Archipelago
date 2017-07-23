package org.archipelago.test.domain.school;

import org.archipelago.core.annotations.Archipel;
import org.archipelago.core.annotations.Bridge;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Archipel
public class Worker extends Person {

    @Bridge(descriptor = "Manage")
    private List<Room> inChargeOf = new ArrayList<>();
    private String diploma;

    public Worker() {
        super();
    }

    public Worker(String firstName, String lastName, LocalDate dateOfBirth, String sexe, List<Room> inChargeOf, String diploma) {
        super(firstName, lastName, dateOfBirth, sexe);
        this.diploma = diploma;
        this.inChargeOf = inChargeOf;
    }

    public List<Room> getInChargeOf() {
        return inChargeOf;
    }

    public void setInChargeOf(List<Room> inChargeOf) {
        this.inChargeOf = inChargeOf;
    }

    public String getDiploma() {
        return diploma;
    }

    public void setDiploma(String diploma) {
        this.diploma = diploma;
    }


}
