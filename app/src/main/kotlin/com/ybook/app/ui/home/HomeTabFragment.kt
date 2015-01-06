package com.ybook.app.ui.home

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
import com.ybook.app.ui.home.IdentityFragment
import me.toxz.kotlin.after
import android.support.v4.view.ViewPager
import com.ybook.app.id
import com.google.samples.apps.iosched.ui.widget.SlidingTabLayout
import com.ybook.app.ui.main.OnHeadViewHideOrShowListener
import android.view.animation.AccelerateInterpolator
import com.ybook.app.ui.main.MainActivity

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link HomeTabFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link HomeTabFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomeTabFragment// Required empty public constructor
: Fragment(), OnHeadViewHideOrShowListener {
    var mSlidingTabLayout: SlidingTabLayout? = null
    var mSlidingTabLayoutBottom: Int = 0
    override fun onHideOrShow(isShow: Boolean, parentBottom: Int) {
        if (mSlidingTabLayoutBottom == 0) {
            mSlidingTabLayoutBottom = mSlidingTabLayout?.getBottom() ?: 0
        }
        if (mSlidingTabLayoutBottom == 0) {
            return
        }
        if (isShow) {
            //open the view
            mSlidingTabLayout?.animate()?.translationY(0F)?.setInterpolator(AccelerateInterpolator())?.start()
        } else {
            mSlidingTabLayout?.animate()?.translationY(-parentBottom.toFloat())?.setInterpolator(AccelerateInterpolator())?.start();
        }
    }


    private var mListener: OnFragmentInteractionListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super<Fragment>.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_home_tab, container, false).after {
            val mPagerAdapter = this.TabAdapter(getChildFragmentManager())
            val mPager = it.id(R.id.pager) as ViewPager
            mPager.setAdapter(mPagerAdapter)

            mSlidingTabLayout = (it.findViewById(R.id.sliding_tabs) as SlidingTabLayout).after {
                it.setCustomTabView(R.layout.tab_indicator, android.R.id.text1)
                it.setSelectedIndicatorColors(getResources().getColor(R.color.tabIndicatorColorAccent))
                it.setDistributeEvenly(true)
                it.setViewPager(mPager)
            }

        }
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
        (activity as MainActivity).setOnHeadViewHideOrShowListener(this)
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