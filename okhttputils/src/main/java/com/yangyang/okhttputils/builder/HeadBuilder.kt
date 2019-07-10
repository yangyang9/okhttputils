package com.yangyang.okhttputils.builder


import com.yangyang.okhttputils.OkHttpUtils
import com.yangyang.okhttputils.request.OtherRequest
import com.yangyang.okhttputils.request.RequestCall

/**
 * Created by zhy on 16/3/2.
 */
class HeadBuilder : GetBuilder() {
    override fun build(): RequestCall {
        return OtherRequest(null, "", OkHttpUtils.METHOD.HEAD, url, tag, params, headers, id).build()
    }
}
