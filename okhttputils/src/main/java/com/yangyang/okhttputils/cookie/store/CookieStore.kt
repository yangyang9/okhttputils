package com.yangyang.okhttputils.cookie.store

import okhttp3.Cookie
import okhttp3.HttpUrl

interface CookieStore {

    fun getCookies(): MutableList<Cookie>

    fun add(uri: HttpUrl, cookie: MutableList<Cookie>)

    operator fun get(uri: HttpUrl): MutableList<Cookie>

    fun remove(uri: HttpUrl, cookie: Cookie): Boolean

    fun removeAll(): Boolean

}
