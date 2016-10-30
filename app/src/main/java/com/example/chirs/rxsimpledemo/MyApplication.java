package com.example.chirs.rxsimpledemo;

import android.app.Application;
import android.content.Context;

import com.squareup.leakcanary.AndroidExcludedRefs;
import com.squareup.leakcanary.DisplayLeakService;
import com.squareup.leakcanary.LeakCanary;
import com.squareup.leakcanary.RefWatcher;


/**
 * Created by jianjianhong on 2016/9/1.
 */
public class MyApplication extends Application {
    private RefWatcher refWatcher;

    public static RefWatcher getRefWatcher(Context context) {
        MyApplication application = (MyApplication) context.getApplicationContext();
        return application.refWatcher;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        refWatcher = LeakCanary.install(this, DisplayLeakService.class, AndroidExcludedRefs.createAppDefaults().build());
    }
}
