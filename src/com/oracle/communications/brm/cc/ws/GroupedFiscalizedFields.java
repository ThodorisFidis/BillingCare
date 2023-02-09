package com.oracle.communications.brm.cc.ws;

import java.math.BigDecimal;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;

@XmlAccessorType(value = XmlAccessType.FIELD)
public class GroupedFiscalizedFields {

    private String dirCode;
    private String dirDescription;
    private String printerId;
    private BigDecimal netAmount;
    private BigDecimal vatAmount;
    private String technologyCode;
    private String billNumber;
    private String endpoint;
    private String description;
    private String taxCode;
    private BigDecimal itemsCounter;

    public GroupedFiscalizedFields() {
    }

    public String getDirCode() {
        return dirCode;
    }

    public void setDirCode(String dirCode) {
        this.dirCode = dirCode;
    }

    public String getDirDescription() {
        return dirDescription;
    }

    public void setDirDescription(String dirDescription) {
        this.dirDescription = dirDescription;
    }

    public String getPrinterId() {
        return printerId;
    }

    public void setPrinterId(String printerId) {
        this.printerId = printerId;
    }

    public BigDecimal getNetAmount() {
        return netAmount;
    }

    public void setNetAmount(BigDecimal netAmount) {
        this.netAmount = netAmount;
    }

    public BigDecimal getVatAmount() {
        return vatAmount;
    }

    public void setVatAmount(BigDecimal vatAmount) {
        this.vatAmount = vatAmount;
    }

    public String getTechnologyCode() {
        return technologyCode;
    }

    public void setTechnologyCode(String technologyCode) {
        this.technologyCode = technologyCode;
    }

    public String getBillNumber() {
        return billNumber;
    }

    public void setBillNumber(String billNumber) {
        this.billNumber = billNumber;
    }

    public String getEndpoint() {
        return endpoint;
    }

    public void setEndpoint(String endpoint) {
        this.endpoint = endpoint;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getTaxCode() {
        return taxCode;
    }

    public void setTaxCode(String taxCode) {
        this.taxCode = taxCode;
    }

    public BigDecimal getItemsCounter() {
        return itemsCounter;
    }

    public void setItemsCounter(BigDecimal itemsCounter) {
        this.itemsCounter = itemsCounter;
    }

    
}
