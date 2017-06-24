package com.example.chirs.rxsimpledemo;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.chirs.rxsimpledemo.entity.DataObject;
import com.example.chirs.rxsimpledemo.entity.User;
import com.example.requestmanager.NetworkRequest;
import com.example.requestmanager.callBack.DataCallBack;
import com.google.gson.reflect.TypeToken;

import rx.Subscription;

/**
 * 获取
 * Created by jianjianhong on 2016/6/12.
 */
public class PostDataActivity extends BaseActivity implements View.OnClickListener {

    private Button searchBt;
    private TextView resultTv;

    private final static String TAG = "PostDataActivity";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_collection);
        initView();
        initEvent();
    }

    public void initView() {
        searchBt = (Button)findViewById(R.id.gcAct_bt);
        resultTv = (TextView)findViewById(R.id.gcAct_result);
    }

    public void initEvent() {
        searchBt.setOnClickListener(this);

    }

    @Override
    public void onClick(@NonNull View v) {
        switch (v.getId()) {
            case R.id.gcAct_bt:
                getCollectionData();
                break;
        }
    }

    private void getCollectionData() {
        showProgress("正在查询...");
        resultTv.setText("");
        new NetworkRequest.Builder(this)
            .url(BASE_PATH + "userInfo/getAllUserInfoLayer.action")
            .dataType(new TypeToken<DataObject<User>>(){}.getType())
            .method(NetworkRequest.POST_TYPE)
            .param("user.name", "Anima18")
            .param("user.password", "123456")
            .call(new DataCallBack<DataObject<User>>() {
                @Override
                public void onSuccess(@Nullable DataObject<User> data) {
                    if(data == null) {
                        resultTv.setText("没有数据");
                    }else {
                        Log.i("WebService", data.data.rows.get(0).getName());
                        resultTv.setText(data.toString());
                    }
                }

                @Override
                public void onFailure(int code, String message) {
                    resultTv.setText("code："+ code +", message:"+message);
                }

                @Override
                public void onCompleted() {
                    hideProgress();
                }
            });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
