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
import com.balysv.materialmenu.MaterialMenuIcon
import android.graphics.Color
import com.balysv.materialmenu.MaterialMenuDrawable.Stroke
import android.os.PersistableBundle
import com.balysv.materialmenu.MaterialMenuDrawable.IconState
import com.ybook.app.ui.NavigationDrawerFragment.OnDrawerListener
import android.view.View
import com.balysv.materialmenu.MaterialMenuDrawable.AnimationState
import android.util.Log

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

    var materialMenu: MaterialMenuIcon? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super<FragmentActivity>.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mNavigationDrawerFragment = (getSupportFragmentManager() findFragmentById R.id.navigation_drawer ) as NavigationDrawerFragment
        mNavigationDrawerFragment!!.setUp(R.id.navigation_drawer, id(R.id.drawer_layout) as DrawerLayout)
        mTitle = getTitle()
        mCollectionDrawerFragment = (getSupportFragmentManager() findFragmentById R.id.collection_drawer) as CollectionDrawerFragment
        mCollectionDrawerFragment!!.setUp(R.id.collection_drawer, id(R.id.drawer_layout) as DrawerLayout)

        materialMenu = MaterialMenuIcon(this, Color.WHITE, Stroke.THIN);
        mNavigationDrawerFragment?.setOnDrawerListener(object : OnDrawerListener {
            var isOpened = false

            override fun onDrawerSlide(p0: View?, p1: Float) {
                Log.d("onDrawerSlide", "float: " + p1)
                materialMenu?.setTransformationOffset(if (isOpened) AnimationState.ARROW_CHECK else AnimationState.BURGER_ARROW, p1)
            }

            override fun onDrawerOpened(p0: View?) {
                isOpened = true
            }

            override fun onDrawerClosed(p0: View?) {
                isOpened = false
            }

            override fun onDrawerStateChanged(p0: Int) {
            }

        })
    }

    override fun onPostCreate(savedInstanceState: Bundle?) {
        super<FragmentActivity>.onPostCreate(savedInstanceState)
        materialMenu?.syncState(savedInstanceState);
    }

    override fun onSaveInstanceState(outState: Bundle?) {
        super<FragmentActivity>.onSaveInstanceState(outState)
        materialMenu?.onSaveInstanceState(outState);
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
        //        actionBar setDisplayShowTitleEnabled true
        //        getActionBar() setDisplayUseLogoEnabled false
        getActionBar() setDisplayHomeAsUpEnabled true
        //        getActionBar() setDisplayOptions ActionBar.DISPLAY_SHOW_TITLE
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
        when (item?.getItemId()) {
            android.R.id.home -> if (!mNavigationDrawerFragment!!.isDrawerOpen() && !mCollectionDrawerFragment!!.isDrawerOpen())
                materialMenu?.animatePressedState(IconState.ARROW) else materialMenu?.animatePressedState(IconState.BURGER)
        }
        return super<FragmentActivity>.onOptionsItemSelected(item)
    }
}
