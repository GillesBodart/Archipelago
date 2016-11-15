package org.archipelago.test.domain.school;

import java.util.ArrayList;
import java.util.List;

import org.archipelago.core.annotations.Archipel;

@Archipel
public class Teacher extends Person {

    private List<Lesson> capabilities = new ArrayList<>();
    private List<Lesson> dispensed = new ArrayList<>();
    private String diploma;

}
