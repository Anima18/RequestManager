RequestManager是一个Android的网络请求框架，通过解耦请求者和响应者简化多重网络传递。支持kotlin和Java。  

#### 支持单一请求
1. 数据请求，支持GET和POST请求
2. 支持请求数据解析和自定义解析
2. 支持请求提示框自动显示和关闭
3. 支持请求取消
4. 支持页面退出取消页面所有请求

#### 支持网络请求流
网络请求流是指多个单一请求的串行或者并行，包括： 
1. 顺序请求，先发送请求一再发送请求二，返回结果保证请求顺序
2. 并发请求，同时发送请求一和请求二，返回结果保证请求顺序

#### 支持请求管理
在发送单一请求或者多重请求时，会自动显示loading提示框，请求结束后会自动关闭。在请求过程中，如果手动关闭loading提示框，会取消请求。


## 安装



## 示例
### 单一网络请求
有3个方法：
1. getObject，返回单一数据对象
2. getList, 返回数据对象列表
3. getPageData, 返回分页数据对象列表和总数目

#### getObject
> Kotlin
```
NetworkRequest<UserInfo>(this)
            .url("http://xxxx/userright/loginVerify.do")
            .coroutineScope(this)
            .addParam("username", "xxx")
            .addParam("pwd", "xxxx")
            .method(RequestParam.Method.POST)
            .dataClass(UserInfo::class.java)
            .loadingMessage("正在登录中,请稍后...")
            .getObject(object :DataObjectCallback<UserInfo> {
                override fun onSuccess(data: UserInfo) {
                    Toast.makeText(this@MainActivity, data.user.userName, Toast.LENGTH_SHORT).show()
                }

                override fun onFailure(message: String) {
                    Toast.makeText(this@MainActivity, message, Toast.LENGTH_SHORT).show()
                }
            })
```

> Java

```
new NetworkRequest<UserInfo>(this)
                .url("http://xxxx/userright/loginVerify.do")
                .addParam("username", "xx")
                .addParam("pwd", "xxx")
                .method(RequestParam.Method.POST)
                .dataClass(UserInfo.class)
                .loadingMessage("正在登录中,请稍后...")
                .getObject(new DataObjectCallback<UserInfo>() {
                    @Override
                    public void onFailure(@NotNull String s) {
                        Log.i("dddddd", s);
                    }

                    @Override
                    public void onSuccess(UserInfo userInfo) {
                        Log.i("dddddd", userInfo.toString());
                    }
                });
```

> url 设置请求地址  
> addParam 设置请求参数  
> method 设置请求方式   
> dataClass 设置请求解析对象，[具体请阅读请求解析](https://note.youdao.com/)  
> loadingMessage 设置请求提示，如果设置信息，会显示请求提示框，否则不会显示。  
> coroutineScope 设置请求域。[具体请阅读请求管理](https://note.youdao.com/)


#### getList  
> Kotlin

```
NetworkRequest<MajorTable>(this)
            ...
            .getList(object: DataListCallback<MajorTable> {
                override fun onSuccess(t: List<MajorTable>) {
                    Toast.makeText(this@MainActivity, "${t.size}", Toast.LENGTH_SHORT).show()
                }

                override fun onFailure(message: String) {
                    Toast.makeText(this@MainActivity, message, Toast.LENGTH_SHORT).show()
                }
            })
```
> Java

```
new NetworkRequest<MajorTable>(this)
                ...
                .getList(new DataListCallback<MajorTable>() {
                    @Override
                    public void onSuccess(@NotNull List<? extends MajorTable> list) {
                        Log.i("dddddd", list.toString());
                    }

                    @Override
                    public void onFailure(@NotNull String s) {
                        Log.i("dddddd", s);
                    }
                });
```

#### getPageData  
> Kotlin

```
NetworkRequest<Defect>(this)
            ...
            .getPageData(object: DataPagingCallback<Defect> {
                override fun onSuccess(dataList: List<Defect>, total: Int) {
                    Toast.makeText(this@MainActivity, "${total}", Toast.LENGTH_SHORT).show()
                }

                override fun onFailure(message: String) {
                    Toast.makeText(this@MainActivity, message, Toast.LENGTH_SHORT).show()
                }
            })
```
> Java

```
new NetworkRequest<Defect>(this)
                ...
                .getPageData(new DataPagingCallback<Defect>() {
                    @Override
                    public void onSuccess(@NotNull List<? extends Defect> list, int i) {
                        Log.i("dddddd", list.toString());
                    }

                    @Override
                    public void onFailure(@NotNull String s) {
                        Log.i("dddddd",s);
                    }
                });
```


### 网络请求流

#### 顺序请求

```
val request1 = NetworkRequest<GitHubUser>(this)
    .url("https://api.github.com/users/yeasy/followers?page=1")
    .method(RequestParam.Method.GET)
    .dataClass(GitHubUser::class.java)
    .dataParser(GitHubUserParser<GitHubUser>())
    .create()

var request2 = NetworkRequest<MajorTable>(this)
    .url("http://xxx/utdtpower/datasource/getOptionsOfDatasource.do")
    .addParam("datasourceFcode", "majortableFcode")
    .method(RequestParam.Method.POST)
    .dataClass(MajorTable::class.java)
    .dataFormat(RequestParam.DataFormat.LIST)
    .create()

val request3 = NetworkRequest<UserInfo>(this)
    .url("http://xxxx/userright/loginVerify.do")
    .addParam("username", "xxx")
    .addParam("pwd", "xxx")
    .method(RequestParam.Method.POST)
    .dataClass(UserInfo::class.java)
    .dataFormat(RequestParam.DataFormat.OBJECT)
    .create()

val request4 = NetworkRequest<Defect>(this)
    .url("http://xxxx/utqx/getQueryData.do?rows=30&page=1&sidx=utdtversion&sord=desc")
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
```  

> 通过create方法创建一个待请求对象   
> sequence方法把请求添加到顺序请求列表  
> parallel方法把请求添加到并发请求列表  
> collect方法把请求列表的结果按顺序收集


## 请求解析


## 请求管理 

## Author

Anima18, 591151451@qq.com

## License

RequestManager is available under the MIT license. See the LICENSE file for more info.
