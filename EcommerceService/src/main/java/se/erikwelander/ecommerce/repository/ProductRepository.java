/////////////////////////////////////////////////////////////////////////////
// Name:        ProductRepository.java
// Encoding:	UTF-8
//
// Purpose:     Implementation interface for Products in the store.
//
// Author:      Erik Welander (mail@erikwelander.se)
// Modified:    2016-06-21
// Copyright:   Erik Welander
// Licence:     Creative Commons "by-nc-nd"
/////////////////////////////////////////////////////////////////////////////
package se.erikwelander.ecommerce.repository;

import se.erikwelander.ecommerce.exception.RepositoryException;
import se.erikwelander.ecommerce.model.Product;

import java.util.List;

public interface ProductRepository
{
    void addProduct (final Product product) throws RepositoryException;

    Product getProduct (final int productId) throws RepositoryException;

    List<Product> getAllproducts () throws RepositoryException;

    int getHighestProductId () throws RepositoryException;

    void updateProduct (final Product product) throws RepositoryException;

    void productsUpdateQuantity (final List<Integer> productIDs, final int quantityChange) throws RepositoryException;

    void removeProduct (final int productId) throws RepositoryException;
}
