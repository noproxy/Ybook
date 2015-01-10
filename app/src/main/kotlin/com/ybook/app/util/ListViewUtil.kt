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

package com.ybook.app.util

import android.view.View
import android.view.View.MeasureSpec
import android.view.ViewGroup
import android.widget.AbsListView
import android.widget.ListAdapter
import android.widget.ListView
import android.view.ViewGroup.LayoutParams
import android.widget.ExpandableListView
import android.widget.ExpandableListAdapter

/**
 * Created by Carlos on 2014/12/15.
 */
public class ListViewUtil {
    class object {
        /**
         * * Method for Setting the Height of the ListView dynamically.
         * *** Hack to fix the issue of not showing all the items of the ListView
         * *** when placed inside a ScrollView  ***
         */
        public fun setListViewHeightBasedOnChildren(expandableListView: ExpandableListView, adapter: ExpandableListAdapter) {
            val desiredWidth = MeasureSpec.makeMeasureSpec(expandableListView.getWidth(), MeasureSpec.UNSPECIFIED)
            var totalHeight = 0
            var view: View? = null

            //            val widthMeasureSpec = MeasureSpec.makeMeasureSpec(ViewGroup.LayoutParams.MATCH_PARENT, View.MeasureSpec.EXACTLY)
            //            val heightMeasureSpec = MeasureSpec.makeMeasureSpec(ViewGroup.LayoutParams.WRAP_CONTENT, View.MeasureSpec.EXACTLY)
            //            view = adapter.getView(0, view, expandableListView);
            //            view?.measure(widthMeasureSpec, heightMeasureSpec);

            for (i in 0..adapter.getGroupCount() - 1) {
                for (j in 0..adapter.getChildrenCount(i)) {
                    view = adapter.getGroupView(i, true, view, expandableListView)
                    view!!.setLayoutParams(ViewGroup.LayoutParams(desiredWidth, LayoutParams.WRAP_CONTENT))
                    view!!.measure(desiredWidth, MeasureSpec.UNSPECIFIED)
                    totalHeight += view!!.getMeasuredHeight()

                    view = adapter.getChildView(i, j, true, view, expandableListView)
                    view!!.setLayoutParams(ViewGroup.LayoutParams(desiredWidth, LayoutParams.WRAP_CONTENT))
                    view!!.measure(desiredWidth, MeasureSpec.UNSPECIFIED)
                    totalHeight += view!!.getMeasuredHeight()
                }
            }

            val params = expandableListView.getLayoutParams()
            params.height = totalHeight + (expandableListView.getDividerHeight() * 7)
            expandableListView.setLayoutParams(params)
            expandableListView.requestLayout()
        }
    }
}
