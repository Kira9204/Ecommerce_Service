/////////////////////////////////////////////////////////////////////////////
// Name:        SQLShopServiceTest.java
// Encoding:	UTF-8
//
// Purpose:     Step by step testing of ShopService APIs using SQL repositories.
//
// Author:      Erik Welander (mail@erikwelander.se)
// Modified:    2016-06-21
// Copyright:   Erik Welander
// Licence:     Creative Commons "by-nc-nd"
/////////////////////////////////////////////////////////////////////////////
package se.erikwelander.ecommerce.test;

import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import se.erikwelander.ecommerce.exception.ShopServiceException;
import se.erikwelander.ecommerce.model.Customer;
import se.erikwelander.ecommerce.model.Order;
import se.erikwelander.ecommerce.model.Product;
import se.erikwelander.ecommerce.repository.sql.*;
import se.erikwelander.ecommerce.service.ShopService;

import java.sql.Date;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

@FixMethodOrder (MethodSorters.NAME_ASCENDING)
public class SQLShopServiceTest
{
    private static Product PRODUCT_TOMATO = new Product(1, 5, 45, "Tomato", "Vegetables", "Spain", "A beautiful tomato", "http://google.com/tomato.jpg");
    private static Product PRODUCT_LETTUCE = new Product(2, 88, 2, "Lettuce", "Vegetables", "France", "A mound of lettuce", "http://google.com/lettuce.jpg");
    private static Product PRODUCT_APPLE = new Product(3, 20, 10, "Apple", "Fruit", "Asia", "Some delicious apples", "http://google.com/apple.jpg");
    //After we remove the product tomato and then re-add it, it will have a new auto-incremented id value of 3
    private static Product PRODUCT_TOMATO_READDED = new Product(4, 5, 45, "Tomato", "Vegetables", "Spain", "A beautiful tomato", "http://google.com/tomato.jpg");
    private static Customer CUSTOMER_1 = new Customer("Kira", "password", "erik@erikwelander.se", "Erik", "Welander", "Järfälla", "987654321");
    private static Customer CUSTOMER_2 = new Customer("Erik", "lol123", "erik.welander@hotmail.com", "Erik2", "Welander2", "Järfälla2", "9876543212");
    private static Customer CUSTOMER_3 = new Customer("Erik2", "lol123456789", "erik.welander@hotmail.com", "Erik22", "Welander22", "Järfälla22", "98765432122");
    private static Date currentDate = new Date(System.currentTimeMillis());
    private static Order CUSTOMER_1_ORDER = new Order(1, CUSTOMER_1.userName, new ArrayList<Integer>(Arrays.asList(PRODUCT_TOMATO_READDED.id, PRODUCT_LETTUCE.id, PRODUCT_APPLE.id, PRODUCT_APPLE.id)), currentDate, null);
    private static Order CUSTOMER_1_ORDER_UPDATED = new Order(1, CUSTOMER_1.userName, new ArrayList<Integer>(Arrays.asList(PRODUCT_TOMATO_READDED.id, PRODUCT_LETTUCE.id, PRODUCT_APPLE.id, PRODUCT_APPLE.id)), currentDate, currentDate);
    private static Order CUSTOMER_2_ORDER = new Order(2, CUSTOMER_2.userName, new ArrayList<Integer>(Arrays.asList(PRODUCT_TOMATO_READDED.id, PRODUCT_TOMATO_READDED.id)), currentDate, null);
    private static Order CUSTOMER_3_ORDER = new Order(3, CUSTOMER_3.userName, new ArrayList<Integer>(Arrays.asList(PRODUCT_TOMATO_READDED.id, PRODUCT_LETTUCE.id, PRODUCT_APPLE.id)), currentDate, null);
    ShopService shopService;

    @Before
    public void setUp () throws Exception
    {
        shopService = new ShopService(new SQLCustomerRepository(),
                new SQLProductRepository(),
                new SQLOrderRepository());
    }

    @Test
    public void CLEAN0_STATE () throws SQLException
    {
        SQLConnector sqlConnector = new SQLConnector();
        sqlConnector.queryUpdate(sqlConnector.prepareStatement("TRUNCATE TABLE " + DBInfo.database + ".customer"));
        sqlConnector.queryUpdate(sqlConnector.prepareStatement("TRUNCATE TABLE " + DBInfo.database + ".customer_cart"));
        sqlConnector.queryUpdate(sqlConnector.prepareStatement("TRUNCATE TABLE " + DBInfo.database + ".`order`"));
        sqlConnector.queryUpdate(sqlConnector.prepareStatement("TRUNCATE TABLE " + DBInfo.database + ".order_items"));
        sqlConnector.queryUpdate(sqlConnector.prepareStatement("TRUNCATE TABLE " + DBInfo.database + ".product"));
    }

    @Test
    public void test1_AddProduct ()
    {
        shopService.addProduct(PRODUCT_TOMATO);
        shopService.addProduct(PRODUCT_LETTUCE);
        shopService.addProduct(PRODUCT_APPLE);
    }

    @Test
    public void test2_GetProduct ()
    {
        assertEquals(PRODUCT_TOMATO, shopService.getProduct(PRODUCT_TOMATO.id));
        assertEquals(PRODUCT_LETTUCE, shopService.getProduct(PRODUCT_LETTUCE.id));
        assertEquals(PRODUCT_APPLE, shopService.getProduct(PRODUCT_APPLE.id));
    }

    @Test
    public void test3_GetAllProducts () throws ShopServiceException
    {
        List<Product> allProducts = new ArrayList<>();
        allProducts.add(PRODUCT_TOMATO);
        allProducts.add(PRODUCT_LETTUCE);
        allProducts.add(PRODUCT_APPLE);

        assertEquals(allProducts, shopService.getAllProducts());
    }

    @Test
    public void test4_updateProduct ()
    {
        PRODUCT_TOMATO.increaseDecreaseQuantity(2);
        PRODUCT_TOMATO_READDED.increaseDecreaseQuantity(2);
        assertNotEquals(PRODUCT_TOMATO, shopService.getProduct(PRODUCT_TOMATO.id));

        shopService.updateProduct(PRODUCT_TOMATO);
        assertEquals(PRODUCT_TOMATO, shopService.getProduct(PRODUCT_TOMATO.id));
    }

    @Test (expected = ShopServiceException.class)
    public void test5_removeProduct ()
    {
        shopService.removeProduct(PRODUCT_TOMATO.id);
        shopService.getProduct(PRODUCT_TOMATO.id);
    }

    @Test
    public void test6_resetProducts ()
    {
        shopService.addProduct(PRODUCT_TOMATO);
        shopService.updateProduct(PRODUCT_LETTUCE);
        shopService.updateProduct(PRODUCT_APPLE);

        Product tomato = shopService.getProduct(PRODUCT_TOMATO_READDED.id);

        assertEquals(PRODUCT_TOMATO_READDED, shopService.getProduct(PRODUCT_TOMATO_READDED.id));
        assertEquals(PRODUCT_LETTUCE, shopService.getProduct(PRODUCT_LETTUCE.id));
        assertEquals(PRODUCT_APPLE, shopService.getProduct(PRODUCT_APPLE.id));

    }

    @Test
    public void test7_addCustomer ()
    {
        shopService.addCustomer(CUSTOMER_1);
        shopService.addCustomer(CUSTOMER_2);
        shopService.addCustomer(CUSTOMER_3);
    }

    @Test (expected = ShopServiceException.class)
    public void test8_removCustomer ()
    {
        shopService.removeCustomer(CUSTOMER_1.userName);
        shopService.getCustomer(CUSTOMER_1.userName);
    }


    @Test
    public void test9_addGetCustomer ()
    {
        shopService.addCustomer(CUSTOMER_1);
        assertEquals(CUSTOMER_1, shopService.getCustomer(CUSTOMER_1.userName));
        assertEquals(CUSTOMER_2, shopService.getCustomer(CUSTOMER_2.userName));
        assertEquals(CUSTOMER_3, shopService.getCustomer(CUSTOMER_3.userName));
    }


    @Test
    public void testA_updateCustomer ()
    {
        CUSTOMER_1.emptyShoppingCart();
        CUSTOMER_1.addToShoppingCart(PRODUCT_TOMATO_READDED.id);
        CUSTOMER_1.addToShoppingCart(PRODUCT_LETTUCE.id);
        CUSTOMER_1.addToShoppingCart(PRODUCT_APPLE.id);
        CUSTOMER_1.addToShoppingCart(PRODUCT_APPLE.id);

        CUSTOMER_2.emptyShoppingCart();
        CUSTOMER_2.addToShoppingCart(PRODUCT_TOMATO_READDED.id);
        CUSTOMER_2.addToShoppingCart(PRODUCT_TOMATO_READDED.id);

        CUSTOMER_3.emptyShoppingCart();
        CUSTOMER_3.addToShoppingCart(PRODUCT_TOMATO_READDED.id);
        CUSTOMER_3.addToShoppingCart(PRODUCT_LETTUCE.id);
        CUSTOMER_3.addToShoppingCart(PRODUCT_APPLE.id);

        assertNotEquals(CUSTOMER_1, shopService.getCustomer(CUSTOMER_1.userName));
        assertNotEquals(CUSTOMER_2, shopService.getCustomer(CUSTOMER_2.userName));
        assertNotEquals(CUSTOMER_3, shopService.getCustomer(CUSTOMER_3.userName));

        shopService.addProductToCustomer(PRODUCT_TOMATO_READDED.id, CUSTOMER_1.userName, 1);
        shopService.addProductToCustomer(PRODUCT_LETTUCE.id, CUSTOMER_1.userName, 1);
        shopService.addProductToCustomer(PRODUCT_APPLE.id, CUSTOMER_1.userName, 2);

        shopService.addProductToCustomer(PRODUCT_TOMATO_READDED.id, CUSTOMER_2.userName, 2);

        shopService.addProductToCustomer(PRODUCT_TOMATO_READDED.id, CUSTOMER_3.userName, 1);
        shopService.addProductToCustomer(PRODUCT_LETTUCE.id, CUSTOMER_3.userName, 1);
        shopService.addProductToCustomer(PRODUCT_APPLE.id, CUSTOMER_3.userName, 1);


        assertEquals(CUSTOMER_1, shopService.getCustomer(CUSTOMER_1.userName));
        assertEquals(CUSTOMER_2, shopService.getCustomer(CUSTOMER_2.userName));
        assertEquals(CUSTOMER_3, shopService.getCustomer(CUSTOMER_3.userName));

    }

    @Test
    public void testB_hasOrder ()
    {
        ArrayList<Integer> empty = new ArrayList<>();
        assertEquals(empty, shopService.getOrdersFromUser(CUSTOMER_1.userName));
        assertEquals(empty, shopService.getOrdersFromUser(CUSTOMER_2.userName));
        assertEquals(empty, shopService.getOrdersFromUser(CUSTOMER_3.userName));
    }

    @Test
    public void testC_addOrder ()
    {
        shopService.createOrder(CUSTOMER_1.userName);
        shopService.createOrder(CUSTOMER_2.userName);
        shopService.createOrder(CUSTOMER_3.userName);
    }

    @Test
    public void testD_getOrder ()
    {
        assertEquals(new ArrayList<Order>(Arrays.asList(CUSTOMER_1_ORDER)), shopService.getOrdersFromUser(CUSTOMER_1.userName));
        assertEquals(new ArrayList<Order>(Arrays.asList(CUSTOMER_2_ORDER)), shopService.getOrdersFromUser(CUSTOMER_2.userName));
        assertEquals(new ArrayList<Order>(Arrays.asList(CUSTOMER_3_ORDER)), shopService.getOrdersFromUser(CUSTOMER_3.userName));
    }

    @Test
    public void testE_updateOrder ()
    {
        shopService.updateOrder(CUSTOMER_1_ORDER_UPDATED);
        assertNotEquals(CUSTOMER_1_ORDER, shopService.getOrder(1));
        assertEquals(CUSTOMER_1_ORDER_UPDATED, shopService.getOrder(1));
    }

    @Test (expected = ShopServiceException.class)
    public void testF_deleteOrder ()
    {
        assertEquals(CUSTOMER_1_ORDER_UPDATED, shopService.getOrder(1));
        shopService.removeOrder(1);
        shopService.getOrder(1);
    }

    @Test
    public void testG_CLEANUP () throws SQLException
    {
        CLEAN0_STATE();
    }

}
