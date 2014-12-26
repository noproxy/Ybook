package com.ybook.app.ui.detail

import com.ybook.app.swipebacklayout.SwipeBackActivity
import android.os.Bundle
import com.ybook.app.R
import com.ybook.app.id
import com.melnykov.fab.FloatingActionButton
import android.widget.ImageView
import android.widget.TextView
import android.support.v4.view.ViewPager
import com.ybook.app.viewpagerindicator.TabPageIndicator
import com.squareup.picasso.Picasso

/**
 * Created by Carlos on 2014/12/24.
 */
public trait BookDetailView : SwipeBackActivity {

}

public class BookDetailActivity() : BookDetailView, SwipeBackActivity() {
    var mPresenter: BookDetailPresenter? = null
    var mFloatingActionBtn: FloatingActionButton? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super<BookDetailView>.onCreate(savedInstanceState)
        setContentView(R.layout.book_details_activity)
        mPresenter = BookDetailPresenterImpl(this)
        mPresenter!!.onCreate()


        mFloatingActionBtn = id(R.id.fab) as FloatingActionButton
        val imageView = id(R.id.image_view_book_cover) as ImageView
        val titleView = id(R.id.text_view_book_title) as TextView
        val viewPager = id(R.id.detail_viewPager) as ViewPager
        val indicator = id(R.id.detail_viewPager_indicator) as TabPageIndicator


    }
}