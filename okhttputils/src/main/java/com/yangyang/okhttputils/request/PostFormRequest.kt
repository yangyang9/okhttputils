package com.yangyang.okhttputils.request

import com.yangyang.okhttputils.OkHttpUtils

import java.io.UnsupportedEncodingException
import java.net.FileNameMap
import java.net.URLConnection
import java.net.URLEncoder

import com.yangyang.okhttputils.builder.PostFormBuilder
import com.yangyang.okhttputils.callback.Callback
import okhttp3.FormBody
import okhttp3.Headers
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.Request
import okhttp3.RequestBody

/**
 * Created by zhy on 15/12/14.
 */
class PostFormRequest(
    url: String,
    tag: Any,
    params: Map<String, String>?,
    headers: Map<String, String>?,
    private val files: List<PostFormBuilder.FileInput>?,
    id: Int
) : OkHttpRequest(url, tag, params, headers, id) {

    override fun buildRequestBody(): RequestBody? {
        if (files == null || files.isEmpty()) {
            val builder = FormBody.Builder()
            addParams(builder)
            return builder.build()
        } else {
            val builder = MultipartBody.Builder()
                .setType(MultipartBody.FORM)
            addParams(builder)

            for (i in files.indices) {
                val fileInput = files[i]
                val fileBody = RequestBody.create(MediaType.parse(guessMimeType(fileInput.filename)), fileInput.file)
                builder.addFormDataPart(fileInput.key, fileInput.filename, fileBody)
            }
            return builder.build()
        }
    }

    override fun wrapRequestBody(requestBody: RequestBody?, callback: Callback<Any>?): RequestBody? {
        return if (callback == null) requestBody else CountingRequestBody(
            requestBody,
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
    override fun buildRequest(requestBody: RequestBody?): Request? {
        if (requestBody == null) return null
        return builder.post(requestBody).build()
    }

    private fun guessMimeType(path: String): String {
        val fileNameMap = URLConnection.getFileNameMap()
        var contentTypeFor: String? = null
        try {
            contentTypeFor = fileNameMap.getContentTypeFor(URLEncoder.encode(path, "UTF-8"))
        } catch (e: UnsupportedEncodingException) {
            e.printStackTrace()
        }

        if (contentTypeFor == null) {
            contentTypeFor = "application/octet-stream"
        }
        return contentTypeFor
    }

    private fun addParams(builder: MultipartBody.Builder) {
        if (params?.isNotEmpty() == true) {
            for (key in params!!.keys) {
                builder.addPart(
                    Headers.of("Content-Disposition", "form-data; name=\"$key\""),
                    RequestBody.create(null, params!![key] ?: "")
                )
            }
        }
    }

    private fun addParams(builder: FormBody.Builder) {
        val keys = params?.keys ?: return
        for (key in keys) {
            builder.add(key, params!![key] ?: "")
        }
    }

}
