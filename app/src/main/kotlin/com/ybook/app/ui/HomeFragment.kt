package com.ybook.app.ui

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
import com.ybook.app.util.SEARCH_EVENT_ID
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

/**
 * Created by carlos on 11/13/14.
 */

public val KEY_BOOK_LIST_RESPONSE_EXTRA: String = "bookList"

public class HomeFragment() : Fragment(), OnClickListener {
    var searchView: AutoCompleteTextView? = null
    val imageViewIdRes = array(R.id.bookListCoverImage0, R.id.bookListCoverImage1, R.id.bookListCoverImage2, R.id.bookListCoverImage3)
    val textViewIdRes = array(R.id.bookListTitle0, R.id.bookListTitle1, R.id.bookListTitle2, R.id.bookListTitle3)
    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, s: Bundle?) = initViews(inflater?.inflate(R.layout.fragment_home, container, false)!!)
    override fun onClick(v: View) {
        when (v.getTag() ) {
            is BookListResponse -> startActivity(Intent(v.getContext(), javaClass<BookListActivity>()).putExtra(KEY_BOOK_LIST_RESPONSE_EXTRA, v.getTag() as BookListResponse))
        }
    }

    fun initViews(mainView: View): View {
        val listView = (mainView id android.R.id.list) as ExpandableListView
        listView setAdapter HomeExpandableListAdapter()
        listView setChildDivider getResources().getDrawable(android.R.color.transparent)
        listView setGroupIndicator null
        searchView = (mainView id R.id.search_edit_text) as AutoCompleteTextView
        searchView!! setOnKeyListener onSearchKeyListener
        PostHelper getBookList object : Handler() {
            var count: Int = 0
            override fun handleMessage(msg: Message) {
                when (msg.what) {
                    MSG_SUCCESS -> {
                        Log.i("HomeFragmet", "getBookList msg success")
                        if (count > 3) return
                        val bookListResponse = msg.obj as BookListResponse
                        val imageView = (mainView id imageViewIdRes[count]) as ImageView
                        val textView = (mainView id textViewIdRes[count]) as TextView
                        Log.i("HomeFragment", "coverImg: " + bookListResponse.coverImgUrl)
                        Picasso.with(mainView.getContext()) load(bookListResponse.coverImgUrl) error(R.drawable.ic_error) into imageView
                        textView setText bookListResponse.title
                        imageView setTag bookListResponse
                        textView setTag bookListResponse
                        imageView setOnClickListener this@HomeFragment
                        textView setOnClickListener this@HomeFragment
                        count++
                    }
                //TODO solve data
                    MSG_ERROR -> Log.i("HomeFragmet", "getBookList msg error")
                }
            }
        }
        return mainView
    }

    fun getSearchHistory(): Array<String?> {
        val sp = getActivity()?.getSharedPreferences(SEARCH_HISTORY, Context.MODE_PRIVATE)
        val count = sp?.getInt("count", 0) ?: 0
        val arrayList = ArrayList<String>()
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
        super<Fragment>.onAttach(activity)
        (activity as MainActivity).onSectionAttached(0)
    }

    inner class HomeExpandableListAdapter : BaseExpandableListAdapter() {
        val string = array(
                "一层 综合阅览室、借还书处、办证处\n二层 中文自科（TP TQ TS TU TV U V X Z）\n三层 中文自科（N O P T TB TD TE TF TG TH TJ TK TL TM TN）\n四层 中文社科（G H I J K）\n五层 外文图书、工具书、中文社科（A B C D E F）\n六层 外文图书阅览室",
                "一层 旧书报刊密集库\n二层 中文自科（TP TU） 中文自科（N O P TB TD TE TF TG TH TJ TK TL TM TS TV U V X Z）\n三层 中文社科（A B C D E） 中文社科（F G H）\n四层 中外文过刊阅览室\n五层 中文社科（I K）",
                "一层 F H J Q R S\n二层 I\n三层 A B C D E G K TN TQ TS TU TV\n四层 TP U V X Z\n五层 N O P T TB TD TE TF TG TH TJ TK TL TM TU",
                "二层北 中文社科（二） H K G\n二层南 中文社科（一） A B C D F E\n三层南 自然科学（二） T.TB-TV(TP除外) U V X Z\n四层南 自然科学（一） N O P Q R S\n四层北 中文计算机图书 TP"
        )
        val titles = array("主校区图书馆图书借阅室（流动书库）",
                "主校区图书馆逸夫馆图书阅览室（C区）",
                "东校区图书馆流动书库",
                "东校区图书馆阅览室"
        )

        override fun getGroupCount(): Int = 4
        override fun getChildrenCount(groupPosition: Int): Int = 1
        override fun getGroup(groupPosition: Int): Any? = titles[groupPosition]
        override fun getChild(groupPosition: Int, childPosition: Int): Any? = string[groupPosition]
        override fun getGroupId(groupPosition: Int): Long = groupPosition.toLong()
        override fun getChildId(groupPosition: Int, childPosition: Int): Long = groupPosition.toLong()
        override fun hasStableIds(): Boolean = true
        override fun getGroupView(groupPosition: Int, isExpanded: Boolean, convertView: View?, parent: ViewGroup?): View? {
            val v = convertView ?: getActivity().getLayoutInflater().inflate(R.layout.home_head_item, parent, false)
            (v id R.id.headItemTextView) as TextView setText titles[groupPosition]
            (v id R.id.homeHeadArrow )as ImageView setImageResource (if (isExpanded) R.drawable.ic_arrow_drop_up else R.drawable.ic_arrow_drop_down)
            return v
        }

        override fun getChildView(groupPosition: Int, childPosition: Int, isLastChild: Boolean, convertView: View?, parent: ViewGroup?): View? {
            val v = convertView ?: getActivity().getLayoutInflater().inflate(R.layout.home_item, parent, false)
            (v id R.id.homeListItemTextView )as TextView setText string[groupPosition]
            return v
        }

        override fun isChildSelectable(groupPosition: Int, childPosition: Int): Boolean = false

    }
}
