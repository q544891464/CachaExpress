package com.cacha.expressinquiry.bean;

//快递查询实体类
public class DeliveryData {
    private String datetime; //快递时间
    private String remark; //快递状态

    public String getDatetime() {
        return datetime;
    }

    public void setDatetime(String datetime) {
        this.datetime = datetime;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    @Override
    public String toString() {
        return "DeliveryData{" +
                "datetime='" + datetime + '\'' +
                ", remark='" + remark + '\'' +
                '}';
    }
}
