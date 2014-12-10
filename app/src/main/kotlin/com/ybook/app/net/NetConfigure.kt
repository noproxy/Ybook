package com.ybook.app.net

import java.net.InetAddress

/**
 * Created by carlos on 11/12/14.
 */
val MSG_ERROR = 0
val MSG_SUCCESS = 1
val MSG_PASSWORD_WRONG = 2
val oldUrl = "http://whitepanda.org:2333"//TODO add url
fun getMainUrl(): String? {
    var s: String? = null
    try {
        s = "http://" + InetAddress.getByName("www.ybook.me").getHostAddress() + ":2333"
    } catch (e: Exception) {
        e.printStackTrace()
    }
    return s
}


