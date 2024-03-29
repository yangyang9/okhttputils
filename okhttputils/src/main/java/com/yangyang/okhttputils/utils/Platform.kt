/*
 * Copyright (C) 2013 Square, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.yangyang.okhttputils.utils

import android.os.Build
import android.os.Handler
import android.os.Looper

import java.util.concurrent.Executor
import java.util.concurrent.Executors

open class Platform {

    open fun defaultCallbackExecutor(): Executor {
        return Executors.newCachedThreadPool()
    }

    fun execute(runnable: Runnable) {
        defaultCallbackExecutor().execute(runnable)
    }


    internal class Android : Platform() {
        override fun defaultCallbackExecutor(): Executor {
            return MainThreadExecutor()
        }

        internal class MainThreadExecutor : Executor {
            private val handler = Handler(Looper.getMainLooper())

            override fun execute(r: Runnable) {
                handler.post(r)
            }
        }
    }

    companion object {
        private val PLATFORM = findPlatform()

        fun get(): Platform {
            L.e(PLATFORM.javaClass.toString())
            return PLATFORM
        }

        private fun findPlatform(): Platform {
            try {
                Class.forName("android.os.Build")
                if (Build.VERSION.SDK_INT != 0) {
                    return Android()
                }
            } catch (ignored: ClassNotFoundException) {
            }

            return Platform()
        }
    }


}
