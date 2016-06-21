/////////////////////////////////////////////////////////////////////////////
// Name:        OrderService.java
// Encoding:	UTF-8
//
// Purpose:     A RESTful service for managing shop customers.
//              All data serialization is in JSON format.
//
// Author:      Erik Welander (mail@erikwelander.se)
// Modified:    2016-06-21
// Copyright:   Erik Welander
// Licence:     Creative Commons "by-nc-nd"
/////////////////////////////////////////////////////////////////////////////
package se.erikwelander.ecommerce.webservice;

import se.erikwelander.ecommerce.exception.RepositoryException;
import se.erikwelander.ecommerce.model.Customer;
import se.erikwelander.ecommerce.model.Order;

import javax.ws.rs.*;
import javax.ws.rs.core.*;
import javax.ws.rs.core.Response.Status;
import java.net.URI;
import java.util.ArrayList;

@Path ("ecommerce/customers")
@Produces (MediaType.APPLICATION_JSON)
@Consumes (MediaType.APPLICATION_JSON)
public final class CustomerService extends WebShopService
{
    @Context
    private UriInfo uriInfo;

    public CustomerService () throws RepositoryException
    {
        super();
    }

    //  Hämta en användare med ett visst id
    @GET
    @Path ("{username}")
    public Response getCustomer (@PathParam ("username") final String username)
    {
        Customer customer = shopService.getCustomer(username);
        return Response.ok(customer).build();
    }

    //  Skapa en ny användare – detta ska returnera en länk till den skapade
    // användaren i Location-headern
    @POST
    public Response createCustomer (final Customer customer)
    {
        shopService.addCustomer(customer);

        final URI location = uriInfo.getAbsolutePathBuilder().path(customer.userName).build();
        return Response.created(location).build();
    }

    //  Uppdatera en användare
    @PUT
    @Path ("{username}")
    public Response putCustomer (@PathParam ("username") final String username, final Customer customer)
    {
        // if path username and new customer username matches then update
        // repository
        if (username.equals(customer.userName))
        {
            shopService.updateCustomer(customer);
            return Response.status(Status.NO_CONTENT).build();
        }
        // otherwise send error code
        return Response.status(Status.BAD_REQUEST).entity("Username mismatch between path and new customer info").build();
    }

    //  Ta bort en användare (eller sätta den som inaktiv)
    @DELETE
    @Path ("{username}")
    public Response deleteCustomer (@PathParam ("username") final String username)
    {
        shopService.removeCustomer(username);

        return Response.noContent().build();
    }

    @GET
    @Path ("{username}/cart")
    public Response getOrder (@PathParam ("username") final String username)
    {
        ArrayList<Integer> cartList;
        cartList = shopService.getCustomer(username).getAllShoppingCartItems();

        // GenericEntity is created for IntegerListMapper generic handling
        return Response.ok(new GenericEntity<ArrayList<Integer>>(cartList)
        {
        }).build();
    }

    @POST
    @Path ("{username}/cart")
    public Response addToCart (@PathParam ("username") final String username,
                               @QueryParam ("amount") @DefaultValue ("1") final Integer amount,
                               final String productId)
    {
        try
        {
            int productIdInt = Integer.parseInt(productId);
            shopService.addProductToCustomer(productIdInt, username, amount);

            final URI location = uriInfo.getAbsolutePathBuilder().build();
            return Response.created(location).build();
        } catch (NumberFormatException e)
        {
            return Response.status(Status.BAD_REQUEST).entity("Expected body to be parsable as integers").build();
        }
    }

    //  Hämta en användares alla order
    @GET
    @Path ("{username}/orders")
    public Response getOrders (@PathParam ("username") final String username)
    {
        ArrayList<Order> orderList;
        orderList = new ArrayList<Order>(shopService.getOrdersFromUser(username));

        // GenericEntity is created for IntegerListMapper generic handling
        return Response.ok(new GenericEntity<ArrayList<Order>>(orderList)
        {
        }).build();
    }
}