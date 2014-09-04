/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mifosplatform.portfolio.pgs.serviceaccount.data;

import java.util.Collection;

import org.mifosplatform.portfolio.pgs.serviceaccountstatushistory.data.ServiceAccountStatusHistoryData;
import org.mifosplatform.portfolio.pgs.transactionrecord.data.TransactionRecordData;
import org.mifosplatform.portfolio.pgs.usagerecord.data.UsageRecordData;

/**
 * Immutable data object representing a PGS Service Offering.
 */
public class ServiceAccountData {

    private final Long id;
    @SuppressWarnings("unused")
    private final Long pgsClientId;
    @SuppressWarnings("unused")
    private final Long currentAccountInformationId;
    @SuppressWarnings("unused")
    private final Long usageRecordId;
    @SuppressWarnings("unused")
    private final Long transactionRecordId;
    @SuppressWarnings("unused")
    private final Double amount;
    @SuppressWarnings("unused")
    private final boolean isActivated;
    @SuppressWarnings("unused")
    private final Long serviceAccountStatusHistoryId;
    @SuppressWarnings("unused")
    private final Long serviceOfferingId;
    @SuppressWarnings("unused")
    private final Collection<UsageRecordData> usageRecords;
    @SuppressWarnings("unused")
    private final Collection<TransactionRecordData> transactionRecords;
    @SuppressWarnings("unused")
    private final Collection<ServiceAccountStatusHistoryData> serviceAccountStatusHistories;
    
    public static ServiceAccountData instance(final Long id, final Long pgsClientId, final Long currentAccountInformationId, 
    		final Long usageRecordId, final Long transactionRecordId, final Double amount, final boolean isActivated, 
    		final Long serviceAccountStatusHistoryId, final Long serviceOfferingId) {
        return new ServiceAccountData(id, pgsClientId, currentAccountInformationId, usageRecordId, transactionRecordId, amount, isActivated,
        		serviceAccountStatusHistoryId, serviceOfferingId, null, null, null);
    }

    private ServiceAccountData(final Long id, final Long pgsClientId, final Long currentAccountInformationId, 
    		final Long usageRecordId, final Long transactionRecordId, final Double amount, final boolean isActivated, 
    		final Long serviceAccountStatusHistoryId, final Long serviceOfferingId, final Collection<UsageRecordData> usageRecords,
    		final Collection<TransactionRecordData> transactionRecords, final Collection<ServiceAccountStatusHistoryData> serviceAccountStatusHistories) {
        this.id = id;
        this.pgsClientId = pgsClientId;
        this.currentAccountInformationId = currentAccountInformationId;
        this.usageRecordId = usageRecordId;
        this.transactionRecordId = transactionRecordId;
        this.amount = amount;
        this.isActivated = isActivated;
        this.serviceAccountStatusHistoryId = serviceAccountStatusHistoryId;
        this.serviceOfferingId = serviceOfferingId;
        this.usageRecords = usageRecords;
        this.transactionRecords = transactionRecords;
        this.serviceAccountStatusHistories = serviceAccountStatusHistories;
        
    }

	public Long getId() {
		return this.id;
	}

}