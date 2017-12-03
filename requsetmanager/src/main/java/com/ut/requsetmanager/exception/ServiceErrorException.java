package com.ut.requsetmanager.exception;

/**
 * 请求得到服务端的错误响应时，抛出的异常
 * @author 简建鸿 2016/6/3
 * @version 1.0
 */
public class ServiceErrorException extends Exception {
    /**
     * 异常code，包含网络访问的code和自定义code @{LINK WebServiceHandleException}
     */
    private int code;
    public ServiceErrorException(int code) {
        super();
        this.code = code;
    }

    public int getCode() {
        return code;
    }
}
