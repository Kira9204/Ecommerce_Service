/////////////////////////////////////////////////////////////////////////////
// Name:        CustomerMapper.java
// Encoding:	UTF-8
//
// Purpose:     Maps Customer model JSON serialization/deserialization.
//
// Author:      Erik Welander (mail@erikwelander.se)
// Modified:    2016-06-21
// Copyright:   Erik Welander
// Licence:     Creative Commons "by-nc-nd"
/////////////////////////////////////////////////////////////////////////////
package se.erikwelander.ecommerce.webservice.util;

import com.google.gson.*;
import com.google.gson.stream.JsonWriter;
import se.erikwelander.ecommerce.model.Customer;

import javax.ws.rs.Consumes;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyReader;
import javax.ws.rs.ext.MessageBodyWriter;
import javax.ws.rs.ext.Provider;
import java.io.*;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

@Provider
@Produces (MediaType.APPLICATION_JSON)
@Consumes (MediaType.APPLICATION_JSON)
public final class CustomerMapper implements MessageBodyReader<Customer>, MessageBodyWriter<Customer>
{
    private Gson gson;

    public CustomerMapper ()
    {
        gson = new GsonBuilder().registerTypeAdapter(Customer.class, new CustomerAdapter()).create();
    }

    // MessageBodyWriter
    @Override
    public boolean isWriteable (Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType)
    {
        return type.isAssignableFrom(Customer.class);
    }

    @Override
    public long getSize (Customer t, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType)
    {
        return -1;
    }

    @Override
    public void writeTo (Customer customer, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType, MultivaluedMap<String, Object> httpHeaders,
                         OutputStream entityStream) throws IOException, WebApplicationException
    {
        try (final JsonWriter writer = new JsonWriter(new OutputStreamWriter(entityStream)))
        {
            gson.toJson(customer, Customer.class, writer);
        }
    }

    // MessageBodyReader
    @Override
    public boolean isReadable (Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType)
    {
        return type.isAssignableFrom(Customer.class);
    }

    @Override
    public Customer readFrom (Class<Customer> type, Type genericType, Annotation[] annotations, MediaType mediaType, MultivaluedMap<String, String> httpHeaders,
                              InputStream entityStream) throws IOException, WebApplicationException
    {
        final Customer customer = gson.fromJson(new InputStreamReader(entityStream), Customer.class);
        return customer;
    }

    private static final class CustomerAdapter implements JsonDeserializer<Customer>, JsonSerializer<Customer>
    {

        @Override
        public JsonElement serialize (Customer customer, Type typeOfSrc, JsonSerializationContext context)
        {
            final JsonObject customerJson = new JsonObject();
            customerJson.add("username", new JsonPrimitive(customer.userName));
            customerJson.add("password", new JsonPrimitive(customer.password));
            customerJson.add("email", new JsonPrimitive(customer.email));
            customerJson.add("firstName", new JsonPrimitive(customer.firstName));
            customerJson.add("lastName", new JsonPrimitive(customer.lastName));
            customerJson.add("address", new JsonPrimitive(customer.address));
            customerJson.add("phoneNumber", new JsonPrimitive(customer.phoneNumber));

            return customerJson;
        }

        @Override
        public Customer deserialize (JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException
        {
            final JsonObject customerJson = json.getAsJsonObject();
            final String username = customerJson.get("username").getAsString();
            final String password = customerJson.get("password").getAsString();
            final String email = customerJson.get("email").getAsString();
            final String firstName = customerJson.get("firstName").getAsString();
            final String lastName = customerJson.get("lastName").getAsString();
            final String address = customerJson.get("address").getAsString();
            final String mobileNumber = customerJson.get("phoneNumber").getAsString();

            return new Customer(username, password, email, firstName, lastName, address, mobileNumber);
        }
    }
}