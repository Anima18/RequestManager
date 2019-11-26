package com.anima.networkrequest

import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.cancel

/**
 * Created by jianjianhong on 19-11-11
 */
abstract class ScopedActivity: AppCompatActivity(), CoroutineScope by MainScope(){

    override fun onDestroy() {
        super.onDestroy()
        Log.i("ScopedActivity", "onDestroy")
        cancel()
    }
}