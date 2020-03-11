package com.anima.networkrequest

import android.app.ProgressDialog
import android.content.Context
import android.content.DialogInterface
import android.os.Message
import android.util.Log
import com.anima.networkrequest.data.okhttp.OkHttpTask
import com.anima.networkrequest.data.okhttp.dataConvert.ResponseParser
import com.anima.networkrequest.entity.RequestParam
import com.anima.networkrequest.exception.ExceptionUtil
import com.anima.networkrequest.indicator.IndicatorDialog
import kotlinx.coroutines.*
import java.io.File


/**
 * Created by jianjianhong on 19-11-4
 */
class NetworkRequest<T>(private val context: Context) : IRequest<T>, DialogInterface.OnCancelListener {

    public var param: RequestParam = RequestParam()
    private var mainScope: CoroutineScope? = null
    private var job: Job? = null

    constructor(context: Context, mainScope: CoroutineScope) : this(context) {
        this.mainScope = mainScope
    }

    override fun url(url: String): IRequest<T> {
        this.param.url = url
        return this
    }

    override fun method(method: RequestParam.Method): IRequest<T> {
        this.param.method = method
        return this
    }

    override fun coroutineScope(scope: CoroutineScope): IRequest<T> {
        this.mainScope = scope
        return this
    }

    override fun dataClass(dataClass: Class<*>): IRequest<T> {
        this.param.dataClass = dataClass

        return this
    }

    override fun dataParser(parser: ResponseParser): IRequest<T> {
        this.param.dataParser = parser
        return this
    }

    override fun params(params: MutableMap<String, String>): IRequest<T> {
        this.param.params = params
        return this
    }

    override fun addParam(key: String, value: String): IRequest<T> {
        if(this.param.params == null) {
            this.param.params = mutableMapOf(key to value)
        }else{
            this.param.params?.put(key, value)
        }
        return this
    }

    override fun dataFormat(format: RequestParam.DataFormat): IRequest<T> {
        this.param.dataFormat = format
        return this
    }

    override fun loadingMessage(message: String): IRequest<T> {
        this.param.loadingMessage = message
        return this
    }

    override fun downloadFilePath(filePath: String): IRequest<T> {
        this.param.downloadFilePath = filePath
        return this
    }

    override fun downloadFileName(fileName: String): IRequest<T> {
        this.param.downloadFileName = fileName
        return this
    }

    override fun uploadFiles(files: List<File>): IRequest<T> {
        this.param.uploadFiles = files;
        return this;
    }

    override fun getObject(objectCallback: DataObjectCallback<T>): Job {
        var processDialog = showRequestIndicator()
        this.param.dataFormat = RequestParam.DataFormat.OBJECT
        if (mainScope == null)
            mainScope = MainScope()
        job = mainScope!!.launch {
            try {
                val resultData = OkHttpTask.getInstance(context).dataTask<T>(param)
                GlobalScope.launch(Dispatchers.Main) {
                    processDialog?.hideProgress()
                    objectCallback.onSuccess(resultData.getResult()!! as T)
                }
            }catch (e: Exception) {
                val message = ExceptionUtil.analysisException(e)
                GlobalScope.launch(Dispatchers.Main) {
                    processDialog?.hideProgress()
                    message?.let { objectCallback.onFailure(it) }
                }
            }
        }
        return job as Job
    }

    override fun create(): NetworkRequest<T> {
        return this
    }

    override fun getList(callback: DataListCallback<T>): Job {
        var processDialog = showRequestIndicator()
        this.param.dataFormat = RequestParam.DataFormat.LIST
        if (mainScope == null)
            mainScope = MainScope()
        job = mainScope!!.launch {
            try {
                val resultData = OkHttpTask.getInstance(context).dataTask<T>(param)
                GlobalScope.launch(Dispatchers.Main) {
                    processDialog?.hideProgress()
                    callback.onSuccess(resultData.getResult()!! as List<T>)
                }
            }catch (e: Exception) {
                val message = ExceptionUtil.analysisException(e)
                GlobalScope.launch(Dispatchers.Main) {
                    processDialog?.hideProgress()
                    message?.let { callback.onFailure(it) }
                }
            }
        }
        return job as Job
    }

    override fun getPageData(callback: DataPagingCallback<T>): Job {
        var processDialog = showRequestIndicator()
        this.param.dataFormat = RequestParam.DataFormat.PAGELIST
        if (mainScope == null)
            mainScope = MainScope()
        job = mainScope!!.launch {
            try {
                val resultData = OkHttpTask.getInstance(context).dataTask<T>(param)
                GlobalScope.launch(Dispatchers.Main) {
                    processDialog?.hideProgress()
                    callback.onSuccess(resultData.getResult()!! as List<T>, resultData.getTotal())
                }
            }catch (e: Exception) {
                val message = ExceptionUtil.analysisException(e)
                GlobalScope.launch(Dispatchers.Main) {
                    processDialog?.hideProgress()
                    message?.let { callback.onFailure(it) }
                }
            }
        }
        return job as Job
    }


    override fun download(callback: DataObjectCallback<String>): Job {
        var processDialog = showProcessIndicator()
        this.param.dataFormat = RequestParam.DataFormat.OBJECT
        if (mainScope == null)
            mainScope = MainScope()
        job = mainScope!!.launch {
            try {
                val resultData = OkHttpTask.getInstance(context).downloadTask(param, object : DataDownloadCallback{
                    override fun onProgress(fileName: String, progress: Int) {
                        Log.i("ddddddddddddd", ""+progress)
                        processDialog?.updateProgress(fileName, progress)
                    }
                })
                GlobalScope.launch(Dispatchers.Main) {
                    processDialog?.hideProgress()
                    callback.onSuccess(resultData)
                }
            }catch (e: Exception) {
                val message = ExceptionUtil.analysisException(e)
                GlobalScope.launch(Dispatchers.Main) {
                    processDialog?.hideProgress()
                    message?.let { callback.onFailure(it) }
                }
            }
        }
        return job as Job
    }

    override fun upload(callback: DataObjectCallback<T>): Job {
        var processDialog = showProcessIndicator()
        this.param.dataFormat = RequestParam.DataFormat.OBJECT
        if (mainScope == null)
            mainScope = MainScope()
        job = mainScope!!.launch {
            try {
                val resultData = OkHttpTask.getInstance(context).uploadTask<T>(param, object : DataDownloadCallback{
                    override fun onProgress(fileName: String, progress: Int) {
                        Log.i("ddddddddddddd", ""+progress)
                        processDialog?.updateProgress(fileName, progress)
                    }
                })
                GlobalScope.launch(Dispatchers.Main) {
                    processDialog?.hideProgress()
                    callback.onSuccess(resultData.getResult()!! as T)
                }
            }catch (e: Exception) {
                val message = ExceptionUtil.analysisException(e)
                GlobalScope.launch(Dispatchers.Main) {
                    processDialog?.hideProgress()
                    message?.let { callback.onFailure(it) }
                }
            }
        }
        return job as Job
    }

    override fun onCancel(p0: DialogInterface?) {
        job?.cancel()
    }


    private fun showRequestIndicator(): IndicatorDialog? {
        var processDialog = param.loadingMessage?.let {
            IndicatorDialog(context, param.loadingMessage!!, ProgressDialog.STYLE_SPINNER, this)
        }
        processDialog?.showProgress()
        return processDialog
    }

    private fun showProcessIndicator(): IndicatorDialog? {
        var processDialog = param.loadingMessage?.let {
            IndicatorDialog(context, param.loadingMessage!!, ProgressDialog.STYLE_HORIZONTAL, this)
        }
        processDialog?.showProgress()
        return processDialog
    }
}