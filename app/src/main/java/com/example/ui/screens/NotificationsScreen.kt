package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Campaign
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Comment
import androidx.compose.material.icons.filled.DoneAll
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage

enum class NotificationCategoryFilter {
    ALL, LIKES, COMMENTS, FOLLOWS, OFFICIAL, TRENDING
}

data class NotificationModel(
    val id: String,
    val category: NotificationCategoryFilter,
    val title: String,
    val message: String,
    val timeAgo: String,
    val avatarUrl: String? = null,
    val isRead: Boolean = false,
    val isFollowingBack: Boolean = false
)

@Composable
fun NotificationsScreen(
    notifications: List<NotificationModel>,
    onMarkAllRead: () -> Unit,
    onNotificationClick: (NotificationModel) -> Unit,
    onFollowToggle: (String) -> Unit,
    onClearNotification: (String) -> Unit,
    onNavigateToTrending: () -> Unit,
    onNavigateToCreate: () -> Unit
) {
    var selectedFilter by remember { mutableStateOf(NotificationCategoryFilter.ALL) }

    val filteredList = remember(notifications, selectedFilter) {
        if (selectedFilter == NotificationCategoryFilter.ALL) {
            notifications
        } else {
            notifications.filter { it.category == selectedFilter }
        }
    }

    val unreadCount = remember(notifications) {
        notifications.count { !it.isRead }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .testTag("notifications_screen")
    ) {
        // Top Header
        Card(
            shape = RoundedCornerShape(bottomStart = 20.dp, bottomEnd = 20.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Notifications,
                            contentDescription = "Notification Center",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(28.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "Notification Center",
                                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                            )
                            Text(
                                text = if (unreadCount > 0) "$unreadCount unread updates" else "All caught up!",
                                style = MaterialTheme.typography.bodySmall,
                                color = if (unreadCount > 0) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    if (unreadCount > 0) {
                        OutlinedButton(
                            onClick = onMarkAllRead,
                            shape = RoundedCornerShape(20.dp),
                            modifier = Modifier.testTag("mark_all_read_button")
                        ) {
                            Icon(imageVector = Icons.Default.DoneAll, contentDescription = "Mark Read", modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Mark Read", style = MaterialTheme.typography.labelSmall)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Category Filter Chips (All, Likes, Comments, Follows, Official, Trending)
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(NotificationCategoryFilter.values()) { filter ->
                        val isSelected = selectedFilter == filter
                        val label = when (filter) {
                            NotificationCategoryFilter.ALL -> "All"
                            NotificationCategoryFilter.LIKES -> "Likes ❤️"
                            NotificationCategoryFilter.COMMENTS -> "Comments 💬"
                            NotificationCategoryFilter.FOLLOWS -> "Follows 👤"
                            NotificationCategoryFilter.OFFICIAL -> "Official 🛡️"
                            NotificationCategoryFilter.TRENDING -> "Trending 🔥"
                        }

                        FilterChip(
                            selected = isSelected,
                            onClick = { selectedFilter = filter },
                            label = {
                                Text(
                                    text = label,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                                )
                            },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = MaterialTheme.colorScheme.primary,
                                selectedLabelColor = Color.White
                            ),
                            shape = RoundedCornerShape(20.dp),
                            modifier = Modifier.testTag("filter_chip_${filter.name.lowercase()}")
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Notifications List
        if (filteredList.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Default.Notifications,
                        contentDescription = "Empty",
                        modifier = Modifier.size(64.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "No notifications found in this category",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(6.dp),
                contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 12.dp, vertical = 6.dp)
            ) {
                items(filteredList, key = { it.id }) { item ->
                    NotificationCardItem(
                        item = item,
                        onClick = { onNotificationClick(item) },
                        onFollowToggle = { onFollowToggle(item.id) },
                        onClear = { onClearNotification(item.id) },
                        onNavigateToTrending = onNavigateToTrending,
                        onNavigateToCreate = onNavigateToCreate
                    )
                }
            }
        }
    }
}

@Composable
fun NotificationCardItem(
    item: NotificationModel,
    onClick: () -> Unit,
    onFollowToggle: () -> Unit,
    onClear: () -> Unit,
    onNavigateToTrending: () -> Unit,
    onNavigateToCreate: () -> Unit
) {
    val (icon, iconColor) = getNotificationIconAndColor(item.category)

    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (!item.isRead) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.25f)
            else MaterialTheme.colorScheme.surface
        ),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .testTag("notification_item_${item.id}")
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Left Avatar or Category Icon
            Box(contentAlignment = Alignment.BottomEnd) {
                if (!item.avatarUrl.isNullOrEmpty()) {
                    AsyncImage(
                        model = item.avatarUrl,
                        contentDescription = item.title,
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.surfaceVariant),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                            .background(iconColor.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(imageVector = icon, contentDescription = null, tint = iconColor)
                    }
                }

                // Small badge icon overlay
                Box(
                    modifier = Modifier
                        .size(18.dp)
                        .clip(CircleShape)
                        .background(iconColor),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(11.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            // Main Content Area
            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = item.title,
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = item.timeAgo,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Spacer(modifier = Modifier.height(2.dp))

                Text(
                    text = item.message,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                // Contextual Action Buttons
                when (item.category) {
                    NotificationCategoryFilter.FOLLOWS -> {
                        Spacer(modifier = Modifier.height(8.dp))
                        Button(
                            onClick = onFollowToggle,
                            shape = RoundedCornerShape(16.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (item.isFollowingBack) MaterialTheme.colorScheme.surfaceVariant else MaterialTheme.colorScheme.primary,
                                contentColor = if (item.isFollowingBack) MaterialTheme.colorScheme.onSurfaceVariant else Color.White
                            ),
                            modifier = Modifier
                                .height(32.dp)
                                .testTag("follow_back_button_${item.id}")
                        ) {
                            Icon(
                                imageVector = if (item.isFollowingBack) Icons.Default.Check else Icons.Default.PersonAdd,
                                contentDescription = null,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = if (item.isFollowingBack) "Following" else "Follow Back",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    NotificationCategoryFilter.TRENDING -> {
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Button(
                                onClick = onNavigateToTrending,
                                shape = RoundedCornerShape(16.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                                modifier = Modifier
                                    .height(32.dp)
                                    .testTag("explore_trend_button_${item.id}")
                            ) {
                                Icon(imageVector = Icons.Default.TrendingUp, contentDescription = null, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Explore Trend", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
                            }

                            OutlinedButton(
                                onClick = onNavigateToCreate,
                                shape = RoundedCornerShape(16.dp),
                                modifier = Modifier.height(32.dp)
                            ) {
                                Text("Post Now", style = MaterialTheme.typography.labelSmall)
                            }
                        }
                    }

                    else -> {}
                }
            }

            Spacer(modifier = Modifier.width(6.dp))

            // Dismiss/Clear Action
            IconButton(onClick = onClear, modifier = Modifier.size(24.dp)) {
                Icon(
                    imageVector = Icons.Outlined.Delete,
                    contentDescription = "Delete",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}

@Composable
private fun getNotificationIconAndColor(category: NotificationCategoryFilter): Pair<ImageVector, Color> {
    return when (category) {
        NotificationCategoryFilter.ALL -> Icons.Default.Notifications to MaterialTheme.colorScheme.primary
        NotificationCategoryFilter.LIKES -> Icons.Default.Favorite to Color(0xFFE91E63)
        NotificationCategoryFilter.COMMENTS -> Icons.Default.Comment to Color(0xFF00BCD4)
        NotificationCategoryFilter.FOLLOWS -> Icons.Default.PersonAdd to Color(0xFF3F51B5)
        NotificationCategoryFilter.OFFICIAL -> Icons.Default.Campaign to Color(0xFFFF9800)
        NotificationCategoryFilter.TRENDING -> Icons.Default.TrendingUp to Color(0xFF4CAF50)
    }
}
