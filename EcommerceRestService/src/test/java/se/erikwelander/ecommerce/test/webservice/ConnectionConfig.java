/////////////////////////////////////////////////////////////////////////////
// Name:        ConnectionConfig.java
// Encoding:	UTF-8
//
// Purpose:     Contains necessary path information for testing.
//
// Author:      Erik Welander (mail@erikwelander.se)
// Modified:    2016-06-21
// Copyright:   Erik Welander
// Licence:     Creative Commons "by-nc-nd"
/////////////////////////////////////////////////////////////////////////////
package se.erikwelander.ecommerce.test.webservice;

public final class ConnectionConfig
{
    public static final String HOST_NAME = "localhost";
    public static final int HOST_PORT = 8080;
    public static final String PROJECT_NAME = "ecommerce";
    public static final String URL_BASE = "http://" + HOST_NAME + ":" + HOST_PORT + "/" + PROJECT_NAME;
    public static final String CUSTOMERS_URL = URL_BASE + "/customers";
    public static final String PRODUCTS_URL = URL_BASE + "/products";
    public static final String ORDERS_URL = URL_BASE + "/orders";
}
