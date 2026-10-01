package com.chinook.model;

public class InvoiceHeader {
    private int invoiceId;
    private int customerId;
    private String invoiceDate;
    private String billingAddress;
    private String billingCity;
    private String billingState;
    private String billingCountry;
    private String billingPostalCode;
    private double total;

    public InvoiceHeader() {}

    public int getInvoiceId() { return invoiceId; }
    public void setInvoiceId(int invoiceId) { this.invoiceId = invoiceId; }

    public int getCustomerId() { return customerId; }
    public void setCustomerId(int customerId) { this.customerId = customerId; }

    public String getInvoiceDate() { return invoiceDate; }
    public void setInvoiceDate(String invoiceDate) { this.invoiceDate = invoiceDate; }

    public String getBillingAddress() { return billingAddress; }
    public void setBillingAddress(String billingAddress) { this.billingAddress = billingAddress; }

    public String getBillingCity() { return billingCity; }
    public void setBillingCity(String billingCity) { this.billingCity = billingCity; }

    public String getBillingState() { return billingState; }
    public void setBillingState(String billingState) { this.billingState = billingState; }

    public String getBillingCountry() { return billingCountry; }
    public void setBillingCountry(String billingCountry) { this.billingCountry = billingCountry; }

    public String getBillingPostalCode() { return billingPostalCode; }
    public void setBillingPostalCode(String billingPostalCode) { this.billingPostalCode = billingPostalCode; }

    public double getTotal() { return total; }
    public void setTotal(double total) { this.total = total; }
}
