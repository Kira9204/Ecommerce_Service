/////////////////////////////////////////////////////////////////////////////
// Name:        ProductListMapper.java
// Encoding:	UTF-8
//
// Purpose:     Maps ProductLists JSON serialization/deserialization.
//
// Author:      Erik Welander (mail@erikwelander.se)
// Modified:    2016-06-21
// Copyright:   Erik Welander
// Licence:     Creative Commons "by-nc-nd"
/////////////////////////////////////////////////////////////////////////////
package se.erikwelander.ecommerce.webservice.util;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
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
import java.util.ArrayList;

@Provider
@Produces (MediaType.APPLICATION_JSON)
@Consumes (MediaType.APPLICATION_JSON)
public final class ProductListMapper implements MessageBodyWriter<ArrayList<Product>>,
        MessageBodyReader<ArrayList<Product>>
{
    private Gson gson;
    private Type productListType = new TypeToken<ArrayList<Product>>()
    {
    }.getType();

    public ProductListMapper ()
    {
        gson = new GsonBuilder().registerTypeAdapter(productListType, new ProductListAdapter()).create();
    }

    // MessageBodyWriter
    @Override
    public boolean isWriteable (Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType)
    {
        return genericType.equals(productListType);
    }

    @Override
    public long getSize (ArrayList<Product> t, Class<?> type, Type genericType, Annotation[] annotations,
                         MediaType mediaType)
    {
        return -1;
    }

    @Override
    public void writeTo (ArrayList<Product> productList, Class<?> type, Type genericType, Annotation[] annotations,
                         MediaType mediaType,
                         MultivaluedMap<String, Object> httpHeaders,
                         OutputStream entityStream) throws IOException, WebApplicationException
    {
        try (final JsonWriter writer = new JsonWriter(new OutputStreamWriter(entityStream)))
        {
            gson.toJson(productList, productListType, writer);
        }
    }

    @Override
    public boolean isReadable (Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType)
    {
        return genericType.equals(productListType);
    }

    @Override
    public ArrayList<Product> readFrom (Class<ArrayList<Product>> type, Type genericType, Annotation[] annotations,
                                        MediaType mediaType,
                                        MultivaluedMap<String, String> httpHeaders, InputStream entityStream) throws IOException,
            WebApplicationException
    {
        final ArrayList<Product> productArrayList = gson.fromJson(new InputStreamReader(entityStream), productListType);
        return productArrayList;
    }

    private static final class ProductListAdapter implements JsonSerializer<ArrayList<Product>>,
            JsonDeserializer<ArrayList<Product>>
    {
        @Override
        public JsonElement serialize (ArrayList<Product> productList, Type typeOfSrc, JsonSerializationContext context)
        {
            final JsonObject productListJson = new JsonObject();

            for (Product product : productList)
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

                productListJson.add("" + product.id, productJson);
            }
            return productListJson;
        }

        @Override
        public ArrayList<Product> deserialize (JsonElement json, Type typeOfT, JsonDeserializationContext context)
                throws JsonParseException
        {
            final JsonObject productListJson = json.getAsJsonObject();
            final ArrayList<Product> products = new ArrayList<>();

            // TODO This looks weird, refactor?
            final int NUMBER_OF_PRODUCT_PARAMS = 8;

            for (int i = 1; i < NUMBER_OF_PRODUCT_PARAMS; ++i)
            {
                JsonObject productJson = (JsonObject) productListJson.get(String.valueOf(i));

                final int id = productJson.get("id").getAsInt();
                final int quantity = productJson.get("quantity").getAsInt();
                final double price = productJson.get("price").getAsDouble();
                final String title = productJson.get("title").getAsString();
                final String category = productJson.get("category").getAsString();
                final String manufacturer = productJson.get("manufacturer").getAsString();
                final String description = productJson.get("description").getAsString();
                final String image = productJson.get("image").getAsString();
                products.add(new Product(id, quantity, price, title, category, manufacturer, description, image));
            }
            return products;
        }
    }
}