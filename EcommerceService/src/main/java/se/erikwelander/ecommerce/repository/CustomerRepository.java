/////////////////////////////////////////////////////////////////////////////
// Name:        CustomerRepository.java
// Encoding:	UTF-8
//
// Purpose:     Implementation interface for Customer management.
//
// Author:      Erik Welander (mail@erikwelander.se)
// Modified:    2016-06-21
// Copyright:   Erik Welander
// Licence:     Creative Commons "by-nc-nd"
/////////////////////////////////////////////////////////////////////////////
package se.erikwelander.ecommerce.repository;

import se.erikwelander.ecommerce.exception.RepositoryException;
import se.erikwelander.ecommerce.model.Customer;

import java.util.List;

public interface CustomerRepository
{
    void addCustomer (final Customer customer) throws RepositoryException;

    Customer getCustomer (final String userName) throws RepositoryException;

    List<Customer> getAllCustomers () throws RepositoryException;

    void updateCustomer (final Customer customer) throws RepositoryException;

    void removeCustomer (final String userName) throws RepositoryException;
}
