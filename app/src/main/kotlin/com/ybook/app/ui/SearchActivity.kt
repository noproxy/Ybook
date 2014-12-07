package com.ybook.app.ui

import android.os.Bundle
import android.app.SearchManager
import android.os.Handler
import android.os.Message
import android.widget.BaseAdapter
import java.util.ArrayList
import android.view.View
import android.view.ViewGroup
import android.view.LayoutInflater
import android.widget.TextView
import android.content.Context
import android.widget.ImageView
import com.squareup.picasso.Picasso
import com.ybook.app.R
import com.ybook.app.bean.SearchResponse.SearchObject
import com.ybook.app.net.PostHelper
import com.ybook.app.net.SearchRequest
import com.ybook.app.bean.getLibCode
import com.ybook.app.net.MSG_SUCCESS
import com.ybook.app.bean.SearchResponse
import com.ybook.app.net.MSG_ERROR
import com.ybook.app.net.MSG_PASSWORD_WRONG
import com.ybook.app.util.BooksListUtil
import android.widget.ListView
import android.content.Intent
import java.io.Serializable
import android.view.View.OnClickListener
import android.app.ProgressDialog
import com.ybook.app.net.DetailRequest
import com.ybook.app.bean.DetailResponse
import android.util.Log
import android.widget.Toast
import com.ybook.app.bean.BookItem
import com.ybook.app.util.ListEndLoadUtil
import com.ybook.app.swipebacklayout.SwipeBackActivity
import com.umeng.analytics.MobclickAgent

/**
 * Created by carlos on 11/14/14.
 */

public class SearchActivity : SwipeBackActivity(), ListEndLoadUtil.OnEndLoadCallback {
    override fun onEndLoad() {
        if (nextPage > requestedPage && nextPage * 10 == listItems.size()) {
            PostHelper.search(SearchRequest(key!!, nextPage, SEARCH_BY_KEY, getLibCode()), SearchHandler())
            requestedPage++
            loading(nextPage)
        }
    }

    override fun onPause() {
        super<SwipeBackActivity>.onPause()
        MobclickAgent.onPause(this);
    }

    var nextPage = 0
    val SEARCH_BY_KEY = "key"
    val TAG = "SearchAct"
    var requestedPage = -1

    val loadError = { Toast.makeText(this, getResources().getString(R.string.loadSearchError), Toast.LENGTH_SHORT).show() }
    val passError = { Toast.makeText(this, getResources().getString(R.string.passwordError), Toast.LENGTH_SHORT).show() }
    val loading = {(page: Int) -> Toast.makeText(this, getResources().getString(R.string.loadingSearchMessagePrefix) + " " + (page + 1), Toast.LENGTH_SHORT).show() }

    val listItems = ArrayList<SearchObject>()
    var key: String? = null

    val mUtil = BooksListUtil.getInstance(this)
    var mListView: ListView ? = null
    var mAdapter: SearchListAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super<SwipeBackActivity>.onCreate(savedInstanceState)

        setContentView(R.layout.activity_search_result)


        key = getIntent().getStringExtra(SearchManager.QUERY)
        if (key == null) {
            this.finish()
        }

        val actionBar = getActionBar()
        if (actionBar != null) {
            actionBar.setTitle(key)
            actionBar.setDisplayShowTitleEnabled(true)
        }
        mListView = findViewById(android.R.id.list) as ListView
        mAdapter = SearchListAdapter(this)
        mListView!!.setAdapter(mAdapter)
        mListView!!.setOnItemClickListener {(l, v, p, i) ->
            val intent = Intent(this, javaClass<BookDetailActivity>())
            intent.putExtra(BookDetailActivity.INTENT_SEARCH_OBJECT, v?.getTag() as Serializable)
            startActivity(intent)
        }
        ListEndLoadUtil.setupEndLoad(this, mListView)
    }


    override fun onResume() {
        super<SwipeBackActivity>.onResume()
        MobclickAgent.onResume(this);
        if (nextPage == 0) onEndLoad()
        Log.i(TAG, "resume, nextPage:" + nextPage)
    }

    inner class SearchHandler : Handler() {
        override fun handleMessage(msg: Message) {
            when (msg.what) {
                MSG_SUCCESS -> {
                    val r = msg.obj as SearchResponse
                    listItems.addAll(r.objects)
                    nextPage++
                    mAdapter!!.notifyDataSetChanged()
                }
                MSG_ERROR -> loadError()
                MSG_PASSWORD_WRONG -> passError()
            }
        }
    }


    inner class SearchListAdapter(val con: Context) : BaseAdapter() {
        override fun getItem(position: Int): Any? = listItems[position]
        override fun getCount(): Int = listItems.size
        override fun getItemId(position: Int) = position.toLong()
        override fun getView(position: Int, convertView: View?, parent: ViewGroup): View? {
            val v = convertView ?: LayoutInflater.from(con).inflate(R.layout.search_result_item, parent, false)
            val item = getItem(position) as SearchObject

            v.setTag(item)
            v.findViewById(R.id.bookMarkBtn).setTag(item)
            (v.findViewById(R.id.text_view_book_title) as TextView).setText(item.title)
            (v.findViewById(R.id.text_view_book_query_id) as TextView).setText(item.id)
            (v.findViewById(R.id.text_view_book_author) as TextView).setText(item.author)
            (v.findViewById(R.id.text_view_book_publisher) as TextView).setText(item.press)
            val coverImage = v.findViewById(R.id.image_view_book_cover) as ImageView
            Picasso.with(con).load(item.coverImgUrl).error(getResources().getDrawable(R.drawable.ic_error)).into(coverImage)

            val collectionBtn = v.findViewById(R.id.bookMarkBtn) as ImageView
            collectionBtn.setImageResource(if (item.isMarked(mUtil)) R.drawable.ic_marked else R.drawable.ic_mark)
            collectionBtn.setOnClickListener(object : OnClickListener {
                override fun onClick(v: View): Unit = when (v.getId() ) {
                    R.id.bookMarkBtn -> {
                        if (item.isMarked(mUtil)) {
                            BookItem.cancelMarked(mUtil, item)
                            (v as ImageView).setImageResource(R.drawable.ic_mark)
                            Toast.makeText(this@SearchActivity, getResources().getString(R.string.toastCancelMark), Toast.LENGTH_SHORT).show()
                        } else {
                            val dialog = ProgressDialog(this@SearchActivity)
                            dialog.setMessage(getResources().getString(R.string.loadingMessage))
                            dialog.setIndeterminate(true)
                            dialog.setCancelable(false)
                            dialog.show()
                            PostHelper.detail(DetailRequest(item.id, item.idType, getLibCode()),
                                    object : Handler() {
                                        override fun handleMessage(msg: Message) {
                                            dialog.dismiss()
                                            when (msg.what) {
                                                MSG_SUCCESS -> {
                                                    val book = (msg.obj as DetailResponse).toBookItem()
                                                    book.markOrCancelMarked(mUtil)
                                                    (v as ImageView).setImageResource(R.drawable.ic_marked)
                                                    Toast.makeText(this@SearchActivity, getResources().getString(R.string.toastMarked), Toast.LENGTH_SHORT).show()
                                                }
                                                MSG_ERROR -> Toast.makeText(this@SearchActivity, getResources().getString(R.string.collectFailErrorHint) + item.title, Toast.LENGTH_SHORT).show()
                                            }
                                        }
                                    })
                        }
                    }
                }
            })
            return v
        }

    }
}