package org.example;

import java.time.LocalDateTime;

public class Receipt {

    private final int receiptNumber;
    private final LocalDateTime receiptDate;
    private String storeName;

    public Receipt(int receiptNumber, LocalDateTime receiptDate, String storeName) {
        this.receiptNumber = receiptNumber;
        this.receiptDate = receiptDate;
        this.storeName = storeName;
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



}
