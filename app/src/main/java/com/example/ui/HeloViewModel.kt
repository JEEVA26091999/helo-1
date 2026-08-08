package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.AiContentGenerator
import com.example.data.AppDatabase
import com.example.data.CommentEntity
import com.example.data.FeedItemDao
import com.example.data.FeedItemEntity
import com.example.data.HeloRepository
import com.example.data.PostEntity
import com.example.data.PostDraftEntity
import com.example.data.UserProfileEntity
import com.example.data.UserPreferencesDataStore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

import com.example.ui.screens.NotificationCategoryFilter
import com.example.ui.screens.NotificationModel

sealed interface AiGenerationState {
    object Idle : AiGenerationState
    object Loading : AiGenerationState
    data class Success(val generatedText: String) : AiGenerationState
    data class Error(val message: String) : AiGenerationState
}

class HeloViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: HeloRepository
    private val feedItemDao: FeedItemDao
    private val preferencesDataStore = UserPreferencesDataStore(application)
    private val aiGenerator = AiContentGenerator()

    val selectedLanguage = MutableStateFlow("All")
    val selectedCategory = MutableStateFlow("For You")
    val searchQuery = MutableStateFlow("")
    val activeTab = MutableStateFlow("home")

    val notificationsList = MutableStateFlow(
        listOf(
            NotificationModel(
                id = "n_1",
                category = NotificationCategoryFilter.LIKES,
                title = "Priya Sharma liked your post",
                message = "Liked: 'Morning motivation for all Helo creators! 🌅'",
                timeAgo = "10m ago",
                avatarUrl = "android.resource://com.aistudio.helo.app/drawable/img_avatar_default_1786202183590",
                isRead = false
            ),
            NotificationModel(
                id = "n_2",
                category = NotificationCategoryFilter.COMMENTS,
                title = "Rahul Verma commented on your photo",
                message = "\"Awesome picture brother! Keep sharing tech updates! 🔥\"",
                timeAgo = "25m ago",
                avatarUrl = "android.resource://com.aistudio.helo.app/drawable/img_avatar_default_1786202183590",
                isRead = false
            ),
            NotificationModel(
                id = "n_3",
                category = NotificationCategoryFilter.FOLLOWS,
                title = "Sneha Patel started following you",
                message = "Sneha (@sneha_art) is now following your Helo profile.",
                timeAgo = "45m ago",
                avatarUrl = "android.resource://com.aistudio.helo.app/drawable/img_avatar_default_1786202183590",
                isRead = false,
                isFollowingBack = false
            ),
            NotificationModel(
                id = "n_4",
                category = NotificationCategoryFilter.TRENDING,
                title = "#IndependenceDay2026 Trending #1 🔥",
                message = "Over 180K creators are posting under #IndependenceDay2026. Join the celebration!",
                timeAgo = "1h ago",
                isRead = false
            ),
            NotificationModel(
                id = "n_5",
                category = NotificationCategoryFilter.OFFICIAL,
                title = "Helo Official Announcement 🛡️",
                message = "Welcome to the Helo Creator Program! Your profile has been granted Creator badge.",
                timeAgo = "3h ago",
                isRead = true
            ),
            NotificationModel(
                id = "n_6",
                category = NotificationCategoryFilter.LIKES,
                title = "Cricket World and 24 others liked your reel",
                message = "Your short video reel 'Match Winning Highlights' is gaining traction!",
                timeAgo = "5h ago",
                isRead = true
            ),
            NotificationModel(
                id = "n_7",
                category = NotificationCategoryFilter.COMMENTS,
                title = "Tech Buzz commented on your post",
                message = "\"Great camera test! Which device recorded this?\"",
                timeAgo = "1d ago",
                isRead = true
            )
        )
    )

    init {
        val db = AppDatabase.getDatabase(application)
        repository = HeloRepository(db.heloDao())
        feedItemDao = db.feedItemDao()

        viewModelScope.launch {
            repository.initSeedDataIfEmpty()
            // Observe DataStore selected language preference
            preferencesDataStore.selectedLanguage.collect { lang ->
                selectedLanguage.value = lang
            }
        }
    }

    fun updateSelectedLanguage(language: String) {
        viewModelScope.launch {
            selectedLanguage.value = language
            preferencesDataStore.saveSelectedLanguage(language)
        }
    }

    val isRefreshingReels = MutableStateFlow(false)

    fun refreshReels() {
        viewModelScope.launch {
            isRefreshingReels.value = true
            kotlinx.coroutines.delay(1000)
            // Re-seed or randomize counts to simulate fetching fresh short video reels
            repository.incrementShare(videoReels.value.firstOrNull()?.id ?: "")
            isRefreshingReels.value = false
        }
    }

    val activePostForComments = MutableStateFlow<PostEntity?>(null)
    val activeSharePost = MutableStateFlow<PostEntity?>(null)
    val activeChatUser = MutableStateFlow<UserProfileEntity?>(null)
    val activeSelectedUserProfile = MutableStateFlow<UserProfileEntity?>(null)

    fun openUserProfile(authorId: String, authorName: String, authorAvatarUrl: String) {
        viewModelScope.launch {
            val profiles = userProfiles.value
            val existing = profiles.find { it.id == authorId || it.name.equals(authorName, ignoreCase = true) }
            if (existing != null) {
                activeSelectedUserProfile.value = existing
            } else {
                val newProfile = UserProfileEntity(
                    id = authorId.ifBlank { "u_${authorName.hashCode()}" },
                    username = authorName.lowercase().replace(" ", "_"),
                    name = authorName,
                    avatarUrl = authorAvatarUrl,
                    bio = "Helo Content Creator | Sharing updates & posts ✨",
                    isFollowing = false,
                    followersCount = 3420,
                    followingCount = 190,
                    postsCount = 12
                )
                activeSelectedUserProfile.value = newProfile
            }
        }
    }

    private val _aiGenerationState = MutableStateFlow<AiGenerationState>(AiGenerationState.Idle)
    val aiGenerationState: StateFlow<AiGenerationState> = _aiGenerationState.asStateFlow()

    // Reactive Feed based on category, language & search
    val feedPosts: StateFlow<List<PostEntity>> = combine(
        selectedCategory,
        selectedLanguage,
        searchQuery
    ) { category, language, query ->
        Triple(category, language, query)
    }.flatMapLatest { (category, language, query) ->
        combine(
            repository.getPostsByCategory(category),
            repository.getPostsByLanguage(language)
        ) { catPosts, langPosts ->
            val intersection = catPosts.filter { post ->
                (language == "All" || post.language.equals(language, ignoreCase = true)) &&
                (category == "For You" || post.category.equals(category, ignoreCase = true))
            }
            if (query.isBlank()) {
                intersection
            } else {
                intersection.filter {
                    it.content.contains(query, ignoreCase = true) ||
                    it.authorName.contains(query, ignoreCase = true) ||
                    it.hashtags.contains(query, ignoreCase = true)
                }
            }
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val videoReels: StateFlow<List<PostEntity>> = repository.videoReels
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val savedPosts: StateFlow<List<PostEntity>> = repository.savedPosts
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val myPosts: StateFlow<List<PostEntity>> = repository.myPosts
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val draftsList: StateFlow<List<PostDraftEntity>> = repository.allDrafts
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val userProfiles: StateFlow<List<UserProfileEntity>> = repository.userProfiles
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val currentUserProfile: StateFlow<UserProfileEntity?> = repository.currentUserProfile
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val activeComments: StateFlow<List<CommentEntity>> = activePostForComments
        .flatMapLatest { post ->
            if (post != null) repository.getComments(post.id)
            else MutableStateFlow(emptyList())
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val chatMessages = activeChatUser
        .flatMapLatest { user ->
            if (user != null) repository.getChatMessages(user.id)
            else MutableStateFlow(emptyList())
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun toggleLike(postId: String) {
        viewModelScope.launch {
            repository.toggleLike(postId)
        }
    }

    fun toggleSave(postId: String) {
        viewModelScope.launch {
            repository.toggleSave(postId)
        }
    }

    fun directShare(postId: String) {
        viewModelScope.launch {
            repository.incrementShare(postId)
        }
    }

    fun votePoll(postId: String, optionIndex: Int) {
        viewModelScope.launch {
            repository.votePoll(postId, optionIndex)
        }
    }

    fun addComment(postId: String, text: String) {
        viewModelScope.launch {
            repository.addComment(postId, text)
        }
    }

    fun sendChatMessage(text: String) {
        val user = activeChatUser.value ?: return
        viewModelScope.launch {
            repository.sendChatMessage(user.id, text)
        }
    }

    fun createPost(
        content: String,
        category: String,
        language: String,
        postType: String,
        bgGradientIndex: Int,
        imageUrl: String? = null,
        videoUrl: String? = null,
        videoThumbnail: String? = null,
        hashtags: String = ""
    ) {
        viewModelScope.launch {
            val me = currentUserProfile.value
            val newPost = PostEntity(
                id = "p_" + System.currentTimeMillis(),
                authorId = "user_me",
                authorName = me?.name ?: "Alex Johnson",
                authorAvatarUrl = me?.avatarUrl ?: "android.resource://com.aistudio.helo.app/drawable/img_avatar_default_1786202183590",
                authorBadge = if (!me?.badge.isNullOrBlank()) me.badge else "Creator",
                language = language,
                category = category,
                postType = postType,
                content = content,
                bgGradientIndex = bgGradientIndex,
                imageUrl = imageUrl,
                videoUrl = videoUrl,
                videoThumbnail = videoThumbnail,
                hashtags = hashtags,
                timestamp = System.currentTimeMillis()
            )
            repository.addPost(newPost)
        }
    }

    fun updateUserProfile(name: String, username: String, bio: String, avatarUrl: String, badge: String = "") {
        viewModelScope.launch {
            val current = currentUserProfile.value
            val updated = (current ?: UserProfileEntity(
                id = "user_me",
                username = "creator_alex",
                name = "Alex Johnson",
                avatarUrl = "android.resource://com.aistudio.helo.app/drawable/img_avatar_default_1786202183590",
                bio = "Content Creator | Tech & Travel Explorer 🚀",
                followersCount = 2840,
                followingCount = 310,
                badge = ""
            )).copy(
                name = name.ifBlank { "Alex Johnson" },
                username = username.ifBlank { "creator_alex" }.removePrefix("@"),
                bio = bio,
                avatarUrl = avatarUrl.ifBlank { "android.resource://com.aistudio.helo.app/drawable/img_avatar_default_1786202183590" },
                badge = badge
            )
            repository.updateUserProfile(updated)
        }
    }

    fun generateAiContent(topic: String, category: String, language: String) {
        viewModelScope.launch {
            _aiGenerationState.value = AiGenerationState.Loading
            try {
                val generated = aiGenerator.generateStatusOrQuote(topic, category, language)
                _aiGenerationState.value = AiGenerationState.Success(generated)
            } catch (e: Exception) {
                _aiGenerationState.value = AiGenerationState.Error(e.localizedMessage ?: "Generation failed")
            }
        }
    }

    fun resetAiState() {
        _aiGenerationState.value = AiGenerationState.Idle
    }

    fun markAllNotificationsRead() {
        notificationsList.value = notificationsList.value.map { it.copy(isRead = true) }
    }

    fun markNotificationRead(id: String) {
        notificationsList.value = notificationsList.value.map {
            if (it.id == id) it.copy(isRead = true) else it
        }
    }

    fun toggleFollowBackInNotification(id: String) {
        notificationsList.value = notificationsList.value.map {
            if (it.id == id) it.copy(isFollowingBack = !it.isFollowingBack, isRead = true) else it
        }
    }

    fun clearNotification(id: String) {
        notificationsList.value = notificationsList.value.filter { it.id != id }
    }

    fun deletePost(postId: String) {
        viewModelScope.launch {
            repository.deletePost(postId)
        }
    }

    fun editPost(postId: String, newContent: String, newHashtags: String) {
        viewModelScope.launch {
            val existing = feedPosts.value.find { it.id == postId } ?: myPosts.value.find { it.id == postId }
            if (existing != null) {
                repository.updatePost(existing.copy(content = newContent, hashtags = newHashtags))
            }
        }
    }

    fun toggleFollowUser(userId: String) {
        viewModelScope.launch {
            repository.toggleFollowUser(userId)
            val currentModal = activeSelectedUserProfile.value
            if (currentModal != null && currentModal.id == userId) {
                activeSelectedUserProfile.value = currentModal.copy(isFollowing = !currentModal.isFollowing)
            }
        }
    }

    fun saveDraft(draft: PostDraftEntity) {
        viewModelScope.launch {
            repository.saveDraft(draft)
        }
    }

    fun deleteDraft(id: String) {
        viewModelScope.launch {
            repository.deleteDraft(id)
        }
    }
}
