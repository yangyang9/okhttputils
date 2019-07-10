package com.yangyang.okhttputils.cookie.store

import java.util.ArrayList
import java.util.HashMap

import okhttp3.Cookie
import okhttp3.HttpUrl

/**
 * Created by zhy on 16/3/10.
 */
class MemoryCookieStore : CookieStore {

    override fun getCookies(): MutableList<Cookie> {
        val cookies = ArrayList<Cookie>()
        val httpUrls = allCookies.keys
        for (url in httpUrls) {
            cookies.addAll(allCookies[url]!!)
        }
        return cookies
    }

    private val allCookies = HashMap<String, MutableList<Cookie>>()

    override fun add(uri: HttpUrl, cookie: MutableList<Cookie>) {
        val oldCookies = allCookies[uri.host()]

        if (oldCookies != null) {
            val itNew = cookie.iterator()
            val itOld = oldCookies.iterator()
            while (itNew.hasNext()) {
                val va = itNew.next().name()
                while (va != null && itOld.hasNext()) {
                    val v = itOld.next().name()
                    if (v != null && va == v) {
                        itOld.remove()
                    }
                }
            }
            oldCookies.addAll(cookie)
        } else {
            allCookies[uri.host()] = cookie
        }


    }

    override fun get(uri: HttpUrl): MutableList<Cookie> {
        var cookies = allCookies[uri.host()]
        if (cookies == null) {
            cookies = ArrayList()
            allCookies[uri.host()] = cookies
        }
        return cookies

    }

    override fun removeAll(): Boolean {
        allCookies.clear()
        return true
    }


    override fun remove(uri: HttpUrl, cookie: Cookie): Boolean {
        val cookies = allCookies[uri.host()]
        return cookies?.remove(cookie) ?: false
    }


}
