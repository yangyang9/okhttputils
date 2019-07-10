package com.yangyang.okhttputils.builder


import com.yangyang.okhttputils.request.OtherRequest
import com.yangyang.okhttputils.request.RequestCall
import okhttp3.RequestBody

/**
 * DELETE、PUT、PATCH等其他方法
 */
class OtherRequestBuilder(private val method: String) : OkHttpRequestBuilder<OtherRequestBuilder>() {
    private var requestBody: RequestBody? = null
    private var content: String? = null

    override fun build(): RequestCall {
        return OtherRequest(requestBody, content, method, url, tag, params, headers, id).build()
    }

    fun requestBody(requestBody: RequestBody): OtherRequestBuilder {
        this.requestBody = requestBody
        return this
    }

    fun requestBody(content: String): OtherRequestBuilder {
        this.content = content
        return this
    }


}
