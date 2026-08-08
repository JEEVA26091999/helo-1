package com.example.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Article
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.PostAdd
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.data.PostEntity
import com.example.data.UserProfileEntity
import com.example.ui.components.EditPostDialog
import com.example.ui.components.EditProfileDialog
import com.example.ui.components.PostItemCard

@Composable
fun ProfileScreen(
    myPosts: List<PostEntity> = emptyList(),
    savedPosts: List<PostEntity> = emptyList(),
    selectedLanguage: String,
    currentUserProfile: UserProfileEntity? = null,
    onLanguageClick: () -> Unit,
    onLikeClick: (String) -> Unit,
    onCommentClick: (PostEntity) -> Unit,
    onShareClick: (PostEntity) -> Unit,
    onSaveClick: (String) -> Unit,
    onPollVote: (String, Int) -> Unit,
    onDeletePost: (String) -> Unit = {},
    onEditPost: (postId: String, newContent: String, newHashtags: String) -> Unit = { _, _, _ -> },
    onCreatePostClick: () -> Unit = {},
    onSaveProfile: (name: String, username: String, bio: String, avatarUrl: String, creatorType: String) -> Unit = { _, _, _, _, _ -> }
) {
    val context = LocalContext.current
    var selectedTab by remember { mutableIntStateOf(0) }
    var showEditProfileDialog by remember { mutableStateOf(false) }

    var editingPost by remember { mutableStateOf<PostEntity?>(null) }
    var deletingPostId by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .testTag("profile_screen")
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = androidx.compose.foundation.layout.PaddingValues(bottom = 80.dp)
        ) {
            // Profile Card Header
            item {
                val profile = currentUserProfile ?: UserProfileEntity(
                    id = "user_me",
                    username = "creator_alex",
                    name = "Alex Johnson",
                    avatarUrl = "android.resource://com.aistudio.helo.app/drawable/img_avatar_default_1786202183590",
                    bio = "Content Creator | Tech, Daily Motivation & Travel Explorer 🚀",
                    followersCount = 2840,
                    followingCount = 310,
                    badge = "Verified Creator"
                )

                Card(
                    shape = RoundedCornerShape(bottomStart = 24.dp, bottomEnd = 24.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        AsyncImage(
                            model = profile.avatarUrl,
                            contentDescription = "Profile Avatar",
                            modifier = Modifier
                                .size(80.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.surface),
                            contentScale = ContentScale.Crop
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = profile.name,
                                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                            )
                            if (profile.badge.isNotBlank()) {
                                Spacer(modifier = Modifier.width(6.dp))
                                Icon(
                                    imageVector = Icons.Default.CheckCircle,
                                    contentDescription = "Verified Creator",
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(22.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                androidx.compose.material3.Surface(
                                    shape = RoundedCornerShape(12.dp),
                                    color = MaterialTheme.colorScheme.primaryContainer,
                                    modifier = Modifier.testTag("creator_type_badge")
                                ) {
                                    Text(
                                        text = profile.badge,
                                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                    )
                                }
                            }
                        }
                        Text(
                            text = "@${profile.username}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = profile.bio,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        OutlinedButton(
                            onClick = { showEditProfileDialog = true },
                            shape = RoundedCornerShape(20.dp),
                            modifier = Modifier.testTag("edit_profile_button")
                        ) {
                            Icon(imageVector = Icons.Default.Edit, contentDescription = "Edit Profile", modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Edit Profile", fontWeight = FontWeight.Bold)
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Stats Row
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            StatItem("Followers", if (profile.followersCount >= 1000) "${profile.followersCount / 1000}K" else "${profile.followersCount}")
                            StatItem("Following", "${profile.followingCount}")
                            StatItem("My Posts", "${myPosts.size}")
                        }
                    }
                }

                if (showEditProfileDialog) {
                    EditProfileDialog(
                        userProfile = profile,
                        onSave = { newName, newUsername, newBio, newAvatarUrl, newCreatorType ->
                            onSaveProfile(newName, newUsername, newBio, newAvatarUrl, newCreatorType)
                        },
                        onDismiss = { showEditProfileDialog = false }
                    )
                }
            }

            // Language Preference Setting Item
            item {
                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Language,
                                contentDescription = "Lang",
                                tint = MaterialTheme.colorScheme.primary
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = "Preferred Language",
                                    style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold)
                                )
                                Text(
                                    text = selectedLanguage,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                        androidx.compose.material3.OutlinedButton(onClick = onLanguageClick) {
                            Text("Change")
                        }
                    }
                }
            }

            // Tabs for Published Posts & Saved Posts
            item {
                TabRow(
                    selectedTabIndex = selectedTab,
                    containerColor = MaterialTheme.colorScheme.surface,
                    modifier = Modifier.padding(horizontal = 12.dp)
                ) {
                    Tab(
                        selected = selectedTab == 0,
                        onClick = { selectedTab = 0 },
                        text = {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(imageVector = Icons.Default.Article, contentDescription = "My Posts")
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Published Posts (${myPosts.size})")
                            }
                        },
                        modifier = Modifier.testTag("tab_published_posts")
                    )
                    Tab(
                        selected = selectedTab == 1,
                        onClick = { selectedTab = 1 },
                        text = {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(imageVector = Icons.Default.Bookmark, contentDescription = "Saved")
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Saved Posts (${savedPosts.size})")
                            }
                        },
                        modifier = Modifier.testTag("tab_saved_posts")
                    )
                }
            }

            // TAB 0: Published Posts
            if (selectedTab == 0) {
                if (myPosts.isEmpty()) {
                    item {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 40.dp, horizontal = 20.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                imageVector = Icons.Default.PostAdd,
                                contentDescription = "No Posts",
                                modifier = Modifier.size(54.dp),
                                tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.6f)
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = "No published posts yet.",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Publish status, quotes, images, or video reels to share with the Helo community!",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Button(
                                onClick = onCreatePostClick,
                                shape = RoundedCornerShape(20.dp)
                            ) {
                                Text("Create First Post")
                            }
                        }
                    }
                } else {
                    items(myPosts, key = { it.id }) { post ->
                        PostItemCard(
                            post = post,
                            onLikeClick = { onLikeClick(post.id) },
                            onCommentClick = { onCommentClick(post) },
                            onShareClick = { onShareClick(post) },
                            onSaveClick = { onSaveClick(post.id) },
                            onPollVote = { optionIdx -> onPollVote(post.id, optionIdx) },
                            onAuthorClick = { },
                            onVideoPlayClick = { },
                            onEditClick = { editingPost = post },
                            onDeleteClick = { deletingPostId = post.id }
                        )
                    }
                }
            } else {
                // TAB 1: Saved Posts
                if (savedPosts.isEmpty()) {
                    item {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 40.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "No saved posts yet.",
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = "Tap the bookmark icon on any status or video reel to save it here!",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                } else {
                    items(savedPosts, key = { it.id }) { post ->
                        PostItemCard(
                            post = post,
                            onLikeClick = { onLikeClick(post.id) },
                            onCommentClick = { onCommentClick(post) },
                            onShareClick = { onShareClick(post) },
                            onSaveClick = { onSaveClick(post.id) },
                            onPollVote = { optionIdx -> onPollVote(post.id, optionIdx) },
                            onAuthorClick = { },
                            onVideoPlayClick = { }
                        )
                    }
                }
            }
        }
    }

    // Edit Post Dialog
    editingPost?.let { post ->
        EditPostDialog(
            post = post,
            onSave = { newContent, newHashtags ->
                onEditPost(post.id, newContent, newHashtags)
                editingPost = null
            },
            onDismiss = { editingPost = null }
        )
    }

    // Delete Post Confirmation Dialog
    deletingPostId?.let { id ->
        AlertDialog(
            onDismissRequest = { deletingPostId = null },
            title = { Text("Delete Published Post") },
            text = { Text("Are you sure you want to delete this post? This action cannot be undone.") },
            confirmButton = {
                Button(
                    onClick = {
                        onDeletePost(id)
                        Toast.makeText(context, "Post deleted", Toast.LENGTH_SHORT).show()
                        deletingPostId = null
                    }
                ) {
                    Text("Delete")
                }
            },
            dismissButton = {
                TextButton(onClick = { deletingPostId = null }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
private fun StatItem(label: String, count: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = count,
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
        )
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
