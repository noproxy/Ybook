package com.ybook.app.ui.search

import android.content.Context
import com.ybook.app.swipebacklayout.SwipeBackActivity
import android.support.v7.widget.RecyclerView.LayoutManager
import android.support.v7.widget.LinearLayoutManager

/**
 * Created by Carlos on 2014/12/17.
 */
public trait SearchView : SwipeBackActivity {
    public fun setTitle(title: String): SearchView
    public fun showProgress(): SearchView
    public fun hideProgress(): SearchView
    public fun showEmpty(): SearchView
    public fun hideEmpty(): SearchView
    public fun showLoadErrorMessage(): SearchView
    public fun showLoadPageMessage(page: Int): SearchView
    public fun showUnknownMessage(): SearchView
    public fun showMessage(msg: String, type: MessageType): SearchView
    public fun getLayoutManager(): LinearLayoutManager
    public enum class MessageType {
        ERROR;ALERT;INFO
    }
}