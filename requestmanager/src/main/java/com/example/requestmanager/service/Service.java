package com.example.requestmanager.service;

import android.content.Context;

import com.google.gson.stream.JsonReader;
import com.example.requestmanager.okhttp.OkHttpUtils;
import com.example.requestmanager.SubscriptionManager;
import com.example.requestmanager.callBack.CallBack;
import com.example.requestmanager.entity.WebServiceError;
import com.example.requestmanager.entity.WebServiceParam;
import com.example.requestmanager.exception.ServiceErrorException;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.StringReader;
import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.Map;
import java.util.Set;

import okhttp3.Call;
import okhttp3.Response;
import rx.Subscription;

/**
 * 服务基本类
 * @author 简建鸿 2016/6/14
 * @version 1.0
 */
public abstract class Service {

    public final static String TAG = "WebService";
    public final static Gson gson = new Gson();
    private final static String POST_TYPE = "POST";
    private final static String GET_TYPE = "GET";

    /**
     * 服务执行方法
     * @param param 请求参数
     * @param callBack 响应回调
     * @param <T>   请求对象类型
     * @return Subscription订阅对象
     */
    abstract <T> Subscription execute(final WebServiceParam param, CallBack<T> callBack);

    /**
     * 获取网络请求响应体
     * @param param 请求参数
     * @return Response
     * @throws IOException
     */
    Response getResponse(WebServiceParam param) throws IOException {
        Call call = null;
        if(GET_TYPE.equals(param.getMethod())) {
            call = OkHttpUtils.get(param.getRequestUrl());
        }else if(POST_TYPE.equals(param.getMethod())) {
            call = OkHttpUtils.post(param.getRequestUrl(), param.getParams());
        }
        SubscriptionManager.addRequest(param, call);
        return call.execute();
    }

    /**
     * 异常处理，请求出现问题，区分不同的问题
     * @param e 异常对象
     * @param callBack 回调对象
     */
    public static void handleException(Throwable e, CallBack callBack) {
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
        callBack.onFailure(webServiceError.getCode(), webServiceError.getMessage());
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

    /*public static void rebuildJsonObj(JSONObject jsonObject) {
        Iterator<String> iterator = jsonObject.keys();
        while (iterator.hasNext()) {
            String key = iterator.next();
            try {
                Object o = jsonObject.get(key);
                if(o instanceof String) {
                    try {
                        JSONObject dd = new JSONObject(o.toString());
                        jsonObject.put(key, dd);
                        rebuildJsonObj(dd);
                        Log.i("DataService", key+": JSONObject");
                    } catch (JSONException e) {
                        try{
                            JSONArray cc = new JSONArray(o.toString());
                            jsonObject.put(key, cc);
                            rebuildJsonArray(cc);
                            Log.i("DataService", key+": JSONArray");
                        } catch (JSONException e2) {
                            Log.i("DataService", key+": String");
                        }
                    }
                }else if(o instanceof JSONObject) {
                    rebuildJsonObj((JSONObject)o);
                }else if(o instanceof JSONArray) {
                    rebuildJsonArray((JSONArray)o);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }*/

    /*public static void rebuildJsonArray(JSONArray jsonArray) {
        for(int i = 0; i < jsonArray.length(); i++) {
            try {
                Object o = jsonArray.get(i);
                if(o instanceof String) {
                    try {
                        JSONObject dd = new JSONObject(o.toString());
                        o = dd;
                        rebuildJsonObj(dd);
                    } catch (JSONException e) {
                        try{
                            JSONArray cc = new JSONArray(o.toString());
                            o = cc;
                            rebuildJsonArray(cc);
                        } catch (JSONException e2) {
                        }
                    }
                }else if(o instanceof JSONObject) {
                    rebuildJsonObj((JSONObject)o);
                }else if(o instanceof JSONArray) {
                    rebuildJsonArray((JSONArray)o);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }*/
}
