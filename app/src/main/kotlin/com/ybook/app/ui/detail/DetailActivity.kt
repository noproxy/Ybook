package com.ybook.app.ui.detail

import com.github.ksoichiro.android.observablescrollview.ObservableScrollView
import com.github.ksoichiro.android.observablescrollview.ObservableScrollViewCallbacks
import com.ybook.app.R
import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.os.Bundle
import android.os.PersistableBundle
import com.ybook.app.id
import android.widget.LinearLayout
import com.ybook.app.ui.home
import com.ybook.app.bean.SearchResponse
import com.ybook.app.bean.BookItem
import com.ybook.app.bean.BookListResponse
import android.app.Activity
import com.melnykov.fab.FloatingActionButton
import com.ybook.app.util.BooksListUtil
import android.widget.ImageView
import android.widget.TextView
import me.toxz.kotlin.after
import android.view.LayoutInflater
import android.view.ViewGroup

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
/**
 * Created on 2015/1/11.
 */
public class DetailActivity() : SlidingUpBaseActivity<ObservableScrollView>(), ObservableScrollViewCallbacks {

    class object {
        public val INTENT_SEARCH_OBJECT: String = "searchObject"
        public val RESULT_CODE_UNCHANGED: Int = Activity.RESULT_FIRST_USER
        public val RESULT_CODE_CHANGED: Int = Activity.RESULT_FIRST_USER + 1
    }

    var mMarkFAB: FloatingActionButton? = null
    private var mSearchObject: SearchResponse.SearchObject? = null
    private var mBookItem: BookItem? = null
    private var mObject: Any? = null
    private var mUtil = BooksListUtil.getInstance(this)
    //http://ftp.lib.hust.edu.cn/record=b2673698~S0*chx

    protected override fun getLayoutResId(): Int {
        return R.layout.activity_detail
    }

    protected override fun createScrollable(): ObservableScrollView {
        val scrollView = findViewById(R.id.scroll) as ObservableScrollView
        scrollView.setScrollViewCallbacks(this)
        return scrollView
    }


    override fun onCreate(savedInstanceState: Bundle?, persistentState: PersistableBundle?) {
        super<SlidingUpBaseActivity>.onCreate(savedInstanceState, persistentState)// setContextView is called by super.


        setResult(RESULT_CODE_UNCHANGED, getIntent())
        mObject = getIntent() getSerializableExtra INTENT_SEARCH_OBJECT ?: getIntent().getSerializableExtra(home.KEY_BOOK_LIST_RESPONSE_EXTRA)

        initViews()
    }

    private inner class DetailItemViewHolder(view: View, text: String?, textMain: String?) {
        val textView = ( (view id R.id.item_title) as TextView).after {
            if (text.isNotEmpty()) it setText text
        }

        val textViewMain = (view id R.id.item_text) as TextView after {
            it setText (textMain ?: getString(R.string.loadingContentHint))
        }
    }

    private fun initViews() {
        val container = (id (R.id.main_content)as LinearLayout)
        fun ViewGroup.add(text: String? = null, textMain: String? = null, resID: Int = R.layout.detail_item) = DetailItemViewHolder(getLayoutInflater().inflate(resID, container, true), text, textMain)
        val o = mObject
        when (o) {
            is SearchResponse.SearchObject -> {
                container.add(getString(R.string.authorText), o.author)
                container.add(getString(R.string.publisherText), o.press)
                container.add(getString(R.string.queryIdText))
                container.add(getString(R.string.isbnText))
                container.add(getString(R.string.detailText), o.detail)
            }
            is BookItem -> {
                container.add(getString(R.string.authorText), o.detailResponse.author)
                container.add(getString(R.string.publisherText), o.detailResponse.publish)
                container.add(getString(R.string.queryIdText), o.detailResponse.queryID)
                container.add(getString(R.string.isbnText), o.detailResponse.isbn)
                container.add(getString(R.string.detailText), o.detailResponse.detail)
            }
            is BookListResponse.BookListObject -> {
                mObject = (mObject as BookListResponse.BookListObject).toSearchObject()
                return initViews()
            }
            else -> this.finish()
        }


    }


}