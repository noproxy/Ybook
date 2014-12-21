package com.ybook.app.ui.search

import com.ybook.app.id
import com.ybook.app.swipebacklayout.SwipeBackActivity
import com.ybook.app.util.ListEndToLoadUtil
import com.ybook.app.bean.SearchResponse
import android.widget.ListView
import android.view.MenuItem
import android.os.Bundle
import com.ybook.app.ui.BookDetailActivity
import java.io.Serializable
import android.os.Handler
import android.os.Message
import android.content.Context
import android.widget.BaseAdapter
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.ImageView
import com.ybook.app.bean.DetailResponse
import com.ybook.app.util.BooksListUtil
import java.util.ArrayList
import com.ybook.app.bean
import com.ybook.app.net.PostHelper
import com.umeng.analytics.MobclickAgent
import com.ybook.app.R
import android.widget.Toast
import android.app.SearchManager
import android.content.Intent
import android.util.Log
import com.ybook.app.net
import android.view.LayoutInflater
import com.squareup.picasso.Picasso
import com.ybook.app.bean.BookItem
import com.ybook.app
import android.app.ProgressDialog
import com.ybook.app.net.SearchRequest
import com.ybook.app.net.DetailRequest
import com.ybook.app.ui.search.SearchView.MessageType

public class SearchActivity : SwipeBackActivity(), SearchView {


    private var mPresenter: SearchPresenter? = null
    var mListView: ListView ? = null


    override fun setTitle(title: String): SearchView {
        getActionBar() setTitle title
        return this
    }

    override fun showProgress() = this

    override fun hideProgress() = this

    override fun showEmpty(): SearchView = this

    override fun hideEmpty(): SearchView = this

    override fun showMessage(msg: String, type: SearchView.MessageType): SearchView {
        when (type) {
            MessageType.ALERT -> Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
            MessageType.ERROR -> Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
            MessageType.INFO -> Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
        }
        return this
    }

    override fun showUnknownMessage(): SearchView = showMessage(getResources() getString R.string.unknownError, MessageType.ERROR)


    override fun showLoadErrorMessage(): SearchView = showMessage(getResources() getString R.string.loadSearchError, MessageType.ERROR)

    override fun showLoadPageMessage(page: Int) = showMessage((getResources() getString R.string.loadingSearchMessagePrefix) + " " + (page + 1), MessageType.INFO)
    override fun onCreate(savedInstanceState: Bundle?) {
        super<SwipeBackActivity>.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search_result)
        mPresenter = SearchPresenterImpl(this)
        mListView = id(android.R.id.list) as ListView
        mListView!! setAdapter mPresenter!!.getAdapter()
        mListView!! setOnItemClickListener mPresenter
        ListEndToLoadUtil.setupListEndToLoad(mPresenter, mListView)
        mPresenter!!.onCreate(savedInstanceState)
    }


    override fun onResume() {
        super<SwipeBackActivity>.onResume()
        mPresenter!!.onResume()
    }

    override fun onPause() {
        super<SwipeBackActivity>.onPause()
        mPresenter!!.onPause()
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        mPresenter!!.onOptionsItemSelected(item)
        return super<SwipeBackActivity>.onOptionsItemSelected(item)
    }


}