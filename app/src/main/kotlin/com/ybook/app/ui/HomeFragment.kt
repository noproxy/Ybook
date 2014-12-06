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
import com.ybook.app.ui.SearchActivity
import android.app.Activity
import com.ybook.app.ui.MainActivity

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
                intent.putExtra(SearchManager.QUERY, v.getText().toString().trim())
                v.clearFocus()
                startActivity(intent)
            }
        }
        true
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
