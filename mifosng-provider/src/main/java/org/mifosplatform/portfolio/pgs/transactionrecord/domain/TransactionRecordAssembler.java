/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mifosplatform.portfolio.pgs.transactionrecord.domain;

import java.util.Date;
import java.util.Locale;

import org.joda.time.LocalDate;
import org.mifosplatform.infrastructure.core.api.JsonCommand;
import org.mifosplatform.infrastructure.core.serialization.FromJsonHelper;
import org.mifosplatform.portfolio.pgs.serviceaccount.domain.ServiceAccount;
import org.mifosplatform.portfolio.pgs.serviceaccount.domain.ServiceAccountRepository;
import org.mifosplatform.portfolio.pgs.transactionrecord.api.TransactionRecordApiConstants;
import org.mifosplatform.portfolio.pgs.transactionrecord.exception.TransactionRecordNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.gson.JsonElement;

@Component
public class TransactionRecordAssembler {

    private final TransactionRecordRepository transactionRecordRepository;
    private final FromJsonHelper fromApiJsonHelper;
	private ServiceAccountRepository serviceAccountRepository;

    @Autowired
    public TransactionRecordAssembler(final TransactionRecordRepository transactionRecordRepository,
            final FromJsonHelper fromApiJsonHelper, 
            final ServiceAccountRepository serviceAccountRepository) {
        this.transactionRecordRepository = transactionRecordRepository;
        this.fromApiJsonHelper = fromApiJsonHelper;
        this.serviceAccountRepository = serviceAccountRepository;
    }

    public TransactionRecord assembleFromJson(final JsonCommand command) {

        final JsonElement element = command.parsedJson();
        Long serviceAccountId = null;
        String description = null;
        LocalDate localdate = null;
        Date date = null;
        Integer type = null;
        Double amount = null;
        
        if (command.getServiceAccountId() != null) {
            serviceAccountId = command.getServiceAccountId();
        }

        final ServiceAccount serviceAccount = this.serviceAccountRepository.findOne(serviceAccountId); 
        
        if (this.fromApiJsonHelper.parameterExists(TransactionRecordApiConstants.descriptionParamName, element)) {
            description = this.fromApiJsonHelper.extractStringNamed(TransactionRecordApiConstants.descriptionParamName, element);
            
        }
        //TODO figure out how to work with dates
        if (this.fromApiJsonHelper.parameterExists(TransactionRecordApiConstants.dateParamName, element)) {
            localdate = this.fromApiJsonHelper.extractLocalDateNamed(TransactionRecordApiConstants.dateParamName, element);
            if (localdate != null) {
            		date = localdate.toDate();
            	} else {
            		date = LocalDate.now().toDate();
            	}
            
        }
        
        if (this.fromApiJsonHelper.parameterExists(TransactionRecordApiConstants.typeParamName, element)) {
            type = this.fromApiJsonHelper.extractIntegerNamed(TransactionRecordApiConstants.typeParamName, element, Locale.getDefault());
        }

        if (this.fromApiJsonHelper.parameterExists(TransactionRecordApiConstants.amountParamName, element)) {
            amount = this.fromApiJsonHelper.extractDoubleNamed(TransactionRecordApiConstants.amountParamName, element);
        }
        return TransactionRecord.createNew(serviceAccount, description, date, type, amount);
    }

    public TransactionRecord assembleFromResourceId(final Long resourceId) {
        final TransactionRecord transactionRecord = this.transactionRecordRepository.findOne(resourceId);
        if (transactionRecord == null) { throw new TransactionRecordNotFoundException(resourceId); }
        return transactionRecord;
    }
}