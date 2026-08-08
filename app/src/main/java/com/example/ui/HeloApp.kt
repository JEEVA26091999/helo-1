package com.example.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PlayCircle
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.data.PostEntity
import com.example.data.UserProfileEntity
import com.example.ui.components.CommentBottomSheet
import com.example.ui.components.HeloTopBar
import com.example.ui.components.LanguageSelectorDialog
import com.example.ui.components.ShareDialog
import com.example.ui.components.UserProfileDialog
import com.example.ui.screens.ChatDetailScreen
import com.example.ui.screens.NotificationCategoryFilter
import com.example.ui.screens.NotificationsScreen
import com.example.ui.screens.ChatListScreen
import com.example.ui.screens.CreatePostScreen
import com.example.ui.screens.FeedScreen
import com.example.ui.screens.ProfileScreen
import com.example.ui.screens.TrendingScreen
import com.example.ui.screens.VideoFeedScreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HeloApp(
    viewModel: HeloViewModel = viewModel()
) {
    val activeTab by viewModel.activeTab.collectAsState()
    val selectedLanguage by viewModel.selectedLanguage.collectAsState()
    val selectedCategory by viewModel.selectedCategory.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()

    val feedPosts by viewModel.feedPosts.collectAsState()
    val videoReels by viewModel.videoReels.collectAsState()
    val isRefreshingReels by viewModel.isRefreshingReels.collectAsState()
    val savedPosts by viewModel.savedPosts.collectAsState()
    val myPosts by viewModel.myPosts.collectAsState()
    val draftsList by viewModel.draftsList.collectAsState()
    val userProfiles by viewModel.userProfiles.collectAsState()
    val currentUserProfile by viewModel.currentUserProfile.collectAsState()

    val activeComments by viewModel.activeComments.collectAsState()
    val activePostForComments by viewModel.activePostForComments.collectAsState()
    val activeSharePost by viewModel.activeSharePost.collectAsState()
    val activeChatUser by viewModel.activeChatUser.collectAsState()
    val activeSelectedUserProfile by viewModel.activeSelectedUserProfile.collectAsState()
    val chatMessages by viewModel.chatMessages.collectAsState()

    val aiState by viewModel.aiGenerationState.collectAsState()
    val notificationsList by viewModel.notificationsList.collectAsState()
    val unreadNotificationsCount = remember(notificationsList) { notificationsList.count { !it.isRead } }

    var showLanguageDialog by remember { mutableStateOf(false) }

    val commentSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    Scaffold(
        topBar = {
            if (activeTab != "create" && activeChatUser == null) {
                HeloTopBar(
                    selectedLanguage = selectedLanguage,
                    unreadNotificationCount = unreadNotificationsCount,
                    onLanguageClick = { lang -> viewModel.updateSelectedLanguage(lang) },
                    onSearchClick = { viewModel.activeTab.value = "trending" },
                    onNotificationClick = { viewModel.activeTab.value = "notifications" },
                    onWhatsAppStatusClick = {
                        if (feedPosts.isNotEmpty()) {
                            viewModel.activeSharePost.value = feedPosts.first()
                        }
                    },
                    onAddPostClick = { viewModel.activeTab.value = "create" }
                )
            }
        },
        bottomBar = {
            if (activeChatUser == null) {
                NavigationBar(
                    containerColor = MaterialTheme.colorScheme.surface,
                    modifier = Modifier.testTag("helo_bottom_navigation")
                ) {
                    NavigationBarItem(
                        selected = activeTab == "home",
                        onClick = { viewModel.activeTab.value = "home" },
                        icon = { Icon(imageVector = Icons.Default.Home, contentDescription = "Home") },
                        label = { Text("Home") },
                        modifier = Modifier.testTag("nav_tab_home")
                    )
                    NavigationBarItem(
                        selected = activeTab == "reels",
                        onClick = { viewModel.activeTab.value = "reels" },
                        icon = { Icon(imageVector = Icons.Default.PlayCircle, contentDescription = "Reels") },
                        label = { Text("Reels") },
                        modifier = Modifier.testTag("nav_tab_reels")
                    )
                    NavigationBarItem(
                        selected = activeTab == "trending",
                        onClick = { viewModel.activeTab.value = "trending" },
                        icon = { Icon(imageVector = Icons.Default.TrendingUp, contentDescription = "Trending") },
                        label = { Text("Trending") },
                        modifier = Modifier.testTag("nav_tab_trending")
                    )
                    NavigationBarItem(
                        selected = activeTab == "create",
                        onClick = { viewModel.activeTab.value = "create" },
                        icon = {
                            FloatingActionButton(
                                onClick = { viewModel.activeTab.value = "create" },
                                containerColor = MaterialTheme.colorScheme.primary,
                                shape = CircleShape
                            ) {
                                Icon(imageVector = Icons.Default.Add, contentDescription = "Create", tint = Color.White)
                            }
                        },
                        label = { Text("Create") },
                        modifier = Modifier.testTag("nav_tab_create")
                    )
                    NavigationBarItem(
                        selected = activeTab == "chat",
                        onClick = { viewModel.activeTab.value = "chat" },
                        icon = { Icon(imageVector = Icons.Default.Chat, contentDescription = "Chat") },
                        label = { Text("Chat") },
                        modifier = Modifier.testTag("nav_tab_chat")
                    )
                    NavigationBarItem(
                        selected = activeTab == "profile",
                        onClick = { viewModel.activeTab.value = "profile" },
                        icon = { Icon(imageVector = Icons.Default.Person, contentDescription = "Profile") },
                        label = { Text("Profile") },
                        modifier = Modifier.testTag("nav_tab_profile")
                    )
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when {
                activeChatUser != null -> {
                    ChatDetailScreen(
                        user = activeChatUser!!,
                        messages = chatMessages,
                        onSendMessage = { text -> viewModel.sendChatMessage(text) },
                        onBackClick = { viewModel.activeChatUser.value = null }
                    )
                }

                activeTab == "home" -> {
                    FeedScreen(
                        posts = feedPosts,
                        selectedCategory = selectedCategory,
                        selectedLanguage = selectedLanguage,
                        onCategorySelect = { cat -> viewModel.selectedCategory.value = cat },
                        onLikeClick = { postId -> viewModel.toggleLike(postId) },
                        onCommentClick = { post -> viewModel.activePostForComments.value = post },
                        onShareClick = { post -> viewModel.activeSharePost.value = post },
                        onSaveClick = { postId -> viewModel.toggleSave(postId) },
                        onPollVote = { postId, idx -> viewModel.votePoll(postId, idx) },
                        onAuthorClick = { id, name, avatar -> viewModel.openUserProfile(id, name, avatar) },
                        onVideoPlayClick = { viewModel.activeTab.value = "reels" },
                        onCreateAiQuoteClick = { viewModel.activeTab.value = "create" }
                    )
                }

                activeTab == "reels" -> {
                    VideoFeedScreen(
                        videoReels = videoReels,
                        isRefreshing = isRefreshingReels,
                        onRefresh = { viewModel.refreshReels() },
                        onLikeClick = { postId -> viewModel.toggleLike(postId) },
                        onCommentClick = { post -> viewModel.activePostForComments.value = post },
                        onShareClick = { post -> viewModel.activeSharePost.value = post },
                        onSaveClick = { postId -> viewModel.toggleSave(postId) },
                        onAuthorClick = { id, name, avatar -> viewModel.openUserProfile(id, name, avatar) }
                    )
                }

                activeTab == "trending" -> {
                    TrendingScreen(
                        searchQuery = searchQuery,
                        onSearchChange = { q -> viewModel.searchQuery.value = q },
                        posts = feedPosts,
                        onLikeClick = { postId -> viewModel.toggleLike(postId) },
                        onCommentClick = { post -> viewModel.activePostForComments.value = post },
                        onShareClick = { post -> viewModel.activeSharePost.value = post },
                        onSaveClick = { postId -> viewModel.toggleSave(postId) },
                        onPollVote = { postId, idx -> viewModel.votePoll(postId, idx) },
                        onAuthorClick = { id, name, avatar -> viewModel.openUserProfile(id, name, avatar) }
                    )
                }

                activeTab == "create" -> {
                    CreatePostScreen(
                        aiState = aiState,
                        drafts = draftsList,
                        onGenerateAi = { topic, cat, lang -> viewModel.generateAiContent(topic, cat, lang) },
                        onPublish = { content, cat, lang, type, bgIdx, imgUrl, videoUrl, videoThumbUrl, hashtags ->
                            viewModel.createPost(content, cat, lang, type, bgIdx, imgUrl, videoUrl, videoThumbUrl, hashtags)
                        },
                        onSaveDraft = { draft -> viewModel.saveDraft(draft) },
                        onDeleteDraft = { id -> viewModel.deleteDraft(id) },
                        onPublishSuccess = { viewModel.activeTab.value = "home" }
                    )
                }

                activeTab == "chat" -> {
                    ChatListScreen(
                        userProfiles = userProfiles,
                        onSelectUser = { user -> viewModel.activeChatUser.value = user }
                    )
                }

                activeTab == "notifications" -> {
                    NotificationsScreen(
                        notifications = notificationsList,
                        onMarkAllRead = { viewModel.markAllNotificationsRead() },
                        onNotificationClick = { notification ->
                            viewModel.markNotificationRead(notification.id)
                            if (notification.category == NotificationCategoryFilter.TRENDING) {
                                viewModel.activeTab.value = "trending"
                            }
                        },
                        onFollowToggle = { id -> viewModel.toggleFollowBackInNotification(id) },
                        onClearNotification = { id -> viewModel.clearNotification(id) },
                        onNavigateToTrending = { viewModel.activeTab.value = "trending" },
                        onNavigateToCreate = { viewModel.activeTab.value = "create" }
                    )
                }

                activeTab == "profile" -> {
                    ProfileScreen(
                        myPosts = myPosts,
                        savedPosts = savedPosts,
                        selectedLanguage = selectedLanguage,
                        currentUserProfile = currentUserProfile,
                        onLanguageClick = { showLanguageDialog = true },
                        onLikeClick = { postId -> viewModel.toggleLike(postId) },
                        onCommentClick = { post -> viewModel.activePostForComments.value = post },
                        onShareClick = { post -> viewModel.activeSharePost.value = post },
                        onSaveClick = { postId -> viewModel.toggleSave(postId) },
                        onPollVote = { postId, idx -> viewModel.votePoll(postId, idx) },
                        onDeletePost = { postId -> viewModel.deletePost(postId) },
                        onEditPost = { postId, newContent, newHashtags ->
                            viewModel.editPost(postId, newContent, newHashtags)
                        },
                        onCreatePostClick = { viewModel.activeTab.value = "create" },
                        onSaveProfile = { name, username, bio, avatarUrl, creatorType ->
                            viewModel.updateUserProfile(name, username, bio, avatarUrl, creatorType)
                        }
                    )
                }
            }
        }

        // Language Modal
        if (showLanguageDialog) {
            LanguageSelectorDialog(
                selectedLanguage = selectedLanguage,
                onLanguageSelected = { lang -> viewModel.updateSelectedLanguage(lang) },
                onDismiss = { showLanguageDialog = false }
            )
        }

        // Comment Sheet
        if (activePostForComments != null) {
            CommentBottomSheet(
                sheetState = commentSheetState,
                comments = activeComments,
                onSendComment = { text -> viewModel.addComment(activePostForComments!!.id, text) },
                onDismiss = { viewModel.activePostForComments.value = null }
            )
        }

        // Share Dialog
        if (activeSharePost != null) {
            ShareDialog(
                postContent = activeSharePost!!.content,
                onDirectShare = { viewModel.directShare(activeSharePost!!.id) },
                onDismiss = { viewModel.activeSharePost.value = null }
            )
        }

        // User Profile Modal Dialog
        if (activeSelectedUserProfile != null) {
            val authorPosts = feedPosts.filter {
                it.authorId == activeSelectedUserProfile!!.id || it.authorName.equals(activeSelectedUserProfile!!.name, ignoreCase = true)
            }
            UserProfileDialog(
                userProfile = activeSelectedUserProfile!!,
                authorPosts = authorPosts,
                onFollowToggle = { viewModel.toggleFollowUser(activeSelectedUserProfile!!.id) },
                onMessageClick = {
                    val targetUser = activeSelectedUserProfile!!
                    viewModel.activeSelectedUserProfile.value = null
                    viewModel.activeChatUser.value = targetUser
                    viewModel.activeTab.value = "chat"
                },
                onLikeClick = { postId -> viewModel.toggleLike(postId) },
                onCommentClick = { post -> viewModel.activePostForComments.value = post },
                onShareClick = { post -> viewModel.activeSharePost.value = post },
                onSaveClick = { postId -> viewModel.toggleSave(postId) },
                onDismiss = { viewModel.activeSelectedUserProfile.value = null }
            )
        }
    }
}
