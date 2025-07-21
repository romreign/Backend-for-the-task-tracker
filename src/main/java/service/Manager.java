package main.java.service;

import com.google.gson.*;
import main.java.models.StatusTask;
import main.java.models.TypeTask;
import main.java.service.impl.FileBackedTasksManager;
import main.java.service.impl.InMemoryHistoryManager;
import main.java.service.impl.InMemoryTaskManager;
import main.java.service.interfaces.TaskManager;

import java.lang.reflect.Type;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Manager {
    public static TaskManager getDefault() {
        return new InMemoryTaskManager(getDefaultHistory());
    }

    public static TaskManager getFileBacked(String fileName) {
        return new FileBackedTasksManager(fileName, getDefaultHistory());
    }

    public static InMemoryHistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }

    public static Gson getDefaultGson() {
        return new GsonBuilder()
                .setPrettyPrinting()
                .serializeNulls()
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                .registerTypeAdapter(TypeTask.class, new TypeTaskAdapter())
                .registerTypeAdapter(StatusTask.class, new StatusTaskAdapter())
                .create();
    }
}

class LocalDateTimeAdapter implements JsonSerializer<LocalDateTime>, JsonDeserializer<LocalDateTime> {
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");;

    @Override
    public JsonElement serialize(LocalDateTime src, Type typeOfSrc, JsonSerializationContext context) {
        return new JsonPrimitive(formatter.format(src));
    }

    @Override
    public LocalDateTime deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) {
        return LocalDateTime.parse(json.getAsString(), formatter);
    }
}

class TypeTaskAdapter implements JsonSerializer<TypeTask>, JsonDeserializer<TypeTask> {
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

class StatusTaskAdapter implements JsonSerializer<StatusTask>, JsonDeserializer<StatusTask> {
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