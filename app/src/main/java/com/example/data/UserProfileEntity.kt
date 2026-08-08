package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_profiles")
data class UserProfileEntity(
    @PrimaryKey val id: String,
    val username: String,
    val name: String,
    val avatarUrl: String,
    val bio: String,
    val isFollowing: Boolean = false,
    val followersCount: Int = 1250,
    val followingCount: Int = 180,
    val postsCount: Int = 42,
    val preferredLanguage: String = "English",
    val badge: String = "Tech creator"
)
