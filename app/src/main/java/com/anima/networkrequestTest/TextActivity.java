package com.anima.networkrequestTest;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.anima.networkrequest.NetworkRequest;
import com.anima.networkrequest.callback.DataListStatusCallback;
import com.anima.networkrequest.entity.RequestParam;
import com.anima.networkrequest.entity.ViewModelStatus;
import com.anima.networkrequestTest.entity.VersionInfo;

import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Created by jianjianhong on 20-5-9
 */
public class TextActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);



        String url = "";
        new NetworkRequest<VersionInfo>(this)
                .url(url)
                .method(RequestParam.Method.GET)
                .dataClass(VersionInfo.class)
                .getList(new DataListStatusCallback<VersionInfo>() {
                    @Override
                    public void onStatus(@NotNull ViewModelStatus<List<VersionInfo>> t) {

                    }
                });
    }
}
