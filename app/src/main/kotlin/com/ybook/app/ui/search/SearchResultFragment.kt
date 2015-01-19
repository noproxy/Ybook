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

package com.ybook.app.ui.search

import android.app.Fragment
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.View
import android.net.Uri
import android.app.Activity
import com.ybook.app.R
import android.support.v7.widget.Toolbar
import android.app.LoaderManager
import com.ybook.app.bean.SearchResponse
import android.content.Loader
import android.content.AsyncTaskLoader
import android.content.Context
import java.util.ArrayList
import android.support.v7.widget.RecyclerView
import com.ybook.app.net.SearchRequest
import android.os.Handler
import org.apache.http.NameValuePair
import org.apache.http.message.BasicNameValuePair
import com.ybook.app.net.getMainUrl
import org.apache.http.HttpStatus
import com.ybook.app.net.MSG_SUCCESS
import org.apache.http.util.EntityUtils
import com.ybook.app.util.JSONHelper
import com.ybook.app.util.OneResultException
import com.ybook.app.net.MSG_ONE_SEARCH_RESULT
import com.ybook.app.net.MSG_ERROR
import org.apache.http.impl.client.DefaultHttpClient
import com.ybook.app.net.PostHelper
import com.ybook.app.bean.DetailResponse
import com.ybook.app.id
import android.widget.TextView
import android.widget.ImageView

/**
 * A new implement to display search result interface, replacing the [[link:SearchActivity]] with Fragment.
 * This implement is better for adapting LoaderManager and Fragment.
 *
 * Activities that contain this fragment must implement the
 * {@link SearchResultFragment.OnFragmentInteractionListener} interface to handle interaction events.
 * And an argument to contain the search keyword is necessary.
 *
 * Use the {@link SearchResultFragment#newInstance} factory method to create an instance of this fragment.
 */
public class SearchResultFragment// Required empty public constructor
: Fragment(),
        //the loaded results may contain only one items, which will be DetailResponse rather than SearchResponse
        LoaderManager.LoaderCallbacks<Array<SearchResponse.SearchObject>> {

    val BUNDLE_KEY_PAGE = "page"
    val BUNDLE_KEY_KEYWORD = "keyword"
    val mListItems: ArrayList<SearchResponse.SearchObject> = ArrayList()
    val mAdapter: RecyclerView.Adapter<SearchViewHolder>? = null

    override fun onCreateLoader(id: Int, args: Bundle?): Loader<Array<SearchResponse.SearchObject>>? {
        if (args == null) {
            return null
        } else {
            val page = args.getInt(BUNDLE_KEY_PAGE)
            val key = args.getString(BUNDLE_KEY_KEYWORD)
            return SearchLoader(page, key, getActivity())
        }
    }


    /**
     * to handle the loaded data.
     */
    override fun onLoadFinished(loader: Loader<Array<SearchResponse.SearchObject>>?,
                                data: Array<SearchResponse.SearchObject>?) {
        if (data != null) {
            if ((loader as SearchLoader).page == 0) {
                onNewData(data)
            } else onData(data)
        }


    }

    /**
     * add the data to result list.
     */
    private fun onData(data: Array<SearchResponse.SearchObject>) {
        mListItems.addAll(data)
        mAdapter!!.notifyDataSetChanged()
    }

    /**
     * replace the data of result list
     */
    private fun onNewData(data: Array<SearchResponse.SearchObject>) {
        mListItems.clear()
        mListItems.addAll(data)
        mAdapter!!.notifyDataSetChanged()
    }

    override fun onLoaderReset(loader: Loader<Array<SearchResponse.SearchObject>>?) {
        //nothing now
    }


    // variables that control the Action Bar auto hide behavior (aka "quick recall")
    private var mActionBarAutoHideEnabled = false
    private var mActionBarAutoHideSensivity = 0
    private var mActionBarAutoHideMinY = 0
    private var mActionBarAutoHideSignal = 0
    private var mActionBarShown = true
    private var mActionBarToolbar: Toolbar? = null

    private var mSearchKey: String? = null

    /**
     * listener to reflect to the interaction.
     */
    private var mListener: OnFragmentInteractionListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super<Fragment>.onCreate(savedInstanceState)
        if (getArguments() != null) {
            mSearchKey = getArguments().getString(ARG_PARAM_SEARCH_KEY)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_search_result, container, false)
    }

    // TODO: Rename method, update argument and hook method into UI event
    public fun onButtonPressed(uri: Uri) {
        if (mListener != null) {
            mListener!!.onFragmentInteraction(uri)
        }
    }

    override fun onAttach(activity: Activity?) {
        super<Fragment>.onAttach(activity)
        try {
            mListener = activity as OnFragmentInteractionListener
        } catch (e: ClassCastException) {
            throw ClassCastException(activity!!.toString() + " must implement OnFragmentInteractionListener")
        }

    }

    override fun onDetach() {
        super<Fragment>.onDetach()
        mListener = null
    }


    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public trait OnFragmentInteractionListener {
        // TODO: Update argument type and name
        public fun onFragmentInteraction(uri: Uri)
    }

    class object {
        // the fragment initialization parameters, e.g. ARG_PARAM_SEARCH_KEY
        private val ARG_PARAM_SEARCH_KEY = "searchKey"

        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param searchKey the key word to search.
         * @return A new instance of fragment SearchResultFragment.
         */
        public fun newInstance(searchKey: String): SearchResultFragment {
            val fragment = SearchResultFragment()
            val args = Bundle()
            args.putString(ARG_PARAM_SEARCH_KEY, searchKey)
            fragment.setArguments(args)
            return fragment
        }
    }


}

public class SearchLoader(val page: Int, val key: String, con: Context) : AsyncTaskLoader<Array<SearchResponse.SearchObject>>(con) {
    val searchClient = object : DefaultHttpClient() {}

    override fun loadInBackground(): Array<SearchResponse.SearchObject>? {
        val re = search(SearchRequest(key, page))
        when ( re ) {
            is DetailResponse -> null//TODO
            is SearchResponse -> return re.objects//TODO improve
        }
        return null
    }

    public fun search(req: SearchRequest): Any? {
        val data = ArrayList<NameValuePair>()
        data.add(BasicNameValuePair("key", req.key))
        data.add(BasicNameValuePair("curr_page", req.currPage.toString()))
        data.add(BasicNameValuePair("se_type", req.searchType))
        data.add(BasicNameValuePair("lib_code", req.libCode.toString()))
        var re: Any ? = null

        try {
            val rep = searchClient.execute(PostHelper.newPost(getMainUrl() + "/search", data))

            when (rep.getStatusLine().getStatusCode()) {
                HttpStatus.SC_OK -> {
                    val str = EntityUtils.toString(rep.getEntity())
                    try {
                        re = JSONHelper.readSearchResponse(str)
                    } catch(e: OneResultException) {
                        re = JSONHelper.readDetailResponse(str)
                    }
                }
            }
            rep.getEntity().consumeContent()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return re

    }

}

public class SearchHeaderViewHolder(view: View) : RecyclerView.ViewHolder(view)

public class SearchItemViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
    val titleText = (view id R.id.text_view_book_title) as TextView
    val idText = (view id R.id.text_view_book_query_id) as TextView
    val authorText = (view id R.id.text_view_book_author) as TextView
    val pressText = (view id R.id.text_view_book_publisher) as TextView
    val markBtn = (view id R.id.bookMarkBtn)  as ImageView
    val coverImage = (view id R.id.image_view_book_cover) as ImageView
}