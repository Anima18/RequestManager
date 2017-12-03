package com.ut.requsetmanager.callback;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.ut.requsetmanager.entity.WebServiceParam;
import com.ut.requsetmanager.exception.ServiceErrorException;
import com.ut.requsetmanager.network.NetworkTaskImpl;
import com.ut.requsetmanager.util.JsonUtil;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * Created by jianjianhong on 2017/11/19.
 */

public class DataResponseCallback implements Callback {

    private NetworkTaskImpl.DataTaskCallback dataCallback;
    private WebServiceParam param;
    private static Gson gson = new Gson();

    public DataResponseCallback(WebServiceParam param, NetworkTaskImpl.DataTaskCallback dataCallback) {
        this.dataCallback = dataCallback;
        this.param = param;
    }

    @Override
    public void onFailure(Call call, IOException e) {
        dataCallback.onFailure(e);
    }

    @Override
    public void onResponse(Call call, Response response) throws IOException {
        if(response.isSuccessful()) {
            JsonElement jsonElement = new JsonParser().parse(response.body().charStream());
            if(jsonElement.isJsonObject()) {
                JsonUtil.rebuildJsonObj((JsonObject)jsonElement);
            }
            response.body().close();

            Object t = null;
            if(param.getClassType() != null) {
                t = gson.fromJson(jsonElement.toString(), param.getClassType());
            }else if((param.getClazz() != null)) {
                t = gson.fromJson(jsonElement.toString(), param.getClazz());
            }else {
                t = gson.fromJson(jsonElement.toString(), Object.class);
            }

            dataCallback.onSuccess(t);
        }else {
            dataCallback.onFailure(new ServiceErrorException(response.code()));
        }
    }

}
