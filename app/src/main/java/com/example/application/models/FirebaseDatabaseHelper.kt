package com.example.application.models

import android.content.Context
import android.util.Log
import com.example.application.R
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.inject.Deferred
import com.google.firebase.ktx.Firebase
import com.google.gson.Gson
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.tasks.await
import java.io.File
import java.io.FileInputStream
import java.util.concurrent.CountDownLatch

class FirebaseDatabaseHelper {

    companion object {

        val realtimeDatabaseLibraryFolderName: String   = "Library"
        val realtimeDatabaseReviewFolderName: String    = "Review"
        val realtimeDatabaseUserFolderName: String      = "Users"
        val realtimeDatabaseUrl: String                 = "https://kotlin-64235-default-rtdb.europe-west1.firebasedatabase.app/"
        val library: DatabaseReference                  = Firebase.database(realtimeDatabaseUrl).getReference(realtimeDatabaseLibraryFolderName)
        var reviews: DatabaseReference                  = Firebase.database(realtimeDatabaseUrl).getReference(realtimeDatabaseReviewFolderName)
        var users: DatabaseReference                    = Firebase.database(realtimeDatabaseUrl).getReference(realtimeDatabaseUserFolderName)
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

        fun setUserAsync(email: String, user: User, callback: (Boolean, Exception?) -> Unit) {
            val userName = extractUsername(email)
            val usersReference = FirebaseDatabaseHelper.users.child(userName)

            usersReference.setValue(user).addOnCompleteListener { writeTask ->
                if (writeTask.isSuccessful) {
                    Log.i("New User", user.toString())
                    callback(true, null)
                } else {
                    val exception = writeTask.exception
                    callback(false, exception) // Invoke the callback with failure status and exception
                }
            }
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

        fun getUserAsync(email: String, callback: (User?, Exception?) -> Unit) {
            val userName = extractUsername(email)
            val usersReference = FirebaseDatabaseHelper.users.child(userName)

            usersReference.get().addOnCompleteListener { readTask ->
                if (readTask.isSuccessful) {
                    val user = readTask.result?.getValue(User::class.java)
                    callback(user, null)
                } else {

                    val exception = readTask.exception
                    callback(null, exception)
                }
            }
        }

        protected fun extractUsername(email: String): String {
            val atIndex = email.indexOf("@")
            val dotIndex = email.lastIndexOf(".")
            if (atIndex != -1 && dotIndex != -1 && dotIndex > atIndex) {
                return email.substring(0, dotIndex)
            }
            return email
        }

    }
}