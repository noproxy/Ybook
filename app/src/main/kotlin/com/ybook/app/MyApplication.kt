package com.ybook.app

import android.app.Application
import com.ybook.app.util.BooksManager
import com.umeng.analytics.MobclickAgent

/**
 * Created by carlos on 11/12/14.
 */
public class MyApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        //        MobclickAgent.setDebugMode(true);
    }


    override fun onTrimMemory(level: Int) {
        super<Application>.onTrimMemory(level)
        BooksManager.onTrimMemory(level)
    }
}
