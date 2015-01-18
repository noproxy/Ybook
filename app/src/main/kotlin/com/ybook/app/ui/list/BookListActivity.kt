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

package com.ybook.app.ui.list

import com.ybook.app.swipebacklayout.SwipeBackActivity
import android.os.Bundle
import com.ybook.app.bean.BookListResponse
import android.widget.ListView
import android.widget.BaseAdapter
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.ybook.app.R
import com.ybook.app.ui.home
import android.content.Intent
import com.umeng.analytics.MobclickAgent
import com.ybook.app.ui.detail.DetailActivity

/**
 * Created by carlos on 12/8/14.
 */


deprecated("NewBookListActivity instead") public class BookListActivity() : SwipeBackActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super<SwipeBackActivity>.onCreate(savedInstanceState)
        setContentView(R.layout.activity_book_list)
        val rep = getIntent().getSerializableExtra(home.KEY_BOOK_LIST_RESPONSE_EXTRA)
        when (rep) {
            is BookListResponse -> {
                val list = findViewById(android.R.id.list) as ListView
                list.setAdapter(object : BaseAdapter() {
                    override fun getCount(): Int = rep.books.size
                    override fun getItem(position: Int): Any? = rep.books.get(position)
                    override fun getItemId(position: Int) = position.toLong()
                    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View? {
                        val book = getItem(position) as BookListResponse.BookListObject
                        val v = convertView ?: getLayoutInflater().inflate(R.layout.item_book_list, parent, false)
                        ( v.findViewById(R.id.text_view_book_title) as TextView ).setText(book.title)
                        v.setTag(book)
                        return v
                    }
                })
                list.setOnItemClickListener {(adapterView, view, i, l) ->
                    val tag = view.getTag()
                    when (tag) {
                        is BookListResponse.BookListObject -> {
                            val intent = Intent(view.getContext(), javaClass<DetailActivity>())
                            intent.putExtra(home.KEY_BOOK_LIST_RESPONSE_EXTRA, tag)
                            startActivity(intent)
                        }
                    }
                }
            }
            else -> this.finish()
        }
    }

    override fun onResume() {
        super<SwipeBackActivity>.onResume()
        MobclickAgent.onResume(this);
    }

    override fun onPause() {
        super<SwipeBackActivity>.onPause()
        MobclickAgent.onPause(this);
    }
}