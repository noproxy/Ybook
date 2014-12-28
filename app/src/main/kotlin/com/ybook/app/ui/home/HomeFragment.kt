package com.ybook.app.ui.home

import android.view.LayoutInflater
import android.view.ViewGroup
import android.os.Bundle
import android.view.View
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
import android.view.View.OnClickListener
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

/**
 * Created by carlos on 11/13/14.
 */

public val KEY_BOOK_LIST_RESPONSE_EXTRA: String = "bookList"

public class HomeFragment() : Fragment(), View.OnClickListener {

    override fun onCreateView(inflater: android.view.LayoutInflater?, container: android.view.ViewGroup?, s: android.os.Bundle?) = initViews(inflater?.inflate(R.layout.fragment_home, container, false)!!)

    override fun onClick(v: android.view.View) {
        val t = v.getTag()
        when (t) {
            is BookListResponse -> {
                com.umeng.analytics.MobclickAgent.onEvent(getActivity(), com.ybook.app.util.EVENT_OPEN_RECOMMEND_LIST)
                startActivity(android.content.Intent(v.getContext(), javaClass<com.ybook.app.ui.NewBookListActivity>()).putExtra(KEY_BOOK_LIST_RESPONSE_EXTRA, v.getTag() as com.ybook.app.bean.BookListResponse))
            }
            is View -> {
                if (t.getVisibility() == android.view.View.VISIBLE) {
                    t setVisibility android.view.View.GONE
                    (v id R.id.homeHeadArrow) as ImageView setImageResource R.drawable.ic_arrow_drop_down
                } else {
                    t setVisibility android.view.View.VISIBLE
                    (v id R.id.homeHeadArrow) as ImageView setImageResource R.drawable.ic_arrow_drop_up
                    mScrollView?.post { mScrollView!!.smoothScrollTo(0, t.getBottom()) }
                }
            }
        }
    }

    var mScrollView: android.widget.ScrollView? = null

    fun initViews(mainView: android.view.View): android.view.View {
        mScrollView = mainView as android.widget.ScrollView
        ((mainView id R.id.bookListCardContainer) as android.widget.LinearLayout).let { it.removeAllViews();BookListCardProducer.addListCardsInto(it) }

        array(
                Pair(R.id.mapHeadA, R.id.mapItemA),
                Pair(R.id.mapHeadB, R.id.mapItemB),
                Pair(R.id.mapHeadC, R.id.mapItemC),
                Pair(R.id.mapHeadD, R.id.mapItemD)).
                forEach { pair -> (mainView id pair.first).let { it.setTag(mainView id pair.second);it.setOnClickListener(this) } }

        return mainView
    }


    fun getSearchHistory(): Array<String?> {
        val sp = getActivity()?.getSharedPreferences(SEARCH_HISTORY, android.content.Context.MODE_PRIVATE)
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
        val sp = getActivity()?.getSharedPreferences(SEARCH_HISTORY, android.content.Context.MODE_PRIVATE)
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

    override fun onAttach(activity: android.app.Activity?) {
        super<Fragment>.onAttach(activity)
        (activity as MainActivity).onSectionAttached(0)
    }
}
