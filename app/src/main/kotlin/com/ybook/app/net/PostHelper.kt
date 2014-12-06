package com.ybook.app.net

import com.ybook.app.util.JSONHelper
import java.net.ConnectException

/**
 * Created by carlos on 11/11/14.
 */
object PostHelper {

    val client = org.apache.http.impl.client.DefaultHttpClient();

    private fun newPost(s: String, data: java.util.ArrayList<org.apache.http.NameValuePair>?): org.apache.http.client.methods.HttpPost {
        val p = org.apache.http.client.methods.HttpPost(s)
        p.addHeader("application", "x-www-form-urlencoded")
        p.setEntity(org.apache.http.client.entity.UrlEncodedFormEntity(data, org.apache.http.protocol.HTTP.UTF_8))
        return p
    }

    fun login(req: LoginRequest, h: android.os.Handler) {
        val data = java.util.ArrayList<org.apache.http.NameValuePair>()
        data.add(org.apache.http.message.BasicNameValuePair("action", "login"))
        data.add(org.apache.http.message.BasicNameValuePair("lib_code", req.libCode.toString()))
        data.add(org.apache.http.message.BasicNameValuePair("username", req.username))
        data.add(org.apache.http.message.BasicNameValuePair("password", req.password))
        val msg = h.obtainMessage()
        Thread(object : Runnable {
            override fun run() {
                try {
                    val rep = client.execute(newPost(mainUrl + "/profile", data))
                    when (rep.getStatusLine().getStatusCode()) {
                        org.apache.http.HttpStatus.SC_OK -> {
                            msg.what = MSG_SUCCESS
                            msg.obj = JSONHelper.readLoginResponse(org.apache.http.util.EntityUtils.toString(rep.getEntity()))
                        }
                        else -> msg.what = MSG_ERROR
                    }
                    rep.getEntity().consumeContent()
                } catch (e: Exception) {
                    msg.what = MSG_ERROR
                }
                h.sendMessage(msg)
            }
        }).start()
    }


    public fun search(req: SearchRequest, h: android.os.Handler) {
        val data = java.util.ArrayList<org.apache.http.NameValuePair>()
        data.add(org.apache.http.message.BasicNameValuePair("key", req.key))
        data.add(org.apache.http.message.BasicNameValuePair("curr_page", req.currPage.toString()))
        data.add(org.apache.http.message.BasicNameValuePair("se_type", req.searchType))
        data.add(org.apache.http.message.BasicNameValuePair("lib_code", req.libCode.toString()))
        val msg = h.obtainMessage()
        Thread(object : Runnable {
            override fun run() {
                try {
                    val rep = client.execute(newPost(mainUrl + "/search", data))
                    when (rep.getStatusLine().getStatusCode()) {
                        org.apache.http.HttpStatus.SC_OK -> {
                            msg.what = MSG_SUCCESS
                            msg.obj = JSONHelper.readSearchResponse(org.apache.http.util.EntityUtils.toString(rep.getEntity()))
                        }
                        else -> msg.what = MSG_ERROR
                    }
                    rep.getEntity().consumeContent()
                } catch (e: Exception) {
                    msg.what = MSG_ERROR
                }
                h.sendMessage(msg)
            }
        }).start()
    }

    public fun detail(req: DetailRequest, h: android.os.Handler) {
        val data = java.util.ArrayList<org.apache.http.NameValuePair>()
        data.add(org.apache.http.message.BasicNameValuePair("id", req.id))
        data.add(org.apache.http.message.BasicNameValuePair("id_type", req.idType))
        data.add(org.apache.http.message.BasicNameValuePair("lib_code", req.libCode.toString()))
        val msg = h.obtainMessage()
        Thread(object : Runnable {
            override fun run() {
                try {
                    val rep = client.execute(newPost(mainUrl + "/detail", data))
                    when (rep.getStatusLine().getStatusCode()) {
                        org.apache.http.HttpStatus.SC_OK -> {
                            msg.what = MSG_SUCCESS
                            msg.obj = JSONHelper.readDetailResponse(org.apache.http.util.EntityUtils.toString(rep.getEntity()))
                        }
                        else -> msg.what = MSG_ERROR
                    }
                    rep.getEntity().consumeContent()
                } catch (e: Exception) {
                    msg.what = MSG_ERROR
                }
                h.sendMessage(msg)
            }
        }).start()
    }
}

data class DetailRequest(val id: String, val idType: String = "record", val libCode: Int = 0)

data class SearchRequest(val key: String, val currPage: Int = 0, val searchType: String = "key", val libCode: Int = 0)


