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

import com.ybook.app.id
import com.ybook.app.swipebacklayout.SwipeBackActivity
import android.os.Bundle
import android.support.v7.widget.Toolbar
import com.ybook.app.bean.BookListResponse
import android.widget.ListView
import com.ybook.app.bean.SearchResponse
import android.view.MenuItem
import java.util.ArrayList
import android.widget.BaseAdapter
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.ImageView
import android.os.Handler
import android.os.Message
import com.ybook.app.bean.DetailResponse
import com.ybook.app.R
import com.ybook.app.ui.home
import android.content.Intent
import com.umeng.analytics.MobclickAgent
import com.ybook.app.util.BooksListUtil
import android.view.LayoutInflater
import com.squareup.picasso.Picasso
import com.ybook.app.bean.BookItem
import android.widget.Toast
import android.app.ProgressDialog
import com.ybook.app.net.PostHelper
import com.ybook.app.net.MSG_ERROR
import com.ybook.app.net.DetailRequest
import com.ybook.app.net.MSG_SUCCESS
import com.ybook.app.bean.getLibCode
import com.ybook.app.ui.detail.DetailActivity

/**
 * A new implement of the recommended book list interface.
 * This implement adapting the RecyclerView and CardView.
 *
 * book list interface is similar to search result interface, but the former has a recommending card in addition.
 */


public class NewBookListActivity() : SwipeBackActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super<SwipeBackActivity>.onCreate(savedInstanceState)
        setContentView(R.layout.activity_book_list)

        setSupportActionBar(id(R.id.toolBar) as Toolbar)
        getSupportActionBar() setDisplayHomeAsUpEnabled true
        getSupportActionBar() setDisplayUseLogoEnabled false


        val rep = getIntent().getSerializableExtra(home.KEY_BOOK_LIST_RESPONSE_EXTRA)
        when (rep) {
            is BookListResponse -> {
                setTitle(rep.title)
                //TODO the ListView need to be replaced with RecyclerView.
                val list = findViewById(android.R.id.list) as ListView
                listItems addAll rep.books.toListItem()
                list.setAdapter(this.TmpBookListAdapter(rep.comment))
                list.setOnItemClickListener {(adapterView, view, i, l) ->
                    if (i != 0) {
                        val tag = view.getTag()
                        when (tag) {
                            is SearchResponse.SearchObject -> {
                                //TODO handle the DetailActivity to compatible with this.
                                val intent = Intent(view.getContext(), javaClass<DetailActivity>())
                                intent.putExtra(home.KEY_BOOK_LIST_RESPONSE_EXTRA, tag)
                                startActivity(intent)
                            }
                        }
                    }
                }
            }
            else -> this.finish()
        }
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.getItemId()) {
            android.R.id.home -> onBackPressed()
        }
        return super<SwipeBackActivity>.onOptionsItemSelected(item)
    }

    fun Array<BookListResponse.BookListObject>.toListItem(): ArrayList<SearchResponse.SearchObject> {
        val a = ArrayList<SearchResponse.SearchObject>()
        this.forEach { i ->
            a.add(SearchResponse.SearchObject(i.author, i.press, "loading", "record", i.title, i.coverImgUrl, i.id))
        }
        return a
    }

    override fun onResume() {
        super<SwipeBackActivity>.onResume()
        MobclickAgent.onResume(this);
    }

    override fun onPause() {
        super<SwipeBackActivity>.onPause()
        MobclickAgent.onPause(this);
    }

    val listItems = ArrayList<SearchResponse.SearchObject>()
    val mUtil = BooksListUtil.getInstance(this)

    //TODO this implement need improving. Just copy from old.
    inner class TmpBookListAdapter(val comment: String) : BaseAdapter() {
        override fun getItem(position: Int): Any? = listItems[position - 1]
        override fun getCount(): Int = listItems.size + 1
        override fun getItemId(position: Int) = (position - 1).toLong()
        override fun getView(position: Int, convertView: View?, parent: ViewGroup): View? {
            val v = convertView ?: LayoutInflater.from(this@NewBookListActivity).inflate(R.layout.search_result_item, parent, false)
            val commentView = (v id R.id.textViewComment) as TextView
            val markBtnView = (v id R.id.text_view_book_title) as TextView
            val titleView = (v id R.id.text_view_book_title) as TextView
            val queryIdView = (v id R.id.text_view_book_query_id) as TextView
            val authorView = (v id R.id.text_view_book_author) as TextView
            val pressView = (v id R.id.text_view_book_publisher) as TextView
            val coverImage = (v id R.id.image_view_book_cover) as ImageView
            val linearView = v id R.id.linearView
            when (position) {
                0 -> {
                    commentView setVisibility View.VISIBLE
                    coverImage setVisibility View.GONE
                    linearView setVisibility View.GONE
                    markBtnView setVisibility View.GONE
                    commentView setText comment
                }
                else -> {
                    commentView setVisibility View.GONE
                    coverImage setVisibility View.VISIBLE
                    linearView setVisibility View.VISIBLE
                    markBtnView setVisibility View.VISIBLE

                    val item = getItem(position) as SearchResponse.SearchObject
                    v setTag item
                    markBtnView setTag item
                    titleView setText item.title
                    queryIdView setText item.id
                    authorView setText item.author
                    pressView setText item.press

                    Picasso.with(this@NewBookListActivity) load item.coverImgUrl error (getResources() getDrawable R.drawable.ic_error ) into coverImage
                    val collectionBtn = (v id R.id.bookMarkBtn) as ImageView
                    collectionBtn setImageResource(if (item isMarked mUtil) R.drawable.ic_marked else R.drawable.ic_mark)
                    collectionBtn setOnClickListener { v ->
                        when (v.getId() ) {
                            R.id.bookMarkBtn -> {
                                if (item.isMarked(mUtil)) {
                                    BookItem.cancelMarked(mUtil, item)
                                    (v as ImageView).setImageResource(R.drawable.ic_mark)
                                    Toast.makeText(this@NewBookListActivity, getResources().getString(R.string.toastCancelMark), Toast.LENGTH_SHORT).show()
                                } else {
                                    val dialog = ProgressDialog(this@NewBookListActivity)
                                    dialog.setMessage(getResources().getString(R.string.loadingMessage))
                                    dialog.setIndeterminate(true)
                                    dialog.setCancelable(false)
                                    dialog.show()
                                    PostHelper.detail(DetailRequest(item.id, item.idType, getLibCode()), object : Handler() {
                                        override fun handleMessage(msg: Message) {
                                            dialog.dismiss()
                                            when (msg.what) {
                                                MSG_SUCCESS -> {
                                                    val book = (msg.obj as DetailResponse).toBookItem()
                                                    book.markOrCancelMarked(mUtil)
                                                    (v as ImageView).setImageResource(R.drawable.ic_marked)
                                                    Toast.makeText(this@NewBookListActivity, getResources().getString(R.string.toastMarked), Toast.LENGTH_SHORT).show()
                                                }
                                                MSG_ERROR -> Toast.makeText(this@NewBookListActivity, getResources().getString(R.string.collectFailErrorHint) + item.title, Toast.LENGTH_SHORT).show()
                                            }
                                        }
                                    })
                                }
                            }
                        }
                    }
                }
            }

            return v
        }

    }
}