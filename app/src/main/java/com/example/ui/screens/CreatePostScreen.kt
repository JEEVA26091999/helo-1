package com.example.ui.screens

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.camera.core.CameraSelector
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Cameraswitch
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.EditNote
import androidx.compose.material.icons.filled.FiberManualRecord
import androidx.compose.material.icons.filled.FormatQuote
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Poll
import androidx.compose.material.icons.filled.Publish
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import com.example.data.PostDraftEntity
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.LocalLifecycleOwner
import coil.compose.AsyncImage
import com.example.utils.CameraAudioPermissionHandler
import com.example.utils.CameraPermissionRationaleCard
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.AiGenerationState
import com.example.ui.components.GRADIENT_PRESETS

val CREATE_TABS = listOf("Quote/Status", "AI Generator", "Video Reel", "Photo Post", "Poll")
val LANG_OPTIONS = listOf("English", "Hindi", "Tamil", "Telugu", "Malayalam", "Marathi", "Bengali", "Kannada")
val CAT_OPTIONS = listOf("Quotes & Status", "For You", "Entertainment", "Sports", "News", "Memes")

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreatePostScreen(
    aiState: AiGenerationState,
    drafts: List<PostDraftEntity> = emptyList(),
    onGenerateAi: (String, String, String) -> Unit,
    onPublish: (String, String, String, String, Int, String?, String?, String?, String) -> Unit,
    onSaveDraft: (PostDraftEntity) -> Unit = {},
    onDeleteDraft: (String) -> Unit = {},
    onPublishSuccess: () -> Unit
) {
    val context = LocalContext.current

    var selectedTabIndex by remember { mutableIntStateOf(0) }
    var contentText by remember { mutableStateOf("") }
    var selectedLang by remember { mutableStateOf("English") }
    var selectedCat by remember { mutableStateOf("Quotes & Status") }
    var hashtagsText by remember { mutableStateOf("#HeloStatus #Quotes") }
    var selectedBgGradientIndex by remember { mutableIntStateOf(0) }

    // Photo post state
    var photoUrl by remember { mutableStateOf("android.resource://com.aistudio.helo.app/drawable/img_trending_cover_1786202171214") }

    // Video reel state
    var videoUrl by remember { mutableStateOf("https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerBlazes.mp4") }
    var videoThumbnailUrl by remember { mutableStateOf("android.resource://com.aistudio.helo.app/drawable/img_helo_banner_1786202158870") }

    // AI Generator inputs
    var aiTopic by remember { mutableStateOf("Good Morning Motivation") }

    // Poll options
    var pollOption1 by remember { mutableStateOf("") }
    var pollOption2 by remember { mutableStateOf("") }

    // Local storage photo picker launcher
    val localPhotoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            photoUrl = uri.toString()
            Toast.makeText(context, "Local photo selected from storage!", Toast.LENGTH_SHORT).show()
        }
    }

    // Local storage video picker launcher
    val localVideoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            videoUrl = uri.toString()
            Toast.makeText(context, "Local video selected from storage!", Toast.LENGTH_SHORT).show()
        }
    }

    // Handle AI result
    LaunchedEffect(aiState) {
        if (aiState is AiGenerationState.Success) {
            contentText = aiState.generatedText
            Toast.makeText(context, "Generated status quote successfully!", Toast.LENGTH_SHORT).show()
        } else if (aiState is AiGenerationState.Error) {
            Toast.makeText(context, "AI Error: ${aiState.message}", Toast.LENGTH_SHORT).show()
        }
    }

    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(16.dp)
            .padding(bottom = 80.dp)
            .testTag("create_post_screen")
    ) {
        Text(
            text = "Helo Status & Post Studio",
            style = MaterialTheme.typography.titleLarge.copy(
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
        )
        Text(
            text = "Create colorful quote status cards, share updates, or generate with AI",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(12.dp))

        // DRAFTS BANNER / SECTION
        if (drafts.isNotEmpty()) {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.tertiaryContainer),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("saved_drafts_card")
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.EditNote,
                                contentDescription = "Drafts",
                                tint = MaterialTheme.colorScheme.onTertiaryContainer
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Saved Drafts (${drafts.size})",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onTertiaryContainer
                                )
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    drafts.forEach { draft ->
                        Card(
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = if (draft.content.length > 50) draft.content.take(50) + "..." else draft.content.ifBlank { "Untitled Draft" },
                                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Text(
                                        text = "${draft.category} • ${draft.language} • ${CREATE_TABS.getOrElse(draft.selectedTabIndex) { "Status" }}",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }

                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Button(
                                        onClick = {
                                            contentText = draft.content
                                            selectedCat = draft.category
                                            selectedLang = draft.language
                                            selectedTabIndex = draft.selectedTabIndex
                                            selectedBgGradientIndex = draft.selectedBgGradientIndex
                                            if (draft.photoUrl.isNotBlank()) photoUrl = draft.photoUrl
                                            if (draft.videoUrl.isNotBlank()) videoUrl = draft.videoUrl
                                            if (draft.videoThumbnailUrl.isNotBlank()) videoThumbnailUrl = draft.videoThumbnailUrl
                                            hashtagsText = draft.hashtags
                                            pollOption1 = draft.pollOption1
                                            pollOption2 = draft.pollOption2
                                            Toast.makeText(context, "Resumed draft post!", Toast.LENGTH_SHORT).show()
                                        },
                                        shape = RoundedCornerShape(16.dp),
                                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                                        contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 12.dp, vertical = 0.dp),
                                        modifier = Modifier
                                            .height(34.dp)
                                            .testTag("resume_draft_button_${draft.id}")
                                    ) {
                                        Text("Resume", style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold))
                                    }

                                    IconButton(
                                        onClick = {
                                            onDeleteDraft(draft.id)
                                            Toast.makeText(context, "Draft deleted", Toast.LENGTH_SHORT).show()
                                        },
                                        modifier = Modifier.testTag("delete_draft_button_${draft.id}")
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Delete,
                                            contentDescription = "Delete Draft",
                                            tint = MaterialTheme.colorScheme.error,
                                            modifier = Modifier.size(20.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Tab Selector
        TabRow(
            selectedTabIndex = selectedTabIndex,
            containerColor = MaterialTheme.colorScheme.surfaceVariant,
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
        ) {
            CREATE_TABS.forEachIndexed { index, title ->
                Tab(
                    selected = selectedTabIndex == index,
                    onClick = { selectedTabIndex = index },
                    text = {
                        Text(
                            text = title,
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = if (selectedTabIndex == index) FontWeight.Bold else FontWeight.Normal
                            )
                        )
                    }
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // TAB 1: AI GENERATOR
        if (selectedTabIndex == 1) {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.AutoAwesome,
                            contentDescription = "AI",
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Gemini AI Status Generator",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = aiTopic,
                        onValueChange = { aiTopic = it },
                        label = { Text("Enter Topic or Mood") },
                        placeholder = { Text("e.g. Morning Suvichar, Success, Friendship") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = MaterialTheme.colorScheme.surface,
                            unfocusedContainerColor = MaterialTheme.colorScheme.surface
                        )
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Button(
                        onClick = { onGenerateAi(aiTopic, selectedCat, selectedLang) },
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                        shape = RoundedCornerShape(24.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .testTag("generate_ai_button"),
                        enabled = aiState !is AiGenerationState.Loading
                    ) {
                        if (aiState is AiGenerationState.Loading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(24.dp),
                                color = Color.White,
                                strokeWidth = 2.dp
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Generating Status...")
                        } else {
                            Icon(imageVector = Icons.Default.AutoAwesome, contentDescription = "Generate")
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Generate $selectedLang Status Quote", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }

        // TAB 2: VIDEO REEL WITH CAMERAX & VIDEO UPLOAD SELECTION
        if (selectedTabIndex == 2) {
            CameraAudioPermissionHandler(
                onPermissionsGranted = {
                    Toast.makeText(context, "Camera & Audio permissions granted!", Toast.LENGTH_SHORT).show()
                }
            ) { hasPermission, requestPermissions ->
                if (hasPermission) {
                    CameraXVideoPreviewSection()
                } else {
                    CameraPermissionRationaleCard(
                        onRequestPermission = requestPermissions
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(imageVector = Icons.Default.Videocam, contentDescription = "Video", tint = MaterialTheme.colorScheme.primary)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Upload Video Reel Details",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = { localVideoPickerLauncher.launch("video/*") },
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .weight(1f)
                                .testTag("select_local_video_button")
                        ) {
                            Icon(imageVector = Icons.Default.Videocam, contentDescription = "Pick Local Video")
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Select Local Video", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = videoUrl,
                        onValueChange = { videoUrl = it },
                        label = { Text("Video Reel URL / URI") },
                        placeholder = { Text("https://example.com/reel.mp4 or content://...") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = videoThumbnailUrl,
                        onValueChange = { videoThumbnailUrl = it },
                        label = { Text("Video Cover Thumbnail URL") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Text("Live Video Reel Cover Preview:", style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold))
                    Spacer(modifier = Modifier.height(6.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(160.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color.Black),
                        contentAlignment = Alignment.Center
                    ) {
                        AsyncImage(
                            model = videoThumbnailUrl,
                            contentDescription = "Cover",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .clip(CircleShape)
                                .background(Color.Black.copy(alpha = 0.6f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(imageVector = Icons.Default.PlayArrow, contentDescription = "Play", tint = Color.White, modifier = Modifier.size(32.dp))
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }

        // TAB 3: PHOTO POST UPLOAD SELECTION
        if (selectedTabIndex == 3) {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(imageVector = Icons.Default.Image, contentDescription = "Photo", tint = MaterialTheme.colorScheme.primary)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Upload / Choose Photo",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Button(
                        onClick = { localPhotoPickerLauncher.launch("image/*") },
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(44.dp)
                            .testTag("select_local_photo_button")
                    ) {
                        Icon(imageVector = Icons.Default.Image, contentDescription = "Pick Local Photo")
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Select Photo from Local Storage / Gallery", fontWeight = FontWeight.Bold)
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = photoUrl,
                        onValueChange = { photoUrl = it },
                        label = { Text("Photo URL / Local URI") },
                        placeholder = { Text("Enter photo web link or select from device storage above") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Text("Or Select Sample Photo Preset:", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold))
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        val presets = listOf(
                            "android.resource://com.aistudio.helo.app/drawable/img_trending_cover_1786202171214",
                            "android.resource://com.aistudio.helo.app/drawable/img_helo_banner_1786202158870",
                            "android.resource://com.aistudio.helo.app/drawable/img_avatar_default_1786202183590"
                        )
                        presets.forEachIndexed { index, url ->
                            Box(
                                modifier = Modifier
                                    .size(60.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .border(
                                        width = if (photoUrl == url) 2.dp else 0.dp,
                                        color = if (photoUrl == url) MaterialTheme.colorScheme.primary else Color.Transparent,
                                        shape = RoundedCornerShape(8.dp)
                                    )
                                    .clickable { photoUrl = url }
                            ) {
                                AsyncImage(
                                    model = url,
                                    contentDescription = "Preset $index",
                                    modifier = Modifier.fillMaxSize(),
                                    contentScale = ContentScale.Crop
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text("Live Photo Preview:", style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold))
                    Spacer(modifier = Modifier.height(6.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(180.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(MaterialTheme.colorScheme.surface)
                    ) {
                        AsyncImage(
                            model = photoUrl,
                            contentDescription = "Photo Preview",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }

        // CANVAS PREVIEW FOR QUOTE STATUS
        if (selectedTabIndex == 0 || selectedTabIndex == 1) {
            Text(
                text = "Live Status Canvas Preview",
                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                modifier = Modifier.padding(bottom = 8.dp)
            )

            val brush = GRADIENT_PRESETS.getOrElse(selectedBgGradientIndex) { GRADIENT_PRESETS[0] }
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(brush)
                    .padding(20.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Default.FormatQuote,
                        contentDescription = "Quote",
                        tint = Color.White.copy(alpha = 0.7f),
                        modifier = Modifier.size(32.dp)
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = if (contentText.isBlank()) "Your quote / status will appear here..." else contentText,
                        style = MaterialTheme.typography.titleMedium.copy(
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            lineHeight = 24.sp
                        ),
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = hashtagsText,
                        style = MaterialTheme.typography.bodySmall.copy(color = Color.White.copy(alpha = 0.8f))
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Background Color Gradient Selector
            Text(
                text = "Choose Canvas Theme",
                style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold)
            )
            Spacer(modifier = Modifier.height(6.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                GRADIENT_PRESETS.forEachIndexed { idx, presetBrush ->
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .background(presetBrush)
                            .clickable { selectedBgGradientIndex = idx }
                            .border(
                                width = if (selectedBgGradientIndex == idx) 3.dp else 0.dp,
                                color = if (selectedBgGradientIndex == idx) MaterialTheme.colorScheme.primary else Color.Transparent,
                                shape = CircleShape
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        if (selectedBgGradientIndex == idx) {
                            Icon(imageVector = Icons.Default.Check, contentDescription = "Selected", tint = Color.White)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }

        // TEXT / CAPTION INPUT
        OutlinedTextField(
            value = contentText,
            onValueChange = { contentText = it },
            label = { Text("Status / Post Content") },
            placeholder = { Text("Write your thoughts, status or quote here...") },
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp)
                .testTag("post_content_input"),
            shape = RoundedCornerShape(12.dp)
        )

        Spacer(modifier = Modifier.height(12.dp))

        // POLL INPUTS IF POLL TAB
        if (selectedTabIndex == 4) {
            OutlinedTextField(
                value = pollOption1,
                onValueChange = { pollOption1 = it },
                label = { Text("Option 1") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = pollOption2,
                onValueChange = { pollOption2 = it },
                label = { Text("Option 2") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            )
            Spacer(modifier = Modifier.height(12.dp))
        }

        // DROPDOWNS: LANGUAGE & CATEGORY
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Language selector
            var langExpanded by remember { mutableStateOf(false) }
            ExposedDropdownMenuBox(
                expanded = langExpanded,
                onExpandedChange = { langExpanded = !langExpanded },
                modifier = Modifier.weight(1f)
            ) {
                OutlinedTextField(
                    value = selectedLang,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Language") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = langExpanded) },
                    modifier = Modifier.menuAnchor(),
                    shape = RoundedCornerShape(12.dp)
                )
                ExposedDropdownMenu(
                    expanded = langExpanded,
                    onDismissRequest = { langExpanded = false }
                ) {
                    LANG_OPTIONS.forEach { lang ->
                        DropdownMenuItem(
                            text = { Text(lang) },
                            onClick = {
                                selectedLang = lang
                                langExpanded = false
                            }
                        )
                    }
                }
            }

            // Category selector
            var catExpanded by remember { mutableStateOf(false) }
            ExposedDropdownMenuBox(
                expanded = catExpanded,
                onExpandedChange = { catExpanded = !catExpanded },
                modifier = Modifier.weight(1f)
            ) {
                OutlinedTextField(
                    value = selectedCat,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Category") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = catExpanded) },
                    modifier = Modifier.menuAnchor(),
                    shape = RoundedCornerShape(12.dp)
                )
                ExposedDropdownMenu(
                    expanded = catExpanded,
                    onDismissRequest = { catExpanded = false }
                ) {
                    CAT_OPTIONS.forEach { cat ->
                        DropdownMenuItem(
                            text = { Text(cat) },
                            onClick = {
                                selectedCat = cat
                                catExpanded = false
                            }
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = hashtagsText,
            onValueChange = { hashtagsText = it },
            label = { Text("Hashtags") },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp)
        )

        Spacer(modifier = Modifier.height(20.dp))

        // SAVE DRAFT & PUBLISH BUTTONS
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            OutlinedButton(
                onClick = {
                    if (contentText.isBlank()) {
                        Toast.makeText(context, "Draft content cannot be blank!", Toast.LENGTH_SHORT).show()
                        return@OutlinedButton
                    }
                    val draft = PostDraftEntity(
                        content = contentText,
                        category = selectedCat,
                        language = selectedLang,
                        selectedTabIndex = selectedTabIndex,
                        selectedBgGradientIndex = selectedBgGradientIndex,
                        photoUrl = photoUrl,
                        videoUrl = videoUrl,
                        videoThumbnailUrl = videoThumbnailUrl,
                        hashtags = hashtagsText,
                        pollOption1 = pollOption1,
                        pollOption2 = pollOption2
                    )
                    onSaveDraft(draft)
                    Toast.makeText(context, "Saved incomplete post to local drafts!", Toast.LENGTH_SHORT).show()
                },
                shape = RoundedCornerShape(24.dp),
                modifier = Modifier
                    .weight(1f)
                    .height(52.dp)
                    .testTag("save_draft_button")
            ) {
                Icon(imageVector = Icons.Default.Save, contentDescription = "Save Draft", modifier = Modifier.size(20.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("Save Draft", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
            }

            Button(
                onClick = {
                    if (contentText.isBlank()) {
                        Toast.makeText(context, "Please enter status content!", Toast.LENGTH_SHORT).show()
                        return@Button
                    }

                    val type = when (selectedTabIndex) {
                        0, 1 -> "TEXT_QUOTE"
                        2 -> "VIDEO_REEL"
                        3 -> "IMAGE"
                        4 -> "POLL"
                        else -> "TEXT_QUOTE"
                    }

                    val finalImage = if (type == "IMAGE") photoUrl else null
                    val finalVideo = if (type == "VIDEO_REEL") videoUrl else null
                    val finalThumb = if (type == "VIDEO_REEL") videoThumbnailUrl else null

                    onPublish(
                        contentText,
                        selectedCat,
                        selectedLang,
                        type,
                        selectedBgGradientIndex,
                        finalImage,
                        finalVideo,
                        finalThumb,
                        hashtagsText
                    )

                    Toast.makeText(context, "Post published to Helo feed!", Toast.LENGTH_SHORT).show()
                    onPublishSuccess()
                },
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                shape = RoundedCornerShape(24.dp),
                modifier = Modifier
                    .weight(1.3f)
                    .height(52.dp)
                    .testTag("publish_post_button")
            ) {
                Icon(imageVector = Icons.Default.Publish, contentDescription = "Publish", modifier = Modifier.size(20.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("Publish Feed", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
            }
        }
    }
}

@Composable
fun CameraXVideoPreviewSection(
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    var isRecording by remember { mutableStateOf(false) }
    var cameraLensFacing by remember { mutableIntStateOf(CameraSelector.LENS_FACING_BACK) }
    var recordingDuration by remember { mutableIntStateOf(0) }

    LaunchedEffect(isRecording) {
        if (isRecording) {
            recordingDuration = 0
            while (isRecording) {
                kotlinx.coroutines.delay(1000)
                recordingDuration++
            }
        }
    }

    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Black),
        modifier = modifier
            .fillMaxWidth()
            .height(280.dp)
            .testTag("camerax_video_preview_card")
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            AndroidView(
                factory = { ctx ->
                    val previewView = PreviewView(ctx)
                    val cameraProviderFuture = ProcessCameraProvider.getInstance(ctx)
                    cameraProviderFuture.addListener({
                        try {
                            val cameraProvider = cameraProviderFuture.get()
                            val preview = Preview.Builder().build().also {
                                it.setSurfaceProvider(previewView.surfaceProvider)
                            }
                            val cameraSelector = CameraSelector.Builder()
                                .requireLensFacing(cameraLensFacing)
                                .build()
                            cameraProvider.unbindAll()
                            cameraProvider.bindToLifecycle(
                                lifecycleOwner,
                                cameraSelector,
                                preview
                            )
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }, ContextCompat.getMainExecutor(ctx))
                    previewView
                },
                update = { previewView ->
                    val cameraProviderFuture = ProcessCameraProvider.getInstance(context)
                    cameraProviderFuture.addListener({
                        try {
                            val cameraProvider = cameraProviderFuture.get()
                            val preview = Preview.Builder().build().also {
                                it.setSurfaceProvider(previewView.surfaceProvider)
                            }
                            val cameraSelector = CameraSelector.Builder()
                                .requireLensFacing(cameraLensFacing)
                                .build()
                            cameraProvider.unbindAll()
                            cameraProvider.bindToLifecycle(
                                lifecycleOwner,
                                cameraSelector,
                                preview
                            )
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }, ContextCompat.getMainExecutor(context))
                },
                modifier = Modifier.fillMaxSize()
            )

            // Top overlay bar with recording indicator
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp)
                    .align(Alignment.TopStart),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .background(Color.Black.copy(alpha = 0.6f), RoundedCornerShape(16.dp))
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .clip(CircleShape)
                            .background(if (isRecording) Color.Red else Color.Green)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (isRecording) "REC 00:${String.format(java.util.Locale.US, "%02d", recordingDuration)}" else "CameraX Live",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                    )
                }

                // Switch Camera Lens Button
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(Color.Black.copy(alpha = 0.6f))
                        .clickable {
                            cameraLensFacing = if (cameraLensFacing == CameraSelector.LENS_FACING_BACK) {
                                CameraSelector.LENS_FACING_FRONT
                            } else {
                                CameraSelector.LENS_FACING_BACK
                            }
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Cameraswitch,
                        contentDescription = "Switch Camera",
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            // Bottom overlay bar with Record/Stop Button
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter)
                    .padding(16.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Button(
                    onClick = { isRecording = !isRecording },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isRecording) Color.Red else MaterialTheme.colorScheme.primary
                    ),
                    shape = RoundedCornerShape(24.dp),
                    modifier = Modifier.testTag("camerax_record_toggle_button")
                ) {
                    Icon(
                        imageVector = if (isRecording) Icons.Default.Stop else Icons.Default.FiberManualRecord,
                        contentDescription = if (isRecording) "Stop" else "Record",
                        tint = Color.White
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (isRecording) "Stop Recording" else "Record Video Reel",
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}
