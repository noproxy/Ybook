package com.ybook.app.net

import com.ybook.app.util.JSONHelper
import android.os.Handler
import org.apache.http.message.BasicNameValuePair
import org.apache.http.impl.client.DefaultHttpClient
import org.apache.http.client.methods.HttpGet
import org.apache.http.util.EntityUtils
import org.apache.http.NameValuePair

/**
 * Created by carlos on 11/11/14.
 */
object PostHelper {

    val detailClient = object : DefaultHttpClient() {}
    val searchClient = object : DefaultHttpClient() {}
    val loginClient = object : DefaultHttpClient() {}
    val bookListClient = object : DefaultHttpClient() {}

    private fun newPost(s: String, data: java.util.ArrayList<NameValuePair>?): org.apache.http.client.methods.HttpPost {
        val p = org.apache.http.client.methods.HttpPost(s)
        p.addHeader("application", "x-www-form-urlencoded")
        p.setEntity(org.apache.http.client.entity.UrlEncodedFormEntity(data, org.apache.http.protocol.HTTP.UTF_8))
        return p
    }

    fun getBookList(h: Handler) {
        val msg = h.obtainMessage()
        Thread(object : Runnable {
            override fun run() {
                try {
                    for (i in 1..4) {
                        val rep = bookListClient.execute(HttpGet(mainUrl + "/static/temp/bookrec0" + i.toString() + ".json"))
                        when (rep.getStatusLine().getStatusCode()) {
                            org.apache.http.HttpStatus.SC_OK -> {
                                msg.what = MSG_SUCCESS
                                msg.obj = JSONHelper.readBookListResponse(EntityUtils.toString(rep.getEntity()))
                            }
                            else -> msg.what = MSG_ERROR
                        }
                        rep.getEntity().consumeContent()
                    }


                } catch (e: Exception) {
                    msg.what = MSG_ERROR
                }
                h.sendMessage(msg)


            }
        }).start()
    }

    fun login(req: LoginRequest, h: Handler) {
        val data = java.util.ArrayList<NameValuePair>()
        data.add(BasicNameValuePair("action", "login"))
        data.add(BasicNameValuePair("lib_code", req.libCode.toString()))
        data.add(BasicNameValuePair("username", req.username))
        data.add(BasicNameValuePair("password", req.password))
        val msg = h.obtainMessage()
        Thread(object : Runnable {
            override fun run() {
                try {
                    val rep = loginClient.execute(newPost(mainUrl + "/profile", data))
                    when (rep.getStatusLine().getStatusCode()) {
                        org.apache.http.HttpStatus.SC_OK -> {
                            msg.what = MSG_SUCCESS
                            msg.obj = JSONHelper.readLoginResponse(EntityUtils.toString(rep.getEntity()))
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


    public fun search(req: SearchRequest, h: Handler) {
        val data = java.util.ArrayList<NameValuePair>()
        data.add(BasicNameValuePair("key", req.key))
        data.add(BasicNameValuePair("curr_page", req.currPage.toString()))
        data.add(BasicNameValuePair("se_type", req.searchType))
        data.add(BasicNameValuePair("lib_code", req.libCode.toString()))
        val msg = h.obtainMessage()
        Thread(object : Runnable {
            override fun run() {
                try {
                    val rep = searchClient.execute(newPost(mainUrl + "/search", data))
                    when (rep.getStatusLine().getStatusCode()) {
                        org.apache.http.HttpStatus.SC_OK -> {
                            msg.what = MSG_SUCCESS
                            msg.obj = JSONHelper.readSearchResponse(EntityUtils.toString(rep.getEntity()))
                        }
                        else -> msg.what = MSG_ERROR
                    }
                    rep.getEntity().consumeContent()
                } catch (e: Exception) {
                    e.printStackTrace()
                    //msg.what = MSG_ERROR TODO produce lots of msg
                }
                h.sendMessage(msg)
            }
        }).start()
    }

    public fun detail(req: DetailRequest, h: Handler) {
        val data = java.util.ArrayList<NameValuePair>()
        data.add(BasicNameValuePair("id", req.id))
        data.add(BasicNameValuePair("id_type", req.idType))
        data.add(BasicNameValuePair("lib_code", req.libCode.toString()))
        val msg = h.obtainMessage()
        Thread(object : Runnable {
            override fun run() {
                try {
                    val rep = detailClient.execute(newPost(mainUrl + "/detail", data))
                    when (rep.getStatusLine().getStatusCode()) {
                        org.apache.http.HttpStatus.SC_OK -> {
                            msg.what = MSG_SUCCESS
                            msg.obj = JSONHelper.readDetailResponse(EntityUtils.toString(rep.getEntity()))
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


