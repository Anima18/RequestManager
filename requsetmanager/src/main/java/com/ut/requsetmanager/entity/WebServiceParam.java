package com.ut.requsetmanager.entity;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * 请求参数的封装类
 * @author 简建鸿 2016/6/2
 * @version 1.0
 */
public class WebServiceParam {
    /**
     * 请求的url
     */
    private String url;

    /**
     * 请求数据的映射类型
     */
    private Class clazz;

    private Type classType;

    /**
     * 请求的发生, eg:GET或者POST
     */
    private String method;

    /**
     * 请求的参数，存储键值对
     */
    private Map<String, Object> params = new HashMap<>();

    /**
     * 请求超时时间
     */
    private long timeout;

    /**
     * 请求超时时间单位
     */
    private TimeUnit timeoutUnit;

    /**
     * 是否平台服务请求
     * 如果是平台服务请求，会有特殊解析
     */
    private boolean isPlatformService;

    /**
     * 文件下载目录
     */
    private String downloadFilePath;

    /**
     * 添加请求参数
     * @param key 参数名
     * @param value 参数值
     */
    public void addParam(String key, Object value) {
        params.put(key, value);
    }

    public void addParam(Map<String, Object> param) {
        params.putAll(param);
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public Map<String, Object> getParams() {
        return params;
    }

    public void setParams(Map<String, Object> params) {
        this.params = params;
    }

    public Class getClazz() {
        return clazz;
    }

    public void setClazz(Class clazz) {
        this.clazz = clazz;
    }

    public Type getClassType() {
        return classType;
    }

    public void setClassType(Type classType) {
        this.classType = classType;
    }

    public long getTimeout() {
        return timeout;
    }

    public void setTimeout(long timeout) {
        this.timeout = timeout;
    }

    public TimeUnit getTimeoutUnit() {
        return timeoutUnit;
    }

    public void setTimeoutUnit(TimeUnit timeoutUnit) {
        this.timeoutUnit = timeoutUnit;
    }

    public String getDownloadFilePath() {
        return downloadFilePath;
    }

    public void setDownloadFilePath(String downloadFilePath) {
        this.downloadFilePath = downloadFilePath;
    }

    public boolean isPlatformService() {
        return isPlatformService;
    }

    public void setPlatformService(boolean platformService) {
        isPlatformService = platformService;
    }
}
