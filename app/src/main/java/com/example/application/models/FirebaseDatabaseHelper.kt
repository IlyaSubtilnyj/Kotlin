package com.example.application.models

import android.content.Context
import android.util.Log
import com.example.application.R
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.gson.Gson
import java.io.File
import java.io.FileInputStream
import java.util.concurrent.CountDownLatch

class FirebaseDatabaseHelper {

    companion object {

        val realtimeDatabaseLibraryFolderName: String   = "Library"
        val realtimeDatabaseReviewFolderName: String    = "Review"
        val realtimeDatabaseUrl: String                 = "https://kotlin-64235-default-rtdb.europe-west1.firebasedatabase.app/"
        val library: DatabaseReference                  = Firebase.database(realtimeDatabaseUrl).getReference(realtimeDatabaseLibraryFolderName)
        var reviews: DatabaseReference                  = Firebase.database(realtimeDatabaseUrl).getReference(realtimeDatabaseReviewFolderName)

        public fun initialize(context: Context) {

            val json = getFileContentFromRes(context, R.raw.items)
            val items = Gson().fromJson(json, Array<StorageItem>::class.java)

            for ((index, element) in items.withIndex()) {
                element.id = (index + 1).toString()
                library.push().setValue(element)
            }

        }

        private fun getFileContentFromRes(context: Context, resId: Int): String {
            return context.resources.openRawResource(resId).bufferedReader().use { it.readText() }
        }

        interface MyCallback {
            fun onCallback(items: List<StorageItem>)
        }

        fun createCallback(callback: MyCallback) {
            val eventListener = object : ValueEventListener {

                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    val items = dataSnapshot.children.mapNotNull { it.getValue(StorageItem::class.java) }
                    callback.onCallback(items)
                }
                override fun onCancelled(databaseError: DatabaseError) {
                }
            }
            library.addValueEventListener(eventListener)
            //latch.await()
        }

    }
}