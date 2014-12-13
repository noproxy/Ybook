package com.ybook.app.ui

import com.ybook.app.swipebacklayout.SwipeBackActivity
import android.os.Bundle
import com.ybook.app.R
import com.ybook.app.bean.BookListResponse
import android.widget.ListView
import android.widget.BaseAdapter
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.content.Intent
import com.umeng.analytics.MobclickAgent
import android.content.Context
import android.view.LayoutInflater
import com.ybook.app.bean.SearchResponse.SearchObject
import com.ybook.app.id
import android.widget.ImageView
import com.squareup.picasso.Picasso
import com.ybook.app.bean.BookItem
import android.widget.Toast
import android.app.ProgressDialog
import com.ybook.app.net.PostHelper
import com.ybook.app.net.DetailRequest
import com.ybook.app.bean.getLibCode
import android.os.Handler
import android.os.Message
import com.ybook.app.net.MSG_SUCCESS
import com.ybook.app.bean.DetailResponse
import com.ybook.app.net.MSG_ERROR
import java.util.ArrayList
import com.ybook.app.util.BooksListUtil

/**
 * Created by carlos on 12/8/14.
 */


public class NewBookListActivity() : SwipeBackActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super<SwipeBackActivity>.onCreate(savedInstanceState)
        setContentView(R.layout.activity_book_list)
        val rep = getIntent().getSerializableExtra(KEY_BOOK_LIST_RESPONSE_EXTRA)
        when (rep) {
            is BookListResponse -> {
                val list = findViewById(android.R.id.list) as ListView
                listItems addAll rep.books.toListItem()
                list.setAdapter(this.TmpBookListAdapter())
                list.setOnItemClickListener {(adapterView, view, i, l) ->
                    val tag = view.getTag()
                    when (tag) {
                        is SearchObject -> {
                            val intent = Intent(view.getContext(), javaClass<BookDetailActivity>())
                            intent.putExtra(KEY_BOOK_LIST_RESPONSE_EXTRA, tag)
                            startActivity(intent)
                        }
                    }
                }
            }
            else -> this.finish()
        }
    }

    fun Array<BookListResponse.BookListObject>.toListItem(): ArrayList<SearchObject> {
        val a = ArrayList<SearchObject>()
        this.forEach { i ->
            a.add(SearchObject(i.author, i.press, "loading", "record", i.title, i.coverImgUrl, i.id))
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

    val listItems = ArrayList<SearchObject>()
    val mUtil = BooksListUtil.getInstance(this)

    inner class TmpBookListAdapter() : BaseAdapter() {
        override fun getItem(position: Int): Any? = listItems[position]
        override fun getCount(): Int = listItems.size
        override fun getItemId(position: Int) = position.toLong()
        override fun getView(position: Int, convertView: View?, parent: ViewGroup): View? {
            val v = convertView ?: LayoutInflater.from(this@NewBookListActivity).inflate(R.layout.search_result_item, parent, false)
            val item = getItem(position) as SearchObject

            v setTag item
            v id(R.id.bookMarkBtn) setTag item
            (v id R.id.text_view_book_title) as TextView setText item.title
            (v id R.id.text_view_book_query_id) as TextView setText item.id
            (v id R.id.text_view_book_author) as TextView setText item.author
            (v id R.id.text_view_book_publisher) as TextView setText item.press
            val coverImage = (v id R.id.image_view_book_cover) as ImageView
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
            return v
        }

    }
}

