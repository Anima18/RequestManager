package com.ut.requsetmanager.callback;

import android.app.Activity;
import android.content.Context;

import com.ut.requsetmanager.entity.WebServiceParam;
import com.ut.requsetmanager.exception.ServiceErrorException;
import com.ut.requsetmanager.network.NetworkTaskImpl;
import com.ut.requsetmanager.util.FileUtil;

import java.io.BufferedInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * Created by jianjianhong on 2017/11/19.
 */

public class DownloadResponseCallback implements Callback {
    private NetworkTaskImpl.ProgressTaskCallback dataCallback;
    private WebServiceParam param;
    private Context context;

    public DownloadResponseCallback(Context context, WebServiceParam param, NetworkTaskImpl.ProgressTaskCallback dataCallback) {
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
            InputStream is = response.body().byteStream();
            BufferedInputStream input = new BufferedInputStream(is);
            FileUtil.createDir(param.getDownloadFilePath());
            OutputStream output = new FileOutputStream(param.getDownloadFilePath() + param.getDownloadFileName());

            byte[] data = new byte[1024];
            int count = 0;

            while ((count = input.read(data)) != -1) {
                output.write(data, 0, count);
            }
            output.flush();
            output.close();
            input.close();

            ((Activity) context).runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    dataCallback.onSuccess(true);
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
}
