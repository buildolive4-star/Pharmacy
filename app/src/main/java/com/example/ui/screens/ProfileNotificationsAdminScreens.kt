package com.example.ui.screens

import android.content.Intent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.automirrored.rounded.Logout
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.AdminPanelSettings
import androidx.compose.material.icons.rounded.Alarm
import androidx.compose.material.icons.rounded.AutoAwesome
import androidx.compose.material.icons.rounded.Biotech
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.DarkMode
import androidx.compose.material.icons.rounded.DeleteForever
import androidx.compose.material.icons.rounded.Download
import androidx.compose.material.icons.rounded.HealthAndSafety
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material.icons.rounded.Inventory2
import androidx.compose.material.icons.rounded.Language
import androidx.compose.material.icons.rounded.MarkEmailRead
import androidx.compose.material.icons.rounded.Notifications
import androidx.compose.material.icons.rounded.NotificationsActive
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material.icons.rounded.PregnantWoman
import androidx.compose.material.icons.rounded.PrivacyTip
import androidx.compose.material.icons.rounded.Security
import androidx.compose.material.icons.rounded.Shield
import androidx.compose.material.icons.rounded.VerifiedUser
import androidx.compose.material.icons.rounded.WarningAmber
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.data.AdminMedicine
import com.example.data.AppNotification
import com.example.data.BmiEntry
import com.example.data.CabinetMedicine
import com.example.data.MedicineReminder
import com.example.data.PharmaCatalog
import com.example.data.SavedReport
import com.example.data.UserProfile
import com.example.ui.components.GlassMedicalCard
import com.example.ui.components.MedicalSafetyBanner
import com.example.ui.components.StatusPillBadge
import com.example.ui.theme.DangerRed
import com.example.ui.theme.DeepNavy
import com.example.ui.theme.ElectricCyan
import com.example.ui.theme.LocalDRxColors
import com.example.ui.theme.MedicalBlue
import com.example.ui.theme.MedicalTeal
import com.example.ui.theme.PregnancyPink
import com.example.ui.theme.PremiumPurple
import com.example.ui.theme.SuccessGreen
import com.example.ui.theme.WarningAmber

// =========================================================================
// UNIVERSAL NOTIFICATION CENTER (All 8 Alert Categories)
// =========================================================================

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationsScreen(
    notifications: List<AppNotification>,
    onMarkRead: (String) -> Unit,
    onSimulateAlert: (category: String, title: String, message: String) -> Unit,
    onBack: () -> Unit
) {
    val categories = remember {
        listOf(
            "All",
            "Medicine reminders",
            "Expiry alerts",
            "Pregnancy milestones",
            "Report analysis updates",
            "AI responses",
            "Security alerts",
            "New educational content",
            "App updates"
        )
    }
    var selectedCategory by remember { mutableStateOf("All") }

    val filtered = remember(notifications, selectedCategory) {
        if (selectedCategory == "All") notifications
        else notifications.filter { it.category.equals(selectedCategory, ignoreCase = true) }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Notification Center") },
                navigationIcon = {
                    IconButton(onClick = onBack, modifier = Modifier.testTag("notifications_back_button")) {
                        Icon(Icons.AutoMirrored.Rounded.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
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

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedButton(
                        onClick = {
                            onSimulateAlert(
                                "Medicine reminders",
                                "Evening Dose Due",
                                "Metformin 500 mg (Prescribed schedule) is due with dinner."
                            )
                        },
                        modifier = Modifier
                            .weight(1f)
                            .testTag("test_reminder_alert_button"),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(Icons.Rounded.Alarm, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(Modifier.width(6.dp))
                        Text("Test Reminder", style = MaterialTheme.typography.labelMedium)
                    }
                    OutlinedButton(
                        onClick = {
                            onSimulateAlert(
                                "Expiry alerts",
                                "Medicine Expiring Soon (30d)",
                                "Check your digital cabinet for batches nearing expiration and dispose safely if expired."
                            )
                        },
                        modifier = Modifier
                            .weight(1f)
                            .testTag("test_expiry_alert_button"),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(Icons.Rounded.Inventory2, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(Modifier.width(6.dp))
                        Text("Test Expiry Alert", style = MaterialTheme.typography.labelMedium)
                    }
                }
            }

            if (filtered.isEmpty()) {
                item {
                    GlassMedicalCard(modifier = Modifier.fillMaxWidth()) {
                        Column(
                            modifier = Modifier.padding(28.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(Icons.Rounded.Notifications, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(40.dp))
                            Spacer(Modifier.height(8.dp))
                            Text("All caught up!", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                            Text(
                                "New reminders, expiry alerts, LabGuard updates, and security notifications will appear here.",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            } else {
                items(filtered, key = { it.id }) { notif ->
                    val (icon, tint) = notificationIconAndTint(notif.category)
                    GlassMedicalCard(
                        modifier = Modifier.fillMaxWidth(),
                        borderColor = if (!notif.read) tint.copy(alpha = 0.5f) else LocalDRxColors.current.glassBorder
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(14.dp),
                            verticalAlignment = Alignment.Top
                        ) {
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = tint.copy(alpha = 0.14f),
                                modifier = Modifier.size(42.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(icon, contentDescription = notif.category, tint = tint, modifier = Modifier.size(22.dp))
                                }
                            }
                            Spacer(Modifier.width(12.dp))
                            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    StatusPillBadge(
                                        label = notif.category,
                                        color = tint,
                                        backgroundColor = tint.copy(alpha = 0.12f)
                                    )
                                    if (!notif.read) {
                                        Box(
                                            modifier = Modifier
                                                .size(10.dp)
                                                .background(tint, CircleShape)
                                        )
                                    }
                                }
                                Text(notif.title, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                                Text(notif.message, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                if (!notif.read) {
                                    TextButton(
                                        onClick = { onMarkRead(notif.id) },
                                        contentPadding = PaddingValues(0.dp)
                                    ) {
                                        Icon(Icons.Rounded.MarkEmailRead, contentDescription = null, modifier = Modifier.size(16.dp))
                                        Spacer(Modifier.width(4.dp))
                                        Text("Mark as read", style = MaterialTheme.typography.labelMedium)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

private fun notificationIconAndTint(category: String): Pair<ImageVector, Color> = when {
    category.contains("reminder", true) -> Icons.Rounded.Alarm to PremiumPurple
    category.contains("expiry", true) -> Icons.Rounded.Inventory2 to WarningAmber
    category.contains("pregnancy", true) -> Icons.Rounded.PregnantWoman to PregnancyPink
    category.contains("report", true) -> Icons.Rounded.Biotech to MedicalBlue
    category.contains("AI", true) -> Icons.Rounded.AutoAwesome to ElectricCyan
    category.contains("Security", true) -> Icons.Rounded.Security to DangerRed
    else -> Icons.Rounded.NotificationsActive to MedicalTeal
}

// =========================================================================
// PROFILE & SETTINGS SCREEN (Consent-Gated Health Profile, Theme, Export, Delete)
// =========================================================================

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileAndSettingsScreen(
    profile: UserProfile?,
    userEmail: String,
    darkModeEnabled: Boolean,
    selectedLanguage: String,
    reminders: List<MedicineReminder>,
    cabinet: List<CabinetMedicine>,
    bmiHistory: List<BmiEntry>,
    reports: List<SavedReport>,
    onToggleDarkMode: (Boolean) -> Unit,
    onSelectLanguage: (String) -> Unit,
    onSaveProfile: (
        fullName: String,
        mobile: String,
        dob: String,
        language: String,
        healthConsent: Boolean,
        heightCm: Double?,
        weightKg: Double?,
        allergies: String,
        conditions: String,
        emergencyContact: String
    ) -> Unit,
    onOpenAdminPanel: () -> Unit,
    onOpenNotifications: () -> Unit,
    onDeleteAccountData: () -> Unit,
    onSignOut: () -> Unit
) {
    val context = LocalContext.current

    var fullName by remember(profile) { mutableStateOf(profile?.fullName ?: "DRxGuard Member") }
    var mobile by remember(profile) { mutableStateOf(profile?.mobileNumber.orEmpty()) }
    var dob by remember(profile) { mutableStateOf(profile?.dateOfBirth.orEmpty()) }
    var healthConsent by remember(profile) { mutableStateOf(profile?.healthConsentGranted ?: false) }
    var heightText by remember(profile) { mutableStateOf(profile?.heightCm?.toString().orEmpty()) }
    var weightText by remember(profile) { mutableStateOf(profile?.weightKg?.toString().orEmpty()) }
    var allergies by remember(profile) { mutableStateOf(profile?.allergies.orEmpty()) }
    var conditions by remember(profile) { mutableStateOf(profile?.conditions.orEmpty()) }
    var emergencyContact by remember(profile) { mutableStateOf(profile?.emergencyContact.orEmpty()) }

    var showDeleteConfirm by remember { mutableStateOf(false) }
    var showAboutDialog by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Header Card
        item {
            Card(shape = RoundedCornerShape(24.dp), modifier = Modifier.fillMaxWidth()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Brush.linearGradient(listOf(DeepNavy, MedicalBlue)))
                        .padding(20.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            shape = CircleShape,
                            color = Color.White.copy(alpha = 0.2f),
                            modifier = Modifier.size(58.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(Icons.Rounded.Person, contentDescription = null, tint = Color.White, modifier = Modifier.size(30.dp))
                            }
                        }
                        Spacer(Modifier.width(14.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(fullName, style = MaterialTheme.typography.titleLarge, color = Color.White, fontWeight = FontWeight.Bold)
                            Text(
                                text = profile?.email?.ifBlank { userEmail } ?: userEmail,
                                style = MaterialTheme.typography.bodySmall,
                                color = Color.White.copy(alpha = 0.85f)
                            )
                            Spacer(Modifier.height(6.dp))
                            StatusPillBadge(
                                label = "CLOUD SYNC ACTIVE",
                                color = ElectricCyan,
                                backgroundColor = Color.White.copy(alpha = 0.16f)
                            )
                        }
                    }
                }
            }
        }

        // Appearance & Language Preferences
        item {
            GlassMedicalCard(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text("Appearance & Language", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Rounded.DarkMode, contentDescription = null, tint = PremiumPurple)
                            Spacer(Modifier.width(10.dp))
                            Column {
                                Text("Dark Mode", style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.SemiBold)
                                Text("OLED-friendly medical night theme", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }
                        Switch(
                            checked = darkModeEnabled,
                            onCheckedChange = onToggleDarkMode,
                            modifier = Modifier.testTag("profile_dark_mode_switch")
                        )
                    }
                    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Rounded.Language, contentDescription = null, tint = MedicalTeal)
                        Spacer(Modifier.width(8.dp))
                        Text("Preferred AI & App Language", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold)
                    }
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        items(PharmaCatalog.supportedLanguages) { lang ->
                            FilterChip(
                                selected = selectedLanguage == lang,
                                onClick = { onSelectLanguage(lang) },
                                label = { Text(lang) }
                            )
                        }
                    }
                }
            }
        }

        // Personal Profile Details
        item {
            GlassMedicalCard(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text("Account Details", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    OutlinedTextField(
                        value = fullName,
                        onValueChange = { fullName = it },
                        label = { Text("Full Name") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        shape = RoundedCornerShape(14.dp)
                    )
                    OutlinedTextField(
                        value = mobile,
                        onValueChange = { mobile = it },
                        label = { Text("Mobile Number (Optional)") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        shape = RoundedCornerShape(14.dp)
                    )
                    OutlinedTextField(
                        value = dob,
                        onValueChange = { dob = it },
                        label = { Text("Date of Birth (YYYY-MM-DD)") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        shape = RoundedCornerShape(14.dp)
                    )
                }
            }
        }

        // Explicit Consent-Gated Health Profile
        item {
            GlassMedicalCard(
                modifier = Modifier.fillMaxWidth(),
                borderColor = MedicalTeal.copy(alpha = 0.4f)
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                            Icon(Icons.Rounded.HealthAndSafety, contentDescription = null, tint = MedicalTeal)
                            Spacer(Modifier.width(10.dp))
                            Column {
                                Text("Optional Health Profile", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                                Text(
                                    "Grant explicit consent to store height, weight, allergies & emergency contact.",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                        Switch(
                            checked = healthConsent,
                            onCheckedChange = { healthConsent = it },
                            modifier = Modifier.testTag("health_consent_switch")
                        )
                    }

                    AnimatedVisibility(visible = healthConsent) {
                        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                                OutlinedTextField(
                                    value = heightText,
                                    onValueChange = { heightText = it },
                                    label = { Text("Height (cm)") },
                                    modifier = Modifier.weight(1f),
                                    singleLine = true,
                                    shape = RoundedCornerShape(14.dp)
                                )
                                OutlinedTextField(
                                    value = weightText,
                                    onValueChange = { weightText = it },
                                    label = { Text("Weight (kg)") },
                                    modifier = Modifier.weight(1f),
                                    singleLine = true,
                                    shape = RoundedCornerShape(14.dp)
                                )
                            }
                            OutlinedTextField(
                                value = allergies,
                                onValueChange = { allergies = it },
                                label = { Text("Known Drug / Food Allergies") },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(14.dp)
                            )
                            OutlinedTextField(
                                value = conditions,
                                onValueChange = { conditions = it },
                                label = { Text("Existing Health Conditions") },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(14.dp)
                            )
                            OutlinedTextField(
                                value = emergencyContact,
                                onValueChange = { emergencyContact = it },
                                label = { Text("Emergency Contact Name & Phone") },
                                modifier = Modifier.fillMaxWidth(),
                                singleLine = true,
                                shape = RoundedCornerShape(14.dp)
                            )
                        }
                    }

                    Button(
                        onClick = {
                            onSaveProfile(
                                fullName,
                                mobile,
                                dob,
                                selectedLanguage,
                                healthConsent,
                                heightText.toDoubleOrNull(),
                                weightText.toDoubleOrNull(),
                                allergies,
                                conditions,
                                emergencyContact
                            )
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .testTag("save_profile_button"),
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        Icon(Icons.Rounded.CheckCircle, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(Modifier.width(8.dp))
                        Text("Save Profile to Cloud")
                    }
                }
            }
        }

        // Data Controls, Privacy, Admin & Support
        item {
            GlassMedicalCard(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(8.dp)) {
                    SettingsActionRow(
                        icon = Icons.Rounded.Notifications,
                        title = "Notification Center",
                        subtitle = "Reminders, expiry alerts & security logs",
                        tint = MedicalBlue,
                        onClick = onOpenNotifications
                    )
                    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                    SettingsActionRow(
                        icon = Icons.Rounded.AdminPanelSettings,
                        title = "Admin Console",
                        subtitle = "Database management, AI & safety metrics",
                        tint = PremiumPurple,
                        onClick = onOpenAdminPanel,
                        testTag = "open_admin_panel_row"
                    )
                    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                    SettingsActionRow(
                        icon = Icons.Rounded.Download,
                        title = "Export Personal Data",
                        subtitle = "Share JSON summary of reminders, cabinet, BMI & reports",
                        tint = MedicalTeal,
                        onClick = {
                            val exportPayload = buildString {
                                appendLine("=== DRxGuard Personal Data Export ===")
                                appendLine("Member: $fullName ($userEmail)")
                                appendLine("Preferred Language: $selectedLanguage")
                                appendLine("Reminders (${reminders.size}): ${reminders.joinToString { "${it.medicineName} ${it.prescribedDose}" }}")
                                appendLine("Cabinet Items (${cabinet.size}): ${cabinet.joinToString { "${it.medicineName} exp ${it.expiryDate}" }}")
                                appendLine("BMI Entries (${bmiHistory.size}): ${bmiHistory.take(5).joinToString { "${it.recordedDate}: ${it.bmiValue}" }}")
                                appendLine("Saved Reports (${reports.size}): ${reports.joinToString { it.title }}")
                            }
                            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                                type = "text/plain"
                                putExtra(Intent.EXTRA_SUBJECT, "DRxGuard Personal Data Export")
                                putExtra(Intent.EXTRA_TEXT, exportPayload)
                            }
                            context.startActivity(Intent.createChooser(shareIntent, "Export DRxGuard Data"))
                        }
                    )
                    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                    SettingsActionRow(
                        icon = Icons.Rounded.Info,
                        title = "About DRxGuard & Privacy",
                        subtitle = "Zero-trust cloud privacy & medical safety disclaimer",
                        tint = SuccessGreen,
                        onClick = { showAboutDialog = true }
                    )
                }
            }
        }

        // App Check & Cloud Security Diagnostics (for physical-device debug APKs)
        item {
            var appCheckDiag by remember {
                mutableStateOf(com.example.data.readAppCheckDiagnostics(context))
            }
            GlassMedicalCard(
                modifier = Modifier.fillMaxWidth(),
                borderColor = ElectricCyan.copy(alpha = 0.35f)
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Rounded.VerifiedUser, contentDescription = null, tint = MedicalTeal)
                        Spacer(Modifier.width(8.dp))
                        Text("App Check & AI Attestation", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    }
                    Text(
                        text = "Provider: ${appCheckDiag.providerName} • Source: ${appCheckDiag.tokenSource}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    if (appCheckDiag.isDebugBuild) {
                        val uuid = appCheckDiag.debugTokenUuid
                        if (!uuid.isNullOrBlank()) {
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.6f),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    text = uuid,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp)
                                )
                            }
                        }
                        OutlinedButton(
                            onClick = {
                                com.example.data.refreshAppCheckDebugToken(context) { updated ->
                                    appCheckDiag = updated
                                    val tok = updated.debugTokenUuid
                                    if (!tok.isNullOrBlank()) {
                                        val cm = context.getSystemService(android.content.Context.CLIPBOARD_SERVICE) as android.content.ClipboardManager
                                        cm.setPrimaryClip(android.content.ClipData.newPlainText("App Check Debug Token", tok))
                                        android.widget.Toast.makeText(context, "App Check Debug UUID copied!", android.widget.Toast.LENGTH_SHORT).show()
                                    } else {
                                        android.widget.Toast.makeText(context, "Token pending — open AI Chat once first.", android.widget.Toast.LENGTH_SHORT).show()
                                    }
                                }
                            },
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth().testTag("profile_copy_appcheck_token")
                        ) {
                            Text(if (uuid != null) "Copy Device App Check Debug UUID" else "Fetch & Copy App Check Debug UUID")
                        }
                    }
                }
            }
        }

        // Account Danger Zone & Logout
        item {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedButton(
                    onClick = onSignOut,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("profile_sign_out_button"),
                    shape = RoundedCornerShape(14.dp),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary)
                ) {
                    Icon(Icons.AutoMirrored.Rounded.Logout, contentDescription = null)
                    Spacer(Modifier.width(8.dp))
                    Text("Sign Out")
                }

                OutlinedButton(
                    onClick = { showDeleteConfirm = true },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("delete_account_data_button"),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = DangerRed),
                    border = BorderStroke(1.dp, DangerRed.copy(alpha = 0.5f))
                ) {
                    Icon(Icons.Rounded.DeleteForever, contentDescription = null)
                    Spacer(Modifier.width(8.dp))
                    Text("Delete My Cloud Data")
                }
            }
        }
    }

    if (showDeleteConfirm) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirm = false },
            title = { Text("Delete All DRxGuard Data?") },
            text = {
                Text("This permanently deletes your profile, reminders, medicine cabinet, BMI history, saved reports, and notifications from Cloud Firestore. This action cannot be undone.")
            },
            confirmButton = {
                Button(
                    onClick = {
                        showDeleteConfirm = false
                        onDeleteAccountData()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = DangerRed)
                ) {
                    Text("Delete Permanently")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteConfirm = false }) { Text("Cancel") }
            }
        )
    }

    if (showAboutDialog) {
        AlertDialog(
            onDismissRequest = { showAboutDialog = false },
            title = { Text("About DRxGuard") },
            text = {
                Text(
                    "DRxGuard — Smarter Medicine. Safer Decisions.\n\n" +
                        "• 16 integrated pharmaceutical intelligence & personal health modules\n" +
                        "• Protected by Firebase App Check, Google Sign-In, and zero-trust owner-scoped Firestore rules\n" +
                        "• Educational Disclaimer: DRxGuard provides general pharmaceutical education only and does not diagnose, prescribe, or replace a licensed healthcare professional."
                )
            },
            confirmButton = {
                TextButton(onClick = { showAboutDialog = false }) { Text("Got It") }
            }
        )
    }
}

@Composable
private fun SettingsActionRow(
    icon: ImageVector,
    title: String,
    subtitle: String,
    tint: Color,
    onClick: () -> Unit,
    testTag: String? = null
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .then(if (testTag != null) Modifier.testTag(testTag) else Modifier)
            .clickable(onClick = onClick)
            .padding(horizontal = 12.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, contentDescription = title, tint = tint, modifier = Modifier.size(22.dp))
        Spacer(Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(title, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold)
            Text(subtitle, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

// =========================================================================
// ADMIN PANEL (Live KPIs + Cloud Medicine Publishing + Safety Audit)
// =========================================================================

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminPanelScreen(
    userEmail: String,
    aiQueriesCount: Int,
    interactionChecksCount: Int,
    scannerRequestsCount: Int,
    labGuardRequestsCount: Int,
    adminMedicines: List<AdminMedicine>,
    onAddAdminMedicine: (AdminMedicine) -> Unit,
    onBack: () -> Unit
) {
    val isBootstrappedAdmin = userEmail.equals("buildolive4@gmail.com", ignoreCase = true)

    var genericName by remember { mutableStateOf("") }
    var brandsInput by remember { mutableStateOf("") }
    var drugClass by remember { mutableStateOf("Analgesics") }
    var strengthsInput by remember { mutableStateOf("500 mg") }
    var composition by remember { mutableStateOf("") }
    var therapeuticUses by remember { mutableStateOf("") }
    var moa by remember { mutableStateOf("") }
    var precautions by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("DRxGuard Admin Console") },
                navigationIcon = {
                    IconButton(onClick = onBack, modifier = Modifier.testTag("admin_back_button")) {
                        Icon(Icons.AutoMirrored.Rounded.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Admin Role Verification Banner
            item {
                Surface(
                    shape = RoundedCornerShape(18.dp),
                    color = if (isBootstrappedAdmin) LocalDRxColors.current.successContainer else LocalDRxColors.current.warningContainer,
                    border = BorderStroke(1.dp, (if (isBootstrappedAdmin) SuccessGreen else WarningAmber).copy(alpha = 0.45f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(modifier = Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = if (isBootstrappedAdmin) Icons.Rounded.VerifiedUser else Icons.Rounded.PrivacyTip,
                            contentDescription = null,
                            tint = if (isBootstrappedAdmin) SuccessGreen else WarningAmber
                        )
                        Spacer(Modifier.width(10.dp))
                        Column {
                            Text(
                                text = if (isBootstrappedAdmin) "Verified Administrator Session" else "Read-Only Telemetry View",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = if (isBootstrappedAdmin) "Signed in as $userEmail. Cloud catalog writes are authorized via Firestore RBAC."
                                else "Signed in as $userEmail. Firestore security rules enforce admin-only writes on /admin_medicines.",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }
            }

            // Live KPI Grid
            item {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text("Live Platform Telemetry", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth()) {
                        AdminKpiCard("AI Pharmacist Queries", aiQueriesCount.toString(), MedicalBlue, Modifier.weight(1f))
                        AdminKpiCard("Interaction Checks", interactionChecksCount.toString(), DangerRed, Modifier.weight(1f))
                    }
                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth()) {
                        AdminKpiCard("Pill Scanner Runs", scannerRequestsCount.toString(), ElectricCyan, Modifier.weight(1f))
                        AdminKpiCard("LabGuard Analyses", labGuardRequestsCount.toString(), PremiumPurple, Modifier.weight(1f))
                    }
                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth()) {
                        AdminKpiCard("Catalog Monographs", (PharmaCatalog.medicines.size + adminMedicines.size).toString(), MedicalTeal, Modifier.weight(1f))
                        AdminKpiCard("Cloud Published", adminMedicines.size.toString(), SuccessGreen, Modifier.weight(1f))
                    }
                }
            }

            // Publish New Medicine to Cloud Database
            item {
                GlassMedicalCard(
                    modifier = Modifier.fillMaxWidth(),
                    borderColor = MedicalBlue.copy(alpha = 0.4f)
                ) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Text("Medicine Database Management", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                        Text(
                            "Publish a verified monograph to Firestore (/admin_medicines). Appears live in Medicine Library.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        OutlinedTextField(
                            value = genericName,
                            onValueChange = { genericName = it },
                            label = { Text("Generic Name *") },
                            modifier = Modifier.fillMaxWidth().testTag("admin_generic_input"),
                            singleLine = true,
                            shape = RoundedCornerShape(12.dp)
                        )
                        OutlinedTextField(
                            value = brandsInput,
                            onValueChange = { brandsInput = it },
                            label = { Text("Brand Names (comma-separated)") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            shape = RoundedCornerShape(12.dp)
                        )
                        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            OutlinedTextField(
                                value = drugClass,
                                onValueChange = { drugClass = it },
                                label = { Text("Drug Class *") },
                                modifier = Modifier.weight(1f),
                                singleLine = true,
                                shape = RoundedCornerShape(12.dp)
                            )
                            OutlinedTextField(
                                value = strengthsInput,
                                onValueChange = { strengthsInput = it },
                                label = { Text("Strengths") },
                                modifier = Modifier.weight(1f),
                                singleLine = true,
                                shape = RoundedCornerShape(12.dp)
                            )
                        }
                        OutlinedTextField(
                            value = composition,
                            onValueChange = { composition = it },
                            label = { Text("Salt Composition") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            shape = RoundedCornerShape(12.dp)
                        )
                        OutlinedTextField(
                            value = therapeuticUses,
                            onValueChange = { therapeuticUses = it },
                            label = { Text("Therapeutic Uses") },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp)
                        )
                        OutlinedTextField(
                            value = moa,
                            onValueChange = { moa = it },
                            label = { Text("Mechanism of Action") },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp)
                        )
                        OutlinedTextField(
                            value = precautions,
                            onValueChange = { precautions = it },
                            label = { Text("Safety Precautions") },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp)
                        )
                        Button(
                            onClick = {
                                if (genericName.isNotBlank() && drugClass.isNotBlank()) {
                                    onAddAdminMedicine(
                                        AdminMedicine(
                                            genericName = genericName.trim(),
                                            brandNames = brandsInput.split(",").map { it.trim() }.filter { it.isNotEmpty() },
                                            drugClass = drugClass.trim(),
                                            strengths = strengthsInput.split(",").map { it.trim() }.filter { it.isNotEmpty() },
                                            dosageForms = listOf("Tablet"),
                                            composition = composition.trim(),
                                            therapeuticUses = therapeuticUses.trim(),
                                            mechanismOfAction = moa.trim(),
                                            precautions = precautions.trim(),
                                            published = true
                                        )
                                    )
                                    genericName = ""
                                    brandsInput = ""
                                    composition = ""
                                    therapeuticUses = ""
                                    moa = ""
                                    precautions = ""
                                }
                            },
                            enabled = genericName.isNotBlank() && drugClass.isNotBlank(),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp)
                                .testTag("admin_publish_medicine_button"),
                            shape = RoundedCornerShape(14.dp)
                        ) {
                            Icon(Icons.Rounded.Add, contentDescription = null)
                            Spacer(Modifier.width(8.dp))
                            Text("Publish to Cloud Catalog")
                        }
                    }
                }
            }

            // AI Safety, Source Governance & Security Controls Summary
            item {
                GlassMedicalCard(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Rounded.Shield, contentDescription = null, tint = MedicalTeal)
                            Spacer(Modifier.width(8.dp))
                            Text("AI Safety & Governance Audit", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                        }
                        Text(
                            text = "• Model Routing: Firebase AI Logic (Gemini Flash) protected by App Check\n" +
                                "• Hard Guardrails Active: Non-diagnostic LabGuard & Symptom guidance, non-definitive Pill Scanner, non-authorizing Change Simulator\n" +
                                "• Clinical Reference Registry: FDA DailyMed, WHO Essential Medicines, BNF, MedlinePlus, GINA, ADA, ACC/AHA\n" +
                                "• Firestore Security Rules: Zero-trust owner isolation + RBAC admin enforcement",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }

            if (adminMedicines.isNotEmpty()) {
                item {
                    Text("Published Cloud Entries (${adminMedicines.size})", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                }
                items(adminMedicines, key = { it.id }) { am ->
                    GlassMedicalCard(modifier = Modifier.fillMaxWidth()) {
                        Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(am.genericName, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                                StatusPillBadge("PUBLISHED", SuccessGreen, SuccessGreen.copy(alpha = 0.14f))
                            }
                            Text("${am.drugClass} • ${am.brandNames.joinToString(", ")}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun AdminKpiCard(title: String, value: String, accent: Color, modifier: Modifier = Modifier) {
    GlassMedicalCard(modifier = modifier, borderColor = accent.copy(alpha = 0.35f)) {
        Column(modifier = Modifier.padding(14.dp)) {
            Text(value, style = MaterialTheme.typography.headlineMedium, color = accent, fontWeight = FontWeight.Bold)
            Text(title, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}
