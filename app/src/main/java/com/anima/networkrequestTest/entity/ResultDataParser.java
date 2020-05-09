package com.anima.networkrequestTest.entity;

import android.text.TextUtils;
import com.anima.networkrequest.data.okhttp.dataConvert.ParameterizedTypeImpl;
import com.anima.networkrequest.data.okhttp.dataConvert.ResponseParser;
import com.google.gson.Gson;

import java.io.StringReader;
import java.lang.reflect.Type;

/**
 * Created by jianjianhong on 19-11-25
 */
public class ResultDataParser<T> implements ResponseParser {

    private T resultData;

    private String errorMessage;

   
    @Override
    public String errorMessage() {
        return errorMessage;
    }

    
    @Override
    public T getResult() {
        return resultData;
    }

    @Override
    public int getTotal() {
        return 1;
    }

    @Override
    public boolean isSuccess() {
        return TextUtils.isEmpty(errorMessage);
    }

   
    @Override
    public ResponseParser parser(String s, Class<?> aClass) {
        try {
            s = s.replace("\"{", "{").replace("}\"", "}").replaceAll("\\\\", "");

            Type type = new ParameterizedTypeImpl(ResultData.class, new Class[]{aClass});
            ResultData<T> resultData = new Gson().fromJson(new StringReader(s), type);

            if(resultData.getStatus().isSuccess()) {
                if(resultData.getResult() != null) {
                    this.resultData = (T) resultData.getResult().getData();
                }
            }else {
                errorMessage = resultData.getStatus().getMessage();
            }

        } catch (Exception e) {
            errorMessage = e.getMessage();
            e.printStackTrace();
        }

        return this;
    }
}
