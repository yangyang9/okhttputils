package com.yangyang.okhttputils.builder


import java.io.File

import com.yangyang.okhttputils.request.PostFileRequest
import com.yangyang.okhttputils.request.RequestCall
import okhttp3.MediaType

/**
 * Created by zhy on 15/12/14.
 */
class PostFileBuilder : OkHttpRequestBuilder<PostFileBuilder>() {
    private var file: File? = null
    private var mediaType: MediaType? = null


    fun file(file: File): OkHttpRequestBuilder<*> {
        this.file = file
        return this
    }

    fun mediaType(mediaType: MediaType): OkHttpRequestBuilder<*> {
        this.mediaType = mediaType
        return this
    }

    override fun build(): RequestCall {
        return PostFileRequest(url?:"", tag?:"", params, headers, file, mediaType, id).build()
    }


}
