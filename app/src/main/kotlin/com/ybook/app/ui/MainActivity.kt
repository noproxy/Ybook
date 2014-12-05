package com.ybook.app.ui

import android.app.Activity
import android.os.Bundle
import android.support.v4.widget.DrawerLayout
import android.view.Menu
import android.view.MenuItem
import com.ybook.app.R
import android.app.ActionBar
import android.content.Intent
import com.unique.libraryquery.ui.FeedBackAct
import com.unique.libraryquery.ui.HomeFrag
import com.ybook.app.AboutFragment

public class MainActivity : Activity(), com.ybook.app.ui.NavigationDrawerFragment.NavigationDrawerCallbacks {

    /**
     * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
     */
    private var mNavigationDrawerFragment: com.ybook.app.ui.NavigationDrawerFragment? = null

    private var mCollectionDrawerFragment: CollectionDrawerFragment? = null
    /**
     * Used to store the last screen title. For use in {@link #restoreActionBar()}.
     */
    private var mTitle: CharSequence? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super<Activity>.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mNavigationDrawerFragment = getFragmentManager().findFragmentById(R.id.navigation_drawer) as com.ybook.app.ui.NavigationDrawerFragment
        mNavigationDrawerFragment!!.setUp(R.id.navigation_drawer, findViewById(R.id.drawer_layout) as DrawerLayout)
        mTitle = getTitle()

        mCollectionDrawerFragment = getFragmentManager().findFragmentById(R.id.collection_drawer) as CollectionDrawerFragment
        mCollectionDrawerFragment!!.setUp(R.id.collection_drawer, findViewById(R.id.drawer_layout) as DrawerLayout)
    }

    override fun onNavigationDrawerItemSelected(position: Int) {
        getFragmentManager().beginTransaction().replace(R.id.container, HomeFrag()).commit()
        getFragmentManager().beginTransaction().replace(R.id.container, AboutFragment()).commit()
        startActivity(Intent(this, javaClass<FeedBackAct>()))

    }


    public fun onSectionAttached(number: Int) {
        when (number) {
            1 -> mTitle = getString(R.string.navigationHome)
            2 -> mTitle = getString(R.string.navigationAbout)
        }
    }

    public fun restoreActionBar() {
        val actionBar = getActionBar()
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD)
        actionBar.setDisplayShowTitleEnabled(true)
        actionBar.setTitle(mTitle)
    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        if (!mNavigationDrawerFragment!!.isDrawerOpen()) {
            // Only show items in the action bar relevant to this screen
            // if the drawer is not showing. Otherwise, let the drawer
            // decide what to show in the action bar.
            getMenuInflater().inflate(R.menu.main, menu)
            restoreActionBar()
            return true
        }
        return super<Activity>.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        val id = item!!.getItemId()

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true
        }

        return super<Activity>.onOptionsItemSelected(item)
    }

    //    /**
    //     * A placeholder fragment containing a simple view.
    //     */
    //    public class PlaceholderFragment : Fragment() {
    //
    //        override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
    //            val rootView = inflater.inflate(R.layout.fragment_main, container, false)
    //            return rootView
    //        }
    //
    //        override fun onAttach(activity: Activity?) {
    //            super.onAttach(activity)
    //            (activity as MainActivity).onSectionAttached(getArguments().getInt(ARG_SECTION_NUMBER))
    //        }
    //
    //        class object {
    //            /**
    //             * The fragment argument representing the section number for this
    //             * fragment.
    //             */
    //            private val ARG_SECTION_NUMBER = "section_number"
    //
    //            /**
    //             * Returns a new instance of this fragment for the given section
    //             * number.
    //             */
    //            public fun newInstance(sectionNumber: Int): PlaceholderFragment {
    //                val fragment = PlaceholderFragment()
    //                val args = Bundle()
    //                args.putInt(ARG_SECTION_NUMBER, sectionNumber)
    //                fragment.setArguments(args)
    //                return fragment
    //            }
    //        }
    //    }

}
