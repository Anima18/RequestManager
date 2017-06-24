package com.example.chirs.rxsimpledemo.entity;

import android.support.annotation.NonNull;

import java.util.List;

/**
 * Created by jianjianhong on 2016/9/26.
 */
public class DataObject<T> {
    public DataBean<T> data;
    public String info;
    public String result;

    public static class DataBean<T> {
        public int total;
        public List<T> rows;

        @NonNull
        @Override
        public String toString() {
            return "DataBean{" +
                    "total=" + total +
                    ", rows=" + rows +
                    '}';
        }
    }

    @NonNull
    @Override
    public String toString() {
        return "DataObject{" +
                "data=" + data +
                ", info='" + info + '\'' +
                ", result='" + result + '\'' +
                '}';
    }
}
