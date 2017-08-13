////////////////////////////////////////////////////////////////////
//
// File: City.java
// Created: 12/08/2017
// Author: Gilles Bodart ABM589
// Electrabel n.v./s.a., Regentlaan 8 Boulevard du Régent, BTW BE 0403.107.701 - 1000 Brussel/Bruxelles, Belgium.
//
// Proprietary Notice:
// This software is the confidential and proprietary information of Electrabel s.a./n.v. and/or its licensors. 
// You shall not disclose this Confidential Information to any third parties
// and any use thereof shall be subject to the terms and conditions of use, as agreed upon with Electrabel in writing.
//
////////////////////////////////////////////////////////////////////
package org.archipelago.test.domain.got;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.archipelago.core.annotations.Bridge;

import java.util.ArrayList;
import java.util.List;

public class City {

    private String name;

    @Bridge(descriptor = "Road")
    private List<City> connected = new ArrayList<>();

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public City(String name) {
        this.name = name;
    }
    public City() {
        this.name = name;
    }

    public List<City> getConnected() {
        return connected;
    }

    public void setConnected(List<City> connected) {
        this.connected = connected;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("name", name)
                .append("connected", connected)
                .toString();
    }
}
