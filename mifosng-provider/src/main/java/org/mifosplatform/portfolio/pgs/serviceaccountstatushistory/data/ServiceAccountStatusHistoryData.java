/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mifosplatform.portfolio.pgs.serviceaccountstatushistory.data;

import java.util.Date;

import org.mifosplatform.infrastructure.core.data.EnumOptionData;

/**
 * Immutable data object representing a PGS Service Offering.
 */
public class ServiceAccountStatusHistoryData {

    @SuppressWarnings("unused")
    private final Long id;
    @SuppressWarnings("unused")
    private final long serviceAccountId;
    @SuppressWarnings("unused")
    private final Date date;
    @SuppressWarnings("unused")
    private final Integer change;

    public static ServiceAccountStatusHistoryData instance(final Long id, final Long serviceAccountID, final Date date, final Integer change) {
        return new ServiceAccountStatusHistoryData(id, serviceAccountID, date, change);
    }

    private ServiceAccountStatusHistoryData(final Long id, final long serviceAccountId, final Date date, final Integer change) {
        this.id = id;
        this.serviceAccountId = serviceAccountId;
        this.date = date;
        this.change = change;
    }

}