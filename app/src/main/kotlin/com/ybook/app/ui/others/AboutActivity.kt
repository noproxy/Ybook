package com.ybook.app.ui.others

import com.ybook.app.swipebacklayout.SwipeBackActivity
import android.os.Bundle
import android.view.MenuItem
import com.ybook.app.R
import com.ybook.app.ui

/**
 * Created by Carlos on 2014/12/21.
 */
public class AboutActivity : SwipeBackActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super<SwipeBackActivity>.onCreate(savedInstanceState)
        setContentView(R.layout.activity_about)
        getSupportFragmentManager().beginTransaction().replace(R.id.container, AboutFragment()).commit()
        getActionBar() setTitle (getString(R.string.action_about))
        getActionBar() setDisplayHomeAsUpEnabled true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.getItemId()) {
            android.R.id.home -> onBackPressed()
        }
        return super<SwipeBackActivity>.onOptionsItemSelected(item)
    }
}