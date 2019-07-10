package com.yangyang.okhttputils.utils

import android.util.Log

/**
 * Created by zhy on 15/11/6.
 */
object L {
    private val debug = false

    fun e(msg: String) {
        if (debug) {
            Log.e("OkHttp", msg)
        }
    }

}

