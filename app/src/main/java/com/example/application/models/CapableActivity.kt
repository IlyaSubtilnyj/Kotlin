package com.example.application.models

import android.os.Bundle
import android.view.LayoutInflater
import androidx.appcompat.app.AppCompatActivity
import androidx.viewbinding.ViewBinding
import com.example.application.NavigationCheck
import com.example.application.databinding.TmplSignInBinding

abstract class CapableActivity<T: ViewBinding> : AppCompatActivity() {

    annotation class ViewCallback

    protected lateinit var b: T
    protected abstract fun inflateBinding(): T

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        b = inflateBinding()
        setContentView(b.root)
    }

    protected inline fun <reified T: CapableActivity<*>> forward() {
        Segue(this, T::class.java).forward(emptyList())
    }

    protected inline fun <reified T: CapableActivity<*>> redirect() {
        Segue(this, T::class.java).redirect(emptyList())
    }

    companion object {
        /**
         * Client information
         */
        private var who: String? = null
        private lateinit var User: User

        init {

        }
    }

}