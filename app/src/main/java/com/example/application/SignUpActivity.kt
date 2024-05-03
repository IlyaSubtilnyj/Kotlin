package com.example.application

import android.app.Activity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.appcompat.app.AppCompatActivity
import com.example.application.databinding.TmplSignInBinding
import com.example.application.databinding.TmplSignUpBinding
import com.example.application.models.CapableActivity
import com.example.application.models.Segue
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class SignUpActivity : CapableActivity<TmplSignUpBinding>() {

    override fun inflateBinding(): TmplSignUpBinding {
        return TmplSignUpBinding.inflate(layoutInflater)
    }

    @ViewCallback
    fun signUp(view: View?) {

        val email       = findViewById<EditText>(R.id.editTextEmailAddress).text.toString()
        val password    = findViewById<EditText>(R.id.editTextPassword).text.toString()
        if (email.isEmpty() || password.isEmpty()) return;

        Firebase.auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener { task ->
            if(task.isSuccessful){

                redirect<NavigationCheck>()
            }
        }.addOnFailureListener { exception ->
            Toast.makeText(applicationContext, exception.localizedMessage, Toast.LENGTH_LONG).show()
        }
    }

    @ViewCallback
    fun goToLogin(view: View?) {

        redirect<SignInActivity>()
    }
}