package com.yangyang.okhttputils.request


import com.yangyang.okhttputils.utils.Exceptions
import okhttp3.MediaType
import okhttp3.Request
import okhttp3.RequestBody

/**
 * Created by zhy on 15/12/14.
 */
class PostStringRequest(
    url: String,
    tag: Any,
    params: Map<String, String>?,
    headers: Map<String, String>?,
    private val content: String?,
    mediaType: MediaType?,
    id: Int
) : OkHttpRequest(url, tag, params, headers, id) {
    private var mediaType: MediaType? = null


    init {
        this.mediaType = mediaType

        if (this.content == null) {
            Exceptions.illegalArgument("the content can not be null !")
        }
        if (this.mediaType == null) {
            this.mediaType = MEDIA_TYPE_PLAIN
        }

    }

    override fun buildRequestBody(): RequestBody? {
        return RequestBody.create(mediaType, content?:"")
    }

    override fun buildRequest(requestBody: RequestBody?): Request?{
        return builder.post(requestBody).build()
    }

    companion object {
        private val MEDIA_TYPE_PLAIN = MediaType.parse("text/plain;charset=utf-8")
    }


}
