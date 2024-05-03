package com.example.application.models

import com.google.firebase.database.IgnoreExtraProperties
import java.io.Serializable

@IgnoreExtraProperties
class User(
    var firstName:    String? = null,
    var lastName:     String? = null,
    var favourites:   List<String>? = emptyList()
): Serializable