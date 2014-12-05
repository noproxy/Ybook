package com.ybook.app.util

import android.content.Context
import com.ybook.app.net.LoginRequest
import com.ybook.app.MyApplication

/**
 * Created by carlos on 11/12/14.
 */
object AccountHelper {


    val deleteAccount = {

    }

    public fun getStoreAccount(): LoginRequest {
        val sh = MyApplication.getApplication().getSharedPreferences("LoginInfo", Context.MODE_PRIVATE)
        return LoginRequest(sh.getString("userName", ""), sh.getString("password", ""), sh.getInt("lib_code", 0), null)
    }


    public fun storeAccount(req: LoginRequest, con: Context): Unit = con.getSharedPreferences("LoginInfo", Context.MODE_PRIVATE).edit().putString("userName", req.username).putString("password", req.password).putInt("lib_code", req.libCode).apply()


}