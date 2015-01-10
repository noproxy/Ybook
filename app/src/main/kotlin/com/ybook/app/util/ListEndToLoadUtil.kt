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

package com.ybook.app.util

import android.util.Log
import android.widget.AbsListView

import java.util.HashMap

/**
 * This Util is to enable a {@link android.widget.ListView} to load more items when scroll to end.
 * <p/>
 * You should realise the {@link com.ybook.app.util.ListEndLoadUtil.OnEndLoadCallback} and call {@link #setupEndLoad(com.ybook.app.util.ListEndLoadUtil.OnEndLoadCallback, android.widget.AbsListView)}
 */
public class ListEndToLoadUtil {

    public trait OnListEndCallback {
        public fun onListEnd()
    }

    class object {
        private val TAG = "ListEndLoadUtil"
        private var mMaps: HashMap<AbsListView, OnListEndCallback>? = null
        private val mListener = object : AbsListView.OnScrollListener {
            deprecated("")
            override fun onScrollStateChanged(view: AbsListView, scrollState: Int) {
            }

            override fun onScroll(view: AbsListView, firstVisibleItem: Int, visibleItemCount: Int, totalItemCount: Int) {
                if (firstVisibleItem + visibleItemCount >= totalItemCount) {
                    mMaps!!.get(view)?.onListEnd()
                }
                //                Log.i(TAG, "firstVisibleItem,visibleItemCount,totalItemCount:" + firstVisibleItem + "," + visibleItemCount + "," + totalItemCount)
            }
        }

        /**
         * to make a list view
         *
         * @param callback    to be called when scroll to end.
         * @param absListView the view to enable loading more when end.
         */
        public fun setupListEndToLoad(callback: OnListEndCallback?, absListView: AbsListView?) {
            if (absListView != null && callback != null) {
                checkInit()
                if (mMaps!!.containsKey(absListView)) {
                    Log.i(TAG, "The absListView had been setup")
                } else {
                    mMaps!!.put(absListView, callback)
                    absListView.setOnScrollListener(mListener)
                }

            }
        }

        public fun removeListEndToLoad(absListView: AbsListView) {
            absListView.setOnScrollListener(null)
            mMaps!!.remove(absListView)
        }

        private fun checkInit() {
            if (mMaps == null) {
                mMaps = HashMap<AbsListView, OnListEndCallback>(2)
            }
        }
    }
}
