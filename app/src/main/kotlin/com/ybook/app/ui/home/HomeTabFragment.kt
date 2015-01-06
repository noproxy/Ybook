package com.ybook.app.ui.home

import android.app.Fragment
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.View
import android.net.Uri
import android.app.Activity
import com.ybook.app.R
import android.support.v4.app.FragmentStatePagerAdapter
import android.support.v4.app.FragmentManager
import android.support.v4.app
import android.support.v4.app.Fragment
import android.util.SparseArray
import com.ybook.app.IdentityFragment
import me.toxz.kotlin.after
import android.support.v4.view.ViewPager
import com.ybook.app.id
import com.google.samples.apps.iosched.ui.widget.SlidingTabLayout

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link HomeTabFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link HomeTabFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomeTabFragment// Required empty public constructor
: Fragment() {


    private var mListener: OnFragmentInteractionListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_home_tab, container, false).after {
            val mPagerAdapter = this.TabAdapter(getChildFragmentManager())
            val mPager = it.id(R.id.pager) as ViewPager
            mPager.setAdapter(mPagerAdapter)


            val slidingTabLayout = it.findViewById(R.id.sliding_tabs) as SlidingTabLayout
            slidingTabLayout.setCustomTabView(R.layout.tab_indicator, android.R.id.text1)
            slidingTabLayout.setSelectedIndicatorColors(getResources().getColor(R.color.colorAccent))
            slidingTabLayout.setDistributeEvenly(true)
            slidingTabLayout.setViewPager(mPager)
        }
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
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @return A new instance of fragment HomeTabFragment.
         */
        public fun newInstance(): HomeTabFragment {
            return HomeTabFragment()
        }
    }

    private inner class TabAdapter(val fm: FragmentManager) : FragmentStatePagerAdapter(fm) {

        val titlesRes = array(R.string.homeTabTitleCommon, R.string.homeTabTitleIdentity)

        override fun getItem(position: Int): app.Fragment? {
            return when ( position ) {
                0 -> HomeFragment()
                1 -> IdentityFragment.newInstance()
                else -> throw TabIndexOutOfBoundsException(position)
            }
        }

        override fun getCount(): Int {
            return titlesRes.size()
        }

        override fun getPageTitle(position: Int): CharSequence {
            return getString(titlesRes[position])
        }

    }

    private class TabIndexOutOfBoundsException(index: Int) : IndexOutOfBoundsException("index=" + index)
}