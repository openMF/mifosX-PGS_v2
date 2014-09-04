/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mifosplatform.portfolio.pgs.currentaccountinformation.data;

import org.mifosplatform.portfolio.pgs.pgsclient.domain.PGSClient;

/**
 * Immutable data object representing a PGS Service Offering.
 */
public class CurrentAccountInformationData {

    @SuppressWarnings("unused")
    private final Long id;
    @SuppressWarnings("unused")
    private final Long clientId;
    @SuppressWarnings("unused")
    private final Long savingsId;
    @SuppressWarnings("unused")
    private final double balance;

    public static CurrentAccountInformationData instance(final Long id, final long clientId, final Long savingsId, final double balance) {
        return new CurrentAccountInformationData(id, clientId, savingsId, balance);
    }

    private CurrentAccountInformationData(final Long id, final Long clientId, final long savingsId, final double balance) {
        this.id = id;
        this.clientId = clientId;
        this.savingsId = savingsId;
        this.balance = balance;
    }

}