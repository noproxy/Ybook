package com.ybook.app

import android.view.View
import android.app.Activity

/**
 * Created by carlos on 12/8/14.
 */

public fun View.id(id: Int): View = this.findViewById(id)

public fun Activity.id(id: Int): View = this.findViewById(id)

