/////////////////////////////////////////////////////////////////////////////
// Name:        OrderRepository.java
// Encoding:	UTF-8
//
// Purpose:     Implementation interface for Customer Orders.
//
// Author:      Erik Welander (mail@erikwelander.se)
// Modified:    2016-06-21
// Copyright:   Erik Welander
// Licence:     Creative Commons "by-nc-nd"
/////////////////////////////////////////////////////////////////////////////
package se.erikwelander.ecommerce.repository;

import se.erikwelander.ecommerce.exception.RepositoryException;
import se.erikwelander.ecommerce.model.Order;

import java.util.List;

public interface OrderRepository
{
    void addOrder (final Order order) throws RepositoryException;

    Order getOrder (final int orderId) throws RepositoryException;

    void removeOrder (final int orderId) throws RepositoryException;

    List<Order> getAllOrders (final String customerUsername) throws RepositoryException;

    int getHighestOrderId () throws RepositoryException;

    void updateOrder (final Order order) throws RepositoryException;
}
