/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mifosplatform.portfolio.pgs.serviceaccountstatushistory.service;

import java.util.Map;

import org.mifosplatform.infrastructure.core.api.JsonCommand;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResult;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResultBuilder;
import org.mifosplatform.infrastructure.core.exception.PlatformDataIntegrityException;
import org.mifosplatform.portfolio.pgs.serviceaccountstatushistory.data.ServiceAccountStatusHistoryDataValidator;
import org.mifosplatform.portfolio.pgs.serviceaccountstatushistory.domain.ServiceAccountStatusHistory;
import org.mifosplatform.portfolio.pgs.serviceaccountstatushistory.domain.ServiceAccountStatusHistoryAssembler;
import org.mifosplatform.portfolio.pgs.serviceaccountstatushistory.domain.ServiceAccountStatusHistoryRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ServiceAccountStatusHistoryWritePlatformServiceJpaRepositoryImpl implements ServiceAccountStatusHistoryWritePlatformService {

    private final static Logger logger = LoggerFactory.getLogger(ServiceAccountStatusHistoryWritePlatformServiceJpaRepositoryImpl.class);

    private final ServiceAccountStatusHistoryAssembler assembler;
    private final ServiceAccountStatusHistoryRepository repository;
    private final ServiceAccountStatusHistoryDataValidator validator;
    
    @Autowired
    public ServiceAccountStatusHistoryWritePlatformServiceJpaRepositoryImpl(final ServiceAccountStatusHistoryAssembler assembler, final ServiceAccountStatusHistoryRepository repository,
            final ServiceAccountStatusHistoryDataValidator validator) {
        this.assembler = assembler;
        this.repository = repository;
        this.validator = validator;
    }

    @Transactional
    @Override
    public CommandProcessingResult create(final JsonCommand command) {

        try {
            this.validator.validateForCreate(command.json());

            final ServiceAccountStatusHistory serviceOffering = this.assembler.assembleFromJson(command);
            this.repository.save(serviceOffering);

            return new CommandProcessingResultBuilder() //
                    .withCommandId(command.commandId()) //
                    .withEntityId(serviceOffering.getId()) //
                    .build();
        } catch (final DataIntegrityViolationException dve) {
            handleDataIntegrityIssues(command, dve);
            return CommandProcessingResult.empty();
        }
    }

    @Transactional
    @Override
    public CommandProcessingResult update(final Long resourceId, final JsonCommand command) {

        try {
            this.validator.validateForUpdate(command.json());

            final ServiceAccountStatusHistory serviceOffering = this.assembler.assembleFromResourceId(resourceId);
            final Map<String, Object> changes = serviceOffering.update(command);
            if (!changes.isEmpty()) {
                this.repository.save(serviceOffering);
            }

            return new CommandProcessingResultBuilder() //
                    .withCommandId(command.commandId()) //
                    .withEntityId(resourceId) //
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
            final ServiceAccountStatusHistory serviceOffering = this.assembler.assembleFromResourceId(resourceId);
            this.repository.delete(serviceOffering);
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
        throw new PlatformDataIntegrityException("error.msg.service.account.status.history.unknown.data.integrity.issue",
                "Unknown data integrity issue with resource: " + realCause.getMessage());
    }
}