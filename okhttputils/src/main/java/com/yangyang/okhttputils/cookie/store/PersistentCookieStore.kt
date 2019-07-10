package com.yangyang.okhttputils.cookie.store

import android.content.Context
import android.content.SharedPreferences
import android.text.TextUtils
import android.util.Log

import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.io.ObjectInputStream
import java.io.ObjectOutputStream
import java.util.ArrayList
import java.util.HashMap
import java.util.Locale
import java.util.concurrent.ConcurrentHashMap

import okhttp3.Cookie
import okhttp3.HttpUrl
import kotlin.experimental.and

/**
 * <pre>
 * OkHttpClient client = new OkHttpClient.Builder()
 * .cookieJar(new JavaNetCookieJar(new CookieManager(
 * new PersistentCookieStore(getApplicationContext()),
 * CookiePolicy.ACCEPT_ALL))
 * .build();
 *
</pre> *
 *
 *
 * from http://stackoverflow.com/questions/25461792/persistent-cookie-store-using-okhttp-2-on-android
 *
 *
 * <br></br>
 * A persistent cookie store which implements the Apache HttpClient CookieStore interface.
 * Cookies are stored and will persist on the user's device between application sessions since they
 * are serialized and stored in SharedPreferences. Instances of this class are
 * designed to be used with AsyncHttpClient#setCookieStore, but can also be used with a
 * regular old apache HttpClient/HttpContext if you prefer.
 */
class PersistentCookieStore
/**
 * Construct a persistent cookie store.
 *
 * @param context Context to attach cookie store to
 */
    (context: Context) : CookieStore {

    private val cookies: HashMap<String, ConcurrentHashMap<String, Cookie>>
    private val cookiePrefs: SharedPreferences

    init {
        cookiePrefs = context.getSharedPreferences(COOKIE_PREFS, 0)
        cookies = HashMap()

        // Load any previously stored cookies into the store
        val prefsMap = cookiePrefs.all
        for ((key, value) in prefsMap) {
            if (value != null && value is String && !value.startsWith(COOKIE_NAME_PREFIX)) {
                val cookieNames = TextUtils.split(value, ",")
                for (name in cookieNames) {
                    val encodedCookie = cookiePrefs.getString(COOKIE_NAME_PREFIX + name, null)
                    if (encodedCookie != null) {
                        val decodedCookie = decodeCookie(encodedCookie)
                        if (decodedCookie != null) {
                            if (!cookies.containsKey(key))
                                cookies[key] = ConcurrentHashMap()
                            cookies[key]!![name] = decodedCookie
                        }
                    }
                }

            }
        }
    }

    protected fun add(uri: HttpUrl, cookie: Cookie) {
        val name = getCookieToken(cookie)

        if (cookie.persistent()) {
            if (!cookies.containsKey(uri.host())) {
                cookies[uri.host()] = ConcurrentHashMap()
            }
            cookies[uri.host()]!![name] = cookie
        } else {
            if (cookies.containsKey(uri.host())) {
                cookies[uri.host()]!!.remove(name)
            } else {
                return
            }
        }

        // Save cookie into persistent store
        val prefsWriter = cookiePrefs.edit()
        prefsWriter.putString(uri.host(), TextUtils.join(",", cookies[uri.host()]!!.keys))
        prefsWriter.putString(COOKIE_NAME_PREFIX + name, encodeCookie(SerializableHttpCookie(cookie)))
        prefsWriter.apply()
    }

    protected fun getCookieToken(cookie: Cookie): String {
        return cookie.name() + cookie.domain()
    }

    override fun add(uri: HttpUrl, cookies: MutableList<Cookie>) {
        for (cookie in cookies) {
            add(uri, cookie)
        }
    }

    override fun get(uri: HttpUrl): MutableList<Cookie> {
        val ret = ArrayList<Cookie>()
        if (cookies.containsKey(uri.host())) {
            val cookies = this.cookies[uri.host()]!!.values
            for (cookie in cookies) {
                if (isCookieExpired(cookie)) {
                    remove(uri, cookie)
                } else {
                    ret.add(cookie)
                }
            }
        }

        return ret
    }

    override fun removeAll(): Boolean {
        val prefsWriter = cookiePrefs.edit()
        prefsWriter.clear()
        prefsWriter.apply()
        cookies.clear()
        return true
    }


    override fun remove(uri: HttpUrl, cookie: Cookie): Boolean {
        val name = getCookieToken(cookie)

        if (cookies.containsKey(uri.host()) && cookies[uri.host()]!!.containsKey(name)) {
            cookies[uri.host()]!!.remove(name)

            val prefsWriter = cookiePrefs.edit()
            if (cookiePrefs.contains(COOKIE_NAME_PREFIX + name)) {
                prefsWriter.remove(COOKIE_NAME_PREFIX + name)
            }
            prefsWriter.putString(uri.host(), TextUtils.join(",", cookies[uri.host()]!!.keys))
            prefsWriter.apply()

            return true
        } else {
            return false
        }
    }

    override fun getCookies(): MutableList<Cookie> {
        val ret = ArrayList<Cookie>()
        for (key in cookies.keys)
            ret.addAll(cookies[key]!!.values)

        return ret
    }


    protected fun encodeCookie(cookie: SerializableHttpCookie?): String? {
        if (cookie == null)
            return null
        val os = ByteArrayOutputStream()
        try {
            val outputStream = ObjectOutputStream(os)
            outputStream.writeObject(cookie)
        } catch (e: IOException) {
            Log.d(LOG_TAG, "IOException in encodeCookie", e)
            return null
        }

        return byteArrayToHexString(os.toByteArray())
    }

    protected fun decodeCookie(cookieString: String): Cookie? {
        val bytes = hexStringToByteArray(cookieString)
        val byteArrayInputStream = ByteArrayInputStream(bytes)
        var cookie: Cookie? = null
        try {
            val objectInputStream = ObjectInputStream(byteArrayInputStream)
            cookie = (objectInputStream.readObject() as SerializableHttpCookie).cookie
        } catch (e: IOException) {
            Log.d(LOG_TAG, "IOException in decodeCookie", e)
        } catch (e: ClassNotFoundException) {
            Log.d(LOG_TAG, "ClassNotFoundException in decodeCookie", e)
        }

        return cookie
    }

    /**
     * Using some super basic byte array &lt;-&gt; hex conversions so we don't have to rely on any
     * large Base64 libraries. Can be overridden if you like!
     *
     * @param bytes byte array to be converted
     * @return string containing hex values
     */
    protected fun byteArrayToHexString(bytes: ByteArray): String {
        val sb = StringBuilder(bytes.size * 2)
        for (element in bytes) {
            val v = element and 0xff.toByte()
            if (v < 16) {
                sb.append('0')
            }
            sb.append(Integer.toHexString(v.toInt()))
        }
        return sb.toString().toUpperCase(Locale.US)
    }

    /**
     * Converts hex values from strings to byte arra
     *
     * @param hexString string of hex-encoded values
     * @return decoded byte array
     */
    protected fun hexStringToByteArray(hexString: String): ByteArray {
        val len = hexString.length
        val data = ByteArray(len / 2)
        var i = 0
        while (i < len) {
            data[i / 2] = ((Character.digit(hexString[i], 16) shl 4) + Character.digit(hexString[i + 1], 16)).toByte()
            i += 2
        }
        return data
    }

    companion object {

        private val LOG_TAG = "PersistentCookieStore"
        private val COOKIE_PREFS = "CookiePrefsFile"
        private val COOKIE_NAME_PREFIX = "cookie_"

        private fun isCookieExpired(cookie: Cookie): Boolean {
            return cookie.expiresAt() < System.currentTimeMillis()
        }
    }
}
