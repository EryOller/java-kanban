package service;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import server.LocalDateTimeAdapter;
import service.exception.ManagerSaveException;

import java.time.Instant;

public class Managers {
    public static TaskManager getDefault() throws ManagerSaveException {
        return new HttpTaskManager(8078);
    }

    public static FileBackedTaskManager getFileBackedTaskManager(String pathFile) {
        return  new FileBackedTaskManager(pathFile);
    }

    public static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }

    public static Gson getGson() {
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(Instant.class, new LocalDateTimeAdapter());
        return gsonBuilder.create();
    }
}
