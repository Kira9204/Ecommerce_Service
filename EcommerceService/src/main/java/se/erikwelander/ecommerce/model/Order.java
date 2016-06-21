/////////////////////////////////////////////////////////////////////////////
// Name:        Order.java
// Encoding:	UTF-8
//
// Purpose:     Contains order information for a Customer.
//
// Author:      Erik Welander (mail@erikwelander.se)
// Modified:    2016-06-21
// Copyright:   Erik Welander
// Licence:     Creative Commons "by-nc-nd"
/////////////////////////////////////////////////////////////////////////////
package se.erikwelander.ecommerce.model;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

public final class Order
{
    public final int id;
    public final String customerUserName;
    public final SimpleDateFormat sqlDateFormat = new SimpleDateFormat("yyyy-MM-dd");
    private final ArrayList<Integer> productIDs;
    private Date dateCreated, dateShipped = null;

    public Order (
            final int id,
            final String customerUserName,
            final ArrayList<Integer> productIDs
    )
    {
        this.id = id;
        this.customerUserName = customerUserName;
        this.productIDs = new ArrayList<>(productIDs);
        this.dateCreated = new Date(System.currentTimeMillis());
    }

    public Order (
            final int id,
            final String customerUserName,
            final ArrayList<Integer> productIDs,
            final Date dateCreated,
            final Date dateShipped
    )
    {
        this.id = id;
        this.customerUserName = customerUserName;
        this.productIDs = new ArrayList<>(productIDs);
        this.dateCreated = dateCreated;
        this.dateShipped = dateShipped;
    }

    public final Date getDateCreated ()
    {
        return this.dateCreated;
    }

    public final Date getDateShipped ()
    {
        return this.dateShipped;
    }

    public final boolean isShipped ()
    {
        if (null != dateShipped)
        {
            return true;
        }
        return false;
    }

    public final ArrayList<Integer> getAllProductIDs ()
    {
        return productIDs;
    }

    @Override
    public boolean equals (Object other)
    {
        if (other == this)
        {
            return true;
        }
        else if (other instanceof Order)
        {
            final Order otherOrder = (Order) other;
            String myDateShipped, otherDateShipped;
            if (null == this.getDateShipped())
            {
                myDateShipped = "";
            }
            else
            {
                myDateShipped = sqlDateFormat.format(this.getDateShipped()).toString();
            }
            if (null == otherOrder.getDateShipped())
            {
                otherDateShipped = "";
            }
            else
            {
                otherDateShipped = sqlDateFormat.format(otherOrder.getDateShipped()).toString();
            }

            if (this.id == otherOrder.id
                    && this.customerUserName.equals(otherOrder.customerUserName)
                    && this.productIDs.equals(otherOrder.getAllProductIDs())
                    && myDateShipped.equals(otherDateShipped)
                    )
            {
                return true;
            }
        }
        return false;
    }
}
