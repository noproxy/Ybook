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
: Fragment() {
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
        super.onCreate(savedInstanceState)
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
        super.onAttach(activity)
        try {
            mListener = activity as OnFragmentInteractionListener
        } catch (e: ClassCastException) {
            throw ClassCastException(activity!!.toString() + " must implement OnFragmentInteractionListener")
        }

    }

    override fun onDetach() {
        super.onDetach()
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