package main.java.config.adapters;

import com.google.gson.*;
import main.java.models.StatusTask;

import java.lang.reflect.Type;

public class StatusTaskAdapter implements JsonSerializer<StatusTask>, JsonDeserializer<StatusTask> {
    @Override
    public JsonElement serialize(StatusTask type, Type typeOfSrc, JsonSerializationContext context) {
        return new JsonPrimitive(type.name().toLowerCase());
    }

    @Override
    public StatusTask deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) {
        String value = json.getAsString().toUpperCase();
        return StatusTask.valueOf(value);
    }
}
