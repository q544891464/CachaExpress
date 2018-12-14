package com.example.expressinquiry;

import java.sql.Date;

public class LoginResult {

    private int id;

    private String status;

    private String username;

    private String password;

    private String phonenumber;

    private Date registerDate;

    private int question;

    private String sex;

    private String answer;

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

    public void setId(int id) {
        this.id = id;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setPhonenumber(String phonenumber) {
        this.phonenumber = phonenumber;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setQuestion(int question) {
        this.question = question;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public int getId() {
        return id;
    }

    public String getStatus() {
        return status;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getPhonenumber() {
        return phonenumber;
    }

    public int getQuestion() {
        return question;
    }

    public String getAnswer() {
        return answer;
    }
}
