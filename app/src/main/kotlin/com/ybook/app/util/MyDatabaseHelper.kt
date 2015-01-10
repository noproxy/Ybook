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

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.util.Log
import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper
import com.j256.ormlite.dao.Dao
import com.j256.ormlite.dao.RuntimeExceptionDao
import com.j256.ormlite.support.ConnectionSource
import com.j256.ormlite.table.TableUtils
import com.ybook.app.R
import com.ybook.app.bean.BookItem

import java.sql.SQLException
import com.ybook.app.bean.BookList
import com.ybook.app.bean.MarkedList

/**
 * Created by Carlos on 2014/8/6.
 */
public class MyDatabaseHelper(context: Context) : OrmLiteSqliteOpenHelper(context, MyDatabaseHelper.DATABASE_NAME, null, MyDatabaseHelper.DATABASE_VERSION, R.raw.ormlite_config) {
    private var bookItemDao: Dao<BookItem, Int>? = null
    private var bookItemRuntimeDao: RuntimeExceptionDao<BookItem, Int>? = null
    private var bookListRuntimeDao: RuntimeExceptionDao<BookList, Int>? = null


    override fun onCreate(database: SQLiteDatabase, connectionSource: ConnectionSource) {
        try {
            TableUtils.createTable<BookItem>(connectionSource, javaClass<BookItem>())
            TableUtils.createTable<BookList>(connectionSource, javaClass<BookList>())
        } catch (e: SQLException) {
            Log.e(javaClass<MyDatabaseHelper>().getName(), "Can't create database", e)
            throw RuntimeException(e)
        }
        getBookListDao().create(BookList(MarkedList.MARKED_LIST_NAME))


    }

    throws(javaClass<SQLException>())
    public fun getDao(): Dao<BookItem, Int> = bookItemDao ?: getDao<Dao<BookItem, Int>, BookItem>(javaClass<BookItem>())


    public fun getBookItemDao(): RuntimeExceptionDao<BookItem, Int> = bookItemRuntimeDao ?: getRuntimeExceptionDao<RuntimeExceptionDao<BookItem, Int>, BookItem>(javaClass<BookItem>())

    public fun getBookListDao(): RuntimeExceptionDao<BookList, Int> = bookListRuntimeDao ?: getRuntimeExceptionDao<RuntimeExceptionDao<BookList, Int>, BookList>(javaClass<BookList>())


    override fun onUpgrade(database: SQLiteDatabase, connectionSource: ConnectionSource, oldVersion: Int, newVersion: Int) {
        try {
            Log.i(javaClass<MyDatabaseHelper>().getName(), "onUpgrade")
            TableUtils.dropTable<BookItem, Any>(connectionSource, javaClass<BookItem>(), true)
            // after we drop the old databases, we create the new ones
            onCreate(database, connectionSource)
        } catch (e: SQLException) {
            Log.e(javaClass<MyDatabaseHelper>().getName(), "Can't drop databases", e)
            throw RuntimeException(e)
        }


    }

    class object {
        private val DATABASE_NAME = "LibQuery.db"
        private val DATABASE_VERSION = 1
    }
}
