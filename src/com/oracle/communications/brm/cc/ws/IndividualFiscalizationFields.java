package com.oracle.communications.brm.cc.ws;

import com.portal.pcm.Poid;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Calendar;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;

@XmlAccessorType(value = XmlAccessType.FIELD)
public class IndividualFiscalizationFields implements Serializable {

    private String accountNo;
    private String dirCode;
    private String userCode;
    private Calendar creationDate;
    private String printerId;
    private String userId;
    private String customerName;
    private String orderNumber;
    private String productObj;
    private BigDecimal netAmount;
    private BigDecimal vatAmount;
    private String glId;
    private String poid;
    private int rsSeqNo;
    private String technologyCode;
    private String endpoint;

    public IndividualFiscalizationFields() {
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getPrinterId() {
        return printerId;
    }

    public void setPrinterId(String printerId) {
        this.printerId = printerId;
    }

    public Calendar getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Calendar creationDate) {
        this.creationDate = creationDate;
    }

    public String getAccountNo() {
        return accountNo;
    }

    public void setAccountNo(String accountNo) {
        this.accountNo = accountNo;
    }

    public String getDirCode() {
        return dirCode;
    }

    public void setDirCode(String dirCode) {
        this.dirCode = dirCode;
    }

    public String getUserCode() {
        return userCode;
    }

    public void setUserCode(String userCode) {
        this.userCode = userCode;
    }

    public String getOrderNumber() {
        return orderNumber;
    }

    public void setOrderNumber(String orderNumber) {
        this.orderNumber = orderNumber;
    }

    public String getProductObj() {
        return productObj;
    }

    public void setProductObj(String productObj) {
        this.productObj = productObj;
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

    public String getGlId() {
        return glId;
    }

    public void setGlId(String glId) {
        this.glId = glId;
    }

    public String getPoid() {
        return poid;
    }

    public void setPoid(String poid) {
        this.poid = poid;
    }

    public int getRsSeqNo() {
        return rsSeqNo;
    }

    public void setRsSeqNo(int rsSeqNo) {
        this.rsSeqNo = rsSeqNo;
    }

    public String getTechnologyCode() {
        return technologyCode;
    }

    public void setTechnologyCode(String technologyCode) {
        this.technologyCode = technologyCode;
    }

    public String getEndpoint() {
        return endpoint;
    }

    public void setEndpoint(String endpoint) {
        this.endpoint = endpoint;
    }
    
    
}
