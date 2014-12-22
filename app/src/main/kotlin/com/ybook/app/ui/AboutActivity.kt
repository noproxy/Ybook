package com.ybook.app.ui

import android.app.Activity
import android.os.Bundle
import com.ybook.app.R
import android.support.v4.app.FragmentActivity
import com.ybook.app.swipebacklayout.SwipeBackActivity

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
}