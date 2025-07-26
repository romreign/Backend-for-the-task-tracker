package main.java.config.adapters;

import com.google.gson.*;
import main.java.models.Epic;
import main.java.models.StatusTask;
import main.java.models.Subtask;

import java.lang.reflect.Type;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

public class EpicAdapter implements JsonSerializer<Epic>, JsonDeserializer<Epic> {
    @Override
    public JsonElement serialize(Epic src, Type typeOfSrc, JsonSerializationContext context) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("id", src.getId());
        jsonObject.addProperty("title", src.getTitle());
        jsonObject.addProperty("description", src.getDescription());
        jsonObject.addProperty("statusTask", src.getStatus().name());
        jsonObject.add("startTime", context.serialize(src.getStartTime()));
        jsonObject.addProperty("duration", src.getDuration());
        jsonObject.add("endTime", context.serialize(src.getEndTime()));

        JsonArray subtasksArray = new JsonArray();
        for (Subtask subtask : src.getSubstasks().values()) {
            subtasksArray.add(context.serialize(subtask));
        }
        jsonObject.add("subtasks", subtasksArray);

        return jsonObject;
    }

    @Override
    public Epic deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonObject jsonObject = json.getAsJsonObject();

        Epic epic = new Epic(
                jsonObject.get("title").getAsString(),
                jsonObject.get("description").getAsString(),
                jsonObject.get("id").getAsInt(),
                StatusTask.valueOf(jsonObject.get("statusTask").getAsString()),
                context.deserialize(jsonObject.get("startTime"), LocalDateTime.class),
                jsonObject.get("duration").getAsLong()
        );

        if (jsonObject.has("subtasks")) {
            JsonArray subtasksArray = jsonObject.getAsJsonArray("subtasks");
            Map<Integer, Subtask> subtasks = new HashMap<>();

            for (JsonElement element : subtasksArray) {
                Subtask subtask = context.deserialize(element, Subtask.class);
                subtasks.put(subtask.getId(), subtask);
            }

            epic.setSubstasks(subtasks);
        }

        if (jsonObject.has("endTime")) {
            epic.setEndTime(context.deserialize(jsonObject.get("endTime"), LocalDateTime.class));
        }

        return epic;
    }
}