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
import android.app.LoaderManager
import com.ybook.app.bean.DetailResponse
import android.content.Loader
import android.content.AsyncTaskLoader
import com.ybook.app.net.DetailRequest
import android.os.Handler
import java.util.ArrayList
import org.apache.http.NameValuePair
import org.apache.http.message.BasicNameValuePair
import com.ybook.app.net.getMainUrl
import org.apache.http.HttpStatus
import com.ybook.app.net.MSG_SUCCESS
import com.ybook.app.util.JSONHelper
import org.apache.http.util.EntityUtils
import com.ybook.app.net.MSG_ERROR
import org.apache.http.impl.client.DefaultHttpClient
import com.ybook.app.net.PostHelper
import com.ybook.app.bean
import android.widget.Toast
import me.toxz.kotlin.makeTag
import android.util.Log

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
 * This is the up to date DetailActivity.
 * It must to handle the process to get detail from server.This implement use {@link http://developer.android.com/guide/components/loaders.html}.
 * If the collection status of book has been changed, you must notify the previous Activity to reflect.
 *
 * Created on 2015/1/11.
 */
public class DetailActivity() : SlidingUpBaseActivity<ObservableScrollView>(), ObservableScrollViewCallbacks, LoaderManager.LoaderCallbacks<DetailResponse> {
    val TAG = makeTag()

    override fun onCreateLoader(id: Int, args: Bundle?): Loader<DetailResponse>? {
        val searchO = mObject
        if (searchO is SearchResponse.SearchObject ) {
            Log.i(TAG, "detail loader for search object created. ")
            return DetailLoader(DetailRequest(searchO.id, searchO.idType, bean.getLibCode()))
        }
        return null
    }

    /**
     * @param req describe what detail to load.
     * @see  {@link com.ybook.app.bean.DetailResponse}
     */
    private inner class DetailLoader(val req: DetailRequest) : AsyncTaskLoader<DetailResponse>(this) {
        override fun loadInBackground(): DetailResponse? {
            Log.i(TAG, "start loadInBackground.")
            val data = ArrayList<NameValuePair>()
            data.add(BasicNameValuePair("id", req.id))
            data.add(BasicNameValuePair("id_type", req.idType))
            data.add(BasicNameValuePair("lib_code", req.libCode.toString()))

            val rep = DefaultHttpClient().execute(PostHelper.newPost(getMainUrl() + "/detail", data))
            when (rep.getStatusLine().getStatusCode()) {
                HttpStatus.SC_OK -> {
                    return JSONHelper.readDetailResponse(EntityUtils.toString(rep.getEntity()))
                }
                else -> return null
            }
        }
    }

    override fun onLoadFinished(loader: Loader<DetailResponse>?, data: DetailResponse?) {
        Log.i(TAG, "onLoadFinished")
        if (loader == null) return
        if (data == null) return showLoadError()
        Log.i(TAG, "get detail.")
        mObject = data.toBookItem()
        refreshUi()
    }

    /**
     * to fresh the UI, often after the book info and status has changed. You can call it many times.
     */
    private fun refreshUi() {
        //to ensure refresh both the text info and collection status
        val container = (id (R.id.main_content)as LinearLayout) after { it.removeAllViews() }
        fun ViewGroup.addItem(text: String? = null, textMain: String? = null, resID: Int = R.layout.detail_item) = DetailItemViewHolder(getLayoutInflater().inflate(resID, container, true), text, textMain)
        val o = mObject
        Log.i(TAG, o.toString())
        when (o) {
            is SearchResponse.SearchObject -> {
                container.addItem(getString(R.string.authorText), o.author)
                container.addItem(getString(R.string.publisherText), o.press)
                container.addItem(getString(R.string.queryIdText))
                container.addItem(getString(R.string.isbnText))
                container.addItem(getString(R.string.detailText), o.detail)
                getLoaderManager().initLoader(0, null, this)
            }
            is BookItem -> {
                container.addItem(getString(R.string.authorText), o.detailResponse.author)
                container.addItem(getString(R.string.publisherText), o.detailResponse.publish)
                container.addItem(getString(R.string.queryIdText), o.detailResponse.queryID)
                container.addItem(getString(R.string.isbnText), o.detailResponse.isbn)
                container.addItem(getString(R.string.detailText), o.detailResponse.detail)
            }
            is BookListResponse.BookListObject -> {
                mObject = (mObject as BookListResponse.BookListObject).toSearchObject()
                return initViews()
            }
            else -> this.finish()
        }
    }

    override fun onLoaderReset(loader: Loader<DetailResponse>?) {
        //not implement: no data need release
    }

    /**
     * to notify user failing in loading.
     */
    public fun showLoadError() {
        Toast.makeText(this, getString(R.string.errorMessage), Toast.LENGTH_LONG).show()
    }

    class object {
        /**
         * the key to put/get SearchObject from Intent.
         * only used when this DetailActivity is launched from a SearchActivity.
         */
        public val INTENT_SEARCH_OBJECT: String = "searchObject"
        /**
         * this value is used to respond to startActivityForResults().
         * this means there isn't any changes to the book.
         */
        public val RESULT_CODE_UNCHANGED: Int = Activity.RESULT_FIRST_USER
        /**
         * this value is used to respond to startActivityForResults().
         * this means the collection status of the book has changed.
         */
        public val RESULT_CODE_CHANGED: Int = Activity.RESULT_FIRST_USER + 1
    }

    var mMarkFAB: FloatingActionButton? = null

    private var mObject: Any? = null
    /**
     * The util to read/change the collection status of the book.It can also used to add the book to some list( but not implement yet).
     */
    private var mUtil = BooksListUtil.getInstance(this)
    //http://ftp.lib.hust.edu.cn/record=b2673698~S0*chx

    /**
     * to be called by parent class, this will return the content layout.So this subclass need not to setContentView() in onCreate().
     */
    protected override fun getLayoutResId(): Int {
        return R.layout.activity_detail
    }

    //implement the SlidingUp effect. See the super class get more info.
    protected override fun createScrollable(): ObservableScrollView {
        val scrollView = findViewById(R.id.scroll) as ObservableScrollView
        scrollView.setScrollViewCallbacks(this)
        return scrollView
    }

    override fun onCreate(savedInstanceState: Bundle?, persistentState: PersistableBundle?) {
        super<SlidingUpBaseActivity>.onCreate(savedInstanceState, persistentState)// setContextView is called by super.
        //Don't call setContentView(), it will be called in super class. Change getLayoutResId() will affect the content view layout.
        throw NullPointerException("2333")
        setResult(RESULT_CODE_UNCHANGED, getIntent())//to report whether the status of book is changed
        mObject = getIntent() getSerializableExtra INTENT_SEARCH_OBJECT ?: getIntent().getSerializableExtra(home.KEY_BOOK_LIST_RESPONSE_EXTRA)
        initViews()
    }

    /**
     * holder to contain the text info.It's a horizontal block on the ui.
     */
    private inner class DetailItemViewHolder(view: View, text: String?, textMain: String?) {
        val textView = ( (view id R.id.item_title) as TextView).after {
            if (text.isNotEmpty()) it setText text
        }

        val textViewMain = (view id R.id.item_text) as TextView after {
            it setText (textMain ?: getString(R.string.loadingContentHint))
        }
    }

    //init the ui at first time create.it should only be called once.
    private fun initViews() {
        Log.i(TAG, "initViews")
        showLoadError()
        refreshUi()
    }


}