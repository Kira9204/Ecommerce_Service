/////////////////////////////////////////////////////////////////////////////
// Name:        SQLCustomerRepository.java
// Encoding:	UTF-8
//
// Purpose:     SQL implementation of OrderRepository.
//
// Author:      Erik Welander (mail@erikwelander.se)
// Modified:    2016-06-21
// Copyright:   Erik Welander
// Licence:     Creative Commons "by-nc-nd"
/////////////////////////////////////////////////////////////////////////////
package se.erikwelander.ecommerce.repository.sql;

import se.erikwelander.ecommerce.exception.RepositoryException;
import se.erikwelander.ecommerce.model.Order;
import se.erikwelander.ecommerce.repository.OrderRepository;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class SQLOrderRepository implements OrderRepository
{
    private final SQLConnector sqlConnector;
    private final String dbOrder = "order",
            dbOrderItems = "order_items";
    private final SimpleDateFormat sqlDateFormat = new SimpleDateFormat("yyyy-MM-dd");

    public SQLOrderRepository () throws RepositoryException
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
    public void addOrder (final Order order) throws RepositoryException
    {
        try
        {
            StringBuilder sqlQuery = new StringBuilder();
            sqlQuery.append("INSERT INTO " + DBInfo.database + ".`" + dbOrder + "` ");
            sqlQuery.append("(id_order, customer_name, created, shipped) ");
            sqlQuery.append("VALUES(?,?,?,?);");

            PreparedStatement preparedStatement = sqlConnector.prepareStatement(sqlQuery.toString());
            preparedStatement.setInt(1, order.id);
            preparedStatement.setString(2, order.customerUserName);
            preparedStatement.setDate(3, order.getDateCreated());
            preparedStatement.setDate(4, null);

            sqlConnector.queryUpdate(preparedStatement);
        } catch (final SQLException exception)
        {
            throw new RepositoryException("Could not add Order values to database!", exception);
        }


        final ArrayList<Integer> orderProductIDs = order.getAllProductIDs();
        try
        {
            StringBuilder sqlQuery = new StringBuilder();
            final int orderId = order.id;
            for (int productId : orderProductIDs)
            {
                sqlQuery.append("INSERT INTO " + DBInfo.database + "." + dbOrderItems + " ");
                sqlQuery.append("(id_order, id_product) ");
                sqlQuery.append("VALUES(?,?);");

                PreparedStatement preparedStatement = sqlConnector.prepareStatement(sqlQuery.toString());
                preparedStatement.setInt(1, orderId);
                preparedStatement.setInt(2, productId);

                sqlConnector.queryUpdate(preparedStatement);
                sqlQuery.delete(0, sqlQuery.length());
            }
        } catch (final SQLException exception)
        {
            throw new RepositoryException("Could not add OrderID values to database!", exception);
        }
    }

    @Override
    public Order getOrder (final int orderId) throws RepositoryException
    {
        final String customerUserName;
        final Date dateOrderCreated;
        Date dateOrderShipped;
        ResultSet sqlResult;

        try
        {
            StringBuilder sqlQuery = new StringBuilder();
            sqlQuery.append("SELECT customer_name, created, shipped FROM " + DBInfo.database + ".`" + dbOrder + "` ");
            sqlQuery.append("WHERE id_order = ?;");

            PreparedStatement preparedStatement = sqlConnector.prepareStatement(sqlQuery.toString());
            preparedStatement.setInt(1, orderId);

            sqlResult = sqlConnector.queryResult(preparedStatement);
        } catch (final SQLException exception)
        {
            throw new RepositoryException("Could not query database for Order info!", exception);
        }

        try
        {
            sqlResult.next();
            customerUserName = sqlResult.getString("customer_name");
            dateOrderCreated = sqlResult.getDate("created");
            dateOrderShipped = sqlResult.getDate("shipped");
            sqlResult.close();
        } catch (final SQLException exception)
        {
            throw new RepositoryException("Could not parse SQL values!", exception);
        }


        try
        {
            StringBuilder sqlQuery = new StringBuilder();
            sqlQuery.append("SELECT id_product FROM " + DBInfo.database + "." + dbOrderItems + " ");
            sqlQuery.append("WHERE id_order = ?;");

            PreparedStatement preparedStatement = sqlConnector.prepareStatement(sqlQuery.toString());
            preparedStatement.setInt(1, orderId);

            sqlResult = sqlConnector.queryResult(preparedStatement);
        } catch (final SQLException exception)
        {
            throw new RepositoryException("Could not query CustomerUserName from Order!", exception);
        }

        try
        {
            final ArrayList<Integer> productIds = new ArrayList<>();
            while (sqlResult.next())
            {
                productIds.add(sqlResult.getInt(1));
            }
            sqlResult.close();

            return new Order(orderId, customerUserName, productIds, dateOrderCreated, dateOrderShipped);
        } catch (final SQLException exception)
        {
            throw new RepositoryException("Could not construct Order from database!", exception);
        }
    }

    @Override
    public void removeOrder (final int orderId) throws RepositoryException
    {
        ResultSet sqlResult;
        try
        {
            StringBuilder sqlQuery = new StringBuilder();
            sqlQuery.append("DELETE FROM " + DBInfo.database + "." + dbOrderItems + " ");
            sqlQuery.append("WHERE id_order = ?;");

            PreparedStatement preparedStatement = sqlConnector.prepareStatement(sqlQuery.toString());
            preparedStatement.setInt(1, orderId);

            sqlConnector.queryUpdate(preparedStatement);
        } catch (final SQLException exception)
        {
            throw new RepositoryException("Could not query removal of OrderItems!", exception);
        }

        try
        {
            StringBuilder sqlQuery = new StringBuilder();
            sqlQuery.append("DELETE FROM " + DBInfo.database + "." + dbOrder + " ");
            sqlQuery.append("WHERE id_order = ?;");

            PreparedStatement preparedStatement = sqlConnector.prepareStatement(sqlQuery.toString());
            preparedStatement.setInt(1, orderId);

            sqlConnector.queryUpdate(preparedStatement);
        } catch (final SQLException exception)
        {
            throw new RepositoryException("Could not query removal of order!", exception);
        }
    }

    @Override
    public List<Order> getAllOrders (final String customerUsername) throws RepositoryException
    {
        ResultSet sqlResult;
        try
        {
            StringBuilder sqlQuery = new StringBuilder();
            sqlQuery.append("SELECT id_order FROM " + DBInfo.database + ".`" + dbOrder + "` ");
            sqlQuery.append("WHERE customer_name = ?;");

            PreparedStatement preparedStatement = sqlConnector.prepareStatement(sqlQuery.toString());
            preparedStatement.setString(1, customerUsername);

            sqlResult = sqlConnector.queryResult(preparedStatement);
        } catch (final SQLException exception)
        {
            throw new RepositoryException("Could not fetch OrderIDs from database!", exception);
        }

        List<Integer> customerOrderIDs = new ArrayList<>();
        try
        {
            while (sqlResult.next())
            {
                customerOrderIDs.add(sqlResult.getInt("id_order"));
            }
            sqlResult.close();
        } catch (final SQLException exception)
        {
            throw new RepositoryException("Could not parse Order IDs from ResultSet!", exception);
        }

        List<Order> orderList = new ArrayList<>();
        for (int orderId : customerOrderIDs)
        {
            orderList.add(getOrder(orderId));
        }
        return orderList;
    }

    /*
    final int numOrders;
    ResultSet sqlResult;

    try
    {
        StringBuilder sqlQuery = new StringBuilder();
        sqlQuery.append("SELECT count(id_order) FROM " + DBInfo.database + ".`" + dbOrder + "` ");
        sqlQuery.append("WHERE customer_name = ?;");

        PreparedStatement preparedStatement = sqlConnector.prepareStatement(sqlQuery.toString());
        preparedStatement.setString(1, customerUsername);

        sqlResult = sqlConnector.queryResult(preparedStatement);
        if (!sqlResult.isBeforeFirst())
        {
            throw new RepositoryException("No matches for count(customer_name) in database!\nSQL QUERY: " + preparedStatement.toString());
        }
    }
    catch (final SQLException exception)
    {
        throw new RepositoryException("Could not fetch OrderIDs from database!", exception);
    }

    try
    {
        sqlResult.next();
        numOrders = sqlResult.getInt(1);
        sqlResult.close();
    }
    catch (final SQLException exception)
    {
        throw new RepositoryException("Could not parse number of Order IDs!", exception);
    }


    try
    {
        StringBuilder sqlQuery = new StringBuilder();
        sqlQuery.append("SELECT id_order FROM " + DBInfo.database + ".`" + dbOrder + "` ");
        sqlQuery.append("WHERE customer_name = ?;");

        PreparedStatement preparedStatement = sqlConnector.prepareStatement(sqlQuery.toString());
        preparedStatement.setString(1, customerUsername);

        sqlResult = sqlConnector.queryResult(preparedStatement);
        if (!sqlResult.isBeforeFirst())
        {
            throw new RepositoryException("No matches found customerUserName in database!\nSQL QUERY: " + preparedStatement.toString());
        }
    }
    catch (final SQLException exception)
    {
        throw new RepositoryException("Could not fetch OrderIDs from database!", exception);
    }

    final int customerOrderIDs[] = new int[numOrders];
    try
    {
        for (int i = 0; i < numOrders; i++)
        {
            sqlResult.next();
            customerOrderIDs[i] = sqlResult.getInt(1);
        }
        sqlResult.close();
    }
    catch (final SQLException exception)
    {
        throw new RepositoryException("Could not parse Order IDs from ResultSet!", exception);
    }


    List<Order> orderList = new ArrayList<>();
    for (int orderId: customerOrderIDs)
    {
        orderList.add(getOrder(orderId));
    }
    return orderList;

}
*/
    @Override
    public int getHighestOrderId () throws RepositoryException
    {
        ResultSet sqlResult;
        try
        {
            final String sqlQuery = "SELECT MAX(id_order) FROM " + DBInfo.database + ".`" + dbOrder + "` ";

            PreparedStatement preparedStatement = sqlConnector.prepareStatement(sqlQuery);

            sqlResult = sqlConnector.queryResult(preparedStatement);
        } catch (final SQLException exception)
        {
            throw new RepositoryException("Could not get query MAX(id_order)!", exception);
        }

        try
        {
            sqlResult.next();
            final int highestID = sqlResult.getInt(1);
            sqlResult.close();
            return highestID;
        } catch (final SQLException exception)
        {
            throw new RepositoryException("Could not parse MAX(id_order)!", exception);
        }
    }

    @Override
    public void updateOrder (final Order order) throws RepositoryException
    {

        final String sqlDateCreated = sqlDateFormat.format(order.getDateCreated());
        String sqlDateShipped;
        if (null == order.getDateShipped())
        {
            sqlDateShipped = sqlDateFormat.format(new Date(0L));
        }
        else
        {
            sqlDateShipped = sqlDateFormat.format(order.getDateShipped());
        }

        try
        {
            StringBuilder sqlQuery = new StringBuilder();
            sqlQuery.append("UPDATE " + DBInfo.database + ".`" + dbOrder + "` SET ");
            sqlQuery.append("created = ?, ");
            sqlQuery.append("shipped = ? ");
            sqlQuery.append("WHERE id_order = ?;");

            PreparedStatement preparedStatement = sqlConnector.prepareStatement(sqlQuery.toString());
            preparedStatement.setString(1, sqlDateCreated);
            preparedStatement.setString(2, sqlDateShipped);
            preparedStatement.setInt(3, order.id);

            sqlConnector.queryUpdate(preparedStatement);
        } catch (final SQLException exception)
        {
            throw new RepositoryException("Could not query Order update!", exception);
        }

        try
        {
            StringBuilder sqlQuery = new StringBuilder();
            sqlQuery.append("DELETE FROM " + DBInfo.database + "." + dbOrderItems + " ");
            sqlQuery.append("WHERE id_order = ?;");

            PreparedStatement preparedStatement = sqlConnector.prepareStatement(sqlQuery.toString());
            preparedStatement.setInt(1, order.id);

            sqlConnector.queryUpdate(preparedStatement);
        } catch (SQLException e)
        {
            throw new RepositoryException("Could not delete Order items!", e);
        }


        final ArrayList<Integer> productIDs = order.getAllProductIDs();
        try
        {
            StringBuilder sqlQuery = new StringBuilder();
            for (int productId : productIDs)
            {
                sqlQuery.append("INSERT INTO " + DBInfo.database + "." + dbOrderItems + " ");
                sqlQuery.append("(id_order, id_product) ");
                sqlQuery.append("VALUES(?,?);");

                PreparedStatement preparedStatement = sqlConnector.prepareStatement(sqlQuery.toString());
                preparedStatement.setInt(1, order.id);
                preparedStatement.setInt(2, productId);

                sqlConnector.queryUpdate(preparedStatement);
                sqlQuery.delete(0, sqlQuery.length());
            }
        } catch (final SQLException exception)
        {
            throw new RepositoryException("Could not add OrderIDs to Order! in database!", exception);
        }
    }
}
