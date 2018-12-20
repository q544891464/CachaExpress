package com.cacha.expressinquiry;

import android.app.Activity;
import android.app.Application;

import com.mob.MobSDK;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

public class MyApplication extends Application{

    private List<Activity> oList;//用于存放所有启动的Activity的集合


    @Override
    public void onCreate(){
        super.onCreate();
        MobSDK.init(this);
        oList = new ArrayList<Activity>();
    }

    /**
     * 添加Activity
     */
    public void addActivity_(Activity activity) {
// 判断当前集合中不存在该Activity
        if (!oList.contains(activity)) {
            oList.add(activity);//把当前Activity添加到集合中
        }
    }

    /**
     * 销毁单个Activity
     */
    public void removeActivity_(Activity activity) {
//判断当前集合中存在该Activity
        if (oList.contains(activity)) {
            oList.remove(activity);//从集合中移除
            activity.finish();//销毁当前Activity
        }
    }

    /**
     * 销毁所有的Activity
     */
    public void removeALLActivity_() {
        //通过循环，把集合中的所有Activity销毁
        for (Activity activity : oList) {
            activity.finish();
        }
    }


    private int selectedNum;


    private String icon;

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

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
