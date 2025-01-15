package com.receiptprocessor.Controller;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.PostMapping;

public class ReceiptController {

    @PostMapping("/receipt/process")
    public ResponseEntity<?> processReceipt() {
        return null;
    }

    @PostMapping("/receipt/{id}/points")
    public ResponseEntity<?> calculatePoints(){
        return null;
    }

}
