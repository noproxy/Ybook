package com.ybook.app.ui

import com.github.ksoichiro.android.observablescrollview.ObservableScrollViewCallbacks
import com.github.ksoichiro.android.observablescrollview.ScrollState

/**
 * Created by Carlos on 2014/12/27.
 */
public class BaseActivity() : ActionBarActivity(), ObservableScrollViewCallbacks {
    public fun onScrollChanged(var1: Int, var2: Boolean, var3: Boolean) {

    }

    public fun onDownMotionEvent() {

    }

    public fun onUpOrCancelMotionEvent(var1: ScrollState) {

    }

}