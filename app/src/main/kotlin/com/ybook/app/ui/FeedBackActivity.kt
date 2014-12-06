package com.ybook.app.ui

import android.app.Activity
import android.os.Bundle
import android.view.View
import android.widget.EditText
import com.ybook.app.R

/**
 * Created by carlos on 12/3/14.
 */
public class FeedBackActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super<Activity>.onCreate(savedInstanceState)
        this.setContentView(R.layout.feedback_layout)
    }

    public fun onClick(v: View) {
        when (v.getId()) {
            R.id.commitFeedbackBtn -> postFeedback((findViewById(R.id.feedbackEditText) as EditText).getText().toString())
        }
    }

    //TODO implement postFeedback() function
    private fun postFeedback(text: String): Boolean = true
}