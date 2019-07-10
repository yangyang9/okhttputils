package com.yangyang.okhttputils.request

import android.text.TextUtils

import com.yangyang.okhttputils.OkHttpUtils

import com.yangyang.okhttputils.utils.Exceptions
import okhttp3.MediaType
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.internal.http.HttpMethod

/**
 * Created by zhy on 16/2/23.
 */
class OtherRequest(
    private var requestBody: RequestBody?,
    private val content: String?,
    private val method: String,
    url: String?,
    tag: Any?,
    params: Map<String, String>?,
    headers: Map<String, String>?,
    id: Int
) : OkHttpRequest(url, tag, params, headers, id) {

    override fun buildRequestBody(): RequestBody? {
        if (requestBody == null && TextUtils.isEmpty(content) && HttpMethod.requiresRequestBody(method)) {
            Exceptions.illegalArgument("requestBody and content can not be null in method:$method")
        }

        if (requestBody == null && !TextUtils.isEmpty(content)) {
            requestBody = RequestBody.create(MEDIA_TYPE_PLAIN, content)
        }

        return requestBody
    }

    override fun buildRequest(requestBody: RequestBody?): Request? {
        if (method == OkHttpUtils.METHOD.PUT) {
            builder.put(requestBody)
        } else if (method == OkHttpUtils.METHOD.DELETE) {
            if (requestBody == null)
                builder.delete()
            else
                builder.delete(requestBody)
        } else if (method == OkHttpUtils.METHOD.HEAD) {
            builder.head()
        } else if (method == OkHttpUtils.METHOD.PATCH) {
            builder.patch(requestBody)
        }

        return builder.build()
    }

    companion object {
        private val MEDIA_TYPE_PLAIN = MediaType.parse("text/plain;charset=utf-8")
    }

}
