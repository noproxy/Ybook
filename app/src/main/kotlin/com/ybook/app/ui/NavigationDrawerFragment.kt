package com.ybook.app.ui


import android.app.Activity
import android.app.ActionBar
import android.support.v4.app.Fragment
import android.support.v4.app.ActionBarDrawerToggle
import android.support.v4.view.GravityCompat
import android.support.v4.widget.DrawerLayout
import android.content.res.Configuration
import android.os.Bundle
import android.preference.PreferenceManager
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ListView
import android.widget.Toast
import com.ybook.app.R
import android.widget.BaseAdapter
import android.widget.TextView
import android.widget.ImageView

/**
 * Fragment used for managing interactions for and presentation of a navigation drawer.
 * See the <a href="https://developer.android.com/design/patterns/navigation-drawer.html#Interaction">
 * design guidelines</a> for a complete explanation of the behaviors implemented here.
 */
public class NavigationDrawerFragment : Fragment() {

    /**
     * A pointer to the current callbacks instance (the Activity).
     */
    private var mCallbacks: NavigationDrawerCallbacks? = null

    /**
     * Helper component that ties the action bar to the navigation drawer.
     */
    private var mDrawerToggle: ActionBarDrawerToggle? = null

    private var mDrawerLayout: DrawerLayout? = null
    private var mDrawerListView: ListView? = null
    private var mFragmentContainerView: View? = null

    private var mCurrentSelectedPosition = 0
    private var mFromSavedInstanceState: Boolean = false
    private var mUserLearnedDrawer: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Read in the flag indicating whether or not the user has demonstrated awareness of the
        // drawer. See PREF_USER_LEARNED_DRAWER for details.
        val sp = PreferenceManager.getDefaultSharedPreferences(getActivity())
        mUserLearnedDrawer = sp.getBoolean(PREF_USER_LEARNED_DRAWER, false)

        if (savedInstanceState != null) {
            mCurrentSelectedPosition = savedInstanceState.getInt(STATE_SELECTED_POSITION)
            mFromSavedInstanceState = true
        }

        // Select either the default item (0) or the last selected item.
        selectItem(mCurrentSelectedPosition)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        // Indicate that this fragment would like to influence the set of actions in the action bar.
        setHasOptionsMenu(true)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        mDrawerListView = inflater.inflate(R.layout.fragment_navigation_drawer, container, false) as ListView
        mDrawerListView!!.setOnItemClickListener(object : AdapterView.OnItemClickListener {
            override fun onItemClick(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                selectItem(position)
            }
        })
        mDrawerListView!!.setAdapter(this.NavigationAdapter())
        mDrawerListView!!.setItemChecked(mCurrentSelectedPosition, true)
        return mDrawerListView
    }

    public fun isDrawerOpen(): Boolean {
        return mDrawerLayout != null && mDrawerLayout!!.isDrawerOpen(mFragmentContainerView)
    }

    var mOnDrawerOpenListener: OnDrawerListener? = null

    trait OnDrawerListener : DrawerLayout.DrawerListener

    public fun setOnDrawerListener(listener: OnDrawerListener) {
        mOnDrawerOpenListener = listener
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

        // set a custom shadow that overlays the main content when the drawer opens
        mDrawerLayout!!.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START)
        // set up the drawer's list view with items and click listener

        val actionBar = getActionBar()
        actionBar.setDisplayHomeAsUpEnabled(true)
        actionBar.setHomeButtonEnabled(true)

        // ActionBarDrawerToggle ties together the the proper interactions
        // between the navigation drawer and the action bar app icon.
        mDrawerToggle = object : ActionBarDrawerToggle(getActivity(), mDrawerLayout, R.drawable.ic_launcher, R.string.navigation_drawer_open, R.string.navigation_drawer_close) {
            override fun onDrawerClosed(drawerView: View?) {
                super.onDrawerClosed(drawerView)
                if (!isAdded()) return
                mOnDrawerOpenListener?.onDrawerClosed(drawerView)
                getActivity().invalidateOptionsMenu() // calls onPrepareOptionsMenu()
            }

            override fun onDrawerOpened(drawerView: View?) {
                super.onDrawerOpened(drawerView)
                if (!isAdded()) return
                mOnDrawerOpenListener?.onDrawerOpened(drawerView)
                if (!mUserLearnedDrawer) {
                    // The user manually opened the drawer; store this flag to prevent auto-showing
                    // the navigation drawer automatically in the future.
                    mUserLearnedDrawer = true
                    val sp = PreferenceManager.getDefaultSharedPreferences(getActivity())
                    sp.edit().putBoolean(PREF_USER_LEARNED_DRAWER, true).apply()
                }
                getActivity().invalidateOptionsMenu() // calls onPrepareOptionsMenu()
            }

            override fun onDrawerSlide(drawerView: View?, slideOffset: Float) {
                super<ActionBarDrawerToggle>.onDrawerSlide(drawerView, slideOffset)
                mOnDrawerOpenListener?.onDrawerSlide(drawerView, slideOffset)
            }

            override fun onDrawerStateChanged(newState: Int) {
                super<ActionBarDrawerToggle>.onDrawerStateChanged(newState)
                mOnDrawerOpenListener?.onDrawerStateChanged(newState)
            }
        }

        // If the user hasn't 'learned' about the drawer, open it to introduce them to the drawer,
        // per the navigation drawer design guidelines.
        if (!mUserLearnedDrawer && !mFromSavedInstanceState) {
            //            mDrawerLayout!!.openDrawer(mFragmentContainerView)
        }

        // Defer code dependent on restoration of previous instance state.
        mDrawerLayout!! post { mDrawerToggle!!.syncState() }
        mDrawerLayout!!.setDrawerListener(mDrawerToggle)
    }

    private fun selectItem(position: Int) {
        mCurrentSelectedPosition = position
        if (mDrawerListView != null) {
            mDrawerListView!!.setItemChecked(position, true)
        }
        if (mDrawerLayout != null) {
            mDrawerLayout!!.closeDrawer(mFragmentContainerView)
        }
        if (mCallbacks != null) {
            mCallbacks!!.onNavigationDrawerItemSelected(position)
        }
    }

    override fun onAttach(activity: Activity?) {
        super.onAttach(activity)
        try {
            mCallbacks = activity as NavigationDrawerCallbacks
        } catch (e: ClassCastException) {
            throw ClassCastException("Activity must implement NavigationDrawerCallbacks.")
        }

    }

    override fun onDetach() {
        super.onDetach()
        mCallbacks = null
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt(STATE_SELECTED_POSITION, mCurrentSelectedPosition)
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

    /**
     * Callbacks interface that all activities using this fragment must implement.
     */
    public trait NavigationDrawerCallbacks {
        /**
         * Called when an item in the navigation drawer is selected.
         */
        public fun onNavigationDrawerItemSelected(position: Int)
    }

    inner class NavigationAdapter : BaseAdapter() {
        val nameStrings = array(R.string.navigationHome, R.string.navigationAbout, R.string.navigationFeedback)
        val iconResIds = array(R.drawable.icon_home, R.drawable.icon_info, R.drawable.icon_setting)

        override fun getCount(): Int = 3

        override fun getItem(position: Int): Any? = nameStrings[position]

        override fun getItemId(position: Int): Long = position.toLong()

        override fun getView(position: Int, convertView: View?, parent: ViewGroup): View? {
            val v = convertView ?: LayoutInflater.from(getActivity()).inflate(R.layout.navigation_drawer_item, parent, false)
            (v.findViewById(R.id.menu_text) as TextView).setText(nameStrings[position])
            (v.findViewById(R.id.menu_icon) as ImageView).setImageResource(iconResIds[position])
            return v
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
