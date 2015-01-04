package com.ybook.app.ui.others

import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.ViewGroup
import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.app.Activity
import com.ybook.app.ui.main.MainActivity
import com.ybook.app.R
import com.ybook.app.ui.main.MainActivity

/**
 * Created by carlos on 9/21/14.
 */
public class AboutFragment() : Fragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_about, container, false)
        val textView = view.findViewById(R.id.text_content) as TextView
        textView.setText(R.string.text_about)
        return view
    }


    override fun onAttach(activity: Activity?) {
        super.onAttach(activity)
        when (activity) {
            is MainActivity -> activity.onSectionAttached(1)
        }
    }
}