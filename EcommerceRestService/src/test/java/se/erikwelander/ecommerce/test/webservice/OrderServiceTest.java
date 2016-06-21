/////////////////////////////////////////////////////////////////////////////
// Name:        OrderServiceTest.java
// Encoding:	UTF-8
//
// Purpose:     Tests all order REST APIs in order to ensure that
//              the service works as expected.
//
// Author:      Erik Welander (mail@erikwelander.se)
// Modified:    2016-06-21
// Copyright:   Erik Welander
// Licence:     Creative Commons "by-nc-nd"
/////////////////////////////////////////////////////////////////////////////
package se.erikwelander.ecommerce.test.webservice;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Test;
import se.erikwelander.ecommerce.model.Customer;
import se.erikwelander.ecommerce.model.Order;
import se.erikwelander.ecommerce.model.Product;
import se.erikwelander.ecommerce.webservice.util.CustomerMapper;
import se.erikwelander.ecommerce.webservice.util.IntegerListMapper;
import se.erikwelander.ecommerce.webservice.util.OrderMapper;
import se.erikwelander.ecommerce.webservice.util.ProductMapper;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.util.ArrayList;

import static org.junit.Assert.assertEquals;

public class OrderServiceTest
{
    private static final Client client = ClientBuilder.newBuilder()
            .register(CustomerMapper.class)
            .register(IntegerListMapper.class)
            .register(ProductMapper.class)
            .register(OrderMapper.class)
            .build();

    // Models
    private static final Customer CUSTOMER_KIRA = new Customer("Kira", "password", "erik@erikwelander.se", "Erik",
            "Welander", "Järfälla", "987654321");
    private static final Product PRODUCT_TOMATO = new Product(500, 45, "Tomato", "Vegetables", "Spain", "A beautiful tomato", "http://google.com/tomato.jpg");
    private static final Product PRODUCT_LETTUCE = new Product(200, 88, "Lettuce", "Vegetables", "France", "A mound of lettuce", "http://google.com/lettuce.jpg");

    // Resource targets
    private static final WebTarget CUSTOMERS_TARGET;
    private static final WebTarget PRODUCTS_TARGET;
    private static final WebTarget ORDERS_TARGET;

    static
    {
        CUSTOMERS_TARGET = client.target(ConnectionConfig.CUSTOMERS_URL);
        PRODUCTS_TARGET = client.target(ConnectionConfig.PRODUCTS_URL);
        ORDERS_TARGET = client.target(ConnectionConfig.ORDERS_URL);
    }

    // Responses with info about created products (from init)
    private Response createProductLettuceResponse;
    private Response createProductTomatoResponse;
    private Response createCustomerKiraResponse;

    @AfterClass
    public static void tearDown ()
    {
        // Truncate repository tables after all tests are done
        WebTarget admin = client.target(ConnectionConfig.URL_BASE + "/admin");
        admin.request().buildPost(Entity.entity("reset-repo", MediaType.TEXT_HTML)).invoke();
    }

    @Before
    public void init ()
    {
        // Truncate repository tables before tests
        WebTarget admin = client.target(ConnectionConfig.URL_BASE + "/admin");
        admin.request().buildPost(Entity.entity("reset-repo", MediaType.TEXT_HTML)).invoke();

        // POST - Create products
        createProductTomatoResponse = PRODUCTS_TARGET.request(MediaType.APPLICATION_JSON)
                .buildPost(Entity.entity(PRODUCT_TOMATO, MediaType.APPLICATION_JSON))
                .invoke();
        assertEquals(201, createProductTomatoResponse.getStatus());

        createProductLettuceResponse = PRODUCTS_TARGET.request(MediaType.APPLICATION_JSON)
                .buildPost(Entity.entity(PRODUCT_LETTUCE, MediaType.APPLICATION_JSON))
                .invoke();
        assertEquals(201, createProductLettuceResponse.getStatus());

        // POST - Create customer
        createCustomerKiraResponse = CUSTOMERS_TARGET.request(MediaType.APPLICATION_JSON)
                .buildPost(Entity.entity(CUSTOMER_KIRA, MediaType.APPLICATION_JSON))
                .invoke();
        assertEquals(201, createCustomerKiraResponse.getStatus());
    }

    //  Skapa en order för en användare
    @Test
    public void canCreateCustomerOrder () throws IOException
    {
        final Product PRODUCT_TOMATO = client.target(createProductTomatoResponse.getLocation())
                .request(MediaType.APPLICATION_JSON)
                .get(Product.class);

        // POST - Add products to cart
        final Response addProductsToCartResponse = CUSTOMERS_TARGET
                .path(CUSTOMER_KIRA.userName)
                .path("cart")
                .request()
                .buildPost(Entity.entity(Integer.toString(PRODUCT_TOMATO.id), MediaType.APPLICATION_JSON))
                .invoke();
        assertEquals(201, addProductsToCartResponse.getStatus());

        // POST - Create order
        final Response createOrderResponse = ORDERS_TARGET
                .request()
                .buildPost(Entity.entity(CUSTOMER_KIRA.userName, MediaType.APPLICATION_JSON))
                .invoke();
        assertEquals(201, createOrderResponse.getStatus());

        // GET - Retrieve created order and check contents
        final Order createdOrder = client.target(createOrderResponse.getLocation())
                .request()
                .get(Order.class);

        assertEquals(PRODUCT_TOMATO.id, (int) createdOrder.getAllProductIDs().get(0));
    }

    //  Uppdatera en order för en användare
    @Test
    public void canUpdateCustomerOrder ()
    {
        final Product PRODUCT_TOMATO = client.target(createProductTomatoResponse.getLocation())
                .request(MediaType.APPLICATION_JSON)
                .get(Product.class);

        // POST - Add products to cart
        final Response addProductsToCartResponse = CUSTOMERS_TARGET
                .path(CUSTOMER_KIRA.userName)
                .path("cart")
                .request()
                .buildPost(Entity.entity(Integer.toString(PRODUCT_TOMATO.id), MediaType.APPLICATION_JSON))
                .invoke();
        assertEquals(201, addProductsToCartResponse.getStatus());

        // POST - Create order
        final Response createOrderResponse = ORDERS_TARGET
                .request()
                .buildPost(Entity.entity(CUSTOMER_KIRA.userName, MediaType.APPLICATION_JSON))
                .invoke();
        assertEquals(201, createOrderResponse.getStatus());

        // GET - Retrieve created order and check contents
        final Order createdOrder = client.target(createOrderResponse.getLocation())
                .request()
                .get(Order.class);

        assertEquals(PRODUCT_TOMATO.id, (int) createdOrder.getAllProductIDs().get(0));

        // PUT - Create updated Order with newShoppingCart
        ArrayList<Integer> newShoppingCart = new ArrayList<Integer>();
        newShoppingCart.add(PRODUCT_TOMATO.id);
        newShoppingCart.add(PRODUCT_TOMATO.id);
        Order updatedOrder = new Order(createdOrder.id, CUSTOMER_KIRA.userName, newShoppingCart);

        final Response updateOrderResponse = ORDERS_TARGET
                .request()
                .buildPut(Entity.entity(updatedOrder, MediaType.APPLICATION_JSON))
                .invoke();
        assertEquals(200, updateOrderResponse.getStatus());

        // GET - Get updated order and compare shoppingcarts
        final Order updatedOrderFromServer = client.target(createOrderResponse.getLocation())
                .request()
                .get(Order.class);
        assertEquals(updatedOrder.getAllProductIDs(), updatedOrderFromServer.getAllProductIDs());
    }

    //  Ta bort en order för en användare
    @Test
    public void canRemoveCustomerOrder ()
    {
        final Product PRODUCT_TOMATO = client.target(createProductTomatoResponse.getLocation())
                .request(MediaType.APPLICATION_JSON)
                .get(Product.class);

        // POST - Add products to cart
        final Response addProductsToCartResponse = CUSTOMERS_TARGET
                .path(CUSTOMER_KIRA.userName)
                .path("cart")
                .request()
                .buildPost(Entity.entity(Integer.toString(PRODUCT_TOMATO.id), MediaType.APPLICATION_JSON))
                .invoke();
        assertEquals(201, addProductsToCartResponse.getStatus());

        // POST - Create order
        final Response createOrderResponse = ORDERS_TARGET
                .request()
                .buildPost(Entity.entity(CUSTOMER_KIRA.userName, MediaType.APPLICATION_JSON))
                .invoke();
        assertEquals(201, createOrderResponse.getStatus());

        WebTarget newOrderTarget = client.target(createOrderResponse.getLocation());

        // GET - Retrieve created order and check contents
        final Order createdOrder = newOrderTarget
                .request()
                .get(Order.class);
        assertEquals(PRODUCT_TOMATO.id, (int) createdOrder.getAllProductIDs().get(0));

        // DELETE - Delete created order
        final Response deleteOrderResponse = newOrderTarget
                .request()
                .delete();
        assertEquals(204, deleteOrderResponse.getStatus());

        // GET - Try to retrieve deleted order, should fail
        final Response thisShouldFailResponse = newOrderTarget
                .request()
                .get();
        assertEquals(400, thisShouldFailResponse.getStatus());
    }
}