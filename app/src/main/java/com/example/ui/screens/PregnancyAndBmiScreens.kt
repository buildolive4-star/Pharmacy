package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.AutoAwesome
import androidx.compose.material.icons.rounded.CalendarMonth
import androidx.compose.material.icons.rounded.ChildFriendly
import androidx.compose.material.icons.rounded.Favorite
import androidx.compose.material.icons.rounded.HealthAndSafety
import androidx.compose.material.icons.rounded.Save
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.R
import com.example.data.BmiEntry
import com.example.data.PharmaCatalog
import com.example.ui.components.BmiGaugeCanvas
import com.example.ui.components.GlassMedicalCard
import com.example.ui.components.MedicalSafetyBanner
import com.example.ui.components.StatusPillBadge
import com.example.ui.components.TrendSparklineChart
import com.example.ui.theme.DangerRed
import com.example.ui.theme.ElectricCyan
import com.example.ui.theme.LocalDRxColors
import com.example.ui.theme.MedicalBlue
import com.example.ui.theme.PregnancyLavender
import com.example.ui.theme.PregnancyPink
import com.example.ui.theme.PregnancySky
import com.example.ui.theme.SuccessGreen
import com.example.ui.theme.WarningAmber
import com.example.viewmodel.AiCallState
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import kotlin.math.roundToInt

// =========================================================================
// FEATURE 05 – PREGNANCY SUPPORT
// =========================================================================

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun PregnancySupportScreen(
    symptomAiState: AiCallState<String>,
    onAnalyzeSymptoms: (symptoms: String, week: Int) -> Unit,
    onBack: () -> Unit
) {
    val ext = LocalDRxColors.current
    var selectedTab by remember { mutableIntStateOf(0) }

    // Due Date & Fertility inputs
    var daysAgoLmp by remember { mutableFloatStateOf(112f) } // Default ~16 weeks pregnant
    var cycleLengthDays by remember { mutableFloatStateOf(28f) }
    var cycleRegularity by remember { mutableStateOf("Regular") }

    // Growth tracker week selector
    var growthWeekIndex by remember { mutableIntStateOf(3) } // Week 16 default

    // Symptom Analysis input
    var symptomsText by remember { mutableStateOf("") }

    val dateFormat = remember { SimpleDateFormat("dd MMM yyyy", Locale.getDefault()) }

    val lmpCal = remember(daysAgoLmp) {
        Calendar.getInstance().apply { add(Calendar.DAY_OF_YEAR, -daysAgoLmp.roundToInt()) }
    }
    val dueDateCal = remember(daysAgoLmp, cycleLengthDays) {
        (lmpCal.clone() as Calendar).apply {
            add(Calendar.DAY_OF_YEAR, 280 + (cycleLengthDays.roundToInt() - 28))
        }
    }
    val totalDaysPregnant = daysAgoLmp.roundToInt().coerceIn(0, 294)
    val pregWeeks = (totalDaysPregnant / 7).coerceIn(1, 42)
    val pregExtraDays = totalDaysPregnant % 7
    val remainingDays = (280 + (cycleLengthDays.roundToInt() - 28) - totalDaysPregnant).coerceAtLeast(0)

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Pregnancy Support") },
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
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Soft Lavender / Sky / Pink Hero Card
            item {
                Card(
                    shape = RoundedCornerShape(26.dp),
                    colors = CardDefaults.cardColors(containerColor = ext.pregnancySurface),
                    border = BorderStroke(1.dp, PregnancyLavender.copy(alpha = 0.35f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(18.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            StatusPillBadge("MATERNAL WELLNESS", PregnancyPink, PregnancyPink.copy(alpha = 0.14f))
                            Spacer(Modifier.height(8.dp))
                            Text("Your Pregnancy Companion", style = MaterialTheme.typography.headlineSmall)
                            Spacer(Modifier.height(4.dp))
                            Text(
                                "Educational due date countdown, fertility estimates, week-by-week fetal milestones, and cautious symptom awareness.",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Spacer(Modifier.width(12.dp))
                        Image(
                            painter = painterResource(id = R.drawable.img_pregnancy_care),
                            contentDescription = "3D Pregnancy Illustration",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.size(92.dp).clip(RoundedCornerShape(20.dp))
                        )
                    }
                }
            }

            // 4 Module Tabs
            item {
                TabRow(selectedTabIndex = selectedTab) {
                    Tab(selected = selectedTab == 0, onClick = { selectedTab = 0 }, text = { Text("Due Date", maxLines = 1) })
                    Tab(selected = selectedTab == 1, onClick = { selectedTab = 1 }, text = { Text("Fertility", maxLines = 1) })
                    Tab(selected = selectedTab == 2, onClick = { selectedTab = 2 }, text = { Text("Growth", maxLines = 1) })
                    Tab(selected = selectedTab == 3, onClick = { selectedTab = 3 }, text = { Text("Symptoms", maxLines = 1) })
                }
            }

            when (selectedTab) {
                // MODULE A: DUE DATE COUNTDOWN
                0 -> {
                    item {
                        GlassMedicalCard(modifier = Modifier.fillMaxWidth(), borderColor = PregnancyLavender.copy(alpha = 0.4f)) {
                            Column(modifier = Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Rounded.CalendarMonth, contentDescription = null, tint = PregnancyLavender)
                                    Spacer(Modifier.width(8.dp))
                                    Text("Due Date Countdown", style = MaterialTheme.typography.titleLarge)
                                }
                                Text("Last Menstrual Period (LMP): ${dateFormat.format(lmpCal.time)} (${daysAgoLmp.roundToInt()} days ago)", style = MaterialTheme.typography.bodyMedium)
                                Slider(
                                    value = daysAgoLmp,
                                    onValueChange = { daysAgoLmp = it },
                                    valueRange = 28f..275f,
                                    colors = SliderDefaults.colors(thumbColor = PregnancyLavender, activeTrackColor = PregnancyLavender),
                                    modifier = Modifier.testTag("pregnancy_lmp_slider")
                                )
                                Text("Average Cycle Length: ${cycleLengthDays.roundToInt()} days", style = MaterialTheme.typography.bodyMedium)
                                Slider(
                                    value = cycleLengthDays,
                                    onValueChange = { cycleLengthDays = it },
                                    valueRange = 21f..35f,
                                    colors = SliderDefaults.colors(thumbColor = PregnancyPink, activeTrackColor = PregnancyPink)
                                )
                            }
                        }
                    }

                    item {
                        GlassMedicalCard(modifier = Modifier.fillMaxWidth(), containerColor = ext.pregnancySurface) {
                            Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                                Text("ESTIMATED DUE DATE", style = MaterialTheme.typography.labelSmall, color = PregnancyLavender)
                                Text(
                                    dateFormat.format(dueDateCal.time),
                                    style = MaterialTheme.typography.headlineMedium,
                                    color = PregnancyPink,
                                    fontWeight = FontWeight.Bold
                                )
                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                                    PregnancyStatTile("Current Stage", "Week $pregWeeks, Day $pregExtraDays", PregnancyLavender, Modifier.weight(1f))
                                    PregnancyStatTile("Remaining", "$remainingDays Days", PregnancySky, Modifier.weight(1f))
                                }
                                val progress = (totalDaysPregnant / 280f).coerceIn(0f, 1f)
                                Text("Pregnancy Timeline (${(progress * 100).roundToInt()}% Complete)", style = MaterialTheme.typography.labelMedium)
                                LinearProgressIndicator(
                                    progress = { progress },
                                    color = PregnancyPink,
                                    trackColor = PregnancyLavender.copy(alpha = 0.2f),
                                    modifier = Modifier.fillMaxWidth().height(10.dp).clip(RoundedCornerShape(5.dp))
                                )
                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                    Text("T1 (W1-12)", style = MaterialTheme.typography.labelSmall)
                                    Text("T2 (W13-27)", style = MaterialTheme.typography.labelSmall)
                                    Text("T3 (W28-40)", style = MaterialTheme.typography.labelSmall)
                                }
                            }
                        }
                    }
                }

                // MODULE B: FERTILITY FORECASTER
                1 -> {
                    item {
                        val recentLmp = Calendar.getInstance().apply { add(Calendar.DAY_OF_YEAR, -7) }
                        val ovulationOffset = (cycleLengthDays.roundToInt() - 14).coerceIn(7, 25)
                        val ovulationCal = (recentLmp.clone() as Calendar).apply { add(Calendar.DAY_OF_YEAR, ovulationOffset) }
                        val fertileStart = (ovulationCal.clone() as Calendar).apply { add(Calendar.DAY_OF_YEAR, -4) }
                        val fertileEnd = (ovulationCal.clone() as Calendar).apply { add(Calendar.DAY_OF_YEAR, 1) }
                        val nextPeriod = (recentLmp.clone() as Calendar).apply { add(Calendar.DAY_OF_YEAR, cycleLengthDays.roundToInt()) }

                        GlassMedicalCard(modifier = Modifier.fillMaxWidth()) {
                            Column(modifier = Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Rounded.Favorite, contentDescription = null, tint = PregnancyPink)
                                    Spacer(Modifier.width(8.dp))
                                    Text("Fertility Forecaster", style = MaterialTheme.typography.titleLarge)
                                }
                                Text("Average Cycle Length: ${cycleLengthDays.roundToInt()} days", style = MaterialTheme.typography.bodyMedium)
                                Slider(
                                    value = cycleLengthDays,
                                    onValueChange = { cycleLengthDays = it },
                                    valueRange = 21f..35f
                                )
                                Text("Cycle Regularity", style = MaterialTheme.typography.labelMedium)
                                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    listOf("Regular", "Slightly Variable", "Irregular").forEach { reg ->
                                        FilterChip(
                                            selected = cycleRegularity == reg,
                                            onClick = { cycleRegularity = reg },
                                            label = { Text(reg) }
                                        )
                                    }
                                }
                                PregnancyStatTile("Estimated Ovulation Day", dateFormat.format(ovulationCal.time), PregnancyPink, Modifier.fillMaxWidth())
                                PregnancyStatTile("Estimated Fertile Window", "${dateFormat.format(fertileStart.time)} – ${dateFormat.format(fertileEnd.time)}", PregnancyLavender, Modifier.fillMaxWidth())
                                PregnancyStatTile("Next Expected Period", dateFormat.format(nextPeriod.time), PregnancySky, Modifier.fillMaxWidth())
                            }
                        }
                    }
                    item {
                        MedicalSafetyBanner(
                            text = "Important Estimate Notice: Calendar fertility predictions are statistical estimates only (especially if cycles are irregular) and CANNOT reliably prevent or guarantee pregnancy."
                        )
                    }
                }

                // MODULE C: GROWTH TRACKER
                2 -> {
                    val milestones = PharmaCatalog.pregnancyMilestones
                    val currentMilestone = milestones[growthWeekIndex.coerceIn(0, milestones.lastIndex)]
                    item {
                        GlassMedicalCard(modifier = Modifier.fillMaxWidth()) {
                            Column(modifier = Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Rounded.ChildFriendly, contentDescription = null, tint = PregnancyLavender)
                                    Spacer(Modifier.width(8.dp))
                                    Text("Week-by-Week Growth Tracker", style = MaterialTheme.typography.titleLarge)
                                }
                                FlowRow(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                    milestones.forEachIndexed { idx, m ->
                                        FilterChip(
                                            selected = growthWeekIndex == idx,
                                            onClick = { growthWeekIndex = idx },
                                            label = { Text("W${m.week}") }
                                        )
                                    }
                                }
                            }
                        }
                    }
                    item {
                        GlassMedicalCard(modifier = Modifier.fillMaxWidth(), containerColor = ext.pregnancySurface) {
                            Column(modifier = Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text("Week ${currentMilestone.week}", style = MaterialTheme.typography.headlineMedium, color = PregnancyLavender)
                                    StatusPillBadge("Trimester ${currentMilestone.trimester} • ~${currentMilestone.sizeComparison} (${currentMilestone.lengthCm})", PregnancyPink, PregnancyPink.copy(alpha = 0.14f))
                                }
                                Text(currentMilestone.developmentSummary, style = MaterialTheme.typography.bodyMedium)
                                Text("Approximate Milestones", style = MaterialTheme.typography.titleSmall, color = MaterialTheme.colorScheme.primary)
                                currentMilestone.milestones.forEach { bullet ->
                                    Text("• $bullet", style = MaterialTheme.typography.bodySmall)
                                }
                                Text("Maternal Wellness", style = MaterialTheme.typography.titleSmall, color = PregnancyPink)
                                Text(currentMilestone.maternalWellness, style = MaterialTheme.typography.bodySmall)
                                Text("Appointment Reminder", style = MaterialTheme.typography.titleSmall, color = PregnancyLavender)
                                Text(currentMilestone.appointmentReminder, style = MaterialTheme.typography.bodySmall)
                            }
                        }
                    }
                }

                // MODULE D: SYMPTOM ANALYSIS
                3 -> {
                    item {
                        GlassMedicalCard(modifier = Modifier.fillMaxWidth()) {
                            Column(modifier = Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Rounded.HealthAndSafety, contentDescription = null, tint = PregnancyPink)
                                    Spacer(Modifier.width(8.dp))
                                    Text("Educational Symptom Analysis", style = MaterialTheme.typography.titleLarge)
                                }
                                FlowRow(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                    listOf("Mild morning nausea", "Lower back ache", "Ankle swelling", "Heartburn after meals", "Fatigue").forEach { chip ->
                                        FilterChip(
                                            selected = symptomsText.contains(chip),
                                            onClick = { symptomsText = if (symptomsText.isBlank()) chip else "$symptomsText, $chip" },
                                            label = { Text(chip, style = MaterialTheme.typography.labelSmall) }
                                        )
                                    }
                                }
                                OutlinedTextField(
                                    value = symptomsText,
                                    onValueChange = { symptomsText = it },
                                    label = { Text("Describe symptoms (non-diagnostic education)") },
                                    minLines = 3,
                                    modifier = Modifier.fillMaxWidth()
                                )
                                Button(
                                    onClick = { onAnalyzeSymptoms(symptomsText, pregWeeks) },
                                    enabled = symptomsText.isNotBlank() && symptomAiState !is AiCallState.Loading,
                                    colors = ButtonDefaults.buttonColors(containerColor = PregnancyLavender),
                                    modifier = Modifier.fillMaxWidth().testTag("pregnancy_symptom_analyze_button")
                                ) {
                                    if (symptomAiState is AiCallState.Loading) {
                                        CircularProgressIndicator(Modifier.size(18.dp), color = MaterialTheme.colorScheme.onPrimary, strokeWidth = 2.dp)
                                        Spacer(Modifier.width(8.dp))
                                        Text("Generating Educational Guidance...")
                                    } else {
                                        Icon(Icons.Rounded.AutoAwesome, contentDescription = null)
                                        Spacer(Modifier.width(8.dp))
                                        Text("Get Educational Information")
                                    }
                                }
                            }
                        }
                    }

                    when (symptomAiState) {
                        is AiCallState.Success -> item {
                            GlassMedicalCard(modifier = Modifier.fillMaxWidth()) {
                                Text(symptomAiState.data, style = MaterialTheme.typography.bodyMedium, modifier = Modifier.padding(16.dp))
                            }
                        }
                        is AiCallState.Error -> item {
                            MedicalSafetyBanner(symptomAiState.message, isUrgent = true)
                        }
                        else -> Unit
                    }

                    item {
                        MedicalSafetyBanner(
                            text = "Urgent Obstetric Warning Signs: Seek immediate medical evaluation for vaginal bleeding, severe headache or vision changes, sudden facial/hand swelling, severe abdominal pain, fluid leakage, fever, or decreased fetal movement. DRxGuard never diagnoses pregnancy complications.",
                            isUrgent = true
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun PregnancyStatTile(
    label: String,
    value: String,
    color: androidx.compose.ui.graphics.Color,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surface,
        border = BorderStroke(1.dp, color.copy(alpha = 0.3f))
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(label, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(Modifier.height(2.dp))
            Text(value, style = MaterialTheme.typography.titleSmall, color = color, fontWeight = FontWeight.Bold)
        }
    }
}

// =========================================================================
// FEATURE 06 – BMI CALCULATOR
// =========================================================================

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BmiCalculatorScreen(
    bmiHistory: List<BmiEntry>,
    onSaveBmi: (bmi: Double, category: String, heightCm: Double, weightKg: Double, date: String) -> Unit,
    onBack: () -> Unit
) {
    var heightUnit by remember { mutableStateOf("cm") } // cm, m, ft/in
    var weightUnit by remember { mutableStateOf("kg") } // kg, lbs

    var heightInput by remember { mutableStateOf("170") }
    var feetInput by remember { mutableStateOf("5") }
    var inchesInput by remember { mutableStateOf("7") }
    var weightInput by remember { mutableStateOf("66") }

    val heightMeters = remember(heightUnit, heightInput, feetInput, inchesInput) {
        when (heightUnit) {
            "cm" -> (heightInput.toDoubleOrNull() ?: 170.0) / 100.0
            "m" -> heightInput.toDoubleOrNull() ?: 1.70
            else -> {
                val ft = feetInput.toDoubleOrNull() ?: 5.0
                val inch = inchesInput.toDoubleOrNull() ?: 7.0
                ((ft * 12.0) + inch) * 0.0254
            }
        }.coerceIn(0.4, 2.6)
    }

    val weightKg = remember(weightUnit, weightInput) {
        val raw = weightInput.toDoubleOrNull() ?: 66.0
        (if (weightUnit == "kg") raw else raw * 0.45359237).coerceIn(5.0, 400.0)
    }

    val bmiValue = weightKg / (heightMeters * heightMeters)
    val (category, categoryColor, interpretation) = when {
        bmiValue < 18.5 -> Triple("Underweight", ElectricCyan, "Below standard adult range (<18.5). Consider discussing nutritional balance with a clinician.")
        bmiValue < 25.0 -> Triple("Normal Weight", SuccessGreen, "Within the standard WHO adult reference range (18.5 – 24.9).")
        bmiValue < 30.0 -> Triple("Overweight", WarningAmber, "Above standard adult range (25.0 – 29.9). Waist circumference and metabolic markers provide deeper context.")
        else -> Triple("Obesity Range", DangerRed, "At or above 30.0. Consult a healthcare professional for personalized cardiometabolic assessment.")
    }

    val todayStr = remember { SimpleDateFormat("dd MMM", Locale.getDefault()).format(Date()) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("BMI Calculator") },
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
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Animated Result Gauge Card
            item {
                GlassMedicalCard(modifier = Modifier.fillMaxWidth()) {
                    Column(
                        modifier = Modifier.fillMaxWidth().padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Box(
                            modifier = Modifier.width(220.dp).height(130.dp),
                            contentAlignment = Alignment.BottomCenter
                        ) {
                            BmiGaugeCanvas(bmi = bmiValue, modifier = Modifier.fillMaxSize())
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = String.format("%.1f", bmiValue),
                                    style = MaterialTheme.typography.displayMedium,
                                    color = categoryColor,
                                    fontWeight = FontWeight.Bold
                                )
                                Text("kg/m²", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }
                        Spacer(Modifier.height(10.dp))
                        StatusPillBadge(category.uppercase(), categoryColor, categoryColor.copy(alpha = 0.14f))
                        Spacer(Modifier.height(8.dp))
                        Text(interpretation, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Spacer(Modifier.height(12.dp))
                        Button(
                            onClick = { onSaveBmi(bmiValue, category, heightMeters * 100.0, weightKg, todayStr) },
                            shape = RoundedCornerShape(14.dp),
                            modifier = Modifier.testTag("save_bmi_button")
                        ) {
                            Icon(Icons.Rounded.Save, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(Modifier.width(6.dp))
                            Text("Log to BMI History")
                        }
                    }
                }
            }

            // Unit & Measurement Inputs
            item {
                GlassMedicalCard(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Text("Height Unit", style = MaterialTheme.typography.titleSmall)
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            listOf("cm", "m", "ft/in").forEach { unit ->
                                FilterChip(
                                    selected = heightUnit == unit,
                                    onClick = {
                                        heightUnit = unit
                                        if (unit == "cm") heightInput = "170"
                                        if (unit == "m") heightInput = "1.70"
                                    },
                                    label = { Text(unit) }
                                )
                            }
                        }
                        if (heightUnit == "ft/in") {
                            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                                OutlinedTextField(
                                    value = feetInput,
                                    onValueChange = { feetInput = it },
                                    label = { Text("Feet") },
                                    singleLine = true,
                                    modifier = Modifier.weight(1f)
                                )
                                OutlinedTextField(
                                    value = inchesInput,
                                    onValueChange = { inchesInput = it },
                                    label = { Text("Inches") },
                                    singleLine = true,
                                    modifier = Modifier.weight(1f)
                                )
                            }
                        } else {
                            OutlinedTextField(
                                value = heightInput,
                                onValueChange = { heightInput = it },
                                label = { Text("Height ($heightUnit)") },
                                singleLine = true,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }

                        Text("Weight Unit", style = MaterialTheme.typography.titleSmall)
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            listOf("kg", "lbs").forEach { unit ->
                                FilterChip(
                                    selected = weightUnit == unit,
                                    onClick = {
                                        weightUnit = unit
                                        weightInput = if (unit == "kg") "66" else "145"
                                    },
                                    label = { Text(unit) }
                                )
                            }
                        }
                        OutlinedTextField(
                            value = weightInput,
                            onValueChange = { weightInput = it },
                            label = { Text("Weight ($weightUnit)") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }

            // BMI History Chart
            item {
                val chartPoints = if (bmiHistory.isNotEmpty()) {
                    bmiHistory.takeLast(6).map { it.recordedDate to it.bmiValue }
                } else {
                    listOf("Sample 1" to 23.4, "Sample 2" to 23.1, "Now" to bmiValue)
                }
                GlassMedicalCard(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Text(
                            if (bmiHistory.isNotEmpty()) "Your Cloud BMI History (${bmiHistory.size})" else "BMI Trend Preview (Tap 'Log' to Save)",
                            style = MaterialTheme.typography.titleMedium
                        )
                        Spacer(Modifier.height(10.dp))
                        TrendSparklineChart(points = chartPoints, lineColor = MedicalBlue)
                    }
                }
            }

            // BMI Limitations Notice
            item {
                MedicalSafetyBanner(
                    text = "Limitations of BMI: BMI is a general screening ratio (Weight kg / Height m²) and does not directly measure body fat or muscle mass. Standard adult BMI categories do NOT apply to children/teens (age-sex percentiles needed), pregnancy, or highly muscular athletes."
                )
            }
        }
    }
}
