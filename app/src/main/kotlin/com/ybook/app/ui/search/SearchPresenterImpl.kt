package com.ybook.app.ui.search

import android.widget.Adapter
import android.widget.AdapterView
import android.view.View
import com.ybook.app.net.PostHelper
import com.ybook.app.net.SearchRequest
import com.ybook.app.bean
import android.widget.ListView
import android.widget.AdapterView.OnItemClickListener
import com.ybook.app.util.BooksListUtil
import java.util.ArrayList
import com.ybook.app.bean.SearchResponse
import com.umeng.analytics.MobclickAgent
import android.util.Log
import android.os.Handler
import android.os.Message
import com.ybook.app.net
import android.content.Context
import android.widget.BaseAdapter
import android.view.ViewGroup
import android.view.LayoutInflater
import com.ybook.app.R
import com.ybook.app.id
import android.widget.TextView
import android.widget.ImageView
import com.squareup.picasso.Picasso
import com.ybook.app.bean.BookItem
import com.ybook.app
import android.widget.Toast
import android.app.ProgressDialog
import com.ybook.app.net.DetailRequest
import com.ybook.app.bean.DetailResponse
import android.view.MenuItem
import android.os.Bundle
import android.app.SearchManager
import android.content.Intent
import com.ybook.app.ui.BookDetailActivity
import java.io.Serializable
import com.ybook.app.util.ListEndToLoadUtil
import com.ybook.app.ui.search.SearchView.MessageType
import android.support.v7.widget.RecyclerView
import com.ybook.app.bean.SearchResponse.SearchObject
import android.view.animation.Animation
import android.view.animation.AnimationUtils

/**
 * Created by Carlos on 2014/12/17.
 */
public class SearchPresenterImpl(val searchView: SearchView) : SearchPresenter, OnItemClickListener, RecyclerView.OnScrollListener() {
    val SEARCH_BY_KEY = "key"
    val TAG = "SearchAct"
    val mUtil = BooksListUtil.getInstance(searchView)
    val listItems = ArrayList<SearchResponse.SearchObject>()
    var requestedPage = -1
    var nextPage = 0
    var mKey: String? = null

    var previousTotal = 0
    var isLoading = true
    var visibleThreshold = 5
    var firstVisibleItem: Int = 0
    var visibleItemCount: Int = 0
    var totalItemCount: Int = 0


    override fun getAdapter() = mRecyclerAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        mKey = searchView.getIntent().getStringExtra(SearchManager.QUERY)
        searchView.setTitle(mKey)
        searchView.getActionBar() setDisplayShowTitleEnabled true
        searchView.getActionBar() setDisplayHomeAsUpEnabled  true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.getItemId()) {
            android.R.id.home -> searchView.onBackPressed()
        }
        return true
    }

    override fun onResume() {
        MobclickAgent.onResume(searchView);
        if (nextPage == 0) onScrollEnd()
        Log.i(TAG, "resume, nextPage:" + nextPage)
    }

    override fun onPause() {
        MobclickAgent.onPause(searchView);
    }

    override fun onItemClick(parent: AdapterView<out Adapter>, v: View, position: Int, id: Long) {
        searchView.startActivity(Intent(searchView, javaClass<BookDetailActivity>()).putExtra(BookDetailActivity.INTENT_SEARCH_OBJECT, v.getTag() as Serializable))
    }

    override fun onClick(v: View) {
        when (v.getId() ) {
            R.id.bookMarkBtn -> {
                val item = v.getTag() as SearchObject
                if (item.isMarked(mUtil)) {
                    BookItem.cancelMarked(mUtil, item)
                    (v as ImageView).setImageResource(R.drawable.ic_mark)
                    MobclickAgent.onEvent(searchView, app.util.EVENT_DELETE_FROM_SEARCH)
                    Toast.makeText(searchView, searchView getString R.string.toastCancelMark, Toast.LENGTH_SHORT).show()
                } else {
                    MobclickAgent.onEvent(searchView, app.util.EVENT_ADD_FROM_SEARCH)
                    val dialog = ProgressDialog(searchView)
                    dialog.setMessage(searchView getString R.string.loadingMessage)
                    dialog.setIndeterminate(true)
                    dialog.setCancelable(false)
                    dialog.show()
                    PostHelper.detail(DetailRequest(item.id, item.idType, bean.getLibCode()), object : Handler() {
                        override fun handleMessage(msg: Message) {
                            dialog.dismiss()
                            when (msg.what) {
                                net.MSG_SUCCESS -> {
                                    val book = (msg.obj as DetailResponse).toBookItem()
                                    book.markOrCancelMarked(mUtil)
                                    (v as ImageView).setImageResource(R.drawable.ic_marked)
                                    searchView.showMessage(searchView.getString(R.string.toastMarked), MessageType.INFO)
                                }
                                net.MSG_ERROR -> searchView.showMessage(searchView.getString(R.string.collectFailErrorHint) + item.title, MessageType.INFO)
                            }
                        }
                    })
                }
            }
        }
    }

    fun setOnScrollEndListener() {

    }

    override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
        visibleItemCount = recyclerView.getChildCount()
        totalItemCount = searchView.getLayoutManager().getItemCount()
        firstVisibleItem = searchView.getLayoutManager().findFirstVisibleItemPosition()
        if (isLoading) {
            if (totalItemCount > previousTotal) {
                isLoading = false;
                previousTotal = totalItemCount;
            }
        }
        if (!isLoading && (totalItemCount - visibleItemCount) <= (firstVisibleItem + visibleThreshold)) onScrollEnd()
    }

    fun onScrollEnd() {
        if (nextPage > requestedPage && nextPage * 10 == listItems.size()) {
            PostHelper.search(SearchRequest(mKey!!, nextPage, SEARCH_BY_KEY, bean.getLibCode()), mHandler)
            requestedPage++
            isLoading = true
            searchView.showLoadPageMessage(nextPage)
        }
    }

    private val mHandler = object : Handler() {
        override fun handleMessage(msg: Message) {
            when (msg.what) {
                net.MSG_SUCCESS -> {
                    val r = msg.obj as SearchResponse
                    listItems addAll r.objects
                    nextPage++
                    mRecyclerAdapter.notifyDataSetChanged()
                }
                net.MSG_ERROR -> searchView.showLoadErrorMessage()
                else -> searchView.showUnknownMessage()
            }
            isLoading = false;
        }
    }

    private val mRecyclerAdapter = object : RecyclerView.Adapter<SearchViewHolder>() {
        var lastPosition: Int = -1

        override fun onCreateViewHolder(parent: ViewGroup?, p1: Int): SearchViewHolder? {
            return SearchViewHolder(LayoutInflater.from(parent?.getContext()).inflate(R.layout.search_result_item, parent, false))
        }

        override fun onBindViewHolder(holder: SearchViewHolder, position: Int) {
            val item = listItems[position]
            holder.titleText setText item.title
            holder.idText setText item.id
            holder.authorText setText item.author
            holder.pressText setText item.press
            Picasso.with(searchView) load item.coverImgUrl error (searchView.getResources().getDrawable(R.drawable.ic_error)) into holder.coverImage

            holder.view setTag item
            holder.markBtn setTag item
            holder.markBtn setImageResource(if (item isMarked mUtil) R.drawable.ic_marked else R.drawable.ic_mark)
            holder.markBtn setOnClickListener this@SearchPresenterImpl
            setAnimation(holder.view, position)
        }

        override fun getItemCount(): Int = listItems.size


        private fun setAnimation(viewToAnimate: View, position: Int) {
            // If the bound view wasn't previously displayed on screen, it's animated
            if (position > lastPosition) {
                val animation = AnimationUtils.loadAnimation(viewToAnimate.getContext(), android.R.anim.fade_in);
                viewToAnimate.startAnimation(animation);
                lastPosition = position;
            }
        }
    }


    //    private val mAdapter = object : BaseAdapter() {
    //        override fun getItem(position: Int): Any? = listItems[position]
    //        override fun getCount(): Int = listItems.size
    //        override fun getItemId(position: Int) = position.toLong()
    //        override fun getView(position: Int, convertView: View?, parent: ViewGroup): View? {
    //            val v = convertView ?: LayoutInflater.from(searchView).inflate(R.layout.search_result_item, parent, false)
    //            val item = getItem(position) as SearchResponse.SearchObject
    //
    //            v setTag item
    //            v id(R.id.bookMarkBtn) setTag item
    //
    //            (v id R.id.text_view_book_title) as TextView setText item.title
    //            (v id R.id.text_view_book_query_id) as TextView setText item.id
    //            (v id R.id.text_view_book_author) as TextView setText item.author
    //            (v id R.id.text_view_book_publisher) as TextView setText item.press
    //
    //            val coverImage = (v id R.id.image_view_book_cover) as ImageView
    //            Picasso.with(searchView) load item.coverImgUrl error (searchView getDrawable R.drawable.ic_error ) into coverImage
    //            val collectionBtn = (v id R.id.bookMarkBtn) as ImageView
    //            collectionBtn setImageResource(if (item isMarked mUtil) R.drawable.ic_marked else R.drawable.ic_mark)
    //            collectionBtn setOnClickListener { v ->
    //                when (v.getId() ) {
    //                    R.id.bookMarkBtn -> {
    //                        if (item.isMarked(mUtil)) {
    //                            BookItem.cancelMarked(mUtil, item)
    //                            (v as ImageView).setImageResource(R.drawable.ic_mark)
    //                            MobclickAgent.onEvent(searchView, app.util.EVENT_DELETE_FROM_SEARCH)
    //                            Toast.makeText(searchView, searchView getString R.string.toastCancelMark, Toast.LENGTH_SHORT).show()
    //                        } else {
    //                            MobclickAgent.onEvent(searchView, app.util.EVENT_ADD_FROM_SEARCH)
    //                            val dialog = ProgressDialog(searchView)
    //                            dialog.setMessage(searchView getString R.string.loadingMessage)
    //                            dialog.setIndeterminate(true)
    //                            dialog.setCancelable(false)
    //                            dialog.show()
    //                            PostHelper.detail(DetailRequest(item.id, item.idType, bean.getLibCode()), object : Handler() {
    //                                override fun handleMessage(msg: Message) {
    //                                    dialog.dismiss()
    //                                    when (msg.what) {
    //                                        net.MSG_SUCCESS -> {
    //                                            val book = (msg.obj as DetailResponse).toBookItem()
    //                                            book.markOrCancelMarked(mUtil)
    //                                            (v as ImageView).setImageResource(R.drawable.ic_marked)
    //                                            searchView.showMessage(searchView.getString(R.string.toastMarked), MessageType.INFO)
    //                                        }
    //                                        net.MSG_ERROR -> searchView.showMessage(searchView.getString(R.string.collectFailErrorHint) + item.title, MessageType.INFO)
    //                                    }
    //                                }
    //                            })
    //                        }
    //                    }
    //                }
    //            }
    //            return v
    //        }
    //
    //    }
}

public class SearchViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
    val titleText = (view id R.id.text_view_book_title) as TextView
    val idText = (view id R.id.text_view_book_query_id) as TextView
    val authorText = (view id R.id.text_view_book_author) as TextView
    val pressText = (view id R.id.text_view_book_publisher) as TextView
    val markBtn = (view id R.id.bookMarkBtn)  as ImageView
    val coverImage = (view id R.id.image_view_book_cover) as ImageView
}