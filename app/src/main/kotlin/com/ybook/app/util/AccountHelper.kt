/*
    Copyright 2015 Carlos

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
 */

package com.ybook.app.util

import android.content.Context
import com.ybook.app.net.LoginRequest
import com.ybook.app.MyApplication

/**
 * Created by carlos on 11/12/14.
 */
object AccountHelper {
    val USER_NAME = "userName"
    val PASSWORD = "password"
    val LIB_CODE = "lib_code"
    val ACCOUNT_SHARE_PREFERENCES = "LoginInfo"

    public fun deleteAccount(con: Context) {
        val sh = con.getSharedPreferences(ACCOUNT_SHARE_PREFERENCES, Context.MODE_PRIVATE)
        sh.edit().remove(USER_NAME).remove(PASSWORD).remove(LIB_CODE).commit()
    }

    public fun hasAccount(con: Context): Boolean {
        return getStoreAccount(con).password.size > 5
    }

    public fun getStoreAccount(con: Context): LoginRequest {
        val sh = con.getSharedPreferences(ACCOUNT_SHARE_PREFERENCES, Context.MODE_PRIVATE)
        return LoginRequest(sh.getString(USER_NAME, ""), sh.getString(PASSWORD, ""), sh.getInt(LIB_CODE, 0), null)
    }


    public fun storeAccount(req: LoginRequest, con: Context): Unit = con.getSharedPreferences(ACCOUNT_SHARE_PREFERENCES, Context.MODE_PRIVATE).edit().putString(USER_NAME, req.username).putString(PASSWORD, req.password).putInt("lib_code", req.libCode).apply()


}