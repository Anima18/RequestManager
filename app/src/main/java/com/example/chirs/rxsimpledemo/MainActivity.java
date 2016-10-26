package com.example.chirs.rxsimpledemo;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.example.chirs.rxsimpledemo.entity.ActivityClass;
import com.example.requestmanager.okhttp.OkHttpUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Response;
import okio.BufferedSink;
import okio.Okio;

public class MainActivity extends BaseActivity {

    private ListView dataLv;
    private List<ActivityClass> activityClassList;
    private List<String> urlList;

    private final static String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        urlList = initData();
    }

    @Override
    protected void onResume() {
        super.onResume();
        //Runtime.getRuntime().gc();
        int maxMemory = ((int) Runtime.getRuntime().maxMemory())/1024/1024;
        //应用程序已获得内存
        long totalMemory = ((int) Runtime.getRuntime().totalMemory())/1024/1024;
        //应用程序已获得内存中未使用内存
        long freeMemory = ((int) Runtime.getRuntime().freeMemory())/1024/1024;
        //应用程序已使用的内存
        long usedMemory = totalMemory - freeMemory;
        Log.i(TAG, "---> maxMemory=" + maxMemory + "M,totalMemory=" + totalMemory + "M,freeMemory=" + freeMemory + "M,usedMemory="+usedMemory+"M");
        //Runtime.getRuntime().gc();
    }

    private void initView() {
        dataLv = (ListView)findViewById(R.id.data_lv);

        ListDataAdapter adapter = new ListDataAdapter(this, getActivityClassList());
        dataLv.setAdapter(adapter);
        dataLv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ActivityClass ac = activityClassList.get(position);
                if(ac != null && ac.getActivityClass() != null) {
                    Intent intent = new Intent(MainActivity.this, ac.getActivityClass());
                    startActivity(intent);
                }else {
                    Toast.makeText(MainActivity.this, "没有实现", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public List<ActivityClass> getActivityClassList() {
        activityClassList = new ArrayList<>();

        activityClassList.add(new ActivityClass("GET方式获取数据", GetDataActivity.class));
        activityClassList.add(new ActivityClass("POST方式获取数据", PostDataActivity.class));
        activityClassList.add(new ActivityClass("获取图片资源", GetBitmapDataActivity.class));
        activityClassList.add(new ActivityClass("上传文件", UploadFileActivity.class));
        activityClassList.add(new ActivityClass("请求嵌套", GetNestedObjectDataActivity.class));
        activityClassList.add(new ActivityClass("顺序请求", GetObjectDataListActivity.class));
        activityClassList.add(new ActivityClass("下载文件", DownloadFileActivity.class));
        return activityClassList;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_getbitmap, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.okAct_download:
                download();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void download() {
        showProgress("正在下载中");
        final String basePath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/RxJava/";
        createDir(basePath);
        for(final String url : urlList) {
            new AsyncTask<String, Void, String>()  {
                @Override
                protected void onPostExecute(String s) {
                    super.onPostExecute(s);
                    if(urlList.indexOf(s) == (urlList.size() -1)) {
                        hideProgress();
                        Toast.makeText(MainActivity.this, "下载完成", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                protected String doInBackground(String... params) {
                    String url = params[0];
                    String filename = url.substring(url.lastIndexOf("/"));
                    Call call = null;
                    try {
                        call = OkHttpUtils.get(MainActivity.this, url);
                        Response response = call.execute();
                        File downloadedFile = new File(basePath, filename);
                        BufferedSink sink = Okio.buffer(Okio.sink(downloadedFile));
                        sink.writeAll(response.body().source());
                        sink.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    return url;
                }
            }.execute(url);

        }
    }

    public static boolean createDir(String destDirName) {
        boolean flag = false;
        File dir = new File(destDirName);
        if (!dir.exists()) {
            if (dir.mkdirs()) {
                flag = true;
            }
        }else {
            flag = true;
        }
        return flag;
    }

    public List<String> initData() {
        List<String> urlList = new ArrayList<>();
        urlList.add(BASE_PATH + "file/doughnut.png");
        urlList.add(BASE_PATH + "file/darts.png");
        urlList.add(BASE_PATH + "file/life-saver.png");
        urlList.add(BASE_PATH + "file/beer-cap.png");
        urlList.add(BASE_PATH + "file/compass.png");
        urlList.add(BASE_PATH + "file/clock.png");
        urlList.add(BASE_PATH + "file/beer-cap-dribble.png");
        urlList.add(BASE_PATH + "file/1.png");
        urlList.add(BASE_PATH + "file/2.png");
        urlList.add(BASE_PATH + "file/3.png");
        urlList.add(BASE_PATH + "file/4.png");
        urlList.add(BASE_PATH + "file/5.png");
        urlList.add(BASE_PATH + "file/6.png");
        urlList.add(BASE_PATH + "file/7.png");
        urlList.add(BASE_PATH + "file/Kalimba.mp3");
        urlList.add(BASE_PATH + "file/OkHttp发布.rar");
        urlList.add(BASE_PATH + "file/app-debug.apk");
        return urlList;
    }
}
