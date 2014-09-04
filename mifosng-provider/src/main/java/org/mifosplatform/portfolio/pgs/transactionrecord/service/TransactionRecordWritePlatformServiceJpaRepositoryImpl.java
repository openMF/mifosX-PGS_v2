/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mifosplatform.portfolio.pgs.transactionrecord.service;

import java.util.Map;

import org.mifosplatform.infrastructure.core.api.JsonCommand;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResult;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResultBuilder;
import org.mifosplatform.infrastructure.core.exception.PlatformDataIntegrityException;
import org.mifosplatform.portfolio.pgs.transactionrecord.data.TransactionRecordDataValidator;
import org.mifosplatform.portfolio.pgs.transactionrecord.domain.TransactionRecord;
import org.mifosplatform.portfolio.pgs.transactionrecord.domain.TransactionRecordAssembler;
import org.mifosplatform.portfolio.pgs.transactionrecord.domain.TransactionRecordRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class TransactionRecordWritePlatformServiceJpaRepositoryImpl implements TransactionRecordWritePlatformService {

    private final static Logger logger = LoggerFactory.getLogger(TransactionRecordWritePlatformServiceJpaRepositoryImpl.class);

    private final TransactionRecordAssembler assembler;
    private final TransactionRecordRepository repository;
    private final TransactionRecordDataValidator validator;
    
    @Autowired
    public TransactionRecordWritePlatformServiceJpaRepositoryImpl(final TransactionRecordAssembler assembler, 
    		final TransactionRecordRepository repository,
            final TransactionRecordDataValidator validator) {
        this.repository = repository;
        this.validator = validator;
        this.assembler = assembler;
    }

    @Transactional
    @Override
    public CommandProcessingResult create(final JsonCommand command) {

        try {
            this.validator.validateForCreate(command.json());

            final TransactionRecord transactionRecord = this.assembler.assembleFromJson(command);
            this.repository.save(transactionRecord);

            return new CommandProcessingResultBuilder() //
                    .withCommandId(command.commandId()) //
                    .withEntityId(transactionRecord.getId()) //
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

            final TransactionRecord transactionRecord = this.assembler.assembleFromResourceId(resourceId);
            final Map<String, Object> changes = transactionRecord.update(command);
            if (!changes.isEmpty()) {
                this.repository.save(transactionRecord);
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
            final TransactionRecord transactionRecord = this.assembler.assembleFromResourceId(resourceId);
            this.repository.delete(transactionRecord);
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
        throw new PlatformDataIntegrityException("error.msg.transaction.record.unknown.data.integrity.issue",
                "Unknown data integrity issue with resource: " + realCause.getMessage());
    }
}