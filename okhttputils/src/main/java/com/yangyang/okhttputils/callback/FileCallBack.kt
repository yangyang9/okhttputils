package com.yangyang.okhttputils.callback


import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream

import com.yangyang.okhttputils.OkHttpUtils
import okhttp3.Response

/**
 * Created by zhy on 15/12/15.
 */
abstract class FileCallBack(
    /**
     * 目标文件存储的文件夹路径
     */
    private val destFileDir: String,
    /**
     * 目标文件存储的文件名
     */
    private val destFileName: String
) : Callback<File>() {


    @Throws(Exception::class)
    override fun parseNetworkResponse(response: Response, id: Int): File {
        return saveFile(response, id)
    }


    @Throws(IOException::class)
    fun saveFile(response: Response, id: Int): File {
        var inputStream: InputStream? = null
        val buf = ByteArray(2048)
        var len: Int
        var fos: FileOutputStream? = null
        try {
            inputStream = response.body()?.byteStream()
            val total = response.body()?.contentLength() ?: 0

            var sum: Long = 0

            val dir = File(destFileDir)
            if (!dir.exists()) {
                dir.mkdirs()
            }
            val file = File(dir, destFileName)
            fos = FileOutputStream(file)
            while ((inputStream?.read(buf) ?: -1).also { len = it } != -1) {
                sum += len.toLong()
                fos.write(buf, 0, len)
                val finalSum = sum
                OkHttpUtils.instance?.delivery?.execute { inProgress(finalSum * 1.0f / total, total, id) }
            }
            fos.flush()

            return file

        } finally {
            try {
                response.body()?.close()
                inputStream?.close()
            } catch (e: IOException) {
            }

            try {
                fos?.close()
            } catch (e: IOException) {
            }

        }
    }


}

