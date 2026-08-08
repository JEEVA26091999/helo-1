package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "post_drafts")
data class PostDraftEntity(
    @PrimaryKey val id: String = "draft_" + System.currentTimeMillis(),
    val content: String,
    val category: String = "Quotes & Status",
    val language: String = "English",
    val selectedTabIndex: Int = 0,
    val selectedBgGradientIndex: Int = 0,
    val photoUrl: String = "",
    val videoUrl: String = "",
    val videoThumbnailUrl: String = "",
    val hashtags: String = "#HeloStatus #Quotes",
    val pollOption1: String = "",
    val pollOption2: String = "",
    val timestamp: Long = System.currentTimeMillis()
)
