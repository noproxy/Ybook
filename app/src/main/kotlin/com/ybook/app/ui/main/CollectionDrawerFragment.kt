package com.ybook.app.ui.main


import android.content.res.Configuration
import android.os.Bundle
import android.support.v4.app.ActionBarDrawerToggle
import android.support.v4.widget.DrawerLayout
import android.view.*
import android.widget.ListView
import com.ybook.app.R
import android.util.Log
import android.widget.TextView
import android.widget.ImageView
import com.ybook.app.util.BooksListUtil
import com.ybook.app.pinnedheaderlistview.SectionedBaseAdapter
import com.ybook.app.bean.MarkedList
import java.util.ArrayList
import com.ybook.app.bean.BookItem
import com.ybook.app.EmptyLayout
import android.support.v4.app.ListFragment
import android.widget.AdapterView
import android.content.Intent
import com.ybook.app.id
import android.widget.Toast
import com.umeng.analytics.MobclickAgent
import com.ybook.app.util.EVENT_OPEN_COLLECTION
import android.support.v7.app.ActionBarActivity
import android.support.v7.app.ActionBar
import com.ybook.app.ui.detail.BookDetailActivity

/**
 * Fragment used for managing interactions for and presentation of a navigation drawer.
 * See the <a href="https://developer.android.com/design/patterns/navigation-drawer.html#Interaction">
 * design guidelines</a> for a complete explanation of the behaviors implemented here.
 */
public class CollectionDrawerFragment : ListFragment() {


    /**
     * Helper component that ties the action bar to the navigation drawer.
     */
    private var mDrawerToggle: ActionBarDrawerToggle? = null

    private var mDrawerLayout: android.support.v4.widget.DrawerLayout? = null
    private var mDrawerListView: android.widget.ListView? = null
    private var mFragmentContainerView: android.view.View? = null
    private var mAdapter: MyCollectionListAdapter? = null

    override fun onActivityCreated(savedInstanceState: android.os.Bundle?) {
        super.onActivityCreated(savedInstanceState)
        // Indicate that this fragment would like to influence the set of actions in the action bar.
        setHasOptionsMenu(true)
    }

    override fun onResume() {
        mAdapter?.notifyDataSetChanged()
        super.onResume()
    }


    override fun onCreateView(inflater: android.view.LayoutInflater, container: android.view.ViewGroup?, savedInstanceState: android.os.Bundle?): android.view.View? {
        val view = inflater.inflate(com.ybook.app.R.layout.fragment_collection_drawer, container, false)
        mDrawerListView = view.findViewById(android.R.id.list) as  android.widget.ListView
        mNoHintView = view id com.ybook.app.R.id.nothingHint
        return view
    }

    override fun onViewCreated(view: android.view.View?, savedInstanceState: android.os.Bundle?) {

        mAdapter = MyCollectionListAdapter()
        mDrawerListView!!.setAdapter(mAdapter)
        mDrawerListView!!.setOnItemClickListener(object : android.widget.AdapterView.OnItemClickListener {
            override fun onItemClick(parent: android.widget.AdapterView<*>, view: android.view.View, position: Int, id: Long) {
                android.util.Log.i("onItemClick()", position.toString())
                val intent = android.content.Intent(getActivity(), javaClass<com.ybook.app.ui.detail.BookDetailActivity>())
                intent.putExtra(BookDetailActivity.INTENT_SEARCH_OBJECT, view.getTag() as java.io.Serializable)
                startActivity(intent)
            }
        })
    }

    var mNoHintView: android.view.View? = null
    fun showEmpty() = mNoHintView?.setVisibility(android.view.View.VISIBLE)
    fun removeEmpty() = mNoHintView?.setVisibility(android.view.View.GONE)

    public fun isDrawerOpen(): Boolean {
        return mDrawerLayout?.isDrawerOpen(mFragmentContainerView) ?: false
    }

    /**
     * Users of this fragment must call this method to set up the navigation drawer interactions.
     *
     * @param fragmentId   The android:id of this fragment in its activity's layout.
     * @param drawerLayout The DrawerLayout containing this fragment's UI.
     */
    public fun setUp(fragmentId: Int, drawerLayout: android.support.v4.widget.DrawerLayout) {
        mFragmentContainerView = getActivity().findViewById(fragmentId)
        mDrawerLayout = drawerLayout

        // ActionBarDrawerToggle ties together the the proper interactions
        // between the navigation drawer and the action bar app icon.
        mDrawerToggle = object : ActionBarDrawerToggle(getActivity(), /* host Activity */
                mDrawerLayout, /* DrawerLayout object */
                com.ybook.app.R.drawable.ic_drawer, /* nav drawer image to replace 'Up' caret */
                com.ybook.app.R.string.navigation_drawer_open, /* "open drawer" description for accessibility */
                com.ybook.app.R.string.navigation_drawer_close  /* "close drawer" description for accessibility */) {
            override fun onDrawerClosed(drawerView: android.view.View?) {
                super.onDrawerClosed(drawerView)
                if (!isAdded()) {
                    return
                }

                getActivity().invalidateOptionsMenu() // calls onPrepareOptionsMenu()
            }

            override fun onDrawerOpened(drawerView: android.view.View?) {
                super.onDrawerOpened(drawerView)
                if (!isAdded()) {
                    return
                }
                com.umeng.analytics.MobclickAgent.onEvent(getActivity(), com.ybook.app.util.EVENT_OPEN_COLLECTION)
                getActivity().invalidateOptionsMenu() // calls onPrepareOptionsMenu()
            }
        }

        // Defer code dependent on restoration of previous instance state.
        mDrawerLayout!!.post(object : Runnable {
            override fun run() {
                mDrawerToggle!!.syncState()
            }
        })

        mDrawerLayout!!.setDrawerListener(mDrawerToggle)
    }


    override fun onConfigurationChanged(newConfig: android.content.res.Configuration) {
        super.onConfigurationChanged(newConfig)
        // Forward the new configuration the drawer toggle component.
        mDrawerToggle!!.onConfigurationChanged(newConfig)
    }

    override fun onCreateOptionsMenu(menu: android.view.Menu?, inflater: android.view.MenuInflater?) {
        // If the drawer is open, show the global app actions in the action bar. See also
        // showGlobalContextActionBar, which controls the top-left area of the action bar.
        if (mDrawerLayout != null && isDrawerOpen()) {
            //            inflater!!.inflate(R.menu.global, menu)
            showGlobalContextActionBar()
        }
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: android.view.MenuItem?): Boolean {
        if (mDrawerToggle!!.onOptionsItemSelected(item)) {
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    public fun close() {
        mDrawerLayout?.closeDrawers()
    }

    /**
     * Per the navigation drawer design guidelines, updates the action bar to show the global app
     * 'context', rather than just what's in the current screen.
     */
    private fun showGlobalContextActionBar() {
        val actionBar = getActionBar()
        actionBar.setDisplayShowTitleEnabled(true)
        actionBar.setNavigationMode(android.support.v7.app.ActionBar.NAVIGATION_MODE_STANDARD)
        actionBar.setTitle(com.ybook.app.R.string.app_name)
    }

    private fun getActionBar(): android.support.v7.app.ActionBar {
        return (getActivity() as android.support.v7.app.ActionBarActivity).getSupportActionBar()
    }

    public inner class MyCollectionListAdapter : com.ybook.app.pinnedheaderlistview.SectionedBaseAdapter() {
        private var mUtil: com.ybook.app.util.BooksListUtil? = null
        {
            mUtil = com.ybook.app.util.BooksListUtil.getInstance(getActivity())
        }
        //        private val mEmptyLayout = EmptyLayout(getActivity(), getListView())


        override public fun notifyDataSetChanged() {
            //            mEmptyLayout.showLoading()
            android.util.Log.i("MyCollectionListAdapter", "notifyDataSetChanged()")
            super.notifyDataSetChanged()
            if (com.ybook.app.bean.MarkedList.getMarkedList().getBookItems(mUtil).size() == 0) {
                //                mEmptyLayout.setEmptyViewRes(R.layout.empty_layout)
                //                mEmptyLayout.setEmptyMessage("No books collected yet.", R.id.emptyMessageTextView)
                //                mEmptyLayout.showEmpty()
                //                Toast.makeText(getActivity(), "empty", Toast.LENGTH_SHORT).show()
                showEmpty()
            } else removeEmpty()
        }


        private fun getItems(): java.util.ArrayList<java.util.ArrayList<com.ybook.app.bean.BookItem>> {
            return com.ybook.app.bean.MarkedList.getMarkedList().getSeparatedItems(mUtil)
        }

        override public fun getItem(section: Int, position: Int): Any {
            return getItems().get(section).get(position)
        }

        override public fun getItemId(section: Int, position: Int): Long {
            return position.toLong()
        }

        override public fun getSectionCount(): Int {
            return getItems().size()
        }

        override public fun getCountForSection(section: Int): Int {
            return getItems().get(section).size()
        }

        override public fun getItemView(section: Int, position: Int, convertView: android.view.View?, parent: android.view.ViewGroup): android.view.View {
            val view = convertView ?: android.view.LayoutInflater.from(getActivity()).inflate(com.ybook.app.R.layout.collection_item, parent, false)
            val item = (getItem(section, position) as com.ybook.app.bean.BookItem)
            view.setTag(item)
            (view.findViewById(com.ybook.app.R.id.text_view_book_title) as android.widget.TextView).setText(item.detailResponse.title)
            (view.findViewById(com.ybook.app.R.id.text_view_book_query_id) as android.widget.TextView).setText(item.detailResponse.queryID)
            if (item.detailResponse.queryID.trim().size <= 0) view id com.ybook.app.R.id.text_view_book_query_id setVisibility android.view.View.GONE
            else view id com.ybook.app.R.id.text_view_book_query_id setVisibility android.view.View.VISIBLE
            view.findViewById(com.ybook.app.R.id.image_btn_book_available).setVisibility(android.view.View.GONE)
            return view
        }

        override public fun getSectionHeaderView(section: Int, convertView: android.view.View?, parent: android.view.ViewGroup): android.view.View {
            val view = convertView ?: android.view.LayoutInflater.from(getActivity()).inflate(com.ybook.app.R.layout.collection_head_item, parent, false)
            val item = (getItem(section, 0) as com.ybook.app.bean.BookItem)
            val head = com.ybook.app.bean.MarkedList.getQueryHead(item.detailResponse.queryID)
            val type = com.ybook.app.bean.MarkedList.getType(head)
            view.setTag(item)
            (view.findViewById(com.ybook.app.R.id.textView_head_type) as android.widget.TextView).setText(type)
            (view.findViewById(com.ybook.app.R.id.imageView_head_icon) as android.widget.ImageView).setImageResource(com.ybook.app.bean.MarkedList.getIconID(head))
            return view
        }
    }


    class object {

        /**
         * Remember the position of the selected item.
         */
        private val STATE_SELECTED_POSITION = "selected_navigation_drawer_position"

        /**
         * Per the design guidelines, you should show the drawer on launch until the user manually
         * expands it. This shared preference tracks this.
         */
        private val PREF_USER_LEARNED_DRAWER = "navigation_drawer_learned"
    }
}
