package com.ybook.app.ui.main

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
import com.ybook.app.ui.main.NavigationDrawerFragment.OnDrawerListener
import android.view.View
import com.balysv.materialmenu.MaterialMenuDrawable.AnimationState
import android.util.Log
import android.support.v7.widget.SearchView
import android.app.SearchManager
import android.content.Context
import com.ybook.app.ui.main.NavigationDrawerFragment.NavigationDrawerCallbacks
import android.view.ViewTreeObserver
import com.ybook.app.ui.home.HomeFragment
import android.support.v7.widget.Toolbar
import android.content.ComponentName
import com.ybook.app.ui.search.SearchActivity
import android.support.v7.widget.SearchView.OnQueryTextListener
import com.ybook.app.ui.others.FeedBackActivity
import com.ybook.app.ui.others.AboutFragment
import com.ybook.app.ui.home.HomeFragment.OnFragmentScrollChangedListener
import android.support.v7.app.ActionBarActivity

val ARG_SECTION_NUMBER: String = "section_number"

public class MainActivity : ActionBarActivity(), NavigationDrawerCallbacks, OnFragmentScrollChangedListener {

    override fun onScrollChanged(y: Int) {
        android.util.Log.i("MainActivity", "onScroll, scrollY:${y}")
        val actionBar = getSupportActionBar()
        if (y >= actionBar.getHeight() && actionBar.isShowing()) {
            actionBar.hide();
        } else if ( y == 0 && !actionBar.isShowing()) {
            actionBar.show();
        }
    }

    override fun onBackPressed() {
        if (mNavDrawerFragment?.isDrawerOpen() ?: false) {
            mNavDrawerFragment!!.close()
        } else if (mColDrawerFragment?.isDrawerOpen() ?: false) {
            mColDrawerFragment!!.close()
        } else super<ActionBarActivity>.onBackPressed()

    }


    /**
     * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
     */
    private var mNavDrawerFragment: NavigationDrawerFragment? = null

    private var mColDrawerFragment: CollectionDrawerFragment? = null
    /**
     * Used to store the last screen title. For use in {@link #restoreActionBar()}.
     */
    private var mTitle: CharSequence? = null

    override fun onResume() {
        super<ActionBarActivity>.onResume()
        MobclickAgent.onResume(this)
        if (!(mSearchView?.isIconified() ?: true)) {
            this@MainActivity.onBackPressed()
        }
    }

    override fun onPause() {
        super<ActionBarActivity>.onPause()
        MobclickAgent.onPause(this);
    }

    //    var materialMenu: MaterialMenuIcon? = null

    override fun onCreate(savedInstanceState: android.os.Bundle?) {
        super<ActionBarActivity>.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(id(R.id.toolBar) as android.support.v7.widget.Toolbar)

        val fm = getSupportFragmentManager()

        mNavDrawerFragment = (fm findFragmentById R.id.navigation_drawer ) as NavigationDrawerFragment
        mNavDrawerFragment!!.setUp(R.id.navigation_drawer, id(R.id.drawer_layout) as DrawerLayout)
        mTitle = getTitle()
        mColDrawerFragment = (fm findFragmentById R.id.collection_drawer) as CollectionDrawerFragment
        mColDrawerFragment!!.setUp(R.id.collection_drawer, id(R.id.drawer_layout) as DrawerLayout)



        //        materialMenu = MaterialMenuIcon(this, Color.WHITE, Stroke.THIN);
        //        mNavigationDrawerFragment?.setOnDrawerListener(object : OnDrawerListener {
        //            var isOpened = false
        //
        //            override fun onDrawerSlide(p0: View?, p1: Float) {
        //                Log.d("onDrawerSlide", "float: " + p1)
        //                materialMenu?.setTransformationOffset(if (isOpened) AnimationState.ARROW_CHECK else AnimationState.BURGER_ARROW, p1)
        //            }
        //
        //            override fun onDrawerOpened(p0: View?) {
        //                isOpened = true
        //            }
        //
        //            override fun onDrawerClosed(p0: View?) {
        //                isOpened = false
        //            }
        //
        //            override fun onDrawerStateChanged(p0: Int) {
        //            }
        //
        //        })
        onNavigationDrawerItemSelected(0)
    }

    override fun onPostCreate(savedInstanceState: android.os.Bundle?) {
        super<ActionBarActivity>.onPostCreate(savedInstanceState)
        //        materialMenu?.syncState(savedInstanceState);
    }

    override fun onSaveInstanceState(outState: android.os.Bundle?) {
        super<ActionBarActivity>.onSaveInstanceState(outState)
        //        materialMenu?.onSaveInstanceState(outState);
    }

    override fun onNavigationDrawerItemSelected(position: Int) {
        when (position) {
            0 -> getSupportFragmentManager().beginTransaction().replace(R.id.container, HomeFragment()).commit()
            1 -> getSupportFragmentManager().beginTransaction().replace(R.id.container, AboutFragment()).commit()
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
        //        val actionBar = getActionBar()
        //        actionBar setNavigationMode ActionBar.NAVIGATION_MODE_STANDARD
        //        actionBar setDisplayShowTitleEnabled true
        //        getActionBar() setDisplayUseLogoEnabled false
        //        getActionBar() setDisplayHomeAsUpEnabled true
        //        getActionBar() setDisplayOptions ActionBar.DISPLAY_SHOW_TITLE
        //        actionBar setTitle mTitle
    }

    var mSearchView: SearchView ? = null
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        if (!mNavDrawerFragment!!.isDrawerOpen() && !mColDrawerFragment!!.isDrawerOpen()) {
            // Only show items in the action bar relevant to this screen
            // if the drawer is not showing. Otherwise, let the drawer
            // decide what to show in the action bar.
            getMenuInflater().inflate(R.menu.global, menu)

            // Associate searchable configuration with the SearchView
            val searchManager = getSystemService(Context.SEARCH_SERVICE) as SearchManager
            mSearchView = menu!!.findItem(R.id.action_search)?.getActionView() as SearchView
            mSearchView!!.setSearchableInfo(searchManager.getSearchableInfo(android.content.ComponentName(this, javaClass<SearchActivity>())))
            //            mSearchView.setOnQueryTextListener(object : OnQueryTextListener {
            //                override fun onQueryTextSubmit(p0: String?): Boolean {
            //                    mSearchView.setQuery("", false)
            //                    mSearchView.clearFocus()
            //                    mSearchView.setIconified(true)
            //                    this@MainActivity.onBackPressed()
            //                    return false
            //                }
            //
            //                override fun onQueryTextChange(p0: String?): Boolean {
            //                    return false
            //                }
            //
            //            })
            restoreActionBar()
            return true
        }
        return super<ActionBarActivity>.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.getItemId()) {
            R.id.action_search -> (item!!.getActionView() as SearchView).let { it.setQuery("", false);it.setIconified(true) }
        //            android.R.id.home -> if (!mNavigationDrawerFragment!!.isDrawerOpen() && !mCollectionDrawerFragment!!.isDrawerOpen())
        ////                materialMenu?.animatePressedState(IconState.ARROW) else materialMenu?.animatePressedState(IconState.BURGER)
        //            R.id.action_about -> startActivity(Intent(this, javaClass<AboutActivity>()))
        }
        return super<ActionBarActivity>.onOptionsItemSelected(item)
    }
}
