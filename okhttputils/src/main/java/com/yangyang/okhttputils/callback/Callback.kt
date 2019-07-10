package com.yangyang.okhttputils.callback

import okhttp3.Call
import okhttp3.Request
import okhttp3.Response

abstract class Callback<T> {
    /**
     * UI Thread
     *
     * @param request
     */
    fun onBefore(request: Request?, id: Int) {}

    /**
     * UI Thread
     *
     * @param
     */
    fun onAfter(id: Int) {}

    /**
     * UI Thread
     *
     * @param progress
     */
    fun inProgress(progress: Float, total: Long, id: Int) {

    }

    /**
     * if you parse reponse code in parseNetworkResponse, you should make this method return true.
     *
     * @param response
     * @return
     */
    fun validateResponse(response: Response, id: Int): Boolean {
        return response.isSuccessful
    }

    /**
     * Thread Pool Thread
     *
     * @param response
     */
    @Throws(Exception::class)
    abstract fun parseNetworkResponse(response: Response, id: Int): T?

    abstract fun onError(call: Call, e: Exception, id: Int)

    abstract fun onResponse(response: T?, id: Int)

    companion object {

        var CALLBACK_DEFAULT: Callback<Any> = object : Callback<Any>() {
            override fun onResponse(response: Any?, id: Int) {
            }

            @Throws(Exception::class)
            override fun parseNetworkResponse(response: Response, id: Int): Any {
                return ""
            }

            override fun onError(call: Call, e: Exception, id: Int) {

            }
        }
    }

}