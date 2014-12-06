package com.ybook.app.ui

import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.squareup.picasso.Picasso
import com.ybook.app.R
import com.ybook.app.util.BooksListUtil
import com.ybook.app.viewpagerindicator.TabPageIndicator
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import android.support.v4.view.ViewPager
import android.support.v4.app.FragmentActivity
import com.ybook.app.bean.SearchResponse.SearchObject
import com.ybook.app.net.PostHelper
import com.ybook.app.net.DetailRequest
import com.ybook.app.bean.getLibCode
import android.os.Handler
import android.os.Message
import com.ybook.app.net.MSG_SUCCESS
import com.ybook.app.bean.DetailResponse
import com.ybook.app.net.MSG_ERROR
import android.support.v4.app.Fragment
import com.ybook.app.bean.BookItem
import de.keyboardsurfer.android.widget.crouton.Crouton
import de.keyboardsurfer.android.widget.crouton.Style
import android.widget.Toast
import android.widget.Button

/**
 * This activity is to display the detail of book of the search results.
 * Created by Carlos on 2014/8/1.
 */
public class BookDetailActivity : FragmentActivity(), View.OnClickListener {


    var mMarkBtn: Button? = null
    private var mSearchObject: SearchObject? = null
    private var mBookItem: BookItem? = null
    private var mUtil = BooksListUtil.getInstance(this)
    //http://ftp.lib.hust.edu.cn/record=b2673698~S0*chx

    override fun onCreate(savedInstanceState: Bundle?) {
        super<FragmentActivity>.onCreate(savedInstanceState)
        setContentView(R.layout.book_details_activity)

        val o = getIntent().getSerializableExtra(INTENT_SEARCH_OBJECT)
        when (o) {
            is SearchObject -> mSearchObject = o
            is BookItem -> mBookItem = o
            else -> this.finish()
        }
        initViews()
    }

    private fun initViews() {
        mMarkBtn = findViewById(R.id.image_view_book_isMarked) as Button
        val imageView = findViewById(R.id.image_view_book_cover) as ImageView
        val titleView = (findViewById(R.id.text_view_book_title) as TextView)
        val viewPager = findViewById(R.id.detail_viewPager) as ViewPager
        val indicator = findViewById(R.id.detail_viewPager_indicator) as TabPageIndicator

        var title: String? = null
        if (mSearchObject == null) {
            Picasso.with(this).load(mBookItem!!.detailResponse.coverImageUrl).error(getResources().getDrawable(R.drawable.ic_error)).resizeDimen(R.dimen.cover_height, R.dimen.cover_width).into(imageView)
            if (mBookItem!!.detailResponse.title.trim().length() == 0) title = getResources().getString(R.string.noTitleHint)
            viewPager.setAdapter(MyDetailPagerAdapter(getSupportFragmentManager(), null, mBookItem!!))
            if (mBookItem!!.isMarked(mUtil)) mMarkBtn!!.setBackgroundResource(R.drawable.detail_btn_selector_collected) else mMarkBtn!!.setBackgroundResource(R.drawable.detail_btn_selector)
        } else {
            Picasso.with(this).load(mSearchObject!!.coverImgUrl).error(getResources().getDrawable(R.drawable.ic_error)).resizeDimen(R.dimen.cover_height, R.dimen.cover_width).into(imageView)
            if (mSearchObject!!.title.trim().length() == 0) title = getResources().getString(R.string.noTitleHint)
            viewPager.setAdapter(MyDetailPagerAdapter(getSupportFragmentManager(), mSearchObject!!, null))
        }
        indicator.setViewPager(viewPager)
        titleView.setText(title)
        setupActionBar()
    }

    private fun setupActionBar() {
        val bar = getActionBar()
        if (bar != null) {
            bar.setTitle(mSearchObject?.title ?: mBookItem?.detailResponse?.title)
            bar.setDisplayShowTitleEnabled(true)
        }
    }

    override fun onClick(v: View) {
        when (v.getId()) {
            R.id.image_view_book_isMarked -> {
                if (mBookItem == null) {
                    Toast.makeText(this, "loading, please try again when loaded.", Toast.LENGTH_SHORT).show()
                } else {
                    mBookItem!!.markOrCancelMarked(mUtil)
                    Crouton.makeText(this, getResources().getString(if (mBookItem!!.isMarked(mUtil)) R.string.toastMarked else R.string.toastCancelMark), Style.INFO).show()
                    mMarkBtn?.setBackgroundResource(if (mBookItem!!.isMarked(mUtil)) R.drawable.detail_btn_selector else R.drawable.detail_btn_selector_collected)
                }
            }
        //            R.id.button_addToList -> {
        //            }
        }
    }


    inner class MyDetailPagerAdapter(fm: FragmentManager, searchObject: SearchObject?, bookItem: BookItem?) : FragmentPagerAdapter(fm) {
        {
            if (searchObject != null) PostHelper.detail(DetailRequest(searchObject.id, searchObject.idType, getLibCode()), object : Handler() {
                override fun handleMessage(msg: Message) {
                    when (msg.what) {
                        MSG_SUCCESS -> onRefresh(msg.obj as DetailResponse)
                        MSG_ERROR -> onError()
                    }
                }
            })

        }
        val pagers = array(DetailInfoFragment(searchObject, bookItem), DetailStoreFragment(searchObject, bookItem))
        val titleResId = array(R.string.detailTabTitleInfo, R.string.detailTabTitleStatus)

        fun onRefresh(detail: DetailResponse) {
            mBookItem = detail.toBookItem()
            pagers[0].onRefresh(detail)
            pagers[1].onRefresh(detail)
        }

        fun onError() {
            pagers[0].onError()
            pagers[1].onError()
        }

        override fun getItem(i: Int) = pagers[i]
        override fun getCount() = 2
        override fun getPageTitle(position: Int) = getResources().getString(titleResId[position])

    }

    class object {
        public val INTENT_SEARCH_OBJECT: String = "searchObject"
    }

    trait OnDetail : Fragment {
        public fun onRefresh(detail: DetailResponse)
        public fun onError()
    }
}
