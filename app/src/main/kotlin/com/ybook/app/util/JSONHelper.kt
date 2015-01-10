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

import org.json.JSONObject
import org.json.JSONArray
import java.util.ArrayList

import org.json.JSONException
import android.util.Log
import com.ybook.app.bean.LoginResponse
import com.ybook.app.bean.SearchResponse
import com.ybook.app.bean.SearchResponse.SearchObject
import com.ybook.app.bean.DetailResponse
import com.ybook.app.bean.DetailResponse.LibInfo
import com.ybook.app.bean.RenewResponse
import com.ybook.app.bean.RenewResponse.RenewObject
import com.ybook.app.bean.HistoryResponse
import com.ybook.app.bean.HistoryResponse.HistoryObject
import com.ybook.app.bean.CurrentResponse
import com.ybook.app.bean.CurrentResponse.CurrentObject
import com.ybook.app.bean.BookListResponse.BookListObject
import com.ybook.app.bean.BookListResponse

/**
 * Created by carlos on 11/10/14.
 */
object JSONHelper {
    val keyObjectCount = "objects_count"
    val keyCurrPage = "curr_page"
    val keyHasMore = "has_more"
    val keyObjects = "objects"

    val keyPress = "press"
    val keyIDType = "id_type"


    val keyCoverImgUrl = "cover_image_url"
    val keyAuthor = "author"
    val keyID = "id"
    val keyPublish = "publish"
    val keyStatus = "status"
    val keyISBN = "isbn"
    val keyDetail = "detail"
    val keyTitle = "title"
    val keyQueryID = "query_id"
    val keyAvailable = "available"

    val keyLibInfo = "lib_info"
    val keyOrderStatus = "order_status"

    val keyLibStatus = "lib_status"
    val keyLibLocation = "lib_location"

    val keyMsg = "msg"
    val keyRecordID = "record_id"
    val keyRenewId = "renew_id"

    val keyMaxPage = "max_page"

    val keyDate = "date"
    val TAG = "JSONHelper"

    fun JSONObject.s(key: String): String {
        try {
            return this.getString(key)
        } catch(e: JSONException) {
            Log.e(TAG, "key not found: " + key)
            return ""
        }
    }

    fun JSONObject.i(key: String): Int = this.getInt(key)
    fun JSONObject.b(key: String): Boolean = this.getInt(key) == 1
    fun JSONArray.ls(): Array<JSONObject> {
        var i = 0
        val a = ArrayList<JSONObject>();
        while (i < this.length()) {
            a.add(this.getJSONObject(i))
            i++
        }
        return a.copyToArray()
    }

    fun JSONObject.a(key: String): Array<JSONObject> = this.getJSONArray(key).ls()


    fun readBookListResponse(s: String, id: Int): BookListResponse {
        Log.d("bookListJson", s)
        val j = JSONObject(s)
        val a = ArrayList<BookListObject>()

        for (i in j.a("books")) {
            a.add(
                    BookListObject(
                            i.s("author"),
                            i.s("press"),
                            i.s("title"),
                            i.s("cover_image_url"),
                            i.s("id")
                    )
            )
        }
        return BookListResponse(
                j.s("title"),
                j.s("cover_image_url"),
                j.s("comment"),
                a.copyToArray(),
                id
        )
    }

    fun readLoginResponse(s: String): LoginResponse {
        val j = JSONObject(s)
        return LoginResponse(j.i(keyStatus), j.s(keyMsg))
    }

    fun readSearchResponse(s: String): SearchResponse {
        try {
            val j = JSONObject(s)
            val a = ArrayList<SearchObject> ()

            for (i in j.a(keyObjects)) {
                a.add(
                        SearchObject(
                                i.s(keyAuthor),
                                i.s(keyPress),
                                i.s(keyDetail),
                                i.s(keyIDType),
                                i.s(keyTitle),
                                i.s(keyCoverImgUrl),
                                i.s(keyID)
                        )
                )
            }
            return SearchResponse(
                    j.i(keyStatus),
                    j.i(keyObjectCount),
                    j.i(keyCurrPage),
                    j.b(keyHasMore),
                    a.copyToArray()
            )
        } catch(e: Exception) {
            throw OneResultException()
        }
    }

    fun readDetailResponse(s: String): DetailResponse {
        Log.i("readDetailResponse", s)
        val j = JSONObject(s)
        val a = ArrayList<LibInfo>()

        val d = DetailResponse(
                j.s(keyCoverImgUrl),
                j.s(keyAuthor),
                j.s(keyID),
                j.s(keyPublish),
                j.i(keyStatus),
                j.s(keyISBN),
                j.s(keyDetail),
                j.s(keyTitle),
                j.s(keyQueryID),
                j.b(keyAvailable),
                null,
                a.copyToArray()
        )
        if (j.b(keyAvailable)) {
            for (i in j.a(keyLibInfo)) {
                a.add(
                        LibInfo(
                                i.s(keyLibStatus),
                                i.s(keyLibLocation)
                        )
                )
            }
            d.libInfo = a.copyToArray()
        } else d.orderStatus = j.s(keyOrderStatus)
        return d
    }

    fun readRenewResponse(s: String?): RenewResponse {
        val j = JSONObject(s)
        val a = ArrayList<RenewObject>()
        for (i in j.a(keyObjects)) {
            a.add(
                    RenewObject(
                            i.s(keyMsg),
                            i.s(keyRecordID),
                            i.s(keyRenewId)
                    )
            )
        }
        return RenewResponse(a.copyToArray(), j.i(keyStatus))
    }

    fun readHistoryResponse(s: String?): HistoryResponse {
        val j = JSONObject(s)
        val a = ArrayList<HistoryObject> ()

        for (i in j.a(keyObjects)) {
            a.add(
                    HistoryObject(
                            i.s(keyAuthor),
                            i.s(keyRecordID),
                            i.s(keyDate),
                            i.s(keyTitle)
                    )
            )
        }
        return HistoryResponse(a.copyToArray(), j.i(keyMaxPage), j.i(keyStatus))
    }

    fun readCurrentResponse(s: String?): CurrentResponse {
        if (s == null) {
            throw NullPointerException()
        }
        val j = JSONObject(s)

        val a = ArrayList<CurrentObject> ()

        for (i in j.a(keyObjects)) {
            a.add(
                    CurrentObject(
                            i.i(keyStatus),
                            i.s(keyRecordID),
                            i.s(keyQueryID),
                            i.s(keyRenewId),
                            i.s(keyTitle)
                    )
            )
        }
        return CurrentResponse(a.copyToArray(), j.i(keyStatus)
        )
    }
}