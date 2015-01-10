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

package com.ybook.app.net

import java.net.InetAddress

/**
 * Created by carlos on 11/12/14.
 */
val MSG_ERROR = 0
val MSG_SUCCESS = 1
val MSG_PASSWORD_WRONG = 2
val MSG_ONE_SEARCH_RESULT = 3


val oldUrl = "http://whitepanda.org:2333"//TODO add url
fun getMainUrl(): String? {
    try {
        val s = "http://" + InetAddress.getByName("www.ybook.me").getHostAddress() + ":2333"
        return s
    } catch (e: Exception) {
        e.printStackTrace()
    }
    return null
}



