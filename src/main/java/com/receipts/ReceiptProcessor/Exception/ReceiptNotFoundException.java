package com.receipts.ReceiptProcessor.Exception;


public class ReceiptNotFoundException extends RuntimeException {
    public ReceiptNotFoundException(String message) {
        super(message);
    }
}
