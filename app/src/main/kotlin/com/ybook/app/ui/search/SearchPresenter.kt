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
import android.support.v4.widget.SwipeRefreshLayout
import es.oneoctopus.swiperefreshlayoutoverlay.SwipeRefreshLayoutOverlay
import android.support.v7.widget.RecyclerView.ViewHolder

/**
 * Created by Carlos on 2014/12/17.
 */
public trait SearchPresenter : RecyclerView.OnScrollListener, View.OnClickListener, SwipeRefreshLayoutOverlay.OnRefreshListener {
    public fun onCreate(savedInstanceState: Bundle?)
    public fun onResume()
    public fun onPause()
    public fun getAdapter(): RecyclerView.Adapter<ViewHolder>
    public fun onOptionsItemSelected(item: MenuItem?): Boolean
    public fun onNewIntent(intent: Intent)
}