package com.example.application.models

import com.google.firebase.database.IgnoreExtraProperties
import java.io.Serializable

@IgnoreExtraProperties
data class Review(
    var description:    String? = null,
    var email:           String? = null
): Serializable