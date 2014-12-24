package com.ybook.app.ui.search

import com.ybook.app.pinnedheaderlistview.PinnedHeaderListView
import com.ybook.app.util.ListEndToLoadUtil
import android.view.View
import android.widget.AdapterView.OnItemClickListener
import android.content.Context
import android.view.MenuItem
import android.os.Bundle
import android.widget.ListAdapter
import com.ybook.app.util.BooksListUtil
import java.util.ArrayList
import com.ybook.app.bean.SearchResponse
import android.app.SearchManager
import com.umeng.analytics.MobclickAgent
import android.util.Log
import android.widget.AdapterView
import android.widget.Adapter
import android.content.Intent
import com.ybook.app.ui.BookDetailActivity
import java.io.Serializable
import com.ybook.app.net.PostHelper
import com.ybook.app.net.SearchRequest
import com.ybook.app.bean
import android.os.Handler
import android.os.Message
import com.ybook.app.net
import com.ybook.app.bean.DetailResponse
import com.ybook.app.R
import com.ybook.app.ui.search.SearchView.MessageType
import android.widget.BaseAdapter
import android.view.ViewGroup
import android.view.LayoutInflater
import com.ybook.app.id
import android.widget.TextView
import android.widget.ImageView
import com.squareup.picasso.Picasso
import com.ybook.app.bean.BookItem
import com.ybook.app
import android.widget.Toast
import android.app.ProgressDialog
import com.ybook.app.net.DetailRequest

/**
 * Created by Carlos on 2014/12/17.
 */
public trait SearchPresenter : OnItemClickListener, ListEndToLoadUtil.OnListEndCallback, View.OnClickListener {
    fun onCreate(savedInstanceState: Bundle?)
    fun onResume()
    fun onPause()
    fun getAdapter(): ListAdapter
    fun onOptionsItemSelected(item: MenuItem?): Boolean
}

public class SearchPresenterImpl(val searchView: SearchView) : SearchPresenter, OnItemClickListener {
    val SEARCH_BY_KEY = "key"
    val TAG = "SearchAct"
    val mUtil = BooksListUtil.getInstance(searchView)
    val listItems = ArrayList<SearchResponse.SearchObject>()
    var requestedPage = -1
    var nextPage = 0
    val mKey: String = searchView.getIntent().getStringExtra(SearchManager.QUERY)


    override fun getAdapter() = mAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        searchView.setTitle(mKey)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.getItemId()) {
            android.R.id.home -> searchView.onBackPressed()
        }
        return true
    }

    override fun onResume() {
        MobclickAgent.onResume(searchView);
        if (nextPage == 0) onListEnd()
        Log.i(TAG, "resume, nextPage:" + nextPage)
    }

    override fun onPause() {
        MobclickAgent.onPause(searchView);
    }

    override fun onItemClick(parent: AdapterView<out Adapter>, v: View, position: Int, id: Long) {
        searchView.startActivity(Intent(searchView, javaClass<BookDetailActivity>()).putExtra(BookDetailActivity.INTENT_SEARCH_OBJECT, v.getTag() as Serializable))
    }

    override fun onClick(v: View) {
        throw UnsupportedOperationException()
    }

    override fun onListEnd() {
        if (nextPage > requestedPage && nextPage * 10 == listItems.size()) {
            PostHelper.search(SearchRequest(mKey, nextPage, SEARCH_BY_KEY, bean.getLibCode()), mHandler)
            requestedPage++
            searchView.showLoadPageMessage(nextPage)
        }
    }

    private val mHandler = object : Handler() {
        override fun handleMessage(msg: Message) {
            when (msg.what) {
                net.MSG_ONE_SEARCH_RESULT -> {
                    if (msg.obj is DetailResponse)
                        searchView.startActivity(Intent(searchView, javaClass<BookDetailActivity>()).putExtra(BookDetailActivity.INTENT_SEARCH_OBJECT, (msg.obj as DetailResponse).toBookItem()))
                    searchView.showMessage(searchView.getResources().getString(R.string.onlyOneResultMessage), MessageType.INFO)
                    searchView.finish()
                }
                net.MSG_SUCCESS -> {
                    val r = msg.obj as SearchResponse
                    listItems addAll r.objects
                    nextPage++
                    mAdapter.notifyDataSetChanged()
                }
                net.MSG_ERROR -> searchView.showLoadErrorMessage()
                else -> searchView.showUnknownMessage()
            }
        }
    }

    private val mAdapter = object : BaseAdapter() {
        override fun getItem(position: Int): Any? = listItems[position]
        override fun getCount(): Int = listItems.size
        override fun getItemId(position: Int) = position.toLong()
        override fun getView(position: Int, convertView: View?, parent: ViewGroup): View? {
            val v = convertView ?: LayoutInflater.from(searchView).inflate(R.layout.search_result_item, parent, false)
            val item = getItem(position) as SearchResponse.SearchObject

            v setTag item
            v id(R.id.bookMarkBtn) setTag item
            (v id R.id.text_view_book_title) as TextView setText item.title
            (v id R.id.text_view_book_query_id) as TextView setText item.id
            (v id R.id.text_view_book_author) as TextView setText item.author
            (v id R.id.text_view_book_publisher) as TextView setText item.press
            val coverImage = (v id R.id.image_view_book_cover) as ImageView
            Picasso.with(searchView) load item.coverImgUrl error (searchView.getResources().getDrawable(R.drawable.ic_error) ) into coverImage
            val collectionBtn = (v id R.id.bookMarkBtn) as ImageView
            collectionBtn setImageResource(if (item isMarked mUtil) R.drawable.ic_marked else R.drawable.ic_mark)
            collectionBtn setOnClickListener { v ->
                when (v.getId() ) {
                    R.id.bookMarkBtn -> {
                        if (item.isMarked(mUtil)) {
                            BookItem.cancelMarked(mUtil, item)
                            (v as ImageView).setImageResource(R.drawable.ic_mark)
                            MobclickAgent.onEvent(searchView, app.util.EVENT_DELETE_FROM_SEARCH)
                            Toast.makeText(searchView, searchView getString R.string.toastCancelMark, Toast.LENGTH_SHORT).show()
                        } else {
                            MobclickAgent.onEvent(searchView, app.util.EVENT_ADD_FROM_SEARCH)
                            val dialog = ProgressDialog(searchView)
                            dialog.setMessage(searchView getString R.string.loadingMessage)
                            dialog.setIndeterminate(true)
                            dialog.setCancelable(false)
                            dialog.show()
                            PostHelper.detail(DetailRequest(item.id, item.idType, bean.getLibCode()), object : Handler() {
                                override fun handleMessage(msg: Message) {
                                    dialog.dismiss()
                                    when (msg.what) {
                                        net.MSG_SUCCESS -> {
                                            val book = (msg.obj as DetailResponse).toBookItem()
                                            book.markOrCancelMarked(mUtil)
                                            (v as ImageView).setImageResource(R.drawable.ic_marked)
                                            searchView.showMessage(searchView.getString(R.string.toastMarked), MessageType.INFO)
                                        }
                                        net.MSG_ERROR -> searchView.showMessage(searchView.getString(R.string.collectFailErrorHint) + item.title, MessageType.INFO)
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