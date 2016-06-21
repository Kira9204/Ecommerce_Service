/////////////////////////////////////////////////////////////////////////////
// Name:        WebShopService.java
// Encoding:	UTF-8
//
// Purpose:     Implements the ShopService object for all other services to use.
//              This allows for a very flexible design where the underlying implementation
//              for all the services can be replaced easily.
//
// Author:      Erik Welander (mail@erikwelander.se)
// Modified:    2016-06-21
// Copyright:   Erik Welander
// Licence:     Creative Commons "by-nc-nd"
/////////////////////////////////////////////////////////////////////////////
package se.erikwelander.ecommerce.webservice;

import se.erikwelander.ecommerce.exception.RepositoryException;
import se.erikwelander.ecommerce.repository.sql.SQLCustomerRepository;
import se.erikwelander.ecommerce.repository.sql.SQLOrderRepository;
import se.erikwelander.ecommerce.repository.sql.SQLProductRepository;
import se.erikwelander.ecommerce.service.ShopService;

public abstract class WebShopService
{
    static ShopService shopService;

    WebShopService () throws RepositoryException
    {
        if (shopService == null)
        {
            shopService = new ShopService(
                    new SQLCustomerRepository(),
                    new SQLProductRepository(),
                    new SQLOrderRepository());
        }
    }
}
