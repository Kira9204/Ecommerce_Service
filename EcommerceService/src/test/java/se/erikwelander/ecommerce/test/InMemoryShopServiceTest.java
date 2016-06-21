/////////////////////////////////////////////////////////////////////////////
// Name:        InMemoryShopServiceTest.java
// Encoding:	UTF-8
//
// Purpose:     Unordered independent tests for ShopService APIs using InMemoryRepositories.
//
// Author:      Erik Welander (mail@erikwelander.se)
// Modified:    2016-06-21
// Copyright:   Erik Welander
// Licence:     Creative Commons "by-nc-nd"
/////////////////////////////////////////////////////////////////////////////
package se.erikwelander.ecommerce.test;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import se.erikwelander.ecommerce.exception.ShopServiceException;
import se.erikwelander.ecommerce.model.Customer;
import se.erikwelander.ecommerce.model.Order;
import se.erikwelander.ecommerce.model.Product;
import se.erikwelander.ecommerce.repository.memory.InMemoryCustomerRepository;
import se.erikwelander.ecommerce.repository.memory.InMemoryOrderRepository;
import se.erikwelander.ecommerce.repository.memory.InMemoryProductRepository;
import se.erikwelander.ecommerce.service.ShopService;

import java.sql.Date;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

@FixMethodOrder (MethodSorters.NAME_ASCENDING)
public class InMemoryShopServiceTest
{
    private Date currentDate = new Date(System.currentTimeMillis());

    @Test
    public void addGetProduct ()
    {
        ShopService shopService = new ShopService(new InMemoryCustomerRepository(), new InMemoryProductRepository(), new InMemoryOrderRepository());

        Product PRODUCT_TOMATO = new Product(1, 5, 45, "Tomato", "Vegetables", "Spain", "A beautiful tomato", "http://google.com/tomato.jpg");
        Product PRODUCT_LETTUCE = new Product(2, 88, 2, "Lettuce", "Vegetables", "France", "A mound of lettuce", "http://google.com/lettuce.jpg");
        Product PRODUCT_APPLE = new Product(3, 20, 10, "Apple", "Fruit", "Asia", "Some delicious apples", "http://google.com/apple.jpg");

        shopService.addProduct(PRODUCT_TOMATO);
        shopService.addProduct(PRODUCT_LETTUCE);
        shopService.addProduct(PRODUCT_APPLE);

        assertEquals(PRODUCT_TOMATO, shopService.getProduct(PRODUCT_TOMATO.id));
        assertEquals(PRODUCT_LETTUCE, shopService.getProduct(PRODUCT_LETTUCE.id));
        assertEquals(PRODUCT_APPLE, shopService.getProduct(PRODUCT_APPLE.id));
    }

    @Test
    public void addGetAllProducts ()
    {
        ShopService shopService = new ShopService(new InMemoryCustomerRepository(), new InMemoryProductRepository(), new InMemoryOrderRepository());

        Product PRODUCT_TOMATO = new Product(1, 5, 45, "Tomato", "Vegetables", "Spain", "A beautiful tomato", "http://google.com/tomato.jpg");
        Product PRODUCT_LETTUCE = new Product(2, 88, 2, "Lettuce", "Vegetables", "France", "A mound of lettuce", "http://google.com/lettuce.jpg");
        Product PRODUCT_APPLE = new Product(3, 20, 10, "Apple", "Fruit", "Asia", "Some delicious apples", "http://google.com/apple.jpg");

        shopService.addProduct(PRODUCT_TOMATO);
        shopService.addProduct(PRODUCT_LETTUCE);
        shopService.addProduct(PRODUCT_APPLE);

        PRODUCT_TOMATO.increaseDecreaseQuantity(10);
        PRODUCT_LETTUCE.increaseDecreaseQuantity(2);
        PRODUCT_APPLE.increaseDecreaseQuantity(-4);

        assertNotEquals(PRODUCT_TOMATO, shopService.getProduct(PRODUCT_TOMATO.id));
        assertNotEquals(PRODUCT_LETTUCE, shopService.getProduct(PRODUCT_LETTUCE.id));
        assertNotEquals(PRODUCT_APPLE, shopService.getProduct(PRODUCT_APPLE.id));

        shopService.updateProduct(PRODUCT_TOMATO);
        shopService.updateProduct(PRODUCT_LETTUCE);
        shopService.updateProduct(PRODUCT_APPLE);

        assertEquals(PRODUCT_TOMATO, shopService.getProduct(PRODUCT_TOMATO.id));
        assertEquals(PRODUCT_LETTUCE, shopService.getProduct(PRODUCT_LETTUCE.id));
        assertEquals(PRODUCT_APPLE, shopService.getProduct(PRODUCT_APPLE.id));
    }

    @Test
    public void addUpdateResetProduct ()
    {
        ShopService shopService = new ShopService(new InMemoryCustomerRepository(), new InMemoryProductRepository(), new InMemoryOrderRepository());

        Product PRODUCT_TOMATO = new Product(1, 5, 45, "Tomato", "Vegetables", "Spain", "A beautiful tomato", "http://google.com/tomato.jpg");
        Product PRODUCT_LETTUCE = new Product(2, 88, 2, "Lettuce", "Vegetables", "France", "A mound of lettuce", "http://google.com/lettuce.jpg");
        Product PRODUCT_APPLE = new Product(3, 20, 10, "Apple", "Fruit", "Asia", "Some delicious apples", "http://google.com/apple.jpg");

        shopService.addProduct(PRODUCT_TOMATO);
        shopService.addProduct(PRODUCT_LETTUCE);
        shopService.addProduct(PRODUCT_APPLE);

        PRODUCT_TOMATO.increaseDecreaseQuantity(2);
        assertNotEquals(PRODUCT_TOMATO, shopService.getProduct(PRODUCT_TOMATO.id));

        shopService.updateProduct(PRODUCT_TOMATO);
        assertEquals(PRODUCT_TOMATO, shopService.getProduct(PRODUCT_TOMATO.id));
    }

    @Test (expected = ShopServiceException.class)
    public void addRemoveProduct ()
    {
        ShopService shopService = new ShopService(new InMemoryCustomerRepository(), new InMemoryProductRepository(), new InMemoryOrderRepository());

        Product PRODUCT_TOMATO = new Product(1, 5, 45, "Tomato", "Vegetables", "Spain", "A beautiful tomato", "http://google.com/tomato.jpg");
        Product PRODUCT_LETTUCE = new Product(2, 88, 2, "Lettuce", "Vegetables", "France", "A mound of lettuce", "http://google.com/lettuce.jpg");
        Product PRODUCT_APPLE = new Product(3, 20, 10, "Apple", "Fruit", "Asia", "Some delicious apples", "http://google.com/apple.jpg");

        shopService.addProduct(PRODUCT_TOMATO);
        shopService.addProduct(PRODUCT_LETTUCE);
        shopService.addProduct(PRODUCT_APPLE);

        shopService.removeProduct(PRODUCT_TOMATO.id);
        shopService.getProduct(PRODUCT_TOMATO.id);
    }

    @Test
    public void addGetCustomer ()
    {
        ShopService shopService = new ShopService(new InMemoryCustomerRepository(), new InMemoryProductRepository(), new InMemoryOrderRepository());

        final Customer CUSTOMER_1 = new Customer("Kira", "password", "erik@erikwelander.se", "Erik", "Welander", "Järfälla", "987654321");
        final Customer CUSTOMER_2 = new Customer("Erik", "lol123", "erik.welander@hotmail.com", "Erik2", "Welander2", "Järfälla2", "9876543212");
        final Customer CUSTOMER_3 = new Customer("Erik2", "lol123456789", "erik.welander@hotmail.com", "Erik22", "Welander22", "Järfälla22", "98765432122");

        shopService.addCustomer(CUSTOMER_1);
        shopService.addCustomer(CUSTOMER_2);
        shopService.addCustomer(CUSTOMER_3);

        assertEquals(CUSTOMER_1, shopService.getCustomer(CUSTOMER_1.userName));
        assertEquals(CUSTOMER_2, shopService.getCustomer(CUSTOMER_2.userName));
        assertEquals(CUSTOMER_3, shopService.getCustomer(CUSTOMER_3.userName));
    }

    @Test (expected = ShopServiceException.class)
    public void addRemoveCustomer ()
    {
        ShopService shopService = new ShopService(new InMemoryCustomerRepository(), new InMemoryProductRepository(), new InMemoryOrderRepository());

        final Customer CUSTOMER_1 = new Customer("Kira", "password", "erik@erikwelander.se", "Erik", "Welander", "Järfälla", "987654321");
        final Customer CUSTOMER_2 = new Customer("Erik", "lol123", "erik.welander@hotmail.com", "Erik2", "Welander2", "Järfälla2", "9876543212");
        final Customer CUSTOMER_3 = new Customer("Erik2", "lol123456789", "erik.welander@hotmail.com", "Erik22", "Welander22", "Järfälla22", "98765432122");

        shopService.addCustomer(CUSTOMER_1);
        shopService.addCustomer(CUSTOMER_2);
        shopService.addCustomer(CUSTOMER_3);

        assertEquals(CUSTOMER_1, shopService.getCustomer(CUSTOMER_1.userName));
        assertEquals(CUSTOMER_2, shopService.getCustomer(CUSTOMER_2.userName));
        assertEquals(CUSTOMER_3, shopService.getCustomer(CUSTOMER_3.userName));

        shopService.removeCustomer(CUSTOMER_1.userName);
        shopService.getCustomer(CUSTOMER_1.userName);
    }

    @Test
    public void addGetUpdateCustomer ()
    {
        ShopService shopService = new ShopService(new InMemoryCustomerRepository(), new InMemoryProductRepository(), new InMemoryOrderRepository());

        //!!NOTICE!! Java will automatically make a reference to the customer inside the shopservice unless we trick it by having a copy.
        //Use an identical copy to avoid automatic referencing, since the key (the name) is the same the logic will carry on as intended.

        final Customer CUSTOMER_1 = new Customer("Kira", "password", "erik@erikwelander.se", "Erik", "Welander", "Järfälla", "987654321");
        final Customer CUSTOMER_2 = new Customer("Erik", "lol123", "erik.welander@hotmail.com", "Erik2", "Welander2", "Järfälla2", "9876543212");
        final Customer CUSTOMER_3 = new Customer("Erik2", "lol123456789", "erik.welander@hotmail.com", "Erik22", "Welander22", "Järfälla22", "98765432122");

        final Customer CUSTOMER_1_1 = new Customer("Kira", "password", "erik@erikwelander.se", "Erik", "Welander", "Järfälla", "987654321");
        final Customer CUSTOMER_2_2 = new Customer("Erik", "lol123", "erik.welander@hotmail.com", "Erik2", "Welander2", "Järfälla2", "9876543212");
        final Customer CUSTOMER_3_3 = new Customer("Erik2", "lol123456789", "erik.welander@hotmail.com", "Erik22", "Welander22", "Järfälla22", "98765432122");

        Product PRODUCT_TOMATO = new Product(1, 5, 45, "Tomato", "Vegetables", "Spain", "A beautiful tomato", "http://google.com/tomato.jpg");
        Product PRODUCT_LETTUCE = new Product(2, 88, 2, "Lettuce", "Vegetables", "France", "A mound of lettuce", "http://google.com/lettuce.jpg");
        Product PRODUCT_APPLE = new Product(3, 20, 10, "Apple", "Fruit", "Asia", "Some delicious apples", "http://google.com/apple.jpg");

        shopService.addProduct(PRODUCT_TOMATO);
        shopService.addProduct(PRODUCT_LETTUCE);
        shopService.addProduct(PRODUCT_APPLE);

        shopService.addCustomer(CUSTOMER_1);
        shopService.addCustomer(CUSTOMER_2);
        shopService.addCustomer(CUSTOMER_3);

        CUSTOMER_1_1.addToShoppingCart(PRODUCT_TOMATO.id);
        CUSTOMER_1_1.addToShoppingCart(PRODUCT_LETTUCE.id);
        CUSTOMER_1_1.addToShoppingCart(PRODUCT_APPLE.id);
        CUSTOMER_1_1.addToShoppingCart(PRODUCT_APPLE.id);

        CUSTOMER_2_2.addToShoppingCart(PRODUCT_TOMATO.id);
        CUSTOMER_2_2.addToShoppingCart(PRODUCT_TOMATO.id);

        CUSTOMER_3_3.addToShoppingCart(PRODUCT_TOMATO.id);
        CUSTOMER_3_3.addToShoppingCart(PRODUCT_LETTUCE.id);
        CUSTOMER_3_3.addToShoppingCart(PRODUCT_APPLE.id);

        Customer cu = shopService.getCustomer(CUSTOMER_1.userName);
        assertNotEquals(CUSTOMER_1_1, shopService.getCustomer(CUSTOMER_1.userName));
        assertNotEquals(CUSTOMER_2_2, shopService.getCustomer(CUSTOMER_2.userName));
        assertNotEquals(CUSTOMER_3_3, shopService.getCustomer(CUSTOMER_3.userName));

        shopService.addProductToCustomer(PRODUCT_TOMATO.id, CUSTOMER_1.userName, 1);
        shopService.addProductToCustomer(PRODUCT_LETTUCE.id, CUSTOMER_1.userName, 1);
        shopService.addProductToCustomer(PRODUCT_APPLE.id, CUSTOMER_1.userName, 2);

        shopService.addProductToCustomer(PRODUCT_TOMATO.id, CUSTOMER_2.userName, 2);

        shopService.addProductToCustomer(PRODUCT_TOMATO.id, CUSTOMER_3.userName, 1);
        shopService.addProductToCustomer(PRODUCT_LETTUCE.id, CUSTOMER_3.userName, 1);
        shopService.addProductToCustomer(PRODUCT_APPLE.id, CUSTOMER_3.userName, 1);


        assertEquals(CUSTOMER_1, shopService.getCustomer(CUSTOMER_1.userName));
        assertEquals(CUSTOMER_2, shopService.getCustomer(CUSTOMER_2.userName));
        assertEquals(CUSTOMER_3, shopService.getCustomer(CUSTOMER_3.userName));
    }

    @Test
    public void addGetOrders ()
    {
        ShopService shopService = new ShopService(new InMemoryCustomerRepository(), new InMemoryProductRepository(), new InMemoryOrderRepository());

        final Customer CUSTOMER_1 = new Customer("Kira", "password", "erik@erikwelander.se", "Erik", "Welander", "Järfälla", "987654321");
        final Customer CUSTOMER_2 = new Customer("Erik", "lol123", "erik.welander@hotmail.com", "Erik2", "Welander2", "Järfälla2", "9876543212");
        final Customer CUSTOMER_3 = new Customer("Erik2", "lol123456789", "erik.welander@hotmail.com", "Erik22", "Welander22", "Järfälla22", "98765432122");

        shopService.addCustomer(CUSTOMER_1);
        shopService.addCustomer(CUSTOMER_2);
        shopService.addCustomer(CUSTOMER_3);

        Product PRODUCT_TOMATO = new Product(1, 5, 45, "Tomato", "Vegetables", "Spain", "A beautiful tomato", "http://google.com/tomato.jpg");
        Product PRODUCT_LETTUCE = new Product(2, 88, 2, "Lettuce", "Vegetables", "France", "A mound of lettuce", "http://google.com/lettuce.jpg");
        Product PRODUCT_APPLE = new Product(3, 20, 10, "Apple", "Fruit", "Asia", "Some delicious apples", "http://google.com/apple.jpg");

        shopService.addProduct(PRODUCT_TOMATO);
        shopService.addProduct(PRODUCT_LETTUCE);
        shopService.addProduct(PRODUCT_APPLE);

        ArrayList<Integer> empty = new ArrayList<>();
        assertEquals(empty, shopService.getOrdersFromUser(CUSTOMER_1.userName));
        assertEquals(empty, shopService.getOrdersFromUser(CUSTOMER_2.userName));
        assertEquals(empty, shopService.getOrdersFromUser(CUSTOMER_3.userName));

        shopService.addProductToCustomer(PRODUCT_TOMATO.id, CUSTOMER_1.userName, 1);
        shopService.addProductToCustomer(PRODUCT_LETTUCE.id, CUSTOMER_1.userName, 1);
        shopService.addProductToCustomer(PRODUCT_APPLE.id, CUSTOMER_1.userName, 2);

        shopService.addProductToCustomer(PRODUCT_TOMATO.id, CUSTOMER_2.userName, 2);

        shopService.addProductToCustomer(PRODUCT_TOMATO.id, CUSTOMER_3.userName, 1);
        shopService.addProductToCustomer(PRODUCT_LETTUCE.id, CUSTOMER_3.userName, 1);
        shopService.addProductToCustomer(PRODUCT_APPLE.id, CUSTOMER_3.userName, 1);


        assertEquals(CUSTOMER_1, shopService.getCustomer(CUSTOMER_1.userName));
        assertEquals(CUSTOMER_2, shopService.getCustomer(CUSTOMER_2.userName));
        assertEquals(CUSTOMER_3, shopService.getCustomer(CUSTOMER_3.userName));

        shopService.createOrder(CUSTOMER_1.userName);
        shopService.createOrder(CUSTOMER_2.userName);
        shopService.createOrder(CUSTOMER_3.userName);


        Order CUSTOMER_1_ORDER = new Order(1, CUSTOMER_1.userName, new ArrayList<Integer>(Arrays.asList(PRODUCT_TOMATO.id, PRODUCT_LETTUCE.id, PRODUCT_APPLE.id, PRODUCT_APPLE.id)), currentDate, null);
        Order CUSTOMER_2_ORDER = new Order(2, CUSTOMER_2.userName, new ArrayList<Integer>(Arrays.asList(PRODUCT_TOMATO.id, PRODUCT_TOMATO.id)), currentDate, null);
        Order CUSTOMER_3_ORDER = new Order(3, CUSTOMER_3.userName, new ArrayList<Integer>(Arrays.asList(PRODUCT_TOMATO.id, PRODUCT_LETTUCE.id, PRODUCT_APPLE.id)), currentDate, null);

        List<Order> ORDER_1_LIST = shopService.getOrdersFromUser(CUSTOMER_1.userName);
        List<Order> ORDER_2_LIST = shopService.getOrdersFromUser(CUSTOMER_2.userName);
        List<Order> ORDER_3_LIST = shopService.getOrdersFromUser(CUSTOMER_3.userName);

        assertEquals(new ArrayList<Order>(Arrays.asList(CUSTOMER_1_ORDER)), shopService.getOrdersFromUser(CUSTOMER_1.userName));
        assertEquals(new ArrayList<Order>(Arrays.asList(CUSTOMER_2_ORDER)), shopService.getOrdersFromUser(CUSTOMER_2.userName));
        assertEquals(new ArrayList<Order>(Arrays.asList(CUSTOMER_3_ORDER)), shopService.getOrdersFromUser(CUSTOMER_3.userName));
    }

    @Test
    public void addGetUpdateOrder ()
    {
        ShopService shopService = new ShopService(new InMemoryCustomerRepository(), new InMemoryProductRepository(), new InMemoryOrderRepository());

        final Customer CUSTOMER_1 = new Customer("Kira", "password", "erik@erikwelander.se", "Erik", "Welander", "Järfälla", "987654321");
        final Customer CUSTOMER_2 = new Customer("Erik", "lol123", "erik.welander@hotmail.com", "Erik2", "Welander2", "Järfälla2", "9876543212");
        final Customer CUSTOMER_3 = new Customer("Erik2", "lol123456789", "erik.welander@hotmail.com", "Erik22", "Welander22", "Järfälla22", "98765432122");

        shopService.addCustomer(CUSTOMER_1);
        shopService.addCustomer(CUSTOMER_2);
        shopService.addCustomer(CUSTOMER_3);

        Product PRODUCT_TOMATO = new Product(1, 5, 45, "Tomato", "Vegetables", "Spain", "A beautiful tomato", "http://google.com/tomato.jpg");
        Product PRODUCT_LETTUCE = new Product(2, 88, 2, "Lettuce", "Vegetables", "France", "A mound of lettuce", "http://google.com/lettuce.jpg");
        Product PRODUCT_APPLE = new Product(3, 20, 10, "Apple", "Fruit", "Asia", "Some delicious apples", "http://google.com/apple.jpg");

        shopService.addProduct(PRODUCT_TOMATO);
        shopService.addProduct(PRODUCT_LETTUCE);
        shopService.addProduct(PRODUCT_APPLE);

        shopService.addProductToCustomer(PRODUCT_TOMATO.id, CUSTOMER_1.userName, 1);
        shopService.addProductToCustomer(PRODUCT_LETTUCE.id, CUSTOMER_1.userName, 1);
        shopService.addProductToCustomer(PRODUCT_APPLE.id, CUSTOMER_1.userName, 2);

        shopService.createOrder(CUSTOMER_1.userName);

        Order CUSTOMER_1_ORDER = new Order(1, CUSTOMER_1.userName, new ArrayList<Integer>(Arrays.asList(PRODUCT_TOMATO.id, PRODUCT_LETTUCE.id, PRODUCT_APPLE.id, PRODUCT_APPLE.id)), currentDate, null);
        Order CUSTOMER_1_ORDER_UPDATED = new Order(1, CUSTOMER_1.userName, new ArrayList<Integer>(Arrays.asList(PRODUCT_TOMATO.id, PRODUCT_LETTUCE.id, PRODUCT_APPLE.id, PRODUCT_APPLE.id)), currentDate, currentDate);

        assertEquals(CUSTOMER_1_ORDER, shopService.getOrder(1));

        shopService.updateOrder(CUSTOMER_1_ORDER_UPDATED);

        assertNotEquals(CUSTOMER_1_ORDER, shopService.getOrder(1));
        assertEquals(CUSTOMER_1_ORDER_UPDATED, shopService.getOrder(1));
    }

    @Test (expected = ShopServiceException.class)
    public void addRemoveOrder ()
    {
        ShopService shopService = new ShopService(new InMemoryCustomerRepository(), new InMemoryProductRepository(), new InMemoryOrderRepository());

        final Customer CUSTOMER_1 = new Customer("Kira", "password", "erik@erikwelander.se", "Erik",
                "Welander", "Järfälla", "987654321");
        final Customer CUSTOMER_2 = new Customer("Erik", "lol123", "erik.welander@hotmail.com", "Erik2",
                "Welander2", "Järfälla2", "9876543212");
        final Customer CUSTOMER_3 = new Customer("Erik2", "lol123456789", "erik.welander@hotmail.com", "Erik22",
                "Welander22", "Järfälla22", "98765432122");

        shopService.addCustomer(CUSTOMER_1);
        shopService.addCustomer(CUSTOMER_2);
        shopService.addCustomer(CUSTOMER_3);

        Product PRODUCT_TOMATO = new Product(1, 5, 45, "Tomato", "Vegetables", "Spain", "A beautiful tomato", "http://google.com/tomato.jpg");
        Product PRODUCT_LETTUCE = new Product(2, 88, 2, "Lettuce", "Vegetables", "France", "A mound of lettuce", "http://google.com/lettuce.jpg");
        Product PRODUCT_APPLE = new Product(3, 20, 10, "Apple", "Fruit", "Asia", "Some delicious apples", "http://google.com/apple.jpg");

        shopService.addProduct(PRODUCT_TOMATO);
        shopService.addProduct(PRODUCT_LETTUCE);
        shopService.addProduct(PRODUCT_APPLE);

        shopService.addProductToCustomer(PRODUCT_TOMATO.id, CUSTOMER_1.userName, 1);
        shopService.addProductToCustomer(PRODUCT_LETTUCE.id, CUSTOMER_1.userName, 1);
        shopService.addProductToCustomer(PRODUCT_APPLE.id, CUSTOMER_1.userName, 2);

        shopService.createOrder(CUSTOMER_1.userName);

        Order CUSTOMER_1_ORDER = new Order(1, CUSTOMER_1.userName, new ArrayList<Integer>(Arrays.asList(PRODUCT_TOMATO.id, PRODUCT_LETTUCE.id, PRODUCT_APPLE.id, PRODUCT_APPLE.id)), currentDate, null);

        assertEquals(CUSTOMER_1_ORDER, shopService.getOrder(1));

        shopService.removeOrder(1);
        shopService.getOrder(1);
    }
}