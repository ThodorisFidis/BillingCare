package com.oracle.communications.brm.cc.ws;

import com.portal.pcm.Poid;
import java.math.BigDecimal;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;

@XmlAccessorType(value = XmlAccessType.FIELD)
public class GroupFiscalizationBulkCreationFields {

    private Poid itemObj;
    private String fiscalNo;
    private String technologyCode;
    private Poid accountObj;
    private String salesDirectorate;
    private String userCode;
    private BigDecimal netAmount; 
    private String taxCode;
    private BigDecimal vatAmount;
    private int processingStage;
    private int rsSeqNo;

    public GroupFiscalizationBulkCreationFields() {
    }

    public Poid getItemObj() {
        return itemObj;
    }

    public void setItemObj(Poid itemObj) {
        this.itemObj = itemObj;
    }

    public String getFiscalNo() {
        return fiscalNo;
    }

    public void setFiscalNo(String fiscalNp) {
        this.fiscalNo = fiscalNp;
    }

    public String getTechnologyCode() {
        return technologyCode;
    }

    public void setTechnologyCode(String technologyCode) {
        this.technologyCode = technologyCode;
    }

    public Poid getAccountObj() {
        return accountObj;
    }

    public void setAccountObj(Poid accountObj) {
        this.accountObj = accountObj;
    }

    public String getSalesDirectorate() {
        return salesDirectorate;
    }

    public void setSalesDirectorate(String salesDirectorate) {
        this.salesDirectorate = salesDirectorate;
    }

    public String getUserCode() {
        return userCode;
    }

    public void setUserCode(String userCode) {
        this.userCode = userCode;
    }

    public BigDecimal getNetAmount() {
        return netAmount;
    }

    public void setNetAmount(BigDecimal netAmount) {
        this.netAmount = netAmount;
    }

    public String getTaxCode() {
        return taxCode;
    }

    public void setTaxCode(String taxCode) {
        this.taxCode = taxCode;
    }

    public BigDecimal getVatAmount() {
        return vatAmount;
    }

    public void setVatAmount(BigDecimal vatAmount) {
        this.vatAmount = vatAmount;
    }

    public int getProcessingStage() {
        return processingStage;
    }

    public void setProcessingStage(int processingStage) {
        this.processingStage = processingStage;
    } 

    public int getRsSeqNo() {
        return rsSeqNo;
    }

    public void setRsSeqNo(int rsSeqNo) {
        this.rsSeqNo = rsSeqNo;
    }
        
    
}
