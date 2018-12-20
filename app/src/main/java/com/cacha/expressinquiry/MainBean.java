package com.cacha.expressinquiry;

import com.soubw.jcontactlib.JContacts;

import java.io.Serializable;

public class MainBean extends JContacts implements Serializable {

    private String code;

    private Boolean state = false;

    public Boolean isSelected(){
        return  state;
    }

    public void setState(Boolean state) {
        this.state = state;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public MainBean(){
        super();
    }
}