package com.chinook.model;

public class InvoiceLine {
    private int invoiceId;
    private int trackId;
    private double unitPrice;
    private int quantity;

    public InvoiceLine() {}

    public int getInvoiceId() { return invoiceId; }
    public void setInvoiceId(int invoiceId) { this.invoiceId = invoiceId; }

    public int getTrackId() { return trackId; }
    public void setTrackId(int trackId) { this.trackId = trackId; }

    public double getUnitPrice() { return unitPrice; }
    public void setUnitPrice(double unitPrice) { this.unitPrice = unitPrice; }

    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
}
