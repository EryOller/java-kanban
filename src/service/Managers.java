package service;

import dao.CSVTaskRepository;

import java.io.File;
import java.nio.file.Path;

public class Managers {
    public static TaskManager getDefault() {
        return new InMemoryTaskManager();
    }

    public static FileBackedTaskManager getFileBackedTaskManager(String pathFile) {
        return  new FileBackedTaskManager(new CSVTaskRepository(new File(pathFile)));
    }

    public static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }
}
