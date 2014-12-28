package com.ybook.app.ui.search

import com.ybook.app.pinnedheaderlistview.PinnedHeaderListView
import com.ybook.app.util.ListEndToLoadUtil
import android.view.View
import android.widget.AdapterView.OnItemClickListener
import android.content.Context
import android.view.MenuItem
import android.os.Bundle
import android.widget.ListAdapter
import android.support.v7.widget.RecyclerView
import android.content.Intent

/**
 * Created by Carlos on 2014/12/17.
 */
public trait SearchPresenter : RecyclerView.OnScrollListener, View.OnClickListener {
    public fun onCreate(savedInstanceState: Bundle?)
    public fun onResume()
    public fun onPause()
    public fun getAdapter(): RecyclerView.Adapter<SearchViewHolder>
    public fun onOptionsItemSelected(item: MenuItem?): Boolean
    public fun onNewIntent(intent: Intent)
}