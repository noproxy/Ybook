package com.ybook.app.util;

import android.content.Context;
import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.ybook.app.bean.BookItem;
import com.ybook.app.bean.BookList;
import com.ybook.app.bean.MarkedList;

import java.util.ArrayList;
import java.util.List;


public class BooksListUtil {


    private static BooksListUtil mInstance;
    private MyDatabaseHelper mHelper;
    private Context mContext;

    private ArrayList<BookList> mLists = new ArrayList<BookList>();
    private boolean isListArrayUpdated = false;

    private BooksListUtil(Context context) {
        mContext = context;
    }

    public static BooksListUtil getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new BooksListUtil(context);
        }
        return mInstance;
    }

    private MyDatabaseHelper getHelper() {
        if (mHelper == null) {
            mHelper = OpenHelperManager.getHelper(mContext, MyDatabaseHelper.class);
        }
        return mHelper;
    }

    public void releaseHelper() {
        if (mHelper != null) {
            OpenHelperManager.releaseHelper();
            mHelper = null;
        }
    }

    /**
     * to create a book List, you must call {@link #getLists()} to get the all list .
     *
     * @param name name of the list to be created.
     */
    public boolean createList(String name) {
        isListArrayUpdated = false;
        return 1 == getHelper().getBookListDao().create(new BookList(name));
    }

    public boolean deleteList(BookList list) {
        isListArrayUpdated = false;
        return 1 == getHelper().getBookListDao().delete(list);
    }

    public ArrayList<BookList> getLists() {
        if (!isListArrayUpdated) {
            mLists.clear();
            List<BookList> result = getHelper().getBookListDao().queryForAll();
            for (BookList aResult : result) {
                //to remove the marked list
                if (aResult.name.equals(MarkedList.MARKED_LIST_NAME))
                    continue;
                mLists.add(aResult);
            }
            isListArrayUpdated = true;
        }
        return mLists;
    }

    public boolean addBook(BookItem item) {
        return 1 == getHelper().getBookItemDao().create(item);
    }

    public boolean removeBook(BookItem item) {
        return 1 == getHelper().getBookItemDao().delete(item);
    }

    /**
     * @hide
     */
    public void readList(final BookList bookList) {
        bookList.items.clear();
        bookList.items.addAll(getHelper().getBookItemDao().queryForEq("listID", bookList.id));
    }


}
