package com.yangyang.okhttputils.cookie


import com.yangyang.okhttputils.cookie.store.CookieStore
import com.yangyang.okhttputils.utils.Exceptions
import okhttp3.Cookie
import okhttp3.CookieJar
import okhttp3.HttpUrl

/**
 * Created by zhy on 16/3/10.
 */
class CookieJarImpl(val cookieStore: CookieStore?) : CookieJar {

    init {
        if (cookieStore == null) Exceptions.illegalArgument("cookieStore can not be null.")
    }

    @Synchronized
    override fun saveFromResponse(url: HttpUrl, cookies: MutableList<Cookie>) {
        cookieStore?.add(url, cookies)
    }

    @Synchronized
    override fun loadForRequest(url: HttpUrl): MutableList<Cookie> {
        return cookieStore?.get(url) ?: mutableListOf()
    }
}
