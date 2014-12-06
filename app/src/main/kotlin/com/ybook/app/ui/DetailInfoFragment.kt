package com.ybook.app.ui


import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.ybook.app.R
import com.ybook.app.bean.SearchResponse.SearchObject
import com.ybook.app.bean.DetailResponse
import com.ybook.app.ui.BookDetailActivity.OnDetail
import com.ybook.app.bean.BookItem

/**
 * Created by carlos on 9/14/14.
 */
public class DetailInfoFragment(val searchObject: SearchObject?, val bookItem: BookItem?) : Fragment(), OnDetail {
    var mView: View? = null

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View {

        mView = inflater!!.inflate(R.layout.detail_infor_pager_frag, null, false)
        refresh()
        return mView!!
    }


    override fun onResume() {
        super<Fragment>.onResume()
        refresh()
    }

    var mDetailResponse: DetailResponse? = bookItem?.detailResponse

    fun refresh() {
        val authorTextView = mView?.findViewById(R.id.text_view_book_author) as TextView
        val publisherTextView = mView?.findViewById(R.id.text_view_book_publisher) as TextView
        val detailTextView = mView?.findViewById(R.id.text_view_book_detail) as TextView
        val queryTextView = mView?.findViewById(R.id.text_view_book_query_id) as TextView
        val isbnTextView = mView?.findViewById(R.id.text_view_book_isbn) as TextView

        if (mDetailResponse == null) {
            authorTextView.setText(searchObject!!.author)
            publisherTextView.setText(searchObject.press)
            detailTextView.setText(searchObject.detail)
            queryTextView.setText(R.string.loading_content)
            isbnTextView.setText(R.string.loading_content)
        } else {
            authorTextView.setText(mDetailResponse!!.author)
            publisherTextView.setText(mDetailResponse!!.publish)
            detailTextView.setText(mDetailResponse!!.detail)
            queryTextView.setText(mDetailResponse!!.queryID)
            isbnTextView.setText(mDetailResponse!!.isbn)
        }
    }

    override public fun onRefresh(detail: DetailResponse) {
        mDetailResponse = detail
        refresh()
    }

    override public fun onError() {

    }
}