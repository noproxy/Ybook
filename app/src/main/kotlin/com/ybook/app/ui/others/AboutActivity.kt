/*
    Copyright 2015 Carlos

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
 */

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