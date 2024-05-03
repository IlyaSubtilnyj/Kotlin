package com.example.application.models

import com.google.firebase.database.IgnoreExtraProperties
import java.io.Serializable

@IgnoreExtraProperties
data class StorageItem(
    var id: String,
    var coaches: List<String>,
    var description: String,
    var imagePrefix: String,
    var name: String,
    var price: Int,
    var rating: Double,
    var ratingNumber: Int,
): Serializable {
    constructor() : this("", emptyList(), "", "", "", 0, 0.0, 0)
}