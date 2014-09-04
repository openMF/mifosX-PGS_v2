/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mifosplatform.portfolio.pgs.usagerecord.service;

import java.util.Map;

import org.mifosplatform.infrastructure.core.api.JsonCommand;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResult;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResultBuilder;
import org.mifosplatform.infrastructure.core.exception.PlatformDataIntegrityException;
import org.mifosplatform.portfolio.pgs.usagerecord.data.UsageRecordDataValidator;
import org.mifosplatform.portfolio.pgs.usagerecord.domain.UsageRecord;
import org.mifosplatform.portfolio.pgs.usagerecord.domain.UsageRecordAssembler;
import org.mifosplatform.portfolio.pgs.usagerecord.domain.UsageRecordRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UsageRecordWritePlatformServiceJpaRepositoryImpl implements UsageRecordWritePlatformService {

    private final static Logger logger = LoggerFactory.getLogger(UsageRecordWritePlatformServiceJpaRepositoryImpl.class);

    private final UsageRecordAssembler assembler;
    private final UsageRecordRepository repository;
    private final UsageRecordDataValidator validator;
    
    @Autowired
    public UsageRecordWritePlatformServiceJpaRepositoryImpl(final UsageRecordAssembler assembler, final UsageRecordRepository repository,
            final UsageRecordDataValidator validator) {
        this.assembler = assembler;
        this.repository = repository;
        this.validator = validator;
    }

    @Transactional
    @Override
    public CommandProcessingResult create(final JsonCommand command) {

        try {
            this.validator.validateForCreate(command.json());

            final UsageRecord usageRecord = this.assembler.assembleFromJson(command);
            this.repository.save(usageRecord);

            return new CommandProcessingResultBuilder() //
                    .withCommandId(command.commandId()) //
                    .withEntityId(usageRecord.getId()) //
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

            final UsageRecord usageRecord = this.assembler.assembleFromResourceId(resourceId);
            final Map<String, Object> changes = usageRecord.update(command);
            if (!changes.isEmpty()) {
                this.repository.save(usageRecord);
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
            final UsageRecord usageRecord = this.assembler.assembleFromResourceId(resourceId);
            this.repository.delete(usageRecord);
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
        throw new PlatformDataIntegrityException("error.msg.usage.record.unknown.data.integrity.issue",
                "Unknown data integrity issue with resource: " + realCause.getMessage());
    }
}