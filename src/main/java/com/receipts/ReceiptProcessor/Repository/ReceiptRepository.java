package com.receipts.ReceiptProcessor.Repository;

import com.receipts.ReceiptProcessor.model.Receipt;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ReceiptRepository extends JpaRepository<Receipt, UUID> {

}
