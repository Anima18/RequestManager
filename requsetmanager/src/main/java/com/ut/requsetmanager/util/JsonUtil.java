package com.ut.requsetmanager.util;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import com.google.gson.stream.JsonReader;
import com.ut.requsetmanager.callback.DataRequestCallback;
import com.ut.requsetmanager.entity.ResponseStatus;
import com.ut.requsetmanager.entity.WebServiceError;
import com.ut.requsetmanager.exception.ServiceErrorException;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.StringReader;
import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.Map;
import java.util.Set;

/**
 * Created by jianjianhong on 2017/11/19.
 */

public class JsonUtil {
    /**
     * 异常处理，请求出现问题，区分不同的问题
     * @param e 异常对象
     * @param callBack 回调对象
     */
    public static void handleException(Throwable e, DataRequestCallback callBack) {
        e.printStackTrace();
        WebServiceError webServiceError = null;
        if (e instanceof ServiceErrorException) {
            int code = ((ServiceErrorException) e).getCode();
            if (code >= 300 && code < 400) {
                webServiceError = WebServiceError.REQUESET_REDIRECTION;
            }else if (code >= 400 && code < 500) {
                webServiceError = WebServiceError.REQUESET_MISTAKES_ERROR;
            }else if (code >= 500) {
                webServiceError = WebServiceError.SERVICE_ERROR;
            }
        } else if (e instanceof FileNotFoundException) {
            webServiceError = WebServiceError.FILE_NO_FOUND;
        } else if (e instanceof UnknownHostException) {
            webServiceError = WebServiceError.UNKNOWN_HOST;
        } else if (e instanceof SocketTimeoutException) {
            webServiceError = WebServiceError.SOCKET_TIMEOUT;
        } else if(e instanceof ConnectException) {
            webServiceError = WebServiceError.CONNECT_FAILED;
        }else if (e instanceof IOException) {
            webServiceError = WebServiceError.IO_EXCEPTION;
        } else if (e instanceof JsonSyntaxException) {
            webServiceError = WebServiceError.JSON_SYNTAX;
        } else {
            webServiceError = WebServiceError.UNKNOWN_ERROE;
        }
        callBack.onResult(null, new ResponseStatus(webServiceError.getCode(), webServiceError.getMessage()));
    }

    public static WebServiceError getError(Throwable e) {
        e.printStackTrace();
        WebServiceError webServiceError = null;
        if (e instanceof ServiceErrorException) {
            int code = ((ServiceErrorException) e).getCode();
            if (code >= 300 && code < 400) {
                webServiceError = WebServiceError.REQUESET_REDIRECTION;
            }else if (code >= 400 && code < 500) {
                webServiceError = WebServiceError.REQUESET_MISTAKES_ERROR;
            }else if (code >= 500) {
                webServiceError = WebServiceError.SERVICE_ERROR;
            }
        } else if (e instanceof FileNotFoundException) {
            webServiceError = WebServiceError.FILE_NO_FOUND;
        } else if (e instanceof UnknownHostException) {
            webServiceError = WebServiceError.UNKNOWN_HOST;
        } else if (e instanceof SocketTimeoutException) {
            webServiceError = WebServiceError.SOCKET_TIMEOUT;
        } else if(e instanceof ConnectException) {
            webServiceError = WebServiceError.CONNECT_FAILED;
        }else if (e instanceof IOException) {
            webServiceError = WebServiceError.IO_EXCEPTION;
        } else if (e instanceof JsonSyntaxException) {
            webServiceError = WebServiceError.JSON_SYNTAX;
        } else {
            webServiceError = WebServiceError.UNKNOWN_ERROE;
        }
        return webServiceError;
    }

    public static void rebuildJsonObj(JsonObject jsonObject) {
        Set<Map.Entry<String, JsonElement>> entrySet = jsonObject.entrySet();
        for (Map.Entry<String, JsonElement> entry : entrySet) {
            String key = entry.getKey();
            JsonElement jsonElement = jsonObject.get(key);
            if(jsonElement.isJsonObject()) {
                rebuildJsonObj((JsonObject) jsonElement);
            }else if(jsonElement.isJsonArray()) {
                rebuildJsonArray((JsonArray) jsonElement);
            }else {
                String str = jsonElement.getAsString();
                JsonReader reader = new JsonReader(new StringReader(str));
                reader.setLenient(true);
                JsonElement strJE = new JsonParser().parse(reader);
                if(strJE.isJsonObject()) {
                    jsonObject.add(key, strJE);
                    rebuildJsonObj((JsonObject) strJE);
                }else if(strJE.isJsonArray()) {
                    jsonObject.add(key, strJE);
                    rebuildJsonArray((JsonArray) strJE);
                }else {

                }
            }
        }
    }

    public static void rebuildJsonArray(JsonArray jsonArray) {
        for(JsonElement jsonElement : jsonArray){
            if(jsonElement.isJsonObject()) {
                rebuildJsonObj((JsonObject) jsonElement);
            }else if(jsonElement.isJsonArray()) {
                rebuildJsonArray((JsonArray) jsonElement);
            }else {
                String str = jsonElement.getAsString();
                JsonElement strJE = new JsonParser().parse(str);
                if(strJE.isJsonObject()) {
                    jsonElement = strJE;
                    rebuildJsonObj((JsonObject) strJE);
                }else if(strJE.isJsonArray()) {
                    jsonElement = strJE;
                    rebuildJsonArray((JsonArray) strJE);
                }else {

                }
            }
        }
    }
}
