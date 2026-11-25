package com.example.ui.screens

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.AutoAwesome
import androidx.compose.material.icons.rounded.Biotech
import androidx.compose.material.icons.rounded.BookmarkAdd
import androidx.compose.material.icons.rounded.CameraAlt
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.Description
import androidx.compose.material.icons.rounded.FlashOff
import androidx.compose.material.icons.rounded.FlashOn
import androidx.compose.material.icons.rounded.PhotoLibrary
import androidx.compose.material.icons.rounded.QrCodeScanner
import androidx.compose.material.icons.rounded.Science
import androidx.compose.material.icons.rounded.UploadFile
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateMapOf
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.data.LabAiInterpretation
import com.example.data.PharmaCatalog
import com.example.data.PillScanAnalysis
import com.example.ui.components.GlassMedicalCard
import com.example.ui.components.LabReferenceRangeBar
import com.example.ui.components.MedicalSafetyBanner
import com.example.ui.components.PillScannerOverlayCanvas
import com.example.ui.components.ScientificLoader
import com.example.ui.components.StatusPillBadge
import com.example.ui.components.TrendSparklineChart
import com.example.ui.theme.DangerRed
import com.example.ui.theme.DeepNavy
import com.example.ui.theme.ElectricCyan
import com.example.ui.theme.LocalDRxColors
import com.example.ui.theme.MedicalBlue
import com.example.ui.theme.MedicalTeal
import com.example.ui.theme.SuccessGreen
import com.example.ui.theme.WarningAmber
import com.example.viewmodel.AiCallState
import kotlinx.coroutines.delay

// =========================================================================
// FEATURE 07 – PILL IDENTIFICATION SCANNER
// =========================================================================

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PillScannerScreen(
    scanState: AiCallState<PillScanAnalysis>,
    onScanPill: (Bitmap?, String) -> Unit,
    onSaveReport: (title: String, summary: String, severity: String) -> Unit,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val ext = LocalDRxColors.current
    var selectedBitmap by remember { mutableStateOf<Bitmap?>(null) }
    var flashEnabled by remember { mutableStateOf(false) }
    var imprintHint by remember { mutableStateOf("") }
    var scanStage by remember { mutableIntStateOf(0) }

    val scanStages = remember {
        listOf(
            "Image processing & normalization...",
            "Shape & contour recognition...",
            "Surface colour & coating analysis...",
            "Debossed imprint recognition...",
            "Pharmaceutical reference matching..."
        )
    }

    LaunchedEffect(scanState) {
        if (scanState is AiCallState.Loading) {
            scanStage = 0
            for (i in scanStages.indices) {
                scanStage = i
                delay(750)
            }
        }
    }

    // Zero-permission Photo Picker (Play Policy compliant)
    val photoPicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            val bmp = try {
                context.contentResolver.openInputStream(uri)?.use { BitmapFactory.decodeStream(it) }
            } catch (_: Exception) { null }
            selectedBitmap = bmp
        }
    }

    // Zero-permission system camera intent thumbnail
    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicturePreview()
    ) { bmp: Bitmap? ->
        if (bmp != null) {
            selectedBitmap = bmp
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Pill Identification Scanner") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Rounded.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { flashEnabled = !flashEnabled }) {
                        Icon(
                            if (flashEnabled) Icons.Rounded.FlashOn else Icons.Rounded.FlashOff,
                            contentDescription = "Toggle flash guide"
                        )
                    }
                }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding),
            contentPadding = PaddingValues(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Futuristic Viewfinder Frame
            item {
                Surface(
                    shape = RoundedCornerShape(26.dp),
                    color = DeepNavy,
                    border = BorderStroke(1.5.dp, ElectricCyan.copy(alpha = 0.55f)),
                    modifier = Modifier.fillMaxWidth().height(250.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Brush.radialGradient(listOf(Color(0xFF153B5E), DeepNavy))),
                        contentAlignment = Alignment.Center
                    ) {
                        if (selectedBitmap != null) {
                            Image(
                                bitmap = selectedBitmap!!.asImageBitmap(),
                                contentDescription = "Selected tablet photo",
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize()
                            )
                        } else {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier.padding(28.dp)
                            ) {
                                Icon(
                                    Icons.Rounded.QrCodeScanner,
                                    contentDescription = null,
                                    tint = ElectricCyan,
                                    modifier = Modifier.size(56.dp)
                                )
                                Spacer(Modifier.height(10.dp))
                                Text(
                                    "Place the tablet on a plain surface and capture a clear image.",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = Color.White.copy(alpha = 0.92f)
                                )
                            }
                        }

                        PillScannerOverlayCanvas(
                            isScanning = scanState is AiCallState.Loading,
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                }
            }

            // Capture / Upload / Visual Hint Controls
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Button(
                        onClick = { cameraLauncher.launch(null) },
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier.weight(1f).testTag("pill_camera_button")
                    ) {
                        Icon(Icons.Rounded.CameraAlt, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(Modifier.width(6.dp))
                        Text("Capture")
                    }
                    OutlinedButton(
                        onClick = {
                            photoPicker.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                        },
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier.weight(1f).testTag("pill_upload_button")
                    ) {
                        Icon(Icons.Rounded.PhotoLibrary, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(Modifier.width(6.dp))
                        Text("Upload Photo")
                    }
                }
                Spacer(Modifier.height(10.dp))
                OutlinedTextField(
                    value = imprintHint,
                    onValueChange = { imprintHint = it },
                    label = { Text("Optional imprint / shape / color hint (e.g., 'Oval white, DOLO 650')") },
                    singleLine = true,
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(Modifier.height(10.dp))
                Button(
                    onClick = { onScanPill(selectedBitmap, imprintHint.ifBlank { "White oblong tablet with DOLO 650 imprint" }) },
                    enabled = scanState !is AiCallState.Loading,
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.fillMaxWidth().height(52.dp).testTag("pill_analyze_ai_button")
                ) {
                    Icon(Icons.Rounded.AutoAwesome, contentDescription = null)
                    Spacer(Modifier.width(8.dp))
                    Text(
                        if (selectedBitmap != null) "Analyze Captured Image with AI"
                        else "Run AI Visual Imprint Lookup"
                    )
                }
            }

            // AI Scanning Stage Animation
            if (scanState is AiCallState.Loading) {
                item {
                    GlassMedicalCard(modifier = Modifier.fillMaxWidth()) {
                        Column(
                            modifier = Modifier.padding(18.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            ScientificLoader(size = 48.dp, color = ElectricCyan)
                            Text("AI Visual Feature Extraction", style = MaterialTheme.typography.titleMedium)
                            scanStages.forEachIndexed { idx, label ->
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        Icons.Rounded.CheckCircle,
                                        contentDescription = null,
                                        tint = if (idx <= scanStage) SuccessGreen else MaterialTheme.colorScheme.outline,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(Modifier.width(8.dp))
                                    Text(
                                        label,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = if (idx <= scanStage) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Results
            when (scanState) {
                is AiCallState.Success -> {
                    val result = scanState.data
                    item {
                        GlassMedicalCard(
                            modifier = Modifier.fillMaxWidth(),
                            borderColor = if (result.identified) MedicalTeal.copy(alpha = 0.5f) else WarningAmber.copy(alpha = 0.6f)
                        ) {
                            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                StatusPillBadge(
                                    label = if (result.identified) "POSSIBLE MATCHES FOR VERIFICATION" else "MEDICINE UNIDENTIFIED",
                                    color = if (result.identified) MedicalTeal else WarningAmber,
                                    backgroundColor = if (result.identified) MaterialTheme.colorScheme.secondaryContainer else ext.warningContainer
                                )
                                Text("Observed Features", style = MaterialTheme.typography.titleMedium)
                                Text("Shape: ${result.observedShape} • Color: ${result.observedColor}", style = MaterialTheme.typography.bodySmall)
                                Text("Visible Imprint: ${result.observedImprint}", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.primary)
                            }
                        }
                    }

                    if (!result.identified || result.candidates.isEmpty()) {
                        item {
                            MedicalSafetyBanner(
                                text = "Medicine Unidentified: Visual details were insufficient or uncertain. Do NOT consume any unidentified tablet. Verify using original pharmacy packaging or consult a registered pharmacist.",
                                isUrgent = true
                            )
                        }
                    } else {
                        items(result.candidates) { candidate ->
                            GlassMedicalCard(modifier = Modifier.fillMaxWidth()) {
                                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(5.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(candidate.possibleName, style = MaterialTheme.typography.titleMedium, modifier = Modifier.weight(1f))
                                        StatusPillBadge(candidate.confidenceLevel, WarningAmber, ext.warningContainer)
                                    }
                                    Text("Generic: ${candidate.genericIngredient} • Strength: ${candidate.strength}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.primary)
                                    Text("Shape: ${candidate.shape} • Color: ${candidate.color} • Size: ${candidate.sizeEstimate}", style = MaterialTheme.typography.bodySmall)
                                    Text("Imprint: ${candidate.visibleImprint}", style = MaterialTheme.typography.labelSmall)
                                    Text(candidate.supportingDetails, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                            }
                        }
                        item {
                            OutlinedButton(
                                onClick = {
                                    val first = result.candidates.first()
                                    onSaveReport(
                                        "Pill Scan: ${first.possibleName}",
                                        "Observed ${result.observedColor} ${result.observedShape} (Imprint: ${result.observedImprint}). Candidate: ${first.possibleName} (${first.confidenceLevel}). Requires pharmacist verification.",
                                        "VERIFY"
                                    )
                                },
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Icon(Icons.Rounded.BookmarkAdd, contentDescription = null, modifier = Modifier.size(18.dp))
                                Spacer(Modifier.width(6.dp))
                                Text("Save Scan Comparison Note")
                            }
                        }
                    }
                }
                is AiCallState.Error -> item {
                    com.example.ui.components.AiErrorDiagnosticCard(
                        errorMessage = scanState.message,
                        onRetry = {
                            onScanPill(selectedBitmap, imprintHint.ifBlank { "White oblong tablet with DOLO 650 imprint" })
                        }
                    )
                }
                else -> Unit
            }

            item {
                MedicalSafetyBanner(
                    text = "Critical Identification Safety Rule: No pill can be definitively identified from an image alone. Always confirm using the original labeled pharmacy bottle, official imprint codes, and a licensed pharmacist.",
                    isUrgent = true
                )
            }
        }
    }
}

// =========================================================================
// FEATURE 13 – LABGUARD (LABORATORY REPORT ANALYSIS)
// =========================================================================

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun LabGuardScreen(
    labAiState: AiCallState<LabAiInterpretation>,
    onAnalyzeLab: (String, Bitmap?) -> Unit,
    onSaveReport: (title: String, summary: String, severity: String) -> Unit,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val ext = LocalDRxColors.current

    val categories = remember {
        listOf("All", "CBC", "Blood Glucose", "HbA1c", "Lipid Profile", "Liver Function", "Kidney Function", "Thyroid Profile", "Electrolytes")
    }
    var selectedCategory by remember { mutableStateOf("All") }
    var attachedBitmap by remember { mutableStateOf<Bitmap?>(null) }
    var uploadedFileLabel by remember { mutableStateOf<String?>(null) }
    val userValues = remember {
        mutableStateMapOf<String, String>().apply {
            PharmaCatalog.labParameters.forEach { put(it.id, it.defaultSampleValue.toString()) }
        }
    }

    val imagePicker = rememberLauncherForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        if (uri != null) {
            attachedBitmap = try {
                context.contentResolver.openInputStream(uri)?.use { BitmapFactory.decodeStream(it) }
            } catch (_: Exception) { null }
            uploadedFileLabel = "Uploaded Lab Report Photo"
        }
    }

    val pdfPicker = rememberLauncherForActivityResult(
        ActivityResultContracts.OpenDocument()
    ) { uri ->
        if (uri != null) {
            uploadedFileLabel = "PDF Selected: ${uri.lastPathSegment ?: "LabReport.pdf"}"
        }
    }

    val visibleParams = remember(selectedCategory) {
        if (selectedCategory == "All") PharmaCatalog.labParameters
        else PharmaCatalog.labParameters.filter { it.category == selectedCategory }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("LabGuard") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Rounded.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding),
            contentPadding = PaddingValues(20.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            item {
                Column {
                    Text("Understand Your Lab Reports", style = MaterialTheme.typography.headlineMedium)
                    Text(
                        "Upload a lab report image/PDF or enter markers manually to visualize reference ranges, trends, and plain-language AI insights.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            // Input Option Buttons (Upload PDF, Upload Image, Load Sample)
            item {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedButton(
                        onClick = { pdfPicker.launch(arrayOf("application/pdf")) },
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(Icons.Rounded.UploadFile, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(Modifier.width(4.dp))
                        Text("PDF", style = MaterialTheme.typography.labelMedium)
                    }
                    OutlinedButton(
                        onClick = { imagePicker.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)) },
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(Icons.Rounded.PhotoLibrary, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(Modifier.width(4.dp))
                        Text("Image", style = MaterialTheme.typography.labelMedium)
                    }
                    Button(
                        onClick = {
                            PharmaCatalog.labParameters.forEach { userValues[it.id] = it.defaultSampleValue.toString() }
                            uploadedFileLabel = "Sample Comprehensive Lab Panel Loaded"
                        },
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier.weight(1f).testTag("labguard_sample_button")
                    ) {
                        Text("Sample", style = MaterialTheme.typography.labelMedium)
                    }
                }
                if (uploadedFileLabel != null) {
                    Spacer(Modifier.height(8.dp))
                    StatusPillBadge(uploadedFileLabel!!, MedicalBlue, MaterialTheme.colorScheme.primaryContainer)
                }
            }

            // Category Filter Chips
            item {
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(categories) { cat ->
                        FilterChip(
                            selected = selectedCategory == cat,
                            onClick = { selectedCategory = cat },
                            label = { Text(cat) }
                        )
                    }
                }
            }

            // Trigger AI Explanation Button
            item {
                Button(
                    onClick = {
                        val summary = visibleParams.joinToString("\n") { p ->
                            val v = userValues[p.id]?.toDoubleOrNull() ?: p.defaultSampleValue
                            "${p.name}: $v ${p.unit} (Reference: ${p.refLow}–${p.refHigh} ${p.unit})"
                        }
                        onAnalyzeLab(summary, attachedBitmap)
                    },
                    enabled = labAiState !is AiCallState.Loading,
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.fillMaxWidth().height(52.dp).testTag("labguard_analyze_ai_button")
                ) {
                    Icon(Icons.Rounded.AutoAwesome, contentDescription = null)
                    Spacer(Modifier.width(8.dp))
                    Text("Generate AI Lab Explanation")
                }
            }

            // Lab Processing Animation & AI Result
            when (labAiState) {
                is AiCallState.Loading -> item {
                    GlassMedicalCard(modifier = Modifier.fillMaxWidth()) {
                        Row(
                            modifier = Modifier.padding(18.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            ScientificLoader(size = 42.dp, color = MedicalBlue)
                            Spacer(Modifier.width(14.dp))
                            Column {
                                Text("Processing LabGuard Analysis...", style = MaterialTheme.typography.titleSmall)
                                Text("Evaluating markers against reference ranges...", style = MaterialTheme.typography.bodySmall)
                            }
                        }
                    }
                }
                is AiCallState.Success -> item {
                    val ai = labAiState.data
                    GlassMedicalCard(modifier = Modifier.fillMaxWidth(), borderColor = MedicalBlue.copy(alpha = 0.5f)) {
                        Column(modifier = Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Rounded.Biotech, contentDescription = null, tint = MedicalBlue)
                                Spacer(Modifier.width(8.dp))
                                Text("LabGuard AI Summary", style = MaterialTheme.typography.titleMedium)
                            }
                            Text(ai.overallSummary, style = MaterialTheme.typography.bodyMedium)
                            if (ai.urgentFlags.isNotEmpty()) {
                                MedicalSafetyBanner(
                                    text = "Attention Flags: ${ai.urgentFlags.joinToString("; ")}",
                                    isUrgent = true
                                )
                            }
                            ai.parameterExplanations.forEach { (param, desc) ->
                                Column {
                                    Text(param, style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.primary)
                                    Text(desc, style = MaterialTheme.typography.bodySmall)
                                }
                            }
                            Text(ai.followUpGuidance, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            OutlinedButton(
                                onClick = {
                                    onSaveReport(
                                        "LabGuard Panel ($selectedCategory)",
                                        ai.overallSummary.take(400),
                                        if (ai.urgentFlags.isNotEmpty()) "ATTENTION" else "INFO"
                                    )
                                },
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Icon(Icons.Rounded.BookmarkAdd, contentDescription = null, modifier = Modifier.size(18.dp))
                                Spacer(Modifier.width(6.dp))
                                Text("Save LabGuard Report")
                            }
                        }
                    }
                }
                is AiCallState.Error -> item {
                    com.example.ui.components.AiErrorDiagnosticCard(
                        errorMessage = labAiState.message,
                        onRetry = {
                            val summary = visibleParams.joinToString("\n") { p ->
                                val v = userValues[p.id]?.toDoubleOrNull() ?: p.defaultSampleValue
                                "${p.name}: $v ${p.unit} (Reference: ${p.refLow}–${p.refHigh} ${p.unit})"
                            }
                            onAnalyzeLab(summary, attachedBitmap)
                        }
                    )
                }
                else -> Unit
            }

            // Parameter Result Cards with Editable Values, Reference Range Bars & Trend Sparklines
            items(visibleParams, key = { it.id }) { param ->
                val rawStr = userValues[param.id] ?: param.defaultSampleValue.toString()
                val numVal = rawStr.toDoubleOrNull() ?: param.defaultSampleValue
                val isCritical = (param.criticalLow != null && numVal <= param.criticalLow) ||
                    (param.criticalHigh != null && numVal >= param.criticalHigh)
                val statusLabel = when {
                    isCritical -> "URGENT REVIEW"
                    numVal < param.refLow -> "LOW"
                    numVal > param.refHigh -> "HIGH"
                    else -> "WITHIN RANGE"
                }
                val (badgeColor, badgeBg) = when (statusLabel) {
                    "URGENT REVIEW", "HIGH" -> ext.danger to ext.dangerContainer
                    "LOW" -> ext.warning to ext.warningContainer
                    else -> ext.success to ext.successContainer
                }

                GlassMedicalCard(
                    modifier = Modifier.fillMaxWidth(),
                    borderColor = badgeColor.copy(alpha = 0.45f)
                ) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(param.category.uppercase(), style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.primary)
                                Text(param.name, style = MaterialTheme.typography.titleMedium)
                            }
                            StatusPillBadge(statusLabel, badgeColor, badgeBg)
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            OutlinedTextField(
                                value = rawStr,
                                onValueChange = { userValues[param.id] = it },
                                label = { Text("Value (${param.unit})") },
                                singleLine = true,
                                modifier = Modifier.width(145.dp)
                            )
                            Column(modifier = Modifier.weight(1f)) {
                                Text("Ref. Range", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                Text("${param.refLow} – ${param.refHigh} ${param.unit}", style = MaterialTheme.typography.titleSmall)
                            }
                        }

                        LabReferenceRangeBar(value = numVal, refLow = param.refLow, refHigh = param.refHigh)

                        Text(param.simpleExplanation, style = MaterialTheme.typography.bodySmall)
                        Text(
                            "Possible Clinical Significance: ${param.clinicalSignificance}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        val trendWithCurrent = param.historicalTrend.dropLast(1) + ("Current" to numVal)
                        TrendSparklineChart(points = trendWithCurrent, lineColor = badgeColor)
                    }
                }
            }

            item {
                MedicalSafetyBanner(
                    text = "LabGuard Clinical Safety Notice: Always rely on the specific reference intervals printed on your reporting laboratory's document. Laboratory markers alone cannot diagnose disease — consult your ordering physician promptly for abnormal or urgent values."
                )
            }
        }
    }
}
