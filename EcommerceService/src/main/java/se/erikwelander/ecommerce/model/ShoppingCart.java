/////////////////////////////////////////////////////////////////////////////
// Name:        ShoppingCart.java
// Encoding:	UTF-8
//
// Purpose:     Contains and manages a Customers shopping cart.
//              Products are represented as Integer ID values.
//
// Author:      Erik Welander (mail@erikwelander.se)
// Modified:    2016-06-21
// Copyright:   Erik Welander
// Licence:     Creative Commons "by-nc-nd"
/////////////////////////////////////////////////////////////////////////////
package se.erikwelander.ecommerce.model;

import se.erikwelander.ecommerce.exception.ModelException;

import java.util.ArrayList;
import java.util.Collections;

public final class ShoppingCart
{
    private final ArrayList<Integer> productIDS = new ArrayList<>();

    public ShoppingCart ()
    {
    }

    public void add (final int productId)
    {
        productIDS.add(productId);
    }

    public void remove (final int productId) throws ModelException
    {
        if (productIDS.contains(productId))
        {
            productIDS.remove(productId);
        }
        else
        {
            throw new ModelException("Cannot remove product with ID: " + productId + ": ID does not exist!");
        }
    }

    public void removeAll (final int productId) throws ModelException
    {
        if (!productIDS.removeAll(Collections.singleton(productId)))
        {
            throw new ModelException("Cannot remove product with ID: " + productId + ": ID does not exist!");
        }
    }

    public ArrayList<Integer> getAll ()
    {
        return productIDS;
    }

    public void empty ()
    {
        productIDS.clear();
    }

    @Override
    public boolean equals (final Object other)
    {
        if (other == this)
        {
            return true;
        }
        else if (other instanceof ShoppingCart)
        {
            final ShoppingCart otherCart = (ShoppingCart) other;
            if (this.getAll().equals(otherCart.getAll()))
            {
                return true;
            }
        }
        return false;
    }
}
