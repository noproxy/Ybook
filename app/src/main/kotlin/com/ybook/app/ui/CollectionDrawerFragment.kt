package com.ybook.app.ui


import android.app.ActionBar
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

    private var mDrawerLayout: DrawerLayout? = null
    private var mDrawerListView: ListView? = null
    private var mFragmentContainerView: View? = null
    private var mAdapter: MyCollectionListAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        // Indicate that this fragment would like to influence the set of actions in the action bar.
        setHasOptionsMenu(true)
    }

    override fun onResume() {
        mAdapter?.notifyDataSetChanged()
        super.onResume()
    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_collection_drawer, container, false)
        mDrawerListView = view.findViewById(android.R.id.list) as  ListView
        return view
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {

        mAdapter = MyCollectionListAdapter()
        mDrawerListView!!.setAdapter(mAdapter)
        mDrawerListView!!.setOnItemClickListener(object : AdapterView.OnItemClickListener {
            override fun onItemClick(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                Log.i("onItemClick()", position.toString())
                val intent = Intent(getActivity(), javaClass<BookDetailActivity>())
                intent.putExtra(BookDetailActivity.INTENT_SEARCH_OBJECT, view.getTag() as java.io.Serializable)
                startActivity(intent)
            }
        })
    }

    public fun isDrawerOpen(): Boolean {
        return mDrawerLayout?.isDrawerOpen(mFragmentContainerView) ?: false
    }

    /**
     * Users of this fragment must call this method to set up the navigation drawer interactions.
     *
     * @param fragmentId   The android:id of this fragment in its activity's layout.
     * @param drawerLayout The DrawerLayout containing this fragment's UI.
     */
    public fun setUp(fragmentId: Int, drawerLayout: DrawerLayout) {
        mFragmentContainerView = getActivity().findViewById(fragmentId)
        mDrawerLayout = drawerLayout

        // ActionBarDrawerToggle ties together the the proper interactions
        // between the navigation drawer and the action bar app icon.
        mDrawerToggle = object : ActionBarDrawerToggle(getActivity(), /* host Activity */
                mDrawerLayout, /* DrawerLayout object */
                R.drawable.ic_drawer, /* nav drawer image to replace 'Up' caret */
                R.string.navigation_drawer_open, /* "open drawer" description for accessibility */
                R.string.navigation_drawer_close  /* "close drawer" description for accessibility */) {
            override fun onDrawerClosed(drawerView: View?) {
                super.onDrawerClosed(drawerView)
                if (!isAdded()) {
                    return
                }

                getActivity().invalidateOptionsMenu() // calls onPrepareOptionsMenu()
            }

            override fun onDrawerOpened(drawerView: View?) {
                super.onDrawerOpened(drawerView)
                if (!isAdded()) {
                    return
                }

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


    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        // Forward the new configuration the drawer toggle component.
        mDrawerToggle!!.onConfigurationChanged(newConfig)
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        // If the drawer is open, show the global app actions in the action bar. See also
        // showGlobalContextActionBar, which controls the top-left area of the action bar.
        if (mDrawerLayout != null && isDrawerOpen()) {
            inflater!!.inflate(R.menu.global, menu)
            showGlobalContextActionBar()
        }
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        if (mDrawerToggle!!.onOptionsItemSelected(item)) {
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    /**
     * Per the navigation drawer design guidelines, updates the action bar to show the global app
     * 'context', rather than just what's in the current screen.
     */
    private fun showGlobalContextActionBar() {
        val actionBar = getActionBar()
        actionBar.setDisplayShowTitleEnabled(true)
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD)
        actionBar.setTitle(R.string.app_name)
    }

    private fun getActionBar(): ActionBar {
        return getActivity().getActionBar()
    }

    public inner class MyCollectionListAdapter : SectionedBaseAdapter() {
        private var mUtil: BooksListUtil? = null
        {
            mUtil = BooksListUtil.getInstance(getActivity())
        }
        private val mEmptyLayout = EmptyLayout(getActivity(), getListView())


        override public fun notifyDataSetChanged() {
            mEmptyLayout.showLoading()
            Log.i("MyCollectionListAdapter", "notifyDataSetChanged()")
            super.notifyDataSetChanged()
            if (MarkedList.getMarkedList().getBookItems(mUtil).size() == 0) {
                mEmptyLayout.setEmptyViewRes(R.layout.empty_layout)
                mEmptyLayout.setEmptyMessage("No books collected yet.", R.id.emptyMessageTextView)
                mEmptyLayout.showEmpty()
            }
        }


        private fun getItems(): ArrayList<ArrayList<BookItem>> {
            return MarkedList.getMarkedList().getSeparatedItems(mUtil)
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

        override public fun getItemView(section: Int, position: Int, convertView: View?, parent: ViewGroup): View {
            val view = convertView ?: LayoutInflater.from(getActivity()).inflate(R.layout.collection_item, parent, false)
            val item = (getItem(section, position) as BookItem)
            view.setTag(item)
            (view.findViewById(R.id.text_view_book_title) as TextView).setText(item.detailResponse.title)
            (view.findViewById(R.id.text_view_book_query_id) as TextView).setText(item.detailResponse.queryID)
            view.findViewById(R.id.image_btn_book_available).setVisibility(View.INVISIBLE)
            return view
        }

        override public fun getSectionHeaderView(section: Int, convertView: View?, parent: ViewGroup): View {
            val view = convertView ?: LayoutInflater.from(getActivity()).inflate(R.layout.collection_head_item, parent, false)
            val item = (getItem(section, 0) as BookItem)
            val head = MarkedList.getQueryHead(item.detailResponse.queryID)
            val type = MarkedList.getType(head)
            view.setTag(item)
            (view.findViewById(R.id.textView_head_type) as TextView).setText(type)
            //TODO set the icon
            (view.findViewById(R.id.imageView_head_icon) as ImageView).setImageResource(MarkedList.getIconID(head))
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
