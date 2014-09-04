/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mifosplatform.portfolio.pgs.serviceaccount.service;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Map;

import org.mifosplatform.infrastructure.core.api.JsonCommand;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResult;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResultBuilder;
import org.mifosplatform.infrastructure.core.exception.PlatformDataIntegrityException;
import org.mifosplatform.portfolio.pgs.currentaccountinformation.domain.CurrentAccountInformation;
import org.mifosplatform.portfolio.pgs.currentaccountinformation.domain.CurrentAccountInformationRepository;
import org.mifosplatform.portfolio.pgs.currentaccountinformation.exception.CurrentAccountInformationNotFoundException;
import org.mifosplatform.portfolio.pgs.pgsclient.domain.PGSClient;
import org.mifosplatform.portfolio.pgs.pgsclient.domain.PGSClientRepository;
import org.mifosplatform.portfolio.pgs.pgsclient.exception.ClientNotFoundException;
import org.mifosplatform.portfolio.pgs.serviceaccount.api.ServiceAccountApiConstants;
import org.mifosplatform.portfolio.pgs.serviceaccount.data.ServiceAccountDataValidator;
import org.mifosplatform.portfolio.pgs.serviceaccount.domain.ServiceAccount;
import org.mifosplatform.portfolio.pgs.serviceaccount.domain.ServiceAccountRepository;
import org.mifosplatform.portfolio.pgs.serviceaccountstatushistory.domain.ServiceAccountStatusHistory;
import org.mifosplatform.portfolio.pgs.serviceaccountstatushistory.domain.ServiceAccountStatusHistoryRepository;
import org.mifosplatform.portfolio.pgs.serviceoffering.domain.ServiceOffering;
import org.mifosplatform.portfolio.pgs.serviceoffering.domain.ServiceOfferingRepository;
import org.mifosplatform.portfolio.pgs.serviceoffering.exception.ServiceOfferingNotFoundException;
import org.mifosplatform.portfolio.pgs.transactionrecord.domain.TransactionRecord;
import org.mifosplatform.portfolio.pgs.transactionrecord.domain.TransactionRecordRepository;
import org.mifosplatform.portfolio.pgs.usagerecord.domain.UsageRecord;
import org.mifosplatform.portfolio.pgs.usagerecord.domain.UsageRecordRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ServiceAccountWritePlatformServiceJpaRepositoryImpl implements ServiceAccountWritePlatformService {

    private final static Logger logger = LoggerFactory.getLogger(ServiceAccountWritePlatformServiceJpaRepositoryImpl.class);

    private final ServiceAccountRepository repository;
    private final PGSClientRepository pgsClientRepository;
    private final ServiceAccountDataValidator fromApiJsonDeserializer;
    private final CurrentAccountInformationRepository currentAccountInformationRepository;
    private final UsageRecordRepository usageRecordRepository;
    private final TransactionRecordRepository transactionRecordRepository;
    private final ServiceAccountStatusHistoryRepository serviceAccountStatusHistoryRepository;
    private final ServiceOfferingRepository serviceOfferingRepository;
    
    @Autowired
    public ServiceAccountWritePlatformServiceJpaRepositoryImpl(final ServiceAccountRepository repository,
    		final PGSClientRepository pgsClientRepository,
    		final ServiceAccountDataValidator fromApiJsonDeserializer, 
    		final CurrentAccountInformationRepository currentAccountInformationRepository, 
    		final UsageRecordRepository usageRecordRepository,
    		final TransactionRecordRepository transactionRecordRepository, 
    		final ServiceAccountStatusHistoryRepository serviceAccountStatusHistoryRepository, 
    		final ServiceOfferingRepository serviceOfferingRepository)
    		{
        this.repository = repository;
        this.pgsClientRepository = pgsClientRepository;
        this.fromApiJsonDeserializer = fromApiJsonDeserializer;
        this.currentAccountInformationRepository = currentAccountInformationRepository;
        this.usageRecordRepository = usageRecordRepository;
        this.transactionRecordRepository = transactionRecordRepository;
        this.serviceAccountStatusHistoryRepository = serviceAccountStatusHistoryRepository;
        this.serviceOfferingRepository = serviceOfferingRepository;
    }

    @Transactional
    @Override
    public CommandProcessingResult createServiceAccount(final JsonCommand command) {

        try {
            this.fromApiJsonDeserializer.validateForCreate(command.json());
            
            final Long pgsClientId = command.longValueOfParameterNamed(ServiceAccountApiConstants.clientIdParamName);
            final PGSClient pgsClient = this.pgsClientRepository.findOne(pgsClientId);
            if (pgsClient == null) { throw new ClientNotFoundException(pgsClientId); }
            
            final Long currentAccountInformationId = command.longValueOfParameterNamed(ServiceAccountApiConstants.currentAccountInformationParamName);
            final CurrentAccountInformation currentAccountInformation = this.currentAccountInformationRepository.findOne(currentAccountInformationId);
            if (currentAccountInformation == null) {throw new CurrentAccountInformationNotFoundException(currentAccountInformationId);}
            
            final Long usageRecordId = command.longValueOfParameterNamed(ServiceAccountApiConstants.usageRecordsParamName);
        	final LinkedList<UsageRecord> usageRecords = new LinkedList<UsageRecord>();
            if (usageRecordId != null){ 
            	usageRecords.add(this.usageRecordRepository.findOne(usageRecordId));
            }
            
            final Long transactionRecordId = command.longValueOfParameterNamed(ServiceAccountApiConstants.transactionRecordsParamName);
            final ArrayList<TransactionRecord> transactionRecords = new ArrayList<TransactionRecord>();
            if (transactionRecordId != null){
            	transactionRecords.add(this.transactionRecordRepository.findOne(transactionRecordId));
            }
            
            final Long serviceAccountStatusHistoryId = command.longValueOfParameterNamed(ServiceAccountApiConstants.serviceAccountStatusHistoriesParamName);
            final ArrayList<ServiceAccountStatusHistory> serviceAccountStatusHistory = new ArrayList<ServiceAccountStatusHistory>(); 
            if (serviceAccountStatusHistoryId != null){
            	serviceAccountStatusHistory.add(this.serviceAccountStatusHistoryRepository.findOne(serviceAccountStatusHistoryId));
            }
            
            final Long serviceOfferingId = command.longValueOfParameterNamed(ServiceAccountApiConstants.serviceOfferingParamName);
            final ServiceOffering serviceOffering = this.serviceOfferingRepository.findOne(serviceOfferingId);
            if (serviceOffering == null) { throw new ServiceOfferingNotFoundException(serviceOfferingId); }
            
            final ServiceAccount newServiceAccount = ServiceAccount
            		.createNew(pgsClient, currentAccountInformation, usageRecords, transactionRecords, 
            				serviceAccountStatusHistory, serviceOffering, command);
            this.repository.save(newServiceAccount);

            return new CommandProcessingResultBuilder() //
                    .withCommandId(command.commandId()) //
                    .withEntityId(newServiceAccount.getId()) //
                    .build();
        } catch (final DataIntegrityViolationException dve) {
            handleDataIntegrityIssues(command, dve);
            return CommandProcessingResult.empty();
        }
    }

    @Transactional
    @Override
    public CommandProcessingResult updateServiceAccount(final Long serviceAccountId, final JsonCommand command) {

        try {
        	this.fromApiJsonDeserializer.validateForUpdate(command.json());
        	
            final ServiceAccount serviceAccountForUpdate = this.repository.findOne(serviceAccountId);
            final Map<String, Object> changes = serviceAccountForUpdate.update(command);
            if (!changes.isEmpty()) {
                this.repository.save(serviceAccountForUpdate);
            }

            return new CommandProcessingResultBuilder() //
                    .withCommandId(command.commandId()) //
                    .withEntityId(serviceAccountId) //
                    .with(changes) //
                    .build();
        } catch (final DataIntegrityViolationException dve) {
            handleDataIntegrityIssues(command, dve);
            return CommandProcessingResult.empty();
        }
    }

    @Transactional
    @Override
    public CommandProcessingResult delete(final Long resourceId) {

        try {
            final ServiceAccount serviceAccount = this.repository.findOne(resourceId);
            this.repository.delete(serviceAccount);
            this.repository.flush();
        } catch (final DataIntegrityViolationException dve) {
            handleDataIntegrityIssues(null, dve);
            return CommandProcessingResult.empty();
        }
        return new CommandProcessingResultBuilder().withEntityId(resourceId).build();
    }

    /*
     * Guaranteed to throw an exception no matter what the data integrity issue
     * is.
     */
    private void handleDataIntegrityIssues(@SuppressWarnings("unused") final JsonCommand command, final DataIntegrityViolationException dve) {
        final Throwable realCause = dve.getMostSpecificCause();

        logger.error(dve.getMessage(), dve);
        throw new PlatformDataIntegrityException("error.msg.service.account.unknown.data.integrity.issue",
                "Unknown data integrity issue with resource: " + realCause.getMessage());
    }
}