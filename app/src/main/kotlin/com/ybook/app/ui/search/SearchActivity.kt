package com.ybook.app.ui.search

import com.ybook.app.id
import com.ybook.app.swipebacklayout.SwipeBackActivity
import com.ybook.app.util.ListEndToLoadUtil
import com.ybook.app.bean.SearchResponse
import android.widget.ListView
import android.view.MenuItem
import android.os.Bundle
import com.ybook.app.ui.detail.BookDetailActivity
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
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.Toolbar
import android.support.v7.widget.LinearLayoutManager
import android.view.Menu
import android.content.ComponentName
import android.widget
import android.support.v7.widget.SearchView.OnQueryTextListener
import android.support.v4.widget.SwipeRefreshLayout
import android.util.TypedValue
import me.toxz.kotlin.after
import com.pnikosis.materialishprogress.ProgressWheel
import android.view.animation.AccelerateInterpolator

public class SearchActivity : SwipeBackActivity(), SearchView {
    override fun getLayoutManager(): LinearLayoutManager = mLayoutManager as LinearLayoutManager


    private val mPresenter: SearchPresenter = SearchPresenterImpl(this)
    var mRecyclerView: RecyclerView ? = null
    private var mLayoutManager: RecyclerView.LayoutManager ? = null
    var mSwipeRefreshLayout: SwipeRefreshLayout? = null
    private var mProgressWheel: ProgressWheel? = null

    class object {
        val REQUEST_CODE_IS_COLLECTION_CHANGED = 0
        val EXTRA_POSITION = "position"
    }

    override fun setTitle(title: String): SearchView {
        getSupportActionBar() setTitle title
        return this
    }

    override fun showProgress(): SearchActivity {
        mProgressWheel?.setVisibility(View.VISIBLE)
        return this
    }

    override fun hideProgress(): SearchActivity {
        mProgressWheel?.setVisibility(View.GONE)
        return this
    }

    override fun startRefresh(): SearchActivity {
        mSwipeRefreshLayout?.setRefreshing(true)
        return this
    }

    override fun endRefresh(): SearchView {
        mSwipeRefreshLayout?.setRefreshing(false)
        return this
    }

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

    var searchView: android.support.v7.widget.SearchView ? = null

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super<SearchView>.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_IS_COLLECTION_CHANGED && data != null) {
            val p = data.getIntExtra(EXTRA_POSITION, 0)
            mPresenter.getAdapter().notifyItemChanged(p)
        }
        if (data != null) {
            val p = data.getIntExtra(EXTRA_POSITION, -1)
            if (p > 0) scrollTo(p)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        getMenuInflater().inflate(R.menu.global, menu)

        // Associate searchable configuration with the SearchView
        val searchManager = getSystemService(Context.SEARCH_SERVICE) as SearchManager
        searchView = menu!!.findItem(R.id.action_search)?.getActionView() as android.support.v7.widget.SearchView
        searchView!!.setSearchableInfo(searchManager.getSearchableInfo(ComponentName(this, javaClass<SearchActivity>())))
        searchView!!.setOnFocusChangeListener {(view, b) -> if (!b) this@SearchActivity.onBackPressed() }
        searchView!!.setOnQueryTextListener(object : OnQueryTextListener {
            override fun onQueryTextSubmit(p0: String?): Boolean {
                searchView!!.clearFocus()
                searchView!!.setIconified(true)
                this@SearchActivity.onBackPressed()
                return false
            }

            override fun onQueryTextChange(p0: String?): Boolean {
                return false
            }

        })

        return super<SearchView>.onCreateOptionsMenu(menu)
    }


    override fun showUnknownMessage(): SearchView = showMessage(getResources() getString R.string.unknownError, MessageType.ERROR)


    override fun showLoadErrorMessage(): SearchView = showMessage(getResources() getString R.string.loadSearchError, MessageType.ERROR)

    override fun showLoadPageMessage(page: Int) = showMessage((getResources() getString R.string.loadingSearchMessagePrefix) + " " + (page + 1), MessageType.INFO)

    var mToolBar: Toolbar? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super<SwipeBackActivity>.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search_result)
        setSupportActionBar((this.id(R.id.toolBar) as Toolbar).after {
            mToolBar = it
        })

        mRecyclerView = (id(android.R.id.list) as RecyclerView).after {
            it setHasFixedSize true
            it setLayoutManager LinearLayoutManager(this).after { mLayoutManager = it }
            it setAdapter mPresenter.getAdapter()
            it setOnScrollListener mPresenter
        }

        mSwipeRefreshLayout = (id(R.id.swipeRefreshLayout) as SwipeRefreshLayout).after {
            it setOnRefreshListener mPresenter
            it setSoundEffectsEnabled true
            it setEnabled false
            it.setColorSchemeResources(android.R.color.holo_green_light, android.R.color.holo_blue_light, android.R.color.holo_red_light, android.R.color.holo_orange_light)
        }
        mProgressWheel = (id(R.id.progressWheel)as ProgressWheel)
        mPresenter.onCreate(savedInstanceState)
    }

    override fun scrollTo(position: Int) {
        mRecyclerView?.smoothScrollToPosition(position)
    }

    var isToolBarShow = true
    override fun showToolBar(bool: Boolean) {
        if (bool && isToolBarShow) {
            mToolBar?.animate()?.translationY(-mToolBar!!.getBottom().toFloat())?.setInterpolator(AccelerateInterpolator())?.start();
        }
        if ( !bool && !isToolBarShow) {
            mToolBar?.animate()?.translationY(mToolBar!!.getHeight().toFloat())?.setInterpolator(AccelerateInterpolator())?.start();
        }
    }

    override fun onNewIntent(intent: Intent) {
        super<SearchView>.onNewIntent(intent)
        mPresenter.onNewIntent(intent)
    }

    override fun onResume() {
        super<SwipeBackActivity>.onResume()
        mPresenter.onResume()
    }

    override fun onPause() {
        super<SwipeBackActivity>.onPause()
        mPresenter.onPause()
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        mPresenter.onOptionsItemSelected(item)
        return super<SwipeBackActivity>.onOptionsItemSelected(item)
    }


}