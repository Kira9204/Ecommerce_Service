/////////////////////////////////////////////////////////////////////////////
// Name:        CustomerMapper.java
// Encoding:	UTF-8
//
// Purpose:     Maps IntegerArrays JSON serialization/deserialization.
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

import javax.ws.rs.Consumes;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyWriter;
import javax.ws.rs.ext.Provider;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.ArrayList;

@Provider
@Consumes (MediaType.APPLICATION_JSON)
public final class IntegerListMapper implements MessageBodyWriter<ArrayList<Integer>>
{
    private Gson gson;
    private Type listOfIntegerType = new TypeToken<ArrayList<Integer>>()
    {
    }.getType();

    public IntegerListMapper ()
    {
        gson = new GsonBuilder().registerTypeAdapter(listOfIntegerType, new IntegerListAdapter()).create();
    }

    // MessageBodyWriter
    @Override
    public boolean isWriteable (Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType)
    {
        return genericType.equals(listOfIntegerType);
    }

    @Override
    public long getSize (ArrayList<Integer> t, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType)
    {
        return -1;
    }

    @Override
    public void writeTo (ArrayList<Integer> integerList, Class<?> type, Type genericType, Annotation[] annotations,
                         MediaType mediaType,
                         MultivaluedMap<String, Object> httpHeaders,
                         OutputStream entityStream) throws IOException, WebApplicationException
    {
        try (final JsonWriter writer = new JsonWriter(new OutputStreamWriter(entityStream)))
        {
            gson.toJson(integerList, listOfIntegerType, writer);
        }
    }

    public static final class IntegerListAdapter implements JsonSerializer<ArrayList<Integer>>
    {
        @Override
        public JsonElement serialize (ArrayList<Integer> integerList, Type typeOfSrc, JsonSerializationContext context)
        {
            final JsonObject integerListJson = new JsonObject();
            final JsonArray jsonArray = new JsonArray();

            for (Integer i : integerList)
            {
                jsonArray.add(new JsonPrimitive(i));
            }
            integerListJson.add("integerArray", jsonArray);
            return integerListJson;
        }
    }
}