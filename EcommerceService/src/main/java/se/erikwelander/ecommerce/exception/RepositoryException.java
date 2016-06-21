/////////////////////////////////////////////////////////////////////////////
// Name:        RepositoryException.java
// Encoding:	UTF-8
//
// Purpose:     Exception type for repository objects.
//              Used for localized debugging and error handling.
//
// Author:      Erik Welander (mail@erikwelander.se)
// Modified:    2016-06-21
// Copyright:   Erik Welander
// Licence:     Creative Commons "by-nc-nd"
/////////////////////////////////////////////////////////////////////////////
package se.erikwelander.ecommerce.exception;

public final class RepositoryException extends Exception
{
    private static final long serialVersionUID = 1L;

    public RepositoryException (final String message, final Throwable cause)
    {
        super(message, cause);
    }

    public RepositoryException (final String message)
    {
        super(message);
    }
}
