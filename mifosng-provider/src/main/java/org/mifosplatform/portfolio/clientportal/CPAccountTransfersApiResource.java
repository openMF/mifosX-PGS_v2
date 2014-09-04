package org.mifosplatform.portfolio.clientportal;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriInfo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.mifosplatform.commands.domain.CommandWrapper;
import org.mifosplatform.commands.service.CommandWrapperBuilder;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResult;
import org.mifosplatform.infrastructure.core.serialization.ApiRequestJsonSerializationSettings;
import org.mifosplatform.infrastructure.core.service.Page;
import org.mifosplatform.infrastructure.security.service.PlatformSecurityContext;
import org.mifosplatform.portfolio.account.api.AccountTransfersApiConstants;
import org.mifosplatform.portfolio.account.api.AccountTransfersApiResource;
import org.mifosplatform.portfolio.account.data.AccountTransferData;
import org.mifosplatform.portfolio.client.api.ClientsApiResource;
import org.mifosplatform.portfolio.group.service.SearchParameters;

@Path("/clientportalaccounttransfers")
@Component
@Scope("singleton")
public class CPAccountTransfersApiResource{

	private final PlatformSecurityContext context;
    private final AccountTransfersApiResource accountTransfersApiResource;
    private final ClientsApiResource clientsApiResource;
    
    @Autowired
    public CPAccountTransfersApiResource(final AccountTransfersApiResource accountTransfersApiResource, final ClientsApiResource clientsApiResource, 
    	final PlatformSecurityContext context){
    	this.accountTransfersApiResource = accountTransfersApiResource;
    	this.clientsApiResource = clientsApiResource;
    	this.context = context;
    }
    
    @GET
    @Path("template")
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public String template(@QueryParam("fromOfficeId") final Long fromOfficeId, @QueryParam("fromClientId") final Long fromClientId,
            @QueryParam("fromAccountId") final Long fromAccountId, @QueryParam("fromAccountType") final Integer fromAccountType,
            @QueryParam("toOfficeId") final Long toOfficeId, @QueryParam("toClientId") final Long toClientId,
            @QueryParam("toAccountId") final Long toAccountId, @QueryParam("toAccountType") final Integer toAccountType,
            @Context final UriInfo uriInfo){
        return accountTransfersApiResource.template(fromOfficeId, fromClientId, fromAccountId, fromAccountType, toOfficeId,
        		toClientId, toAccountId, toAccountType, uriInfo);
    }
    
    @POST
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public String create(final String apiRequestBodyAsJson) {

        return accountTransfersApiResource.create(apiRequestBodyAsJson);
    }
    
    @GET
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public String retrieveAll(@Context final UriInfo uriInfo, @QueryParam("sqlSearch") final String sqlSearch,
            @QueryParam("externalId") final String externalId, @QueryParam("offset") final Integer offset,
            @QueryParam("limit") final Integer limit, @QueryParam("orderBy") final String orderBy,
            @QueryParam("sortOrder") final String sortOrder,@QueryParam("accountDetailId") final Long accountDetailId) {

        return accountTransfersApiResource.retrieveAll(uriInfo, sqlSearch, externalId, offset, limit, orderBy, sortOrder, accountDetailId);
    }

    @GET
    @Path("{transferId}")
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public String retrieveOne(@PathParam("transferId") final Long transferId, @Context final UriInfo uriInfo) {

        return accountTransfersApiResource.retrieveOne(transferId, uriInfo);
    }
}
