package com.oracle.communications.brm.cc.ws;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;

@XmlAccessorType(value = XmlAccessType.FIELD)
public class DirectoratesInfo {
   
    private String directorateCode;
    private String directorateName;

    public DirectoratesInfo() {
    }

    public String getDirectorateCode() {
        return directorateCode;
    }

    public void setDirectorateCode(String directorateCode) {
        this.directorateCode = directorateCode;
    }

    public String getDirectorateName() {
        return directorateName;
    }

    public void setDirectorateName(String directorateName) {
        this.directorateName = directorateName;
    }
    
    
}
