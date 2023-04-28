package service.exception;

import java.io.IOException;

public class ManagerSaveException extends IOException {

    public ManagerSaveException(String message, Exception exception) {
        super(message, exception);
    }
}
