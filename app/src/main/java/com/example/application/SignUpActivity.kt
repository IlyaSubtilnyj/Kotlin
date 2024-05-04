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
import com.example.application.models.FirebaseDatabaseHelper
import com.example.application.models.FirebaseDatabaseHelper.Companion.extractUsername
import com.example.application.models.Segue
import com.example.application.models.User
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.tasks.await

class SignUpActivity : CapableActivity<TmplSignUpBinding>() {

    override fun inflateBinding(): TmplSignUpBinding {
        return TmplSignUpBinding.inflate(layoutInflater)
    }

    private lateinit var email: String
    private lateinit var password: String
    private lateinit var firstName: String
    private lateinit var lastName: String

    @ViewCallback
    fun signUp(view: View?) {

        val isOk = this.processLoginForm()
        if (isOk) {

           disableSignUpButton()

            Firebase.auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener { task ->
                if (task.isSuccessful) {

                    Companion.who   = email
                    val user        = User(firstName, lastName)

                    FirebaseDatabaseHelper.setUserAsync(email, user) { success, exception ->
                        if (success) {

                            Companion.User = user
                            redirect<NavigationCheck>()
                        } else {

                            Toast.makeText(applicationContext, exception?.localizedMessage, Toast.LENGTH_LONG).show()
                        }
                        enableSignUpButton()
                    }

                }
            }.addOnFailureListener { exception ->
                Toast.makeText(applicationContext, exception.localizedMessage, Toast.LENGTH_LONG).show()
                enableSignUpButton()
            }
        }
    }

    @ViewCallback
    fun goToLogin(view: View?) {

        redirect<SignInActivity>()
    }

    private fun disableSignUpButton() {

        b.buttonSignUp.isClickable = false
        b.buttonSignUp.isFocusable = false
        b.buttonSignUp.alpha = 0.5f
    }

    private fun enableSignUpButton() {

        b.buttonSignUp.isClickable = true
        b.buttonSignUp.isFocusable = true
        b.buttonSignUp.alpha = 1.0f
    }

    private fun processLoginForm(): Boolean {

        val email       = b.editTextEmailAddress.text.toString()
        val password    = b.editTextPassword.text.toString()
        val firstName   = b.etFirstName.text.toString()
        val lastName    = b.etLastName.text.toString()

        val isOk = email.isNotEmpty() && password.isNotEmpty() && firstName.isNotEmpty() && lastName.isNotEmpty()
        if (isOk) {
            this.email = email; this.password = password; this.firstName = firstName; this.lastName = lastName
        }
        return isOk
    }
}