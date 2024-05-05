package com.example.application.models

import android.content.Context
import android.text.SpannableStringBuilder
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import androidx.core.text.bold
import com.bumptech.glide.Glide
import com.example.application.R
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage

class ReviewListAdapter(context: Context, dataList: List<Review?>?) :
    ArrayAdapter<Review?>(context, R.layout.tmpl_review_list_item, dataList!!) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var view = convertView
        val listData = getItem(position)

        if (view == null) {
            view = LayoutInflater.from(context).inflate(R.layout.tmpl_review_list_item, parent, false)
        }

        //if (listData == null) return view!!

        val reviewDisplayProvider = view!!.findViewById<TextView>(R.id.tv_review_list_item)
        val text = SpannableStringBuilder()
            .bold {
                append(listData!!.email)
            }
            .append("   ")
            .append(listData!!.description)
        reviewDisplayProvider.text = text

        return view
    }
}