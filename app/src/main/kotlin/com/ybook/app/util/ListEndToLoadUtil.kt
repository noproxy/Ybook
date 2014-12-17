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
