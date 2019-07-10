package com.yangyang.okhttputils.builder


import com.yangyang.okhttputils.request.PostStringRequest
import com.yangyang.okhttputils.request.RequestCall
import okhttp3.MediaType

/**
 * Created by zhy on 15/12/14.
 */
class PostStringBuilder : OkHttpRequestBuilder<PostStringBuilder>() {
    private var content: String? = null
    private var mediaType: MediaType? = null


    fun content(content: String): PostStringBuilder {
        this.content = content
        return this
    }

    fun mediaType(mediaType: MediaType): PostStringBuilder {
        this.mediaType = mediaType
        return this
    }

    override fun build(): RequestCall {
        return PostStringRequest(url?:"", tag?:"", params, headers, content, mediaType, id).build()
    }


}
