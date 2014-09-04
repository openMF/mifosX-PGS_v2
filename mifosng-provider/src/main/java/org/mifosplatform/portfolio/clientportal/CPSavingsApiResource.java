package org.mifosplatform.portfolio.clientportal;

import javax.ws.rs.Consumes;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriInfo;

import org.mifosplatform.portfolio.savings.api.SavingsAccountsApiResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Path("/clientportalsavingsaccounts")
@Component
@Scope("singleton")
public class CPSavingsApiResource {

    private final SavingsAccountsApiResource savingsAccountsApiResource;

    @Autowired
    public CPSavingsApiResource(final SavingsAccountsApiResource savingsAccountsApiResource) {
        this.savingsAccountsApiResource = savingsAccountsApiResource;
    }
    
    @GET
    @Path("template")
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public String template(@QueryParam("clientId") final Long clientId, @QueryParam("groupId") final Long groupId,
            @QueryParam("productId") final Long productId,
            @DefaultValue("false") @QueryParam("staffInSelectedOfficeOnly") final boolean staffInSelectedOfficeOnly,
            @Context final UriInfo uriInfo) {

        return savingsAccountsApiResource.template(clientId, groupId, productId, staffInSelectedOfficeOnly, uriInfo);
    }

    @GET
    @Path("{accountId}")
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public String retrieveOne(@PathParam("accountId") final Long accountId,
            @DefaultValue("false") @QueryParam("staffInSelectedOfficeOnly") final boolean staffInSelectedOfficeOnly,
            @Context final UriInfo uriInfo) {

        return savingsAccountsApiResource.retrieveOne(accountId, staffInSelectedOfficeOnly, uriInfo);
    
    }
    
    @GET
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public String retrieveAll(@Context final UriInfo uriInfo, @QueryParam("sqlSearch") final String sqlSearch,
            @QueryParam("externalId") final String externalId,
            // @QueryParam("underHierarchy") final String hierarchy,
            @QueryParam("offset") final Integer offset, @QueryParam("limit") final Integer limit,
            @QueryParam("orderBy") final String orderBy, @QueryParam("sortOrder") final String sortOrder) {

        return savingsAccountsApiResource.retrieveAll(uriInfo, sqlSearch, externalId, offset, limit, orderBy, sortOrder);
    }
}
