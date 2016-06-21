/////////////////////////////////////////////////////////////////////////////
// Name:        SQLCustomerRepository.java
// Encoding:	UTF-8
//
// Purpose:     SQL implementation of CustomerRepository.
//
// Author:      Erik Welander (mail@erikwelander.se)
// Modified:    2016-06-21
// Copyright:   Erik Welander
// Licence:     Creative Commons "by-nc-nd"
/////////////////////////////////////////////////////////////////////////////
package se.erikwelander.ecommerce.repository.sql;

import se.erikwelander.ecommerce.exception.RepositoryException;
import se.erikwelander.ecommerce.model.Customer;
import se.erikwelander.ecommerce.repository.CustomerRepository;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class SQLCustomerRepository implements CustomerRepository
{
    private final SQLConnector sqlConnector;
    private final String dbCustomer = "customer",
            dbCustomerItems = "customer_cart";

    public SQLCustomerRepository () throws RepositoryException
    {
        try
        {
            sqlConnector = new SQLConnector();
        } catch (final SQLException exception)
        {
            throw new RepositoryException("Could not construct SQLCustomer: Could not construct database object", exception);
        }
    }

    @Override
    public void addCustomer (final Customer customer) throws RepositoryException
    {
        try
        {
            StringBuilder sqlQuery = new StringBuilder();
            sqlQuery.append("INSERT INTO " + DBInfo.database + "." + dbCustomer + " ");
            sqlQuery.append("(user_name, password, email, first_name, last_name, address, phone) ");
            sqlQuery.append("VALUES(?,?,?,?,?,?,?);");

            PreparedStatement preparedStatement = sqlConnector.prepareStatement(sqlQuery.toString());
            preparedStatement.setString(1, customer.userName);
            preparedStatement.setString(2, customer.password);
            preparedStatement.setString(3, customer.email);
            preparedStatement.setString(4, customer.firstName);
            preparedStatement.setString(5, customer.lastName);
            preparedStatement.setString(6, customer.address);
            preparedStatement.setString(7, customer.phoneNumber);

            sqlConnector.queryUpdate(preparedStatement);
        } catch (final SQLException exception)
        {
            throw new RepositoryException("Could not add Customer to database!", exception);
        }

        try
        {
            final ArrayList<Integer> customerShoppingCartItems = customer.getAllShoppingCartItems();
            final String userName = customer.userName;
            StringBuilder sqlQuery = new StringBuilder();
            for (final int itemId : customerShoppingCartItems)
            {
                sqlQuery.append("INSERT INTO " + DBInfo.database + "." + dbCustomerItems + " ");
                sqlQuery.append("(id_item, user_name) ");
                sqlQuery.append("VALUES(?,?);");

                PreparedStatement preparedStatement = sqlConnector.prepareStatement(sqlQuery.toString());
                preparedStatement.setInt(1, itemId);
                preparedStatement.setString(2, userName);

                sqlConnector.queryUpdate(preparedStatement);

                sqlQuery.delete(0, sqlQuery.length());
            }
        } catch (final SQLException exception)
        {
            throw new RepositoryException("Could not add Shopping cart IDs to Customer!" + exception.getMessage(), exception);
        }
    }

    @Override
    public Customer getCustomer (final String userName) throws RepositoryException
    {
        ResultSet sqlResult;
        try
        {
            StringBuilder sqlQuery = new StringBuilder();
            sqlQuery.append("SELECT * FROM " + DBInfo.database + "." + dbCustomer + " ");
            sqlQuery.append("WHERE user_name = ?;");

            PreparedStatement preparedStatement = sqlConnector.prepareStatement(sqlQuery.toString());
            preparedStatement.setString(1, userName);

            sqlResult = sqlConnector.queryResult(preparedStatement);
        } catch (final SQLException exception)
        {
            throw new RepositoryException("Failed to retrieve Customer data from database!", exception);
        }


        final Customer customer;
        try
        {
            sqlResult.next();
            customer = new Customer(sqlResult.getString("user_name"),
                    sqlResult.getString("password"),
                    sqlResult.getString("email"),
                    sqlResult.getString("first_name"),
                    sqlResult.getString("last_name"),
                    sqlResult.getString("address"),
                    sqlResult.getString("phone"));
            sqlResult.close();
        } catch (final SQLException exception)
        {
            throw new RepositoryException("Failed to construct Customer from SQL result! " + exception.getMessage(), exception);
        }

        try
        {
            StringBuilder sqlQuery = new StringBuilder();
            sqlQuery.append("SELECT id_item FROM " + DBInfo.database + "." + dbCustomerItems + " ");
            sqlQuery.append("WHERE user_name = ?;");

            PreparedStatement preparedStatement = sqlConnector.prepareStatement(sqlQuery.toString());
            preparedStatement.setString(1, userName);

            sqlResult = sqlConnector.queryResult(preparedStatement);
        } catch (final SQLException exception)
        {
            throw new RepositoryException("Failed to retrieve Customer shopping cart data!", exception);
        }


        try
        {
            while (sqlResult.next())
            {
                customer.addToShoppingCart(sqlResult.getInt("id_item"));
            }
            sqlResult.close();
        } catch (final SQLException exception)
        {
            throw new RepositoryException("Failed to parse Shopping cart data!", exception);
        }

        return customer;
    }

    /*
    ResultSet sqlResult;
    try
    {
        StringBuilder sqlQuery = new StringBuilder();
        sqlQuery.append("SELECT * FROM " + DBInfo.database + "." + dbCustomer + " ");
        sqlQuery.append("WHERE user_name = ?;");

        PreparedStatement preparedStatement = sqlConnector.prepareStatement(sqlQuery.toString());
        preparedStatement.setString(1, userName);

        sqlResult = sqlConnector.queryResult(preparedStatement);
        if(!sqlResult.isBeforeFirst())
        {
            throw new RepositoryException("No matches for Customer found in database!\n SQL QUERY: " + preparedStatement.toString());
        }
    }
    catch (final SQLException exception)
    {
        throw new RepositoryException("Failed to retrieve Customer data from database!", exception);
    }


   final Customer customer;
    try
    {
        sqlResult.next();
        customer = new Customer(sqlResult.getString("user_name"),
                sqlResult.getString("password"),
                sqlResult.getString("email"),
                sqlResult.getString("first_name"),
                sqlResult.getString("last_name"),
                sqlResult.getString("address"),
                sqlResult.getString("phone"));
        sqlResult.close();
    }
    catch (final SQLException exception)
    {
        throw new RepositoryException("Failed to construct Customer from SQL result!", exception);
    }


    final int numCustomerCartItems;
    try
    {
        StringBuilder sqlQuery = new StringBuilder();
        sqlQuery.append("SELECT COUNT(id_item) FROM " + DBInfo.database + "." + dbCustomerItems + " ");
        sqlQuery.append("WHERE user_name = ?;");

        PreparedStatement preparedStatement = sqlConnector.prepareStatement(sqlQuery.toString());
        preparedStatement.setString(1, userName);

        sqlResult = sqlConnector.queryResult(preparedStatement);
        if (!sqlResult.isBeforeFirst())
        {
            throw new RepositoryException("No matches COUNT(id_item)In Customer shopping cart!\nSQL QUERY: " + preparedStatement.toString());
        }
    }
    catch (final SQLException exception)
    {
        throw new RepositoryException("Failed to count items in Customer Shopping Cart!", exception);
    }

    try
    {
        sqlResult.next();
        numCustomerCartItems = sqlResult.getInt(1);
        sqlResult.close();
    }
    catch (final SQLException exception)
    {
        throw new RepositoryException("Failed to parse count items in Customer Shopping Cart!", exception);
    }


    try
    {
        StringBuilder sqlQuery = new StringBuilder();
        sqlQuery.append("SELECT id_item FROM " + DBInfo.database + "." + dbCustomerItems + " ");
        sqlQuery.append("WHERE user_name = ?;");

        PreparedStatement preparedStatement = sqlConnector.prepareStatement(sqlQuery.toString());
        preparedStatement.setString(1, userName);

        sqlResult = sqlConnector.queryResult(preparedStatement);
    }
    catch (final SQLException exception)
    {
        throw new RepositoryException("Failed to retrieve Customer shopping cart data!", exception);
    }


    try
    {
        for (int i = 0; i < numCustomerCartItems; i++)
        {
            sqlResult.next();
            customer.addToShoppingCart(sqlResult.getInt(1));
        }
        sqlResult.close();
    }
    catch (final SQLException exception)
    {
        throw new RepositoryException("Failed to parse Shopping cart data!", exception);
    }

    return customer;

}
*/
    @Override
    public List<Customer> getAllCustomers () throws RepositoryException
    {
        ResultSet sqlResult;
        try
        {
            final String sqlQuery = "SELECT user_name FROM " + DBInfo.database + "." + dbCustomer + ";";
            PreparedStatement preparedStatement = sqlConnector.prepareStatement(sqlQuery);

            sqlResult = sqlConnector.queryResult(preparedStatement);
        } catch (final SQLException exception)
        {
            throw new RepositoryException("Could not fetch all Customers from database!", exception);
        }

        List<Customer> customerList = new ArrayList<>();
        try
        {
            while (sqlResult.next())
            {
                customerList.add(getCustomer(sqlResult.getString("user_name")));
            }
            sqlResult.close();

        } catch (final SQLException exception)
        {
            throw new RepositoryException("Could not parse all Customer from database!", exception);
        }

        return customerList;
    }

    /*
    ResultSet sqlResult;
    final int numCustomers;
    try
    {
        final String sqlQuery = "SELECT COUNT(user_name) FROM " + DBInfo.database + "." + dbCustomer + ";";
        PreparedStatement preparedStatement = sqlConnector.prepareStatement(sqlQuery);

        sqlResult = sqlConnector.queryResult(preparedStatement);
        if (!sqlResult.isBeforeFirst())
        {
            throw new RepositoryException("No matches for user_name in Customers!\nSQL QUERY: " + sqlQuery);
        }
    }
    catch (final SQLException exception)
    {
        throw new RepositoryException("Could not count all Customers in database!", exception);
    }

    try
    {
        sqlResult.next();
        numCustomers = sqlResult.getInt(1);
        sqlResult.close();
    }
    catch (final SQLException exception)
    {
        throw new RepositoryException("Could not parse count value for all Customers in database!", exception);
    }


    try
    {
        final String sqlQuery = "SELECT user_name FROM " + DBInfo.database + "." + dbCustomer + ";";
        PreparedStatement preparedStatement = sqlConnector.prepareStatement(sqlQuery);

        sqlResult = sqlConnector.queryResult(preparedStatement);
        if (!sqlResult.isBeforeFirst())
        {
            throw new RepositoryException("No matches for user_name in Customers!\nSQL QUERY: " + sqlQuery);
        }
    }
    catch (final SQLException exception)
    {
        throw new RepositoryException("Could not fetch all Customers from database!", exception);
    }

    String allCustomerUserNames[] = new String[numCustomers];
    try
    {
        for (int i = 0; i < numCustomers; i++)
        {
            sqlResult.next();
            allCustomerUserNames[i] = sqlResult.getString(1);
        }
        sqlResult.close();
    }
    catch (final SQLException exception)
    {
        throw new RepositoryException("Could not parse all Customer from database!", exception);
    }


    List<Customer> customerList = new ArrayList<>();
    for (int i = 0; i < numCustomers; i++)
    {
        customerList.add(getCustomer(allCustomerUserNames[i]));
    }

    return customerList;

}

*/
    @Override
    public void updateCustomer (final Customer customer) throws RepositoryException
    {
        try
        {
            StringBuilder sqlQuery = new StringBuilder();
            sqlQuery.append("UPDATE " + DBInfo.database + "." + dbCustomer + " SET ");
            sqlQuery.append("password = ?, ");
            sqlQuery.append("email = ?, ");
            sqlQuery.append("first_name = ?, ");
            sqlQuery.append("last_name = ?, ");
            sqlQuery.append("address = ?, ");
            sqlQuery.append("phone = ? ");
            sqlQuery.append("WHERE user_name = ?;");

            PreparedStatement preparedStatement = sqlConnector.prepareStatement(sqlQuery.toString());
            preparedStatement.setString(1, customer.password);
            preparedStatement.setString(2, customer.email);
            preparedStatement.setString(3, customer.firstName);
            preparedStatement.setString(4, customer.lastName);
            preparedStatement.setString(5, customer.address);
            preparedStatement.setString(6, customer.phoneNumber);
            preparedStatement.setString(7, customer.userName);

            sqlConnector.queryUpdate(preparedStatement);
        } catch (final SQLException exception)
        {
            throw new RepositoryException("Could not query Customer update!", exception);
        }

        try
        {
            StringBuilder sqlQuery = new StringBuilder();
            sqlQuery.append("DELETE FROM " + DBInfo.database + "." + dbCustomerItems + " ");
            sqlQuery.append("WHERE user_name = ?;");

            PreparedStatement preparedStatement = sqlConnector.prepareStatement(sqlQuery.toString());
            preparedStatement.setString(1, customer.userName);

            sqlConnector.queryUpdate(preparedStatement);
        } catch (final SQLException exception)
        {
            throw new RepositoryException("Could not query Customer items deletion!", exception);
        }


        final ArrayList<Integer> customerShoppingCartIDs = customer.getAllShoppingCartItems();
        final String customerUserName = customer.userName;
        try
        {
            StringBuilder sqlQuery = new StringBuilder();
            for (int productId : customerShoppingCartIDs)
            {
                sqlQuery.append("INSERT INTO " + DBInfo.database + "." + dbCustomerItems + " ");
                sqlQuery.append("(id_item, user_name) ");
                sqlQuery.append("VALUES(?,?);");

                PreparedStatement preparedStatement = sqlConnector.prepareStatement(sqlQuery.toString());
                preparedStatement.setInt(1, productId);
                preparedStatement.setString(2, customerUserName);

                sqlConnector.queryUpdate(preparedStatement);
                sqlQuery.delete(0, sqlQuery.length());
            }
        } catch (final SQLException exception)
        {
            throw new RepositoryException("Could not add Shopping cart IDs to Customer!", exception);
        }
    }

    @Override
    public void removeCustomer (final String userName) throws RepositoryException
    {
        try
        {
            StringBuilder sqlQuery = new StringBuilder();
            sqlQuery.append("DELETE FROM " + DBInfo.database + "." + dbCustomer + " ");
            sqlQuery.append("WHERE user_name = ?;");

            PreparedStatement preparedStatement = sqlConnector.prepareStatement(sqlQuery.toString());
            preparedStatement.setString(1, userName);

            sqlConnector.queryUpdate(preparedStatement);
        } catch (final SQLException exception)
        {
            throw new RepositoryException("Could not query removal of Customer!", exception);
        }


        try
        {
            StringBuilder sqlQuery = new StringBuilder();
            sqlQuery.append("DELETE FROM " + DBInfo.database + "." + dbCustomerItems + " ");
            sqlQuery.append("WHERE user_name = ?;");

            PreparedStatement preparedStatement = sqlConnector.prepareStatement(sqlQuery.toString());
            preparedStatement.setString(1, userName);

            sqlConnector.queryUpdate(preparedStatement);
        } catch (final SQLException exception)
        {
            throw new RepositoryException("Could not query Customer Items deletion!", exception);
        }
    }
}
