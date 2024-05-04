package com.example.application

import android.graphics.drawable.Icon
import android.opengl.Visibility
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.ListView
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.bumptech.glide.Glide
import com.denzcoskun.imageslider.ImageSlider
import com.denzcoskun.imageslider.constants.ScaleTypes
import com.denzcoskun.imageslider.models.SlideModel
import com.example.application.databinding.TmplDetailedBinding
import com.example.application.databinding.TmplSignInBinding
import com.example.application.models.CapableActivity
import com.example.application.models.FirebaseDatabaseHelper
import com.example.application.models.ListAdapter
import com.example.application.models.Review
import com.example.application.models.ReviewListAdapter
import com.example.application.models.StorageItem
import com.example.application.models.serializable
import com.example.application.ui.theme.ApplicationTheme
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.like.LikeButton
import com.like.OnAnimationEndListener
import com.like.OnLikeListener
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.tasks.await

class DetailedActivity : CapableActivity<TmplDetailedBinding>(), OnLikeListener {

    override fun inflateBinding(): TmplDetailedBinding {
        return TmplDetailedBinding.inflate(layoutInflater)
    }

    private lateinit var listAdapter: ReviewListAdapter
    private lateinit var dataList: List<Review>
    private lateinit var target: StorageItem
    private var isStared: Boolean = false
    private var inComments: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (this.intent != null) {

            val target  = intent.serializable<StorageItem>("object")!!
            this.target = target

            val slides = runBlocking { fetchSlidesFromFirebaseStorage(target.imagePrefix) }
            if (slides.isEmpty()) {

                b.imageSlider.visibility = View.GONE
            } else {

                b.imageSlider.setImageList(slides)
            }

            val result = runBlocking {
                listReviewAsync()
            }

            this.dataList = result
            this.listAdapter = ReviewListAdapter(this, dataList)
            b.lvReviews.adapter       = listAdapter
            b.lvReviews.isClickable   = true

            b.tvName.text         = target.name
            b.tvDescription.text  = target.description
            b.tvPrice.text        = target.price.toString()
            b.tvRating.text       = target.rating.toString()
            b.tvCoaches.text      = target.coaches.joinToString(", ")
        }


        b.imageView.setOnClickListener{
            if (this.isStared) {
                b.imageView.setImageResource(com.like.view.R.drawable.star_off)
            } else {
                b.imageView.setImageResource(com.like.view.R.drawable.star_on)
            }
            this.isStared = !this.isStared
        }

        b.tvReviewsTitle.setOnClickListener {
            if (this.inComments) {
                b.mainContent.visibility = View.VISIBLE
            } else {
                b.mainContent.visibility = View.GONE
            }
            this.inComments = !this.inComments
        }

        b.starButton.isLiked = false;
        b.starButton.setOnLikeListener(this);
    }

    @ViewCallback
    fun writeReview(view: View?) {

        val review = Review("Hello world!sadfdfdsfdsfa cdsc csdcs csdac", Companion.who)
        FirebaseDatabaseHelper.reviews.child(target.id).push().setValue(review)
    }

    @ViewCallback
    override fun liked(likeButton: LikeButton?) {
        val id = target.id

    }

    @ViewCallback
    override fun unLiked(likeButton: LikeButton?) {
        TODO("Not yet implemented")
    }

    data class SlideModel(val imageUrl: String, val title: String)
    private suspend fun fetchSlidesFromFirebaseStorage(prefix: String): List<com.denzcoskun.imageslider.models.SlideModel> {
        val storageRef = Firebase.storage.reference

        // Replace "images" with the appropriate folder or path in your Firebase Storage
        val imagesRef = storageRef.child("images")

        // Fetch the list of items (files) from Firebase Storage
        val items = imagesRef.listAll().await().items

        // Filter the items based on the provided image names or attributes
        val filteredItems = items.filter { item ->
            val imageName = item.name
            imageName.startsWith(prefix) // Modify the condition based on your criteria
        }

        // Fetch the download URL for each filtered item and create SlideModel objects
        val slideModels = mutableListOf<com.denzcoskun.imageslider.models.SlideModel>()
        filteredItems.forEach { item ->
            val downloadUrl = item.downloadUrl.await().toString()
            val slideModel = SlideModel(downloadUrl)
            slideModels.add(slideModel)
        }

        return slideModels
    }

    private suspend fun listReviewAsync(): List<Review> {
        val itemList = mutableListOf<Review>() // Change String to your item type
        val list: DataSnapshot = FirebaseDatabaseHelper.reviews.child(target.id).get().await()
        for (review in list.children) {
            val item = review.getValue(Review::class.java) // Change String to your item type
            item?.let {
                itemList.add(it)
            }
        }
        return itemList
    }
}