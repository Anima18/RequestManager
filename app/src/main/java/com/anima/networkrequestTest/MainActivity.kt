package com.anima.networkrequestTest

import android.Manifest
import android.os.Bundle
import android.os.Environment
import android.view.View
import android.widget.Toast
import com.anima.networkrequest.*
import com.anima.networkrequest.callback.DataListCallback
import com.anima.networkrequest.callback.DataObjectCallback
import com.anima.networkrequest.callback.DataPagingCallback
import com.anima.networkrequest.entity.RequestParam
import com.anima.networkrequestTest.entity.ResultDataParser
import com.google.gson.Gson
import com.tbruyelle.rxpermissions.RxPermissions
import java.io.File
import java.net.URLEncoder
import java.nio.charset.StandardCharsets
import java.util.HashMap


class MainActivity : ScopedActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        RxPermissions(this)
            .request(Manifest.permission.WRITE_EXTERNAL_STORAGE)
            .subscribe({ granted ->
                if (granted) { // Always true pre-M
                    // I can control the camera now
                } else {
                    // Oups permission denied
                }
            });

    }

    fun login(view: View) {
        /*NetworkRequest<UserInfo>(this)
            .url("http://192.168.66.132:9021/rest/users/login")
            .coroutineScope(this)
            .addParam("username", "admin")
            .addParam("password", "e10adc3949ba59abbe56e057f20f883e")
            .method(RequestParam.Method.POST)
            .dataClass(UserInfo::class.java)
            .loadingMessage("正在登录中,请稍后...")
            .getObject(object :DataObjectCallback<UserInfo> {
                override fun onSuccess(data: UserInfo) {
                    Toast.makeText(this@MainActivity, data.user.userName, Toast.LENGTH_SHORT).show()
                    UserInfoSharedPreferences.getInstance(this@MainActivity).putStringValue(USER_SEEION_ID, data.token)
                }

                override fun onFailure(message: String) {
                    Toast.makeText(this@MainActivity, message, Toast.LENGTH_SHORT).show()
                }
            })*/

        val argServiceParam = HashMap<String, String>()
        argServiceParam["UserId"] = "5be268f1-c4d2-46f9-b682-90247f07f369"
        argServiceParam["UserName"] = "张三"
        argServiceParam["Token"] = "89CFB5886913CCDE6881DD2C2A35F68E35638E7FE54965A20A11D4DF016D955BE2F490583A1C2915042E7DBEFDACB9470B02DF7270482DDD9C5B08AE4666ED00"
        argServiceParam["CarCode"] = "粤C00001"
        argServiceParam["Jiashiren"] = "张三"
        argServiceParam["MachineId"] = "2cd119436631ba2d"

        var paramStr = Gson().toJson(argServiceParam)
        paramStr = URLEncoder.encode(paramStr, StandardCharsets.UTF_8.name())

        NetworkRequest<String>(this)
            .url("http://61.145.230.152:8735/WZJCYWebService/WZJCSubmitGrant")
            .coroutineScope(this)
            .addParam("argServiceParam", paramStr)
            .asJson(true)
            .method(RequestParam.Method.POST)
            .dataClass(String::class.java)
            .dataParser(ResultDataParser<String>())
            .loadingMessage("正在登录中,请稍后...")
            .getObject(object :
                DataObjectCallback<String> {
                override fun onSuccess(data: String?) {
                    Toast.makeText(this@MainActivity, data, Toast.LENGTH_SHORT).show()

                }

                override fun onFailure(message: String) {
                    Toast.makeText(this@MainActivity, message, Toast.LENGTH_SHORT).show()
                }
            })
    }

    fun getList(view: View) {
        NetworkRequest<MajorTable>(this)
            .url("http://192.168.60.146:8080/utdtpower/datasource/getOptionsOfDatasource.do")
            .coroutineScope(this)
            .addParam("datasourceFcode", "majortableFcode")
            .method(RequestParam.Method.POST)
            .dataClass(MajorTable::class.java)
            .loadingMessage("正在登录中,请稍后...")
            .getList(object:
                DataListCallback<MajorTable> {
                override fun onSuccess(t: List<MajorTable>?) {
                    Toast.makeText(this@MainActivity, "${t?.size}", Toast.LENGTH_SHORT).show()
                }

                override fun onFailure(message: String) {
                    Toast.makeText(this@MainActivity, message, Toast.LENGTH_SHORT).show()
                }
            })
    }

    fun getPaging(view: View) {
        NetworkRequest<Defect>(this)
            .url("http://192.168.60.146:8080/utqx/getQueryData.do?rows=30&page=1&sidx=utdtversion&sord=desc")
            .coroutineScope(this)
            .method(RequestParam.Method.GET)
            .dataClass(Defect::class.java)
            .loadingMessage("正在登录中,请稍后...")
            .getPageData(object:
                DataPagingCallback<Defect> {
                override fun onSuccess(dataList: List<Defect>, total: Int) {
                    Toast.makeText(this@MainActivity, "${total}", Toast.LENGTH_SHORT).show()
                }

                override fun onFailure(message: String) {
                    Toast.makeText(this@MainActivity, message, Toast.LENGTH_SHORT).show()
                }
            })
    }

    fun customData(view: View) {
        NetworkRequest<GitHubUser>(this)
            .url("https://api.github.com/users/yeasy/followers?page=1")
            .coroutineScope(this)
            .method(RequestParam.Method.GET)
            .dataClass(GitHubUser::class.java)
            .dataParser(GitHubUserParser<GitHubUser>())
            //.loadingMessage("正在登录中,请稍后...")
            .getList(object:
                DataListCallback<GitHubUser> {
                override fun onSuccess(t: List<GitHubUser>?) {
                    Toast.makeText(this@MainActivity, "${t?.size}", Toast.LENGTH_SHORT).show()
                }

                override fun onFailure(message: String) {
                    Toast.makeText(this@MainActivity, message, Toast.LENGTH_SHORT).show()
                }
            })
    }

    fun sequenceRequests(view: View) {
        val request1 = NetworkRequest<GitHubUser>(this)
            .url("https://api.github.com/users/yeasy/followers?page=1")
            .method(RequestParam.Method.GET)
            .dataClass(GitHubUser::class.java)
            .dataParser(GitHubUserParser<Any>())
            .create()

        var request2 = NetworkRequest<MajorTable>(this)
            .url("http://192.168.60.146:8080/utdtpower/datasource/getOptionsOfDatasource.do")
            .addParam("datasourceFcode", "majortableFcode")
            .method(RequestParam.Method.POST)
            .dataClass(MajorTable::class.java)
            .dataFormat(RequestParam.DataFormat.LIST)
            .create()

        val request3 = NetworkRequest<UserInfo>(this)
            .url("http://192.168.60.146:8080/userright/loginVerify.do")
            .addParam("username", "utadmin")
            .addParam("pwd", "e10adc3949ba59abbe56e057f20f883e")
            .method(RequestParam.Method.POST)
            .dataClass(UserInfo::class.java)
            .dataFormat(RequestParam.DataFormat.OBJECT)
            .create()

        val request4 = NetworkRequest<Defect>(this)
            .url("http://192.168.60.146:8080/utqx/getQueryData.do?rows=30&page=1&sidx=utdtversion&sord=desc")
            .method(RequestParam.Method.GET)
            .dataClass(Defect::class.java)
            .dataFormat(RequestParam.DataFormat.PAGELIST)
            .create()

        RequestStream.create(this).showMessage("顺序请求...").sequence(request1, request2, request3, request4).collect(object: RequestStream.OnCollectListener {
            override fun onSuccess(dataList: List<*>) {
                Toast.makeText(this@MainActivity, "${dataList.size}", Toast.LENGTH_SHORT).show()
            }

            override fun onFailure(message: String) {
                Toast.makeText(this@MainActivity, message, Toast.LENGTH_SHORT).show()
            }
        })
    }

    fun parallelRequests(view: View) {
        val request1 = NetworkRequest<GitHubUser>(this)
            .url("https://api.github.com/users/yeasy/followers?page=1")
            .method(RequestParam.Method.GET)
            .dataClass(GitHubUser::class.java)
            .dataParser(GitHubUserParser<Any>())
            .create()

        var request2 = NetworkRequest<MajorTable>(this)
            .url("http://192.168.60.146:8080/utdtpower/datasource/getOptionsOfDatasource.do")
            .addParam("datasourceFcode", "majortableFcode")
            .method(RequestParam.Method.POST)
            .dataClass(MajorTable::class.java)
            .dataFormat(RequestParam.DataFormat.LIST)
            .create()

        val request3 = NetworkRequest<UserInfo>(this)
            .url("http://192.168.60.146:8080/userright/loginVerify.do")
            .addParam("username", "utadmin")
            .addParam("pwd", "e10adc3949ba59abbe56e057f20f883e")
            .method(RequestParam.Method.POST)
            .dataClass(UserInfo::class.java)
            .dataFormat(RequestParam.DataFormat.OBJECT)
            .create()

        val request4 = NetworkRequest<Defect>(this)
            .url("http://192.168.60.146:8080/utqx/getQueryData.do?rows=30&page=1&sidx=utdtversion&sord=desc")
            .method(RequestParam.Method.GET)
            .dataClass(Defect::class.java)
            .dataFormat(RequestParam.DataFormat.PAGELIST)
            .create()

        RequestStream.create(this).showMessage("并发请求...").parallel(request1, request2, request3, request4).collect(object: RequestStream.OnCollectListener {
            override fun onSuccess(dataList: List<*>) {
                Toast.makeText(this@MainActivity, "${dataList.size}", Toast.LENGTH_SHORT).show()
            }

            override fun onFailure(message: String) {
                Toast.makeText(this@MainActivity, message, Toast.LENGTH_SHORT).show()
            }
        })
    }

    fun downloadRequests(view: View) {
        NetworkRequest<String>(this)
            .url("http://114.67.65.199:8080/software/Android/protect.eye_9.6_88.apk")
            .coroutineScope(this)
            .method(RequestParam.Method.POST)
            .dataClass(String::class.java)
            .loadingMessage("正在下载中,请稍后...")
            .downloadFilePath(this.getExternalFilesDir(null)?.getAbsolutePath() + "/RxJava22/")
            .downloadFileName("ddddddddddddd.apk")
            .download(object :
                DataObjectCallback<String> {
                override fun onSuccess(data: String?) {
                    Toast.makeText(this@MainActivity, data, Toast.LENGTH_SHORT).show()
                 }

                override fun onFailure(message: String) {
                    Toast.makeText(this@MainActivity, message, Toast.LENGTH_SHORT).show()
                }
            })
    }

    fun uploadRequests(view: View) {
        var files = listOf<File>(File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/atom/test.pdf"))
        NetworkRequest<String>(this)
            .url("http://114.67.65.199:8080/software/Android/protect.eye_9.6_88.apk")
            .coroutineScope(this)
            .method(RequestParam.Method.POST)
            .dataClass(String::class.java)
            .loadingMessage("正在下载中,请稍后...")
            .uploadFiles(files)
            .upload(object :
                DataObjectCallback<String> {
                override fun onSuccess(data: String?) {
                    Toast.makeText(this@MainActivity, data, Toast.LENGTH_SHORT).show()
                }

                override fun onFailure(message: String) {
                    Toast.makeText(this@MainActivity, message, Toast.LENGTH_SHORT).show()
                }
            })
    }
}
