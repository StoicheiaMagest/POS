package org.example;

import java.time.LocalDateTime;

public class ReceiptOld {

    private final int receiptNumber;
    private final LocalDateTime receiptDate;
    private String storeName;
    private Basket basket;

    public ReceiptOld(int receiptNumber, LocalDateTime receiptDate, String storeName, Basket basket) {
        this.receiptNumber = receiptNumber;
        this.receiptDate = receiptDate;
        this.storeName = storeName;
        this.basket = basket;
    }

    public int getReceiptNumber() {
        return receiptNumber;
    }

    public LocalDateTime getReceiptDateAndTime() {
        return receiptDate;
    }

    public String getStoreName() {
        return storeName;
    }

    public String print() {
        StringBuilder sb = new StringBuilder();
        sb.append("Printing receipt number ").append(receiptNumber).append("\n");
        sb.append("Store name: ").append(getStoreName()).append("\n");
        sb.append("Basket: [\n");
        for (Item item : basket.getItems()) {
            sb.append("  ").append(item.getName()).append(" - ").append(item.getPrice()).append("\n");
        }
        sb.append("]");
        return sb.toString();
    }



}
