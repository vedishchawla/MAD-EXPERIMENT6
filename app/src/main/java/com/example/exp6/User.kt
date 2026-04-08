package com.example.exp6

/**
 * Data class to hold user information fetched from the REST API.
 */
data class User(
    val id: Int,
    val name: String,
    val email: String,
    val phone: String,
    val company: String
)
