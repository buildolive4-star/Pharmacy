package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Alarm
import androidx.compose.material.icons.rounded.Bookmark
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.DeleteOutline
import androidx.compose.material.icons.rounded.Description
import androidx.compose.material.icons.rounded.Inventory2
import androidx.compose.material.icons.rounded.NotificationsActive
import androidx.compose.material.icons.rounded.Snooze
import androidx.compose.material.icons.rounded.Undo
import androidx.compose.material.icons.rounded.WarningAmber
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.data.CabinetMedicine
import com.example.data.MedicineReminder
import com.example.data.PharmaCatalog
import com.example.data.SavedReport
import com.example.ui.components.GlassMedicalCard
import com.example.ui.components.MedicalSafetyBanner
import com.example.ui.components.StatusPillBadge
import com.example.ui.theme.DangerRed
import com.example.ui.theme.LocalDRxColors
import com.example.ui.theme.MedicalBlue
import com.example.ui.theme.MedicalTeal
import com.example.ui.theme.PremiumPurple
import com.example.ui.theme.SuccessGreen
import com.example.ui.theme.WarningAmber
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.concurrent.TimeUnit

// =========================================================================
// FEATURE 08 – SMART MEDICINE REMINDER
// =========================================================================

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun SmartMedicineReminderScreen(
    reminders: List<MedicineReminder>,
    onAddReminder: (MedicineReminder) -> Unit,
    onUpdateStatus: (id: String, status: String) -> Unit,
    onDeleteReminder: (id: String) -> Unit,
    onSimulateNotification: (title: String, msg: String) -> Unit,
    onBack: () -> Unit
) {
    val ext = LocalDRxColors.current
    var selectedTab by remember { mutableIntStateOf(0) }
    var showAddDialog by remember { mutableStateOf(false) }

    val filtered = remember(reminders, selectedTab) {
        when (selectedTab) {
            0 -> reminders // Today / All
            1 -> reminders.filter { it.status == "UPCOMING" || it.status == "SNOOZED" }
            2 -> reminders.filter { it.status == "TAKEN" }
            else -> reminders.filter { it.status == "MISSED" || it.status == "SKIPPED" }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Smart Medicine Reminder") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Rounded.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddDialog = true },
                containerColor = MaterialTheme.colorScheme.primary,
                modifier = Modifier.testTag("add_reminder_fab")
            ) {
                Icon(Icons.Rounded.Add, contentDescription = "Add Medicine Reminder", tint = Color.White)
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding),
            contentPadding = PaddingValues(20.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Interactive Simulated Notification Preview Banner
            val nextDue = reminders.firstOrNull { it.status == "UPCOMING" || it.status == "SNOOZED" }
            if (nextDue != null) {
                item {
                    GlassMedicalCard(
                        modifier = Modifier.fillMaxWidth(),
                        borderColor = PremiumPurple.copy(alpha = 0.55f)
                    ) {
                        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Rounded.NotificationsActive, contentDescription = null, tint = PremiumPurple)
                                Spacer(Modifier.width(8.dp))
                                Text("Active Reminder Card", style = MaterialTheme.typography.titleSmall, color = PremiumPurple)
                                Spacer(Modifier.weight(1f))
                                StatusPillBadge(nextDue.reminderTimes.firstOrNull() ?: "08:00", PremiumPurple, PremiumPurple.copy(alpha = 0.14f))
                            }
                            Text("${nextDue.medicineName} ${nextDue.strength}", style = MaterialTheme.typography.titleLarge)
                            Text(
                                "Prescribed Dose: ${nextDue.prescribedDose} • ${nextDue.dosageForm} • ${nextDue.scheduleType}",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                Button(
                                    onClick = { onUpdateStatus(nextDue.id, "TAKEN") },
                                    colors = ButtonDefaults.buttonColors(containerColor = SuccessGreen),
                                    modifier = Modifier.weight(1f).testTag("mark_taken_button")
                                ) {
                                    Icon(Icons.Rounded.Check, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(Modifier.width(4.dp))
                                    Text("Taken")
                                }
                                OutlinedButton(
                                    onClick = {
                                        onUpdateStatus(nextDue.id, "SNOOZED")
                                        onSimulateNotification("Snoozed: ${nextDue.medicineName}", "Will remind again in 15 minutes.")
                                    },
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Icon(Icons.Rounded.Snooze, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(Modifier.width(4.dp))
                                    Text("Snooze")
                                }
                                OutlinedButton(
                                    onClick = { onUpdateStatus(nextDue.id, "SKIPPED") },
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Text("Skip")
                                }
                            }
                        }
                    }
                }
            }

            // Filter Tabs
            item {
                TabRow(selectedTabIndex = selectedTab) {
                    Tab(selected = selectedTab == 0, onClick = { selectedTab = 0 }, text = { Text("Today (${reminders.size})") })
                    Tab(selected = selectedTab == 1, onClick = { selectedTab = 1 }, text = { Text("Upcoming") })
                    Tab(selected = selectedTab == 2, onClick = { selectedTab = 2 }, text = { Text("Taken") })
                    Tab(selected = selectedTab == 3, onClick = { selectedTab = 3 }, text = { Text("Skipped") })
                }
            }

            if (filtered.isEmpty()) {
                item {
                    GlassMedicalCard(modifier = Modifier.fillMaxWidth()) {
                        Column(
                            modifier = Modifier.fillMaxWidth().padding(22.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(Icons.Rounded.Alarm, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(40.dp))
                            Text("No reminders in this view", style = MaterialTheme.typography.titleMedium)
                            Text(
                                "Tap + to enter your doctor-prescribed medicine schedule. DRxGuard never assumes schedules automatically.",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Button(onClick = { showAddDialog = true }) {
                                Text("Add Prescribed Medicine")
                            }
                        }
                    }
                }
            } else {
                items(filtered, key = { it.id }) { rem ->
                    val (statusColor, statusBg) = when (rem.status) {
                        "TAKEN" -> ext.success to ext.successContainer
                        "SNOOZED" -> ext.warning to ext.warningContainer
                        "SKIPPED", "MISSED" -> ext.danger to ext.dangerContainer
                        else -> MedicalBlue to MaterialTheme.colorScheme.primaryContainer
                    }
                    GlassMedicalCard(modifier = Modifier.fillMaxWidth()) {
                        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("${rem.medicineName} ${rem.strength}".trim(), style = MaterialTheme.typography.titleMedium)
                                StatusPillBadge(rem.status, statusColor, statusBg)
                            }
                            Text(
                                "${rem.prescribedDose} (${rem.dosageForm}) • ${rem.scheduleType} at ${rem.reminderTimes.joinToString(", ")}",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.primary
                            )
                            if (rem.prescribingProfessional.isNotBlank()) {
                                Text("Prescriber: ${rem.prescribingProfessional}", style = MaterialTheme.typography.bodySmall)
                            }
                            if (rem.instructions.isNotBlank()) {
                                Text("Instructions: ${rem.instructions}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    if (rem.status != "TAKEN") {
                                        TextButton(onClick = { onUpdateStatus(rem.id, "TAKEN") }) {
                                            Text("Mark Taken")
                                        }
                                    } else {
                                        // Option to correct accidental entries
                                        TextButton(onClick = { onUpdateStatus(rem.id, "UPCOMING") }) {
                                            Icon(Icons.Rounded.Undo, contentDescription = null, modifier = Modifier.size(16.dp))
                                            Spacer(Modifier.width(4.dp))
                                            Text("Correct Entry")
                                        }
                                    }
                                    if (rem.status != "SKIPPED") {
                                        TextButton(onClick = { onUpdateStatus(rem.id, "SKIPPED") }) {
                                            Text("Skip")
                                        }
                                    }
                                }
                                IconButton(onClick = { onDeleteReminder(rem.id) }) {
                                    Icon(Icons.Rounded.DeleteOutline, contentDescription = "Delete reminder", tint = DangerRed)
                                }
                            }
                        }
                    }
                }
            }

            // Monthly Medication Adherence Calendar
            item {
                GlassMedicalCard(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Monthly Medication Calendar", style = MaterialTheme.typography.titleMedium)
                        Spacer(Modifier.height(6.dp))
                        Text("Color-coded adherence overview (Green: Taken • Blue: Upcoming • Amber: Partial)", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Spacer(Modifier.height(12.dp))
                        val takenCount = reminders.count { it.status == "TAKEN" }
                        FlowRow(
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            for (day in 1..30) {
                                val bg = when {
                                    day < 23 && day % 6 == 0 -> ext.warningContainer
                                    day < 23 -> ext.successContainer
                                    day == 23 -> if (takenCount > 0) ext.successContainer else MaterialTheme.colorScheme.primaryContainer
                                    else -> MaterialTheme.colorScheme.surfaceVariant
                                }
                                val fg = when {
                                    day < 23 && day % 6 == 0 -> ext.warning
                                    day < 23 -> ext.success
                                    day == 23 -> if (takenCount > 0) ext.success else MaterialTheme.colorScheme.primary
                                    else -> MaterialTheme.colorScheme.onSurfaceVariant
                                }
                                Surface(
                                    shape = CircleShape,
                                    color = bg,
                                    border = if (day == 23) BorderStroke(1.5.dp, MaterialTheme.colorScheme.primary) else null,
                                    modifier = Modifier.size(34.dp)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Text(day.toString(), style = MaterialTheme.typography.labelSmall, color = fg, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    if (showAddDialog) {
        AddReminderDialog(
            onDismiss = { showAddDialog = false },
            onSave = {
                onAddReminder(it)
                showAddDialog = false
            }
        )
    }
}

@Composable
private fun AddReminderDialog(
    onDismiss: () -> Unit,
    onSave: (MedicineReminder) -> Unit
) {
    var medName by remember { mutableStateOf("") }
    var strength by remember { mutableStateOf("") }
    var dosageForm by remember { mutableStateOf("Tablet") }
    var prescribedDose by remember { mutableStateOf("1 tablet") }
    var scheduleType by remember { mutableStateOf("Once daily") }
    var timesText by remember { mutableStateOf("08:00") }
    var startDate by remember { mutableStateOf("2026-11-23") }
    var endDate by remember { mutableStateOf("2026-12-23") }
    var prescriber by remember { mutableStateOf("") }
    var instructions by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add Prescribed Reminder") },
        text = {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                item {
                    Text(
                        "Enter the exact schedule prescribed by your clinician.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                item { OutlinedTextField(medName, { medName = it }, label = { Text("Medicine Name *") }, singleLine = true, modifier = Modifier.fillMaxWidth()) }
                item {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(strength, { strength = it }, label = { Text("Strength") }, singleLine = true, modifier = Modifier.weight(1f))
                        OutlinedTextField(dosageForm, { dosageForm = it }, label = { Text("Form") }, singleLine = true, modifier = Modifier.weight(1f))
                    }
                }
                item { OutlinedTextField(prescribedDose, { prescribedDose = it }, label = { Text("Prescribed Dose *") }, singleLine = true, modifier = Modifier.fillMaxWidth()) }
                item {
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        items(listOf("Once daily", "Twice daily", "Three times daily", "Specific days", "Custom intervals", "Custom schedule")) { opt ->
                            FilterChip(
                                selected = scheduleType == opt,
                                onClick = {
                                    scheduleType = opt
                                    timesText = when (opt) {
                                        "Twice daily" -> "08:00, 20:00"
                                        "Three times daily" -> "08:00, 14:00, 20:00"
                                        else -> "08:00"
                                    }
                                },
                                label = { Text(opt, style = MaterialTheme.typography.labelSmall) }
                            )
                        }
                    }
                }
                item { OutlinedTextField(timesText, { timesText = it }, label = { Text("Reminder Times (comma separated)") }, singleLine = true, modifier = Modifier.fillMaxWidth()) }
                item {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(startDate, { startDate = it }, label = { Text("Start Date") }, singleLine = true, modifier = Modifier.weight(1f))
                        OutlinedTextField(endDate, { endDate = it }, label = { Text("End Date") }, singleLine = true, modifier = Modifier.weight(1f))
                    }
                }
                item { OutlinedTextField(prescriber, { prescriber = it }, label = { Text("Prescribing Professional") }, singleLine = true, modifier = Modifier.fillMaxWidth()) }
                item { OutlinedTextField(instructions, { instructions = it }, label = { Text("Additional Instructions (e.g., After food)") }, singleLine = true, modifier = Modifier.fillMaxWidth()) }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val times = timesText.split(",").map { it.trim() }.filter { it.isNotBlank() }.ifEmpty { listOf("08:00") }
                    onSave(
                        MedicineReminder(
                            medicineName = medName.ifBlank { "Prescribed Medicine" },
                            strength = strength,
                            dosageForm = dosageForm.ifBlank { "Tablet" },
                            prescribedDose = prescribedDose.ifBlank { "1 dose" },
                            scheduleType = scheduleType,
                            reminderTimes = times,
                            startDate = startDate,
                            endDate = endDate,
                            prescribingProfessional = prescriber,
                            instructions = instructions,
                            status = "UPCOMING"
                        )
                    )
                },
                enabled = medName.isNotBlank()
            ) { Text("Save Schedule") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } }
    )
}

// =========================================================================
// FEATURE 15 – MEDICINE EXPIRY TRACKER (DIGITAL CABINET)
// =========================================================================

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MedicineExpiryTrackerScreen(
    cabinet: List<CabinetMedicine>,
    onAddCabinetMedicine: (CabinetMedicine) -> Unit,
    onDeleteCabinetMedicine: (String) -> Unit,
    onBack: () -> Unit
) {
    val ext = LocalDRxColors.current
    var showAddDialog by remember { mutableStateOf(false) }
    var alertThresholdDays by remember { mutableIntStateOf(30) }

    val sdf = remember { SimpleDateFormat("yyyy-MM-dd", Locale.US) }
    val todayMillis = remember { System.currentTimeMillis() }

    fun daysUntilExpiry(dateStr: String): Long {
        return try {
            val d = sdf.parse(dateStr) ?: return 365L
            TimeUnit.MILLISECONDS.toDays(d.time - todayMillis)
        } catch (_: Exception) { 180L }
    }

    val expiredCount = cabinet.count { daysUntilExpiry(it.expiryDate) < 0 }
    val expiringSoonCount = cabinet.count { val d = daysUntilExpiry(it.expiryDate); d in 0..alertThresholdDays.toLong() }
    val activeCount = cabinet.size - expiredCount

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Medicine Expiry Tracker") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Rounded.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddDialog = true },
                containerColor = MaterialTheme.colorScheme.primary,
                modifier = Modifier.testTag("add_cabinet_fab")
            ) {
                Icon(Icons.Rounded.Add, contentDescription = "Add Medicine to Cabinet", tint = Color.White)
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding),
            contentPadding = PaddingValues(20.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // 4 KPI Summary Tiles
            item {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    ExpiryKpiTile("Total", cabinet.size.toString(), MedicalBlue, Modifier.weight(1f))
                    ExpiryKpiTile("Active", activeCount.toString(), SuccessGreen, Modifier.weight(1f))
                    ExpiryKpiTile("Soon", expiringSoonCount.toString(), WarningAmber, Modifier.weight(1f))
                    ExpiryKpiTile("Expired", expiredCount.toString(), DangerRed, Modifier.weight(1f))
                }
            }

            // Customizable Alert Threshold Selector
            item {
                GlassMedicalCard(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text("Alert Window Before Expiry", style = MaterialTheme.typography.titleSmall)
                        Spacer(Modifier.height(8.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            listOf(90, 30, 7, 1).forEach { days ->
                                FilterChip(
                                    selected = alertThresholdDays == days,
                                    onClick = { alertThresholdDays = days },
                                    label = { Text(if (days == 1) "Expiry Day" else "$days days") }
                                )
                            }
                        }
                    }
                }
            }

            if (cabinet.isEmpty()) {
                item {
                    GlassMedicalCard(modifier = Modifier.fillMaxWidth()) {
                        Column(
                            modifier = Modifier.fillMaxWidth().padding(22.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(Icons.Rounded.Inventory2, contentDescription = null, tint = WarningAmber, modifier = Modifier.size(40.dp))
                            Text("Your Digital Medicine Cabinet is Empty", style = MaterialTheme.typography.titleMedium)
                            Text("Add medicines with batch number, expiry date, and storage location to receive timely alerts.", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Button(onClick = { showAddDialog = true }) { Text("Add First Medicine") }
                        }
                    }
                }
            } else {
                items(cabinet, key = { it.id }) { item ->
                    val daysLeft = daysUntilExpiry(item.expiryDate)
                    val isExpired = daysLeft < 0
                    val isSoon = daysLeft in 0..item.alertDaysBefore
                    val (statusLabel, color, bg) = when {
                        isExpired -> Triple("EXPIRED — DO NOT USE", ext.danger, ext.dangerContainer)
                        isSoon -> Triple("EXPIRES IN $daysLeft DAYS", ext.warning, ext.warningContainer)
                        else -> Triple("SAFE • $daysLeft DAYS LEFT", ext.success, ext.successContainer)
                    }

                    GlassMedicalCard(
                        modifier = Modifier.fillMaxWidth(),
                        borderColor = color.copy(alpha = 0.5f)
                    ) {
                        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("${item.medicineName} ${item.strength}".trim(), style = MaterialTheme.typography.titleMedium)
                                StatusPillBadge(statusLabel, color, bg)
                            }
                            Text("Brand: ${item.brand.ifBlank { "Generic" }} • Qty: ${item.quantity} units", style = MaterialTheme.typography.bodySmall)
                            Text("Expiry: ${item.expiryDate} • Batch: ${item.batchNumber.ifBlank { "N/A" }}", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.primary)
                            Text("Storage: ${item.storageLocation}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            if (isExpired) {
                                Text(
                                    "Warning: Never consume expired medications. Safely dispose via a pharmacy take-back program.",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = DangerRed,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                                IconButton(onClick = { onDeleteCabinetMedicine(item.id) }) {
                                    Icon(Icons.Rounded.DeleteOutline, contentDescription = "Remove cabinet item", tint = DangerRed)
                                }
                            }
                        }
                    }
                }
            }

            item {
                MedicalSafetyBanner(
                    text = "Safe Disposal Reminder: Never consume expired medicines. Potency loss or chemical degradation can be dangerous — consult a pharmacist for safe medication disposal."
                )
            }
        }
    }

    if (showAddDialog) {
        AddCabinetDialog(
            defaultAlertDays = alertThresholdDays,
            onDismiss = { showAddDialog = false },
            onSave = {
                onAddCabinetMedicine(it)
                showAddDialog = false
            }
        )
    }
}

@Composable
private fun ExpiryKpiTile(label: String, value: String, color: Color, modifier: Modifier = Modifier) {
    GlassMedicalCard(modifier = modifier, borderColor = color.copy(alpha = 0.35f)) {
        Column(modifier = Modifier.padding(12.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Text(value, style = MaterialTheme.typography.headlineSmall, color = color, fontWeight = FontWeight.Bold)
            Text(label, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
private fun AddCabinetDialog(
    defaultAlertDays: Int,
    onDismiss: () -> Unit,
    onSave: (CabinetMedicine) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var brand by remember { mutableStateOf("") }
    var batch by remember { mutableStateOf("") }
    var expiry by remember { mutableStateOf("2027-06-30") }
    var qty by remember { mutableStateOf("10") }
    var strength by remember { mutableStateOf("500 mg") }
    var storage by remember { mutableStateOf("Cool dry cabinet") }
    var alertDays by remember { mutableIntStateOf(defaultAlertDays) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add to Medicine Cabinet") },
        text = {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                item { OutlinedTextField(name, { name = it }, label = { Text("Medicine Name *") }, singleLine = true, modifier = Modifier.fillMaxWidth()) }
                item { OutlinedTextField(brand, { brand = it }, label = { Text("Brand") }, singleLine = true, modifier = Modifier.fillMaxWidth()) }
                item {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(strength, { strength = it }, label = { Text("Strength") }, singleLine = true, modifier = Modifier.weight(1f))
                        OutlinedTextField(qty, { qty = it }, label = { Text("Qty") }, singleLine = true, modifier = Modifier.weight(1f))
                    }
                }
                item { OutlinedTextField(expiry, { expiry = it }, label = { Text("Expiry Date (YYYY-MM-DD) *") }, singleLine = true, modifier = Modifier.fillMaxWidth()) }
                item { OutlinedTextField(batch, { batch = it }, label = { Text("Batch Number") }, singleLine = true, modifier = Modifier.fillMaxWidth()) }
                item { OutlinedTextField(storage, { storage = it }, label = { Text("Storage Location") }, singleLine = true, modifier = Modifier.fillMaxWidth()) }
                item {
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        listOf(90, 30, 7).forEach { d ->
                            FilterChip(selected = alertDays == d, onClick = { alertDays = d }, label = { Text("${d}d alert") })
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onSave(
                        CabinetMedicine(
                            medicineName = name.trim(),
                            brand = brand.trim(),
                            batchNumber = batch.trim(),
                            expiryDate = expiry.trim().ifBlank { "2027-06-30" },
                            quantity = qty.toLongOrNull()?.coerceIn(0, 10000) ?: 10L,
                            strength = strength.trim(),
                            storageLocation = storage.trim().ifBlank { "Home Cabinet" },
                            alertDaysBefore = alertDays.toLong()
                        )
                    )
                },
                enabled = name.isNotBlank()
            ) { Text("Add Medicine") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } }
    )
}

// =========================================================================
// MY MEDICINES HUB (BOTTOM TAB 4)
// =========================================================================

@Composable
fun MyMedicinesHubScreen(
    reminders: List<MedicineReminder>,
    cabinet: List<CabinetMedicine>,
    savedMedicineIds: List<String>,
    reports: List<SavedReport>,
    onNavigate: (String) -> Unit,
    onOpenMedicineDetail: (String) -> Unit,
    onDeleteReport: (String) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Column {
                Text("My Medicines", style = MaterialTheme.typography.headlineMedium)
                Text(
                    "Your cloud-synced reminders, digital cabinet, saved monographs, and clinical reports.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        item {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                GlassMedicalCard(
                    modifier = Modifier.weight(1f).clickable { onNavigate(DRxRoute.REMINDERS) },
                    borderColor = PremiumPurple.copy(alpha = 0.4f)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Icon(Icons.Rounded.Alarm, contentDescription = null, tint = PremiumPurple)
                        Spacer(Modifier.height(8.dp))
                        Text("Reminders", style = MaterialTheme.typography.titleMedium)
                        Text("${reminders.size} scheduled", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
                GlassMedicalCard(
                    modifier = Modifier.weight(1f).clickable { onNavigate(DRxRoute.EXPIRY_TRACKER) },
                    borderColor = WarningAmber.copy(alpha = 0.4f)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Icon(Icons.Rounded.Inventory2, contentDescription = null, tint = WarningAmber)
                        Spacer(Modifier.height(8.dp))
                        Text("Expiry Cabinet", style = MaterialTheme.typography.titleMedium)
                        Text("${cabinet.size} tracked items", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }
        }

        // Saved Medicines
        item {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Rounded.Bookmark, contentDescription = null, tint = MedicalBlue)
                Spacer(Modifier.width(8.dp))
                Text("Saved Medicines (${savedMedicineIds.size})", style = MaterialTheme.typography.titleLarge)
            }
        }

        if (savedMedicineIds.isEmpty()) {
            item {
                GlassMedicalCard(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Bookmark medicines in the Library for quick access.", style = MaterialTheme.typography.bodySmall, modifier = Modifier.weight(1f))
                        TextButton(onClick = { onNavigate(DRxRoute.MEDICINE_LIBRARY) }) { Text("Browse") }
                    }
                }
            }
        } else {
            items(savedMedicineIds.mapNotNull { PharmaCatalog.findById(it) }) { med ->
                GlassMedicalCard(
                    modifier = Modifier.fillMaxWidth().clickable { onOpenMedicineDetail(med.id) }
                ) {
                    Row(modifier = Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(med.genericName, style = MaterialTheme.typography.titleSmall)
                            Text(med.brandNames.take(3).joinToString(" • "), style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        StatusPillBadge(med.category, MedicalTeal, MaterialTheme.colorScheme.secondaryContainer)
                    }
                }
            }
        }

        // My Reports
        item {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Rounded.Description, contentDescription = null, tint = MedicalTeal)
                Spacer(Modifier.width(8.dp))
                Text("My Saved Reports (${reports.size})", style = MaterialTheme.typography.titleLarge)
            }
        }

        if (reports.isEmpty()) {
            item {
                GlassMedicalCard(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        "Saved Drug Interaction checks, Pill Scanner comparisons, and LabGuard reports will appear here automatically.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }
        } else {
            items(reports, key = { it.id }) { rep ->
                GlassMedicalCard(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            StatusPillBadge(rep.reportType, MedicalBlue, MaterialTheme.colorScheme.primaryContainer)
                            IconButton(onClick = { onDeleteReport(rep.id) }, modifier = Modifier.size(32.dp)) {
                                Icon(Icons.Rounded.DeleteOutline, contentDescription = "Delete report", tint = DangerRed, modifier = Modifier.size(18.dp))
                            }
                        }
                        Text(rep.title, style = MaterialTheme.typography.titleSmall)
                        Text(rep.summary, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }
        }
    }
}
