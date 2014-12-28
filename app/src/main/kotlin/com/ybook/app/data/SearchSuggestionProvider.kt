package com.ybook.app.data

import android.content.SearchRecentSuggestionsProvider

/**
 * Created by Carlos on 2014/12/29.
 */
public class SearchSuggestionProvider : SearchRecentSuggestionsProvider() {
    class object {
        val AUTHORITY = "com.ybook.app.SearchSuggestionProvider";
        val MODE = SearchRecentSuggestionsProvider.DATABASE_MODE_QUERIES
    }
    {
        setupSuggestions(AUTHORITY, MODE)
    }
}