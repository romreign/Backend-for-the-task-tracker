package main.java.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import main.java.models.Epic;
import main.java.models.StatusTask;
import main.java.models.TypeTask;
import main.java.config.adapters.EpicAdapter;
import main.java.config.adapters.LocalDateTimeAdapter;
import main.java.config.adapters.StatusTaskAdapter;
import main.java.config.adapters.TypeTaskAdapter;

import java.time.LocalDateTime;

public class GsonConfig {
    public static Gson getDefaultGson() {
        return new GsonBuilder()
                .setPrettyPrinting()
                .serializeNulls()
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                .registerTypeAdapter(TypeTask.class, new TypeTaskAdapter())
                .registerTypeAdapter(StatusTask.class, new StatusTaskAdapter())
                .registerTypeAdapter(Epic.class, new EpicAdapter())
                .create();
    }
}

