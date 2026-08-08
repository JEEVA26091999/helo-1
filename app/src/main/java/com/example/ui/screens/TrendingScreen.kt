package com.example.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.R
import com.example.data.PostEntity
import com.example.ui.components.MOCK_TRENDING_TOPICS
import com.example.ui.components.PostItemCard
import com.example.ui.components.TrendingTopicsList

data class TrendingTopic(val hashtag: String, val category: String, val postsCount: String)

val TOP_TRENDING = listOf(
    TrendingTopic("#Cricket2026", "Sports", "142.5K posts"),
    TrendingTopic("#MorningQuotes", "Quotes & Status", "89.2K posts"),
    TrendingTopic("#SuvicharHindi", "Status", "64.1K posts"),
    TrendingTopic("#BollywoodNews", "Entertainment", "112.8K posts"),
    TrendingTopic("#HeloTamil", "Regional Feed", "45.0K posts"),
    TrendingTopic("#ViralReels", "Videos", "210.4K posts")
)

@Composable
fun TrendingScreen(
    searchQuery: String,
    onSearchChange: (String) -> Unit,
    posts: List<PostEntity>,
    onLikeClick: (String) -> Unit,
    onCommentClick: (PostEntity) -> Unit,
    onShareClick: (PostEntity) -> Unit,
    onSaveClick: (String) -> Unit,
    onPollVote: (String, Int) -> Unit,
    onAuthorClick: (String, String, String) -> Unit = { _, _, _ -> }
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .testTag("trending_screen")
    ) {
        // Search Input Bar
        OutlinedTextField(
            value = searchQuery,
            onValueChange = onSearchChange,
            placeholder = { Text("Search status, creators, quotes or #hashtags...") },
            leadingIcon = { Icon(imageVector = Icons.Default.Search, contentDescription = "Search") },
            trailingIcon = {
                if (searchQuery.isNotEmpty()) {
                    IconButton(onClick = { onSearchChange("") }) {
                        Icon(imageVector = Icons.Default.Clear, contentDescription = "Clear")
                    }
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
                .testTag("search_input_field"),
            shape = RoundedCornerShape(24.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = MaterialTheme.colorScheme.outline
            ),
            singleLine = true
        )

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = 80.dp)
        ) {
            // Helo Banner Graphic
            item {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 4.dp)
                ) {
                    Box {
                        Image(
                            painter = painterResource(id = R.drawable.img_helo_banner_1786202158870),
                            contentDescription = "Trending Banner",
                            modifier = Modifier
                                .fillMaxWidth()
                                .aspectRatio(16f / 9f),
                            contentScale = ContentScale.Crop
                        )
                        Box(
                            modifier = Modifier
                                .align(Alignment.BottomStart)
                                .padding(12.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color.Black.copy(alpha = 0.7f))
                                .padding(horizontal = 10.dp, vertical = 6.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.LocalFireDepartment,
                                    contentDescription = "Trending",
                                    tint = Color(0xFFFF5722),
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "Helo Hot Trends 2026",
                                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                    color = Color.White
                                )
                            }
                        }
                    }
                }
            }

            // Trending Topics & Tags List Section
            item {
                TrendingTopicsList(
                    topics = MOCK_TRENDING_TOPICS,
                    onTopicClick = { hashtag -> onSearchChange(hashtag) },
                    modifier = Modifier.height(420.dp)
                )
            }

            // Trending Posts Header
            item {
                Text(
                    text = if (searchQuery.isBlank()) "Trending Posts Feed" else "Search Results for '$searchQuery'",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                )
            }

            // Filtered Posts
            items(posts, key = { it.id }) { post ->
                PostItemCard(
                    post = post,
                    onLikeClick = { onLikeClick(post.id) },
                    onCommentClick = { onCommentClick(post) },
                    onShareClick = { onShareClick(post) },
                    onSaveClick = { onSaveClick(post.id) },
                    onPollVote = { optionIdx -> onPollVote(post.id, optionIdx) },
                    onAuthorClick = { onAuthorClick(post.authorId, post.authorName, post.authorAvatarUrl) },
                    onVideoPlayClick = { }
                )
            }
        }
    }
}
