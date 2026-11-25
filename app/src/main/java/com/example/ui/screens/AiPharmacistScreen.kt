package com.example.ui.screens

import android.app.Activity
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
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
import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.Send
import androidx.compose.material.icons.rounded.AutoAwesome
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.ContentCopy
import androidx.compose.material.icons.rounded.DeleteSweep
import androidx.compose.material.icons.rounded.HealthAndSafety
import androidx.compose.material.icons.rounded.Image
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material.icons.rounded.Language
import androidx.compose.material.icons.rounded.LocalHospital
import androidx.compose.material.icons.rounded.MenuBook
import androidx.compose.material.icons.rounded.Mic
import androidx.compose.material.icons.rounded.Science
import androidx.compose.material.icons.rounded.Shield
import androidx.compose.material.icons.rounded.VerifiedUser
import androidx.compose.material.icons.rounded.WarningAmber
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
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
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import com.example.R
import com.example.data.PharmaCatalog
import com.example.ui.components.AiErrorDiagnosticCard
import com.example.ui.components.AiTypingWaveform
import com.example.ui.components.AnimatedDnaHelixCanvas
import com.example.ui.components.DRxGuardLogoMark
import com.example.ui.components.MedicalSafetyBanner
import com.example.ui.components.ScientificLoader
import com.example.ui.components.StatusPillBadge
import com.example.ui.theme.DangerRed
import com.example.ui.theme.DeepNavy
import com.example.ui.theme.ElectricCyan
import com.example.ui.theme.LocalDRxColors
import com.example.ui.theme.MedicalBlue
import com.example.ui.theme.MedicalTeal
import com.example.ui.theme.PremiumPurple
import com.example.ui.theme.SuccessGreen
import com.example.ui.theme.WarningAmber
import com.example.viewmodel.AiCallState
import com.example.viewmodel.ChatMessage

@Composable
fun AiPharmacistScreen(
    selectedLanguage: String,
    messages: List<ChatMessage>,
    aiState: AiCallState<String>,
    onSelectLanguage: (String) -> Unit,
    onSendQuestion: (String, Bitmap?) -> Unit,
    onClearChat: () -> Unit,
    onRetry: () -> Unit = {}
) {
    val context = LocalContext.current
    val ext = LocalDRxColors.current
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
            "Difference between generic and brand medicines?"
        )
    }

    val languageLabels = remember {
        mapOf(
            "English" to "English",
            "Hindi" to "हिन्दी • Hindi",
            "Hinglish" to "Hinglish",
            "Kannada" to "ಕನ್ನಡ • Kannada",
            "Telugu" to "తెలుగు • Telugu",
            "Tamil" to "தமிழ் • Tamil",
            "Bengali" to "বাংলা • Bengali",
            "Marathi" to "मराठी • Marathi",
            "Gujarati" to "ગુજરાતી • Gujarati"
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
        val targetIndex = (messages.size + 2).coerceAtLeast(0)
        if (targetIndex > 0) {
            listState.animateScrollToItem(targetIndex)
        }
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            Surface(
                color = MaterialTheme.colorScheme.surface.copy(alpha = 0.96f),
                tonalElevation = 2.dp
            ) {
                Column {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                            DRxGuardLogoMark(size = 38.dp, glowing = true)
                            Spacer(Modifier.width(12.dp))
                            Column {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = "Ask DRxGuard AI",
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Spacer(Modifier.width(8.dp))
                                    Surface(
                                        shape = CircleShape,
                                        color = ext.successContainer,
                                        border = BorderStroke(1.dp, ext.success.copy(alpha = 0.35f))
                                    ) {
                                        Row(
                                            modifier = Modifier.padding(horizontal = 7.dp, vertical = 2.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Box(
                                                modifier = Modifier
                                                    .size(6.dp)
                                                    .clip(CircleShape)
                                                    .background(ext.success)
                                            )
                                            Spacer(Modifier.width(4.dp))
                                            Text(
                                                text = "LIVE",
                                                style = MaterialTheme.typography.labelSmall,
                                                color = ext.success,
                                                fontWeight = FontWeight.Bold
                                            )
                                        }
                                    }
                                }
                                Text(
                                    text = "Multilingual AI Pharmacist • $selectedLanguage",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }

                        Surface(
                            shape = CircleShape,
                            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f),
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                            modifier = Modifier
                                .clip(CircleShape)
                                .clickable(onClick = onClearChat)
                                .testTag("ai_clear_chat_button")
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    Icons.Rounded.DeleteSweep,
                                    contentDescription = "Clear conversation",
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(Modifier.width(5.dp))
                                Text(
                                    text = "Reset",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }

                    // Polished Language Selector Strip (9 Languages)
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 16.dp, end = 16.dp, bottom = 10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = MedicalTeal.copy(alpha = 0.14f),
                            modifier = Modifier.size(28.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    Icons.Rounded.Language,
                                    contentDescription = "Language",
                                    tint = MedicalTeal,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                        Spacer(Modifier.width(8.dp))
                        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            items(PharmaCatalog.supportedLanguages) { lang ->
                                val selected = selectedLanguage == lang
                                Surface(
                                    shape = CircleShape,
                                    color = if (selected) Color.Transparent else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.55f),
                                    border = BorderStroke(
                                        1.dp,
                                        if (selected) Color.Transparent else MaterialTheme.colorScheme.outlineVariant
                                    ),
                                    modifier = Modifier
                                        .clip(CircleShape)
                                        .then(
                                            if (selected) {
                                                Modifier.background(
                                                    Brush.linearGradient(listOf(MedicalBlue, MedicalTeal)),
                                                    CircleShape
                                                )
                                            } else {
                                                Modifier
                                            }
                                        )
                                        .clickable { onSelectLanguage(lang) }
                                ) {
                                    Text(
                                        text = languageLabels[lang] ?: lang,
                                        style = MaterialTheme.typography.labelMedium,
                                        color = if (selected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                                        fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
                                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 7.dp)
                                    )
                                }
                            }
                        }
                    }
                    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.6f))
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .imePadding()
        ) {
            // Chat History + Hero Banner + Suggestions
            LazyColumn(
                state = listState,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 14.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Hero Clinical AI Banner
                item {
                    Card(
                        shape = RoundedCornerShape(24.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                        border = BorderStroke(1.dp, ElectricCyan.copy(alpha = 0.45f)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(
                                    Brush.linearGradient(
                                        listOf(ext.heroStart, ext.heroMid, ext.heroEnd)
                                    )
                                )
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.img_hero_ai_pharmacist),
                                contentDescription = "DRxGuard AI Pharmacist Avatar",
                                contentScale = ContentScale.Crop,
                                alpha = 0.24f,
                                modifier = Modifier.matchParentSize()
                            )
                            AnimatedDnaHelixCanvas(
                                modifier = Modifier.matchParentSize(),
                                primaryColor = ElectricCyan.copy(alpha = 0.25f),
                                secondaryColor = Color.White.copy(alpha = 0.12f)
                            )
                            Row(
                                modifier = Modifier.padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                DRxGuardLogoMark(size = 50.dp, glowing = true)
                                Spacer(Modifier.width(14.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Surface(
                                        shape = CircleShape,
                                        color = Color.White.copy(alpha = 0.16f)
                                    ) {
                                        Text(
                                            text = "6-SECTION CLINICAL PROTOCOL",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = ElectricCyan,
                                            fontWeight = FontWeight.Bold,
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                        )
                                    }
                                    Spacer(Modifier.height(6.dp))
                                    Text(
                                        "Evidence-Guided Pharmaceutical Assistant",
                                        style = MaterialTheme.typography.titleSmall,
                                        color = Color.White,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Spacer(Modifier.height(2.dp))
                                    Text(
                                        "Responding in $selectedLanguage • Upload strip photos or ask by voice",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = Color.White.copy(alpha = 0.85f)
                                    )
                                }
                            }
                        }
                    }
                }

                // Educational Notice (Compact)
                item {
                    MedicalSafetyBanner(
                        text = "General pharmaceutical education only — never a medical diagnosis or prescription change. Contact emergency services immediately for urgent symptoms."
                    )
                }

                // Suggested Question Chips
                item {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                Icons.Rounded.AutoAwesome,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(15.dp)
                            )
                            Spacer(Modifier.width(6.dp))
                            Text(
                                "Quick Clinical Prompts",
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            items(suggestedQuestions) { suggestion ->
                                Surface(
                                    shape = RoundedCornerShape(16.dp),
                                    color = ext.glassSurface,
                                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.25f)),
                                    shadowElevation = 1.dp,
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(16.dp))
                                        .clickable(enabled = aiState !is AiCallState.Loading && aiState !is AiCallState.Streaming) {
                                            onSendQuestion(suggestion, null)
                                        }
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 13.dp, vertical = 9.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = suggestion,
                                            style = MaterialTheme.typography.labelMedium,
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                // Message Bubbles
                items(messages, key = { it.id }) { msg ->
                    ChatBubbleItem(
                        message = msg,
                        onCopyText = { textToCopy ->
                            val cm = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                            cm.setPrimaryClip(ClipData.newPlainText("DRxGuard AI Answer", textToCopy))
                            Toast.makeText(context, "Answer copied", Toast.LENGTH_SHORT).show()
                        }
                    )
                }

                // Live Streaming / Loading / Error Indicator
                when (aiState) {
                    is AiCallState.Loading -> {
                        item {
                            Surface(
                                shape = RoundedCornerShape(20.dp),
                                color = ext.glassSurface,
                                border = BorderStroke(1.dp, ElectricCyan.copy(alpha = 0.4f)),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier.padding(14.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    ScientificLoader(size = 34.dp, color = ElectricCyan)
                                    Spacer(Modifier.width(12.dp))
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            "Synthesizing Clinical Guidance",
                                            style = MaterialTheme.typography.titleSmall,
                                            color = MaterialTheme.colorScheme.onSurface,
                                            fontWeight = FontWeight.Bold
                                        )
                                        Text(
                                            "Preparing 6-section response in $selectedLanguage...",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.primary
                                        )
                                    }
                                    AiTypingWaveform(color = MedicalTeal)
                                }
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
                            AiErrorDiagnosticCard(
                                errorMessage = aiState.message,
                                onRetry = onRetry
                            )
                        }
                    }
                    else -> Unit
                }
            }

            // Attached Image Thumbnail Preview
            AnimatedVisibility(visible = attachedBitmap != null) {
                if (attachedBitmap != null) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            shape = RoundedCornerShape(14.dp),
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.45f)),
                            color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.6f)
                        ) {
                            Row(
                                modifier = Modifier.padding(8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Image(
                                    bitmap = attachedBitmap!!.asImageBitmap(),
                                    contentDescription = "Attached medicine image",
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier
                                        .size(44.dp)
                                        .clip(RoundedCornerShape(10.dp))
                                )
                                Spacer(Modifier.width(10.dp))
                                Column {
                                    Text("Medicine Image Ready", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
                                    Text("Will analyze packaging/label with AI", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                                Spacer(Modifier.width(6.dp))
                                IconButton(onClick = { attachedBitmap = null }, modifier = Modifier.size(32.dp)) {
                                    Icon(Icons.Rounded.Close, contentDescription = "Remove image", modifier = Modifier.size(16.dp))
                                }
                            }
                        }
                    }
                }
            }

            // Floating Capsule Input Composer
            val busy = aiState is AiCallState.Loading || aiState is AiCallState.Streaming
            val canSend = !busy && (inputText.isNotBlank() || attachedBitmap != null)
            Surface(
                color = MaterialTheme.colorScheme.surface,
                tonalElevation = 4.dp,
                shadowElevation = 6.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column {
                    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.55f))
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            shape = RoundedCornerShape(26.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.48f),
                            border = BorderStroke(
                                1.dp,
                                if (inputText.isNotBlank()) MaterialTheme.colorScheme.primary.copy(alpha = 0.55f)
                                else MaterialTheme.colorScheme.outlineVariant
                            ),
                            modifier = Modifier.weight(1f)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 6.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                IconButton(
                                    onClick = {
                                        photoPicker.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                                    },
                                    modifier = Modifier
                                        .size(40.dp)
                                        .testTag("ai_attach_image_button")
                                ) {
                                    Icon(
                                        Icons.Rounded.Image,
                                        contentDescription = "Upload medicine image",
                                        tint = MedicalBlue,
                                        modifier = Modifier.size(21.dp)
                                    )
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
                                    modifier = Modifier
                                        .size(40.dp)
                                        .testTag("ai_voice_input_button")
                                ) {
                                    Icon(
                                        Icons.Rounded.Mic,
                                        contentDescription = "Voice input",
                                        tint = MedicalTeal,
                                        modifier = Modifier.size(21.dp)
                                    )
                                }
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .padding(horizontal = 6.dp, vertical = 10.dp),
                                    contentAlignment = Alignment.CenterStart
                                ) {
                                    if (inputText.isEmpty()) {
                                        Text(
                                            text = "Ask about medicines in $selectedLanguage...",
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.78f),
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                    }
                                    BasicTextField(
                                        value = inputText,
                                        onValueChange = { inputText = it },
                                        textStyle = MaterialTheme.typography.bodyMedium.copy(
                                            color = MaterialTheme.colorScheme.onSurface
                                        ),
                                        cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
                                        maxLines = 4,
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .testTag("ai_chat_input")
                                    )
                                }
                            }
                        }

                        Spacer(Modifier.width(8.dp))

                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .size(48.dp)
                                .clip(CircleShape)
                                .background(
                                    brush = if (canSend) {
                                        Brush.linearGradient(listOf(MedicalBlue, ElectricCyan))
                                    } else {
                                        Brush.linearGradient(
                                            listOf(
                                                MaterialTheme.colorScheme.surfaceVariant,
                                                MaterialTheme.colorScheme.surfaceVariant
                                            )
                                        )
                                    },
                                    shape = CircleShape
                                )
                                .clickable(enabled = canSend) {
                                    onSendQuestion(inputText, attachedBitmap)
                                    inputText = ""
                                    attachedBitmap = null
                                }
                                .testTag("ai_send_button")
                        ) {
                            Icon(
                                Icons.AutoMirrored.Rounded.Send,
                                contentDescription = "Send question",
                                tint = if (canSend) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

private data class ClinicalSectionStyle(
    val title: String,
    val body: String,
    val accent: Color,
    val icon: ImageVector
)

@Composable
private fun ChatBubbleItem(
    message: ChatMessage,
    isStreaming: Boolean = false,
    onCopyText: ((String) -> Unit)? = null
) {
    val ext = LocalDRxColors.current
    val alignment = if (message.fromUser) Alignment.End else Alignment.Start
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = alignment
    ) {
        if (message.fromUser) {
            Surface(
                shape = RoundedCornerShape(
                    topStart = 22.dp,
                    topEnd = 22.dp,
                    bottomStart = 22.dp,
                    bottomEnd = 6.dp
                ),
                shadowElevation = 3.dp,
                color = Color.Transparent,
                modifier = Modifier.widthIn(max = 310.dp)
            ) {
                Box(
                    modifier = Modifier
                        .background(Brush.linearGradient(listOf(MedicalBlue, Color(0xFF0B5ED7))))
                        .padding(horizontal = 16.dp, vertical = 12.dp)
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        if (message.hasImage) {
                            StatusPillBadge(
                                label = "IMAGE ATTACHED",
                                color = Color.White,
                                backgroundColor = Color.White.copy(alpha = 0.22f)
                            )
                        }
                        Text(
                            text = message.text,
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.White
                        )
                    }
                }
            }
        } else {
            val sections = remember(message.text) { parseClinicalSections(message.text) }
            Surface(
                shape = RoundedCornerShape(
                    topStart = 22.dp,
                    topEnd = 22.dp,
                    bottomStart = 6.dp,
                    bottomEnd = 22.dp
                ),
                color = ext.glassSurface,
                border = BorderStroke(
                    1.dp,
                    if (isStreaming) ElectricCyan.copy(alpha = 0.55f) else ext.glassBorder
                ),
                shadowElevation = 2.dp,
                modifier = Modifier.fillMaxWidth(0.95f)
            ) {
                Column(
                    modifier = Modifier.padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(7.dp)
                        ) {
                            Surface(
                                shape = CircleShape,
                                color = MedicalTeal.copy(alpha = 0.14f),
                                modifier = Modifier.size(26.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        Icons.Rounded.AutoAwesome,
                                        contentDescription = null,
                                        tint = MedicalTeal,
                                        modifier = Modifier.size(14.dp)
                                    )
                                }
                            }
                            Text(
                                "DRxGuard AI",
                                style = MaterialTheme.typography.labelLarge,
                                color = MedicalTeal,
                                fontWeight = FontWeight.Bold
                            )
                            if (isStreaming) {
                                AiTypingWaveform(color = ElectricCyan)
                                StatusPillBadge(
                                    label = "STREAMING",
                                    color = ElectricCyan,
                                    backgroundColor = ElectricCyan.copy(alpha = 0.14f)
                                )
                            }
                        }

                        if (!isStreaming && onCopyText != null) {
                            IconButton(
                                onClick = { onCopyText(message.text.replace("### ", "").replace("**", "")) },
                                modifier = Modifier.size(32.dp)
                            ) {
                                Icon(
                                    Icons.Rounded.ContentCopy,
                                    contentDescription = "Copy response",
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    }

                    if (sections.isNotEmpty()) {
                        sections.forEach { sec ->
                            Surface(
                                shape = RoundedCornerShape(14.dp),
                                color = sec.accent.copy(alpha = 0.06f),
                                border = BorderStroke(1.dp, sec.accent.copy(alpha = 0.22f)),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(
                                    modifier = Modifier.padding(12.dp),
                                    verticalArrangement = Arrangement.spacedBy(5.dp)
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            sec.icon,
                                            contentDescription = null,
                                            tint = sec.accent,
                                            modifier = Modifier.size(15.dp)
                                        )
                                        Spacer(Modifier.width(6.dp))
                                        Text(
                                            text = sec.title,
                                            style = MaterialTheme.typography.labelLarge,
                                            color = sec.accent,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                    if (sec.body.isNotBlank()) {
                                        Text(
                                            text = formatMarkdownInline(sec.body),
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                    }
                                }
                            }
                        }
                    } else {
                        Text(
                            text = formatMarkdownInline(message.text),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }
        }
    }
}

private fun parseClinicalSections(rawText: String): List<ClinicalSectionStyle> {
    val lines = rawText.lines()
    val result = mutableListOf<ClinicalSectionStyle>()
    var currentTitle: String? = null
    val currentBody = StringBuilder()

    fun flushSection() {
        val t = currentTitle?.trim()
        val b = currentBody.toString().trim()
        if (!t.isNullOrBlank()) {
            val (accent, icon) = sectionThemeForTitle(t)
            result.add(ClinicalSectionStyle(title = t, body = b, accent = accent, icon = icon))
        }
        currentBody.clear()
    }

    lines.forEach { line ->
        val trimmed = line.trim()
        val isMarkdownHeading = trimmed.startsWith("### ") || trimmed.startsWith("## ")
        if (isMarkdownHeading) {
            if (currentTitle != null) flushSection()
            currentTitle = trimmed.removePrefix("### ").removePrefix("## ").trim()
        } else {
            if (currentTitle == null && trimmed.isNotBlank()) {
                currentTitle = "Clinical Summary"
            }
            if (currentTitle != null) {
                currentBody.appendLine(line)
            }
        }
    }
    flushSection()
    return if (result.size >= 2 || rawText.contains("### ")) result else emptyList()
}

private fun sectionThemeForTitle(title: String): Pair<Color, ImageVector> {
    val lower = title.lowercase()
    return when {
        lower.contains("direct") || lower.contains("1.") -> MedicalBlue to Icons.Rounded.AutoAwesome
        lower.contains("scientific") || lower.contains("mechanism") || lower.contains("2.") -> PremiumPurple to Icons.Rounded.Science
        lower.contains("precaution") || lower.contains("3.") -> WarningAmber to Icons.Rounded.Shield
        lower.contains("warning") || lower.contains("4.") -> DangerRed to Icons.Rounded.WarningAmber
        lower.contains("source") || lower.contains("5.") -> MedicalTeal to Icons.Rounded.MenuBook
        lower.contains("consult") || lower.contains("professional") || lower.contains("6.") -> SuccessGreen to Icons.Rounded.LocalHospital
        else -> MedicalBlue to Icons.Rounded.Info
    }
}

private fun formatMarkdownInline(input: String): AnnotatedString {
    val normalized = input
        .replace(Regex("(?m)^\\s*[-*]\\s+"), "• ")
        .trim()

    return buildAnnotatedString {
        var idx = 0
        while (idx < normalized.length) {
            val startBold = normalized.indexOf("**", idx)
            if (startBold == -1) {
                append(normalized.substring(idx))
                break
            }
            if (startBold > idx) {
                append(normalized.substring(idx, startBold))
            }
            val endBold = normalized.indexOf("**", startBold + 2)
            if (endBold == -1) {
                append(normalized.substring(startBold + 2))
                break
            }
            withStyle(SpanStyle(fontWeight = FontWeight.Bold)) {
                append(normalized.substring(startBold + 2, endBold))
            }
            idx = endBold + 2
        }
    }
}
