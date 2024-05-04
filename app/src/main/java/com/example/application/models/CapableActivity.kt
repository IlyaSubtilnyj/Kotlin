package com.example.application.models

import android.os.Bundle
import android.view.LayoutInflater
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.viewbinding.ViewBinding
import com.example.application.NavigationCheck
import com.example.application.R
import com.example.application.databinding.TmplSignInBinding
import kotlin.properties.Delegates
import kotlin.reflect.full.declaredMemberProperties

abstract class CapableFragment: Fragment() {

    private var attachedTo by Delegates.notNull<Int>()
    protected abstract fun setData(vararg items: Any)
    fun updateView(attachedTo: Int = this.attachedTo) {
        FragmentUtils.refreshFragment(context, attachedTo)
    }

    fun attach(attachedTo: Int, vararg items: Any) {
        this.attachedTo = attachedTo
        this.setData(*items)
        this.updateView()
    }
}

abstract class CapableActivity<T: ViewBinding> : AppCompatActivity() {

    annotation class ViewCallback

    protected lateinit var b: T
    protected abstract fun inflateBinding(): T

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        b = inflateBinding()
        setContentView(b.root)
    }


    protected fun convertDataClassToPairs(data: Any): List<Pair<String, Any?>> {
        return data.javaClass.kotlin.declaredMemberProperties.map { property ->
            property.name to property.get(data)
        }
    }

    protected inline fun <reified T: CapableActivity<*>> forward() {
        Segue(this, T::class.java).forward(emptyList())
    }

    protected inline fun <reified T: CapableActivity<*>> redirect() {
        Segue(this, T::class.java).redirect(emptyList())
    }

    protected fun <T : Fragment> getFragment(): T? {

        return try {
            val fragment = supportFragmentManager.findFragmentById(R.id.frame_layout) as T
            fragment
        } catch (e: ClassCastException) {
            null
        }
    }

    protected fun replaceFragment(fragment: Fragment) {

        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.frame_layout, fragment)
        fragmentTransaction.commit()
    }

    companion object {
        /**
         * Client information
         */
        var who: String? = null
        lateinit var User: User

        init {

        }
    }

}