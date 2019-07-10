package com.yangyang.okhttputils.callback

/**
 * Created by JimGong on 2016/6/23.
 */
interface IGenericsSerializator {
    fun <T> transform(response: String, classOfT: Class<T>): T
}
