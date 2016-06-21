/////////////////////////////////////////////////////////////////////////////
// Name:        ProductMapper.java
// Encoding:	UTF-8
//
// Purpose:     Maps Product models JSON serialization/deserialization.
//
// Author:      Erik Welander (mail@erikwelander.se)
// Modified:    2016-06-21
// Copyright:   Erik Welander
// Licence:     Creative Commons "by-nc-nd"
/////////////////////////////////////////////////////////////////////////////
package se.erikwelander.ecommerce.webservice.util;

import com.google.gson.*;
import com.google.gson.stream.JsonWriter;
import se.erikwelander.ecommerce.model.Product;

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
public final class ProductMapper implements MessageBodyWriter<Product>, MessageBodyReader<Product>
{
    private Gson gson;

    public ProductMapper ()
    {
        gson = new GsonBuilder().registerTypeAdapter(Product.class, new ProductAdapter()).create();
    }

    // MessageBodyWriter
    @Override
    public boolean isWriteable (Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType)
    {
        return type.isAssignableFrom(Product.class);
    }

    @Override
    public long getSize (Product t, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType)
    {
        return -1;
    }

    @Override
    public void writeTo (Product product, Class<?> type, Type genericType, Annotation[] annotations,
                         MediaType mediaType, MultivaluedMap<String, Object> httpHeaders,
                         OutputStream entityStream) throws IOException, WebApplicationException
    {
        try (final JsonWriter writer = new JsonWriter(new OutputStreamWriter(entityStream)))
        {
            gson.toJson(product, Product.class, writer);
        }
    }

    // MessageBodyReader
    @Override
    public boolean isReadable (Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType)
    {
        return type.isAssignableFrom(Product.class);
    }

    @Override
    public Product readFrom (Class<Product> type, Type genericType, Annotation[] annotations, MediaType mediaType,
                             MultivaluedMap<String, String> httpHeaders,
                             InputStream entityStream) throws IOException, WebApplicationException
    {
        final Product product = gson.fromJson(new InputStreamReader(entityStream), Product.class);
        return product;
    }

    public static final class ProductAdapter implements JsonSerializer<Product>, JsonDeserializer<Product>
    {
        @Override
        public JsonElement serialize (Product product, Type typeOfSrc, JsonSerializationContext context)
        {
            final JsonObject productJson = new JsonObject();
            productJson.add("id", new JsonPrimitive(product.id));
            productJson.add("title", new JsonPrimitive(product.title));
            productJson.add("category", new JsonPrimitive(product.category));
            productJson.add("manufacturer", new JsonPrimitive(product.manufacturer));
            productJson.add("description", new JsonPrimitive(product.description));
            productJson.add("image", new JsonPrimitive(product.image));
            productJson.add("price", new JsonPrimitive(product.price));
            productJson.add("quantity", new JsonPrimitive(product.getQuantity()));

            return productJson;
        }

        @Override
        public Product deserialize (JsonElement json, Type typeOfT, JsonDeserializationContext context)
                throws JsonParseException
        {
            final JsonObject productJson = json.getAsJsonObject();

            final int id = productJson.get("id").getAsInt();
            final int quantity = productJson.get("quantity").getAsInt();
            final double price = productJson.get("price").getAsDouble();
            final String title = productJson.get("title").getAsString();
            final String category = productJson.get("category").getAsString();
            final String manufacturer = productJson.get("manufacturer").getAsString();
            final String description = productJson.get("description").getAsString();
            final String image = productJson.get("image").getAsString();
            return new Product(id, quantity, price, title, category, manufacturer, description, image);
        }
    }
}