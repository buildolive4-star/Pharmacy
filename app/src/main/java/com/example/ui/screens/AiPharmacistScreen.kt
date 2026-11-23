package com.example.ui.screens

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.os.Build
import android.provider.MediaStore
import android.speech.RecognizerIntent
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.Send
import androidx.compose.material.icons.rounded.AutoAwesome
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.DeleteSweep
import androidx.compose.material.icons.rounded.Image
import androidx.compose.material.icons.rounded.Language
import androidx.compose.material.icons.rounded.Mic
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.R
import com.example.data.PharmaCatalog
import com.example.ui.components.DRxGuardLogoMark
import com.example.ui.components.MedicalSafetyBanner
import com.example.ui.components.ScientificLoader
import com.example.ui.components.StatusPillBadge
import com.example.ui.theme.DangerRed
import com.example.ui.theme.DeepNavy
import com.example.ui.theme.ElectricCyan
import com.example.ui.theme.MedicalBlue
import com.example.ui.theme.MedicalTeal
import com.example.viewmodel.AiCallState
import com.example.viewmodel.ChatMessage

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AiPharmacistScreen(
    selectedLanguage: String,
    messages: List<ChatMessage>,
    aiState: AiCallState<String>,
    onSelectLanguage: (String) -> Unit,
    onSendQuestion: (String, Bitmap?) -> Unit,
    onClearChat: () -> Unit
) {
    val context = LocalContext.current
    var inputText by remember { mutableStateOf("") }
    var attachedBitmap by remember { mutableStateOf<Bitmap?>(null) }
    val listState = rememberLazyListState()

    val suggestedQuestions = remember {
        listOf(
            "What is the mechanism of action of paracetamol?",
            "Can I take ibuprofen with omeprazole?",
            "What are common side effects of metformin?",
            "What does elevated HbA1c generally mean?",
            "How should antibiotics be stored?",
            "What is the difference between generic and brand medicines?"
        )
    }

    // Zero-permission Android Photo Picker
    val photoPicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        if (uri != null) {
            try {
                attachedBitmap = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                    ImageDecoder.decodeBitmap(ImageDecoder.createSource(context.contentResolver, uri))
                } else {
                    @Suppress("DEPRECATION")
                    MediaStore.Images.Media.getBitmap(context.contentResolver, uri)
                }
            } catch (_: Exception) {
                Toast.makeText(context, "Unable to decode selected image.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // Standard Android Voice Recognizer Intent (no RECORD_AUDIO permission required)
    val voiceLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val spoken = result.data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)?.firstOrNull()
            if (!spoken.isNullOrBlank()) {
                inputText = spoken
            }
        }
    }

    LaunchedEffect(messages.size, aiState) {
        val targetIndex = (messages.size + 1).coerceAtLeast(0)
        if (targetIndex > 0) {
            listState.animateScrollToItem(targetIndex)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        DRxGuardLogoMark(size = 28.dp, glowing = false)
                        Spacer(Modifier.width(8.dp))
                        Column {
                            Text("Ask DRxGuard AI", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                            Text("Multilingual AI Pharmacist", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.primary)
                        }
                    }
                },
                actions = {
                    IconButton(onClick = onClearChat, modifier = Modifier.testTag("ai_clear_chat_button")) {
                        Icon(Icons.Rounded.DeleteSweep, contentDescription = "Clear conversation")
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .imePadding()
        ) {
            // Language Selector Strip (9 Languages)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Rounded.Language, contentDescription = "Language", tint = MedicalTeal, modifier = Modifier.size(18.dp))
                Spacer(Modifier.width(8.dp))
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(PharmaCatalog.supportedLanguages) { lang ->
                        FilterChip(
                            selected = selectedLanguage == lang,
                            onClick = { onSelectLanguage(lang) },
                            label = { Text(lang, style = MaterialTheme.typography.labelMedium) }
                        )
                    }
                }
            }

            // Chat History + Hero Banner + Suggestions
            LazyColumn(
                state = listState,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 10.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Hero Avatar Card
                item {
                    Card(shape = RoundedCornerShape(22.dp), modifier = Modifier.fillMaxWidth()) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Brush.linearGradient(listOf(DeepNavy, MedicalBlue)))
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.img_hero_ai_pharmacist),
                                contentDescription = "DRxGuard AI Pharmacist Avatar",
                                contentScale = ContentScale.Crop,
                                alpha = 0.30f,
                                modifier = Modifier.matchParentSize()
                            )
                            Row(
                                modifier = Modifier.padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                DRxGuardLogoMark(size = 54.dp, glowing = true)
                                Spacer(Modifier.width(14.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        "Your intelligent pharmaceutical information assistant.",
                                        style = MaterialTheme.typography.titleSmall,
                                        color = Color.White,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Spacer(Modifier.height(4.dp))
                                    Text(
                                        "Responding in $selectedLanguage • 6-section clinical format",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = ElectricCyan
                                    )
                                }
                            }
                        }
                    }
                }

                // Educational Notice
                item {
                    MedicalSafetyBanner(
                        text = "DRxGuard AI provides general pharmaceutical education — never individualized medical diagnosis or prescription changes. For emergencies, contact local medical services immediately."
                    )
                }

                // Suggested Question Chips
                item {
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text(
                            "Suggested Questions",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            items(suggestedQuestions) { suggestion ->
                                Surface(
                                    shape = RoundedCornerShape(14.dp),
                                    color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.65f),
                                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.25f)),
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(14.dp))
                                        .clickable(enabled = aiState !is AiCallState.Loading && aiState !is AiCallState.Streaming) {
                                            onSendQuestion(suggestion, null)
                                        }
                                ) {
                                    Text(
                                        text = suggestion,
                                        style = MaterialTheme.typography.labelMedium,
                                        color = MaterialTheme.colorScheme.onSurface,
                                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                                    )
                                }
                            }
                        }
                    }
                }

                // Message Bubbles
                items(messages, key = { it.id }) { msg ->
                    ChatBubbleItem(message = msg)
                }

                // Live Streaming / Loading / Error Indicator
                when (aiState) {
                    is AiCallState.Loading -> {
                        item {
                            Row(
                                modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                ScientificLoader(size = 32.dp)
                                Spacer(Modifier.width(12.dp))
                                Text(
                                    "DRxGuard AI is synthesizing evidence-guided information in $selectedLanguage...",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                    }
                    is AiCallState.Streaming -> {
                        item {
                            ChatBubbleItem(
                                message = ChatMessage(
                                    id = "streaming_live",
                                    fromUser = false,
                                    text = aiState.partial,
                                    language = selectedLanguage
                                ),
                                isStreaming = true
                            )
                        }
                    }
                    is AiCallState.Error -> {
                        item {
                            Surface(
                                shape = RoundedCornerShape(16.dp),
                                color = DangerRed.copy(alpha = 0.12f),
                                border = BorderStroke(1.dp, DangerRed.copy(alpha = 0.35f)),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    text = "Unable to reach DRxGuard AI: ${aiState.message}. Please retry in a moment.",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = DangerRed,
                                    modifier = Modifier.padding(14.dp)
                                )
                            }
                        }
                    }
                    else -> Unit
                }
            }

            // Attached Image Thumbnail Preview
            if (attachedBitmap != null) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary),
                        color = MaterialTheme.colorScheme.surfaceVariant
                    ) {
                        Row(
                            modifier = Modifier.padding(6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Image(
                                bitmap = attachedBitmap!!.asImageBitmap(),
                                contentDescription = "Attached medicine image",
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .size(44.dp)
                                    .clip(RoundedCornerShape(8.dp))
                            )
                            Spacer(Modifier.width(8.dp))
                            Text("Medicine image attached", style = MaterialTheme.typography.labelMedium)
                            IconButton(onClick = { attachedBitmap = null }, modifier = Modifier.size(32.dp)) {
                                Icon(Icons.Rounded.Close, contentDescription = "Remove image", modifier = Modifier.size(16.dp))
                            }
                        }
                    }
                }
            }

            // Input Bar (Text + Voice + Image + Send)
            Surface(
                tonalElevation = 3.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = {
                            photoPicker.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                        },
                        modifier = Modifier.testTag("ai_attach_image_button")
                    ) {
                        Icon(Icons.Rounded.Image, contentDescription = "Upload medicine image", tint = MedicalBlue)
                    }
                    IconButton(
                        onClick = {
                            val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
                                putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
                                putExtra(RecognizerIntent.EXTRA_PROMPT, "Ask DRxGuard AI in $selectedLanguage")
                            }
                            try {
                                voiceLauncher.launch(intent)
                            } catch (_: Exception) {
                                Toast.makeText(context, "Voice dictation not available on this device.", Toast.LENGTH_SHORT).show()
                            }
                        },
                        modifier = Modifier.testTag("ai_voice_input_button")
                    ) {
                        Icon(Icons.Rounded.Mic, contentDescription = "Voice input", tint = MedicalTeal)
                    }
                    OutlinedTextField(
                        value = inputText,
                        onValueChange = { inputText = it },
                        modifier = Modifier
                            .weight(1f)
                            .testTag("ai_chat_input"),
                        placeholder = { Text("Ask about medicines in $selectedLanguage...", maxLines = 1) },
                        shape = RoundedCornerShape(18.dp),
                        maxLines = 3
                    )
                    Spacer(Modifier.width(8.dp))
                    val busy = aiState is AiCallState.Loading || aiState is AiCallState.Streaming
                    Surface(
                        shape = CircleShape,
                        color = if (!busy && (inputText.isNotBlank() || attachedBitmap != null)) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                            .clickable(enabled = !busy && (inputText.isNotBlank() || attachedBitmap != null)) {
                                onSendQuestion(inputText, attachedBitmap)
                                inputText = ""
                                attachedBitmap = null
                            }
                            .testTag("ai_send_button")
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                Icons.AutoMirrored.Rounded.Send,
                                contentDescription = "Send question",
                                tint = if (!busy && (inputText.isNotBlank() || attachedBitmap != null)) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ChatBubbleItem(message: ChatMessage, isStreaming: Boolean = false) {
    val alignment = if (message.fromUser) Alignment.End else Alignment.Start
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = alignment
    ) {
        Surface(
            shape = RoundedCornerShape(
                topStart = 20.dp,
                topEnd = 20.dp,
                bottomStart = if (message.fromUser) 20.dp else 4.dp,
                bottomEnd = if (message.fromUser) 4.dp else 20.dp
            ),
            color = if (message.fromUser) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface,
            border = if (message.fromUser) null else BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
            tonalElevation = 2.dp,
            modifier = Modifier.widthIn(max = 330.dp)
        ) {
            Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                if (!message.fromUser) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        Icon(Icons.Rounded.AutoAwesome, contentDescription = null, tint = MedicalTeal, modifier = Modifier.size(16.dp))
                        Text("DRxGuard AI", style = MaterialTheme.typography.labelMedium, color = MedicalTeal, fontWeight = FontWeight.Bold)
                        if (isStreaming) {
                            StatusPillBadge(label = "STREAMING", color = ElectricCyan, backgroundColor = ElectricCyan.copy(alpha = 0.14f))
                        }
                    }
                } else if (message.hasImage) {
                    StatusPillBadge(label = "IMAGE ATTACHED", color = Color.White, backgroundColor = Color.White.copy(alpha = 0.2f))
                }
                Text(
                    text = message.text.replace("### ", "").replace("**", ""),
                    style = MaterialTheme.typography.bodyMedium,
                    color = if (message.fromUser) Color.White else MaterialTheme.colorScheme.onSurface
                )
            }
        }
    }
}
