/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mifosplatform.portfolio.pgs.serviceaccountstatushistory.api;

import java.util.Collection;

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
import org.mifosplatform.infrastructure.core.api.ApiRequestParameterHelper;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResult;
import org.mifosplatform.infrastructure.core.serialization.ApiRequestJsonSerializationSettings;
import org.mifosplatform.infrastructure.core.serialization.DefaultToApiJsonSerializer;
import org.mifosplatform.infrastructure.security.service.PlatformSecurityContext;
import org.mifosplatform.portfolio.pgs.serviceaccountstatushistory.data.ServiceAccountStatusHistoryData;
import org.mifosplatform.portfolio.pgs.serviceaccountstatushistory.service.ServiceAccountStatusHistoryReadPlatformService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Path("/serviceaccount/{serviceAccountId}/serviceaccountstatushistory")
@Consumes({ MediaType.APPLICATION_JSON })
@Produces({ MediaType.APPLICATION_JSON })
@Component
@Scope("singleton")
public class ServiceAccountStatusHistoryApiResource {

    private final String resourceNameForPermissions = "SERVICEACCOUNTSTATUSHISTORY";

    private final PlatformSecurityContext context;
    private final ServiceAccountStatusHistoryReadPlatformService readPlatformService;
    private final DefaultToApiJsonSerializer<ServiceAccountStatusHistoryData> toApiJsonSerializer;
    private final ApiRequestParameterHelper apiRequestParameterHelper;
    private final PortfolioCommandSourceWritePlatformService commandsSourceWritePlatformService;

    @Autowired
    public ServiceAccountStatusHistoryApiResource(final PlatformSecurityContext context, final ServiceAccountStatusHistoryReadPlatformService readPlatformService,
            final DefaultToApiJsonSerializer<ServiceAccountStatusHistoryData> toApiJsonSerializer, final ApiRequestParameterHelper apiRequestParameterHelper,
            final PortfolioCommandSourceWritePlatformService commandsSourceWritePlatformService) {
        this.context = context;
        this.readPlatformService = readPlatformService;
        this.toApiJsonSerializer = toApiJsonSerializer;
        this.apiRequestParameterHelper = apiRequestParameterHelper;
        this.commandsSourceWritePlatformService = commandsSourceWritePlatformService;
    }

    @GET
    public String retrieveAll(@Context final UriInfo uriInfo, @PathParam("serviceAccountId") final Long serviceAccountId) {

        this.context.authenticatedUser().validateHasReadPermission(this.resourceNameForPermissions);

        final Collection<ServiceAccountStatusHistoryData> serviceAccountStatusHistory = this.readPlatformService.retrieveAll(serviceAccountId);

        final ApiRequestJsonSerializationSettings settings = this.apiRequestParameterHelper.process(uriInfo.getQueryParameters());
        return this.toApiJsonSerializer.serialize(settings, serviceAccountStatusHistory);
    }

    @POST
    public String create(final String apiRequestBodyAsJson, @PathParam("serviceAccountId") final Long serviceAccountId) {

        final CommandWrapper commandRequest = new CommandWrapperBuilder().createServiceAccountStatusHistory(serviceAccountId).withJson(apiRequestBodyAsJson).build();

        final CommandProcessingResult result = this.commandsSourceWritePlatformService.logCommandSource(commandRequest);

        return this.toApiJsonSerializer.serialize(result);
    }

    @GET
    @Path("{resourceId}")
    public String retrieveOne(@PathParam("resourceId") final Long resourceId, @Context final UriInfo uriInfo, 
    		@PathParam("serviceAccountId") final Long serviceAccountId) {

        final ServiceAccountStatusHistoryData serviceAccountStatusHistory = this.readPlatformService.retrieveOne(serviceAccountId, resourceId);

        final ApiRequestJsonSerializationSettings settings = this.apiRequestParameterHelper.process(uriInfo.getQueryParameters());
        return this.toApiJsonSerializer.serialize(settings, serviceAccountStatusHistory);
    }

    @PUT
    @Path("{resourceId}")
    public String update(@PathParam("resourceId") final Long resourceId, final String apiRequestBodyAsJson, 
    		@PathParam("serviceAccountId") final Long serviceAccountId) {

        final CommandWrapper commandRequest = new CommandWrapperBuilder().updateServiceAccountStatusHistory(serviceAccountId, resourceId).withJson(apiRequestBodyAsJson).build();

        final CommandProcessingResult result = this.commandsSourceWritePlatformService.logCommandSource(commandRequest);

        return this.toApiJsonSerializer.serialize(result);
    }

    @DELETE
    @Path("{resourceId}")
    public String delete(@PathParam("resourceId") final Long resourceId, @PathParam("serviceAccountId") final Long serviceAccountId) {

        final CommandWrapper commandRequest = new CommandWrapperBuilder().deleteServiceAccountStatusHistory(serviceAccountId, resourceId).build();

        final CommandProcessingResult result = this.commandsSourceWritePlatformService.logCommandSource(commandRequest);

        return this.toApiJsonSerializer.serialize(result);
    }
}