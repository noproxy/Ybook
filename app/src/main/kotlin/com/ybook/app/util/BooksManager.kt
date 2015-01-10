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

import java.util.ArrayList
import com.ybook.app.bean.BookItem
import com.j256.ormlite.android.apptools.OpenHelperManager
import com.ybook.app.MyApplication
import com.ybook.app.R
import android.util.Log
import android.content.Context

/**
 * Created by carlos on 12/5/14.
 */
public object BooksManager {
    private val TAG = "MarkedList"


    private val mBooks = ArrayList<BookItem>()
    private var isUpdate = false
    private var mHelper: MyDatabaseHelper? = null
    private fun getHelper(con: Context): MyDatabaseHelper = mHelper ?: OpenHelperManager.getHelper(con, javaClass<MyDatabaseHelper>())

    public fun releaseHelper() {
        if (mHelper != null) {
            OpenHelperManager.releaseHelper()
            mHelper = null
        }
    }

    public fun onTrimMemory(level: Int) {
        mBooks.clear()
        System.gc()
        isUpdate = false
    }

    public fun getCollectedBooks(body: (Array<BookItem>) -> Unit) {

    }

    public fun addBook(item: BookItem, con: Context): Boolean = 1 == getHelper(con).getBookItemDao().create(item)

    public fun removeBook(item: BookItem, con: Context): Boolean = 1 == getHelper(con).getBookItemDao().delete(item)

    private fun loadBooks() {
        isUpdate = true
    }


    public val QUERY_ID_ARRAY: Array<String> = array("A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "N", "O", "P", "Q", "R", "S", "T", "TB", "TD", "TE", "TF", "TG", "TH", "TJ", "TK", "TL", "TM", "TN", "TP", "TQ", "TS", "TU", "TV", "U", "V", "X", "Z", "no_type")
    public val QUERY_ICON_ID: IntArray = intArray(R.drawable.a, R.drawable.b, R.drawable.c, R.drawable.d, R.drawable.e, R.drawable.f, R.drawable.g, R.drawable.h, R.drawable.i, R.drawable.j, R.drawable.k, R.drawable.n, R.drawable.o, R.drawable.p, R.drawable.q, R.drawable.r, R.drawable.s, R.drawable.t, R.drawable.tb, R.drawable.td, R.drawable.te, R.drawable.tf, R.drawable.tg, R.drawable.th, R.drawable.tj, R.drawable.tk, R.drawable.tl, R.drawable.tm, R.drawable.tn, R.drawable.tp, R.drawable.tq, R.drawable.ts, R.drawable.tu, R.drawable.tv, R.drawable.u, R.drawable.v, R.drawable.x, R.drawable.z, R.drawable.no_type)
    val TYPE_ARRAY = array("马列主义毛泽东思想", "哲学", "社会科学总论", "政治法律", "军事", "经济", "文化科学教育体育", "语言、文字", "文学", "艺术", "历史地理", "自然科学总论", "数理科学和化学", "天文学、地球科学", "生物科学", "医药卫生", "农业科学", "工业技术", " 一般工业技术", "矿业工程", "石油、天然气工业", "冶金工业", "金属学与金属工艺", "机械、仪表工业", "武器工业", "能源与动力工程", "原子能技术", "电工技术", "无线电电子学、电信技术", "自动化技术、计算机技术", "化学工业", "轻工业、手工业", "建筑科学", "水利工程", "交通运输", "航空、航天", "环境科学、安全科学", "综合性图书", "未上架")

    private var mSeparatedItems: ArrayList<ArrayList<BookItem>>? = null


    public fun getType(queryHead: String): String {
        for (i in QUERY_ID_ARRAY.indices) {
            if (QUERY_ID_ARRAY[i] == queryHead) {
                return TYPE_ARRAY[i]
            }
        }
        return TYPE_ARRAY[0]
    }

    public fun getIconID(queryHead: String): Int {
        for (i in QUERY_ID_ARRAY.indices) {
            if (queryHead == QUERY_ID_ARRAY[i]) {
                return QUERY_ICON_ID[i]
            }
        }
        return QUERY_ICON_ID[0]
    }

    public fun getQueryHead(queryID: String?): String {
        if (queryID == null || queryID.trim().length() == 0) {
            return "no_type"
        }
        val a = queryID.charAt(0)
        val b = queryID.charAt(2)
        if (b >= 'a' && b <= 'z') {
            return a.toString() + b.toString()
        } else {
            return a.toString()
        }
    }

    //    private fun separate(): ArrayList<ArrayList<BookItem>> {
    //        Log.i(TAG, "separate()")
    //        val results = ArrayList<ArrayList<BookItem>>()
    //        val headRecord = ArrayList<String>()
    //        for (bookItem in items) {
    //            val head = getQueryHead(bookItem.queryID)
    //            if (!headRecord.contains(head)) {
    //                headRecord.add(head)
    //                results.add(ArrayList<BookItem>())
    //            }
    //            results.get(headRecord.indexOf(head)).add(bookItem)
    //        }
    //        return results
    //    }
    //
    //    public fun getSeparatedItems(util: BooksListUtil): ArrayList<ArrayList<BookItem>> {
    //        Log.i(TAG, "isUpdate:" + isUpdate)
    //        if (!isUpdate) {
    //            getBookItems(util)
    //        }
    //        mSeparatedItems = this.separate()
    //        return mSeparatedItems!!
    //    }
    //
    //    public fun index(util: BooksListUtil, item: BookItem): Int {
    //        val bookItems = getBookItems(util)
    //        for (bookItem in bookItems) {
    //            if (item.recordID.equals(bookItem.recordID)) {
    //                return bookItems.indexOf(bookItem)
    //            }
    //        }
    //        return -1
    //    }
}