package com.example.traintravel.data

import java.io.Serializable
import java.util.Date

data class Users(
    var id: String = "",
    var role: String = "user",
    var email: String = "",
    var username: String = "",
    var password: String = "",
    var birthDate: String = ""
) : Serializable
