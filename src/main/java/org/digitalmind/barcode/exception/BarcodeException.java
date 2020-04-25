package org.digitalmind.barcode.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST)
public class BarcodeException extends RuntimeException {
    public BarcodeException() {
    }

    public BarcodeException(String message) {
        super(message);
    }

    public BarcodeException(String message, Throwable cause) {
        super(message, cause);
    }

    public BarcodeException(Throwable cause) {
        super(cause);
    }

    public BarcodeException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
