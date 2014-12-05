package com.ybook.app.ui

import android.app.Fragment
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.ybook.app.R
import android.app.Activity

/**
 * Created by carlos on 9/21/14.
 */
public class AboutFragment(sectionNumber: Int) : Fragment() {
    {
        val args = Bundle()
        args.putInt(MainActivity.ARG_SECTION_NUMBER, sectionNumber)
        this.setArguments(args)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_about, container, false)
        val textView = view.findViewById(R.id.text_content) as TextView
        textView.setText(R.string.text_about)
        return view
    }


    override fun onAttach(activity: Activity?) {
        super.onAttach(activity)
        (activity as MainActivity).onSectionAttached(getArguments().getInt(MainActivity.ARG_SECTION_NUMBER))
    }
}
