package com.receipts.ReceiptProcessor.Exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<Map<String, String>> handleTypeMismatch(MethodArgumentTypeMismatchException ex) {
        Map<String, String> response = new HashMap<>();
        response.put("error", "Invalid data type: " + ex.getName());
        response.put("message", "Expected type: " + Objects.requireNonNull(ex.getRequiredType()).getSimpleName());
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(InvalidUUIDFormatException.class)
    public ResponseEntity<Map<String, String>> handleInvalidUUIDFormatException(InvalidUUIDFormatException ex) {
        Map<String, String> response = new HashMap<>();
        response.put("error", "Invalid UUID format");
        response.put("message", ex.getMessage());
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ReceiptNotFoundException.class)
    public ResponseEntity<Map<String, String>> handleReceiptNotFoundException(ReceiptNotFoundException ex) {
        Map<String, String> response = new HashMap<>();
        response.put("error", "Receipt not found");
        response.put("message", ex.getMessage());
        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(InvalidDataException.class)
    public ResponseEntity<Map<String, String>> handleInvalidDataException(InvalidDataException ex) {
        Map<String, String> response = new HashMap<>();
        response.put("error", "Invalid data");
        response.put("message", ex.getMessage());
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, String>> handleGenericException(Exception ex) {
        Map<String, String> response = new HashMap<>();
        response.put("error", "Internal Server Error");
        response.put("message", ex.getMessage());
        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}