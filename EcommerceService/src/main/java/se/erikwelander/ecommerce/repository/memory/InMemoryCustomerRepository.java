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
import se.erikwelander.ecommerce.model.Customer;
import se.erikwelander.ecommerce.repository.CustomerRepository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class InMemoryCustomerRepository implements CustomerRepository
{
    private HashMap<String, Customer> customers = new HashMap<>();

    @Override
    public void addCustomer (final Customer customer) throws RepositoryException
    {
        if (customers.containsKey(customer.userName))
        {
            throw new RepositoryException("Could not add customer: Customer already exists");
        }
        customers.put(customer.userName, customer);
    }

    @Override
    public Customer getCustomer (final String userName) throws RepositoryException
    {
        if (customers.containsKey(userName))
        {
            return customers.get(userName);
        }
        throw new RepositoryException("Could not get customer: Customer does not exist");
    }

    @Override
    public List<Customer> getAllCustomers () throws RepositoryException
    {
        return new ArrayList<Customer>(customers.values());
    }

    @Override
    public void updateCustomer (final Customer customer) throws RepositoryException
    {
        if (customers.containsKey(customer.userName))
        {
            customers.replace(customer.userName, customer);
        }
        else
        {
            throw new RepositoryException("Could not update customer: Customer does not exist");
        }
    }

    @Override
    public void removeCustomer (final String userName) throws RepositoryException
    {
        if (customers.containsKey(userName))
        {
            customers.remove(userName);
        }
        else
        {
            throw new RepositoryException("Could not remove customer: Customer does not exist.");
        }
    }
}
