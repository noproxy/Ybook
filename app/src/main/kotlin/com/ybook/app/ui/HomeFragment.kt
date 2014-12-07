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

/**
 * Created by carlos on 11/13/14.
 */

public class HomeFragment() : ListFragment() {

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, s: Bundle?): View? {
        setListAdapter(HomeListAdapter)
        val v = inflater?.inflate(R.layout.fragment_home, container, false)
        v?.findViewById(R.id.search_edit_text)?.setOnKeyListener(onSearchKeyListener)
        return v
    }

    val onSearchKeyListener = {(v: View?, keyCode: Int, keyEvent: KeyEvent) ->
        when (v) {
            is EditText -> if (keyCode == KeyEvent.KEYCODE_ENTER && keyEvent.getAction() == KeyEvent.ACTION_UP) {
                val intent = Intent(getActivity(), javaClass<SearchActivity>())
                val keyWord = v.getText().toString().trim()
                intent.putExtra(SearchManager.QUERY, keyWord)
                v.clearFocus()
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
