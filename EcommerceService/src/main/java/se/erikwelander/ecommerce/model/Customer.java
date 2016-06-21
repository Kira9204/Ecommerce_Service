/////////////////////////////////////////////////////////////////////////////
// Name:        Customer.java
// Encoding:	UTF-8
//
// Purpose:     Customer information container.
//
// Author:      Erik Welander (mail@erikwelander.se)
// Modified:    2016-06-21
// Copyright:   Erik Welander
// Licence:     Creative Commons "by-nc-nd"
/////////////////////////////////////////////////////////////////////////////
package se.erikwelander.ecommerce.model;

import se.erikwelander.ecommerce.exception.ModelException;

import java.util.ArrayList;

public final class Customer
{
    public final String userName,
            password,
            email,
            firstName,
            lastName,
            address,
            phoneNumber;
    private final ShoppingCart shoppingCart = new ShoppingCart();

    public Customer
            (
                    final String userName,
                    final String password,
                    final String email,
                    final String firstName,
                    final String lastName,
                    final String address,
                    final String phoneNumber
            )
    {
        this.userName = userName;
        this.password = password;
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.address = address;
        this.phoneNumber = phoneNumber;
    }

    public void addToShoppingCart (final int productId)
    {
        shoppingCart.add(productId);
    }

    public void removeFromShoppingCart (final int productId) throws ModelException
    {
        shoppingCart.remove(productId);
    }

    public ArrayList<Integer> getAllShoppingCartItems ()
    {
        ArrayList<Integer> shoppingCartItems = shoppingCart.getAll();
        return shoppingCartItems;
    }

    public void emptyShoppingCart ()
    {
        shoppingCart.empty();
    }

    @Override
    public boolean equals (final Object other)
    {
        if (other == this)
        {
            return true;
        }
        else if (other instanceof Customer)
        {
            final Customer otherCustomer = (Customer) other;
            if (
                    this.userName.equals(otherCustomer.userName) &&
                            this.password.equals(otherCustomer.password) &&
                            this.email.equals(otherCustomer.email) &&
                            this.firstName.equals(otherCustomer.firstName) &&
                            this.lastName.equals(otherCustomer.lastName) &&
                            this.address.equals(otherCustomer.address) &&
                            this.phoneNumber.equals(otherCustomer.phoneNumber) &&
                            this.shoppingCart.equals(otherCustomer.shoppingCart)
                    )
            {
                return true;
            }
        }
        return false;
    }
}