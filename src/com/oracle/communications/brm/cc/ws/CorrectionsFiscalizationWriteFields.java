package com.oracle.communications.brm.cc.ws;

import com.portal.pcm.Poid;
import java.math.BigDecimal;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;

@XmlAccessorType(value = XmlAccessType.FIELD)
public class CorrectionsFiscalizationWriteFields {
    
    private int fscADjStatus;
    private int adjustmentType;
    private BigDecimal amount;
    private Poid poid;
    private String fiscalNo;

    public CorrectionsFiscalizationWriteFields() {
    }

    public int getFscADjStatus() {
        return fscADjStatus;
    }

    public void setFscADjStatus(int fscADjStatus) {
        this.fscADjStatus = fscADjStatus;
    }

    public int getAdjustmentType() {
        return adjustmentType;
    }

    public void setAdjustmentType(int adjustmentType) {
        this.adjustmentType = adjustmentType;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public Poid getPoid() {
        return poid;
    }

    public void setPoid(Poid poid) {
        this.poid = poid;
    }

    public String getFiscalNo() {
        return fiscalNo;
    }

    public void setFiscalNo(String fiscalNo) {
        this.fiscalNo = fiscalNo;
    }
    
    
    
}
