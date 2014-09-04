/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mifosplatform.portfolio.pgs.currentaccountinformation.domain;

import org.mifosplatform.infrastructure.core.api.JsonCommand;
import org.mifosplatform.infrastructure.core.serialization.FromJsonHelper;
import org.mifosplatform.portfolio.pgs.currentaccountinformation.api.CurrentAccountInformationApiConstants;
import org.mifosplatform.portfolio.pgs.currentaccountinformation.exception.CurrentAccountInformationNotFoundException;
import org.mifosplatform.portfolio.pgs.serviceaccount.domain.ServiceAccount;
import org.mifosplatform.portfolio.pgs.serviceaccount.domain.ServiceAccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.gson.JsonElement;

@Component
public class CurrentAccountInformationAssembler {

    private final CurrentAccountInformationRepository currentAccountInformationRepository;
    private final FromJsonHelper fromApiJsonHelper;
	private final ServiceAccountRepository serviceAccountRepository;

    @Autowired
    public CurrentAccountInformationAssembler(final CurrentAccountInformationRepository currentAccountInformationRepository, 
    		final ServiceAccountRepository serviceAccountRepository, 
            final FromJsonHelper fromApiJsonHelper) {
        this.currentAccountInformationRepository = currentAccountInformationRepository;
        this.serviceAccountRepository = serviceAccountRepository;
        this.fromApiJsonHelper = fromApiJsonHelper;
    }

    public CurrentAccountInformation assembleFromJson(final JsonCommand command) {

        final JsonElement element = command.parsedJson();
        long clientId = 0;
        long savingsId = 0;
        double balance = 0.0;
        long serviceAccountId = 0;
        
        if (command.getServiceAccountId() != null) {
            serviceAccountId = command.getServiceAccountId();
        }
        
        final ServiceAccount serviceAccount = this.serviceAccountRepository.findOne(serviceAccountId);
        
        if (this.fromApiJsonHelper.parameterExists(CurrentAccountInformationApiConstants.clientParamName, element)) {
            clientId = this.fromApiJsonHelper.extractLongNamed(CurrentAccountInformationApiConstants.clientParamName, element);
        }
        
        if (this.fromApiJsonHelper.parameterExists(CurrentAccountInformationApiConstants.savingsIdParamName, element)) {
            savingsId = this.fromApiJsonHelper.extractLongNamed(CurrentAccountInformationApiConstants.savingsIdParamName, element);
        }

        if (this.fromApiJsonHelper.parameterExists(CurrentAccountInformationApiConstants.balanceParamName, element)) {
            balance = this.fromApiJsonHelper.extractDoubleNamed(CurrentAccountInformationApiConstants.balanceParamName, element);
            
        }
        return CurrentAccountInformation.CurrentAccountInformation(serviceAccount, clientId, savingsId, balance);
    }

    public CurrentAccountInformation assembleFromResourceId(final Long resourceId) {
        final CurrentAccountInformation currentAccountInformation = this.currentAccountInformationRepository.findOne(resourceId);
        if (currentAccountInformation == null) { throw new CurrentAccountInformationNotFoundException(resourceId); }
        return currentAccountInformation;
    }
}