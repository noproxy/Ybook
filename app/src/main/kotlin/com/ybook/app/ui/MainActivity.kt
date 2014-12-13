package com.ybook.app.ui

import android.os.Bundle
import android.support.v4.widget.DrawerLayout
import android.view.Menu
import android.view.MenuItem
import com.ybook.app.R
import android.app.ActionBar
import android.content.Intent
import android.support.v4.app.FragmentActivity
import com.umeng.analytics.MobclickAgent

import com.ybook.app.id

val ARG_SECTION_NUMBER: String = "section_number"

public class MainActivity : FragmentActivity(), com.ybook.app.ui.NavigationDrawerFragment.NavigationDrawerCallbacks {

    /**
     * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
     */
    private var mNavigationDrawerFragment: NavigationDrawerFragment? = null

    private var mCollectionDrawerFragment: CollectionDrawerFragment? = null
    /**
     * Used to store the last screen title. For use in {@link #restoreActionBar()}.
     */
    private var mTitle: CharSequence? = null

    override fun onResume() {
        super<FragmentActivity>.onResume()
        MobclickAgent.onResume(this);
    }

    override fun onPause() {
        super<FragmentActivity>.onPause()
        MobclickAgent.onPause(this);
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super<FragmentActivity>.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mNavigationDrawerFragment = (getSupportFragmentManager() findFragmentById R.id.navigation_drawer ) as NavigationDrawerFragment
        mNavigationDrawerFragment!!.setUp(R.id.navigation_drawer, id(R.id.drawer_layout) as DrawerLayout)
        mTitle = getTitle()
        mCollectionDrawerFragment = (getSupportFragmentManager() findFragmentById R.id.collection_drawer) as CollectionDrawerFragment
        mCollectionDrawerFragment!!.setUp(R.id.collection_drawer, id(R.id.drawer_layout) as DrawerLayout)
    }

    override fun onNavigationDrawerItemSelected(position: Int) {
        when (position) {
            0 -> getSupportFragmentManager().beginTransaction().replace(R.id.container, HomeFragment()).commit()
            1 -> getSupportFragmentManager().beginTransaction().replace(R.id.container, com.ybook.app.ui.AboutFragment()).commit()
            2 -> startActivity(Intent(this, javaClass<FeedBackActivity>()))
        }
    }

    public fun onSectionAttached(number: Int) {
        when (number) {
            0 -> mTitle = getString(R.string.navigationHome)
            1 -> mTitle = getString(R.string.navigationAbout)
        }
    }

    public fun restoreActionBar() {
        val actionBar = getActionBar()
        actionBar setNavigationMode ActionBar.NAVIGATION_MODE_STANDARD
        actionBar setDisplayShowTitleEnabled true
        getActionBar() setDisplayHomeAsUpEnabled true
        actionBar setTitle mTitle
    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        if (!mNavigationDrawerFragment!!.isDrawerOpen() && !mCollectionDrawerFragment!!.isDrawerOpen()) {
            // Only show items in the action bar relevant to this screen
            // if the drawer is not showing. Otherwise, let the drawer
            // decide what to show in the action bar.
            getMenuInflater().inflate(R.menu.main, menu)
            restoreActionBar()
            return true
        }
        return super<FragmentActivity>.onCreateOptionsMenu(menu)
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

        return super<FragmentActivity>.onOptionsItemSelected(item)
    }
}
