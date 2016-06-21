/////////////////////////////////////////////////////////////////////////////
// Name:        CustomerServiceTest.java
// Encoding:	UTF-8
//
// Purpose:     Tests all Customer REST APIs in order to ensure that
//              the service works as expected.
//
// Author:      Erik Welander (mail@erikwelander.se)
// Modified:    2016-06-21
// Copyright:   Erik Welander
// Licence:     Creative Commons "by-nc-nd"
/////////////////////////////////////////////////////////////////////////////
package se.erikwelander.ecommerce.test.webservice;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
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
import java.lang.reflect.Type;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class CustomerServiceTest
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
        // Truncate repository tables before each test
        WebTarget admin = client.target(ConnectionConfig.URL_BASE + "/admin");
        admin.request().buildPost(Entity.entity("reset-repo", MediaType.TEXT_HTML)).invoke();
    }

    //  Skapa en ny användare
    @Test
    public void canCreateCustomer ()
    {
        // POST - Create customer
        Response response = CUSTOMERS_TARGET.request(MediaType.APPLICATION_JSON)
                .buildPost(Entity.entity(CUSTOMER_KIRA, MediaType.APPLICATION_JSON))
                .invoke();
        assertEquals(201, response.getStatus());

        // GET - Retrieve created customer
        Customer createdCustomer = CUSTOMERS_TARGET.path(CUSTOMER_KIRA.userName)
                .request(MediaType.APPLICATION_JSON)
                .get(Customer.class);
        assertEquals(createdCustomer, CUSTOMER_KIRA);
    }

    //  Skapa en ny användare – detta ska returnera en länk till den skapade
    // användaren i Location-headern
    @Test
    public void createCustomerReturnsCorrectLocationHeaderForCreatedCustomer () throws URISyntaxException
    {
        final URI EXPECTED_URI = new URI("http://localhost:8080/ecommerce/customers/"
                + CUSTOMER_KIRA.userName);

        // POST - Create new customer
        Response response = CUSTOMERS_TARGET.request(MediaType.APPLICATION_JSON)
                .buildPost(Entity.entity(CUSTOMER_KIRA, MediaType.APPLICATION_JSON))
                .invoke();
        assertEquals(201, response.getStatus());

        // Check returned location URI
        assertEquals(EXPECTED_URI, response.getLocation());
    }

    //  Uppdatera en användare
    @Test
    public void canUpdateCustomer ()
    {
        // POST - create Customer2 in repo
        Response postResponse = CUSTOMERS_TARGET.request(MediaType.APPLICATION_JSON)
                .buildPost(Entity.entity(CUSTOMER_KIRA, MediaType.APPLICATION_JSON))
                .invoke();
        assertEquals(201, postResponse.getStatus());

        // Updated customer2 with changed password.
        Customer updatedCustomer2 = new Customer(CUSTOMER_KIRA.userName, "secret", CUSTOMER_KIRA.email,
                CUSTOMER_KIRA.firstName, CUSTOMER_KIRA.lastName,
                CUSTOMER_KIRA.address, CUSTOMER_KIRA.phoneNumber);

        // POST - Update customer
        Response putResponse = CUSTOMERS_TARGET.path(CUSTOMER_KIRA.userName)
                .request(MediaType.APPLICATION_JSON)
                .buildPut(Entity.entity(updatedCustomer2, MediaType.APPLICATION_JSON))
                .invoke();
        assertEquals(204, putResponse.getStatus());

        // GET - Check that customer is updated
        Customer updatedCustomer2FromRepo = CUSTOMERS_TARGET.path(CUSTOMER_KIRA.userName)
                .request(MediaType.APPLICATION_JSON)
                .get(Customer.class);
        assertEquals(updatedCustomer2, updatedCustomer2FromRepo);
    }

    //  Ta bort en användare (eller sätta den som inaktiv)
    @Test
    public void canRemoveCustomer ()
    {
        // POST - Create customer
        Response postResponse = CUSTOMERS_TARGET.request(MediaType.APPLICATION_JSON)
                .buildPost(Entity.entity(CUSTOMER_KIRA, MediaType.APPLICATION_JSON))
                .invoke();
        assertEquals(201, postResponse.getStatus());

        // GET - Check that it is in repository
        Response thisShouldSucceedResponse = CUSTOMERS_TARGET.path(CUSTOMER_KIRA.userName)
                .request(MediaType.APPLICATION_JSON)
                .get();
        assertEquals(200, thisShouldSucceedResponse.getStatus());

        // DELETE - Delete it
        Response deleteResponse = CUSTOMERS_TARGET.path(CUSTOMER_KIRA.userName)
                .request(MediaType.APPLICATION_JSON)
                .buildDelete()
                .invoke();
        assertEquals(204, deleteResponse.getStatus());

        // GET - Try to retrieve deleted customer, should fail
        Response thisShouldFailResponse = CUSTOMERS_TARGET.path(CUSTOMER_KIRA.userName)
                .request(MediaType.APPLICATION_JSON)
                .get();
        assertEquals(400, thisShouldFailResponse.getStatus());
    }

    @Test
    public void canAddProductToCart ()
    {
        // POST - Create customer
        Response createCustomerKiraResponse = CUSTOMERS_TARGET.request(MediaType.APPLICATION_JSON)
                .buildPost(Entity.entity(CUSTOMER_KIRA, MediaType.APPLICATION_JSON))
                .invoke();
        assertEquals(201, createCustomerKiraResponse.getStatus());

        // POST - Create products
        Response createProductTomatoResponse = PRODUCTS_TARGET.request(MediaType.APPLICATION_JSON)
                .buildPost(Entity.entity(PRODUCT_TOMATO, MediaType.APPLICATION_JSON))
                .invoke();
        assertEquals(201, createProductTomatoResponse.getStatus());

        // GET - Get created products
        final Product PRODUCT_TOMATO = client.target(createProductTomatoResponse.getLocation())
                .request(MediaType.APPLICATION_JSON)
                .get(Product.class);

        // POST - Add products to cart
        final Response addProductToCartResponse = CUSTOMERS_TARGET
                .path(CUSTOMER_KIRA.userName)
                .path("cart")
                .request()
                .buildPost(Entity.entity(Integer.toString(PRODUCT_TOMATO.id), MediaType.APPLICATION_JSON))
                .invoke();
        assertEquals(201, addProductToCartResponse.getStatus());

        // GET - Get cart contents
        final String shoppingCartJson = CUSTOMERS_TARGET
                .path(CUSTOMER_KIRA.userName)
                .path("cart")
                .request(MediaType.APPLICATION_JSON)
                .get(String.class);

        // Create gson parser that uses adapter from IntegerListMapper
        Type integerListType = new TypeToken<ArrayList<Integer>>()
        {
        }.getType();
        Gson gson = new GsonBuilder().registerTypeAdapter(integerListType, new IntegerListMapper.IntegerListAdapter())
                .create();

        // Parse received shoppingartJson
        JsonObject shoppingCartJsonObject = gson.fromJson(shoppingCartJson, JsonObject.class);
        JsonArray cartJsonArray = shoppingCartJsonObject.get("integerArray").getAsJsonArray();
        ArrayList<Integer> cartArrayList = gson.fromJson(cartJsonArray, integerListType);

        // And verify content
        assertEquals(PRODUCT_TOMATO.id, (int) cartArrayList.get(0));
    }

    //  Hämta en användares alla order
    @Test
    public void canGetCustomerOrders ()
    {
        // POST - Create customer
        Response createCustomerResponse = CUSTOMERS_TARGET.request(MediaType.APPLICATION_JSON)
                .buildPost(Entity.entity(CUSTOMER_KIRA, MediaType.APPLICATION_JSON))
                .invoke();
        assertEquals(201, createCustomerResponse.getStatus());

        Order orderToBeChecked1 = addOrder(CUSTOMER_KIRA);
        Order orderToBeChecked2 = addOrder(CUSTOMER_KIRA);
        Order orderToBeChecked3 = addOrder(CUSTOMER_KIRA);

        // GET - Retrieve created order
        final String ordersJson = CUSTOMERS_TARGET
                .path(CUSTOMER_KIRA.userName)
                .path("orders")
                .request()
                .get(String.class);

        // Verify received ordersJson String
        HashMap<Integer, Order> customerOrders = parseOrderJsonArrayList(ordersJson);

        assertTrue(customerOrders.containsKey(orderToBeChecked1.id));
        Order orderFromRepo1 = customerOrders.get(orderToBeChecked1.id);
        assertEquals(orderToBeChecked1, orderFromRepo1);

        assertTrue(customerOrders.containsKey(orderToBeChecked2.id));
        Order orderFromRepo2 = customerOrders.get(orderToBeChecked2.id);
        assertEquals(orderToBeChecked2, orderFromRepo2);

        assertTrue(customerOrders.containsKey(orderToBeChecked3.id));
        Order orderFromRepo3 = customerOrders.get(orderToBeChecked3.id);
        assertEquals(orderToBeChecked3, orderFromRepo3);
    }

    private HashMap<Integer, Order> parseOrderJsonArrayList (String ordersJson)
    {
        // Create gson parser that uses adapter from OrderMapper
        Gson gson = new GsonBuilder().registerTypeAdapter(Order.class, new OrderMapper.OrderAdapter()).create();
        HashMap<Integer, Order> customerOrders = new HashMap<>();

        // Parse received ordersJson String and put it in HashMap
        JsonObject orderJsonObject = gson.fromJson(ordersJson, JsonObject.class);
        JsonArray orderJsonArray = orderJsonObject.get("orderArray").getAsJsonArray();

        for (JsonElement order : orderJsonArray)
        {
            Order newOrder2 = gson.fromJson(order, Order.class);
            customerOrders.put(newOrder2.id, newOrder2);
        }
        return customerOrders;
    }

    // Handles POSTs and GET needed to addOrder and returns finished Order object
    private Order addOrder (Customer customer)
    {
        // POST - Create products
        Response createProductResponse1 = PRODUCTS_TARGET.request(MediaType.APPLICATION_JSON)
                .buildPost(Entity.entity(PRODUCT_TOMATO, MediaType.APPLICATION_JSON))
                .invoke();
        assertEquals(201, createProductResponse1.getStatus());

        final Product PRODUCT_TOMATO = client.target(createProductResponse1.getLocation())
                .request(MediaType.APPLICATION_JSON)
                .get(Product.class);

        // POST - Add products to cart
        final Response addProductsToCartResponse = CUSTOMERS_TARGET
                .path(customer.userName)
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

        return createdOrder;
    }
}