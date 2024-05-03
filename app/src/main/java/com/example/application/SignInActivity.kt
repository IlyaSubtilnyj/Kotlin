package com.example.application

import android.os.Bundle
import android.os.PersistableBundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.appcompat.app.AppCompatActivity
import com.example.application.databinding.TmplCheckBinding
import com.example.application.databinding.TmplSignInBinding
import com.example.application.models.CapableActivity
import com.example.application.models.Segue
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage

class SignInActivity : CapableActivity<TmplSignInBinding>() {

    override fun inflateBinding(): TmplSignInBinding {
        return TmplSignInBinding.inflate(layoutInflater)
    }

    private lateinit var email: String
    private lateinit var password: String

    @ViewCallback
    fun signIn(view: View?){

        val isOk = this.processLoginForm()
        Log.i("lol", isOk.toString())
        if (isOk) {

            Firebase.auth.signInWithEmailAndPassword(email,password).addOnCompleteListener { task ->
                if(task.isSuccessful){
                    redirect<NavigationCheck>()
                }
            }.addOnFailureListener { exception ->
                Toast.makeText(applicationContext,exception.localizedMessage, Toast.LENGTH_LONG).show()
            }
        }
    }

    @ViewCallback
    fun goToRegister(view: View?) {

        redirect<SignUpActivity>()
    }

    private fun processLoginForm(): Boolean {

        val email       = b.editTextEmailAddress.text.toString()
        val password    = b.editTextPassword.text.toString()

        val isOk = email.isNotEmpty() && password.isNotEmpty()
        if (isOk) {
            this.email = email; this.password = password
        }
        return isOk
    }
}