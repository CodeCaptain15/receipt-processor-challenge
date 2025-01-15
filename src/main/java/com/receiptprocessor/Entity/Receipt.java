package com.receiptprocessor.Entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
public record Receipt(
    @Id @GeneratedValue UUID receiptId,
    @NotNull String retailer,
    LocalDate purchaseDate,
    LocalTime purchaseTime,
    @NotNull BigDecimal total,
    @OneToMany(mappedBy = "receipt", cascade = CascadeType.ALL, orphanRemoval = true) List<Item> items
) {
    public Receipt() {
        this(UUID.randomUUID(), "", LocalDate.now(), LocalTime.now(), BigDecimal.ZERO, List.of());
    }

    public Receipt(UUID receiptId, String retailer, LocalDate purchaseDate, LocalTime purchaseTime, BigDecimal total, List<Item> items) {
        this.receiptId = receiptId;
        this.retailer = retailer;
        this.purchaseDate = purchaseDate;
        this.purchaseTime = purchaseTime;
        this.total = total;
        this.items = items;
    }

    public void setItems(List<Item> items) {
        if (items != null) {
            List<Item> updatedItems = new ArrayList<>();
            for (Item item : items) {
                updatedItems.add(new Item(item.itemID(), item.shortDescription(), item.price(), this));
            }
            this.items.clear();
            this.items.addAll(updatedItems);
        }
    }
}