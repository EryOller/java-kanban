package service.exception;

import java.io.IOException;

public class ManagerSaveException extends IOException {

    public ManagerSaveException(Exception exception) {
        super("Не удалось прочитать файл. ", exception);
    }
}
