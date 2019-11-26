package com.anima.networkrequestTest

import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.anima.networkrequest.*
import com.anima.networkrequest.data.okhttp.USER_SEEION_ID
import com.anima.networkrequest.entity.RequestParam
import com.anima.networkrequest.util.sharedprefs.UserInfoSharedPreferences

class MainActivity : ScopedActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    fun login(view: View) {
        NetworkRequest<UserInfo>(this)
            .url("http://192.168.60.146:8080/userright/loginVerify.do")
            .coroutineScope(this)
            .addParam("username", "utadmin")
            .addParam("pwd", "e10adc3949ba59abbe56e057f20f883e")
            .method(RequestParam.Method.POST)
            .dataClass(UserInfo::class.java)
            .loadingMessage("正在登录中,请稍后...")
            .getObject(object :DataObjectCallback<UserInfo> {
                override fun onSuccess(data: UserInfo) {
                    Toast.makeText(this@MainActivity, data.user.userName, Toast.LENGTH_SHORT).show()
                    UserInfoSharedPreferences.getInstance(this@MainActivity).putStringValue(USER_SEEION_ID, data.sessionid)
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
            .getList(object: DataListCallback<MajorTable> {
                override fun onSuccess(t: List<MajorTable>) {
                    Toast.makeText(this@MainActivity, "${t.size}", Toast.LENGTH_SHORT).show()
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
            .getPageData(object: DataPagingCallback<Defect> {
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
            .getList(object: DataListCallback<GitHubUser> {
                override fun onSuccess(t: List<GitHubUser>) {
                    Toast.makeText(this@MainActivity, "${t.size}", Toast.LENGTH_SHORT).show()
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
}
