package org.mifosplatform.portfolio.clientportal;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriInfo;

import org.mifosplatform.portfolio.loanproduct.api.LoanProductsApiResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Path("/clientportalloanproducts")
@Component
@Scope("singleton")
public class CPLoansApiResource {

    LoanProductsApiResource loanProductsApiResource;

    @Autowired
    public CPLoansApiResource(final LoanProductsApiResource loanProductsApiResource) {
        this.loanProductsApiResource = loanProductsApiResource;
    }

    @GET
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public String retrieveAllLoanProducts(@Context final UriInfo uriInfo) {

        return loanProductsApiResource.retrieveAllLoanProducts(uriInfo);
    }

    @GET
    @Path("template")
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public String retrieveTemplate(@Context final UriInfo uriInfo, @QueryParam("isProductMixTemplate") final boolean isProductMixTemplate) {

        return loanProductsApiResource.retrieveTemplate(uriInfo, isProductMixTemplate);
    }

    @GET
    @Path("{productId}")
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public String retrieveLoanProductDetails(@PathParam("productId") final Long productId, @Context final UriInfo uriInfo) {

        return loanProductsApiResource.retrieveLoanProductDetails(productId, uriInfo);
    }
}
