package com.example.data

data class Channel(
    val id: Int = 0,
    val playlistId: Int = 0,
    val name: String,
    val streamUrl: String,
    val logoUrl: String? = null,
    val groupTitle: String? = null,
    val isFavorite: Boolean = false
)
