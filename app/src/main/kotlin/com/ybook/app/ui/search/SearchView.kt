package com.ybook.app.ui.search

import android.content.Context
import com.ybook.app.swipebacklayout.SwipeBackActivity
import com.ybook.app.ui.search.SearchView.MessageType
import android.widget.Toast
import com.ybook.app.R
import android.os.Bundle
import com.ybook.app.id
import android.widget.ListView
import com.ybook.app.util.ListEndToLoadUtil
import android.view.MenuItem

/**
 * Created by Carlos on 2014/12/17.
 */
public trait SearchView : SwipeBackActivity {
    fun setTitle(title: String): SearchView
    fun showProgress(): SearchView
    fun hideProgress(): SearchView
    fun showEmpty(): SearchView
    fun hideEmpty(): SearchView
    fun showLoadErrorMessage(): SearchView
    fun showLoadPageMessage(page: Int): SearchView
    fun showUnknownMessage(): SearchView
    fun showMessage(msg: String, type: MessageType): SearchView
    enum class MessageType {
        ERROR; ALERT; INFO
    }
}

public class SearchActivity : SwipeBackActivity(), SearchView {


    private var mPresenter: SearchPresenter? = null


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
        val listView = id(android.R.id.list) as ListView
        listView setAdapter mPresenter!!.getAdapter()
        listView setOnItemClickListener mPresenter
        ListEndToLoadUtil.setupListEndToLoad(mPresenter, listView)
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