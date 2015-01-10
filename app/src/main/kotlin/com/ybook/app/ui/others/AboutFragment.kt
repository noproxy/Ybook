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