package com.yangyang.okhttputils.utils

/**
 * Created by zhy on 15/12/14.
 */
object Exceptions {
    fun illegalArgument(msg: String, vararg params: Any) {
        throw IllegalArgumentException(String.format(msg, *params))
    }

}
