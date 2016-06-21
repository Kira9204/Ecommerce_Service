/////////////////////////////////////////////////////////////////////////////
// Name:        SQLCustomerRepository.java
// Encoding:	UTF-8
//
// Purpose:     SQL implementation of ProductRepository.
//
// Author:      Erik Welander (mail@erikwelander.se)
// Modified:    2016-06-21
// Copyright:   Erik Welander
// Licence:     Creative Commons "by-nc-nd"
/////////////////////////////////////////////////////////////////////////////
package se.erikwelander.ecommerce.repository.sql;

import se.erikwelander.ecommerce.exception.RepositoryException;
import se.erikwelander.ecommerce.model.Product;
import se.erikwelander.ecommerce.repository.ProductRepository;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class SQLProductRepository implements ProductRepository
{
    private final String dbTable = "product";
    private final SQLConnector sqlConnector;

    public SQLProductRepository () throws RepositoryException
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
    public void addProduct (final Product product) throws RepositoryException
    {
        try
        {
            StringBuilder sqlQuery = new StringBuilder();
            sqlQuery.append("INSERT INTO " + DBInfo.database + "." + dbTable + " ");
            sqlQuery.append("(id_product, title, category, manufacturer, description, image, price, quantity) ");
            sqlQuery.append("VALUES(?,?,?,?,?,?,?,?);");

            PreparedStatement preparedStatement = sqlConnector.prepareStatement(sqlQuery.toString());
            preparedStatement.setInt(1, product.id);
            preparedStatement.setString(2, product.title);
            preparedStatement.setString(3, product.category);
            preparedStatement.setString(4, product.manufacturer);
            preparedStatement.setString(5, product.description);
            preparedStatement.setString(6, product.image);
            preparedStatement.setDouble(7, product.price);
            preparedStatement.setInt(8, product.getQuantity());

            sqlConnector.queryUpdate(preparedStatement);
        } catch (final SQLException exception)
        {
            throw new RepositoryException("Could not add Product to database!", exception);
        }
    }

    @Override
    public Product getProduct (final int productId) throws RepositoryException
    {
        ResultSet sqlResult;
        try
        {
            StringBuilder sqlQuery = new StringBuilder();
            sqlQuery.append("SELECT * FROM " + DBInfo.database + "." + dbTable + " ");
            sqlQuery.append("WHERE id_product = ?;");

            PreparedStatement preparedStatement = sqlConnector.prepareStatement(sqlQuery.toString());
            preparedStatement.setInt(1, productId);

            sqlResult = sqlConnector.queryResult(preparedStatement);
        } catch (final SQLException exception)
        {
            throw new RepositoryException("Failed to retrieve Product data from database!", exception);
        }

        try
        {
            sqlResult.next();
            final Product product = new Product(
                    productId,
                    sqlResult.getInt("quantity"),
                    sqlResult.getDouble("price"),
                    sqlResult.getString("title"),
                    sqlResult.getString("category"),
                    sqlResult.getString("manufacturer"),
                    sqlResult.getString("description"),
                    sqlResult.getString("image")
            );
            sqlResult.close();
            return product;
        } catch (final SQLException exception)
        {
            throw new RepositoryException("Failed to construct Product from database! " + exception.getMessage(), exception);
        }
    }

    @Override
    public List<Product> getAllproducts () throws RepositoryException
    {
        ResultSet sqlResult;
        try
        {
            final String sqlQuery = "SELECT * FROM " + DBInfo.database + "." + dbTable + ";";

            PreparedStatement preparedStatement = sqlConnector.prepareStatement(sqlQuery);

            sqlResult = sqlConnector.queryResult(preparedStatement);
        } catch (final SQLException exception)
        {
            throw new RepositoryException("Could not fetch all Products from database!", exception);
        }

        try
        {
            final List<Product> productList = new ArrayList<>();
            while (sqlResult.next())
            {
                final Product product = new Product(
                        sqlResult.getInt("id_product"),
                        sqlResult.getInt("quantity"),
                        sqlResult.getDouble("price"),
                        sqlResult.getString("title"),
                        sqlResult.getString("category"),
                        sqlResult.getString("manufacturer"),
                        sqlResult.getString("description"),
                        sqlResult.getString("image")
                );
                productList.add(product);
            }
            sqlResult.close();
            return productList;
        } catch (final SQLException exception)
        {
            throw new RepositoryException("Could not parse Products in database!", exception);
        }
    }

    @Override
    public final int getHighestProductId () throws RepositoryException
    {
        ResultSet sqlResult;
        try
        {
            final String sqlQuery = "SELECT MAX(id_product) FROM " + DBInfo.database + "." + dbTable;

            PreparedStatement preparedStatement = sqlConnector.prepareStatement(sqlQuery);

            sqlResult = sqlConnector.queryResult(preparedStatement);
        } catch (final SQLException exception)
        {
            throw new RepositoryException("Could not get query MAX Product ID!", exception);
        }

        try
        {
            sqlResult.next();
            final int highestProductID = sqlResult.getInt(1);
            sqlResult.close();
            return highestProductID;
        } catch (final SQLException exception)
        {
            throw new RepositoryException("Could not parse MAX(id_product) Product database!", exception);
        }
    }


    @Override
    public void updateProduct (final Product product) throws RepositoryException
    {
        try
        {
            StringBuilder sqlQuery = new StringBuilder();
            sqlQuery.append("UPDATE " + DBInfo.database + "." + dbTable + " SET ");
            sqlQuery.append("title = ?, ");
            sqlQuery.append("category = ?, ");
            sqlQuery.append("manufacturer = ?, ");
            sqlQuery.append("description = ?, ");
            sqlQuery.append("image = ?, ");
            sqlQuery.append("price = ?, ");
            sqlQuery.append("quantity = ? ");
            sqlQuery.append("WHERE id_product = ?;");

            PreparedStatement preparedStatement = sqlConnector.prepareStatement(sqlQuery.toString());
            preparedStatement.setString(1, product.title);
            preparedStatement.setString(2, product.category);
            preparedStatement.setString(3, product.manufacturer);
            preparedStatement.setString(4, product.description);
            preparedStatement.setString(5, product.image);
            preparedStatement.setDouble(6, product.price);
            preparedStatement.setInt(7, product.getQuantity());
            preparedStatement.setInt(8, product.id);

            sqlConnector.queryUpdate(preparedStatement);
        } catch (final SQLException exception)
        {
            throw new RepositoryException("Could not query Product update!", exception);
        }
    }

    @Override
    public void productsUpdateQuantity (final List<Integer> productIDs, final int quantityChange) throws RepositoryException
    {
        Product product[] = new Product[productIDs.size()];
        for (int i = 0; i < productIDs.size(); i++)
        {
            product[i] = getProduct(productIDs.get(i));
        }

        StringBuilder sqlQuery = new StringBuilder();
        for (int i = 0; i < productIDs.size(); i++)
        {
            sqlQuery.append("UPDATE " + DBInfo.database + "." + dbTable + " SET ");
            sqlQuery.append("quantity = ? ");
            sqlQuery.append("WHERE id_product = ?;");

            try
            {
                PreparedStatement preparedStatement = sqlConnector.prepareStatement(sqlQuery.toString());
                preparedStatement.setInt(1, product[i].getQuantity());
                preparedStatement.setInt(2, product[i].id);

                sqlConnector.queryUpdate(preparedStatement);
                sqlQuery.delete(0, sqlQuery.length());
            } catch (final SQLException exception)
            {
                throw new RepositoryException("Could not query Product quantity update!", exception);
            }
        }
    }

    @Override
    public void removeProduct (final int productId) throws RepositoryException
    {
        try
        {
            final String sqlQuery = "DELETE FROM " + DBInfo.database + "." + dbTable + " WHERE id_product = ?;";
            PreparedStatement preparedStatement = sqlConnector.prepareStatement(sqlQuery);
            preparedStatement.setInt(1, productId);

            sqlConnector.queryUpdate(preparedStatement);
        } catch (final SQLException exception)
        {
            throw new RepositoryException("Could not query removal of Product!", exception);
        }
    }
}
