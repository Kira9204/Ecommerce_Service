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
import se.erikwelander.ecommerce.model.Order;

import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import java.net.URI;

@Path ("ecommerce/orders")
@Produces (MediaType.APPLICATION_JSON)
@Consumes (MediaType.APPLICATION_JSON)
public final class OrderService extends WebShopService
{
    @Context
    private UriInfo uriInfo;

    public OrderService () throws RepositoryException
    {
        super();
    }

    //  Hämta en viss order för en användare
    @GET
    @Path ("{orderId}")
    public Response getOrder (@PathParam ("orderId") final int orderId)
    {
        Order order = shopService.getOrder(orderId);
        return Response.ok(order).build();
    }

    //  Skapa en order för en användare
    @POST
    public Response createOrder (final String username)
    {
        Order newOrder = shopService.createOrder(username);

        final URI location = uriInfo.getAbsolutePathBuilder().path(Integer.toString(newOrder.id)).build();
        return Response.created(location).build();
    }

    //  Uppdatera en order för en användare
    @PUT
    public Response updateOrder (final Order order)
    {
        shopService.updateOrder(order);
        return Response.ok().build();
    }

    //  Ta bort en order för en användare
    @DELETE
    @Path ("{orderId}")
    public Response removeOrder (@PathParam ("orderId") final Integer orderId)
    {
        shopService.removeOrder(orderId);
        return Response.noContent().build();
    }
}