package com.example.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface FeedItemDao {

    @Query("SELECT * FROM feed_items ORDER BY timestamp DESC")
    fun getAllFeedItems(): Flow<List<FeedItemEntity>>

    @Query("SELECT * FROM feed_items WHERE category = :category ORDER BY timestamp DESC")
    fun getFeedItemsByCategory(category: String): Flow<List<FeedItemEntity>>

    @Query("SELECT * FROM feed_items WHERE language = :language ORDER BY timestamp DESC")
    fun getFeedItemsByLanguage(language: String): Flow<List<FeedItemEntity>>

    @Query("SELECT * FROM feed_items WHERE id = :id")
    suspend fun getFeedItemById(id: String): FeedItemEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFeedItem(item: FeedItemEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFeedItems(items: List<FeedItemEntity>)

    @Update
    suspend fun updateFeedItem(item: FeedItemEntity)

    @Query("DELETE FROM feed_items WHERE id = :id")
    suspend fun deleteFeedItem(id: String)

    @Query("DELETE FROM feed_items")
    suspend fun clearAllFeedItems()

    @Query("SELECT COUNT(*) FROM feed_items")
    suspend fun getFeedItemsCount(): Int
}
