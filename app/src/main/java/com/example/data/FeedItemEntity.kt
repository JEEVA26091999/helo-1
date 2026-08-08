package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "feed_items")
data class FeedItemEntity(
    @PrimaryKey
    val id: String,
    val authorName: String,
    val authorAvatarUrl: String,
    val authorHandle: String,
    val content: String,
    val mediaUrl: String? = null,
    val postType: String = "TEXT_QUOTE", // TEXT_QUOTE, IMAGE, VIDEO, POLL
    val language: String = "English",
    val category: String = "For You",
    val likesCount: Int = 0,
    val commentsCount: Int = 0,
    val sharesCount: Int = 0,
    val viewsCount: Int = 0,
    val isLiked: Boolean = false,
    val isSaved: Boolean = false,
    val timestamp: Long = System.currentTimeMillis()
)
