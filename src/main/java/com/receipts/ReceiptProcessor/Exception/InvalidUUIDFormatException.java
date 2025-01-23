package com.receipts.ReceiptProcessor.Exception;

public class InvalidUUIDFormatException extends RuntimeException {
    public InvalidUUIDFormatException(String message) {
        super(message);
    }
}
