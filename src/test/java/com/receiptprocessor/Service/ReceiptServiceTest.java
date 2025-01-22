package com.receiptprocessor.Service;

import com.receiptprocessor.Entity.Item;
import com.receiptprocessor.Entity.Receipt;
import com.receiptprocessor.Exception.InvalidDataException;
import com.receiptprocessor.Exception.ReceiptNotFoundException;
import com.receiptprocessor.Repository.ReceiptRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class ReceiptServiceTest {

    @Mock
    private ReceiptRepository receiptRepository;

    @InjectMocks
    private ReceiptService receiptService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    // Helper method to create a valid Receipt
    private Receipt createValidReceipt() {
        Item item1 = new Item(UUID.randomUUID(), "Milk", BigDecimal.valueOf(3.99), null);
        Item item2 = new Item(UUID.randomUUID(), "Bread", BigDecimal.valueOf(2.49), null);
        List<Item> items = Arrays.asList(item1, item2);

        Receipt receipt = new Receipt(
                UUID.randomUUID(),
                "SuperMart",
                LocalDate.now(),
                LocalTime.of(15, 30),
                BigDecimal.valueOf(6.48),
                new ArrayList<>(items)
        );

        // Set the receipt in each item
        item1.setReceipt(receipt);
        item2.setReceipt(receipt);

        return receipt;
    }

    @Test
    void processReceipt_ValidReceipt_ReturnsCreated() {
        // Arrange
        Receipt receipt = createValidReceipt();
        when(receiptRepository.save(any(Receipt.class))).thenReturn(receipt);

        // Act
        ReceiptService.ReceiptIdResponse response = receiptService.processReceipt(receipt);

        // Assert
        assertNotNull(response, "Response should not be null");
        assertEquals(receipt.getReceiptId(), response.getId(), "Receipt ID should match the saved receipt ID");

        verify(receiptRepository, times(1)).save(receipt);
    }

    @Test
    void processReceipt_InvalidReceipt_ThrowsInvalidDataException() {
        // Arrange
        Receipt invalidReceipt = new Receipt(
                UUID.randomUUID(),
                "", // Invalid retailer
                LocalDate.now(),
                LocalTime.now(),
                BigDecimal.valueOf(0), // Invalid total
                new ArrayList<>()
        );

        // Act & Assert
        InvalidDataException exception = assertThrows(InvalidDataException.class, () -> {
            receiptService.processReceipt(invalidReceipt);
        }, "Expected processReceipt to throw InvalidDataException for invalid receipt");

        assertTrue(exception.getMessage().contains("Retailer name is required"), "Exception message should mention the missing retailer name");

        verify(receiptRepository, times(0)).save(any(Receipt.class));
    }

    @Test
    void calculatePoints_ExistingReceipt_ReturnsPoints() {
        // Arrange
        Receipt receipt = createValidReceipt();
        when(receiptRepository.findById(receipt.getReceiptId())).thenReturn(Optional.of(receipt));

        // Act
        ReceiptService.PointsResponse response = receiptService.calculatePoints(receipt.getReceiptId());

        // Assert
        assertNotNull(response, "PointsResponse should not be null");
        assertTrue(response.getPoints() > 0, "Points should be greater than zero");

        verify(receiptRepository, times(1)).findById(receipt.getReceiptId());
    }

    @Test
    void calculatePoints_NonExistingReceipt_ThrowsReceiptNotFoundException() {
        // Arrange
        UUID nonExistingId = UUID.randomUUID();
        when(receiptRepository.findById(nonExistingId)).thenReturn(Optional.empty());

        // Act & Assert
        ReceiptNotFoundException exception = assertThrows(ReceiptNotFoundException.class, () -> {
            receiptService.calculatePoints(nonExistingId);
        }, "Expected calculatePoints to throw ReceiptNotFoundException for non-existing receipt");

        assertTrue(exception.getMessage().contains("No receipt found for id"), "Exception message should mention the missing receipt");

        verify(receiptRepository, times(1)).findById(nonExistingId);
    }
}