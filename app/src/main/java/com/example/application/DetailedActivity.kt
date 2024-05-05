package com.example.application

import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.example.application.databinding.TmplDetailedBinding
import com.example.application.models.CapableActivity
import com.example.application.models.FirebaseDatabaseHelper
import com.example.application.models.Review
import com.example.application.models.ReviewListAdapter
import com.example.application.models.StorageItem
import com.example.application.models.User
import com.example.application.models.serializable
import com.google.firebase.database.DataSnapshot
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.tasks.await


class DetailedActivity : CapableActivity<TmplDetailedBinding>() {

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

            val slides = runBlocking { FirebaseDatabaseHelper.fetchSlidesFromFirebaseStorage(target.imagePrefix) }
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
            if (Companion.User.favourites?.contains(this.target.id) == true) {

                b.imageView.setImageResource(com.like.view.R.drawable.star_on)
                this.isStared = true
            } else {

                b.imageView.setImageResource(com.like.view.R.drawable.star_off)
                this.isStared = false
            }
        }

        b.tvReviewsTitle.setOnClickListener {
            if (this.inComments) {
                b.mainContent.visibility = View.VISIBLE
            } else {
                b.mainContent.visibility = View.GONE
            }
            this.inComments = !this.inComments
        }
    }

    @ViewCallback
    fun writeReview(view: View?) {

        val review = Review("Hello world!sadfdfdsfdsfa cdsc csdcs csdac", Companion.who)
        FirebaseDatabaseHelper.reviews.child(target.id).push().setValue(review)
    }

    @ViewCallback
    fun addToFavorites(view: View?) {

        if (this.isStared) {

            val matchingIds: MutableList<String> = Companion.User.favourites?.toMutableList() ?: mutableListOf()
            matchingIds.remove(target.id)
            val user = User(Companion.User.firstName, Companion.User.lastName, matchingIds)

            Companion.who?.let {
                FirebaseDatabaseHelper.setUserAsync(it, user) { success, exception ->
                    if (success) {

                        Companion.User = user
                        b.imageView.setImageResource(com.like.view.R.drawable.star_off)
                        this.isStared = false
                    } else {

                        Toast.makeText(applicationContext, exception?.localizedMessage, Toast.LENGTH_LONG).show()
                    }
                }
            }

        } else {

            val matchingIds: MutableList<String> = Companion.User.favourites?.toMutableList() ?: mutableListOf()
            matchingIds.add(target.id)
            val user = User(Companion.User.firstName, Companion.User.lastName, matchingIds)

            Companion.who?.let {
                FirebaseDatabaseHelper.setUserAsync(it, user) { success, exception ->
                    if (success) {

                        Companion.User = user
                        b.imageView.setImageResource(com.like.view.R.drawable.star_on)
                        this.isStared = true
                    } else {

                        Toast.makeText(applicationContext, exception?.localizedMessage, Toast.LENGTH_LONG).show()
                    }
                }
            }
        }
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