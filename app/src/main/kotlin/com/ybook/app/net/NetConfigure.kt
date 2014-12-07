package com.ybook.app.net

import java.net.InetAddress

/**
 * Created by carlos on 11/12/14.
 */
val MSG_ERROR = 0
val MSG_SUCCESS = 1
val MSG_PASSWORD_WRONG = 2
val oldUrl = "http://whitepanda.org:2333"//TODO add url
val mainUrl = "http://" + InetAddress.getByName("www.ybook.me").getHostAddress() + ":2333"
