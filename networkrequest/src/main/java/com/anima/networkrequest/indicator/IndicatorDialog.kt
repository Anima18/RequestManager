package com.anima.networkrequest.indicator

import android.app.Activity
import android.app.ProgressDialog
import android.content.Context
import android.os.Handler
import android.os.Message
import com.anima.networkrequest.NetworkRequest

/**
 * Created by jianjianhong on 19-11-7
 */
class IndicatorDialog(val context: Context, val message: String, val style: Int, val request: NetworkRequest<*>?): IRequestIndicator {
    private val TAG = "NetworkProgressDialog"

    var progressDialog: ProgressDialog? = null


    init {
        initProgressDialog(style, request)
    }

    private val handler = object : Handler() {

        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)
            //updateProgress(msg.obj.toString(), msg.what)
            progressDialog!!.setMessage(msg.obj.toString())
            progressDialog!!.progress = msg.what
        }
    }

    private fun initProgressDialog(style: Int, request: NetworkRequest<*>?) {
        if (isShowInActivity()) {
            progressDialog = ProgressDialog(context)
            progressDialog!!.setProgressStyle(style)
            //progressDialog.setIndeterminate(true);
            progressDialog!!.setCanceledOnTouchOutside(false)
            progressDialog!!.setOnCancelListener(request)
        }
    }

    override fun showProgress() {
        if (isShowInActivity()) {
            progressDialog!!.setMessage(message)
            progressDialog!!.show()
        }
    }

    override fun hideProgress() {
        if (isShowInActivity()) {
            progressDialog!!.cancel()
        }
    }

    override fun updateProgress(message: String, progress: Int) {
        if (isShowInActivity()) {
            val msg = Message()
            msg.what = progress
            msg.obj = message
            handler.sendMessage(msg)
        }
    }

    private fun isShowInActivity(): Boolean {
        return context is Activity
    }


}