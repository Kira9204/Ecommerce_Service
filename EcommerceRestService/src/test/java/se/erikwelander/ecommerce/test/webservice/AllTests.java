/////////////////////////////////////////////////////////////////////////////
// Name:        AllTests.java
// Encoding:	UTF-8
//
// Purpose:     A testing suite that takes a list of test classes and runs all of their tests.
//
// Author:      Erik Welander (mail@erikwelander.se)
// Modified:    2016-06-21
// Copyright:   Erik Welander
// Licence:     Creative Commons "by-nc-nd"
/////////////////////////////////////////////////////////////////////////////
package se.erikwelander.ecommerce.test.webservice;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith (Suite.class)
@SuiteClasses ({CustomerServiceTest.class, ProductServiceTest.class, OrderServiceTest.class})
public class AllTests
{
}