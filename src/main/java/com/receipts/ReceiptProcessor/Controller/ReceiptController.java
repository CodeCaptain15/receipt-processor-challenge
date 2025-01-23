package com.receipts.ReceiptProcessor.Controller;

import com.receipts.ReceiptProcessor.Exception.InvalidUUIDFormatException;
import com.receipts.ReceiptProcessor.Service.ReceiptService;
import com.receipts.ReceiptProcessor.model.Receipt;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/receipts")
public class ReceiptController {

    private final ReceiptService receiptService;

    public ReceiptController(ReceiptService receiptService) {
        this.receiptService = receiptService;
    }

    @PostMapping("/process")
    public ResponseEntity<?> processReceipt(@Valid @RequestBody Receipt receipt) {
        return receiptService.processReceipt(receipt);
    }

    @GetMapping("/{id}/points")
    public ResponseEntity<?> calculatePoints(@PathVariable String id) {
        UUID uuid;
        try {
            uuid = UUID.fromString(id);
        } catch (IllegalArgumentException e) {
            throw new InvalidUUIDFormatException("Invalid UUID format for id: " + id);
        }
        return receiptService.findPoints(uuid);
    }
}