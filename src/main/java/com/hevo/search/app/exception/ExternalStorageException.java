package com.hevo.search.app.exception;

import java.io.IOException;

public class ExternalStorageException extends RuntimeException {
    public ExternalStorageException(String message) {
        super(message);
    }

    public ExternalStorageException(Exception e) {
        super(e);
    }
}
