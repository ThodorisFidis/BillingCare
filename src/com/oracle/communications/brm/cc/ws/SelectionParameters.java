package com.oracle.communications.brm.cc.ws;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;

@XmlAccessorType(value = XmlAccessType.FIELD)
public class SelectionParameters {
    
   private String directorateName;
   private String directorateCode;
   private String technologyCode;
   private String accountingPeriod;

    public SelectionParameters() {
    }

    public String getDirectorateName() {
        return directorateName;
    }

    public void setDirectorateName(String directorateName) {
        this.directorateName = directorateName;
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

    public String getAccountingPeriod() {
        return accountingPeriod;
    }

    public void setAccountingPeriod(String accountingPeriod) {
        this.accountingPeriod = accountingPeriod;
    }
  
   
}
