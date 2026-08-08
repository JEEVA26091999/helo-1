package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "posts")
data class PostEntity(
    @PrimaryKey val id: String,
    val authorId: String,
    val authorName: String,
    val authorAvatarUrl: String,
    val authorBadge: String? = null,
    val language: String = "English",
    val category: String = "For You",
    val postType: String = "IMAGE", // TEXT_QUOTE, IMAGE, VIDEO_REEL, POLL
    val content: String,
    val bgGradientIndex: Int = 0,
    val imageUrl: String? = null,
    val videoUrl: String? = null,
    val videoThumbnail: String? = null,
    val pollOptions: String? = null, // "Option 1|Option 2|Option 3"
    val pollVotes: String? = null,   // "45|120|30"
    val userVotedOption: Int = -1,
    val likesCount: Int = 0,
    val commentsCount: Int = 0,
    val sharesCount: Int = 0,
    val viewsCount: Int = 0,
    val isLiked: Boolean = false,
    val isSaved: Boolean = false,
    val hashtags: String = "",
    val timestamp: Long = System.currentTimeMillis()
)
