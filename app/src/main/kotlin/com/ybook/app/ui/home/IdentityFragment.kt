package com.ybook.app.ui.home

import android.support.v4.app.Fragment
import android.widget.AbsListView
import android.widget.ListAdapter
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.View
import android.widget.AdapterView
import android.app.Activity
import android.widget.TextView
import com.ybook.app.dummy.DummyContent
import android.widget.ArrayAdapter
import com.ybook.app
import android.widget.AdapterView.OnItemClickListener
import com.github.ksoichiro.android.observablescrollview.ObservableListView
import com.github.ksoichiro.android.observablescrollview.ObservableScrollViewCallbacks
import com.github.ksoichiro.android.observablescrollview.ObservableGridView
import com.github.ksoichiro.android.observablescrollview.Scrollable
import com.ybook.app.ui.main.MainActivity
import com.ybook.app.ui.main.OnHeadViewHideOrShowListener
import android.widget.ListView
import com.ybook.app.R
import android.support.v7.widget.RecyclerView
import com.github.ksoichiro.android.observablescrollview.ObservableRecyclerView
import java.util.ArrayList
import com.ybook.app.ui.home.IdentityRecyclerAdapter.IdentityCardData
import com.ybook.app.util.AccountHelper
import me.toxz.kotlin.after
import android.support.v7.widget.LinearLayoutManager

/**
 * A fragment representing a list of Items.
 * <p/>
 * Large screen devices (such as tablets) are supported by replacing the ListView
 * with a GridView.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnFragmentInteractionListener}
 * interface.
 */
public class IdentityFragment : Fragment(), OnItemClickListener {

    private var mInteractionListener: OnFragmentInteractionListener? = null
    private var mRecyclerView: ObservableRecyclerView? = null
    private var mAdapter: IdentityRecyclerAdapter? = null
    private val mItems = ArrayList<IdentityCardData>()
    private var mEmptyData: IdentityCardData? = IdentityCardData(IdentityRecyclerAdapter.ViewType.Login)

    override fun onCreate(savedInstanceState: Bundle?) {
        super<Fragment>.onCreate(savedInstanceState)
        // TODO: Change Adapter to display your content
    }

    override fun onResume() {
        super<Fragment>.onResume()
        testLogin()
    }

    private fun testLogin() {
        if (mItems.contains(mEmptyData) && AccountHelper.hasAccount(getActivity())) {
            mItems.remove(mEmptyData)
            mAdapter!!.notifyDataSetChanged()
        } else if (!mItems.contains(mEmptyData) && !AccountHelper.hasAccount(getActivity())) {
            mItems.add(mEmptyData)
            mAdapter!!.notifyDataSetChanged()
        }
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View {

        val view = inflater!!.inflate(app.R.layout.fragment_identity_list, container, false)
        mAdapter = IdentityRecyclerAdapter(inflater, mItems)
        mRecyclerView = (view.findViewById(android.R.id.list) as ObservableRecyclerView) after {
            it setHasFixedSize true
            it setLayoutManager LinearLayoutManager(inflater.getContext())
            it setAdapter mAdapter
            if (!this.isDetached()) it setScrollViewCallbacks mCallback
        }
        return view
    }

    private var mCallback: ObservableScrollViewCallbacks? = null


    override fun onAttach(activity: Activity?) {
        super<Fragment>.onAttach(activity)
        try {
            mInteractionListener = activity as OnFragmentInteractionListener
        } catch (e: ClassCastException) {
            throw ClassCastException(activity!!.toString() + " must implement OnFragmentInteractionListener")
        }

        try {
            mCallback = activity as ObservableScrollViewCallbacks
            if (mRecyclerView != null) {
                (mRecyclerView as Scrollable).setScrollViewCallbacks(mCallback)
            }
        } catch(e: ClassCastException) {
            throw ClassCastException(activity!!.toString() + " must implement ObservableScrollViewCallbacks")
        }
    }

    override fun onDetach() {
        super<Fragment>.onDetach()
        mInteractionListener = null
        mRecyclerView!!.setScrollViewCallbacks(null)
    }


    override fun onItemClick(parent: AdapterView<*>, view: View, position: Int, id: Long) {
        if (null != mInteractionListener) {
            // Notify the active callbacks interface (the activity, if the
            // fragment is attached to one) that an item has been selected.
            mInteractionListener!!.onFragmentInteraction(DummyContent.ITEMS.get(position).id)
        }
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
        public fun onFragmentInteraction(id: String)
    }

    class object {

        // TODO: Rename and change types of parameters
        public fun newInstance(): IdentityFragment {
            return IdentityFragment()
        }
    }

}