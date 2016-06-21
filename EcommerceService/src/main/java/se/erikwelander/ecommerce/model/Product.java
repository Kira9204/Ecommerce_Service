/////////////////////////////////////////////////////////////////////////////
// Name:        Product.java
// Encoding:	UTF-8
//
// Purpose:     Contains information about about a product.
//
// Author:      Erik Welander (mail@erikwelander.se)
// Modified:    2016-06-21
// Copyright:   Erik Welander
// Licence:     Creative Commons "by-nc-nd"
/////////////////////////////////////////////////////////////////////////////
package se.erikwelander.ecommerce.model;

public final class Product
{
    public final int id;
    public final double price;
    public final String title, category, manufacturer, description, image;
    private int quantity;

    public Product (
            final int quantity,
            final double price,
            final String title,
            final String category,
            final String manufacturer,
            final String description,
            final String image
    )
    {
        this.id = -1;
        this.quantity = quantity;
        this.price = price;
        this.title = title;
        this.category = category;
        this.manufacturer = manufacturer;
        this.description = description;
        this.image = image;
    }

    public Product (
            final int id,
            final int quantity,
            final double price,
            final String title,
            final String category,
            final String manufacturer,
            final String description,
            final String image
    )
    {
        this.id = id;
        this.quantity = quantity;
        this.price = price;
        this.title = title;
        this.category = category;
        this.manufacturer = manufacturer;
        this.description = description;
        this.image = image;
    }


    public int getQuantity ()
    {
        return this.quantity;
    }

    public void increaseDecreaseQuantity (final int amount)
    {
        if (amount < 0)
        {
            this.quantity -= amount;
        }
        else
        {
            this.quantity += amount;
        }
    }

    @Override
    public boolean equals (final Object other)
    {
        if (other == this)
        {
            return true;
        }
        else if (other instanceof Product)
        {
            Product otherProduct = (Product) other;
            if (this.id == otherProduct.id
                    && this.quantity == otherProduct.getQuantity()
                    && this.price == otherProduct.price
                    && this.title.equals(otherProduct.title)
                    && this.category.equals(otherProduct.category)
                    && this.manufacturer.equals(otherProduct.manufacturer)
                    && this.description.equals(otherProduct.description)
                    && this.image.equals(otherProduct.image))
            {
                return true;
            }
        }
        return false;
    }
}
