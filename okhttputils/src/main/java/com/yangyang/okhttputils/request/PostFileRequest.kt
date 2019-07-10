package com.yangyang.okhttputils.request

import com.yangyang.okhttputils.OkHttpUtils

import java.io.File

import com.yangyang.okhttputils.callback.Callback
import com.yangyang.okhttputils.utils.Exceptions
import okhttp3.MediaType
import okhttp3.Request
import okhttp3.RequestBody

/**
 * Created by zhy on 15/12/14.
 */
class PostFileRequest(
    url: String,
    tag: Any,
    params: Map<String, String>?,
    headers: Map<String, String>?,
    private val file: File?,
    mediaType: MediaType?,
    id: Int
) : OkHttpRequest(url, tag, params, headers, id) {
    private var mediaType: MediaType? = null

    init {
        this.mediaType = mediaType

        if (this.file == null) {
            Exceptions.illegalArgument("the file can not be null !")
        }
        if (this.mediaType == null) {
            this.mediaType = MEDIA_TYPE_STREAM
        }
    }

    override fun buildRequestBody(): RequestBody? {
        return RequestBody.create(mediaType, file)
    }

    override fun wrapRequestBody(requestBody: RequestBody?, callback: Callback<Any>?): RequestBody? {
        return if (callback == null) requestBody else CountingRequestBody(
            requestBody!!,
            object : CountingRequestBody.Listener {
                override fun onRequestProgress(bytesWritten: Long, contentLength: Long) {

                    OkHttpUtils.instance?.delivery?.execute {
                        callback.inProgress(
                            bytesWritten * 1.0f / contentLength,
                            contentLength,
                            id
                        )
                    }

                }
            })
    }

    override fun buildRequest(requestBody: RequestBody?): Request {
        return builder.post(requestBody!!).build()
    }

    companion object {
        private val MEDIA_TYPE_STREAM = MediaType.parse("application/octet-stream")
    }


}
