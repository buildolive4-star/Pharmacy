package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowForward
import androidx.compose.material.icons.automirrored.rounded.CompareArrows
import androidx.compose.material.icons.automirrored.rounded.Logout
import androidx.compose.material.icons.rounded.AccountTree
import androidx.compose.material.icons.rounded.AdminPanelSettings
import androidx.compose.material.icons.rounded.Alarm
import androidx.compose.material.icons.rounded.AutoAwesome
import androidx.compose.material.icons.rounded.Biotech
import androidx.compose.material.icons.rounded.Bookmark
import androidx.compose.material.icons.rounded.CameraAlt
import androidx.compose.material.icons.rounded.Compare
import androidx.compose.material.icons.rounded.Description
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material.icons.rounded.EventAvailable
import androidx.compose.material.icons.rounded.ExpandLess
import androidx.compose.material.icons.rounded.ExpandMore
import androidx.compose.material.icons.rounded.Favorite
import androidx.compose.material.icons.rounded.HealthAndSafety
import androidx.compose.material.icons.rounded.Help
import androidx.compose.material.icons.rounded.History
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material.icons.rounded.Inventory2
import androidx.compose.material.icons.rounded.Language
import androidx.compose.material.icons.rounded.LocalHospital
import androidx.compose.material.icons.rounded.LocalPharmacy
import androidx.compose.material.icons.rounded.Medication
import androidx.compose.material.icons.rounded.Menu
import androidx.compose.material.icons.rounded.MenuBook
import androidx.compose.material.icons.rounded.Mic
import androidx.compose.material.icons.rounded.MonitorWeight
import androidx.compose.material.icons.rounded.Notifications
import androidx.compose.material.icons.rounded.PregnantWoman
import androidx.compose.material.icons.rounded.PrivacyTip
import androidx.compose.material.icons.rounded.QrCodeScanner
import androidx.compose.material.icons.rounded.Science
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material.icons.rounded.SwapHoriz
import androidx.compose.material.icons.rounded.WarningAmber
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.R
import com.example.data.AppNotification
import com.example.data.CabinetMedicine
import com.example.data.MedicineReminder
import com.example.data.PharmaCatalog
import com.example.data.UserProfile
import com.example.ui.components.DRxGuardLogoMark
import com.example.ui.components.GlassMedicalCard
import com.example.ui.components.StatusPillBadge
import com.example.ui.theme.DangerRed
import com.example.ui.theme.DeepNavy
import com.example.ui.theme.ElectricCyan
import com.example.ui.theme.LocalDRxColors
import com.example.ui.theme.MedicalBlue
import com.example.ui.theme.MedicalTeal
import com.example.ui.theme.PregnancyLavender
import com.example.ui.theme.PregnancyPink
import com.example.ui.theme.PremiumPurple
import com.example.ui.theme.SuccessGreen
import com.example.ui.theme.WarningAmber
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

object DRxRoute {
    const val HOME = "home"
    const val EXPLORE = "explore"
    const val AI_PHARMACIST = "ai_pharmacist"
    const val MY_MEDICINES = "my_medicines"
    const val PROFILE = "profile"

    // 16 Features + Supporting Modules
    const val DRUG_INTERACTION = "drug_interaction"
    const val SIDE_EFFECTS = "side_effects"
    const val COMPARE_DRUGS = "compare_drugs"
    const val ALTERNATIVES = "alternatives"
    const val PREGNANCY = "pregnancy"
    const val BMI = "bmi"
    const val PILL_SCANNER = "pill_scanner"
    const val REMINDERS = "reminders"
    const val MEDICINE_LIBRARY = "medicine_library"
    const val MECHANISM = "mechanism"
    const val HEALTH_CONDITIONS = "health_conditions"
    const val DRUG_CLASSES = "drug_classes"
    const val LABGUARD = "labguard"
    const val CHANGE_SIMULATOR = "change_simulator"
    const val EXPIRY_TRACKER = "expiry_tracker"
    const val NOTIFICATIONS = "notifications"
    const val ADMIN_PANEL = "admin_panel"
}

private data class QuickActionItem(
    val title: String,
    val subtitle: String,
    val route: String,
    val icon: ImageVector,
    val accent: Color
)

@Composable
fun HomeDashboardScreen(
    profile: UserProfile?,
    reminders: List<MedicineReminder>,
    cabinet: List<CabinetMedicine>,
    notifications: List<AppNotification>,
    recentlyViewedIds: List<String>,
    recentlyUsedTools: List<String>,
    onOpenDrawer: () -> Unit,
    onNavigate: (String) -> Unit,
    onOpenMedicineDetail: (String) -> Unit
) {
    val ext = LocalDRxColors.current
    val unreadCount = notifications.count { !it.read }
    val todayText = remember {
        SimpleDateFormat("EEEE, d MMMM yyyy", Locale.getDefault()).format(Date())
    }
    val greeting = remember {
        val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
        when (hour) {
            in 5..11 -> "Good Morning!"
            in 12..16 -> "Good Afternoon!"
            else -> "Good Evening!"
        }
    }

    val quickActions = remember {
        listOf(
            QuickActionItem("Drug Interaction", "Multi-drug safety check", DRxRoute.DRUG_INTERACTION, Icons.Rounded.WarningAmber, DangerRed),
            QuickActionItem("Side Effect Checker", "Common, serious & rare", DRxRoute.SIDE_EFFECTS, Icons.Rounded.HealthAndSafety, WarningAmber),
            QuickActionItem("Compare Drugs", "Side-by-side matrix", DRxRoute.COMPARE_DRUGS, Icons.AutoMirrored.Rounded.CompareArrows, MedicalBlue),
            QuickActionItem("Alternatives", "Salt & brand matcher", DRxRoute.ALTERNATIVES, Icons.Rounded.SwapHoriz, MedicalTeal),
            QuickActionItem("Pill Scanner", "AI vision analysis", DRxRoute.PILL_SCANNER, Icons.Rounded.QrCodeScanner, ElectricCyan),
            QuickActionItem("Medicine Reminder", "Smart dose schedules", DRxRoute.REMINDERS, Icons.Rounded.Alarm, PremiumPurple),
            QuickActionItem("BMI Calculator", "Scale & trend history", DRxRoute.BMI, Icons.Rounded.MonitorWeight, SuccessGreen),
            QuickActionItem("LabGuard", "Lab report intelligence", DRxRoute.LABGUARD, Icons.Rounded.Biotech, MedicalBlue)
        )
    }

    val glowInfinite = rememberInfiniteTransition(label = "hero_glow")
    val glowPulse by glowInfinite.animateFloat(
        initialValue = 0.65f,
        targetValue = 1.0f,
        animationSpec = infiniteRepeatable(tween(2200, easing = FastOutSlowInEasing), RepeatMode.Reverse),
        label = "glow_pulse"
    )

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = 28.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp)
    ) {
        // 1. TOP HEADER
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = onOpenDrawer,
                    modifier = Modifier.testTag("hamburger_menu_button")
                ) {
                    Icon(
                        Icons.Rounded.Menu,
                        contentDescription = "Open navigation drawer",
                        tint = MaterialTheme.colorScheme.onBackground
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    DRxGuardLogoMark(size = 30.dp, glowing = false)
                    Spacer(Modifier.width(8.dp))
                    Text(
                        text = "DRxGuard",
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onBackground,
                        fontWeight = FontWeight.Bold
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(
                        onClick = { onNavigate(DRxRoute.NOTIFICATIONS) },
                        modifier = Modifier.testTag("notifications_bell_button")
                    ) {
                        BadgedBox(
                            badge = {
                                if (unreadCount > 0) {
                                    Badge { Text(unreadCount.coerceAtMost(9).toString()) }
                                }
                            }
                        ) {
                            Icon(
                                Icons.Rounded.Notifications,
                                contentDescription = "Notifications",
                                tint = MaterialTheme.colorScheme.onBackground
                            )
                        }
                    }
                    Surface(
                        shape = CircleShape,
                        color = MaterialTheme.colorScheme.primaryContainer,
                        border = BorderStroke(1.5.dp, MaterialTheme.colorScheme.primary),
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .clickable { onNavigate(DRxRoute.PROFILE) }
                            .testTag("profile_avatar_button")
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            val initials = (profile?.fullName ?: "DR")
                                .split(" ")
                                .take(2)
                                .mapNotNull { it.firstOrNull()?.uppercaseChar() }
                                .joinToString("")
                                .ifBlank { "DR" }
                            Text(
                                text = initials,
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }

        // 2. GREETING SECTION
        item {
            Column(modifier = Modifier.padding(horizontal = 20.dp)) {
                Text(
                    text = greeting,
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Text(
                    text = "Welcome to DRxGuard${profile?.fullName?.let { ", ${it.substringBefore(" ")}" }.orEmpty()}",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(Modifier.height(2.dp))
                Text(
                    text = todayText,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        // 3. FUTURISTIC AI SEARCH BAR
        item {
            GlassMedicalCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
                    .clickable { onNavigate(DRxRoute.MEDICINE_LIBRARY) }
                    .testTag("home_ai_search_bar"),
                borderColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.35f)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 14.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Rounded.Search,
                        contentDescription = "Search",
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Spacer(Modifier.width(10.dp))
                    Text(
                        text = "Search medicines, symptoms, drug interactions...",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f)
                    )
                    IconButton(
                        onClick = { onNavigate(DRxRoute.AI_PHARMACIST) },
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(
                            Icons.Rounded.Mic,
                            contentDescription = "Voice search",
                            tint = MedicalTeal,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    IconButton(
                        onClick = { onNavigate(DRxRoute.PILL_SCANNER) },
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(
                            Icons.Rounded.CameraAlt,
                            contentDescription = "Camera scanner shortcut",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Surface(
                        shape = CircleShape,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier
                            .size(34.dp)
                            .clip(CircleShape)
                            .clickable { onNavigate(DRxRoute.AI_PHARMACIST) }
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                Icons.Rounded.AutoAwesome,
                                contentDescription = "Ask AI shortcut",
                                tint = Color.White,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }
            }
        }

        // 4. HERO AI CARD
        item {
            Card(
                shape = RoundedCornerShape(26.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
                border = BorderStroke(1.5.dp, ElectricCyan.copy(alpha = glowPulse * 0.8f)),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Brush.linearGradient(listOf(DeepNavy, MedicalBlue, Color(0xFF1599C4))))
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.img_hero_ai_pharmacist),
                        contentDescription = "3D AI Medical Assistant",
                        contentScale = ContentScale.Crop,
                        alpha = 0.36f,
                        modifier = Modifier.matchParentSize()
                    )
                    Column(modifier = Modifier.padding(22.dp)) {
                        Surface(
                            shape = CircleShape,
                            color = ElectricCyan.copy(alpha = 0.25f)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    Icons.Rounded.AutoAwesome,
                                    contentDescription = null,
                                    tint = ElectricCyan,
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(Modifier.width(4.dp))
                                Text(
                                    text = "9 LANGUAGES • EVIDENCE-GUIDED",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = Color.White
                                )
                            }
                        }
                        Spacer(Modifier.height(12.dp))
                        Text(
                            text = "Meet Your AI Pharmacist",
                            style = MaterialTheme.typography.headlineMedium,
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(Modifier.height(6.dp))
                        Text(
                            text = "Get understandable information about medicines, interactions and pharmaceutical topics.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.White.copy(alpha = 0.92f)
                        )
                        Spacer(Modifier.height(16.dp))
                        Button(
                            onClick = { onNavigate(DRxRoute.AI_PHARMACIST) },
                            shape = RoundedCornerShape(14.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color.White,
                                contentColor = DeepNavy
                            ),
                            modifier = Modifier.testTag("ask_ai_now_button")
                        ) {
                            Icon(Icons.Rounded.AutoAwesome, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(Modifier.width(8.dp))
                            Text("Ask AI Now", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        // 5. QUICK ACTIONS 2-COLUMN GRID (ALL 8 TOOLS)
        item {
            Column(modifier = Modifier.padding(horizontal = 20.dp)) {
                SectionHeader(title = "Clinical Quick Tools", actionLabel = "Explore All") {
                    onNavigate(DRxRoute.EXPLORE)
                }
                Spacer(Modifier.height(12.dp))
                quickActions.chunked(2).forEach { rowItems ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        rowItems.forEach { item ->
                            QuickActionCard(
                                item = item,
                                onClick = { onNavigate(item.route) },
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                    Spacer(Modifier.height(12.dp))
                }
            }
        }

        // 6. PREGNANCY SUPPORT CARD
        item {
            Card(
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = ext.pregnancySurface),
                border = BorderStroke(1.dp, ext.pregnancyAccent.copy(alpha = 0.35f)),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
                    .clickable { onNavigate(DRxRoute.PREGNANCY) }
                    .testTag("pregnancy_support_card")
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        StatusPillBadge(
                            label = "MATERNAL WELLNESS",
                            color = PregnancyPink,
                            backgroundColor = PregnancyPink.copy(alpha = 0.14f)
                        )
                        Spacer(Modifier.height(8.dp))
                        Text(
                            text = "Pregnancy Support",
                            style = MaterialTheme.typography.titleLarge,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(Modifier.height(4.dp))
                        Text(
                            text = "Track pregnancy milestones and explore helpful educational tools.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(Modifier.height(12.dp))
                        Button(
                            onClick = { onNavigate(DRxRoute.PREGNANCY) },
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = PregnancyLavender),
                            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
                        ) {
                            Text("Explore Tools", style = MaterialTheme.typography.labelMedium)
                        }
                    }
                    Spacer(Modifier.width(12.dp))
                    Image(
                        painter = painterResource(id = R.drawable.img_pregnancy_care),
                        contentDescription = "Pregnancy Support illustration",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .size(104.dp)
                            .clip(RoundedCornerShape(20.dp))
                    )
                }
            }
        }

        // 7. MEDICINE LIBRARY CATEGORIES
        item {
            Column {
                Box(modifier = Modifier.padding(horizontal = 20.dp)) {
                    SectionHeader(title = "Medicine Library", actionLabel = "Browse") {
                        onNavigate(DRxRoute.MEDICINE_LIBRARY)
                    }
                }
                Spacer(Modifier.height(10.dp))
                val categories = listOf(
                    "Antibiotics" to MedicalBlue,
                    "Analgesics" to DangerRed,
                    "Antacids" to MedicalTeal,
                    "Antihistamines" to PremiumPurple,
                    "Antidiabetics" to WarningAmber,
                    "Antihypertensives" to SuccessGreen
                )
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 20.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(categories) { (cat, color) ->
                        Surface(
                            shape = RoundedCornerShape(16.dp),
                            color = color.copy(alpha = 0.12f),
                            border = BorderStroke(1.dp, color.copy(alpha = 0.28f)),
                            modifier = Modifier
                                .clip(RoundedCornerShape(16.dp))
                                .clickable { onNavigate(DRxRoute.MEDICINE_LIBRARY) }
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Rounded.Medication, contentDescription = null, tint = color, modifier = Modifier.size(18.dp))
                                Spacer(Modifier.width(8.dp))
                                Text(cat, style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.onSurface)
                            }
                        }
                    }
                }
            }
        }

        // 8. UPCOMING REMINDERS & EXPIRY ALERTS SNAPSHOT
        item {
            Column(modifier = Modifier.padding(horizontal = 20.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                SectionHeader(title = "Reminders & Cabinet Alerts", actionLabel = "Manage") {
                    onNavigate(DRxRoute.MY_MEDICINES)
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    val upcomingCount = reminders.count { it.status == "UPCOMING" || it.status == "SNOOZED" }
                    GlassMedicalCard(
                        modifier = Modifier
                            .weight(1f)
                            .clickable { onNavigate(DRxRoute.REMINDERS) }
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Icon(Icons.Rounded.Alarm, contentDescription = null, tint = PremiumPurple)
                            Spacer(Modifier.height(8.dp))
                            Text(
                                text = "$upcomingCount Upcoming",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = reminders.firstOrNull()?.medicineName ?: "Schedule prescribed doses",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }

                    GlassMedicalCard(
                        modifier = Modifier
                            .weight(1f)
                            .clickable { onNavigate(DRxRoute.EXPIRY_TRACKER) }
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Icon(Icons.Rounded.Inventory2, contentDescription = null, tint = WarningAmber)
                            Spacer(Modifier.height(8.dp))
                            Text(
                                text = "${cabinet.size} In Cabinet",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Track expiry alerts",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }

        // 9. RECENTLY VIEWED & SAVED MEDICINES
        item {
            Column {
                Box(modifier = Modifier.padding(horizontal = 20.dp)) {
                    SectionHeader(title = "Recently Viewed Medicines", actionLabel = "Library") {
                        onNavigate(DRxRoute.MEDICINE_LIBRARY)
                    }
                }
                Spacer(Modifier.height(10.dp))
                val recentMeds = recentlyViewedIds.mapNotNull { PharmaCatalog.findById(it) }
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 20.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(recentMeds) { med ->
                        GlassMedicalCard(
                            modifier = Modifier
                                .width(196.dp)
                                .clickable { onOpenMedicineDetail(med.id) }
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                StatusPillBadge(
                                    label = med.category,
                                    color = MaterialTheme.colorScheme.primary,
                                    backgroundColor = MaterialTheme.colorScheme.primaryContainer
                                )
                                Spacer(Modifier.height(8.dp))
                                Text(
                                    text = med.genericName,
                                    style = MaterialTheme.typography.titleSmall,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Text(
                                    text = med.brandNames.take(2).joinToString(" • "),
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }
                    }
                }
            }
        }

        // 10. RECENTLY USED TOOLS + SCIENTIFIC DISCOVERY SHORTCUTS
        item {
            Column(modifier = Modifier.padding(horizontal = 20.dp)) {
                SectionHeader(title = "Pharmaceutical Intelligence", actionLabel = "More") {
                    onNavigate(DRxRoute.EXPLORE)
                }
                Spacer(Modifier.height(10.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    MiniDiscoveryTile(
                        title = "Mechanism of Action",
                        icon = Icons.Rounded.Science,
                        color = ElectricCyan,
                        onClick = { onNavigate(DRxRoute.MECHANISM) },
                        modifier = Modifier.weight(1f)
                    )
                    MiniDiscoveryTile(
                        title = "Change Simulator",
                        icon = Icons.Rounded.Compare,
                        color = PremiumPurple,
                        onClick = { onNavigate(DRxRoute.CHANGE_SIMULATOR) },
                        modifier = Modifier.weight(1f)
                    )
                    MiniDiscoveryTile(
                        title = "Drug Classes",
                        icon = Icons.Rounded.AccountTree,
                        color = MedicalTeal,
                        onClick = { onNavigate(DRxRoute.DRUG_CLASSES) },
                        modifier = Modifier.weight(1f)
                    )
                }
                if (recentlyUsedTools.isNotEmpty()) {
                    Spacer(Modifier.height(10.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Rounded.History, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(16.dp))
                        Spacer(Modifier.width(6.dp))
                        Text(
                            text = "Recent: ${recentlyUsedTools.take(3).joinToString(" · ")}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }

        // 11. LATEST PHARMACEUTICAL ARTICLES
        item {
            Column(modifier = Modifier.padding(horizontal = 20.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                SectionHeader(title = "Pharmaceutical Articles", actionLabel = "Conditions") {
                    onNavigate(DRxRoute.HEALTH_CONDITIONS)
                }
                PharmaCatalog.articles.forEach { article ->
                    GlassMedicalCard(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onNavigate(DRxRoute.HEALTH_CONDITIONS) }
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                StatusPillBadge(
                                    label = article.category,
                                    color = MedicalTeal,
                                    backgroundColor = MedicalTeal.copy(alpha = 0.14f)
                                )
                                Text(
                                    text = "${article.readMinutes} min read",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            Spacer(Modifier.height(8.dp))
                            Text(article.title, style = MaterialTheme.typography.titleMedium)
                            Spacer(Modifier.height(4.dp))
                            Text(
                                article.summary,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SectionHeader(title: String, actionLabel: String, onAction: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onBackground
        )
        Text(
            text = actionLabel,
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier
                .clip(RoundedCornerShape(8.dp))
                .clickable(onClick = onAction)
                .padding(horizontal = 6.dp, vertical = 4.dp)
        )
    }
}

@Composable
private fun QuickActionCard(
    item: QuickActionItem,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    GlassMedicalCard(
        modifier = modifier
            .clickable(onClick = onClick)
            .testTag("quick_action_${item.route}"),
        borderColor = item.accent.copy(alpha = 0.25f)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Surface(
                shape = RoundedCornerShape(14.dp),
                color = item.accent.copy(alpha = 0.14f),
                modifier = Modifier.size(44.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(item.icon, contentDescription = item.title, tint = item.accent, modifier = Modifier.size(24.dp))
                }
            }
            Spacer(Modifier.height(10.dp))
            Text(
                text = item.title,
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = item.subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
private fun MiniDiscoveryTile(
    title: String,
    icon: ImageVector,
    color: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    GlassMedicalCard(
        modifier = modifier.clickable(onClick = onClick),
        borderColor = color.copy(alpha = 0.3f)
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(icon, contentDescription = title, tint = color, modifier = Modifier.size(24.dp))
            Spacer(Modifier.height(6.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.labelMedium,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

/**
 * Explore Hub (Bottom Tab 2): Complete directory of all 16 DRxGuard modules
 */
@Composable
fun ExploreHubScreen(onNavigate: (String) -> Unit) {
    val sections = remember {
        listOf(
            "Clinical Safety Tools" to listOf(
                QuickActionItem("Drug Interaction Checker", "Check multi-drug severity & evidence", DRxRoute.DRUG_INTERACTION, Icons.Rounded.WarningAmber, DangerRed),
                QuickActionItem("Side Effect Checker", "Common, serious & rare adverse effects", DRxRoute.SIDE_EFFECTS, Icons.Rounded.HealthAndSafety, WarningAmber),
                QuickActionItem("Compare Drugs", "12-parameter clinical comparison", DRxRoute.COMPARE_DRUGS, Icons.AutoMirrored.Rounded.CompareArrows, MedicalBlue),
                QuickActionItem("Medicine Alternatives", "Exact salt vs similar therapeutic options", DRxRoute.ALTERNATIVES, Icons.Rounded.SwapHoriz, MedicalTeal)
            ),
            "AI & Diagnostic Companions" to listOf(
                QuickActionItem("Multilingual AI Pharmacist", "Structured answers in 9 languages", DRxRoute.AI_PHARMACIST, Icons.Rounded.AutoAwesome, MedicalBlue),
                QuickActionItem("Pill Identification Scanner", "AI visual shape, color & imprint analysis", DRxRoute.PILL_SCANNER, Icons.Rounded.QrCodeScanner, ElectricCyan),
                QuickActionItem("LabGuard Report Analyzer", "CBC, HbA1c, Lipid, Thyroid & AI insights", DRxRoute.LABGUARD, Icons.Rounded.Biotech, PremiumPurple),
                QuickActionItem("Medicine Change Simulator", "Educational pharmacological comparison", DRxRoute.CHANGE_SIMULATOR, Icons.Rounded.Compare, MedicalTeal)
            ),
            "Pharmaceutical Knowledge" to listOf(
                QuickActionItem("Medicine Information Library", "16-section monographs with bookmarks", DRxRoute.MEDICINE_LIBRARY, Icons.Rounded.MenuBook, MedicalBlue),
                QuickActionItem("Mechanism of Action", "Animated receptors: Simple & Advanced", DRxRoute.MECHANISM, Icons.Rounded.Science, ElectricCyan),
                QuickActionItem("Health Conditions Library", "8 medical specialties educational guides", DRxRoute.HEALTH_CONDITIONS, Icons.Rounded.LocalHospital, DangerRed),
                QuickActionItem("Drug Classes Explorer", "Interactive Class -> Subclass -> Brand tree", DRxRoute.DRUG_CLASSES, Icons.Rounded.AccountTree, SuccessGreen)
            ),
            "Personal Health & Maternal Care" to listOf(
                QuickActionItem("Pregnancy Support", "Due date, fertility, growth & symptoms", DRxRoute.PREGNANCY, Icons.Rounded.PregnantWoman, PregnancyPink),
                QuickActionItem("BMI Calculator", "Multi-unit gauge & saved trend chart", DRxRoute.BMI, Icons.Rounded.MonitorWeight, SuccessGreen),
                QuickActionItem("Smart Medicine Reminder", "Prescribed schedules & monthly calendar", DRxRoute.REMINDERS, Icons.Rounded.Alarm, PremiumPurple),
                QuickActionItem("Medicine Expiry Tracker", "Digital cabinet & 90/30/7-day alerts", DRxRoute.EXPIRY_TRACKER, Icons.Rounded.Inventory2, WarningAmber)
            )
        )
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(20.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp)
    ) {
        item {
            Column {
                Text("Explore DRxGuard", style = MaterialTheme.typography.headlineMedium)
                Text(
                    "All 16 pharmaceutical intelligence modules in one unified ecosystem.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
        sections.forEach { (sectionTitle, itemsList) ->
            item {
                Text(
                    text = sectionTitle,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary
                )
            }
            items(itemsList) { tool ->
                GlassMedicalCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onNavigate(tool.route) }
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            shape = RoundedCornerShape(14.dp),
                            color = tool.accent.copy(alpha = 0.14f),
                            modifier = Modifier.size(46.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(tool.icon, contentDescription = null, tint = tool.accent)
                            }
                        }
                        Spacer(Modifier.width(14.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(tool.title, style = MaterialTheme.typography.titleSmall)
                            Text(
                                tool.subtitle,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Icon(
                            Icons.AutoMirrored.Rounded.ArrowForward,
                            contentDescription = "Open ${tool.title}",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}

/**
 * Universal Three-Line Side Navigation Drawer with expandable categories
 */
private data class DrawerMenuItem(
    val title: String,
    val description: String,
    val icon: ImageVector,
    val route: String
)

@Composable
fun DRxNavigationDrawerContent(
    profile: UserProfile?,
    userEmail: String,
    onNavigate: (String) -> Unit,
    onLogout: () -> Unit
) {
    val expandedState = remember {
        mutableStateMapOf(
            "HEALTH TOOLS" to true,
            "PREGNANCY SUPPORT" to false,
            "MEDICINE MANAGEMENT" to true,
            "PHARMACEUTICAL KNOWLEDGE" to false,
            "ADVANCED TOOLS" to true,
            "OTHER" to false
        )
    }

    val categories = remember {
        listOf(
            "HEALTH TOOLS" to listOf(
                DrawerMenuItem("Drug Interaction Checker", "Check multi-drug interactions", Icons.Rounded.WarningAmber, DRxRoute.DRUG_INTERACTION),
                DrawerMenuItem("Side Effect Checker", "Common, serious & rare effects", Icons.Rounded.HealthAndSafety, DRxRoute.SIDE_EFFECTS),
                DrawerMenuItem("Compare Drugs", "Side-by-side clinical comparison", Icons.AutoMirrored.Rounded.CompareArrows, DRxRoute.COMPARE_DRUGS),
                DrawerMenuItem("Medicine Alternatives", "Exact salt & therapeutic matches", Icons.Rounded.SwapHoriz, DRxRoute.ALTERNATIVES),
                DrawerMenuItem("Pill Scanner", "AI visual pill inspection", Icons.Rounded.QrCodeScanner, DRxRoute.PILL_SCANNER),
                DrawerMenuItem("BMI Calculator", "Multi-unit gauge & history", Icons.Rounded.MonitorWeight, DRxRoute.BMI)
            ),
            "PREGNANCY SUPPORT" to listOf(
                DrawerMenuItem("Due Date Countdown", "LMP timeline & week tracker", Icons.Rounded.EventAvailable, DRxRoute.PREGNANCY),
                DrawerMenuItem("Fertility Forecaster", "Ovulation & fertile window", Icons.Rounded.Favorite, DRxRoute.PREGNANCY),
                DrawerMenuItem("Growth Tracker", "Week-by-week fetal milestones", Icons.Rounded.PregnantWoman, DRxRoute.PREGNANCY),
                DrawerMenuItem("Symptom Analysis", "Cautious educational guidance", Icons.Rounded.HealthAndSafety, DRxRoute.PREGNANCY)
            ),
            "MEDICINE MANAGEMENT" to listOf(
                DrawerMenuItem("Smart Medicine Reminder", "Prescribed schedules & calendar", Icons.Rounded.Alarm, DRxRoute.REMINDERS),
                DrawerMenuItem("My Medicine Cabinet", "Unified medicine dashboard", Icons.Rounded.LocalPharmacy, DRxRoute.MY_MEDICINES),
                DrawerMenuItem("Expiry Tracker", "90/30/7-day expiry alerts", Icons.Rounded.Inventory2, DRxRoute.EXPIRY_TRACKER),
                DrawerMenuItem("Saved Medicines", "Bookmarked monographs", Icons.Rounded.Bookmark, DRxRoute.MY_MEDICINES)
            ),
            "PHARMACEUTICAL KNOWLEDGE" to listOf(
                DrawerMenuItem("Medicine Library", "16-section verified database", Icons.Rounded.MenuBook, DRxRoute.MEDICINE_LIBRARY),
                DrawerMenuItem("Mechanism of Action", "Interactive molecular pathways", Icons.Rounded.Science, DRxRoute.MECHANISM),
                DrawerMenuItem("Health Conditions", "Educational disease guides", Icons.Rounded.LocalHospital, DRxRoute.HEALTH_CONDITIONS),
                DrawerMenuItem("Drug Classes", "Classification hierarchy tree", Icons.Rounded.AccountTree, DRxRoute.DRUG_CLASSES)
            ),
            "ADVANCED TOOLS" to listOf(
                DrawerMenuItem("LabGuard", "Laboratory report analysis", Icons.Rounded.Biotech, DRxRoute.LABGUARD),
                DrawerMenuItem("Medicine Change Simulator", "Explore pharmacological differences", Icons.Rounded.Compare, DRxRoute.CHANGE_SIMULATOR),
                DrawerMenuItem("AI Pharmacist", "Multilingual Gemini AI chat", Icons.Rounded.AutoAwesome, DRxRoute.AI_PHARMACIST)
            ),
            "OTHER" to listOf(
                DrawerMenuItem("Notifications", "Unified alert center", Icons.Rounded.Notifications, DRxRoute.NOTIFICATIONS),
                DrawerMenuItem("My Reports", "Saved interactions & lab summaries", Icons.Rounded.Description, DRxRoute.MY_MEDICINES),
                DrawerMenuItem("Admin Panel", "Database, AI & security console", Icons.Rounded.AdminPanelSettings, DRxRoute.ADMIN_PANEL),
                DrawerMenuItem("Settings & Language", "Theme, dark mode & 9 languages", Icons.Rounded.Settings, DRxRoute.PROFILE),
                DrawerMenuItem("Privacy & Security", "Consent & account data controls", Icons.Rounded.PrivacyTip, DRxRoute.PROFILE),
                DrawerMenuItem("Help & About DRxGuard", "Clinical sources & safety notice", Icons.Rounded.Info, DRxRoute.PROFILE)
            )
        )
    }

    ModalDrawerSheet(
        modifier = Modifier.width(320.dp),
        drawerContainerColor = MaterialTheme.colorScheme.surface
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = 24.dp)
        ) {
            // Profile Header
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Brush.linearGradient(listOf(DeepNavy, MedicalBlue)))
                        .padding(20.dp)
                ) {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Surface(
                                shape = CircleShape,
                                color = Color.White.copy(alpha = 0.2f),
                                modifier = Modifier.size(54.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    DRxGuardLogoMark(size = 42.dp, glowing = false)
                                }
                            }
                            Spacer(Modifier.width(12.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = profile?.fullName ?: "DRxGuard Member",
                                    style = MaterialTheme.typography.titleMedium,
                                    color = Color.White,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Text(
                                    text = profile?.email?.ifBlank { userEmail } ?: userEmail,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Color.White.copy(alpha = 0.85f),
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Spacer(Modifier.height(4.dp))
                                StatusPillBadge(
                                    label = "VERIFIED CLOUD ACCOUNT",
                                    color = ElectricCyan,
                                    backgroundColor = Color.White.copy(alpha = 0.14f)
                                )
                            }
                        }
                        Spacer(Modifier.height(14.dp))
                        Button(
                            onClick = { onNavigate(DRxRoute.PROFILE) },
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color.White.copy(alpha = 0.18f),
                                contentColor = Color.White
                            ),
                            contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp)
                        ) {
                            Icon(Icons.Rounded.Edit, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(Modifier.width(6.dp))
                            Text("Edit Profile", style = MaterialTheme.typography.labelMedium)
                        }
                    }
                }
            }

            // Expandable Categories
            categories.forEach { (categoryName, itemsList) ->
                item(key = "cat_$categoryName") {
                    val isExpanded = expandedState[categoryName] ?: false
                    Column {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { expandedState[categoryName] = !isExpanded }
                                .padding(horizontal = 18.dp, vertical = 14.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = categoryName,
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.Bold
                            )
                            Icon(
                                imageVector = if (isExpanded) Icons.Rounded.ExpandLess else Icons.Rounded.ExpandMore,
                                contentDescription = "Toggle $categoryName",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        AnimatedVisibility(visible = isExpanded) {
                            Column(modifier = Modifier.padding(bottom = 8.dp)) {
                                itemsList.forEach { entry ->
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clickable { onNavigate(entry.route) }
                                            .padding(horizontal = 20.dp, vertical = 10.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(
                                            entry.icon,
                                            contentDescription = null,
                                            tint = MaterialTheme.colorScheme.primary,
                                            modifier = Modifier.size(20.dp)
                                        )
                                        Spacer(Modifier.width(12.dp))
                                        Column {
                                            Text(entry.title, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold)
                                            Text(
                                                entry.description,
                                                style = MaterialTheme.typography.bodySmall,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                        }
                                    }
                                }
                            }
                        }
                        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                    }
                }
            }

            // Logout
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable(onClick = onLogout)
                        .padding(horizontal = 20.dp, vertical = 16.dp)
                        .testTag("drawer_logout_button"),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.AutoMirrored.Rounded.Logout, contentDescription = "Logout", tint = DangerRed)
                    Spacer(Modifier.width(12.dp))
                    Column {
                        Text("Logout", style = MaterialTheme.typography.titleSmall, color = DangerRed)
                        Text("Sign out of DRxGuard cloud session", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }
        }
    }
}
