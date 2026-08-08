package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Tag
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

data class DummyTrendingTopic(
    val id: String,
    val hashtag: String,
    val category: String,
    val postsCount: String,
    val isHot: Boolean = false,
    val description: String
)

val MOCK_TRENDING_TOPICS = listOf(
    DummyTrendingTopic(
        id = "t1",
        hashtag = "#Cricket2026",
        category = "Sports • Trending in India",
        postsCount = "184.2K posts",
        isHot = true,
        description = "Live match highlights, player quotes and match status updates."
    ),
    DummyTrendingTopic(
        id = "t2",
        hashtag = "#MorningQuotes",
        category = "Quotes & Suvichar",
        postsCount = "120.5K posts",
        isHot = true,
        description = "Inspirational good morning quotes and daily motivation images."
    ),
    DummyTrendingTopic(
        id = "t3",
        hashtag = "#ViralReels2026",
        category = "Video Feed",
        postsCount = "95.8K posts",
        isHot = true,
        description = "Trending short video reels, funny clips, and dance covers."
    ),
    DummyTrendingTopic(
        id = "t4",
        hashtag = "#BollywoodUpdates",
        category = "Entertainment",
        postsCount = "88.4K posts",
        isHot = false,
        description = "New movie teaser launches, celebrity statuses, and box office news."
    ),
    DummyTrendingTopic(
        id = "t5",
        hashtag = "#TechInnovations",
        category = "Technology",
        postsCount = "64.1K posts",
        isHot = false,
        description = "AI advancements, Android 16 updates, and gadget reviews."
    ),
    DummyTrendingTopic(
        id = "t6",
        hashtag = "#FestivalStatus",
        category = "Culture & Regional",
        postsCount = "52.9K posts",
        isHot = false,
        description = "Festive greetings, devotional quotes, and custom wishes cards."
    ),
    DummyTrendingTopic(
        id = "t7",
        hashtag = "#DailySuvicharHindi",
        category = "Language Feeds",
        postsCount = "48.2K posts",
        isHot = false,
        description = "Heart-touching Hindi thought quotes and life status updates."
    ),
    DummyTrendingTopic(
        id = "t8",
        hashtag = "#FitnessMotivation",
        category = "Health & Lifestyle",
        postsCount = "39.6K posts",
        isHot = false,
        description = "Home workout routines, diet tips, and transformation stories."
    )
)

/**
 * Vertical list component displaying dummy trending topics and tags using LazyColumn.
 */
@Composable
fun TrendingTopicsList(
    topics: List<DummyTrendingTopic> = MOCK_TRENDING_TOPICS,
    onTopicClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("trending_topics_lazy_column"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(bottom = 8.dp)
            ) {
                Surface(
                    shape = CircleShape,
                    color = MaterialTheme.colorScheme.primaryContainer,
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.TrendingUp,
                        contentDescription = "Trending",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier
                            .padding(8.dp)
                            .size(20.dp)
                    )
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(
                        text = "Trending Topics & Tags",
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Text(
                        text = "Explore top hashtags and popular categories",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        itemsIndexed(topics, key = { _, topic -> topic.id }) { index, topic ->
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onTopicClick(topic.hashtag) }
                    .testTag("trending_topic_item_$index")
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "#${index + 1}",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.primary
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = topic.hashtag,
                                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            if (topic.isHot) {
                                Spacer(modifier = Modifier.width(6.dp))
                                Icon(
                                    imageVector = Icons.Default.LocalFireDepartment,
                                    contentDescription = "Hot",
                                    tint = Color(0xFFFF5722),
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }

                        Text(
                            text = topic.category,
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.primary
                        )

                        Spacer(modifier = Modifier.height(2.dp))

                        Text(
                            text = topic.description,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 2
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Tag,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(12.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = topic.postsCount,
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    Icon(
                        imageVector = Icons.Default.BookmarkBorder,
                        contentDescription = "Save Tag",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}
