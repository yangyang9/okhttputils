package com.yangyang.okhttputils.builder

/**
 */
interface HasParamsable {
    fun params(params: MutableMap<String, String>): OkHttpRequestBuilder<*>
    fun addParams(key: String, value : String): OkHttpRequestBuilder<*>
}
