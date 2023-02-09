package com.oracle.communications.brm.cc.ws;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;

@XmlAccessorType(value = XmlAccessType.FIELD)
public class FiscalPrinterResultFields {
    
    private String InvoiceNumber;
    private String InvoiceStatus;
    private String ErrorMessage;
    private String newSequenceNumber;
        
    public FiscalPrinterResultFields(){
    }

    public String getInvoiceNumber() {
        return InvoiceNumber;
    }

    public void setInvoiceNumber(String InvoiceNumber) {
        this.InvoiceNumber = InvoiceNumber;
    }

    public String getInvoiceStatus() {
        return InvoiceStatus;
    }

    public void setInvoiceStatus(String InvoiceStatus) {
        this.InvoiceStatus = InvoiceStatus;
    }

    public String getErrorMessage() {
        return ErrorMessage;
    }

    public void setErrorMessage(String ErrorMessage) {
        this.ErrorMessage = ErrorMessage;
    }

    public String getNewSequenceNumber() {
        return newSequenceNumber;
    }

    public void setNewSequenceNumber(String newSequenceNumber) {
        this.newSequenceNumber = newSequenceNumber;
    }
        
        
        
}
