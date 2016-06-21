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
package se.erikwelander.ecommerce.repository.memory;

import se.erikwelander.ecommerce.exception.RepositoryException;
import se.erikwelander.ecommerce.model.Order;
import se.erikwelander.ecommerce.repository.OrderRepository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class InMemoryOrderRepository implements OrderRepository
{
    private HashMap<Integer, Order> orders = new HashMap<>();

    @Override
    public void addOrder (final Order order) throws RepositoryException
    {
        if (this.orders.containsKey(order.id))
        {
            throw new RepositoryException("Could not add order: Order already exists in repository.");
        }
        this.orders.put(order.id, order);
    }

    @Override
    public Order getOrder (final int orderId) throws RepositoryException
    {
        if (orders.containsKey(orderId))
        {
            return orders.get(orderId);
        }
        throw new RepositoryException("Cannot get order: Order does not exist in repository.");
    }

    @Override
    public void removeOrder (final int orderId) throws RepositoryException
    {
        if (orders.containsKey(orderId))
        {
            orders.remove(orderId);
        }
        else
        {
            throw new RepositoryException("Could not remove order: Order does not exist in repository:");
        }
    }

    @Override
    public List<Order> getAllOrders (final String customerUsername) throws RepositoryException
    {
        ArrayList<Order> orderList = new ArrayList<>();
        for (Order order : orders.values())
        {
            if (order.customerUserName.equals(customerUsername))
            {
                orderList.add(order);
            }
        }
        if (orderList.isEmpty())
        {
            throw new RepositoryException("No orders for this user");
        }
        return orderList;
    }

    @Override
    public int getHighestOrderId () throws RepositoryException
    {
        return orders.size();
    }

    @Override
    public void updateOrder (final Order order) throws RepositoryException
    {
        if (orders.containsKey(order.id))
        {
            orders.replace(order.id, order);
        }
        else
        {
            throw new RepositoryException("No order with this ID exists in repository");
        }
    }
}
