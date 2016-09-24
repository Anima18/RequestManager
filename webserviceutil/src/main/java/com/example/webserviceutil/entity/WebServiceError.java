package com.example.webserviceutil.entity;

/**
 * Created by Admin on 2016/9/24.
 */

public enum WebServiceError {
    REQUESET_REDIRECTION(300, "请求重定向"),
    REQUESET_MISTAKES_ERROR(400, "请求包含语法错误或者请求无法实现"),
    SERVICE_ERROR(500, "服务器遇到错误，无法完成请求"),
    FILE_NO_FOUND(1000, "文件找不到"),
    UNKNOWN_HOST(2000, "请求的服务器地址不存在"),
    JSON_SYNTAX(3000, "Json解析有误"),
    SOCKET_TIMEOUT(4000, "服务器无响应，访问超时"),
    CONNECT_FAILED(5000, "连接服务器失败,请检查网络或服务器是否开启"),
    IO_EXCEPTION(6000, "连接服务器失败"),
    UNKNOWN_ERROE(7000, "未知错误");
    private int code;
    private String message;
    WebServiceError(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}
