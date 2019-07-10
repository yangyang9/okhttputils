package com.yangyang.okhttputils.builder

import android.net.Uri
import com.yangyang.okhttputils.request.GetRequest
import com.yangyang.okhttputils.request.RequestCall
import java.util.LinkedHashMap

/**
 */
open class GetBuilder : OkHttpRequestBuilder<GetBuilder>(), HasParamsable {
    override fun build(): RequestCall {
        if (params != null) {
            url = appendParams(url, params)
        }

        return GetRequest(url, tag, params, headers, id).build()
    }

    protected fun appendParams(url: String?, params: Map<String, String>?): String? {
        if (url == null || params == null || params.isEmpty()) {
            return url
        }
        val builder = Uri.parse(url).buildUpon()
        val keys = params.keys
        val iterator = keys.iterator()
        while (iterator.hasNext()) {
            val key = iterator.next()
            builder.appendQueryParameter(key, params[key])
        }
        return builder.build().toString()
    }


    override fun params(params: MutableMap<String, String>): GetBuilder {
        this.params = params
        return this
    }

    override fun addParams(key: String, value: String): GetBuilder {
        if (this.params == null) {
            params = LinkedHashMap()
        }
        params!![key] = value
        return this
    }


}
