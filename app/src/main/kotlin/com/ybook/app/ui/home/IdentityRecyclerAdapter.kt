package com.ybook.app.ui.home

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import java.util.ArrayList
import android.view.View
import android.content.Context
import android.view.ViewGroup
import android.widget.TextView
import com.ybook.app.R
import com.ybook.app.ui.home.IdentityRecyclerAdapter.IdentityCardData

/**
 * Created by Carlos on 2015/1/7.
 */
public class IdentityRecyclerAdapter(mInflater: LayoutInflater, val mItems: ArrayList<IdentityCardData>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    public enum class ViewType {
        Header
        Empty
        Item
    }


    private val mHeaderView = mInflater.inflate(R.layout.padding_tab, null)
    private val mEmptyView = mInflater.inflate(R.layout.padding_tab, null)
    override fun getItemCount(): Int {
        //a head and an empty view addition
        if (mItems.size() == 0) return 2
        else return mItems.size() + 1//a headView addition
    }

    override fun getItemViewType(position: Int): Int {
        if (position == 0) return ViewType.Header.ordinal()
        else {
            if (mItems.size() == 0) return ViewType.Empty.ordinal()
            else return mItems[position].viewType.ordinal()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when ( ViewType.valueOf(viewType.toString()) ) {
            ViewType.Header -> HeaderViewHolder(mHeaderView)
            ViewType.Empty -> EmptyViewHolder(mEmptyView)
            else -> ItemViewHolder(null!!)
        }
    }

    override fun onBindViewHolder(viewHolder: RecyclerView.ViewHolder, position: Int) {
        if (viewHolder is ItemViewHolder) {

        }
    }

    class HeaderViewHolder(view: View) : RecyclerView.ViewHolder(view)
    class EmptyViewHolder(view: View) : RecyclerView.ViewHolder(view)

    class ItemViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var textView: TextView

        {
            textView = view.findViewById(android.R.id.text1) as TextView
        }
    }

    public data class IdentityCardData(val viewType: ViewType) {

    }

}