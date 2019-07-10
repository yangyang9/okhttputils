package com.yangyang.okhttputils.request


import com.yangyang.okhttputils.callback.Callback
import com.yangyang.okhttputils.utils.Exceptions
import okhttp3.Headers
import okhttp3.Request
import okhttp3.RequestBody

/**
 * Created by zhy on 15/11/6.
 */
abstract class OkHttpRequest protected constructor(
    protected var url: String?, protected var tag: Any?,
    protected var params: Map<String, String>?, protected var headers: Map<String, String>?, id: Int
) {
    var id: Int = 0
        protected set

    protected var builder = Request.Builder()

    init {
        this.id = id

        if (url == null) {
            Exceptions.illegalArgument("url can not be null.")
        }

        initBuilder()
    }


    /**
     * 初始化一些基本参数 url , tag , headers
     */
    private fun initBuilder() {
        builder.url(url ?: "").tag(tag)
        appendHeaders()
    }

    protected abstract fun buildRequestBody(): RequestBody?

    protected open fun wrapRequestBody(requestBody: RequestBody?, callback: Callback<Any>?): RequestBody? {
        return requestBody
    }

    protected abstract fun buildRequest(requestBody: RequestBody?): Request?

    fun build(): RequestCall {
        return RequestCall(this)
    }


    fun generateRequest(callback: Callback<Any>?): Request? {
        val requestBody = buildRequestBody()
        val wrappedRequestBody = wrapRequestBody(requestBody, callback)
        return buildRequest(wrappedRequestBody)
    }


    private fun appendHeaders() {
        val headerBuilder = Headers.Builder()
        if (headers == null || headers?.isEmpty() == true) return

        for (key in headers!!.keys) {
            headerBuilder.add(key, headers!![key] ?: "")
        }
        builder.headers(headerBuilder.build())
    }

}
