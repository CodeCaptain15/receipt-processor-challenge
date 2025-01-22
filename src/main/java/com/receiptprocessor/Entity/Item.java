package com.receiptprocessor.Entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.UUID;

@Entity
public class Item {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @NotNull(message = "Short description is required.")
    private String shortDescription;

    @NotNull(message = "Price is required.")
    private BigDecimal price;

    @ManyToOne
    @JoinColumn(name = "receipt_id")
    private Receipt receipt;

    // Default constructor for JPA
    public Item() {
    }

    // Constructor with parameters
    public Item(UUID id, String shortDescription, BigDecimal price, Receipt receipt) {
        this.id = id;
        this.shortDescription = shortDescription;
        this.price = price;
        this.receipt = receipt;
    }

    // Getters and setters
    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getShortDescription() {
        return shortDescription;
    }

    public void setShortDescription(String shortDescription) {
        this.shortDescription = shortDescription;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public Receipt getReceipt() {
        return receipt;
    }

    public void setReceipt(Receipt receipt) {
        this.receipt = receipt;
    }
}