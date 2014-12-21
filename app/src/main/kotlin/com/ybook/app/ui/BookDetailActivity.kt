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
import com.ybook.app.swipebacklayout.SwipeBackActivity
import com.umeng.analytics.MobclickAgent
import com.ybook.app.bean.BookListResponse
import com.ybook.app.id
import android.view.MenuItem
import com.melnykov.fab.FloatingActionButton
import com.ybook.app.util.EVENT_DELETE_FROM_SEARCH
import com.ybook.app.util.EVENT_ADD_FROM_DETAIL
import com.ybook.app.util.EVENT_DELETE_FROM_DETAIL

/**
 * This activity is to display the detail of book of the search results.
 * Created by Carlos on 2014/8/1.
 */
public class BookDetailActivity : SwipeBackActivity(), View.OnClickListener {


    var mMarkFAB: FloatingActionButton? = null
    private var mSearchObject: SearchObject? = null
    private var mBookItem: BookItem? = null
    private var mUtil = BooksListUtil.getInstance(this)
    //http://ftp.lib.hust.edu.cn/record=b2673698~S0*chx

    override fun onCreate(savedInstanceState: Bundle?) {
        super<SwipeBackActivity>.onCreate(savedInstanceState)
        setContentView(R.layout.book_details_activity)

        val o = getIntent() getSerializableExtra INTENT_SEARCH_OBJECT ?: getIntent().getSerializableExtra(KEY_BOOK_LIST_RESPONSE_EXTRA)
        when (o) {
            is SearchObject -> mSearchObject = o
            is BookItem -> mBookItem = o
            is BookListResponse.BookListObject -> mSearchObject = o.toSearchObject()
            else -> this.finish()
        }
        initViews()
    }

    override fun onResume() {
        super<SwipeBackActivity>.onResume()
        MobclickAgent.onResume(this);
    }

    override fun onPause() {
        super<SwipeBackActivity>.onPause()
        MobclickAgent.onPause(this);
    }

    private fun initViews() {
        mMarkFAB = id(R.id.fab) as FloatingActionButton
        val imageView = id(R.id.image_view_book_cover) as ImageView
        val titleView = id(R.id.text_view_book_title) as TextView
        val viewPager = id(R.id.detail_viewPager) as ViewPager
        val indicator = id(R.id.detail_viewPager_indicator) as TabPageIndicator

        var title: String?
        if (mSearchObject == null) {
            Picasso.with(this).load(mBookItem!!.detailResponse.coverImageUrl).error(getResources().getDrawable(R.drawable.ic_error)).resizeDimen(R.dimen.cover_height, R.dimen.cover_width).into(imageView)
            title = mBookItem!!.detailResponse.title.trim()
            viewPager setAdapter MyDetailPagerAdapter(getSupportFragmentManager(), null, mBookItem!!)
            if (mBookItem!! isMarked mUtil) mMarkFAB!! setImageResource  R.drawable.ic_marked
            else mMarkFAB!! setImageResource  R.drawable.ic_mark
        } else {
            Picasso.with(this).load(mSearchObject!!.coverImgUrl).error(getResources().getDrawable(R.drawable.ic_error)).resizeDimen(R.dimen.cover_height, R.dimen.cover_width).into(imageView)
            title = mSearchObject!!.title.trim()
            viewPager.setAdapter(MyDetailPagerAdapter(getSupportFragmentManager(), mSearchObject!!, null))
            if (mSearchObject!! isMarked mUtil ) mMarkFAB!! setImageResource  R.drawable.ic_marked
            else mMarkFAB!! setImageResource  R.drawable.ic_mark

        }
        indicator setViewPager viewPager
        indicator setBackgroundResource R.drawable.indicator_bg_selector
        if (title!!.trim().length() == 0) title = getString(R.string.noTitleHint)
        titleView setText title
        setupActionBar()
    }

    private fun setupActionBar() {
        val bar = getActionBar()
        bar?.setTitle(mSearchObject?.title ?: mBookItem?.detailResponse?.title)
        bar?.setDisplayShowTitleEnabled(true)
        getActionBar() setDisplayHomeAsUpEnabled true
        getActionBar() setDisplayUseLogoEnabled false
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.getItemId()) {
            android.R.id.home -> onBackPressed()
        }
        return super<SwipeBackActivity>.onOptionsItemSelected(item)
    }

    override fun onClick(v: View) {
        when (v.getId()) {
            R.id.fab -> {
                if (mBookItem == null) Toast.makeText(this, "loading, please try again when loaded.", Toast.LENGTH_SHORT).show()
                else {
                    mBookItem!!.markOrCancelMarked(mUtil)
                    if (mBookItem!!.isMarked(mUtil)) {
                        Crouton.makeText(this, getResources().getString(R.string.toastMarked), Style.INFO).show()
                        mMarkFAB!! setImageResource  R.drawable.fab_star_unlike
                        MobclickAgent.onEvent(this, EVENT_ADD_FROM_DETAIL)
                    } else {
                        Crouton.makeText(this, getResources().getString(R.string.toastCancelMark), Style.INFO).show()
                        mMarkFAB!! setImageResource  R.drawable.fab_star_like
                        MobclickAgent.onEvent(this, EVENT_DELETE_FROM_DETAIL)
                    }
                }
            }
        }
    }
    //            R.id.button_addToList -> {
    //            }

    inner class MyDetailPagerAdapter(fm: FragmentManager, searchObject: SearchObject?, bookItem: BookItem?) : FragmentPagerAdapter(fm) {
        {
            if (searchObject != null) PostHelper.detail(DetailRequest(searchObject.id, searchObject.idType, getLibCode()), object : Handler() {
                override fun handleMessage(msg: Message) {
                    when (msg.what) {
                        MSG_SUCCESS -> {
                            mBookItem = (msg.obj as DetailResponse).toBookItem()
                            pagers.forEach { p -> p.onRefresh(msg.obj as DetailResponse) }
                        }
                        MSG_ERROR -> pagers.forEach { p -> p.onError() }
                    }
                }
            })
        }
        val pagers = array(DetailInfoFragment(searchObject, bookItem), DetailStoreFragment(searchObject, bookItem))
        val titleResId = array(R.string.detailTabTitleInfo, R.string.detailTabTitleStatus)
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
