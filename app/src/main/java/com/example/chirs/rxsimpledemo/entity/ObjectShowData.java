package com.example.chirs.rxsimpledemo.entity;

import android.support.annotation.NonNull;

import java.util.List;

/**
 * Created by jianjianhong on 2016/9/26.
 */
public class ObjectShowData {
    private List<ShowObject> data;
    private String info;
    private String result;

    public List<ShowObject> getData() {
        return data;
    }

    public void setData(List<ShowObject> data) {
        this.data = data;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    @NonNull
    @Override
    public String toString() {
        return "ObjectShowData{" +
                "data=" + data +
                ", info='" + info + '\'' +
                ", result='" + result + '\'' +
                '}';
    }
}
