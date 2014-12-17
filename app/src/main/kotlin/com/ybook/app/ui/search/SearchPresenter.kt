package com.ybook.app.ui.search

import com.ybook.app.pinnedheaderlistview.PinnedHeaderListView
import com.ybook.app.util.ListEndToLoadUtil
import android.view.View
import android.widget.AdapterView.OnItemClickListener
import android.content.Context
import android.view.MenuItem
import android.os.Bundle
import android.widget.ListAdapter

/**
 * Created by Carlos on 2014/12/17.
 */
public trait SearchPresenter : OnItemClickListener, ListEndToLoadUtil.OnListEndCallback, View.OnClickListener {
    public fun onCreate(savedInstanceState: Bundle?)
    public fun onResume()
    public fun onPause()
    public fun getAdapter(): ListAdapter
    public fun onOptionsItemSelected(item: MenuItem?): Boolean
}