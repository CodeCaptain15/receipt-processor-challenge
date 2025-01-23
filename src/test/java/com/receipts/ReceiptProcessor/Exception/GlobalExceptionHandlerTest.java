package com.receipts.ReceiptProcessor.Exception;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler exceptionHandler = new GlobalExceptionHandler();

    @Test
    void handleTypeMismatch_shouldReturnBadRequest() {
        MethodArgumentTypeMismatchException ex = new MethodArgumentTypeMismatchException(
                "abc", UUID.class, "id", null, new IllegalArgumentException("Invalid UUID")
        );

        ResponseEntity<Map<String, String>> response = exceptionHandler.handleTypeMismatch(ex);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        Map<String, String> body = response.getBody();
        assertNotNull(body);
        assertEquals("Invalid data type: id", body.get("error"));
        assertEquals("Expected type: UUID", body.get("message"));
    }

    @Test
    void handleInvalidUUIDFormatException_shouldReturnBadRequest() {
        InvalidUUIDFormatException ex = new InvalidUUIDFormatException("Invalid UUID provided");

        ResponseEntity<Map<String, String>> response = exceptionHandler.handleInvalidUUIDFormatException(ex);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        Map<String, String> body = response.getBody();
        assertNotNull(body);
        assertEquals("Invalid UUID format", body.get("error"));
        assertEquals("Invalid UUID provided", body.get("message"));
    }

    @Test
    void handleReceiptNotFoundException_shouldReturnNotFound() {
        ReceiptNotFoundException ex = new ReceiptNotFoundException("Receipt with ID not found");

        ResponseEntity<Map<String, String>> response = exceptionHandler.handleReceiptNotFoundException(ex);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        Map<String, String> body = response.getBody();
        assertNotNull(body);
        assertEquals("Receipt not found", body.get("error"));
        assertEquals("Receipt with ID not found", body.get("message"));
    }

    @Test
    void handleInvalidDataException_shouldReturnBadRequest() {
        InvalidDataException ex = new InvalidDataException("Invalid data provided");

        ResponseEntity<Map<String, String>> response = exceptionHandler.handleInvalidDataException(ex);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        Map<String, String> body = response.getBody();
        assertNotNull(body);
        assertEquals("Invalid data", body.get("error"));
        assertEquals("Invalid data provided", body.get("message"));
    }

    @Test
    void handleGenericException_shouldReturnInternalServerError() {
        Exception ex = new Exception("Unexpected error occurred");

        ResponseEntity<Map<String, String>> response = exceptionHandler.handleGenericException(ex);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        Map<String, String> body = response.getBody();
        assertNotNull(body);
        assertEquals("Internal Server Error", body.get("error"));
        assertEquals("Unexpected error occurred", body.get("message"));
    }
}