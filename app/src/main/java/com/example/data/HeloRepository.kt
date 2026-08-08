package com.example.data

import com.example.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

class HeloRepository(private val dao: HeloDao) {

    val allPosts: Flow<List<PostEntity>> = dao.getAllPosts()
    val videoReels: Flow<List<PostEntity>> = dao.getVideoReels()
    val savedPosts: Flow<List<PostEntity>> = dao.getSavedPosts()
    val myPosts: Flow<List<PostEntity>> = dao.getPostsByAuthor("user_me")
    val allDrafts: Flow<List<PostDraftEntity>> = dao.getAllDrafts()
    val userProfiles: Flow<List<UserProfileEntity>> = dao.getAllUserProfiles()
    val currentUserProfile: Flow<UserProfileEntity?> = dao.getUserProfileFlow("user_me")

    fun getPostsByCategory(category: String): Flow<List<PostEntity>> {
        return if (category == "For You") dao.getAllPosts()
        else dao.getPostsByCategory(category)
    }

    fun getPostsByLanguage(language: String): Flow<List<PostEntity>> {
        return if (language == "All" || language == "English") dao.getAllPosts()
        else dao.getPostsByLanguage(language)
    }

    fun getComments(postId: String): Flow<List<CommentEntity>> = dao.getCommentsForPost(postId)

    fun getChatMessages(peerId: String): Flow<List<ChatMessageEntity>> =
        dao.getChatMessages(userId = "user_me", peerId = peerId)

    suspend fun initSeedDataIfEmpty() = withContext(Dispatchers.IO) {
        if (dao.getPostsCount() == 0) {
            val sampleUsers = listOf(
                UserProfileEntity(
                    id = "user_me",
                    username = "creator_alex",
                    name = "Alex Johnson",
                    avatarUrl = "android.resource://com.aistudio.helo.app/" + R.drawable.img_avatar_default_1786202183590,
                    bio = "Content Creator | Tech & Travel Explorer 🚀",
                    followersCount = 2840,
                    followingCount = 310,
                    badge = "Tech creator"
                ),
                UserProfileEntity(
                    id = "u_1",
                    username = "priya_status",
                    name = "Priya Sharma",
                    avatarUrl = "android.resource://com.aistudio.helo.app/" + R.drawable.img_avatar_default_1786202183590,
                    bio = "Daily Motivation Quotes & Good Morning Status ✨",
                    followersCount = 14200,
                    followingCount = 120,
                    isFollowing = true,
                    badge = "Status creator"
                ),
                UserProfileEntity(
                    id = "u_2",
                    username = "cricket_daily",
                    name = "Cricket Buzz Daily",
                    avatarUrl = "android.resource://com.aistudio.helo.app/" + R.drawable.img_trending_cover_1786202171214,
                    bio = "Real-time match updates, highlights & news 🏏",
                    followersCount = 98500,
                    followingCount = 15,
                    isFollowing = true,
                    badge = "Sports creator"
                ),
                UserProfileEntity(
                    id = "u_3",
                    username = "reels_master",
                    name = "Aarav Reels",
                    avatarUrl = "android.resource://com.aistudio.helo.app/" + R.drawable.img_helo_banner_1786202158870,
                    bio = "Short videos, funny trends & travel reels 🎬",
                    followersCount = 52100,
                    followingCount = 240,
                    badge = "Entertainment creator"
                )
            )
            dao.insertUserProfiles(sampleUsers)

            val samplePosts = listOf(
                PostEntity(
                    id = "p_1",
                    authorId = "u_1",
                    authorName = "Priya Sharma",
                    authorAvatarUrl = "android.resource://com.aistudio.helo.app/" + R.drawable.img_avatar_default_1786202183590,
                    authorBadge = "Popular Creator",
                    language = "English",
                    category = "Quotes & Status",
                    postType = "TEXT_QUOTE",
                    content = "Every sunrise is a fresh canvas to write your own story. Wake up with determination, go to bed with satisfaction! ✨",
                    bgGradientIndex = 0,
                    likesCount = 3420,
                    commentsCount = 184,
                    sharesCount = 950,
                    viewsCount = 18200,
                    hashtags = "#MorningQuotes #HeloStatus #Positivity",
                    timestamp = System.currentTimeMillis() - 3600000
                ),
                PostEntity(
                    id = "p_2",
                    authorId = "u_2",
                    authorName = "Cricket Buzz Daily",
                    authorAvatarUrl = "android.resource://com.aistudio.helo.app/" + R.drawable.img_trending_cover_1786202171214,
                    authorBadge = "Official Sports",
                    language = "English",
                    category = "Sports",
                    postType = "IMAGE",
                    content = "Unbelievable victory! India clinches the series with a stunning last-over finish! What a heroic performance by the team! 🏆🇮🇳",
                    imageUrl = "android.resource://com.aistudio.helo.app/" + R.drawable.img_trending_cover_1786202171214,
                    likesCount = 12450,
                    commentsCount = 620,
                    sharesCount = 3400,
                    viewsCount = 65000,
                    hashtags = "#Cricket2026 #TeamIndia #Champions",
                    timestamp = System.currentTimeMillis() - 7200000
                ),
                PostEntity(
                    id = "p_3",
                    authorId = "u_3",
                    authorName = "Aarav Reels",
                    authorAvatarUrl = "android.resource://com.aistudio.helo.app/" + R.drawable.img_helo_banner_1786202158870,
                    authorBadge = "Star Creator",
                    language = "English",
                    category = "Reels/Videos",
                    postType = "VIDEO_REEL",
                    content = "Top 5 hidden valleys in Himachal Pradesh! 🏔️ Save this video status for your upcoming holiday! ✈️",
                    videoUrl = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerBlazes.mp4",
                    videoThumbnail = "android.resource://com.aistudio.helo.app/" + R.drawable.img_helo_banner_1786202158870,
                    likesCount = 28900,
                    commentsCount = 890,
                    sharesCount = 5400,
                    viewsCount = 142000,
                    hashtags = "#TravelReels #StatusVideo #Nature",
                    timestamp = System.currentTimeMillis() - 14400000
                ),
                PostEntity(
                    id = "p_4",
                    authorId = "u_1",
                    authorName = "Priya Sharma",
                    authorAvatarUrl = "android.resource://com.aistudio.helo.app/" + R.drawable.img_avatar_default_1786202183590,
                    authorBadge = "Popular Creator",
                    language = "Hindi",
                    category = "Quotes & Status",
                    postType = "TEXT_QUOTE",
                    content = "सफलता पाने का कोई शॉर्टकट नहीं होता, हर दिन थोड़ी मेहनत आपको आपकी मंज़िल के करीब ले जाती है। 💪🔥",
                    bgGradientIndex = 2,
                    likesCount = 5120,
                    commentsCount = 240,
                    sharesCount = 1200,
                    viewsCount = 24000,
                    hashtags = "#HindiQuotes #Suvichar #HeloHindi",
                    timestamp = System.currentTimeMillis() - 21600000
                ),
                PostEntity(
                    id = "p_5",
                    authorId = "u_2",
                    authorName = "Cricket Buzz Daily",
                    authorAvatarUrl = "android.resource://com.aistudio.helo.app/" + R.drawable.img_trending_cover_1786202171214,
                    authorBadge = "Official Sports",
                    language = "English",
                    category = "Sports",
                    postType = "POLL",
                    content = "Who will win the MVP award in the upcoming tournament finals?",
                    pollOptions = "Virat Kohli|Rohit Sharma|Jasprit Bumrah|Hardik Pandya",
                    pollVotes = "3420|2150|1840|920",
                    likesCount = 8900,
                    commentsCount = 410,
                    sharesCount = 1100,
                    viewsCount = 42000,
                    hashtags = "#CricketPoll #HeloSports",
                    timestamp = System.currentTimeMillis() - 28800000
                ),
                PostEntity(
                    id = "p_6",
                    authorId = "u_3",
                    authorName = "Aarav Reels",
                    authorAvatarUrl = "android.resource://com.aistudio.helo.app/" + R.drawable.img_helo_banner_1786202158870,
                    authorBadge = "Star Creator",
                    language = "Tamil",
                    category = "Quotes & Status",
                    postType = "TEXT_QUOTE",
                    content = "உன் முயற்சியே உன் வெற்றியின் முதல் படி! எதை பற்றியும் கவலைப்படாமல் முன்னேறிச் செல்! ✨",
                    bgGradientIndex = 3,
                    likesCount = 4180,
                    commentsCount = 195,
                    sharesCount = 1120,
                    viewsCount = 21000,
                    hashtags = "#TamilStatus #MotivationalQuotes #HeloTamil",
                    timestamp = System.currentTimeMillis() - 36000000
                )
            )
            dao.insertPosts(samplePosts)

            // Seed initial comments
            val sampleComments = listOf(
                CommentEntity(
                    id = "c_1",
                    postId = "p_1",
                    authorName = "Rahul Verma",
                    authorAvatarUrl = "android.resource://com.aistudio.helo.app/" + R.drawable.img_avatar_default_1786202183590,
                    commentText = "So inspiring! Sharing this quote as my WhatsApp status right now 🌟",
                    likesCount = 42
                ),
                CommentEntity(
                    id = "c_2",
                    postId = "p_1",
                    authorName = "Ananya Das",
                    authorAvatarUrl = "android.resource://com.aistudio.helo.app/" + R.drawable.img_avatar_default_1786202183590,
                    commentText = "Beautiful thought to start the morning with! Thank you 🙏",
                    likesCount = 28
                ),
                CommentEntity(
                    id = "c_3",
                    postId = "p_2",
                    authorName = "Vikram Singh",
                    authorAvatarUrl = "android.resource://com.aistudio.helo.app/" + R.drawable.img_avatar_default_1786202183590,
                    commentText = "What a match! Absolute thriller down to the last ball! 🏏🔥",
                    likesCount = 89
                )
            )
            sampleComments.forEach { dao.insertComment(it) }

            // Seed initial chat messages
            val sampleChats = listOf(
                ChatMessageEntity(
                    id = "m_1",
                    senderId = "u_1",
                    receiverId = "user_me",
                    senderName = "Priya Sharma",
                    messageText = "Hey Alex! Loved your recent video reel post on Helo! 🎬",
                    isFromMe = false,
                    timestamp = System.currentTimeMillis() - 1200000
                ),
                ChatMessageEntity(
                    id = "m_2",
                    senderId = "user_me",
                    receiverId = "u_1",
                    senderName = "Alex Johnson",
                    messageText = "Thanks Priya! Appreciate the support! Your morning quotes are awesome too ✨",
                    isFromMe = true,
                    timestamp = System.currentTimeMillis() - 600000
                )
            )
            sampleChats.forEach { dao.insertChatMessage(it) }
        }
    }

    suspend fun toggleLike(postId: String) = withContext(Dispatchers.IO) {
        val post = dao.getPostById(postId) ?: return@withContext
        val newIsLiked = !post.isLiked
        val newLikesCount = if (newIsLiked) post.likesCount + 1 else (post.likesCount - 1).coerceAtLeast(0)
        dao.updatePost(post.copy(isLiked = newIsLiked, likesCount = newLikesCount))
    }

    suspend fun toggleSave(postId: String) = withContext(Dispatchers.IO) {
        val post = dao.getPostById(postId) ?: return@withContext
        val newIsSaved = !post.isSaved
        if (newIsSaved) {
            dao.insertSavedPost(SavedPostEntity(postId = postId))
        } else {
            dao.removeSavedPost(postId)
        }
        dao.updatePost(post.copy(isSaved = newIsSaved))
    }

    suspend fun incrementShare(postId: String) = withContext(Dispatchers.IO) {
        val post = dao.getPostById(postId) ?: return@withContext
        dao.updatePost(post.copy(sharesCount = post.sharesCount + 1))
    }

    suspend fun votePoll(postId: String, optionIndex: Int) = withContext(Dispatchers.IO) {
        val post = dao.getPostById(postId) ?: return@withContext
        if (post.userVotedOption >= 0) return@withContext // Already voted
        val votesList = post.pollVotes?.split("|")?.map { it.toIntOrNull() ?: 0 }?.toMutableList() ?: return@withContext
        if (optionIndex in votesList.indices) {
            votesList[optionIndex] = votesList[optionIndex] + 1
            val updatedVotesStr = votesList.joinToString("|")
            dao.updatePost(post.copy(pollVotes = updatedVotesStr, userVotedOption = optionIndex))
        }
    }

    suspend fun addPost(post: PostEntity) = withContext(Dispatchers.IO) {
        dao.insertPost(post)
    }

    suspend fun deletePost(postId: String) = withContext(Dispatchers.IO) {
        dao.deletePost(postId)
    }

    suspend fun updatePost(post: PostEntity) = withContext(Dispatchers.IO) {
        dao.updatePost(post)
    }

    suspend fun addComment(postId: String, commentText: String) = withContext(Dispatchers.IO) {
        val me = dao.getUserProfile("user_me")
        val comment = CommentEntity(
            id = "c_" + System.currentTimeMillis(),
            postId = postId,
            authorName = me?.name ?: "Alex Johnson",
            authorAvatarUrl = me?.avatarUrl ?: ("android.resource://com.aistudio.helo.app/" + R.drawable.img_avatar_default_1786202183590),
            commentText = commentText
        )
        dao.insertComment(comment)
        val post = dao.getPostById(postId)
        if (post != null) {
            dao.updatePost(post.copy(commentsCount = post.commentsCount + 1))
        }
    }

    suspend fun sendChatMessage(peerId: String, text: String) = withContext(Dispatchers.IO) {
        val me = dao.getUserProfile("user_me")
        val msg = ChatMessageEntity(
            id = "m_" + System.currentTimeMillis(),
            senderId = "user_me",
            receiverId = peerId,
            senderName = me?.name ?: "Alex Johnson",
            messageText = text,
            isFromMe = true
        )
        dao.insertChatMessage(msg)
    }

    suspend fun toggleFollowUser(userId: String) = withContext(Dispatchers.IO) {
        val profile = dao.getUserProfile(userId) ?: return@withContext
        val newStatus = !profile.isFollowing
        dao.setFollowStatus(userId, newStatus)
    }

    suspend fun updateUserProfile(profile: UserProfileEntity) = withContext(Dispatchers.IO) {
        dao.insertUserProfile(profile)
        dao.updateAuthorInfoInPosts(profile.id, profile.name, profile.avatarUrl, profile.badge)
    }

    suspend fun saveDraft(draft: PostDraftEntity) = withContext(Dispatchers.IO) {
        dao.insertDraft(draft)
    }

    suspend fun deleteDraft(id: String) = withContext(Dispatchers.IO) {
        dao.deleteDraft(id)
    }
}
