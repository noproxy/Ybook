package com.ybook.app

import android.app.Application

/**
 * Created by carlos on 11/12/14.
 */
public class MyApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        mInstance = this
    }

    class object {
        public var mInstance: MyApplication? = null

        public fun getApplication(): MyApplication {
            return mInstance!!
        }
    }
}
