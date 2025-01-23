package com.receipts.ReceiptProcessor.Service;

import com.receipts.ReceiptProcessor.Repository.ReceiptRepository;
import com.receipts.ReceiptProcessor.model.Item;
import com.receipts.ReceiptProcessor.model.Receipt;
import com.receipts.ReceiptProcessor.model.ReceiptId;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Optional;
import java.util.UUID;

@Service
public class ReceiptService {
    private final ReceiptRepository receiptRepository;

    public ReceiptService(ReceiptRepository receiptRepository) {
        this.receiptRepository = receiptRepository;
    }

    public ReceiptId generateId(Receipt receipt) {
        receiptRepository.save(receipt);
        return new ReceiptId(receipt.getReceiptId());
    }

    public ResponseEntity<?> processReceipt(Receipt receipt) {
        String validationError = validateReceipt(receipt);
        if (validationError != null) {
            return new ResponseEntity<>("The receipt is invalid: " + validationError, HttpStatus.BAD_REQUEST);
        }
        Receipt savedReceipt = receiptRepository.save(receipt);
        return new ResponseEntity<>(new ReceiptId(savedReceipt.getReceiptId()), HttpStatus.CREATED);
    }

    private String validateReceipt(Receipt receipt) {
        if (receipt.getRetailer() == null || receipt.getRetailer().trim().isEmpty()) {
            return "Retailer name is required.";
        }
        if (receipt.getItems() == null || receipt.getItems().isEmpty()) {
            return "At least one item is required.";
        }
        for (Item item : receipt.getItems()) {
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
        return null;
    }

    public ResponseEntity<?> findPoints(UUID id) {
        Optional<Receipt> receiptOptional = receiptRepository.findById(id);
        if (receiptOptional.isEmpty()) {
            return new ResponseEntity<>("No receipt found for that id", HttpStatus.BAD_REQUEST);
        }
        Receipt receipt = receiptOptional.get();
        int points = calculatePoints(receipt);
        return new ResponseEntity<>(new PointsResponse(points), HttpStatus.OK);
    }

    public int calculatePoints(Receipt receipt) {
        int points = 0;
        points += countAlphanumericCharacters(receipt.getRetailer());
        if (isRoundDollarAmount(receipt.getTotal())) {
            points += 50;
        }
        if (receipt.getTotal().remainder(BigDecimal.valueOf(0.25)).compareTo(BigDecimal.ZERO) == 0) {
            points += 25;
        }
        points += (receipt.getItems().size() / 2) * 5;
        for (Item item : receipt.getItems()) {
            if (item.getShortDescription().trim().length() % 3 == 0) {
                points += item.getPrice().multiply(BigDecimal.valueOf(0.2)).setScale(0, BigDecimal.ROUND_UP).intValue();
            }
        }
        if (receipt.getPurchaseDate() != null && receipt.getPurchaseDate().getDayOfMonth() % 2 != 0) {
            points += 6;
        }
        if (receipt.getPurchaseTime() != null && receipt.getPurchaseTime().isAfter(LocalTime.of(14, 0)) && receipt.getPurchaseTime().isBefore(LocalTime.of(16, 0))) {
            points += 10;
        }
        return points;
    }

    private int countAlphanumericCharacters(String str) {
        int count = 0;
        for (char c : str.toCharArray()) {
            if (Character.isLetterOrDigit(c)) {
                count++;
            }
        }
        return count;
    }

    private boolean isRoundDollarAmount(BigDecimal amount) {
        return amount.scale() == 0 || amount.remainder(BigDecimal.ONE).compareTo(BigDecimal.ZERO) == 0;
    }

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