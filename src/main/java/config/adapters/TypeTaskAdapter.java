package main.java.config.adapters;

import com.google.gson.*;
import main.java.models.TypeTask;

import java.lang.reflect.Type;

public class TypeTaskAdapter implements JsonSerializer<TypeTask>, JsonDeserializer<TypeTask> {
    @Override
    public JsonElement serialize(TypeTask type, Type typeOfSrc, JsonSerializationContext context) {
        return new JsonPrimitive(type.name().toLowerCase());
    }

    @Override
    public TypeTask deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) {
        String value = json.getAsString().toUpperCase();
        return TypeTask.valueOf(value);
    }
}