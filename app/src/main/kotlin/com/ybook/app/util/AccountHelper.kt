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