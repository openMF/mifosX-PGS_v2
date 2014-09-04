/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mifosplatform.portfolio.pgs.usagerecord.data;

import java.util.Date;

import org.joda.time.DateTime;
import org.mifosplatform.infrastructure.core.data.EnumOptionData;

/**
 * Immutable data object representing a PGS Usaage Record.
 */
public class UsageRecordData {

    @SuppressWarnings("unused")
    private final Long id;
    @SuppressWarnings("unused")
    private final long serviceAccountId;
    @SuppressWarnings("unused")
    private final Date date;
    @SuppressWarnings("unused")
    private final EnumOptionData metricType;
    @SuppressWarnings("unused")
    private final double noOfUnits;
    @SuppressWarnings("unused")
    private final DateTime startDateTime;
    @SuppressWarnings("unused")
    private final DateTime endDateTime;
    
    public static UsageRecordData instance(final Long id, final long serviceAccountId, final Date date, final EnumOptionData metricTrype, 
    		final double noOfUnits, final DateTime startDateTime, final DateTime endDateTime) {
        return new UsageRecordData(id, serviceAccountId, date, metricTrype, noOfUnits, startDateTime, endDateTime);
    }

    private UsageRecordData(final Long id, final long serviceAccountId, final Date date, final EnumOptionData metricTrype, 
    		final double noOfUnits, final DateTime startDateTime, final DateTime endDateTime) {
        this.id = id;
        this.serviceAccountId = serviceAccountId;
        this.date = date;
        this.metricType = metricTrype;
        this.noOfUnits = noOfUnits;
        this.startDateTime = startDateTime;
        this.endDateTime = endDateTime;
    }

}