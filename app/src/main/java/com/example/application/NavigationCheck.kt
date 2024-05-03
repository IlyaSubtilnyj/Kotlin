package com.example.application

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.SearchView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.application.databinding.TmplCheckBinding
import com.example.application.databinding.TmplSignInBinding
import com.example.application.models.CapableActivity
import com.example.application.models.FirebaseDatabaseHelper
import com.example.application.models.StorageItem
import com.example.application.models.serializable

class NavigationCheck : CapableActivity<TmplCheckBinding>() {

    override fun inflateBinding(): TmplCheckBinding {
        return TmplCheckBinding.inflate(layoutInflater)
    }

    private var fullArray: List<StorageItem> = emptyList()

    private val someActivityResultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == RESULT_OK) {
            val data: Intent? = result.data
            val array = data!!.serializable<ArrayList<StorageItem>>("filteredArray")!!
            getFragment<Home>()?.setData(array)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        replaceFragment(Home.newInstance(UserSettings("test"), fullArray, R.id.frame_layout))
        b.bottomNavigationView.setOnItemSelectedListener {
            when(it.itemId) {
                R.id.home -> {
                    replaceFragment(Home.newInstance(UserSettings("test"), fullArray, R.id.frame_layout))
                }
            }
            true
        }

        b.searchView.setOnQueryTextListener(object: SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean {
                b.searchView.clearFocus()
                if (query != null) {
                    val filteredItems = fullArray.filter { it.name.contains(query) }
                    getFragment<Home>()?.setData(filteredItems)
                }
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                if (newText.isNullOrEmpty()) {
                    b.searchView.clearFocus()
                    getFragment<Home>()?.setData(fullArray)
                }
                return false
            }

        })

        FirebaseDatabaseHelper.createCallback(object : FirebaseDatabaseHelper.Companion.MyCallback {
            override fun onCallback(items: List<StorageItem>) {
                fullArray = items
                getFragment<Home>()?.setData(items)
            }
        })

    }

    private fun <T : Fragment> getFragment(): T? {

        return try {
            val fragment = supportFragmentManager.findFragmentById(R.id.frame_layout) as T
            fragment
        } catch (e: ClassCastException) {
            null
        }
    }

    fun filterThat(view: View?) {

        val intent = Intent(this, FilterActivity::class.java)
        intent.putExtra("array", ArrayList(fullArray))
        someActivityResultLauncher.launch(intent)
    }

    private fun replaceFragment(fragment: Fragment) {

        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.frame_layout, fragment)
        fragmentTransaction.commit()
    }
}