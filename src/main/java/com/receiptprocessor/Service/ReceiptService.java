package com.receiptprocessor.Service;

import com.receiptprocessor.Entity.Receipt;
import com.receiptprocessor.Exception.InvalidDataException;
import com.receiptprocessor.Exception.ReceiptNotFoundException;
import com.receiptprocessor.Repository.ReceiptRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Optional;
import java.util.UUID;

@Service
public class ReceiptService {

    private final ReceiptRepository receiptRepository;

    @Autowired
    public ReceiptService(ReceiptRepository receiptRepository) {
        this.receiptRepository = receiptRepository;
    }

    /**
     * Processes a receipt by validating and saving it to the repository.
     *
     * @param receipt The receipt to process.
     * @return The ID of the processed receipt or throws an exception if invalid.
     */
    public ReceiptIdResponse processReceipt(@Valid Receipt receipt) {
        // Validate receipt
        String validationError = validateReceipt(receipt);
        if (validationError != null) {
            throw new InvalidDataException("The receipt is invalid: " + validationError);
        }

        // Save receipt
        Receipt savedReceipt = receiptRepository.save(receipt);
        return new ReceiptIdResponse(savedReceipt.getReceiptId());
    }

    /**
     * Validates the receipt data.
     *
     * @param receipt The receipt to validate.
     * @return An error message if validation fails; otherwise, null.
     */
    private String validateReceipt(Receipt receipt) {
        if (receipt.getRetailer() == null || receipt.getRetailer().trim().isEmpty()) {
            return "Retailer name is required.";
        }

        if (receipt.getItems() == null || receipt.getItems().isEmpty()) {
            return "At least one item is required.";
        }

        for (var item : receipt.getItems()) {
            if (item.getShortDescription() == null || item.getShortDescription().trim().isEmpty()) {
                return "Each item must have a description.";
            }
            if (item.getPrice() == null || item.getPrice().compareTo(BigDecimal.ZERO) <= 0) {
                return "Each item must have a positive price.";
            }
        }

        if (receipt.getTotal() == null || receipt.getTotal().compareTo(BigDecimal.ZERO) <= 0) {
            return "Total must be a positive value.";
        }

        if (receipt.getPurchaseDate() != null && receipt.getPurchaseDate().isAfter(LocalDate.now())) {
            return "Purchase date cannot be in the future.";
        }

        if (receipt.getPurchaseTime() != null && receipt.getPurchaseDate() == null) {
            return "Purchase date must be provided if time is specified.";
        }

        return null; // No validation errors
    }

    /**
     * Calculates reward points for a given receipt ID.
     *
     * @param receiptId The UUID of the receipt.
     * @return The calculated points or throws an exception if receipt not found.
     */
    public PointsResponse calculatePoints(UUID receiptId) {
        Optional<Receipt> receiptOptional = receiptRepository.findById(receiptId);

        if (receiptOptional.isEmpty()) {
            throw new ReceiptNotFoundException("No receipt found for id: " + receiptId);
        }

        Receipt receipt = receiptOptional.get();
        int points = calculatePoints(receipt);

        return new PointsResponse(points);
    }

    /**
     * Calculates points based on the receipt's details.
     *
     * @param receipt The receipt for which to calculate points.
     * @return The total points earned.
     */
    private int calculatePoints(Receipt receipt) {
        int points = 0;

        // Rule 1: 1 point for every alphanumeric character in the retailer name
        String retailer = receipt.getRetailer();
        points += countAlphanumericCharacters(retailer);

        // Rule 2: 50 points if the total is a round dollar amount with no cents
        BigDecimal total = receipt.getTotal();
        if (isRoundDollarAmount(total)) {
            points += 50;
        }

        // Rule 3: 25 points if the total is a multiple of 0.25
        if (total.remainder(BigDecimal.valueOf(0.25)).compareTo(BigDecimal.ZERO) == 0) {
            points += 25;
        }

        // Rule 4: 5 points for every two items on the receipt
        int itemCount = receipt.getItems().size();
        points += (itemCount / 2) * 5;

        // Rule 5: 5 points for every item with a trimmed description length that is a multiple of 3
        for (var item : receipt.getItems()) {
            String description = item.getShortDescription().trim();
            if (description.length() % 3 == 0) {
                points += 5;
            }
        }

        // Rule 6: 6 points if the day of the purchase date is odd
        if (receipt.getPurchaseDate() != null && receipt.getPurchaseDate().getDayOfMonth() % 2 != 0) {
            points += 6;
        }

        // Rule 7: 10 points if the time of purchase is after 2:00pm and before 4:00pm
        if (receipt.getPurchaseTime() != null) {
            LocalTime time = receipt.getPurchaseTime();
            if (time.isAfter(LocalTime.of(14, 0)) && time.isBefore(LocalTime.of(16, 0))) {
                points += 10;
            }
        }

        return points;
    }

    /**
     * Counts the number of alphanumeric characters in a string.
     *
     * @param str The string to evaluate.
     * @return The count of alphanumeric characters.
     */
    private int countAlphanumericCharacters(String str) {
        return (int) str.chars()
                .filter(Character::isLetterOrDigit)
                .count();
    }

    /**
     * Checks if the amount is a round dollar amount (no cents).
     *
     * @param amount The amount to check.
     * @return True if it's a round dollar amount; otherwise, false.
     */
    private boolean isRoundDollarAmount(BigDecimal amount) {
        return amount.stripTrailingZeros().scale() <= 0;
    }

    // Response DTOs

    /**
     * DTO for returning the receipt ID.
     */
    public static class ReceiptIdResponse {
        private UUID id;

        public ReceiptIdResponse(UUID id) {
            this.id = id;
        }

        public UUID getId() {
            return id;
        }

        public void setId(UUID id) {
            this.id = id;
        }
    }

    /**
     * DTO for returning points.
     */
    public static class PointsResponse {
        private int points;

        public PointsResponse(int points) {
            this.points = points;
        }

        public int getPoints() {
            return points;
        }

        public void setPoints(int points) {
            this.points = points;
        }
    }
}