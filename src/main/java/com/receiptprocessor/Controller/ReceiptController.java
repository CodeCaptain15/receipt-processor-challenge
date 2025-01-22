package com.receiptprocessor.Controller;

import com.receiptprocessor.Entity.Receipt;
import com.receiptprocessor.Service.ReceiptService;
import com.receiptprocessor.Service.ReceiptService.PointsResponse;
import com.receiptprocessor.Service.ReceiptService.ReceiptIdResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/receipts")
public class ReceiptController {

    private final ReceiptService receiptService;

    @Autowired
    public ReceiptController(ReceiptService receiptService) {
        this.receiptService = receiptService;
    }

    /**
     * Endpoint to process a receipt.
     * URL: POST /receipt/process
     *
     * @param receipt The receipt data.
     * @return The ID of the processed receipt or an error message.
     */
    @PostMapping("/process")
    public ResponseEntity<ReceiptIdResponse> processReceipt(@Valid @RequestBody Receipt receipt) {
        ReceiptIdResponse response = receiptService.processReceipt(receipt);
        return ResponseEntity.status(201).body(response);
    }

    /**
     * Endpoint to calculate points for a given receipt.
     * URL: GET /receipt/{id}/points
     *
     * @param id The UUID of the receipt.
     * @return The calculated points or an error message.
     */
    @GetMapping("/{id}/points")
    public ResponseEntity<PointsResponse> calculatePoints(@PathVariable UUID id) {
        PointsResponse response = receiptService.calculatePoints(id);
        return ResponseEntity.ok(response);
    }
}
