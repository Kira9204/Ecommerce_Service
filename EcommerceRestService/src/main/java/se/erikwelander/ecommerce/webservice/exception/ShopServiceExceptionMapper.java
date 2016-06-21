/////////////////////////////////////////////////////////////////////////////
// Name:        ShopServiceExceptionMapper.java
// Encoding:	UTF-8
//
// Purpose:     Maps exceptions to BAD REQUEST responses.
//
// Author:      Erik Welander (mail@erikwelander.se)
// Modified:    2016-06-21
// Copyright:   Erik Welander
// Licence:     Creative Commons "by-nc-nd"
/////////////////////////////////////////////////////////////////////////////
package se.erikwelander.ecommerce.webservice.exception;

import se.erikwelander.ecommerce.exception.ShopServiceException;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public final class ShopServiceExceptionMapper implements ExceptionMapper<ShopServiceException>
{
    @Override
    public Response toResponse (ShopServiceException e)
    {
        return Response.status(Status.BAD_REQUEST).entity("This is from MAPPER: " + e.getMessage()).build();
    }
}
