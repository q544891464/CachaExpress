package com.cacha.expressinquiry.bean;

import android.view.View;

/**
 * 快递历史实体类
 */
public class HistoryData {
    private String companyName;
    private String company; //快递公司编号
    private String number; //快递单号
    private String status; //快递状态（1代表结束，0代表还会有变化）
    private String datetime; //快递更新时间
    private String remark="";//备注
    private boolean isChecked;
    private int visible = View.GONE;

    public void setVisible(int visible) {
        this.visible = visible;
    }

    public int getVisible() {
        return visible;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
    }

    public boolean isChecked() {
        return isChecked;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getDatetime() {
        return datetime;
    }

    public void setDatetime(String datetime) {
        this.datetime = datetime;
    }

    @Override
    public String toString() {
        return "HistoryData{" +
                "company='" + company + '\'' +
                ", number='" + number + '\'' +
                ", status='" + status + '\'' +
                ", datetime='" + datetime + '\'' +
                '}';
    }
}
