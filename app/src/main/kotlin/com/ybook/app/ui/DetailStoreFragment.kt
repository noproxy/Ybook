package com.ybook.app.ui

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.ybook.app.R
import com.ybook.app.bean.SearchResponse.SearchObject
import com.ybook.app.bean.DetailResponse
import com.ybook.app.ui.BookDetailActivity.OnDetail
import android.widget.ListView
import android.widget.BaseAdapter
import android.widget.TextView
import com.ybook.app.bean.BookItem

/**
 * Created by carlos on 9/14/14.
 */
public class DetailStoreFragment(val searchObject: SearchObject?, val bookItem: BookItem?) : Fragment(), OnDetail {
    var mListView: ListView? = null
    //    var mEmptyLayout: EmptyLayout? = null
    var mAdapter: BaseAdapter? = null

    override fun onRefresh(detail: DetailResponse) {
        mDetailResponse = detail
        refresh()
    }

    var mDetailResponse: DetailResponse? = bookItem?.detailResponse


    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val view = inflater!!.inflate(R.layout.detail_store_pager_frag, container, false)
        mListView = view.findViewById(R.id.contentListView) as ListView
        //        mEmptyLayout = EmptyLayout(inflater.getContext(), mListView)
        mAdapter = object : BaseAdapter() {
            override fun getCount() = mDetailResponse?.libInfo?.size() ?: 0

            override fun getItem(position: Int) = mDetailResponse?.libInfo?.get(position)

            override fun getItemId(position: Int) = position.toLong()

            override fun getView(position: Int, convertView: View?, parent: ViewGroup): View? {
                val view = convertView ?: inflater.inflate(R.layout.detail_store_item, parent, false)
                val ordBtn = view.findViewById(R.id.ordBtn)
                if (mDetailResponse == null) {
                    ordBtn.setEnabled(false)
                } else {
                    (view.findViewById(R.id.textViewStoreLocation) as TextView).setText(getItem(position)!!.libLocation)
                    (view.findViewById(R.id.textViewStoreStatus) as TextView).setText(getItem(position)!!.libStatus)
                    ordBtn.setOnClickListener { v ->//TODO order
                    }
                }
                return view
            }

        }
        mListView!!.setAdapter(mAdapter)
        refresh()
        return mListView!!
    }

    fun onLoad(detail: DetailResponse) {
        mDetailResponse = detail
        if (isAdded() ) refresh()
    }

    override fun onError() {

    }

    override fun onResume() {
        super<Fragment>.onResume()
        refresh()
    }

    fun refresh() {
        if (mDetailResponse == null)
        //            mEmptyLayout?.showEmpty()
        else {
            //            mEmptyLayout?.showLoading()
            mAdapter?.notifyDataSetInvalidated()
        }
    }
}