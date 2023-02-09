package com.oracle.communications.brm.cc.ws;

import java.io.Serializable;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;

@XmlAccessorType(value = XmlAccessType.FIELD)
public class FiscalUpdateResults implements Serializable {

    public FiscalUpdateResults() {
    }

    private String fiscalNumber;
    private String message;
    private String poid;
    private int newSequenceNumber;

    public String getFiscalNumber() {
        return fiscalNumber;
    }

    public void setFiscalNumber(String fiscalNumber) {
        this.fiscalNumber = fiscalNumber;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getPoid() {
        return poid;
    }

    public void setPoid(String poid) {
        this.poid = poid;
    }

    public int getNewSequenceNumber() {
        return newSequenceNumber;
    }

    public void setNewSequenceNumber(int newSequenceNumber) {
        this.newSequenceNumber = newSequenceNumber;
    }
    

}
