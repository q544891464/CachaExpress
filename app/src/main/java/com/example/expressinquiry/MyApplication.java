package com.example.expressinquiry;

import android.app.Application;

import com.mob.MobSDK;

import java.sql.Date;

public class MyApplication extends Application{

    @Override
    public void onCreate(){
        super.onCreate();
        MobSDK.init(this);
    }

    private int selectedNum;



    private String orderId;

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getOrderId() {
        return orderId;
    }

    private String searchResult;

    public void setSearchResult(String searchResult) {
        this.searchResult = searchResult;
    }

    public String getSearchResult() {
        return searchResult;
    }

    private Boolean companySelected = false;

    public void setCompanySelected(Boolean companySelected) {
        this.companySelected = companySelected;
    }

    public Boolean isCompanySelected() {
        return companySelected;
    }

    private String companyName="";

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getCompanyName() {
        return companyName;
    }

    private String companyCode="";

    public void setCompanyCode(String companyCode) {
        this.companyCode = companyCode;
    }

    public String getCompanyCode() {
        return companyCode;
    }

    private int id;
    private String username;

    private String password;

    private String phonenumber;

    private int question;

    private String answer;

    private String sex;

    private Date registerDate;

    public void setRegisterDate(Date registerDate) {
        this.registerDate = registerDate;
    }

    public Date getRegisterDate() {
        return registerDate;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getSex() {
        return sex;
    }

    public int getId() {
        return id;
    }

    public String getAnswer() {
        return answer;
    }

    public int getQuestion() {
        return question;
    }

    public String getPhonenumber() {
        return phonenumber;
    }

    public String getPassword() {
        return password;
    }

    public String getUsername() {
        return username;
    }


    public void setId(int id) {
        this.id = id;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public void setQuestion(int question) {
        this.question = question;
    }

    public void setPhonenumber(String phonenumber) {
        this.phonenumber = phonenumber;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setUsername(String username) {
        this.username = username;
    }

}
