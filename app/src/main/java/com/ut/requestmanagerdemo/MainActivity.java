package com.ut.requestmanagerdemo;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.util.ArrayMap;
import android.util.Log;
import android.view.View;

import com.google.gson.reflect.TypeToken;
import com.ut.requestmanagerdemo.entity.User;
import com.ut.requsetmanager.callback.DataListRequestCallback;
import com.ut.requsetmanager.callback.DataRequestCallback;
import com.ut.requestmanagerdemo.entity.DataObject;
import com.ut.requsetmanager.entity.FileObject;
import com.ut.requsetmanager.entity.ResponseStatus;
import com.ut.requsetmanager.manager.NetworkRequestManager;
import com.ut.requsetmanager.request.NetworkRequest;
import com.ut.requsetmanager.request.NetworkRequestImpl;

import java.io.File;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "MainActivity";
    protected final static String BASE_PATH = "http://192.168.1.103:8080/webService/";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.mainAct_data_bt).setOnClickListener(this);
        findViewById(R.id.mainAct_download_bt).setOnClickListener(this);
        findViewById(R.id.mainAct_upload_bt).setOnClickListener(this);
        findViewById(R.id.mainAct_nestData_bt).setOnClickListener(this);
        findViewById(R.id.mainAct_sequence_bt).setOnClickListener(this);
        findViewById(R.id.mainAct_merge_bt).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.mainAct_data_bt:
                dataRequest();
                break;
            case R.id.mainAct_download_bt:
                downloadRequest();
                break;
            case R.id.mainAct_upload_bt:
                uploadRequest();
                break;
            case R.id.mainAct_nestData_bt:
                nestRequest();
                break;
            case R.id.mainAct_sequence_bt:
                sequenceRequest();
                break;
            case R.id.mainAct_merge_bt:
                mergeRequest();
                break;
        }
    }

    public void dataRequest() {
        NetworkRequestImpl.create(this)
                .setUrl(BASE_PATH + "userInfo/getAllUserInfoLayer.action")
                .setMethod("GET")
                .setProgressMessage("正在加载中，请稍后...")
                .setDataType(new TypeToken<DataObject<User>>(){}.getType())
                .send(new DataRequestCallback<DataObject<User>>() {
                    @Override
                    public void onResult(DataObject<User> data, ResponseStatus status) {
                        Log.i(TAG, status.toString());
                        if(data != null) {
                            Log.i(TAG, data.toString());
                        }
                    }
                });
    }

    public void downloadRequest() {
        NetworkRequestImpl.create(this).setUrl(BASE_PATH + "file/LuaDemo.rar")
                .setDataClass(Boolean.class)
                .setDownloadFilePath(Environment.getExternalStorageDirectory().getAbsolutePath() + "/RxJava22/")
                .addParam("fileName", "LuaDemo.rar")
                .setMethod("POST")
                .setProgressMessage("正在加载中，请稍后...", ProgressDialog.STYLE_HORIZONTAL)
                .download(new DataRequestCallback<Boolean>() {
                    @Override
                    public void onResult(Boolean data, ResponseStatus status) {
                        Log.i(TAG, status.toString());
                        if(data != null) {
                            Log.i(TAG, data.toString());
                        }
                    }
                });
    }

    public Map<String, Object> getUploadFileParam() {
        String basePath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/documents/";
        File file = new File(basePath);
        String[] fileNameArray = file.list();

        Map<String, Object> param = new ArrayMap<>();
        param.put("user.name", "Anima18");
        param.put("user.password", "123456");
        for(String fileName : fileNameArray) {
            Log.d(TAG, fileName);
            param.put(fileName, new FileObject(basePath + fileName));
        }
        return param;
    }

    public void uploadRequest() {

        NetworkRequestImpl.create(this)
                .setUrl(BASE_PATH + "security/security_uploadList.action")
                .setParams(getUploadFileParam())
                .setMethod(NetworkRequestImpl.POST)
                .setDataType(new TypeToken<DataObject<User>>(){}.getType())
                .setProgressMessage("正在上传中，请稍后后", ProgressDialog.STYLE_HORIZONTAL)
                .upload(new DataRequestCallback<DataObject<User>>() {
                    @Override
                    public void onResult(DataObject<User> data, ResponseStatus status) {
                        Log.i(TAG, status.toString());
                        if(data != null) {
                            Log.i(TAG, data.toString());
                        }
                    }
                });
    }

    public void nestRequest() {
        NetworkRequest request = NetworkRequestImpl.create(this)
                                    .setUrl(BASE_PATH + "userInfo/getAllUserInfoLayer.action")
                                    .setMethod("GET")
                                    .setProgressMessage("请求一，请稍后...")
                                    .setDataType(new TypeToken<DataObject<User>>(){}.getType())
                                    .dataRequest();

        NetworkRequestManager.create(request).nest(new NetworkRequestManager.NestFlatMapCallback<DataObject<User>>(){
            @Override
            public NetworkRequest flatMap(DataObject<User> userDataObject, ResponseStatus status, NetworkRequestManager manager) {
                Log.i(TAG, status.toString());
                if(userDataObject != null) {
                    Log.i(TAG, "first: "+ userDataObject.data.rows.get(0).getName());
                    return NetworkRequestImpl.create(MainActivity.this).setUrl(BASE_PATH + "file/LuaDemo.rar")
                            .setDataClass(Boolean.class)
                            .setDownloadFilePath(Environment.getExternalStorageDirectory().getAbsolutePath() + "/RxJava22/")
                            .addParam("fileName", "LuaDemo.rar")
                            .setMethod("POST")
                            .setProgressMessage("正在加载中，请稍后...", ProgressDialog.STYLE_HORIZONTAL)
                            .downloadRequest();
                }else {
                    manager.cancelSubscription();
                    return null;
                }
            }
        }).subscribe(new DataRequestCallback<Boolean>() {
            @Override
            public void onResult(Boolean data, ResponseStatus status) {
                Log.i(TAG, status.toString());
                if(data != null) {
                    Log.i(TAG, data.toString());
                }
            }
        });
    }

    public void sequenceRequest() {
        NetworkRequest request1 = NetworkRequestImpl.create(this)
                .setUrl(BASE_PATH + "userInfo/getAllUserInfoLayer.action")
                .setMethod("GET")
                .setProgressMessage("请求一...")
                .setDataType(new TypeToken<DataObject<User>>(){}.getType())
                .dataRequest();

        NetworkRequest request2 = NetworkRequestImpl.create(this).setUrl(BASE_PATH + "file/LuaDemo.rar")
                .setDataClass(Boolean.class)
                .setDownloadFilePath(Environment.getExternalStorageDirectory().getAbsolutePath() + "/RxJava22/")
                .addParam("fileName", "LuaDemo.rar")
                .setMethod("POST")
                .setProgressMessage("正在加载中，请稍后...", ProgressDialog.STYLE_HORIZONTAL)
                .downloadRequest();

        NetworkRequest request3 = NetworkRequestImpl.create(this)
                .setUrl(BASE_PATH + "security/security_uploadList.action")
                .setParams(getUploadFileParam())
                .setMethod(NetworkRequestImpl.POST)
                .setDataType(new TypeToken<DataObject<User>>(){}.getType())
                .setProgressMessage("正在上传中，请稍后后", ProgressDialog.STYLE_HORIZONTAL)
                .uploadRequest();

        NetworkRequestManager.create(request1).sequence(request2).sequence(request3).subscribe(new DataListRequestCallback<Boolean>() {
            @Override
            public void onResult(List<Boolean> resultData, ResponseStatus status) {
                Log.i(TAG, status.toString());
                if(resultData != null) {
                    Log.i(TAG, resultData.toString());
                }
            }
        });
    }

    public void mergeRequest() {
        NetworkRequest request1 = NetworkRequestImpl.create(this)
                .setUrl(BASE_PATH + "userInfo/getAllUserInfoLayer.action")
                .setMethod("GET")
                .setProgressMessage("请求一...")
                .setDataType(new TypeToken<DataObject<User>>(){}.getType())
                .dataRequest();

        /*NetworkRequest request2 = NetworkRequestImpl.create(this).setUrl(BASE_PATH + "file/LuaDemo.rar")
                .setDataClass(Boolean.class)
                .setDownloadFilePath(Environment.getExternalStorageDirectory().getAbsolutePath() + "/RxJava22/")
                .addParam("fileName", "LuaDemo.rar")
                .setMethod("POST")
                .setProgressMessage("正在加载中，请稍后...", ProgressDialog.STYLE_HORIZONTAL)
                .downloadRequest();*/

        NetworkRequest request3 = NetworkRequestImpl.create(this)
                .setUrl(BASE_PATH + "security/security_uploadList.action")
                .setParams(getUploadFileParam())
                .setMethod(NetworkRequestImpl.POST)
                .setDataType(new TypeToken<DataObject<User>>(){}.getType())
                .setProgressMessage("正在上传中，请稍后后", ProgressDialog.STYLE_HORIZONTAL)
                .uploadRequest();

        NetworkRequestManager.create(request1).merge(request3).subscribe(new DataListRequestCallback<Object>() {
            @Override
            public void onResult(List<Object> dataList, ResponseStatus status) {
                Log.i(TAG, status.toString());
                if(dataList != null) {
                    Log.i(TAG, dataList.toString());
                }
            }
        });
    }
}
