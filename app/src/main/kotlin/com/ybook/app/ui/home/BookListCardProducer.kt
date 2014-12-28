package com.ybook.app.ui.home

import android.view.ViewGroup
import com.ybook.app.R
import android.os.Handler
import org.apache.http.impl.client.DefaultHttpClient
import org.apache.http.client.methods.HttpGet
import com.ybook.app.net.getMainUrl
import org.apache.http.HttpStatus
import com.ybook.app.net.MSG_SUCCESS
import com.ybook.app.util.JSONHelper
import org.apache.http.util.EntityUtils
import org.apache.http.protocol.HTTP
import com.ybook.app.net.MSG_ERROR
import com.ybook.app.bean.BookListResponse
import android.view.View
import android.support.v7.widget.CardView
import android.view.LayoutInflater

/**
 * Created by Carlos on 2014/12/28.
 */
object BookListCardProducer {
    private val mHandler = Handler()

    fun addListCardsInto(parent: ViewGroup) {
        val bookListClient = object : DefaultHttpClient() {}
        Thread {
            var c = 0
            var all = 0
            while (true) {
                try {
                    all++
                    if (all > 10 || c > 3) break
                    val rep = bookListClient.execute(HttpGet(getMainUrl() + "/static/temp/bookrec0" + (c + 1).toString() + ".json"))
                    if (rep.getStatusLine().getStatusCode().equals(HttpStatus.SC_OK)) {
                        val bookListResponse = JSONHelper.readBookListResponse(EntityUtils.toString(rep.getEntity(), HTTP.UTF_8))
                        parent.post { parent.addView(makeBookListCardView(bookListResponse, parent)) }
                        c++
                    }
                    rep.getEntity().consumeContent()
                } catch (e: Exception) {
                    e.printStackTrace()
                }
                c++
            }
        }
    }

    fun makeBookListCardView(bookListResponse: BookListResponse, parent: ViewGroup): View {
        val card = LayoutInflater.from(parent.getContext()).inflate(R.layout.book_card, parent, false) as CardView

        return card
    }
}