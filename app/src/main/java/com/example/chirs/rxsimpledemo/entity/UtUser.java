package com.example.chirs.rxsimpledemo.entity;

/**
 * Created by jianjianhong on 2016/9/28.
 */
public class UtUser {

    public String utdtguid;
    public String fcode;
    public String fname;
    public String description;
    public String extendparam;
    public String userId;
    public String appcode;
    public String procname;
    public String userName;
    public String userPassword;
    public String userClassName;

    @Override
    public String toString() {
        return "UtUser{" +
                "utdtguid='" + utdtguid + '\'' +
                ", fcode='" + fcode + '\'' +
                ", fname='" + fname + '\'' +
                ", description='" + description + '\'' +
                ", extendparam='" + extendparam + '\'' +
                ", userId='" + userId + '\'' +
                ", appcode='" + appcode + '\'' +
                ", procname='" + procname + '\'' +
                ", userName='" + userName + '\'' +
                ", userPassword='" + userPassword + '\'' +
                ", userClassName='" + userClassName + '\'' +
                '}';
    }

    public String getUtdtguid() {
        return utdtguid;
    }

    public void setUtdtguid(String utdtguid) {
        this.utdtguid = utdtguid;
    }

    public String getFcode() {
        return fcode;
    }

    public void setFcode(String fcode) {
        this.fcode = fcode;
    }

    public String getFname() {
        return fname;
    }

    public void setFname(String fname) {
        this.fname = fname;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getExtendparam() {
        return extendparam;
    }

    public void setExtendparam(String extendparam) {
        this.extendparam = extendparam;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getAppcode() {
        return appcode;
    }

    public void setAppcode(String appcode) {
        this.appcode = appcode;
    }

    public String getProcname() {
        return procname;
    }

    public void setProcname(String procname) {
        this.procname = procname;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserPassword() {
        return userPassword;
    }

    public void setUserPassword(String userPassword) {
        this.userPassword = userPassword;
    }

    public String getUserClassName() {
        return userClassName;
    }

    public void setUserClassName(String userClassName) {
        this.userClassName = userClassName;
    }
}
