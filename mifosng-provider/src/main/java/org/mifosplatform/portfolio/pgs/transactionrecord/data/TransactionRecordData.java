/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mifosplatform.portfolio.pgs.transactionrecord.data;

import java.util.Date;

import org.mifosplatform.infrastructure.core.data.EnumOptionData;

import com.sun.istack.FinalArrayList;

/**
 * Immutable data object representing a PGS Transaction Record.
 */
public class TransactionRecordData {

    @SuppressWarnings("unused")
    private final Long id;
    @SuppressWarnings("unused")
    private final long serviceAccountId;
    @SuppressWarnings("unused")
    private final String description;
    @SuppressWarnings("unused")
    private final Date date;
    @SuppressWarnings("unused")
    private final EnumOptionData type;
    @SuppressWarnings("unused")
    private final double amount;

    public static TransactionRecordData instance(final Long id, final long serviceAccountId, final String description, 
    		final Date date, final EnumOptionData type, final double amount) {
        return new TransactionRecordData(id, serviceAccountId, description, date, type, amount);
    }

    private TransactionRecordData(final Long id, final long serviceAccountId, final String description, final Date date, 
    		final EnumOptionData type, final double amount) {
        this.id = id;
        this.serviceAccountId = serviceAccountId;
        this.description = description;
        this.date = date;
        this.type = type;
        this.amount = amount;
    }

}