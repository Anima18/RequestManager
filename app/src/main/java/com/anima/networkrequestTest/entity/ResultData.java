package com.anima.networkrequestTest.entity;

/**
 * Created by jianjianhong on 20-4-16
 */
public class ResultData<T> {

    /**
     * Status : {"Success":true,"Code":"0000","Message":"OK"}
     * Result : {"Data":{"ID":"2","Version":"1.0.1.9","AppType":"Android","Name":"EKEY_TT20190108","Desc":"1、修复一些缺陷","Url":"http://218.13.182.106:8043/prelease/ekey/download/android/EKey_TT.apk"}}
     */

    private StatusBean Status;
    private ResultBean<T> Result;

    public StatusBean getStatus() {
        return Status;
    }

    public void setStatus(StatusBean Status) {
        this.Status = Status;
    }

    public ResultBean getResult() {
        return Result;
    }

    public void setResult(ResultBean Result) {
        this.Result = Result;
    }

    public static class StatusBean {
        /**
         * Success : true
         * Code : 0000
         * Message : OK
         */

        private boolean Success;
        private String Code;
        private String Message;

        public boolean isSuccess() {
            return Success;
        }

        public void setSuccess(boolean Success) {
            this.Success = Success;
        }

        public String getCode() {
            return Code;
        }

        public void setCode(String Code) {
            this.Code = Code;
        }

        public String getMessage() {
            return Message;
        }

        public void setMessage(String Message) {
            this.Message = Message;
        }
    }

    public static class ResultBean<T> {
        /**
         * Data : {"ID":"2","Version":"1.0.1.9","AppType":"Android","Name":"EKEY_TT20190108","Desc":"1、修复一些缺陷","Url":"http://218.13.182.106:8043/prelease/ekey/download/android/EKey_TT.apk"}
         */

        private T Data;

        public T getData() {
            return Data;
        }

        public void setData(T data) {
            Data = data;
        }
    }
}
