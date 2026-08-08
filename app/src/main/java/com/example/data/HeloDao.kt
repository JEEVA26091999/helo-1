package com.example.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface HeloDao {
    // POSTS
    @Query("SELECT * FROM posts ORDER BY timestamp DESC")
    fun getAllPosts(): Flow<List<PostEntity>>

    @Query("SELECT * FROM posts WHERE category = :category ORDER BY timestamp DESC")
    fun getPostsByCategory(category: String): Flow<List<PostEntity>>

    @Query("SELECT * FROM posts WHERE language = :language ORDER BY timestamp DESC")
    fun getPostsByLanguage(language: String): Flow<List<PostEntity>>

    @Query("SELECT * FROM posts WHERE postType = 'VIDEO_REEL' ORDER BY timestamp DESC")
    fun getVideoReels(): Flow<List<PostEntity>>

    @Query("SELECT * FROM posts WHERE authorId = :authorId ORDER BY timestamp DESC")
    fun getPostsByAuthor(authorId: String): Flow<List<PostEntity>>

    @Query("SELECT * FROM posts WHERE isSaved = 1 ORDER BY timestamp DESC")
    fun getSavedPosts(): Flow<List<PostEntity>>

    @Query("SELECT * FROM posts WHERE id = :postId")
    suspend fun getPostById(postId: String): PostEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPost(post: PostEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPosts(posts: List<PostEntity>)

    @Update
    suspend fun updatePost(post: PostEntity)

    @Query("DELETE FROM posts WHERE id = :postId")
    suspend fun deletePost(postId: String)

    @Query("SELECT COUNT(*) FROM posts")
    suspend fun getPostsCount(): Int

    // COMMENTS
    @Query("SELECT * FROM comments WHERE postId = :postId ORDER BY timestamp ASC")
    fun getCommentsForPost(postId: String): Flow<List<CommentEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertComment(comment: CommentEntity)

    @Query("UPDATE comments SET likesCount = likesCount + 1, isLiked = 1 WHERE id = :commentId")
    suspend fun likeComment(commentId: String)

    // USER PROFILES
    @Query("SELECT * FROM user_profiles")
    fun getAllUserProfiles(): Flow<List<UserProfileEntity>>

    @Query("SELECT * FROM user_profiles WHERE id = :userId")
    suspend fun getUserProfile(userId: String): UserProfileEntity?

    @Query("SELECT * FROM user_profiles WHERE id = :userId")
    fun getUserProfileFlow(userId: String): Flow<UserProfileEntity?>

    @Query("UPDATE posts SET authorName = :name, authorAvatarUrl = :avatarUrl, authorBadge = :badge WHERE authorId = :userId")
    suspend fun updateAuthorInfoInPosts(userId: String, name: String, avatarUrl: String, badge: String)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUserProfile(profile: UserProfileEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUserProfiles(profiles: List<UserProfileEntity>)

    @Query("UPDATE user_profiles SET isFollowing = :isFollowing WHERE id = :userId")
    suspend fun setFollowStatus(userId: String, isFollowing: Boolean)

    // CHAT MESSAGES
    @Query("SELECT * FROM chat_messages WHERE (senderId = :userId AND receiverId = :peerId) OR (senderId = :peerId AND receiverId = :userId) ORDER BY timestamp ASC")
    fun getChatMessages(userId: String, peerId: String): Flow<List<ChatMessageEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertChatMessage(message: ChatMessageEntity)

    // SAVED POSTS
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSavedPost(savedPost: SavedPostEntity)

    @Query("DELETE FROM saved_posts WHERE postId = :postId")
    suspend fun removeSavedPost(postId: String)

    // POST DRAFTS
    @Query("SELECT * FROM post_drafts ORDER BY timestamp DESC")
    fun getAllDrafts(): Flow<List<PostDraftEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDraft(draft: PostDraftEntity)

    @Query("DELETE FROM post_drafts WHERE id = :id")
    suspend fun deleteDraft(id: String)

    @Query("DELETE FROM post_drafts")
    suspend fun deleteAllDrafts()
}
