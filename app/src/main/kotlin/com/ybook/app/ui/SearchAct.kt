package com.ybook.app.ui

import android.app.ListActivity
import android.os.Bundle
import android.app.SearchManager
import android.os.Handler
import android.os.Message
import android.widget.BaseAdapter
import de.keyboardsurfer.android.widget.crouton.Crouton
import de.keyboardsurfer.android.widget.crouton.Style
import java.util.ArrayList
import android.view.View
import android.view.ViewGroup
import android.view.LayoutInflater
import android.widget.TextView
import android.content.Context
import android.widget.ImageView
import com.squareup.picasso.Picasso
import android.view.View.OnClickListener
import com.ybook.app.R
import com.ybook.app.bean.SearchResponse.SearchObject
import com.ybook.app.net.PostHelper
import com.ybook.app.net.SearchRequest
import com.ybook.app.bean.getLibCode
import com.ybook.app.net.MSG_SUCCESS
import com.ybook.app.bean.SearchResponse
import com.ybook.app.net.MSG_ERROR
import com.ybook.app.net.MSG_PASSWORD_WRONG

/**
 * Created by carlos on 11/14/14.
 */

public class SearchAct : ListActivity() {
    var nextPage = 0
    val SEARCH_BY_KEY = "key"
    val TAG = "SearchAct"

    val loadError = { Crouton.makeText(this, getResources().getString(R.string.load_search_error), Style.ALERT).show() }
    val passError = { Crouton.makeText(this, getResources().getString(R.string.password_error), Style.ALERT).show() }
    val loading = {(page: Int) -> Crouton.makeText(this, getResources().getString(R.string.loading_search_message_prefix) + page, Style.INFO).show() }

    val listItems = ArrayList<SearchObject>()
    var key: String? = null

    val mUtil = BooksListUtil.getInstance(this)


    override fun onCreate(savedInstanceState: Bundle?) {
        super<ListActivity>.onCreate(savedInstanceState)

        setContentView(R.layout.activity_search_result)


        key = getIntent().getStringExtra(SearchManager.QUERY)
        if (key == null) {
            this.finish()
        }

        val actionBar = getActionBar()
        if (actionBar != null) {
            actionBar.setTitle(key)
            actionBar.setDisplayShowTitleEnabled(true)
        }

        setListAdapter(SearchListAdapter(this))
    }


    override fun onResume() {
        super<ListActivity>.onResume()
        if (nextPage <= 0) load()
    }

    private fun load() {
        PostHelper.search(SearchRequest(key!!, nextPage, SEARCH_BY_KEY, getLibCode()), SearchHandler())
    }

    inner class SearchHandler : Handler() {
        override fun handleMessage(msg: Message) {
            when (msg.what) {
                MSG_SUCCESS -> {
                    val r = msg.obj as SearchResponse
                    listItems.addAll(r.objects)
                    nextPage++;
                    (getListView().getAdapter() as BaseAdapter).notifyDataSetChanged()
                }
                MSG_ERROR -> loadError()
                MSG_PASSWORD_WRONG -> passError()
            }
        }
    }

    inner class SearchListAdapter(val con: Context) : BaseAdapter() {
        override fun getItem(position: Int): Any? = listItems[position]
        override fun getCount(): Int = listItems.size
        override fun getItemId(position: Int) = position.toLong()
        override fun getView(position: Int, convertView: View?, parent: ViewGroup): View? {
            val v = convertView ?: LayoutInflater.from(con).inflate(R.layout.search_result_item, parent, false)
            val item = getItem(position) as SearchObject
            val bookItem = item.toBookItem()

            v.setTag(item)
            v.findViewById(R.id.image_view_book_isMarked).setTag(item)
            (v.findViewById(R.id.text_view_book_title) as TextView).setText(item.title)
            (v.findViewById(R.id.text_view_book_query_id) as TextView).setText(item.id)
            (v.findViewById(R.id.text_view_book_author) as TextView).setText(item.author)
            (v.findViewById(R.id.text_view_book_publisher) as TextView).setText(item.press)
            val coverImage = v.findViewById(R.id.image_view_book_cover) as ImageView
            Picasso.with(con).load(item.coverImgUrl).error(getResources().getDrawable(R.drawable.ic_error)).into(coverImage)

            //TODO * to compatible with the old bean
            val collectionBtn = v.findViewById(R.id.image_view_book_isMarked) as ImageView
            collectionBtn.setImageResource(if (bookItem.isMarked(mUtil)) R.drawable.ic_marked else R.drawable.ic_mark)
            collectionBtn.setOnClickListener(object : OnClickListener {
                override fun onClick(v: View): Unit = when (v.getId() ) {
                    R.id.image_view_book_isMarked -> {
                        val bookItem = (v.getTag() as SearchObject).toBookItem()
                        bookItem.markOrCancelMarked(mUtil)
                        (v as ImageView).setImageResource(if (bookItem.isMarked(mUtil)) R.drawable.ic_marked else R.drawable.ic_mark)
                        Crouton.makeText(this@SearchAct, getResources().getString(if (bookItem.isMarked(mUtil)) R.string.toast_mark else R.string.toast_cancel_mark), Style.INFO).show()
                    }
                }
            })
            return v
        }

    }
}