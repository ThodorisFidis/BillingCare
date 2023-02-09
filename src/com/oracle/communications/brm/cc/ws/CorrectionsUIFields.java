package com.oracle.communications.brm.cc.ws;

import java.math.BigDecimal;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;

@XmlAccessorType(value = XmlAccessType.FIELD)
public class CorrectionsUIFields {

    private String adjustmentType;
    private String fiscalNumber;
    private String taxCode;
    private BigDecimal amount;
    private String directorateCode;
    private String technologyCode;
    private int adjustmentStatus;
    private String adjFiscalNumber;
    private String directorateDescription;
    private int processingStage;
    private String itemObj;
    private int rsSeqNo;
    private int rsSeqAdjNo;

    public String getFiscalNumber() {
        return fiscalNumber;
    }

    public String getAdjustmentType() {
        return adjustmentType;
    }

    public void setAdjustmentType(String adjustmentType) {
        this.adjustmentType = adjustmentType;
    }

    public int getAdjustmentStatus() {
        return adjustmentStatus;
    }

    public void setAdjustmentStatus(int adjustmentStatus) {
        this.adjustmentStatus = adjustmentStatus;
    }

    public void setFiscalNumber(String fiscalNumber) {
        this.fiscalNumber = fiscalNumber;
    }

    public String getTaxCode() {
        return taxCode;
    }

    public void setTaxCode(String taxCode) {
        this.taxCode = taxCode;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getDirectorateCode() {
        return directorateCode;
    }

    public void setDirectorateCode(String directorateCode) {
        this.directorateCode = directorateCode;
    }

    public String getTechnologyCode() {
        return technologyCode;
    }

    public void setTechnologyCode(String technologyCode) {
        this.technologyCode = technologyCode;
    }

    public String getAdjFiscalNumber() {
        return adjFiscalNumber;
    }

    public void setAdjFiscalNumber(String adjFiscalNumber) {
        this.adjFiscalNumber = adjFiscalNumber;
    }

    public String getDirectorateDescription() {
        return directorateDescription;
    }

    public void setDirectorateDescription(String directorateDescription) {
        this.directorateDescription = directorateDescription;
    }

    public int getProcessingStage() {
        return processingStage;
    }

    public void setProcessingStage(int processingStage) {
        this.processingStage = processingStage;
    }

    public String getItemObj() {
        return itemObj;
    }

    public void setItemObj(String itemObj) {
        this.itemObj = itemObj;
    }

    public int getRsSeqNo() {
        return rsSeqNo;
    }

    public void setRsSeqNo(int rsSeqNo) {
        this.rsSeqNo = rsSeqNo;
    }

    public int getRsSeqAdjNo() {
        return rsSeqAdjNo;
    }

    public void setRsSeqAdjNo(int rsSeqAdjNo) {
        this.rsSeqAdjNo = rsSeqAdjNo;
    }
    
    

}
