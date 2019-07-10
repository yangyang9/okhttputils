package com.yangyang.okhttputils.callback

import java.io.IOException
import java.lang.reflect.ParameterizedType

import okhttp3.Response

/**
 * Created by JimGong on 2016/6/23.
 */

abstract class GenericsCallback<T>(private var mGenericsSerializator: IGenericsSerializator) : Callback<T>() {

    @Throws(IOException::class)
    override fun parseNetworkResponse(response: Response, id: Int): T {
        val string = response.body()!!.string()
        val entityClass = (javaClass.genericSuperclass as ParameterizedType).actualTypeArguments[0] as Class<T>
        return if (entityClass == String::class.java) {
            string as T
        } else mGenericsSerializator.transform(string, entityClass)
    }

}
