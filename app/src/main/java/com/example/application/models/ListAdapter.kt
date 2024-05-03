package com.example.application.models

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.example.application.R
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage

class ListAdapter(context: Context, dataList: List<StorageItem?>?) :
    ArrayAdapter<StorageItem?>(context, R.layout.list_item, dataList!!) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var view = convertView
        val listData = getItem(position)
        if (view == null) {
            view = LayoutInflater.from(context).inflate(R.layout.list_item, parent, false)
        }
        val listImage = view!!.findViewById<ImageView>(R.id.listImage)
        val listName = view.findViewById<TextView>(R.id.listName)
        listName.text = listData!!.name
        val listCoaches = view.findViewById<TextView>(R.id.listCoaches)
        listCoaches.text = listData.coaches.joinToString(", ")
        val listPrice = view.findViewById<TextView>(R.id.listPrice)
        listPrice.text = listData.price.toString()
        val listRating = view.findViewById<TextView>(R.id.listRating)
        listRating.text = listData.rating.toString()

        Firebase.storage("gs://kotlin-64235.appspot.com")
            .getReference("images").child(listData.imagePrefix + ".jpg")
           .downloadUrl.addOnSuccessListener {
               try {
                   Glide.with(view)
                       .load(it)
                       .into(listImage)
               } catch (ex: IllegalArgumentException) {}
           }

        return view
    }
}