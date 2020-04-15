package com.app.mycan1.model;

import java.util.List;

public class Invoice {

    private String receiptId;
    private List<InvoiceItem> items;

    public Invoice(){}

    public String getReceiptId() {
        return receiptId;
    }

    public void setReceiptId(String receiptId) {
        this.receiptId = receiptId;
    }


    public List<InvoiceItem> getItems() {
        return items;
    }

    public void setItems(List<InvoiceItem> items) {
        this.items = items;
    }

    @Override
    public String toString() {
        return "Invoice{" +
                "receiptId=" + receiptId +
                ", items=" + items +
                '}';
    }
}
