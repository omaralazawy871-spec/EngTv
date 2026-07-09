package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "channels")
data class Channel(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    val playlistId: Int,

    val name: String,

    val url: String,

    val logo: String? = null,

    val category: String = "General",

    val groupTitle: String? = null,

    val isFavorite: Boolean = false,

    val lastPlayed: Boolean = false
)
