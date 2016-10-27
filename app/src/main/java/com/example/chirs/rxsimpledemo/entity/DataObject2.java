package com.example.chirs.rxsimpledemo.entity;

import java.util.List;

/**
 * Created by jianjianhong on 2016/9/26.
 */
public class DataObject2<T> {
    public List<T> data;
    public String info;
    public String result;

    @Override
    public String toString() {
        return "DataObject{" +
                "data=" + data +
                ", info='" + info + '\'' +
                ", result='" + result + '\'' +
                '}';
    }
}
