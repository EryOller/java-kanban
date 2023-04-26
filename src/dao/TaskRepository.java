package dao;

import model.TaskData;
import service.exception.ManagerSaveException;

import java.io.IOException;

public interface  TaskRepository {
    TaskData load() throws IOException; // выгрузить из файла

    void save(TaskData taskData) throws ManagerSaveException; // сохранить в файл
}
