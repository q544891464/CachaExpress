package com.cacha.expressinquiry.bean;

import java.util.Date;
import java.util.List;

/*
"OrderCode": "",
 "ShipperCode": "ZTO",
 "LogisticCode": "638650888018",
 "Success": true,
 "State": 3,
"Traces": [
 {
 "AcceptTime": "2014-06-24 20:18:58",
 "AcceptStation": "已收件[深圳市]",
 },
 {
 "AcceptTime": "2014-06-24 20:55:28",
 "AcceptStation": "快件在 深圳 ,准备送往下一站 深圳集散中心 [深圳市]",
 },
 {
 "AcceptTime": "2014-06-25 10:23:03",
 "AcceptStation": "派件已签收[深圳市]",
 },
 {
 "AcceptTime": "2014-06-25 10:23:03",
 "AcceptStation": "签收人是：已签收[深圳市]",
 }
 ]
 */
public class DeliveryResult {
    private String EBussinessID;
    private String ShipperCode;
    private String LogisticCode;
    private String State;
    private Boolean Success;

    public Boolean isSuccess() {
        return Success;
    }

    public void setSuccess(Boolean success) {
        Success = success;
    }

    private List<Traces> Traces;

    public class Traces{

        private Date AcceptTime;
        private String AcceptStation;

        public Date getAcceptTime() {
            return AcceptTime;
        }

        public void setAcceptTime(Date acceptTime) {
            AcceptTime = acceptTime;
        }

        public String getAcceptStation() {
            return AcceptStation;
        }

        public void setAcceptStation(String acceptStation) {
            AcceptStation = acceptStation;
        }
    }

    public List<DeliveryResult.Traces> getTraces() {
        return Traces;
    }

    public void setState(String state) {
        State = state;
    }

    public String getEBussinessID() {
        return EBussinessID;
    }

    public void setEBussinessID(String EBussinessID) {
        this.EBussinessID = EBussinessID;
    }

    public String getLogisticCode() {
        return LogisticCode;
    }

    public void setLogisticCode(String logisticCode) {
        LogisticCode = logisticCode;
    }

    public String getShipperCode() {
        return ShipperCode;
    }

    public void setShipperCode(String shipperCode) {
        ShipperCode = shipperCode;
    }

    public String getState() {
        return State;
    }

    public void setTraces(List<DeliveryResult.Traces> traces) {
        Traces = traces;
    }


}
