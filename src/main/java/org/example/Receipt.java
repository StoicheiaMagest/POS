package org.example;

import java.time.LocalDateTime;

public class Receipt {

    private final int receiptNumber;
    private final LocalDateTime receiptDate;

    public Receipt(int receiptNumber, LocalDateTime receiptDate) {
        this.receiptNumber = receiptNumber;
        this.receiptDate = receiptDate;
    }

    public int getReceiptNumber() {
        return receiptNumber;
    }

    public LocalDateTime getReceiptDateAndTime() {
        return receiptDate;
    }



}
