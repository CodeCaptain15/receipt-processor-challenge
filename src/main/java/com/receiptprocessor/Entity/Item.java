package com.receiptprocessor.Entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
public record Item(
    @Id @GeneratedValue UUID itemID,
    @NotNull String shortDescription,
    @NotNull BigDecimal price,
    @ManyToOne @JoinColumn(name = "receipt_id", nullable = false) Receipt receipt
) {
    public Item() {
        this(UUID.randomUUID(), "", BigDecimal.ZERO, null);
    }

    public Item(UUID itemID, String shortDescription, BigDecimal price) {
        this(itemID, shortDescription, price, null);
    }
}