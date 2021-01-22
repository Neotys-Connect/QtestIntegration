package com.neotys.qtest.webhook.datamodel;

public class QtestUser {

    Long userid;
    Long clientid;

    public Long getUserid() {
        return userid;
    }

    public void setUserid(Long userid) {
        this.userid = userid;
    }

    public Long getClientid() {
        return clientid;
    }

    public void setClientid(Long clientid) {
        this.clientid = clientid;
    }

    public QtestUser(Long userid, Long clientid) {
        this.userid = userid;
        this.clientid = clientid;
    }
}
