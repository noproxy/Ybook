package com.unique.libraryquery.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import android.os.Bundle
import android.view.View
import android.widget.BaseAdapter
import android.view.KeyEvent
import android.widget.EditText
import android.content.Intent
import android.app.SearchManager
import com.ybook.app.R
import android.app.ListFragment

/**
 * Created by carlos on 11/13/14.
 */

public class HomeFrag : ListFragment() {


    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, s: Bundle?): View? {
        setListAdapter(HomeListAdapter)
        val v = inflater?.inflate(R.layout.fragment_home, container, false)
        v?.findViewById(R.id.search_edit_text)?.setOnKeyListener(onSearchKeyListener)
        return v
    }

    val onSearchKeyListener = {(v: View?, keyCode: Int, keyEvent: KeyEvent) ->
        when (v) {
            is EditText -> if (keyCode == KeyEvent.KEYCODE_ENTER && keyEvent.getAction() == KeyEvent.ACTION_UP) {
                val intent = Intent(getActivity(), javaClass<SearchResultActivity>())
                intent.putExtra(SearchManager.QUERY, v.getText().toString().trim())
                v.clearFocus()
            }
        }
        true
    }


    object HomeListAdapter : BaseAdapter() {
        override fun getCount(): Int {
            throw UnsupportedOperationException()
        }

        override fun getItem(position: Int): Any? {
            throw UnsupportedOperationException()
        }

        override fun getItemId(position: Int): Long {
            throw UnsupportedOperationException()
        }

        override fun getView(position: Int, convertView: View?, parent: ViewGroup): View? {
            return null
        }

    }
}
