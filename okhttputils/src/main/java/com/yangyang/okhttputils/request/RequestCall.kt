package com.yangyang.okhttputils.request

import com.yangyang.okhttputils.OkHttpUtils

import java.io.IOException
import java.util.concurrent.TimeUnit

import com.yangyang.okhttputils.callback.Callback
import okhttp3.Call
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response

/**
 * Created by zhy on 15/12/15.
 * 对OkHttpRequest的封装，对外提供更多的接口：cancel(),readTimeOut()...
 */
class RequestCall(val okHttpRequest: OkHttpRequest) {
    var request: Request? = null
        private set
    var call: Call? = null
        private set

    private var readTimeOut: Long = 0
    private var writeTimeOut: Long = 0
    private var connTimeOut: Long = 0

    private var clone: OkHttpClient? = null

    fun readTimeOut(readTimeOut: Long): RequestCall {
        this.readTimeOut = readTimeOut
        return this
    }

    fun writeTimeOut(writeTimeOut: Long): RequestCall {
        this.writeTimeOut = writeTimeOut
        return this
    }

    fun connTimeOut(connTimeOut: Long): RequestCall {
        this.connTimeOut = connTimeOut
        return this
    }

    private fun buildCall(callback: Callback<Any>?): Call? {
        request = generateRequest(callback) ?: return null

        if (readTimeOut > 0 || writeTimeOut > 0 || connTimeOut > 0) {
            readTimeOut = if (readTimeOut > 0) readTimeOut else OkHttpUtils.DEFAULT_MILLISECONDS
            writeTimeOut = if (writeTimeOut > 0) writeTimeOut else OkHttpUtils.DEFAULT_MILLISECONDS
            connTimeOut = if (connTimeOut > 0) connTimeOut else OkHttpUtils.DEFAULT_MILLISECONDS

            clone = OkHttpUtils.instance?.okHttpClient?.newBuilder()
                ?.readTimeout(readTimeOut, TimeUnit.MILLISECONDS)
                ?.writeTimeout(writeTimeOut, TimeUnit.MILLISECONDS)
                ?.connectTimeout(connTimeOut, TimeUnit.MILLISECONDS)
                ?.build()

            call = clone?.newCall(request)
        } else {
            call = OkHttpUtils.instance?.okHttpClient?.newCall(request)
        }
        return call
    }

    private fun generateRequest(callback: Callback<Any>?): Request? {
        return okHttpRequest.generateRequest(callback)
    }

    fun execute(callback: Callback<Any>?) {
        buildCall(callback)

        callback?.onBefore(request, okHttpRequest.id)

        OkHttpUtils.instance?.execute(this, callback)
    }

    @Throws(IOException::class)
    fun execute(): Response? {
        buildCall(null)
        return call?.execute()
    }

    fun cancel() {
        call?.cancel()
    }


}
