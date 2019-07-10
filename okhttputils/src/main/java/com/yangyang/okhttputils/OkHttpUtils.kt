package com.yangyang.okhttputils


import java.io.IOException
import java.util.concurrent.Executor

import com.yangyang.okhttputils.builder.*
import com.yangyang.okhttputils.callback.Callback
import com.yangyang.okhttputils.request.RequestCall
import com.yangyang.okhttputils.utils.Platform
import okhttp3.Call
import okhttp3.OkHttpClient
import okhttp3.Response

/**
 * Created by zhy on 15/8/17.
 */
class OkHttpUtils(okHttpClient: OkHttpClient?) {
    var okHttpClient: OkHttpClient? = null
        private set
    private val mPlatform: Platform


    val delivery: Executor
        get() = mPlatform.defaultCallbackExecutor()

    init {
        if (okHttpClient == null) {
            this.okHttpClient = OkHttpClient()
        } else {
            this.okHttpClient = okHttpClient
        }

        mPlatform = Platform.get()
    }

    fun execute(requestCall: RequestCall, httpCallback: Callback<Any>?) {
        var callback = httpCallback
        if (callback == null)
            callback = Callback.CALLBACK_DEFAULT
        val finalCallback = callback
        val id = requestCall.okHttpRequest.id

        requestCall.call?.enqueue(object : okhttp3.Callback {
            override fun onFailure(call: Call, e: IOException) {
                sendFailResultCallback(call, e, finalCallback, id)
            }

            override fun onResponse(call: Call, response: Response) {
                try {
                    if (call.isCanceled) {
                        sendFailResultCallback(call, IOException("Canceled!"), finalCallback, id)
                        return
                    }

                    if (!finalCallback.validateResponse(response, id)) {
                        sendFailResultCallback(
                            call,
                            IOException("request failed , reponse's code is : " + response.code()),
                            finalCallback,
                            id
                        )
                        return
                    }

                    val o = finalCallback.parseNetworkResponse(response, id)
                    sendSuccessResultCallback(o, finalCallback, id)
                } catch (e: Exception) {
                    sendFailResultCallback(call, e, finalCallback, id)
                } finally {
                    response.body()?.close()
                }

            }
        })
    }


    fun sendFailResultCallback(call: Call, e: Exception, callback: Callback<Any>?, id: Int) {
        if (callback == null) return
        mPlatform.execute(Runnable {
            callback.onError(call, e, id)
            callback.onAfter(id)
        })
    }

    fun sendSuccessResultCallback(data: Any?, callback: Callback<Any>?, id: Int) {
        if (callback == null) return
        mPlatform.execute(Runnable {
            callback.onResponse(data, id)
            callback.onAfter(id)
        })
    }

    fun cancelTag(tag: Any) {
        if (okHttpClient?.dispatcher()?.queuedCalls() == null) return
        for (call in okHttpClient?.dispatcher()?.queuedCalls()!!) {
            if (tag == call.request().tag()) {
                call.cancel()
            }
        }
        for (call in okHttpClient?.dispatcher()?.runningCalls()!!) {
            if (tag == call.request().tag()) {
                call.cancel()
            }
        }
    }

    object METHOD {
        val HEAD = "HEAD"
        val DELETE = "DELETE"
        val PUT = "PUT"
        val PATCH = "PATCH"
    }

    companion object {
        const val DEFAULT_MILLISECONDS = 10_000L
        @Volatile
        private var mInstance: OkHttpUtils? = null


        private fun initClient(okHttpClient: OkHttpClient?): OkHttpUtils? {
            if (mInstance == null) {
                synchronized(OkHttpUtils::class.java) {
                    if (mInstance == null) {
                        mInstance = OkHttpUtils(okHttpClient)
                    }
                }
            }
            return mInstance
        }

        val instance: OkHttpUtils?
            get() = initClient(null)

        fun get(): GetBuilder {
            return GetBuilder()
        }

        fun postString(): PostStringBuilder {
            return PostStringBuilder()
        }

        fun postFile(): PostFileBuilder {
            return PostFileBuilder()
        }

        fun post(): PostFormBuilder {
            return PostFormBuilder()
        }

        fun put(): OtherRequestBuilder {
            return OtherRequestBuilder(METHOD.PUT)
        }

        fun head(): HeadBuilder {
            return HeadBuilder()
        }

        fun delete(): OtherRequestBuilder {
            return OtherRequestBuilder(METHOD.DELETE)
        }

        fun patch(): OtherRequestBuilder {
            return OtherRequestBuilder(METHOD.PATCH)
        }
    }
}

