package com.ybook.app.bean;

import android.util.Log;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import com.ybook.app.util.BooksListUtil;

import java.util.ArrayList;

/**
 * Notice: a same {@link BookItem} can be add to a {@link BookList} twice.
 */
@DatabaseTable(tableName = "BookLists")
public class BookList {
    private static final String TAG = "BookList";
    @DatabaseField
    public String name;
    @DatabaseField(generatedId = true)
    public int id;
    public ArrayList<BookItem> items = new ArrayList<BookItem>();

    protected boolean isUpdate = false;

    public BookList() {
        isUpdate = true;
    }

    public BookList(String name) {
        this.name = name;
    }

    public boolean addBook(BookItem bookItem, BooksListUtil booksListUtil) {
//      items.add(bookItem);
// if you directly add it to ArrayList, this BookItem will not contain Id field.And this BookItem will not be saved if you change it then.So, assign false to isUpdate.
        bookItem.listID = id;
        Log.i(TAG, "add book:" + bookItem.detailResponse.getTitle() + ",List:" + this.name + ", queryID:" + bookItem.detailResponse.getQueryID());
        if (booksListUtil.addBook(bookItem)) {
            Log.i(TAG, "add success");
            isUpdate = false;
            return true;
        }
        return false;
    }

    public boolean removeBook(BookItem bookItem, BooksListUtil booksListUtil) {
        if (booksListUtil.removeBook(bookItem)) {
            items.remove(bookItem);
            return true;
        }
        return false;
    }

    public ArrayList<BookItem> getBookItems(BooksListUtil booksListUtil) {
        if (!isUpdate) {
            Log.i(TAG, "bookItems reading");
            booksListUtil.readList(this);
            isUpdate = true;
        }
        return items;
    }
}
