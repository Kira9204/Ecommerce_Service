/////////////////////////////////////////////////////////////////////////////
// Name:        ProductService.java
// Encoding:	UTF-8
//
// Purpose:     A RESTful service for managing shop products.
//              All data serialization is in JSON format.
//
// Author:      Erik Welander (mail@erikwelander.se)
// Modified:    2016-06-21
// Copyright:   Erik Welander
// Licence:     Creative Commons "by-nc-nd"
/////////////////////////////////////////////////////////////////////////////
package se.erikwelander.ecommerce.webservice;

import se.erikwelander.ecommerce.exception.RepositoryException;
import se.erikwelander.ecommerce.model.Product;

import javax.ws.rs.*;
import javax.ws.rs.core.*;
import javax.ws.rs.core.Response.Status;
import java.net.URI;
import java.util.ArrayList;

@Path ("ecommerce/products")
@Produces (MediaType.APPLICATION_JSON)
@Consumes (MediaType.APPLICATION_JSON)
public class ProductService extends WebShopService
{
    @Context
    private UriInfo uriInfo;

    public ProductService () throws RepositoryException
    {
        super();
    }

    //  Skapa en ny produkt – detta ska returnera en länk till den skapade
    // produkten i Location-headern
    @POST
    public Response createProduct (final Product inProduct)
    {
        Product product = shopService.addProduct(inProduct);
        final URI location = uriInfo.getAbsolutePathBuilder().path("" + product.id).build();
        return Response.created(location).build();
    }

    //  Hämta alla produkter
    @GET
    public Response getProducts ()
    {
        ArrayList<Product> products = (ArrayList<Product>) shopService.getAllProducts();

        // GenericEntity is created for ProductListMapper generic handling
        return Response.ok(new GenericEntity<ArrayList<Product>>(products)
        {
        }).build();
    }

    //  Hämta en produkt med ett visst id
    @GET
    @Path ("{productId}")
    public Response getProduct (@PathParam ("productId") final String productId)
    {
        try
        {
            int productIdInt = Integer.parseInt(productId);
            Product product = shopService.getProduct(productIdInt);

            return Response.ok(product).build();
        } catch (NumberFormatException e)
        {
            return Response.status(Status.BAD_REQUEST).entity("Product id must be parsable to an integer.").build();
        }
    }

    //  Uppdatera en produkt
    @PUT
    @Path ("{productId}")
    public Response putProduct (@PathParam ("productId") final String productId, Product inProduct)
    {
        try
        {
            int productIdInt = Integer.parseInt(productId);
            Product product = new Product(productIdInt, inProduct.getQuantity(), inProduct.price, inProduct.title, inProduct.category, inProduct.manufacturer, inProduct.description, inProduct.image);

            shopService.updateProduct(product);
            return Response.status(Status.NO_CONTENT).build();
        } catch (NumberFormatException e)
        {
            return Response.status(Status.BAD_REQUEST).entity("Product id must be parsable to an integer.").build();
        }
    }

    //  Ta bort en produkt (eller sätta den som inaktiv)
    @DELETE
    @Path ("{productId}")
    public Response deleteProduct (@PathParam ("productId") final String productId)
    {
        try
        {
            int productIdInt = Integer.parseInt(productId);
            shopService.removeProduct(productIdInt);
            return Response.noContent().build();
        } catch (NumberFormatException e)
        {
            return Response.status(Status.BAD_REQUEST).entity("Product id must be parsable to an integer.").build();
        }
    }
}