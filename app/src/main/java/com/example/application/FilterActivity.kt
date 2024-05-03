package com.example.application

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Switch
import androidx.activity.ComponentActivity
import androidx.activity.OnBackPressedCallback
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.application.models.StorageItem
import com.example.application.ui.theme.ApplicationTheme
import com.example.application.models.serializable
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.database
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.tasks.await

class FilterActivity : AppCompatActivity() {

    private lateinit var originList: ArrayList<StorageItem>

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.tmpl_filter)

        originList = intent.serializable<ArrayList<StorageItem>>("array")!!

        val backButton = findViewById<Button>(R.id.applyButton)
        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {

                val data = Intent()
                data.putExtra("filteredArray", ArrayList(filter()))
                setResult(Activity.RESULT_OK, data);
                finish()
            }
        }
        callback.isEnabled = true
        backButton.setOnClickListener {
            callback.handleOnBackPressed()
        }
    }

    private fun filter(): List<StorageItem> {

        var result: List<StorageItem> = originList

        val priceFromString   = findViewById<EditText>(R.id.et_price_from).text.toString()
        val priceToString     = findViewById<EditText>(R.id.et_price_to).text.toString()
        if (priceFromString.isNotEmpty() || priceToString.isNotEmpty()) {

            val priceFrom = if (priceFromString.isEmpty()) 0 else priceFromString.toIntOrNull() ?: Int.MAX_VALUE
            val priceTo = if (priceToString.isEmpty()) Int.MAX_VALUE else priceToString.toIntOrNull() ?: 0
            result = result.filter { it.price in priceFrom..priceTo }
        }

        val isChecked   = findViewById<Switch>(R.id.sw_without_comments).isChecked
        if (isChecked) {
            val database = Firebase.database("https://kotlin-64235-default-rtdb.europe-west1.firebasedatabase.app/").reference
            val reference = database.child("Reviews")
            result = runBlocking {
                filterAsync(result, reference)
            }
        }

        return result
    }

    suspend fun filterAsync(result: List<StorageItem>, reference: DatabaseReference): List<StorageItem> =
        result.filter { storageItem ->
            val idReference = reference.child(storageItem.id)
            !idReference.get().await().exists()
        }
}
