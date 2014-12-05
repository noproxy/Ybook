package com.ybook.app.ui


import android.app.ActionBar
import android.app.Fragment
import android.content.res.Configuration
import android.os.Bundle
import android.support.v4.app.ActionBarDrawerToggle
import android.support.v4.widget.DrawerLayout
import android.view.*
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.Toast
import com.ybook.app.R

/**
 * Fragment used for managing interactions for and presentation of a navigation drawer.
 * See the <a href="https://developer.android.com/design/patterns/navigation-drawer.html#Interaction">
 * design guidelines</a> for a complete explanation of the behaviors implemented here.
 */
public class CollectionDrawerFragment : Fragment() {


    /**
     * Helper component that ties the action bar to the navigation drawer.
     */
    private var mDrawerToggle: ActionBarDrawerToggle? = null

    private var mDrawerLayout: DrawerLayout? = null
    private var mDrawerListView: ListView? = null
    private var mFragmentContainerView: View? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

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
                Toast.makeText(inflater.getContext(), "onClick", Toast.LENGTH_SHORT).show()
            }
        })
        mDrawerListView!!.setAdapter(ArrayAdapter(getActionBar().getThemedContext(), android.R.layout.simple_list_item_activated_1, android.R.id.text1, array(getString(R.string.title_section1), getString(R.string.title_section2), getString(R.string.title_section3))))
        return mDrawerListView
    }

    public fun isDrawerOpen(): Boolean {
        return mDrawerLayout != null && mDrawerLayout!!.isDrawerOpen(mFragmentContainerView)
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

        if (item!!.getItemId() == R.id.action_example) {
            Toast.makeText(getActivity(), "Example.", Toast.LENGTH_SHORT).show()
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
