package com.ybook.app.net

import com.ybook.app.util.JSONHelper
import android.os.Handler
import org.apache.http.message.BasicNameValuePair
import org.apache.http.impl.client.DefaultHttpClient
import org.apache.http.client.methods.HttpGet
import org.apache.http.util.EntityUtils
import org.apache.http.NameValuePair
import org.apache.http.HttpStatus
import java.util.ArrayList
import org.apache.http.client.methods.HttpPost
import org.apache.http.protocol.HTTP
import org.apache.http.client.entity.UrlEncodedFormEntity
import com.ybook.app.util.OneResultException

/**
 * Created by carlos on 11/11/14.
 */
object PostHelper {

    val detailClient = object : DefaultHttpClient() {}
    val searchClient = object : DefaultHttpClient() {}
    val loginClient = object : DefaultHttpClient() {}
    val bookListClient = object : DefaultHttpClient() {}

    private fun newPost(s: String, data: ArrayList<NameValuePair>?): HttpPost {
        val p = HttpPost(s)
        p.addHeader("application", "x-www-form-urlencoded")
        p.setEntity(UrlEncodedFormEntity(data, HTTP.UTF_8))
        return p
    }

    deprecated("use AsyncTask") fun getBookList(h: Handler) {
        Thread {
            for (i in 1..4) {
                val msg = h.obtainMessage()
                try {
                    val rep = bookListClient.execute(HttpGet(getMainUrl() + "/static/temp/bookrec0" + i.toString() + ".json"))
                    when (rep.getStatusLine().getStatusCode()) {
                        HttpStatus.SC_OK -> {
                            msg.what = MSG_SUCCESS
                            msg.obj = JSONHelper.readBookListResponse(EntityUtils.toString(rep.getEntity(), HTTP.UTF_8))
                        }
                        else -> msg.what = MSG_ERROR
                    }
                    h.sendMessage(msg)
                    rep.getEntity().consumeContent()
                } catch (e: Exception) {
                    e.printStackTrace()
                    msg.what = MSG_ERROR
                    h.sendMessage(msg)
                }
            }
        }.start()
    }

    fun login(req: LoginRequest, h: Handler) {
        val data = ArrayList<NameValuePair>()
        data.add(BasicNameValuePair("action", "login"))
        data.add(BasicNameValuePair("lib_code", req.libCode.toString()))
        data.add(BasicNameValuePair("username", req.username))
        data.add(BasicNameValuePair("password", req.password))
        val msg = h.obtainMessage()
        Thread {
            try {
                val rep = loginClient.execute(newPost(getMainUrl() + "/profile", data))
                when (rep.getStatusLine().getStatusCode()) {
                    HttpStatus.SC_OK -> {
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
        }.start()
    }


    public fun search(req: SearchRequest, h: Handler) {
        val data = ArrayList<NameValuePair>()
        data.add(BasicNameValuePair("key", req.key))
        data.add(BasicNameValuePair("curr_page", req.currPage.toString()))
        data.add(BasicNameValuePair("se_type", req.searchType))
        data.add(BasicNameValuePair("lib_code", req.libCode.toString()))
        val msg = h.obtainMessage()
        Thread {
            try {
                val rep = searchClient.execute(newPost(getMainUrl() + "/search", data))
                when (rep.getStatusLine().getStatusCode()) {
                    HttpStatus.SC_OK -> {
                        msg.what = MSG_SUCCESS
                        val str = EntityUtils.toString(rep.getEntity())
                        try {
                            msg.obj = JSONHelper.readSearchResponse(str)
                        } catch(e: OneResultException) {
                            msg.what = MSG_ONE_SEARCH_RESULT
                            msg.obj = JSONHelper.readDetailResponse(str)
                        }
                    }
                    else -> msg.what = MSG_ERROR
                }
                rep.getEntity().consumeContent()
            } catch (e: Exception) {
                e.printStackTrace()
            }
            h.sendMessage(msg)
        }.start()
    }

    public fun detail(req: DetailRequest, h: Handler) {
        val data = ArrayList<NameValuePair>()
        data.add(BasicNameValuePair("id", req.id))
        data.add(BasicNameValuePair("id_type", req.idType))
        data.add(BasicNameValuePair("lib_code", req.libCode.toString()))
        val msg = h.obtainMessage()
        Thread {
            try {
                val rep = detailClient.execute(newPost(getMainUrl() + "/detail", data))
                when (rep.getStatusLine().getStatusCode()) {
                    HttpStatus.SC_OK -> {
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
        }.start()
    }
}

data class DetailRequest(val id: String, val idType: String = "record", val libCode: Int = 0)

data class SearchRequest(val key: String, val currPage: Int = 0, val searchType: String = "key", val libCode: Int = 0)


