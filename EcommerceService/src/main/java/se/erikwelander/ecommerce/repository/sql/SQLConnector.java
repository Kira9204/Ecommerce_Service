/////////////////////////////////////////////////////////////////////////////
// Name:        DBInfo.java
// Encoding:	UTF-8
//
// Purpose:     Manages database connection and issues database query's.
//
// Author:      Erik Welander (mail@erikwelander.se)
// Modified:    2016-06-21
// Copyright:   Erik Welander
// Licence:     Creative Commons "by-nc-nd"
/////////////////////////////////////////////////////////////////////////////
package se.erikwelander.ecommerce.repository.sql;

import java.sql.*;

public class SQLConnector
{
    private final String sqlDriver = "com.mysql.jdbc.Driver";
    private Connection sqlConnection;
    private Statement sqlStatement;

    public SQLConnector () throws SQLException
    {
        loadDriver();
        connect();
    }

    @Override
    protected void finalize () throws SQLException
    {
        disconnect();
    }

    private void loadDriver () throws SQLException
    {
        // Checks if the sqlDriver is available
        try
        {
            Class.forName(sqlDriver);
        } catch (final ClassNotFoundException exception)
        {
            throw new SQLException("Could not load database driver: " + exception.getMessage());
        }
    }

    private void connect () throws SQLException
    {
        try
        {
            sqlConnection = DriverManager.getConnection("jdbc:mysql://" + DBInfo.host + ":" + DBInfo.port + "/"
                    + DBInfo.database + "?characterEncoding=" + DBInfo.encoding, DBInfo.username, DBInfo.password);
            sqlStatement = sqlConnection.createStatement();
        } catch (final SQLException exception)
        {
            throw new SQLException("Could not connect to database: " + exception.getMessage());
        }
    }

    private void disconnect () throws SQLException
    {
        try
        {
            sqlStatement.close();
            sqlConnection.close();
        } catch (final SQLException exception)
        {
            throw new SQLException("Could not disconnect from database: " + exception.getMessage());
        }
    }

    public final PreparedStatement prepareStatement (final String sqlQuery) throws SQLException
    {
        try
        {
            final PreparedStatement preparedStatement = sqlConnection.prepareStatement(sqlQuery);
            return preparedStatement;
        } catch (final SQLException exception)
        {
            throw new SQLException("Could not prepare SQL statement: " + exception.getMessage());
        }
    }

    public void queryUpdate (final PreparedStatement preparedStatement) throws SQLException
    {
        try
        {
            preparedStatement.executeUpdate();
        } catch (final SQLException exception)
        {
            throw new SQLException("Error performing queryUpdate: " + exception.getMessage());
        }
    }

    public final ResultSet queryResult (final PreparedStatement preparedStatement) throws SQLException
    {
        try
        {
            final ResultSet resultSet = preparedStatement.executeQuery();
            return resultSet;
        } catch (final SQLException exception)
        {
            throw new SQLException("Error performing queryResult: " + exception.getMessage());
        }
    }
}
