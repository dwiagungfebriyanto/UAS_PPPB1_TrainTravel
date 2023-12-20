package com.example.traintravel.data

import java.io.Serializable

data class Users(
    var role: String = "user",
    var username: String = "",
    var birthDate: String = ""
) : Serializable
