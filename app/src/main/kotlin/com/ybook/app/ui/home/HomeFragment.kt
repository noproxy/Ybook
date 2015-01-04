package com.ybook.app.ui.home

import android.view.LayoutInflater
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
import com.ybook.app.ui.main.MainActivity
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
import android.support.v4.content.AsyncTaskLoader
import me.toxz.kotlin.after
import android.os.Bundle
import com.ybook.app.ui.list.NewBookListActivity

/**
 * Created by carlos on 11/13/14.
 */
//TODO show one BookList at once it loaded
public val KEY_BOOK_LIST_RESPONSE_EXTRA: String = "bookList"

public class HomeFragment() : Fragment(), View.OnClickListener, OnScrollChangedListener, LoaderManager.LoaderCallbacks<List<BookListResponse>> {

    val BUNDLE_KEY_POSITION = "position"
    val mListData = HashMap<Int, BookListResponse>(4)
    var mAdapter: BookListRecyclerViewAdapter? = null

    override fun onCreateLoader(id: Int, args: Bundle?): Loader<List<BookListResponse>>? {
        Log.d(TAG, "load is created: " + id)
        return when ( id ) {
            BOOK_LIST_LOADER_ID -> BookListsLoader(mActivity!!)
            SINGLE_BOOK_LIST_LOADER_ID -> SingleBookListLoader(mActivity!!, args!!.getInt(BUNDLE_KEY_POSITION))
            else -> null
        }
    }

    override fun onLoadFinished(loader: Loader<List<BookListResponse>>?, data: List<BookListResponse>?) {
        Log.i(TAG, "onLoadFinished,data: " + data + ", adapter: " + mAdapter)
        data?.forEach { mListData.put(it.id, it);Log.d(TAG, it.toString()) }
        mAdapter?.notifyDataSetChanged()
    }

    override fun onLoaderReset(loader: Loader<List<BookListResponse>>?) {
        mListData.clear()
    }

    val TAG: String = makeTag()
    var mBookListLoadTasks: Array<AsyncTask<URL, Void, BookListResponse>>? = null

    override fun onCreateView(inflater: android.view.LayoutInflater?, container: ViewGroup?, s: android.os.Bundle?) = initViews(inflater?.inflate(R.layout.fragment_home, container, false)!!)

    override fun onClick(v: View) {
        val t = v.getTag()
        when (t) {
            is BookListResponse -> {
                MobclickAgent.onEvent(getActivity(), EVENT_OPEN_RECOMMEND_LIST)
                startActivity(Intent(mActivity, javaClass<NewBookListActivity>()).putExtra(KEY_BOOK_LIST_RESPONSE_EXTRA, t))
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
        mRecyclerView setAdapter BookListRecyclerViewAdapter().after { mAdapter = it }


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

    var mActivity: Activity? = null

    override fun onAttach(activity: Activity?) {
        super<Fragment>.onAttach(activity)
        (activity as MainActivity).onSectionAttached(0)
        mActivity = activity
    }

    val BOOK_LIST_LOADER_ID = 0
    val SINGLE_BOOK_LIST_LOADER_ID = 1

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super<Fragment>.onActivityCreated(savedInstanceState)
        Log.i(TAG, "activity created")
        getLoaderManager().initLoader(BOOK_LIST_LOADER_ID, Bundle().after { it.putInt(BUNDLE_KEY_POSITION, 0) }, this)
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
            val card = (LayoutInflater.from(parent.getContext()).inflate(R.layout.book_card, parent, false) as CardView ).after {
                it setOnClickListener this@HomeFragment
            }
            return BookListCardHolder(card)
        }

        override fun onBindViewHolder(holder: BookListCardHolder, position: Int) {
            holder.coverImage.setImageResource(R.drawable.ic_empty)
            holder.titleText.setText("")
            holder.cardView setTag null
            holder.coverImage setTag null
            holder.titleText setTag null
            if (mListData.size() > position) {
                mListData.get(position + 1)?.into(holder)
            }
        }

        private fun BookListResponse.into(holder: BookListCardHolder) {
            Picasso.with(holder.cardView.getContext()).load(this.coverImgUrl).into(holder.coverImage)
            holder.titleText.setText(this.title)
            holder.cardView setTag this
            holder.coverImage setTag this
            holder.titleText setTag this
        }

        override fun getItemCount(): Int = 4

    }

    private class BookListsLoader(con: Context) : AsyncTaskLoader<List<BookListResponse>>(con) {
        {
            onContentChanged()
        }
        val TAG = makeTag()
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

        override fun onStartLoading() {
            if (takeContentChanged())
                forceLoad();
        }

        override fun onStopLoading() {
            cancelLoad();
        }
    }

    private class SingleBookListLoader(con: Context, val listId: Int) : AsyncTaskLoader<List<BookListResponse>>(con) {
        {
            onContentChanged()
        }
        val TAG = makeTag()
        override fun loadInBackground(): List<BookListResponse>? {
            val url = getMainUrl() + "/static/temp/bookrec0" + listId.toString() + ".json"
            Log.i(TAG, "url: " + url)
            try {
                val rep = DefaultHttpClient().execute(HttpGet(url))
                if (rep.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                    return arrayListOf(JSONHelper.readBookListResponse(EntityUtils.toString(rep.getEntity(), HTTP.UTF_8), listId))
                }
            } catch(e: Exception) {
                e.printStackTrace()
            }
            return null
        }

        override fun onStartLoading() {
            if (takeContentChanged())
                forceLoad();
        }

        override fun onStopLoading() {
            cancelLoad();
        }
    }

}
