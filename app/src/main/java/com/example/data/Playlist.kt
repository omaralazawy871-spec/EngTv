package com.example.data

data class Playlist(
    val id: Int = 0,
    val name: String,
    val sourceUrl: String,
    val addedAt: Long = System.currentTimeMillis()
)
