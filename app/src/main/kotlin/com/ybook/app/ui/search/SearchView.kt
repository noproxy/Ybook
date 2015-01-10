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
    public fun scrollTo(position: Int)
    public fun startRefresh(): SearchView
    public fun endRefresh(): SearchView
    public fun showToolBar(bool: Boolean)
    public enum class MessageType {
        ERROR; ALERT; INFO
    }
}