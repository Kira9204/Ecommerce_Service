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
package se.erikwelander.ecommerce.repository.memory;

import se.erikwelander.ecommerce.exception.RepositoryException;
import se.erikwelander.ecommerce.model.Product;
import se.erikwelander.ecommerce.repository.ProductRepository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class InMemoryProductRepository implements ProductRepository
{
    private HashMap<Integer, Product> products = new HashMap<>();

    @Override
    public void addProduct (final Product product) throws RepositoryException
    {
        if (products.containsKey(product.id))
        {
            throw new RepositoryException("Cannot get add: Product with id " + product.id + " already exist in repository");
        }
        products.put(product.id, product);
    }

    @Override
    public Product getProduct (final int productId) throws RepositoryException
    {
        if (products.containsKey(productId))
        {
            return products.get(productId);
        }
        throw new RepositoryException("Cannot get product: product with id " + productId + " does not exist in repository");
    }

    @Override
    public List<Product> getAllproducts () throws RepositoryException
    {
        return new ArrayList<Product>(products.values());
    }

    @Override
    public int getHighestProductId () throws RepositoryException
    {
        return this.products.size();
    }

    @Override
    public void updateProduct (final Product product) throws RepositoryException
    {
        this.products.replace(product.id, product);
    }

    @Override
    public void productsUpdateQuantity (List<Integer> productIDs, int quantityChange) throws RepositoryException
    {
        for (int productId : productIDs)
        {
            this.products.get(productId).increaseDecreaseQuantity(quantityChange);
        }
    }

    @Override
    public void removeProduct (int productId) throws RepositoryException
    {
        this.products.remove(productId);
    }
}
