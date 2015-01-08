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
import com.ybook.app.ui.search.HeaderViewHolder
import android.content.Intent
import com.ybook.app.ui.others.LoginActivity

/**
 * Created by Carlos on 2015/1/7.
 */
public class IdentityRecyclerAdapter(val mInflater: LayoutInflater, val mItems: ArrayList<IdentityCardData>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    public enum class ViewType {
        Header
        Empty
        Item
        Login
    }


    override fun getItemCount(): Int {
        //a head and an empty view addition
        if (mItems.size() == 0) return 2
        else return mItems.size() + 1//a headView addition
    }

    override fun getItemViewType(position: Int): Int {
        if (position == 0) return ViewType.Header.ordinal()
        else {
            if (mItems.size() == 0) return ViewType.Empty.ordinal()
            else return mItems[position - 1]//header
                    .viewType.ordinal()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when ( ViewType.values()[viewType] ) {
            ViewType.Header -> HeaderViewHolder(mInflater.inflate(R.layout.padding_tab, null))
            ViewType.Empty -> EmptyViewHolder(mInflater.inflate(R.layout.padding_tab, null))
            ViewType.Login -> LoginViewHolder(mInflater.inflate(R.layout.card_to_login, null))
            else -> ItemViewHolder(null!!)
        }
    }

    override fun onBindViewHolder(viewHolder: RecyclerView.ViewHolder, position: Int) {
        //TODO header:position - 1
        when ( viewHolder ) {
            is LoginViewHolder -> viewHolder.view setOnClickListener { it.getContext().let { it.startActivity(Intent(it, javaClass<LoginActivity>())) } }
            is ItemViewHolder -> return
        }
    }

    class HeaderViewHolder(val view: View) : RecyclerView.ViewHolder(view)
    class EmptyViewHolder(val view: View) : RecyclerView.ViewHolder(view)
    class ItemViewHolder(val view: View) : RecyclerView.ViewHolder(view)
    class LoginViewHolder(val view: View) : RecyclerView.ViewHolder(view)
    public data class IdentityCardData(val viewType: ViewType)

}