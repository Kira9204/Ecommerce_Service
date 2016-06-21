/////////////////////////////////////////////////////////////////////////////
// Name:        AdminService.java
// Encoding:	UTF-8
//
// Purpose:     Service administration tool.
//
// Author:      Erik Welander (mail@erikwelander.se)
// Modified:    2016-06-21
// Copyright:   Erik Welander
// Licence:     Creative Commons "by-nc-nd"
/////////////////////////////////////////////////////////////////////////////
package se.erikwelander.ecommerce.webservice;

import se.erikwelander.ecommerce.exception.RepositoryException;
import se.erikwelander.ecommerce.repository.sql.SQLConnector;

import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;
import java.sql.PreparedStatement;
import java.sql.SQLException;

@Path ("ecommerce/admin")
public class AdminService extends WebShopService
{
    public AdminService () throws RepositoryException
    {
        super();
    }

    @POST
    public Response readAdminCommand (String command) throws RepositoryException, SQLException
    {
        if (command.equals("reset-repo"))
        {
            SQLConnector sql = new SQLConnector();
            PreparedStatement preparedStatement = sql.prepareStatement("TRUNCATE TABLE customer_cart;");
            sql.queryUpdate(preparedStatement);

            preparedStatement = sql.prepareStatement("TRUNCATE TABLE order_items;");
            sql.queryUpdate(preparedStatement);

            preparedStatement = sql.prepareStatement("TRUNCATE TABLE product;");
            sql.queryUpdate(preparedStatement);

            preparedStatement = sql.prepareStatement("TRUNCATE TABLE `order`;");
            sql.queryUpdate(preparedStatement);

            preparedStatement = sql.prepareStatement("TRUNCATE TABLE customer;");
            sql.queryUpdate(preparedStatement);

            return Response.ok("SQLRepo has been reset").build();

//			 Code below is for InMemory Repo reset
//
//			 shopService = new ShopService(new InMemoryCustomerRepository(), new InMemoryProductRepository(), new InMemoryOrderRepository());
//			 return Response.ok("InMemoryRepo has been reset").build();
        }
        return Response.status(400).entity("Invalid command received").build();
    }
}