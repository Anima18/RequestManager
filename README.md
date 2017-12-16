# RequestManager
RequestManager是一个Android的网络请求框架，通过解耦请求者和响应者简化多重网络传递。   

#### 支持单一请求
1. 数据请求，支持GET和POST请求
2. 文件下载请求，并显示下载进度
3. 文件上传请求，并显示上传经典

#### 支持多重网络请求
多重网络请求是指多个单一请求的串行或者并行，包括：
1. 嵌套请求，请求二可以在请求一的结果基础上发送
2. 顺序请求，先发送请求一再发送请求二，返回结果保证请求顺序
3. 并发请求，同时发送请求一和请求二，返回结果保证请求顺序

#### 支持请求管理
在发送单一请求或者多重请求时，会自动显示loading提示框，请求结束后会自动关闭。在请求过程中，如果手动关闭loading提示框，会取消请求。


## 安装



## 示例
#### NetworkRequest 配置方法
1. request方法，创建一个NetworkRequst对象
2. url方法，设置url链接
3. method方法，设置请求方式，比如GET或者POST
4. dataClass方法，设置请求返回数据的类型
5. addParam方法，设置请求参数
6. send方法，数据请求的成功和失败回调
7. download方法，下载请求的成功和失败回调
8. upload方法，上传请求的成功和失败回调

#### 数据请求

```
NetworkRequestImpl.create(this)
    .setUrl(BASE_PATH + "userInfo/getAllUserInfoLayer.action")
    .setMethod("GET")
    .setProgressMessage("正在加载中，请稍后...")
    .setDataType(new TypeToken<DataObject<User>>(){}.getType())
    .send(new DataRequestCallback<DataObject<User>>() {
        @Override
        public void onResult(DataObject<User> data, ResponseStatus status) {
            
        }
    });
```

#### 文件下载
```
NetworkRequestImpl.create(this).setUrl(BASE_PATH + "file/text.zip")
    .setDataClass(Boolean.class)
    .setDownloadFilePath(Environment.getExternalStorageDirectory().getAbsolutePath() + "/RxJava22/")
    .setDownloadFileName("text.zip")
    .setMethod("POST")
    .setProgressMessage("正在加载中，请稍后...", ProgressDialog.STYLE_HORIZONTAL)
    .download(new DataRequestCallback<Boolean>() {
        @Override
        public void onResult(Boolean data, ResponseStatus status) {
            
        }
    });
```

#### 文件上传

```
NetworkRequestImpl.create(this)
    .setUrl(BASE_PATH + "security/security_uploadList.action")
    .setParams(getUploadFileParam())
    .setMethod(NetworkRequestImpl.POST)
    .setDataType(new TypeToken<DataObject<User>>(){}.getType())
    .setProgressMessage("正在上传中，请稍后后", ProgressDialog.STYLE_HORIZONTAL)
    .upload(new DataRequestCallback<DataObject<User>>() {
        @Override
        public void onResult(DataObject<User> data, ResponseStatus status) {
            
        }
    });
```

#### NetworkRequestManager
NetworkRequestManager是NetworkRequest的管理器，多重网络请求是指多个单一请求的串行或者并行.  
1. create方法，初始化管理器
2. nest方法，嵌套一个请求
4. sequence方法，顺序一个请求
6. merge方法，并发一个请求
7. subscribe方法，并发请求的成功和失败回调方法

#### 嵌套请求

```
NetworkRequest request = NetworkRequestImpl.create(this)
        .setUrl(BASE_PATH + "userInfo/getAllUserInfoLayer.action")
        .setMethod("GET")
        .setProgressMessage("请求一，请稍后...")
        .setDataType(new TypeToken<DataObject<User>>(){}.getType())
        .dataRequest();

NetworkRequestManager.create(request).nest(new NetworkRequestManager.NestFlatMapCallback<DataObject<User>>(){
    @Override
    public NetworkRequest flatMap(DataObject<User> userDataObject, ResponseStatus status, NetworkRequestManager manager) {

            return NetworkRequestImpl.create(MainActivity.this).setUrl(BASE_PATH + "file/gxcz_1.1.2.ipa")
                    .setDataClass(Boolean.class)
                    .setDownloadFilePath(Environment.getExternalStorageDirectory().getAbsolutePath() + "/RxJava22/")
                    .addParam("fileName", "gxcz_1.1.2.ipa")
                    .setMethod("POST")
                    .setProgressMessage("正在加载中，请稍后...", ProgressDialog.STYLE_HORIZONTAL)
                    .downloadRequest();
    
    }
}).subscribe(new DataRequestCallback<Boolean>() {
    @Override
    public void onResult(Boolean data, ResponseStatus status) {
        
    }
});
```

#### 顺序请求

```
NetworkRequest request1 = NetworkRequestImpl.create(this)
	.setUrl(BASE_PATH + "userInfo/getAllUserInfoLayer.action")
	.setMethod("GET")
	.setProgressMessage("请求一...")
	.setDataType(new TypeToken<DataObject<User>>(){}.getType())
	.dataRequest();

NetworkRequest request2 = NetworkRequestImpl.create(this).setUrl(BASE_PATH + "file/gxcz_1.1.2.ipa")
	.setDataClass(Boolean.class)
	.setDownloadFilePath(Environment.getExternalStorageDirectory().getAbsolutePath() + "/RxJava22/")
	.addParam("fileName", "gxcz_1.1.2.ipa")
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
	
    }
});
```

#### 并发请求

```
NetworkRequest request1 = NetworkRequestImpl.create(this)
	.setUrl(BASE_PATH + "userInfo/getAllUserInfoLayer.action")
	.setMethod("GET")
	.setProgressMessage("请求一...")
	.setDataType(new TypeToken<DataObject<User>>(){}.getType())
	.dataRequest();

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
	
    }
});
```

## Author

Anima18, 591151451@qq.com

## License

JJHRequest is available under the MIT license. See the LICENSE file for more info.
