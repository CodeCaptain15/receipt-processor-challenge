package com.receipts.ReceiptProcessor.model;

import java.util.UUID;

public class ReceiptId {
    UUID uuid;

    public ReceiptId() {
    }

    public ReceiptId(UUID uuid) {
        this.uuid = uuid;
    }

    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }

    public UUID getUuid() {
        return uuid;
    }
}
