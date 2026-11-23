package com.example.ui.screens

import android.content.Intent
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.material.icons.rounded.BookmarkAdd
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.ErrorOutline
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material.icons.rounded.Medication
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material.icons.rounded.Share
import androidx.compose.material.icons.rounded.SwapHoriz
import androidx.compose.material.icons.rounded.VerifiedUser
import androidx.compose.material.icons.rounded.WarningAmber
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.data.DrugInteractionRecord
import com.example.data.InteractionSeverity
import com.example.data.MedicineMonograph
import com.example.data.PharmaCatalog
import com.example.data.SideEffectDetail
import com.example.ui.components.GlassMedicalCard
import com.example.ui.components.MedicalSafetyBanner
import com.example.ui.components.StatusPillBadge
import com.example.ui.components.SuccessConfirmationBanner
import com.example.ui.theme.DangerRed
import com.example.ui.theme.LocalDRxColors
import com.example.ui.theme.MedicalBlue
import com.example.ui.theme.MedicalTeal
import com.example.ui.theme.SuccessGreen
import com.example.ui.theme.WarningAmber

// =========================================================================
// FEATURE 01 – DRUG INTERACTION CHECKER
// =========================================================================

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun DrugInteractionScreen(
    onBack: () -> Unit,
    onCheckLogged: () -> Unit,
    onSaveReport: (title: String, summary: String, severity: String) -> Unit
) {
    val context = LocalContext.current
    val ext = LocalDRxColors.current
    var searchQuery by remember { mutableStateOf("") }
    val selectedIds = remember { mutableStateListOf("med_warfarin", "med_aspirin") }
    var selectedStrengthMap by remember { mutableStateOf(mapOf<String, String>()) }
    var checkedResults by remember { mutableStateOf<List<DrugInteractionRecord>?>(null) }
    var savedBanner by remember { mutableStateOf(false) }

    val matchingMeds = remember(searchQuery, selectedIds.toList()) {
        PharmaCatalog.searchMedicines(searchQuery).filter { it.id !in selectedIds }.take(6)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Drug Interaction Checker") },
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
            item {
                Column {
                    Text("Check Drug Interactions", style = MaterialTheme.typography.headlineMedium)
                    Text(
                        "Understand potential interactions between multiple medicines using verified clinical monographs.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            // Medicine Search & Quick Add
            item {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text("Search brand, generic, or active ingredient...") },
                    leadingIcon = { Icon(Icons.Rounded.Search, contentDescription = null) },
                    singleLine = true,
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.fillMaxWidth().testTag("interaction_search_input")
                )
                Spacer(Modifier.height(10.dp))
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    matchingMeds.forEach { med ->
                        Surface(
                            shape = CircleShape,
                            color = MaterialTheme.colorScheme.primaryContainer,
                            modifier = Modifier.clickable {
                                selectedIds.add(med.id)
                                checkedResults = null
                                savedBanner = false
                            }
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 7.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Rounded.Add, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(16.dp))
                                Spacer(Modifier.width(4.dp))
                                Text(med.genericName.substringBefore(" ("), style = MaterialTheme.typography.labelMedium)
                            }
                        }
                    }
                }
            }

            // Selected Medicine Cards with Strength Selector
            item {
                Text(
                    "Selected Medicines (${selectedIds.size})",
                    style = MaterialTheme.typography.titleMedium
                )
                Spacer(Modifier.height(8.dp))
                if (selectedIds.isEmpty()) {
                    GlassMedicalCard(modifier = Modifier.fillMaxWidth()) {
                        Text(
                            "Add at least 2 medicines above to analyze potential interactions.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(16.dp)
                        )
                    }
                } else {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        selectedIds.forEach { id ->
                            val med = PharmaCatalog.findById(id) ?: return@forEach
                            val activeStrength = selectedStrengthMap[id] ?: med.strengths.firstOrNull().orEmpty()
                            GlassMedicalCard(modifier = Modifier.fillMaxWidth()) {
                                Column(modifier = Modifier.padding(14.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(Icons.Rounded.Medication, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                                        Spacer(Modifier.width(10.dp))
                                        Column(modifier = Modifier.weight(1f)) {
                                            Text(med.genericName, style = MaterialTheme.typography.titleSmall)
                                            Text(
                                                "${med.brandNames.take(2).joinToString(", ")} • ${med.dosageForms.firstOrNull().orEmpty()}",
                                                style = MaterialTheme.typography.bodySmall,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                        }
                                        IconButton(onClick = {
                                            selectedIds.remove(id)
                                            checkedResults = null
                                        }) {
                                            Icon(Icons.Rounded.Close, contentDescription = "Remove ${med.genericName}")
                                        }
                                    }
                                    Spacer(Modifier.height(6.dp))
                                    LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                        items(med.strengths) { str ->
                                            FilterChip(
                                                selected = activeStrength == str,
                                                onClick = { selectedStrengthMap = selectedStrengthMap + (id to str) },
                                                label = { Text(str, style = MaterialTheme.typography.labelSmall) }
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Primary Check Button
            item {
                Button(
                    onClick = {
                        checkedResults = PharmaCatalog.checkInteractions(selectedIds)
                        savedBanner = false
                        onCheckLogged()
                    },
                    enabled = selectedIds.size >= 2,
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(54.dp)
                        .testTag("check_interactions_button")
                ) {
                    Icon(Icons.Rounded.WarningAmber, contentDescription = null)
                    Spacer(Modifier.width(8.dp))
                    Text("Check Interactions", style = MaterialTheme.typography.labelLarge)
                }
            }

            // Results Section
            checkedResults?.let { results ->
                item {
                    if (results.isEmpty()) {
                        GlassMedicalCard(
                            modifier = Modifier.fillMaxWidth(),
                            borderColor = ext.success.copy(alpha = 0.45f)
                        ) {
                            Column(modifier = Modifier.padding(18.dp)) {
                                StatusPillBadge(
                                    label = InteractionSeverity.NONE.label,
                                    color = ext.success,
                                    backgroundColor = ext.successContainer
                                )
                                Spacer(Modifier.height(10.dp))
                                Text(
                                    "No Known Interaction Found in Reference Database",
                                    style = MaterialTheme.typography.titleMedium
                                )
                                Spacer(Modifier.height(6.dp))
                                Text(
                                    "No verified pair interaction was matched for this combination in DRxGuard's current monograph database. However, an absent database entry does NOT prove that no interaction exists — especially with herbal supplements, renal/liver impairment, or individual patient factors.",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    } else {
                        Text(
                            "${results.size} Interaction${if (results.size > 1) "s" else ""} Detected",
                            style = MaterialTheme.typography.titleLarge,
                            color = DangerRed
                        )
                    }
                }

                items(results) { rec ->
                    val medA = PharmaCatalog.findById(rec.drugAId)
                    val medB = PharmaCatalog.findById(rec.drugBId)
                    val (sevColor, sevBg) = when (rec.severity) {
                        InteractionSeverity.HIGH -> ext.danger to ext.dangerContainer
                        InteractionSeverity.MODERATE -> ext.warning to ext.warningContainer
                        InteractionSeverity.LOW -> androidx.compose.ui.graphics.Color(0xFFB78103) to androidx.compose.ui.graphics.Color(0xFFFFF8DC)
                        InteractionSeverity.NONE -> ext.success to ext.successContainer
                    }
                    GlassMedicalCard(
                        modifier = Modifier.fillMaxWidth(),
                        borderColor = sevColor.copy(alpha = 0.5f)
                    ) {
                        Column(modifier = Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                StatusPillBadge(rec.severity.label.uppercase(), sevColor, sevBg)
                                Text("Updated ${rec.lastUpdated}", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                            Text(
                                "${medA?.genericName ?: rec.drugAId} + ${medB?.genericName ?: rec.drugBId}",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                "Active Ingredients: ${medA?.activeIngredients?.joinToString().orEmpty()} & ${medB?.activeIngredients?.joinToString().orEmpty()}",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.primary
                            )
                            HorizontalDivider()
                            DetailLine("Interaction Overview", rec.description)
                            DetailLine("Possible Clinical Effects", rec.clinicalEffects)
                            DetailLine("Supporting Evidence", rec.supportingEvidence)
                            DetailLine("Recommended Precautions", rec.precautions)
                            Text(
                                "Source: ${rec.source}",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }

                // Actions: Save, Share, Check Another
                item {
                    val comboTitle = selectedIds.mapNotNull { PharmaCatalog.findById(it)?.genericName?.substringBefore(" (") }.joinToString(" + ")
                    val topSeverity = results.firstOrNull()?.severity?.name ?: "NONE"
                    val summaryText = if (results.isEmpty()) {
                        "No known interaction matched in current catalog for $comboTitle. Verify with pharmacist."
                    } else {
                        results.joinToString("\n") { "${it.severity.label}: ${it.description}" }
                    }

                    if (savedBanner) {
                        SuccessConfirmationBanner("Interaction report saved to your DRxGuard cloud reports.")
                        Spacer(Modifier.height(10.dp))
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Button(
                            onClick = {
                                onSaveReport(comboTitle, summaryText, topSeverity)
                                savedBanner = true
                            },
                            modifier = Modifier.weight(1f).testTag("save_interaction_report_button")
                        ) {
                            Icon(Icons.Rounded.BookmarkAdd, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(Modifier.width(6.dp))
                            Text("Save Report")
                        }
                        OutlinedButton(
                            onClick = {
                                val sendIntent = Intent(Intent.ACTION_SEND).apply {
                                    type = "text/plain"
                                    putExtra(Intent.EXTRA_SUBJECT, "DRxGuard Interaction Report: $comboTitle")
                                    putExtra(Intent.EXTRA_TEXT, "DRxGuard Interaction Analysis ($comboTitle)\n\n$summaryText\n\nEducational reference only. Consult a pharmacist or doctor.")
                                }
                                context.startActivity(Intent.createChooser(sendIntent, "Share DRxGuard Report"))
                            },
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(Icons.Rounded.Share, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(Modifier.width(6.dp))
                            Text("Share")
                        }
                    }
                    Spacer(Modifier.height(8.dp))
                    OutlinedButton(
                        onClick = {
                            selectedIds.clear()
                            checkedResults = null
                            savedBanner = false
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Rounded.Refresh, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(Modifier.width(6.dp))
                        Text("Check Another Combination")
                    }
                }
            }

            item {
                MedicalSafetyBanner(
                    text = "Medical Safety Notice: Never interpret an absent database result as proof that no interaction exists. Always verify drug combinations with a qualified physician or registered pharmacist before starting or stopping any therapy."
                )
            }
        }
    }
}

// =========================================================================
// FEATURE 02 – SIDE EFFECT CHECKER
// =========================================================================

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SideEffectCheckerScreen(onBack: () -> Unit) {
    var query by remember { mutableStateOf("") }
    var selectedMed by remember { mutableStateOf(PharmaCatalog.medicines.first()) }
    var selectedTab by remember { mutableIntStateOf(0) }
    val searchResults = remember(query) { PharmaCatalog.searchMedicines(query) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Side Effect Checker") },
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
                OutlinedTextField(
                    value = query,
                    onValueChange = { query = it },
                    placeholder = { Text("Search brand, generic, or active ingredient...") },
                    leadingIcon = { Icon(Icons.Rounded.Search, contentDescription = null) },
                    singleLine = true,
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.fillMaxWidth().testTag("side_effect_search_input")
                )
                Spacer(Modifier.height(8.dp))
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(searchResults.take(8)) { med ->
                        FilterChip(
                            selected = selectedMed.id == med.id,
                            onClick = { selectedMed = med },
                            label = { Text(med.genericName.substringBefore(" (")) }
                        )
                    }
                }
            }

            // Medicine Result Card
            item {
                GlassMedicalCard(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            StatusPillBadge(selectedMed.category, MedicalBlue, MaterialTheme.colorScheme.primaryContainer)
                            Text("Updated ${selectedMed.lastUpdated}", style = MaterialTheme.typography.labelSmall)
                        }
                        Text(selectedMed.genericName, style = MaterialTheme.typography.headlineSmall)
                        Text("Brands: ${selectedMed.brandNames.joinToString(" • ")}", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.primary)
                        Text("Active Ingredient: ${selectedMed.activeIngredients.joinToString()}", style = MaterialTheme.typography.bodySmall)
                        Text("Strengths: ${selectedMed.strengths.joinToString(", ")} | Forms: ${selectedMed.dosageForms.joinToString(", ")}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }

            // Emergency Warning Banner
            item {
                MedicalSafetyBanner(
                    text = "Urgent Care Warning: Seek immediate emergency care for swelling of lips/tongue/throat, breathing difficulty, severe blistering rash, chest pain, fainting, or unusual heavy bleeding.",
                    isUrgent = true
                )
            }

            // Category Tabs: Common, Serious, Rare
            item {
                TabRow(selectedTabIndex = selectedTab) {
                    Tab(selected = selectedTab == 0, onClick = { selectedTab = 0 }, text = { Text("Common (${selectedMed.commonSideEffects.size})") })
                    Tab(selected = selectedTab == 1, onClick = { selectedTab = 1 }, text = { Text("Serious (${selectedMed.seriousSideEffects.size})") })
                    Tab(selected = selectedTab == 2, onClick = { selectedTab = 2 }, text = { Text("Rare (${selectedMed.rareSideEffects.size})") })
                }
            }

            val listToShow: List<SideEffectDetail> = when (selectedTab) {
                0 -> selectedMed.commonSideEffects
                1 -> selectedMed.seriousSideEffects
                else -> selectedMed.rareSideEffects
            }

            items(listToShow) { effect ->
                val ext = LocalDRxColors.current
                val (sevColor, sevBg) = when (effect.severity.lowercase()) {
                    "high" -> ext.danger to ext.dangerContainer
                    "moderate" -> ext.warning to ext.warningContainer
                    else -> ext.tealAccent to MaterialTheme.colorScheme.secondaryContainer
                }
                GlassMedicalCard(
                    modifier = Modifier.fillMaxWidth(),
                    borderColor = sevColor.copy(alpha = 0.4f)
                ) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(effect.name, style = MaterialTheme.typography.titleMedium, modifier = Modifier.weight(1f))
                            StatusPillBadge(effect.severity.uppercase(), sevColor, sevBg)
                        }
                        Text(effect.description, style = MaterialTheme.typography.bodyMedium)
                        Text("Frequency: ${effect.frequency}", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.primary)
                        Text("Warning: ${effect.warning}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text("Source: ${effect.source}", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }

            item {
                MedicalSafetyBanner(
                    text = "Do not stop or change prescribed medication without consulting a qualified healthcare professional, except when seeking emergency care."
                )
            }
        }
    }
}

// =========================================================================
// FEATURE 03 – COMPARE DRUGS
// =========================================================================

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun CompareDrugsScreen(onBack: () -> Unit) {
    var medA by remember { mutableStateOf(PharmaCatalog.medicines[0]) } // Paracetamol
    var medB by remember { mutableStateOf(PharmaCatalog.medicines[1]) } // Ibuprofen
    var selectingSlot by remember { mutableStateOf<String?>(null) }
    val ext = LocalDRxColors.current

    val sameIngredient = medA.activeIngredients == medB.activeIngredients
    val sameCategory = medA.category == medB.category
    val sameStrength = medA.strengths == medB.strengths
    val sameForm = medA.dosageForms == medB.dosageForms

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Compare Drugs") },
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
                Text("Side-by-Side Clinical Comparison", style = MaterialTheme.typography.headlineSmall)
                Spacer(Modifier.height(10.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    CompareSelectorCard(
                        slotLabel = "MEDICINE A",
                        med = medA,
                        accent = MedicalBlue,
                        onClick = { selectingSlot = if (selectingSlot == "A") null else "A" },
                        modifier = Modifier.weight(1f).testTag("compare_select_a")
                    )
                    CompareSelectorCard(
                        slotLabel = "MEDICINE B",
                        med = medB,
                        accent = MedicalTeal,
                        onClick = { selectingSlot = if (selectingSlot == "B") null else "B" },
                        modifier = Modifier.weight(1f).testTag("compare_select_b")
                    )
                }
            }

            if (selectingSlot != null) {
                item {
                    GlassMedicalCard(modifier = Modifier.fillMaxWidth()) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Text("Choose Medicine $selectingSlot", style = MaterialTheme.typography.titleSmall)
                            Spacer(Modifier.height(8.dp))
                            FlowRow(
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                PharmaCatalog.medicines.forEach { candidate ->
                                    FilterChip(
                                        selected = (selectingSlot == "A" && medA.id == candidate.id) ||
                                            (selectingSlot == "B" && medB.id == candidate.id),
                                        onClick = {
                                            if (selectingSlot == "A") medA = candidate else medB = candidate
                                            selectingSlot = null
                                        },
                                        label = { Text(candidate.genericName.substringBefore(" (")) }
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Comparison Indicator Badges
            item {
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    StatusPillBadge(
                        if (sameIngredient) "Same Active Ingredient" else "Different Ingredient",
                        if (sameIngredient) ext.success else ext.warning,
                        if (sameIngredient) ext.successContainer else ext.warningContainer
                    )
                    StatusPillBadge(
                        if (sameCategory) "Similar Therapeutic Category" else "Different Therapeutic Category",
                        MedicalBlue,
                        MaterialTheme.colorScheme.primaryContainer
                    )
                    StatusPillBadge(
                        if (sameStrength) "Same Strengths" else "Different Strengths",
                        MedicalTeal,
                        MaterialTheme.colorScheme.secondaryContainer
                    )
                    StatusPillBadge(
                        if (sameForm) "Same Dosage Forms" else "Different Dosage Forms",
                        MaterialTheme.colorScheme.tertiary,
                        MaterialTheme.colorScheme.tertiaryContainer
                    )
                }
            }

            val rows = listOf(
                Triple("Brand Names", medA.brandNames.joinToString(", "), medB.brandNames.joinToString(", ")),
                Triple("Generic Name", medA.genericName, medB.genericName),
                Triple("Active Ingredients", medA.activeIngredients.joinToString(", "), medB.activeIngredients.joinToString(", ")),
                Triple("Drug Class", medA.drugClass, medB.drugClass),
                Triple("Therapeutic Uses", medA.therapeuticUses.joinToString("; "), medB.therapeuticUses.joinToString("; ")),
                Triple("Mechanism of Action", medA.simpleMoa, medB.simpleMoa),
                Triple("Available Strengths", medA.strengths.joinToString(", "), medB.strengths.joinToString(", ")),
                Triple("Dosage Forms", medA.dosageForms.joinToString(", "), medB.dosageForms.joinToString(", ")),
                Triple("Common Side Effects", medA.commonSideEffects.joinToString { it.name }, medB.commonSideEffects.joinToString { it.name }),
                Triple("Contraindications", medA.contraindications.joinToString("; "), medB.contraindications.joinToString("; ")),
                Triple("Precautions", medA.precautions.joinToString("; "), medB.precautions.joinToString("; ")),
                Triple("Important Warnings", medA.seriousSideEffects.joinToString { it.name }, medB.seriousSideEffects.joinToString { it.name })
            )

            items(rows) { (label, valA, valB) ->
                GlassMedicalCard(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text(label.uppercase(), style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.primary)
                        Spacer(Modifier.height(8.dp))
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(medA.genericName.substringBefore(" ("), style = MaterialTheme.typography.labelMedium, color = MedicalBlue)
                                Spacer(Modifier.height(2.dp))
                                Text(valA, style = MaterialTheme.typography.bodySmall)
                            }
                            Column(modifier = Modifier.weight(1f)) {
                                Text(medB.genericName.substringBefore(" ("), style = MaterialTheme.typography.labelMedium, color = MedicalTeal)
                                Spacer(Modifier.height(2.dp))
                                Text(valB, style = MaterialTheme.typography.bodySmall)
                            }
                        }
                    }
                }
            }

            item {
                MedicalSafetyBanner(
                    text = "Neutral Educational Comparison: DRxGuard does not label one medicine as superior. Choice of therapy depends on diagnosis, comorbidities, kidney/liver function, and prescriber judgment."
                )
            }
        }
    }
}

@Composable
private fun CompareSelectorCard(
    slotLabel: String,
    med: MedicineMonograph,
    accent: androidx.compose.ui.graphics.Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    GlassMedicalCard(
        modifier = modifier.clickable(onClick = onClick),
        borderColor = accent.copy(alpha = 0.45f)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Text(slotLabel, style = MaterialTheme.typography.labelSmall, color = accent, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(4.dp))
            Text(med.genericName.substringBefore(" ("), style = MaterialTheme.typography.titleMedium)
            Text(med.brandNames.firstOrNull().orEmpty(), style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(Modifier.height(6.dp))
            Text("Tap to switch", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.primary)
        }
    }
}

// =========================================================================
// FEATURE 04 – MEDICINE ALTERNATIVES
// =========================================================================

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MedicineAlternativesScreen(onBack: () -> Unit) {
    var search by remember { mutableStateOf("") }
    var selectedMed by remember { mutableStateOf(PharmaCatalog.medicines.first()) }
    var formFilter by remember { mutableStateOf("All") }
    val ext = LocalDRxColors.current

    val matchingMeds = remember(search) { PharmaCatalog.searchMedicines(search) }
    val allAlternatives = remember(selectedMed, formFilter) {
        val exact = PharmaCatalog.brandAlternatives.filter { it.genericId == selectedMed.id && it.exactMatch }
        val similar = PharmaCatalog.brandAlternatives.filter {
            (it.genericId == selectedMed.id && !it.exactMatch) ||
                (it.genericId != selectedMed.id && PharmaCatalog.findById(it.genericId)?.category == selectedMed.category)
        }
        val filterFn: (com.example.data.BrandAlternative) -> Boolean = {
            formFilter == "All" || it.dosageForm.contains(formFilter, ignoreCase = true)
        }
        exact.filter(filterFn) to similar.filter(filterFn)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Medicine Alternatives") },
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
                OutlinedTextField(
                    value = search,
                    onValueChange = { search = it },
                    placeholder = { Text("Search medicine brand or generic salt...") },
                    leadingIcon = { Icon(Icons.Rounded.Search, contentDescription = null) },
                    singleLine = true,
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(Modifier.height(8.dp))
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(matchingMeds.take(7)) { med ->
                        FilterChip(
                            selected = selectedMed.id == med.id,
                            onClick = { selectedMed = med },
                            label = { Text(med.brandNames.firstOrNull() ?: med.genericName) }
                        )
                    }
                }
            }

            // Reference Medicine Salt Analysis Card
            item {
                GlassMedicalCard(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text("SALT & COMPOSITION ANALYSIS", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.primary)
                        Text(selectedMed.brandNames.firstOrNull() ?: selectedMed.genericName, style = MaterialTheme.typography.titleLarge)
                        Text("Generic Ingredient: ${selectedMed.genericName}", style = MaterialTheme.typography.bodyMedium)
                        Text("Salt Composition: ${selectedMed.saltComposition}", style = MaterialTheme.typography.bodySmall)
                        Text("Strengths: ${selectedMed.strengths.joinToString(", ")} • Route: ${selectedMed.route}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }

            // Dosage form filters
            item {
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(listOf("All", "Tablet", "Capsule", "SR")) { f ->
                        FilterChip(
                            selected = formFilter == f,
                            onClick = { formFilter = f },
                            label = { Text(f) }
                        )
                    }
                }
            }

            // Section 1: Exact Ingredient & Strength Matches
            item {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Rounded.VerifiedUser, contentDescription = null, tint = ext.success, modifier = Modifier.size(20.dp))
                    Spacer(Modifier.width(6.dp))
                    Text("Exact Active Ingredient & Strength Matches", style = MaterialTheme.typography.titleMedium, color = ext.success)
                }
            }

            if (allAlternatives.first.isEmpty()) {
                item {
                    Text(
                        "No additional exact-match brand entries listed for this filter in the local reference catalog. Ask your registered pharmacist for verified generic equivalents.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                items(allAlternatives.first) { alt ->
                    AlternativeBrandCard(alt = alt, isExact = true)
                }
            }

            // Section 2: Similar Therapeutic Category (NOT exact substitutes)
            item {
                Spacer(Modifier.height(6.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Rounded.SwapHoriz, contentDescription = null, tint = ext.warning, modifier = Modifier.size(20.dp))
                    Spacer(Modifier.width(6.dp))
                    Text("Different Strength / Similar Therapeutic Category", style = MaterialTheme.typography.titleMedium, color = ext.warning)
                }
            }

            if (allAlternatives.second.isEmpty()) {
                item {
                    Text(
                        "No separate therapeutic-category comparison cards for this filter.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                items(allAlternatives.second) { alt ->
                    AlternativeBrandCard(alt = alt, isExact = false)
                }
            }

            item {
                MedicalSafetyBanner(
                    text = "Pharmacist Verification Required: Brand substitution — even with identical active ingredients — must be verified by a doctor or registered pharmacist, especially for narrow-therapeutic-index or extended-release medicines. DRxGuard does not authorize automatic replacement."
                )
            }
        }
    }
}

@Composable
private fun AlternativeBrandCard(alt: com.example.data.BrandAlternative, isExact: Boolean) {
    val ext = LocalDRxColors.current
    GlassMedicalCard(
        modifier = Modifier.fillMaxWidth(),
        borderColor = (if (isExact) ext.success else ext.warning).copy(alpha = 0.4f)
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(5.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(alt.brandName, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                StatusPillBadge(
                    if (isExact) "EXACT SALT MATCH" else "DIFFERENT SPEC / CLASS",
                    if (isExact) ext.success else ext.warning,
                    if (isExact) ext.successContainer else ext.warningContainer
                )
            }
            Text("Composition: ${alt.genericComposition}", style = MaterialTheme.typography.bodyMedium)
            Text("Strength: ${alt.strength} • Form: ${alt.dosageForm}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.primary)
            Text("Manufacturer: ${alt.manufacturer}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text("Notes: ${alt.availableInfo}", style = MaterialTheme.typography.bodySmall)
            Text("Key Differences: ${alt.relevantDifferences}", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Medium)
        }
    }
}

@Composable
private fun DetailLine(label: String, body: String) {
    Column {
        Text(label, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.primary)
        Text(body, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurface)
    }
}
