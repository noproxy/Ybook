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
import com.ybook.app.id

/**
 * This is an recyclerAdapter of IdentityFragment to manage the cards.
 * This must handle the placeholder( there is an empty because of the SlidingUp to hide header), the empty card and the login hint card.
 *
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
            ViewType.Header -> HeaderViewHolder(mInflater.inflate(R.layout.padding_tab, parent, false))
            ViewType.Empty -> EmptyViewHolder(mInflater.inflate(R.layout.card_empty, parent, false))
            ViewType.Login -> LoginViewHolder(mInflater.inflate(R.layout.card_to_login, parent, false))
            else -> ItemViewHolder(null!!)
        }
    }

    override fun onBindViewHolder(viewHolder: RecyclerView.ViewHolder, position: Int) {
        //TODO header:position - 1
        when ( viewHolder ) {
            is EmptyViewHolder -> viewHolder.view setOnClickListener { viewHolder.switchState(EmptyState.Loading) }
            is LoginViewHolder -> viewHolder.view setOnClickListener { it.getContext().let { it.startActivity(Intent(it, javaClass<LoginActivity>())) } }
            is ItemViewHolder -> return
        }
    }

    class HeaderViewHolder(val view: View) : RecyclerView.ViewHolder(view)
    class EmptyViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
        private val mEmptyText = (view id R.id.emptyText) as TextView
        private val mProgressBar = (view id R.id.emptyProgressBar)
        public fun switchState(state: EmptyState) {
            when (state) {
                EmptyState.More -> {
                    mEmptyText setVisibility View.VISIBLE
                    mEmptyText setText (view.getResources() getString R.string.emptyCardHintA)
                    mProgressBar setVisibility View.INVISIBLE
                }
                EmptyState.Loading -> {
                    mEmptyText setVisibility View.INVISIBLE
                    mProgressBar setVisibility View.VISIBLE
                }
                EmptyState.Empty -> {
                    mEmptyText setVisibility View.VISIBLE
                    mEmptyText setText (view.getResources() getString R.string.emptyCardHintB)
                    mProgressBar setVisibility View.INVISIBLE
                }
            }
        }


    }

    public enum class EmptyState {
        More
        Loading
        Empty
    }

    class ItemViewHolder(val view: View) : RecyclerView.ViewHolder(view)
    class LoginViewHolder(val view: View) : RecyclerView.ViewHolder(view)
    public data class IdentityCardData(val viewType: ViewType)

}