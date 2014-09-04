/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mifosplatform.portfolio.pgs.pgsclient.service;

import java.util.Map;

import org.mifosplatform.infrastructure.codes.domain.CodeValue;
import org.mifosplatform.infrastructure.codes.domain.CodeValueRepositoryWrapper;
import org.mifosplatform.infrastructure.codes.exception.CodeValueNotFoundException;
import org.mifosplatform.infrastructure.core.api.JsonCommand;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResult;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResultBuilder;
import org.mifosplatform.infrastructure.core.exception.PlatformDataIntegrityException;
import org.mifosplatform.infrastructure.security.service.PlatformSecurityContext;
import org.mifosplatform.portfolio.pgs.pgsclient.command.PGSClientIdentifierCommand;
import org.mifosplatform.portfolio.pgs.pgsclient.domain.PGSClient;
import org.mifosplatform.portfolio.pgs.pgsclient.domain.PGSClientIdentifier;
import org.mifosplatform.portfolio.pgs.pgsclient.domain.PGSClientIdentifierRepository;
import org.mifosplatform.portfolio.pgs.pgsclient.domain.PGSClientRepositoryWrapper;
import org.mifosplatform.portfolio.pgs.pgsclient.exception.ClientIdentifierNotFoundException;
import org.mifosplatform.portfolio.pgs.pgsclient.exception.DuplicateClientIdentifierException;
import org.mifosplatform.portfolio.pgs.pgsclient.serialization.PGSClientIdentifierCommandFromApiJsonDeserializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PGSClientIdentifierWritePlatformServiceJpaRepositoryImpl implements PGSClientIdentifierWritePlatformService {

    private final static Logger logger = LoggerFactory.getLogger(PGSClientIdentifierWritePlatformServiceJpaRepositoryImpl.class);

    private final PlatformSecurityContext context;
    private final PGSClientRepositoryWrapper clientRepository;
    private final PGSClientIdentifierRepository clientIdentifierRepository;
    private final CodeValueRepositoryWrapper codeValueRepository;
    private final PGSClientIdentifierCommandFromApiJsonDeserializer clientIdentifierCommandFromApiJsonDeserializer;

    @Autowired
    public PGSClientIdentifierWritePlatformServiceJpaRepositoryImpl(final PlatformSecurityContext context,
            final PGSClientRepositoryWrapper clientRepository, final PGSClientIdentifierRepository clientIdentifierRepository,
            final CodeValueRepositoryWrapper codeValueRepository,
            final PGSClientIdentifierCommandFromApiJsonDeserializer clientIdentifierCommandFromApiJsonDeserializer) {
        this.context = context;
        this.clientRepository = clientRepository;
        this.clientIdentifierRepository = clientIdentifierRepository;
        this.codeValueRepository = codeValueRepository;
        this.clientIdentifierCommandFromApiJsonDeserializer = clientIdentifierCommandFromApiJsonDeserializer;
    }

    @Transactional
    @Override
    public CommandProcessingResult addClientIdentifier(final Long clientId, final JsonCommand command) {

        this.context.authenticatedUser();
        final PGSClientIdentifierCommand clientIdentifierCommand = this.clientIdentifierCommandFromApiJsonDeserializer
                .commandFromApiJson(command.json());
        clientIdentifierCommand.validateForCreate();

        final String documentKey = clientIdentifierCommand.getDocumentKey();
        String documentTypeLabel = null;
        Long documentTypeId = null;
        try {
            final PGSClient client = this.clientRepository.findOneWithNotFoundDetection(clientId);

            final CodeValue documentType = this.codeValueRepository.findOneWithNotFoundDetection(clientIdentifierCommand
                    .getDocumentTypeId());
            documentTypeId = documentType.getId();
            documentTypeLabel = documentType.label();

            final PGSClientIdentifier clientIdentifier = PGSClientIdentifier.fromJson(client, documentType, command);

            this.clientIdentifierRepository.save(clientIdentifier);

            return new CommandProcessingResultBuilder() //
                    .withCommandId(command.commandId()) //
                    .withOfficeId(client.officeId()) //
                    .withClientId(clientId) //
                    .withEntityId(clientIdentifier.getId()) //
                    .build();
        } catch (final DataIntegrityViolationException dve) {
            handleClientIdentifierDataIntegrityViolation(documentTypeLabel, documentTypeId, documentKey, dve);
            return CommandProcessingResult.empty();
        }
    }

    @Transactional
    @Override
    public CommandProcessingResult updateClientIdentifier(final Long clientId, final Long identifierId, final JsonCommand command) {

        this.context.authenticatedUser();
        final PGSClientIdentifierCommand clientIdentifierCommand = this.clientIdentifierCommandFromApiJsonDeserializer
                .commandFromApiJson(command.json());
        clientIdentifierCommand.validateForUpdate();

        String documentTypeLabel = null;
        String documentKey = null;
        Long documentTypeId = clientIdentifierCommand.getDocumentTypeId();
        try {
            CodeValue documentType = null;

            final PGSClient client = this.clientRepository.findOneWithNotFoundDetection(clientId);
            final PGSClientIdentifier clientIdentifierForUpdate = this.clientIdentifierRepository.findOne(identifierId);
            if (clientIdentifierForUpdate == null) { throw new ClientIdentifierNotFoundException(identifierId); }

            final Map<String, Object> changes = clientIdentifierForUpdate.update(command);

            if (changes.containsKey("documentTypeId")) {
                documentType = this.codeValueRepository.findOneWithNotFoundDetection(documentTypeId);
                if (documentType == null) { throw new CodeValueNotFoundException(documentTypeId); }

                documentTypeId = documentType.getId();
                documentTypeLabel = documentType.label();
                clientIdentifierForUpdate.update(documentType);
            }

            if (changes.containsKey("documentTypeId") && changes.containsKey("documentKey")) {
                documentTypeId = clientIdentifierCommand.getDocumentTypeId();
                documentKey = clientIdentifierCommand.getDocumentKey();
            } else if (changes.containsKey("documentTypeId") && !changes.containsKey("documentKey")) {
                documentTypeId = clientIdentifierCommand.getDocumentTypeId();
                documentKey = clientIdentifierForUpdate.documentKey();
            } else if (!changes.containsKey("documentTypeId") && changes.containsKey("documentKey")) {
                documentTypeId = clientIdentifierForUpdate.documentTypeId();
                documentKey = clientIdentifierForUpdate.documentKey();
            }

            if (!changes.isEmpty()) {
                this.clientIdentifierRepository.saveAndFlush(clientIdentifierForUpdate);
            }

            return new CommandProcessingResultBuilder() //
                    .withCommandId(command.commandId()) //
                    .withOfficeId(client.officeId()) //
                    .withClientId(clientId) //
                    .withEntityId(identifierId) //
                    .with(changes) //
                    .build();
        } catch (final DataIntegrityViolationException dve) {
            handleClientIdentifierDataIntegrityViolation(documentTypeLabel, documentTypeId, documentKey, dve);
            return new CommandProcessingResult(Long.valueOf(-1));
        }
    }

    @Transactional
    @Override
    public CommandProcessingResult deleteClientIdentifier(final Long clientId, final Long identifierId, final Long commandId) {

        final PGSClient client = this.clientRepository.findOneWithNotFoundDetection(clientId);

        final PGSClientIdentifier clientIdentifier = this.clientIdentifierRepository.findOne(identifierId);
        if (clientIdentifier == null) { throw new ClientIdentifierNotFoundException(identifierId); }
        this.clientIdentifierRepository.delete(clientIdentifier);

        return new CommandProcessingResultBuilder() //
                .withCommandId(commandId) //
                .withOfficeId(client.officeId()) //
                .withClientId(clientId) //
                .withEntityId(identifierId) //
                .build();
    }

    private void handleClientIdentifierDataIntegrityViolation(final String documentTypeLabel, final Long documentTypeId,
            final String documentKey, final DataIntegrityViolationException dve) {

        if (dve.getMostSpecificCause().getMessage().contains("unique_client_identifier")) {
            throw new DuplicateClientIdentifierException(documentTypeLabel);
        } else if (dve.getMostSpecificCause().getMessage().contains("unique_identifier_key")) { throw new DuplicateClientIdentifierException(
                documentTypeId, documentTypeLabel, documentKey); }

        logAsErrorUnexpectedDataIntegrityException(dve);
        throw new PlatformDataIntegrityException("error.msg.clientIdentifier.unknown.data.integrity.issue",
                "Unknown data integrity issue with resource.");
    }

    private void logAsErrorUnexpectedDataIntegrityException(final DataIntegrityViolationException dve) {
        logger.error(dve.getMessage(), dve);
    }
}