/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mifosplatform.portfolio.pgs.pgsclient.api;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriInfo;

import org.mifosplatform.commands.domain.CommandWrapper;
import org.mifosplatform.commands.service.CommandWrapperBuilder;
import org.mifosplatform.commands.service.PortfolioCommandSourceWritePlatformService;
import org.mifosplatform.infrastructure.codes.data.CodeValueData;
import org.mifosplatform.infrastructure.codes.service.CodeValueReadPlatformService;
import org.mifosplatform.infrastructure.core.api.ApiRequestParameterHelper;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResult;
import org.mifosplatform.infrastructure.core.serialization.ApiRequestJsonSerializationSettings;
import org.mifosplatform.infrastructure.core.serialization.DefaultToApiJsonSerializer;
import org.mifosplatform.infrastructure.security.service.PlatformSecurityContext;
import org.mifosplatform.portfolio.pgs.pgsclient.data.PGSClientData;
import org.mifosplatform.portfolio.pgs.pgsclient.data.PGSClientIdentifierData;
import org.mifosplatform.portfolio.pgs.pgsclient.exception.DuplicateClientIdentifierException;
import org.mifosplatform.portfolio.pgs.pgsclient.service.PGSClientIdentifierReadPlatformService;
import org.mifosplatform.portfolio.pgs.pgsclient.service.PGSClientReadPlatformService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Path("/pgsclients/{clientId}/identifiers")
@Component
@Scope("singleton")
public class PGSClientIdentifiersApiResource {

    private static final Set<String> PGSCLIENT_IDENTIFIER_DATA_PARAMETERS = new HashSet<String>(Arrays.asList("id", "clientId",
            "documentType", "documentKey", "description", "allowedDocumentTypes"));

    private final String resourceNameForPermissions = "PGSCLIENTIDENTIFIER";

    private final PlatformSecurityContext context;
    private final PGSClientReadPlatformService clientReadPlatformService;
    private final PGSClientIdentifierReadPlatformService clientIdentifierReadPlatformService;
    private final CodeValueReadPlatformService codeValueReadPlatformService;
    private final DefaultToApiJsonSerializer<PGSClientIdentifierData> toApiJsonSerializer;
    private final ApiRequestParameterHelper apiRequestParameterHelper;
    private final PortfolioCommandSourceWritePlatformService commandsSourceWritePlatformService;

    @Autowired
    public PGSClientIdentifiersApiResource(final PlatformSecurityContext context, final PGSClientReadPlatformService readPlatformService,
            final CodeValueReadPlatformService codeValueReadPlatformService,
            final DefaultToApiJsonSerializer<PGSClientIdentifierData> toApiJsonSerializer,
            final ApiRequestParameterHelper apiRequestParameterHelper,
            final PortfolioCommandSourceWritePlatformService commandsSourceWritePlatformService,
            final PGSClientIdentifierReadPlatformService clientIdentifierReadPlatformService) {
        this.context = context;
        this.clientReadPlatformService = readPlatformService;
        this.codeValueReadPlatformService = codeValueReadPlatformService;
        this.toApiJsonSerializer = toApiJsonSerializer;
        this.apiRequestParameterHelper = apiRequestParameterHelper;
        this.commandsSourceWritePlatformService = commandsSourceWritePlatformService;
        this.clientIdentifierReadPlatformService = clientIdentifierReadPlatformService;
    }

    @GET
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public String retrieveAllClientIdentifiers(@Context final UriInfo uriInfo, @PathParam("clientId") final Long clientId) {

        this.context.authenticatedUser().validateHasReadPermission(this.resourceNameForPermissions);

        final Collection<PGSClientIdentifierData> clientIdentifiers = this.clientIdentifierReadPlatformService
                .retrieveClientIdentifiers(clientId);

        final ApiRequestJsonSerializationSettings settings = this.apiRequestParameterHelper.process(uriInfo.getQueryParameters());
        return this.toApiJsonSerializer.serialize(settings, clientIdentifiers, PGSCLIENT_IDENTIFIER_DATA_PARAMETERS);
    }

    @GET
    @Path("template")
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public String newClientIdentifierDetails(@Context final UriInfo uriInfo) {

        this.context.authenticatedUser().validateHasReadPermission(this.resourceNameForPermissions);

        final Collection<CodeValueData> codeValues = this.codeValueReadPlatformService.retrieveCodeValuesByCode("Customer Identifier");
        final PGSClientIdentifierData clientIdentifierData = PGSClientIdentifierData.template(codeValues);

        final ApiRequestJsonSerializationSettings settings = this.apiRequestParameterHelper.process(uriInfo.getQueryParameters());
        return this.toApiJsonSerializer.serialize(settings, clientIdentifierData, PGSCLIENT_IDENTIFIER_DATA_PARAMETERS);
    }

    @POST
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public String createClientIdentifier(@PathParam("clientId") final Long clientId, final String apiRequestBodyAsJson) {

        try {
            final CommandWrapper commandRequest = new CommandWrapperBuilder().createClientIdentifier(clientId)
                    .withJson(apiRequestBodyAsJson).build();

            final CommandProcessingResult result = this.commandsSourceWritePlatformService.logCommandSource(commandRequest);

            return this.toApiJsonSerializer.serialize(result);
        } catch (final DuplicateClientIdentifierException e) {
            DuplicateClientIdentifierException rethrowas = e;
            if (e.getDocumentTypeId() != null) {
                // need to fetch client info
                final PGSClientData clientInfo = this.clientReadPlatformService.retrieveClientByIdentifier(e.getDocumentTypeId(),
                        e.getIdentifierKey());
                rethrowas = new DuplicateClientIdentifierException(clientInfo.displayName(), clientInfo.officeName(),
                        e.getIdentifierType(), e.getIdentifierKey());
            }
            throw rethrowas;
        }
    }

    @GET
    @Path("{identifierId}")
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public String retrieveClientIdentifiers(@PathParam("clientId") final Long clientId,
            @PathParam("identifierId") final Long clientIdentifierId, @Context final UriInfo uriInfo) {

        this.context.authenticatedUser().validateHasReadPermission(this.resourceNameForPermissions);

        final ApiRequestJsonSerializationSettings settings = this.apiRequestParameterHelper.process(uriInfo.getQueryParameters());

        PGSClientIdentifierData clientIdentifierData = this.clientIdentifierReadPlatformService.retrieveClientIdentifier(clientId,
                clientIdentifierId);
        if (settings.isTemplate()) {
            final Collection<CodeValueData> codeValues = this.codeValueReadPlatformService.retrieveCodeValuesByCode("Customer Identifier");
            clientIdentifierData = PGSClientIdentifierData.template(clientIdentifierData, codeValues);
        }

        return this.toApiJsonSerializer.serialize(settings, clientIdentifierData, PGSCLIENT_IDENTIFIER_DATA_PARAMETERS);
    }

    @PUT
    @Path("{identifierId}")
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public String updateClientIdentifer(@PathParam("clientId") final Long clientId,
            @PathParam("identifierId") final Long clientIdentifierId, final String apiRequestBodyAsJson) {

        try {
            final CommandWrapper commandRequest = new CommandWrapperBuilder().updateClientIdentifier(clientId, clientIdentifierId)
                    .withJson(apiRequestBodyAsJson).build();

            final CommandProcessingResult result = this.commandsSourceWritePlatformService.logCommandSource(commandRequest);

            return this.toApiJsonSerializer.serialize(result);
        } catch (final DuplicateClientIdentifierException e) {
            DuplicateClientIdentifierException reThrowAs = e;
            if (e.getDocumentTypeId() != null) {
                final PGSClientData clientInfo = this.clientReadPlatformService.retrieveClientByIdentifier(e.getDocumentTypeId(),
                        e.getIdentifierKey());
                reThrowAs = new DuplicateClientIdentifierException(clientInfo.displayName(), clientInfo.officeName(),
                        e.getIdentifierType(), e.getIdentifierKey());
            }
            throw reThrowAs;
        }
    }

    @DELETE
    @Path("{identifierId}")
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public String deleteClientIdentifier(@PathParam("clientId") final Long clientId,
            @PathParam("identifierId") final Long clientIdentifierId) {

        final CommandWrapper commandRequest = new CommandWrapperBuilder().deleteClientIdentifier(clientId, clientIdentifierId).build();

        final CommandProcessingResult result = this.commandsSourceWritePlatformService.logCommandSource(commandRequest);

        return this.toApiJsonSerializer.serialize(result);
    }
}