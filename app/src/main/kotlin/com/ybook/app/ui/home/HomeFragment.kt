package com.ybook.app.ui.home

import android.view.LayoutInflater
import android.os.Bundle
import android.view.KeyEvent
import android.widget.EditText
import android.content.Intent
import android.app.SearchManager
import com.ybook.app.R
import android.app.Activity
import com.umeng.analytics.MobclickAgent
import com.ybook.app.util.EVENT_SEARCH
import java.util.HashMap
import android.widget.AutoCompleteTextView
import android.content.Context
import java.util.ArrayList
import android.widget.ArrayAdapter
import com.ybook.app.net.PostHelper
import android.os.Handler
import android.os.Message
import com.ybook.app.net.MSG_SUCCESS
import android.util.Log
import com.ybook.app.net.MSG_ERROR
import com.squareup.picasso.Picasso
import android.widget.ImageView
import com.ybook.app.bean.BookListResponse
import android.widget.TextView
import android.support.v4.app.Fragment
import android.widget.ExpandableListView
import android.widget.BaseExpandableListAdapter
import com.ybook.app.id
import com.ybook.app.util.ListViewUtil
import com.ybook.app.util.EVENT_ADD_FROM_DETAIL
import com.ybook.app.util.EVENT_OPEN_RECOMMEND_LIST
import com.ybook.app.ui.search.SearchActivity
import android.widget.ScrollView
import android.widget.LinearLayout
import android.support.v7.widget.CardView
import com.ybook.app.ui.MainActivity
import android.view.ViewTreeObserver.OnScrollChangedListener
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import android.view.ViewGroup
import android.os.AsyncTask
import java.net.URL
import com.ybook.app.net.getMainUrl
import me.toxz.kotlin.makeTag
import org.apache.http.impl.client.DefaultHttpClient
import org.apache.http.client.methods.HttpGet
import org.apache.http.HttpStatus
import com.ybook.app.util.JSONHelper
import org.apache.http.util.EntityUtils
import org.apache.http.protocol.HTTP
import android.support.v4.app.LoaderManager
import android.support.v4.content.Loader
import android.content.AsyncTaskLoader

/**
 * Created by carlos on 11/13/14.
 */

public val KEY_BOOK_LIST_RESPONSE_EXTRA: String = "bookList"

public class HomeFragment() : Fragment(), View.OnClickListener, OnScrollChangedListener, LoaderManager.LoaderCallbacks<BookListResponse> {
    override fun onCreateLoader(id: Int, args: Bundle?): Loader<BookListResponse>? {
        throw UnsupportedOperationException()
    }

    override fun onLoadFinished(loader: Loader<BookListResponse>?, data: BookListResponse?) {
        throw UnsupportedOperationException()
    }

    override fun onLoaderReset(loader: Loader<BookListResponse>?) {
        throw UnsupportedOperationException()
    }

    val TAG: String = makeTag()
    var mBookListLoadTasks: Array<AsyncTask<URL, Void, BookListResponse>>? = null

    override fun onCreateView(inflater: android.view.LayoutInflater?, container: ViewGroup?, s: android.os.Bundle?) = initViews(inflater?.inflate(R.layout.fragment_home, container, false)!!)

    override fun onClick(v: View) {
        val t = v.getTag()
        when (t) {
            is BookListResponse -> {
                com.umeng.analytics.MobclickAgent.onEvent(getActivity(), com.ybook.app.util.EVENT_OPEN_RECOMMEND_LIST)
                startActivity(android.content.Intent(v.getContext(), javaClass<com.ybook.app.ui.NewBookListActivity>()).putExtra(KEY_BOOK_LIST_RESPONSE_EXTRA, v.getTag() as com.ybook.app.bean.BookListResponse))
            }
            is View -> {
                if (t.getVisibility() == View.VISIBLE) {
                    t setVisibility View.GONE
                    (v id R.id.homeHeadArrow) as ImageView setImageResource R.drawable.ic_arrow_drop_down
                } else {
                    t setVisibility View.VISIBLE
                    (v id R.id.homeHeadArrow) as ImageView setImageResource R.drawable.ic_arrow_drop_up
                    mScrollView?.post { mScrollView!!.smoothScrollTo(0, t.getBottom()) }
                }
            }
        }
    }

    var mScrollView: android.widget.ScrollView? = null

    fun initViews(mainView: View): View {
        mScrollView = mainView as android.widget.ScrollView

        val mRecyclerView = mainView.id(R.id.recyclerView) as RecyclerView

        mRecyclerView.setHasFixedSize(true)
        val mLayoutManager = LinearLayoutManager(mRecyclerView.getContext(), LinearLayoutManager.HORIZONTAL, false)
        mRecyclerView.setLayoutManager(mLayoutManager)
        mRecyclerView setAdapter BookListRecyclerViewAdapter()


        array(
                R.id.mapHeadA to R.id.mapItemA,
                R.id.mapHeadB to R.id.mapItemB,
                R.id.mapHeadC to R.id.mapItemC,
                R.id.mapHeadD to R.id.mapItemD
        ).forEach { pair ->
            (mainView id pair.first).let {
                it.setTag(mainView id pair.second)
                it.setOnClickListener(this)
            }
        }


        //TODO no animation        if (mActivity is OnFragmentScrollChangedListener) {
        //            Log.i("HomeFragment", "listener" + mScrollView)
        //            mScrollView?.getViewTreeObserver()?.addOnScrollChangedListener(this)
        //        }

        return mainView
    }


    fun getSearchHistory(): Array<String?> {
        val sp = getActivity()?.getSharedPreferences(SEARCH_HISTORY, Context.MODE_PRIVATE)
        val count = sp?.getInt("count", 0) ?: 0
        val arrayList = java.util.ArrayList<String>()
        for (i in 1..count) {
            val s = sp?.getString(i.toString(), null)
            if (s != null) arrayList add s
        }
        return arrayList.copyToArray()
    }

    val SEARCH_HISTORY = "searchHistory"

    fun saveSearchHistory(key: String) {
        val sp = getActivity()?.getSharedPreferences(SEARCH_HISTORY, Context.MODE_PRIVATE)
        val all = sp?.getAll()
        if (!(all?.containsValue(key) ?: true)) {
            val count = sp?.getInt("count", 0)
            sp?.edit()?.putString((count!!.toInt() + 1).toString(), key)?.putInt("count", count + 1)?.commit()
        }
    }

    override fun onResume() {
        super<Fragment>.onResume()
        //        searchView?.setAdapter(object : ArrayAdapter<String>(getActivity(), R.layout.search_suggest_item, getSearchHistory()) {})
    }


    //    val onSearchKeyListener = {(v: View?, keyCode: Int, keyEvent: KeyEvent) ->
    //        when (v) {
    //            is EditText -> if (keyCode == KeyEvent.KEYCODE_ENTER && keyEvent.getAction() == KeyEvent.ACTION_UP) {
    //                val intent = Intent(getActivity(), javaClass<SearchActivity>())
    //                val keyWord = v.getText().toString().trim()
    //                intent.putExtra(SearchManager.QUERY, keyWord)
    //                v.clearFocus()
    //                saveSearchHistory(keyWord)
    //                val map = HashMap<String, String>()
    //                map.put("searchKey", keyWord)
    //                map.put("time", System.currentTimeMillis().toString())
    //                MobclickAgent.onEventValue(getActivity(), EVENT_SEARCH, map, 0)
    //                v.setText(null)
    //                startActivity(intent)
    //            }
    //        }
    //        false
    //    }
    var mActivity: Activity? = null

    override fun onAttach(activity: android.app.Activity?) {
        super<Fragment>.onAttach(activity)
        (activity as MainActivity).onSectionAttached(0)
        mActivity = activity
    }

    val BookListLoaderID = 0

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super<Fragment>.onActivityCreated(savedInstanceState)
        getLoaderManager().initLoader(BookListLoaderID, null, this)
    }

    override fun onScrollChanged() {
        Log.i("HomeFragment", "onScrollChanged:${mScrollView!!.getScrollY()}")
        (mActivity as? OnFragmentScrollChangedListener )?.onScrollChanged(mScrollView!!.getScrollY())
    }

    override fun onDetach() {
        super<Fragment>.onDetach()
        mScrollView?.getViewTreeObserver()?.removeOnScrollChangedListener(this)
        mActivity = null
    }


    trait OnFragmentScrollChangedListener {
        fun onScrollChanged(y: Int)
    }

    private class BookListCardHolder(val cardView: View) : RecyclerView.ViewHolder(cardView) {
        val titleText = ( cardView id R.id.cardText )as TextView
        val coverImage = ( cardView id R.id.cardImage )as ImageView
    }

    private inner class BookListRecyclerViewAdapter() : RecyclerView.Adapter<BookListCardHolder>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookListCardHolder? {
            val card = LayoutInflater.from(parent.getContext()).inflate(R.layout.book_card, parent, false) as CardView
            return BookListCardHolder(card)
        }

        override fun onBindViewHolder(holder: BookListCardHolder, position: Int) {
            holder.coverImage.setImageResource(R.drawable.ic_empty)
            holder.titleText.setText(holder.cardView.getContext().getResources().getString(R.string.suggestedBook))


            //TODO async task to load net or read cache
        }

        override fun getItemCount(): Int = 4

    }

    private inner class MyBookListLoadTask() : AsyncTask<Int, Void, BookListResponse>() {
        override fun doInBackground(vararg params: Int?): BookListResponse? {
            val num = params.get(0)
            val url = getMainUrl() + "/static/temp/bookrec0" + num.toString() + ".json"
            Log.i(TAG, "url: " + url)
            try {
                val rep = DefaultHttpClient().execute(HttpGet(url))
                if (rep.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                    return JSONHelper.readBookListResponse(EntityUtils.toString(rep.getEntity(), HTTP.UTF_8))
                }
            } catch(e: Exception) {
                e.printStackTrace()
            }
            return null
        }

        SuppressWarnings("UnusedDeclaration")
        override fun onPostExecute(result: BookListResponse?) {
            super.onPostExecute(result)
            if (result == null || result.receiver == null) return
            result.receiver!!.receive(result)
        }
    }

    private inner class BookListsLoader(con: Context) : AsyncTaskLoader<List<BookListResponse>>(con) {
        override fun loadInBackground(): List<BookListResponse>? {
            val result = ArrayList<BookListResponse>(4)
            for (num in 1..4) {
                val url = getMainUrl() + "/static/temp/bookrec0" + num.toString() + ".json"
                Log.i(TAG, "url: " + url)
                try {
                    val rep = DefaultHttpClient().execute(HttpGet(url))
                    if (rep.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                        result add JSONHelper.readBookListResponse(EntityUtils.toString(rep.getEntity(), HTTP.UTF_8), num)
                    }
                } catch(e: Exception) {
                    e.printStackTrace()
                }
            }
            return result
        }
    }

    private inner class SingleBookListLoader(con: Context, val listId: Int) : AsyncTaskLoader<BookListResponse>(con) {

        override fun loadInBackground(): BookListResponse? {
            val url = getMainUrl() + "/static/temp/bookrec0" + listId.toString() + ".json"
            Log.i(TAG, "url: " + url)
            try {
                val rep = DefaultHttpClient().execute(HttpGet(url))
                if (rep.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                    return JSONHelper.readBookListResponse(EntityUtils.toString(rep.getEntity(), HTTP.UTF_8), listId)
                }
            } catch(e: Exception) {
                e.printStackTrace()
            }
            return null
        }
    }

}
