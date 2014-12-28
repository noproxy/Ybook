package com.ybook.app.ui.detail

import com.ybook.app.ui.home.KEY_BOOK_LIST_RESPONSE_EXTRA

/**
 * Created by Carlos on 2014/12/24.
 */
public trait BookDetailPresenter {
    fun onCreate()
}

public val INTENT_SEARCH_OBJECT: String = "searchObject"


public class BookDetailPresenterImpl(val mView: BookDetailView) : BookDetailPresenter {
    private val extraData = mView.getIntent() getSerializableExtra INTENT_SEARCH_OBJECT
            ?: mView.getIntent() getSerializableExtra KEY_BOOK_LIST_RESPONSE_EXTRA

    override fun onCreate() {

    }

}
