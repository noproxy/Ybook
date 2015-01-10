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