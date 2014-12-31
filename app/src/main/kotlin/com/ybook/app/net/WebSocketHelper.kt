package com.ybook.app.net

import com.koushikdutta.async.http.WebSocket
import android.os.Handler
import com.koushikdutta.async.http.AsyncHttpClient
import org.json.JSONObject
import org.json.JSONArray
import java.util.LinkedHashMap
import java.util.ArrayList
import android.util.Log
import com.koushikdutta.async.http.WebSocket.StringCallback
import com.ybook.app.util.AccountHelper
import com.ybook.app.util.JSONHelper
import me.toxz.kotlin.makeTag

/**
 *  read Token of json string
 */
fun String.getToken(): Int {
    try {
        val j = JSONObject(this)
        return j.getInt("token")
    } catch (e: Exception) {
        return this.hashCode()
    }
}


/**
 *
 */
object WebSocketHelper : StringCallback {
    override fun onStringAvailable(s: String?) {
        Log.i(TAG, "response:" + s)
        val hash = s?.getToken()
        Log.i(TAG, hash.toString())
        val r = map.get(hash)
        if (r == null) {
            return
        }
        val m = r.handler.obtainMessage()
        assert(m != null, "avoid complier bug")
        m.what = MSG_SUCCESS
        Log.i(TAG, "a response recieved")
        when (r) {
            is CurrentRequest -> m.obj = JSONHelper.readCurrentResponse(s)
            is HistoryRequest -> m.obj = JSONHelper.readHistoryResponse(s)
            is RenewRequest -> m.obj = JSONHelper.readRenewResponse(s)
            else -> m.what = MSG_ERROR
        }
        r.handler.sendMessage(m)
        map.remove(hash)
    }

    val map = LinkedHashMap<Int, SocketRequest>()
    val list = ArrayList<SocketRequest>()
    var w: WebSocket? = null
    var isLogin: Boolean = false
    val TAG = "WebSocketHelper"

    /**
     * set up connection.
     */
    val reConnect = {
        val mainUrl = getMainUrl()
        if (mainUrl != null) {
            val url = mainUrl + "/profile"
            Log.i(TAG, "connect: " + url)
            AsyncHttpClient.getDefaultInstance().websocket(url, "my-protocol", {(exception, webSocket) ->
                Log.i(TAG, "webSocket is " + webSocket)
                Log.i(TAG, "exception is " + exception?.getMessage())
                w = webSocket
                w?.setClosedCallback {(ex: Exception?) ->
                    Log.i(TAG, "closed")
                    w = null
                    isLogin = false
                    start()
                }
                w?.setStringCallback {(s: String) ->
                    val rep = JSONHelper.readLoginResponse(s)
                    Log.i(TAG, "login status:" + rep.status)
                    if (rep.status == 1) {
                        isLogin = false
                    } else {
                        isLogin = true
                        w!!.setStringCallback(this)
                    }
                    start()
                }
                login()
            })
        }
    }

    val login = {
        Log.i(TAG, "login..." + ( w == null))
        w?.send(AccountHelper.getStoreAccount().getJSONStr())
    }


    private fun start(): Unit {
        Log.i(TAG, "start, list size: " + list.size)
        if (list.empty) {
            Log.i(TAG, "list is empty")
            return
        }
        if (w == null) {
            Log.i(TAG, "websocket is null")
            //fail connect again, network error
            for (req in list) {
                req.handler.sendEmptyMessage(MSG_ERROR)
            }
            return
        }
        if (!isLogin) {
            Log.i(TAG, "websocket not login")
            for (req in list) {
                req.handler.sendEmptyMessage(MSG_PASSWORD_WRONG)
                break
            }
            return
        }
        for (req in list) {
            map.put(req.hashCode(), req)
            w?.send(req.getJSONStr())
            list.remove(req)
            Log.i(TAG, "a req sent")
        }
    }


    /**
     * request profile.But you must login  first.
     */
    public fun request(req: SocketRequest): Unit {
        list.add(req)
        Log.i(TAG, "request")
        if (w == null) {
            Log.d(TAG, "w == null")
            reConnect()
        } else if (!isLogin) {
            Log.d(TAG, "isLogin false")
            login()
        } else start()
    }

    public fun login(req: LoginRequest) {
        list.clear()
        if (w == null) reConnect()//list empty
        if (w == null) {
            req.han?.sendEmptyMessage(MSG_ERROR)
            return
        }
        w!!.setStringCallback {(s: String) ->
            isLogin = true
            req.han?.sendEmptyMessage(MSG_SUCCESS)
        }
        w!!.send(req.getJSONStr())
    }


}

abstract class SocketRequest(val handler: Handler) {
    abstract public fun getJSONStr(): String
}

data class LoginRequest(val username: String, val password: String, val libCode: Int = 0, val han: Handler?) {
    fun getJSONStr(): String {
        val j = JSONObject()
        j.put("action", "login")
        j.put("lib_code", libCode)
        j.put("username", username)
        j.put("password", password)
        j.put("token", this.hashCode())
        val s = j.toString()
        Log.i("data class LoginRequest", s)
        return s
    }

}


data class RenewRequest(val renewList: Array<String>, val libCode: Int, val han: Handler) : SocketRequest(han) {
    override fun getJSONStr(): String {
        val j = JSONObject()
        j.put("action", "renew")
        j.put("lib_code", libCode)
        j.put("renew_list", JSONArray(renewList))
        j.put("token", this.hashCode())
        val s = j.toString()
        Log.i("data class RenewRequest", s)
        return s
    }
}

data class CurrentRequest(val page: Int = 0, val libCode: Int = 0, val han: Handler) : SocketRequest(han) {
    override fun getJSONStr(): String {
        val j = JSONObject()
        j.put("action", "current")
        j.put("page", page + 1)
        j.put("lib_code", libCode)
        j.put("token", this.hashCode())
        val s = j.toString()
        Log.i("data class CurrentRequest", s)
        return s
    }
}

data class HistoryRequest(val page: Int = 0, val libCode: Int = 0, val han: Handler) : SocketRequest(han) {
    override fun getJSONStr(): String {
        val j = JSONObject()
        j.put("action", "history")
        j.put("page", page + 1)
        j.put("lib_code", libCode)
        j.put("token", this.hashCode())
        val s = j.toString()
        Log.i("data class HistoryRequest", s)
        return s
    }
}