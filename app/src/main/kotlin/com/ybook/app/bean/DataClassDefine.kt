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

package com.ybook.app.bean

import com.ybook.app.bean.SearchResponse.SearchObject
import com.ybook.app.bean.RenewResponse.RenewObject
import com.ybook.app.bean.HistoryResponse.HistoryObject
import com.ybook.app.bean.CurrentResponse.CurrentObject
import java.io.Serializable
import com.ybook.app.bean.DetailResponse.LibInfo
import com.ybook.app.util.BooksListUtil
import com.ybook.app.bean.BookListResponse.BookListObject

data class BookListResponse (
        val title: String,
        val coverImgUrl: String,
        val comment: String,
        val books: Array<BookListObject>,
        val id: Int
) : Serializable {

    override fun toString(): String {
        return ("BookListResponse: {"
                + " title: " + title
                + "; coverImgUrl: " + coverImgUrl
                + "; comment: " + comment
                + "; id: " + id
                + "; books: " + books.toString()
                )
    }

    data class BookListObject(
            val author: String,
            val press: String,
            val title: String,
            val coverImgUrl: String,
            val id: String
    ) : Serializable {
        fun toSearchObject(): SearchObject {
            return SearchObject(author, press, "loading", "record", title, coverImgUrl, id)
        }

        override fun toString(): String {
            return ("Book: {"
                    + " author: " + author
                    + "; press: " + press
                    + "; title: " + title
                    + "; coverImgUrl: " + coverImgUrl
                    + "; id: " + id
                    )
        }
    }
}

data class LoginResponse(
        val status: Int,
        val msg: String
) : Serializable

/**
 * store the data from search response json
 */
data class SearchResponse(
        val  status: Int,
        val  objectCount: Int,
        val  currPage: Int,
        val  hasMore: Boolean,
        var  objects: Array<SearchObject>
        /*
        {
			"status": status_code,
			"objects_count": objects_count,
			"curr_page": curr_page,
			"has_more": 0(means false) or 1,
			"objects": [
				{
					"author": author,
					"press": press,
					"detail": detail,
					"id_type": id_type,
					"title": title,
					"cover_image_url": cover_image_url,
					"id": book_id,
        		}, ...
			]
		}
         */

) {
    data class SearchObject(
            val author: String,
            val press: String,
            val detail: String,
            val idType: String,
            val title: String,
            val coverImgUrl: String,
            val id: String
            /*
            {
                        "author": author,
                        "press": press,
                        "detail": detail,
                        "id_type": id_type,
                        "title": title,
                        "cover_image_url": cover_image_url,
                        "id": book_id,
                    },
             */
    ) : Serializable {
        /**
         * to compatible with the old bean
         */
        public fun isMarked(util: BooksListUtil): Boolean {
            return MarkedList.getMarkedList().index(util, this) > -1
        }
    }
}

/**
 * store the data from detail response json
 */
data class DetailResponse(
        val coverImageUrl: String,
        val author: String,
        val id: String,
        val publish: String,
        val status: Int,
        val isbn: String,
        val detail: String,
        val title: String,
        val queryID: String,
        val available: Boolean,
        var orderStatus: String?,
        var libInfo: Array<LibInfo>

        /*
            "cover_image_url": cover_image_url,
            "author": author,
            "id": book_id,
            "publish": publisher(press),
            "status": 0,
            "isbn": isbn,
            "detail": "",
            "title": title,
            "query_id": query_id,

            ###
            "available": 1,
            "lib_info": [
                {
                    "lib_status": lib_status,
                    "lib_location": lib_location,
                }, ...
            ],
            ###
            "available": 0,
            "order_status": order_status,
            ###
         */
) : Serializable {
    data class LibInfo (
            val libStatus: String,
            val libLocation: String
            /*
            {
                        "lib_status": lib_status,
                        "lib_location": lib_location,
                    }
             */

    ) : Serializable

    fun toBookItem(): com.ybook.app.bean.BookItem {
        val b = com.ybook.app.bean.BookItem()
        b.detailResponse = this;
        b.collectTime = System.currentTimeMillis()
        return b
    }
}

data class RenewResponse(
        val objects: Array<RenewObject>?,
        val status: Int
) {
    data class RenewObject(
            msg: String,
            recordID: String,
            renewID: String
    )
    /*
    {
			"objects": [
					{
						"msg": msg,
						"record_id": book_id(record_id),
						"renew_id": renew_id,
					}, ...
				],
				"status": 0
			}
     */
}

data class HistoryResponse(
        val objects: Array<HistoryObject>,
        val maxPage: Int,
        val status: Int
) {
    data class HistoryObject(
            val author: String,
            val recordID: String,
            val date: String,
            val title: String
    )
    /*
    			{
				"objects": [
					{
						"author": author,
						"record_id": book_id(record_id),
						"date": read_date,
						"title": title
					}, ...
				],
				"max_page": max_page,
				"status": 0,
			}
     */
}

data class CurrentResponse(
        val objects: Array<CurrentObject>,
        val status: Int
) {
    data class CurrentObject(
            val status: Int,
            val recordID: String,
            val queryID: String,
            val renewID: String,
            val title: String
    )
    /*
    {"action": "current", "page": "1", "lib_code": "0"}
		response:
			{
				"objects": [
					{
						"status": book_status,
						"record_id": book_id(record_id),
						"query_id": query_id,
						"renew_id": renew_id,
						"title": title,
					}, ...
				],
				"status": 0
			}
     */
}


trait DataReceiver <T : Any> {
    public fun receive(data: T)
}
