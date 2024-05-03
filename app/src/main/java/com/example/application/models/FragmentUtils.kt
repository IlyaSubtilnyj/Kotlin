package com.example.application.models

import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.application.R
import java.io.Serializable

inline fun <reified T : Serializable> Bundle.serializable(key: String): T? = when {
    Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU -> getSerializable(key, T::class.java)
    else -> @Suppress("DEPRECATION") getSerializable(key) as? T
}

inline fun <reified T : Serializable> Intent.serializable(key: String): T? = when {
    Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU -> getSerializableExtra(key, T::class.java)
    else -> @Suppress("DEPRECATION") getSerializableExtra(key) as? T
}

class FragmentUtils {

    companion object {

        fun refreshFragment(context: Context?, fragment: Int) {
            context?.let {
                val fragmentManager = (context as? AppCompatActivity)?.supportFragmentManager
                fragmentManager ?.let {
                    val currentFragment = fragmentManager.findFragmentById(fragment)
                    currentFragment?.let {
                        val detachTransaction = fragmentManager.beginTransaction()
                        val attachTransaction = fragmentManager.beginTransaction()
                        detachTransaction.detach(it)
                        attachTransaction.attach(it)
                        detachTransaction.commit()
                        attachTransaction.commit()
                    }
                }
            }
        }
    }
}