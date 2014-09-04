/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mifosplatform.portfolio.pgs.serviceaccountstatushistory.domain;

import java.util.Date;
import java.util.Locale;

import org.joda.time.LocalDate;
import org.mifosplatform.infrastructure.core.api.JsonCommand;
import org.mifosplatform.infrastructure.core.serialization.FromJsonHelper;
import org.mifosplatform.portfolio.pgs.serviceaccount.domain.ServiceAccount;
import org.mifosplatform.portfolio.pgs.serviceaccount.domain.ServiceAccountRepository;
import org.mifosplatform.portfolio.pgs.serviceaccountstatushistory.api.ServiceAccountStatusHistoryApiConstants;
import org.mifosplatform.portfolio.pgs.serviceaccountstatushistory.exception.ServiceAccountStatusHistoryNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.gson.JsonElement;

@Component
public class ServiceAccountStatusHistoryAssembler {

    private final ServiceAccountStatusHistoryRepository serviceAccountStatusHistoryRepository;
    private final ServiceAccountRepository serviceAccountRepository;
    private final FromJsonHelper fromApiJsonHelper;

    @Autowired
    public ServiceAccountStatusHistoryAssembler(final ServiceAccountStatusHistoryRepository serviceAccountStatusHistoryRepository,
            final FromJsonHelper fromApiJsonHelper, final ServiceAccountRepository serviceAccountRepository) {
        this.serviceAccountStatusHistoryRepository = serviceAccountStatusHistoryRepository;
        this.fromApiJsonHelper = fromApiJsonHelper;
        this.serviceAccountRepository = serviceAccountRepository;
    }

    public ServiceAccountStatusHistory assembleFromJson(final JsonCommand command) {

        final JsonElement element = command.parsedJson();
        long serviceAccountId = 0;
        Date date = null;
        LocalDate localdate = null;
        int change = 0;
        
        if (command.getServiceAccountId() != null) {
            serviceAccountId = command.getServiceAccountId();
        }
        
        final ServiceAccount serviceAccount = this.serviceAccountRepository.findOne(serviceAccountId);
        
        
        if (this.fromApiJsonHelper.parameterExists(ServiceAccountStatusHistoryApiConstants.dateParamName, element)) {
            localdate = this.fromApiJsonHelper.extractLocalDateNamed(ServiceAccountStatusHistoryApiConstants.dateParamName, element);
            date = localdate.toDate();
        }
        
        if (this.fromApiJsonHelper.parameterExists(ServiceAccountStatusHistoryApiConstants.changeParamName, element)) {
            change = this.fromApiJsonHelper.extractIntegerNamed(ServiceAccountStatusHistoryApiConstants.changeParamName, element, Locale.getDefault());
            
        }
        
        return ServiceAccountStatusHistory.createNew(serviceAccount, date, change);
    }

    public ServiceAccountStatusHistory assembleFromResourceId(final Long resourceId) {
        final ServiceAccountStatusHistory serviceAccountStatusHistory = this.serviceAccountStatusHistoryRepository.findOne(resourceId);
        if (serviceAccountStatusHistory == null) { throw new ServiceAccountStatusHistoryNotFoundException(resourceId); }
        return serviceAccountStatusHistory;
    }
}