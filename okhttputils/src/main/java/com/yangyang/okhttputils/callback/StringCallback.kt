package com.yangyang.okhttputils.callback

import java.io.IOException

import okhttp3.Response

/**
 * Created by zhy on 15/12/14.
 */
abstract class StringCallback : Callback<String>() {
    @Throws(IOException::class)
    override fun parseNetworkResponse(response: Response, id: Int): String {
        return response.body()?.string() ?: ""
    }
}
