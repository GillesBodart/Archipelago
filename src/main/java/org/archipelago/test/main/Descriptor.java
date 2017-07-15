////////////////////////////////////////////////////////////////////
//
// File: Descriptor.java
// Created: 12/07/2017
// Author: Gilles Bodart ABM589
// Electrabel n.v./s.a., Regentlaan 8 Boulevard du Régent, BTW BE 0403.107.701 - 1000 Brussel/Bruxelles, Belgium.
//
// Proprietary Notice:
// This software is the confidential and proprietary information of Electrabel s.a./n.v. and/or its licensors. 
// You shall not disclose this Confidential Information to any third parties
// and any use thereof shall be subject to the terms and conditions of use, as agreed upon with Electrabel in writing.
//
////////////////////////////////////////////////////////////////////
package org.archipelago.test.main;

import java.time.LocalDate;

/**
 * Created by ABM589 on 12/07/2017.
 */
public class Descriptor {
    LocalDate since;

    public Descriptor(LocalDate since) {
        this.since = since;
    }

    public LocalDate getSince() {
        return since;
    }

    public void setSince(LocalDate since) {
        this.since = since;
    }
}
