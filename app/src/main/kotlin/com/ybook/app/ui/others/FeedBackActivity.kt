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
import android.view.View
import android.widget.EditText
import android.view.MenuItem
import com.ybook.app.R
import com.umeng.analytics.MobclickAgent

/**
 * Created by carlos on 12/3/14.
 */
public class FeedBackActivity : SwipeBackActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super<SwipeBackActivity>.onCreate(savedInstanceState)
        this.setContentView(R.layout.feedback_layout)
        getActionBar() setDisplayHomeAsUpEnabled true
        getActionBar() setDisplayUseLogoEnabled false
    }

    public fun onClick(v: View) {
        when (v.getId()) {
            R.id.commitFeedbackBtn -> postFeedback((findViewById(R.id.feedbackEditText) as EditText).getText().toString(), (findViewById(R.id.mailEditText) as EditText).getText().toString())
        }
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.getItemId()) {
            android.R.id.home -> onBackPressed()
        }
        return super<SwipeBackActivity>.onOptionsItemSelected(item)
    }

    override fun onResume() {
        super<SwipeBackActivity>.onResume()
        MobclickAgent.onResume(this);
    }

    override fun onPause() {
        super<SwipeBackActivity>.onPause()
        MobclickAgent.onPause(this);
    }

    //TODO implement postFeedback() function
    private fun postFeedback(text: String, mail: String): Boolean = true
}