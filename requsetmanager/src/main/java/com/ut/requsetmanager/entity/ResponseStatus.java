package com.ut.requsetmanager.entity;

/**
 * Created by jianjianhong on 2017/11/30.
 */

public class ResponseStatus {
    public int code;
    public String message;

    public ResponseStatus(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public String toString() {
        return "ResponseStatus{" +
                "code=" + code +
                ", message='" + message + '\'' +
                '}';
    }
}
