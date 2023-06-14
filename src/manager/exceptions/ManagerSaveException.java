package manager.exceptions;

import java.io.IOException;

public class ManagerSaveException extends  RuntimeException {
    public ManagerSaveException() {
    }

    public ManagerSaveException(String message) {
        super(message);
    }

    public ManagerSaveException(String message, Throwable cause) {
        super(message, cause);
    }

    public ManagerSaveException(Throwable cause) {
        super(cause);
    }

    public ManagerSaveException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
