package com.example.application

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.activity.ComponentActivity
import com.example.application.models.*
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth

class EnterActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initialize(this)
    }
}

fun initialize(context: EnterActivity) {

    FirebaseApp.initializeApp(context)
    //FirebaseDatabaseHelper.initialize(context)
    Segue(_context = context, _destination = SignInActivity::class.java)()
    context.finish()
}