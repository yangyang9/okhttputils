package com.yangyang.okhttputils.callback

import android.graphics.Bitmap
import android.graphics.BitmapFactory

import okhttp3.Response

/**
 * Created by zhy on 15/12/14.
 */
abstract class BitmapCallback : Callback<Bitmap>() {
    @Throws(Exception::class)
    override fun parseNetworkResponse(response: Response, id: Int): Bitmap {
        return BitmapFactory.decodeStream(response.body()!!.byteStream())
    }

}
