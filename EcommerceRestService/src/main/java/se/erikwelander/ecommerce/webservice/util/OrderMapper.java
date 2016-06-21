/////////////////////////////////////////////////////////////////////////////
// Name:        OrderMapper.java
// Encoding:	UTF-8
//
// Purpose:     Maps Order models JSON serialization/deserialization.
//
// Author:      Erik Welander (mail@erikwelander.se)
// Modified:    2016-06-21
// Copyright:   Erik Welander
// Licence:     Creative Commons "by-nc-nd"
/////////////////////////////////////////////////////////////////////////////
package se.erikwelander.ecommerce.webservice.util;

import com.google.gson.*;
import com.google.gson.stream.JsonWriter;
import se.erikwelander.ecommerce.model.Order;

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
import java.util.ArrayList;

@Provider
@Produces (MediaType.APPLICATION_JSON)
@Consumes (MediaType.APPLICATION_JSON)
public final class OrderMapper implements MessageBodyWriter<Order>, MessageBodyReader<Order>
{
    private Gson gson;

    public OrderMapper ()
    {
        gson = new GsonBuilder().registerTypeAdapter(Order.class, new OrderAdapter()).create();
    }

    // MessageBodyWriter
    @Override
    public boolean isWriteable (Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType)
    {
        return type.isAssignableFrom(Order.class);
    }

    @Override
    public long getSize (Order t, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType)
    {
        return -1;
    }

    @Override
    public void writeTo (Order Order, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType,
                         MultivaluedMap<String, Object> httpHeaders,
                         OutputStream entityStream) throws IOException, WebApplicationException
    {
        try (final JsonWriter writer = new JsonWriter(new OutputStreamWriter(entityStream)))
        {
            gson.toJson(Order, Order.class, writer);
        }
    }

    // MessageBodyReader
    @Override
    public boolean isReadable (Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType)
    {
        return type.isAssignableFrom(Order.class);
    }

    @Override
    public Order readFrom (Class<Order> type, Type genericType, Annotation[] annotations, MediaType mediaType,
                           MultivaluedMap<String, String> httpHeaders,
                           InputStream entityStream) throws IOException, WebApplicationException
    {
        final Order order = gson.fromJson(new InputStreamReader(entityStream), Order.class);
        return order;
    }

    public static final class OrderAdapter implements JsonSerializer<Order>, JsonDeserializer<Order>
    {
        @Override
        public JsonElement serialize (Order order, Type typeOfSrc, JsonSerializationContext context)
        {
            final JsonObject orderJson = new JsonObject();
            final JsonArray productIdsJsonArray = new JsonArray();

            orderJson.add("id", new JsonPrimitive(order.id));
            orderJson.add("username", new JsonPrimitive(order.customerUserName));
            for (int productId : order.getAllProductIDs())
            {
                productIdsJsonArray.add(new JsonPrimitive(productId));
            }
            orderJson.add("productIds", productIdsJsonArray);
            return orderJson;
        }

        @Override
        public Order deserialize (JsonElement json, Type typeOfT, JsonDeserializationContext context)
                throws JsonParseException
        {
            final JsonObject productJson = json.getAsJsonObject();
            final ArrayList<Integer> productIds = new ArrayList<Integer>();

            final int orderId = productJson.get("id").getAsInt();
            final String username = productJson.get("username").getAsString();
            if (productJson.get("productIds").isJsonArray())
            {
                JsonArray productIdsJsonArray = productJson.get("productIds").getAsJsonArray();
                for (JsonElement jsonElement : productIdsJsonArray)
                {
                    productIds.add(jsonElement.getAsInt());
                }
            }
            else
            {
                throw new JsonParseException("Incorrect Json format, productIds array missing");
            }
            return new Order(orderId, username, productIds);
        }
    }
}
