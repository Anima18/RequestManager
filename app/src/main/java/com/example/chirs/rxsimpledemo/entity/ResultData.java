package com.example.chirs.rxsimpledemo.entity;

import java.util.List;

public class ResultData {
    private String status;
    private String errorMessage;
    private List<User> dataList;

    public ResultData() {}

    public ResultData(String status, String errorMessage, List<User> dataList) {
        this.status = status;
        this.errorMessage = errorMessage;
        this.dataList = dataList;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public List<User> getDataList() {
        return dataList;
    }

    public void setDataList(List<User> dataList) {
        this.dataList = dataList;
    }

    @Override
    public String toString() {
        return "ResultData{" +
                "status='" + status + '\'' +
                ", errorMessage='" + errorMessage + '\'' +
                ", dataList=" + dataList +
                '}';
    }
}
