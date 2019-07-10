package com.yangyang.okhttputils.request

import okhttp3.Request
import okhttp3.RequestBody

/**
 * Created by zhy on 15/12/14.
 */
class GetRequest(url: String?, tag: Any?, params: Map<String, String>?, headers: Map<String, String>?, id: Int) :
    OkHttpRequest(url, tag, params, headers, id) {

    override fun buildRequestBody(): RequestBody? {
        return null
    }

    override fun buildRequest(requestBody: RequestBody?): Request {
        return builder.get().build()
    }


}
