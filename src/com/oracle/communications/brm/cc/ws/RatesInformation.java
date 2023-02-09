
package com.oracle.communications.brm.cc.ws;

import com.portal.pcm.Poid;
import java.math.BigDecimal;
import java.util.Date;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;

@XmlAccessorType(value = XmlAccessType.FIELD)
public class RatesInformation {
    
    private String maturityPeriod;
    private int agingDays;
    private Date effectiveDate;
    private BigDecimal percent;
    private int status;
    private String poid;
    private int index;

    public String getMaturityPeriod() {
        return maturityPeriod;
    }

    public void setMaturityPeriod(String maturityPeriod) {
        this.maturityPeriod = maturityPeriod;
    }

    public int getAgingDays() {
        return agingDays;
    }

    public void setAgingDays(int agingDays) {
        this.agingDays = agingDays;
    }

    public Date getEffectiveDate() {
        return effectiveDate;
    }

    public void setEffectiveDate(Date effectiveDate) {
        this.effectiveDate = effectiveDate;
    }

    public BigDecimal getPercent() {
        return percent;
    }

    public void setPercent(BigDecimal percent) {
        this.percent = percent;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getPoid() {
        return poid;
    }

    public void setPoid(String poid) {
        this.poid = poid;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }
    
    
}
