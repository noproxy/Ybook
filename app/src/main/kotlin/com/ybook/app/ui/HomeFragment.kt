package com.ybook.app.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import android.os.Bundle
import android.view.View
import android.widget.BaseAdapter
import android.view.KeyEvent
import android.widget.EditText
import android.content.Intent
import android.app.SearchManager
import android.support.v4.app.ListFragment
import com.ybook.app.R
import android.app.Activity
import com.umeng.analytics.MobclickAgent
import com.ybook.app.util.SEARCH_EVENT_ID
import java.util.HashMap
import android.widget.AutoCompleteTextView
import android.content.Context
import java.util.ArrayList
import android.widget.ArrayAdapter

/**
 * Created by carlos on 11/13/14.
 */

public class HomeFragment() : ListFragment() {
    var searchView: AutoCompleteTextView? = null

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, s: Bundle?): View? {
        setListAdapter(HomeListAdapter)
        val v = inflater?.inflate(R.layout.fragment_home, container, false)
        searchView = v?.findViewById(R.id.search_edit_text) as AutoCompleteTextView
        searchView?.setOnKeyListener(onSearchKeyListener)
        return v
    }

    fun getSearchHistory(): Array<String?> {
        val sp = getActivity()?.getSharedPreferences(SEARCH_HISTORY, Context.MODE_PRIVATE)
        val count = sp?.getInt("count", 0) ?: 0
        val arrayList = ArrayList<String>()
        for (i in 1..count) {
            val s = sp?.getString(i.toString(), null)
            if (s != null) arrayList.add(s)
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
        super<ListFragment>.onResume()
        searchView?.setAdapter(object : ArrayAdapter<String>(getActivity(), R.layout.search_suggest_item, getSearchHistory()) {})
    }

    val onSearchKeyListener = {(v: View?, keyCode: Int, keyEvent: KeyEvent) ->
        when (v) {
            is EditText -> if (keyCode == KeyEvent.KEYCODE_ENTER && keyEvent.getAction() == KeyEvent.ACTION_UP) {
                val intent = Intent(getActivity(), javaClass<SearchActivity>())
                val keyWord = v.getText().toString().trim()
                intent.putExtra(SearchManager.QUERY, keyWord)
                v.clearFocus()
                saveSearchHistory(keyWord)
                val map = HashMap<String, String>()
                map.put("searchKey", keyWord)
                map.put("time", System.currentTimeMillis().toString())
                MobclickAgent.onEventValue(getActivity(), SEARCH_EVENT_ID, map, 0)//TODO the last code
                v.setText(null)
                startActivity(intent)

            }
        }
        false
    }

    override fun onAttach(activity: Activity?) {
        super.onAttach(activity)
        (activity as MainActivity).onSectionAttached(0)
    }

    object HomeListAdapter : BaseAdapter() {
        override fun getCount(): Int = 0

        override fun getItem(position: Int): Any? = null

        override fun getItemId(position: Int) = position.toLong()

        override fun getView(position: Int, convertView: View?, parent: ViewGroup): View? {
            return null
        }

    }
}
