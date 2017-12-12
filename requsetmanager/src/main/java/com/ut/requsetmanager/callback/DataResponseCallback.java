package com.ut.requsetmanager.callback;

import android.app.Activity;
import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
import com.ut.requsetmanager.entity.PlatformServiceData;
import com.ut.requsetmanager.entity.WebServiceParam;
import com.ut.requsetmanager.exception.ServiceErrorException;
import com.ut.requsetmanager.network.NetworkTaskImpl;
import com.ut.requsetmanager.util.JsonUtil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * Created by jianjianhong on 2017/11/19.
 */

public class DataResponseCallback implements Callback {

    private NetworkTaskImpl.DataTaskCallback dataCallback;
    private WebServiceParam param;
    private Context context;
    private Object result;
    private static Gson gson = new Gson();

    public DataResponseCallback(Context context,WebServiceParam param, NetworkTaskImpl.DataTaskCallback dataCallback) {
        this.dataCallback = dataCallback;
        this.param = param;
        this.context = context;
    }

    @Override
    public void onFailure(Call call, final IOException e) {
        ((Activity) context).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                dataCallback.onFailure(e);
            }
        });
    }

    @Override
    public void onResponse(Call call, final Response response) throws IOException {
        if(response.isSuccessful()) {
            JsonElement jsonElement = new JsonParser().parse(response.body().charStream());
            if(jsonElement.isJsonObject()) {
                JsonUtil.rebuildJsonObj((JsonObject)jsonElement);
            }
            response.body().close();
            if(param.getClassType() != null) {
                result = gson.fromJson(jsonElement.toString(), param.getClassType());
            }else if((param.getClazz() != null)) {
                result = gson.fromJson(jsonElement.toString(), param.getClazz());
            }else if(param.isPlatformService()) {
                result = convertPlatformServiceData(jsonElement.toString());
            }else {
                result = gson.fromJson(jsonElement.toString(), new TypeToken<Object>(){}.getType());
            }

            ((Activity) context).runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    dataCallback.onSuccess(result);
                }
            });
        }else {
            ((Activity) context).runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    dataCallback.onFailure(new ServiceErrorException(response.code()));
                }
            });
        }
    }

    private Object convertPlatformServiceData(String jsonData) {
        List<PlatformServiceData> dataList = gson.fromJson(jsonData.toString(), new TypeToken<List<PlatformServiceData>>(){}.getType());
        List<Object> valueList = new ArrayList<>();
        if(dataList != null) {
            PlatformServiceData data;
            for(int i = 1; i < dataList.size(); i++) {
                data = dataList.get(i);
                valueList.add(gson.fromJson(data.getV(), new TypeToken<Object>(){}.getType()));
            }
        }
        return valueList;
    }

}
