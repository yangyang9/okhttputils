package com.yangyang.okhttputils.builder


import com.yangyang.okhttputils.request.PostFormRequest
import com.yangyang.okhttputils.request.RequestCall

import java.io.File
import java.util.ArrayList
import java.util.LinkedHashMap

/**
 * Created by zhy on 15/12/14.
 */
class PostFormBuilder : OkHttpRequestBuilder<PostFormBuilder>(), HasParamsable {
    private val files = ArrayList<FileInput>()

    override fun build(): RequestCall {
        return PostFormRequest(url?:"", tag?:"", params, headers, files, id).build()
    }

    fun files(key: String, files: MutableMap<String, File>): PostFormBuilder {
        for (filename in files.keys) {
            this.files.add(FileInput(key, filename, files[filename]!!))
        }
        return this
    }

    fun addFile(name: String, filename: String, file: File): PostFormBuilder {
        files.add(FileInput(name, filename, file))
        return this
    }

    class FileInput(var key: String, var filename: String, var file: File) {

        override fun toString(): String {
            return "FileInput{" +
                    "key='" + key + '\''.toString() +
                    ", filename='" + filename + '\''.toString() +
                    ", file=" + file +
                    '}'.toString()
        }
    }


    override fun params(params: MutableMap<String, String>): PostFormBuilder {
        this.params = params
        return this
    }

    override fun addParams(key: String, value: String): PostFormBuilder {
        if (this.params == null) {
            params = LinkedHashMap()
        }
        params?.put(key, value)
        return this
    }


}
