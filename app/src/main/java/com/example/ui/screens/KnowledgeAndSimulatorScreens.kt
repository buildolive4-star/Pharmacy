package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.material.icons.automirrored.rounded.ArrowForward
import androidx.compose.material.icons.rounded.AccountTree
import androidx.compose.material.icons.rounded.Bookmark
import androidx.compose.material.icons.rounded.BookmarkBorder
import androidx.compose.material.icons.rounded.Compare
import androidx.compose.material.icons.rounded.ExpandLess
import androidx.compose.material.icons.rounded.ExpandMore
import androidx.compose.material.icons.rounded.HealthAndSafety
import androidx.compose.material.icons.rounded.LocalHospital
import androidx.compose.material.icons.rounded.Medication
import androidx.compose.material.icons.rounded.Science
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material.icons.rounded.WarningAmber
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.data.AdminMedicine
import com.example.data.MedicineMonograph
import com.example.data.PharmaCatalog
import com.example.ui.components.AiErrorDiagnosticCard
import com.example.ui.components.GlassMedicalCard
import com.example.ui.components.MedicalSafetyBanner
import com.example.ui.components.MolecularMechanismCanvas
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

// =========================================================================
// FEATURE 09: MEDICINE INFORMATION LIBRARY & 16-SECTION MONOGRAPH DETAIL
// =========================================================================

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MedicineLibraryScreen(
    savedMedicineIds: List<String>,
    adminMedicines: List<AdminMedicine>,
    liveMedicines: List<MedicineMonograph> = emptyList(),
    openFdaLoading: Boolean = false,
    lookupState: AiCallState<MedicineMonograph> = AiCallState.Idle,
    onSearchOpenFda: (String) -> Unit = {},
    onLookupMedicineAi: (String) -> Unit = {},
    onToggleSave: (String) -> Unit,
    onOpenDetail: (String) -> Unit,
    onBack: () -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("All") }
    var bookmarksOnly by remember { mutableStateOf(false) }

    val mergedAdminMonographs = remember(adminMedicines) {
        adminMedicines.map { am ->
            MedicineMonograph(
                id = "admin_${am.id}",
                genericName = am.genericName,
                brandNames = am.brandNames.ifEmpty { listOf("Published Generic") },
                activeIngredients = listOf(am.genericName),
                saltComposition = am.composition.ifBlank { am.genericName },
                category = am.drugClass.ifBlank { "General" },
                drugClass = am.drugClass.ifBlank { "Published Monograph" },
                subclass = "DRxGuard Cloud Catalog",
                therapeuticUses = am.therapeuticUses.split(",").map { it.trim() }.filter { it.isNotEmpty() }
                    .ifEmpty { listOf("Educational pharmaceutical entry") },
                strengths = am.strengths.ifEmpty { listOf("See product label") },
                dosageForms = am.dosageForms.ifEmpty { listOf("Tablet") },
                route = "Oral / Label-Directed",
                simpleMoa = am.mechanismOfAction.ifBlank { "Consult official prescribing label for pharmacological mechanism." },
                advancedMoa = am.mechanismOfAction.ifBlank { "Published via DRxGuard Admin Panel." },
                molecularTarget = "Pharmacological target per label",
                receptorInteraction = "See monograph details",
                enzymeInvolvement = "Label-specified metabolism",
                biologicalPathway = "Systemic therapeutic pathway",
                physiologicalEffects = am.therapeuticUses.ifBlank { "See therapeutic applications" },
                commonSideEffects = emptyList(),
                seriousSideEffects = emptyList(),
                rareSideEffects = emptyList(),
                contraindications = listOf("Known hypersensitivity to active ingredient or excipients."),
                precautions = listOf(am.precautions.ifBlank { "Consult a licensed healthcare professional before use." }),
                keyInteractions = listOf("Check Drug Interaction Checker for multi-drug regimens."),
                storageInfo = "Store below 25°C in a dry place away from light and children.",
                manufacturerInfo = "Verified Cloud Entry (Admin Updated)",
                referenceSources = "DRxGuard Admin Curated Database",
                lastUpdated = "Cloud Synced",
                halfLifeHours = "Per label",
                bioavailability = "Per label",
                metabolism = "Per label"
            )
        }
    }

    val allMeds = remember(mergedAdminMonographs, liveMedicines) {
        (liveMedicines + PharmaCatalog.medicines + mergedAdminMonographs).distinctBy { it.id }
    }

    val filtered = remember(allMeds, searchQuery, selectedCategory, bookmarksOnly, savedMedicineIds) {
        val q = searchQuery.trim().lowercase()
        allMeds.filter { med ->
            val matchesCat = selectedCategory == "All" ||
                med.category.equals(selectedCategory, ignoreCase = true) ||
                med.drugClass.contains(selectedCategory, ignoreCase = true) ||
                med.id.startsWith("fda_") ||
                med.id.startsWith("ai_")
            val matchesQuery = q.isEmpty() ||
                med.genericName.lowercase().contains(q) ||
                med.brandNames.any { it.lowercase().contains(q) } ||
                med.drugClass.lowercase().contains(q) ||
                med.saltComposition.lowercase().contains(q)
            val matchesBookmark = !bookmarksOnly || med.id in savedMedicineIds
            matchesCat && matchesQuery && matchesBookmark
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Medicine Information Library") },
                navigationIcon = {
                    IconButton(onClick = onBack, modifier = Modifier.testTag("library_back_button")) {
                        Icon(Icons.AutoMirrored.Rounded.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(
                        onClick = { bookmarksOnly = !bookmarksOnly },
                        modifier = Modifier.testTag("library_bookmarks_filter")
                    ) {
                        Icon(
                            imageVector = if (bookmarksOnly) Icons.Rounded.Bookmark else Icons.Rounded.BookmarkBorder,
                            contentDescription = "Filter bookmarked medicines",
                            tint = if (bookmarksOnly) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                        )
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
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            item {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("library_search_input"),
                    placeholder = { Text("Search generic, brand, salt, or class...") },
                    leadingIcon = { Icon(Icons.Rounded.Search, contentDescription = null) },
                    singleLine = true,
                    shape = RoundedCornerShape(16.dp)
                )
            }

            item {
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(PharmaCatalog.medicineCategories) { cat ->
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
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "${filtered.size} Verified Monographs",
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.primary
                    )
                    if (bookmarksOnly) {
                        StatusPillBadge(
                            label = "BOOKMARKS ONLY",
                            color = PremiumPurple,
                            backgroundColor = PremiumPurple.copy(alpha = 0.14f)
                        )
                    }
                }
            }

            if (searchQuery.trim().length >= 2) {
                item {
                    GlassMedicalCard(
                        modifier = Modifier.fillMaxWidth(),
                        borderColor = MedicalTeal.copy(alpha = 0.45f)
                    ) {
                        Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Text(
                                "Global & Indian Medicine Live Search",
                                style = MaterialTheme.typography.labelLarge,
                                color = MedicalTeal,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                "Can't find \"${searchQuery.trim()}\" below? Fetch official FDA label data or generate a complete Indian/global monograph instantly.",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                OutlinedButton(
                                    onClick = { onSearchOpenFda(searchQuery.trim()) },
                                    enabled = !openFdaLoading,
                                    shape = RoundedCornerShape(12.dp),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Text(if (openFdaLoading) "Searching FDA..." else "OpenFDA DB")
                                }
                                Button(
                                    onClick = { onLookupMedicineAi(searchQuery.trim()) },
                                    enabled = lookupState !is AiCallState.Loading,
                                    shape = RoundedCornerShape(12.dp),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Text(if (lookupState is AiCallState.Loading) "Fetching..." else "AI Lookup")
                                }
                            }
                            if (lookupState is AiCallState.Error) {
                                AiErrorDiagnosticCard(
                                    errorMessage = lookupState.message,
                                    onRetry = { onLookupMedicineAi(searchQuery.trim()) }
                                )
                            }
                        }
                    }
                }
            }

            if (filtered.isEmpty()) {
                item {
                    GlassMedicalCard(modifier = Modifier.fillMaxWidth()) {
                        Column(
                            modifier = Modifier.padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(Icons.Rounded.Medication, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(40.dp))
                            Spacer(Modifier.height(8.dp))
                            Text("No local monograph matched", style = MaterialTheme.typography.titleMedium)
                            Text(
                                "Tap OpenFDA DB or AI Lookup above to load \"${searchQuery.ifBlank { "this medicine" }}\" live.",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            } else {
                items(filtered, key = { it.id }) { med ->
                    val isSaved = med.id in savedMedicineIds
                    GlassMedicalCard(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onOpenDetail(med.id) }
                            .testTag("medicine_card_${med.id}")
                    ) {
                        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                StatusPillBadge(
                                    label = med.category,
                                    color = MedicalBlue,
                                    backgroundColor = MedicalBlue.copy(alpha = 0.12f)
                                )
                                IconButton(
                                    onClick = { onToggleSave(med.id) },
                                    modifier = Modifier.size(36.dp)
                                ) {
                                    Icon(
                                        imageVector = if (isSaved) Icons.Rounded.Bookmark else Icons.Rounded.BookmarkBorder,
                                        contentDescription = if (isSaved) "Remove bookmark" else "Bookmark medicine",
                                        tint = if (isSaved) PremiumPurple else MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                            Text(med.genericName, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                            Text(
                                text = "Brands: ${med.brandNames.joinToString(" · ")}",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Text(
                                text = "${med.drugClass} • ${med.strengths.take(3).joinToString(", ")}",
                                style = MaterialTheme.typography.labelMedium,
                                color = MedicalTeal
                            )
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.End,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("16-Section Monograph", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.primary)
                                Spacer(Modifier.width(4.dp))
                                Icon(Icons.AutoMirrored.Rounded.ArrowForward, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(14.dp))
                            }
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun MedicineDetailScreen(
    medicineId: String,
    adminMedicines: List<AdminMedicine>,
    liveMedicines: List<MedicineMonograph> = emptyList(),
    isSaved: Boolean,
    onToggleSave: () -> Unit,
    onOpenMechanism: () -> Unit,
    onOpenInteractions: () -> Unit,
    onBack: () -> Unit
) {
    val med = remember(medicineId, adminMedicines, liveMedicines) {
        PharmaCatalog.findById(medicineId)
            ?: liveMedicines.firstOrNull { it.id == medicineId }
            ?: adminMedicines.firstOrNull { "admin_${it.id}" == medicineId }?.let { am ->
                MedicineMonograph(
                    id = "admin_${am.id}",
                    genericName = am.genericName,
                    brandNames = am.brandNames.ifEmpty { listOf("Generic") },
                    activeIngredients = listOf(am.genericName),
                    saltComposition = am.composition.ifBlank { am.genericName },
                    category = am.drugClass.ifBlank { "General" },
                    drugClass = am.drugClass,
                    subclass = "Cloud Catalog",
                    therapeuticUses = am.therapeuticUses.split(",").map { it.trim() }.filter { it.isNotEmpty() },
                    strengths = am.strengths.ifEmpty { listOf("Standard") },
                    dosageForms = am.dosageForms.ifEmpty { listOf("Tablet") },
                    route = "Oral / Label-Directed",
                    simpleMoa = am.mechanismOfAction.ifBlank { "See clinical label." },
                    advancedMoa = am.mechanismOfAction.ifBlank { "See clinical label." },
                    molecularTarget = "Label-defined target",
                    receptorInteraction = "Label-defined interaction",
                    enzymeInvolvement = "Label-defined metabolism",
                    biologicalPathway = "Systemic pathway",
                    physiologicalEffects = am.therapeuticUses,
                    commonSideEffects = emptyList(),
                    seriousSideEffects = emptyList(),
                    rareSideEffects = emptyList(),
                    contraindications = listOf("Hypersensitivity to active salt"),
                    precautions = listOf(am.precautions.ifBlank { "Use only under licensed clinical guidance." }),
                    keyInteractions = listOf("Verify via Drug Interaction Checker"),
                    storageInfo = "Store in a cool, dry place below 25°C.",
                    manufacturerInfo = "Verified Cloud Catalog Entry",
                    referenceSources = "DRxGuard Verified Admin Entry",
                    lastUpdated = "Cloud Synced",
                    halfLifeHours = "Per label",
                    bioavailability = "Per label",
                    metabolism = "Per label"
                )
            }
            ?: PharmaCatalog.medicines.first()
    }

    val expandedMap = remember(med.id) {
        mutableStateMapOf(
            "1" to true,
            "2" to true,
            "5" to true,
            "8" to true
        )
    }

    val monographSections = remember(med) {
        listOf(
            Triple("1", "1. Generic Name", med.genericName),
            Triple("2", "2. Brand Names", med.brandNames.joinToString(", ")),
            Triple("3", "3. Active Ingredients", med.activeIngredients.joinToString(", ")),
            Triple("4", "4. Salt Composition", med.saltComposition),
            Triple("5", "5. Drug Class & Subclass", "${med.drugClass} — ${med.subclass} (Category: ${med.category})"),
            Triple("6", "6. Therapeutic Uses", med.therapeuticUses.joinToString("\n• ", prefix = "• ")),
            Triple("7", "7. Strengths & Dosage Forms", "Strengths: ${med.strengths.joinToString(", ")}\nForms: ${med.dosageForms.joinToString(", ")}\nRoute: ${med.route}"),
            Triple("8", "8. Mechanism of Action", "Simple: ${med.simpleMoa}\n\nPharmacology: ${med.advancedMoa}"),
            Triple("9", "9. Pharmacokinetics Snapshot", "Half-life: ${med.halfLifeHours}\nBioavailability: ${med.bioavailability}\nMetabolism: ${med.metabolism}"),
            Triple("10", "10. Common Side Effects", med.commonSideEffects.joinToString("\n") { "• ${it.name} (${it.frequency}): ${it.description}" }.ifBlank { "Refer to official patient leaflet for full adverse event profile." }),
            Triple("11", "11. Serious & Rare Side Effects", (med.seriousSideEffects + med.rareSideEffects).joinToString("\n") { "• ${it.name} [${it.severity}]: ${it.warning}" }.ifBlank { "Seek urgent care for allergic swelling, breathing difficulty, or severe rash." }),
            Triple("12", "12. Contraindications", med.contraindications.joinToString("\n• ", prefix = "• ")),
            Triple("13", "13. Precautions & Warnings", med.precautions.joinToString("\n• ", prefix = "• ")),
            Triple("14", "14. Key Drug Interactions", med.keyInteractions.joinToString("\n• ", prefix = "• ")),
            Triple("15", "15. Storage & Manufacturer Info", "Storage: ${med.storageInfo}\nManufacturers: ${med.manufacturerInfo}"),
            Triple("16", "16. Reference Sources & Last Updated", "Sources: ${med.referenceSources}\nLast Updated: ${med.lastUpdated}")
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(med.genericName, maxLines = 1, overflow = TextOverflow.Ellipsis) },
                navigationIcon = {
                    IconButton(onClick = onBack, modifier = Modifier.testTag("med_detail_back")) {
                        Icon(Icons.AutoMirrored.Rounded.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = onToggleSave, modifier = Modifier.testTag("med_detail_bookmark")) {
                        Icon(
                            imageVector = if (isSaved) Icons.Rounded.Bookmark else Icons.Rounded.BookmarkBorder,
                            contentDescription = "Toggle bookmark",
                            tint = if (isSaved) PremiumPurple else MaterialTheme.colorScheme.onSurface
                        )
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
                Card(
                    shape = RoundedCornerShape(24.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Brush.linearGradient(listOf(DeepNavy, MedicalBlue)))
                            .padding(20.dp)
                    ) {
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            StatusPillBadge(
                                label = med.category.uppercase(),
                                color = ElectricCyan,
                                backgroundColor = Color.White.copy(alpha = 0.14f)
                            )
                            Text(med.genericName, style = MaterialTheme.typography.headlineSmall, color = Color.White, fontWeight = FontWeight.Bold)
                            Text(med.drugClass, style = MaterialTheme.typography.bodyMedium, color = Color.White.copy(alpha = 0.88f))
                            FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                med.brandNames.take(4).forEach { brand ->
                                    Surface(shape = CircleShape, color = Color.White.copy(alpha = 0.18f)) {
                                        Text(
                                            text = brand,
                                            style = MaterialTheme.typography.labelSmall,
                                            color = Color.White,
                                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                                        )
                                    }
                                }
                            }
                            Spacer(Modifier.height(4.dp))
                            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                                Button(
                                    onClick = onOpenMechanism,
                                    shape = RoundedCornerShape(12.dp),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Icon(Icons.Rounded.Science, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(Modifier.width(6.dp))
                                    Text("Mechanism", style = MaterialTheme.typography.labelMedium)
                                }
                                OutlinedButton(
                                    onClick = onOpenInteractions,
                                    shape = RoundedCornerShape(12.dp),
                                    border = BorderStroke(1.dp, Color.White.copy(alpha = 0.6f)),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Icon(Icons.Rounded.WarningAmber, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                                    Spacer(Modifier.width(6.dp))
                                    Text("Interactions", color = Color.White, style = MaterialTheme.typography.labelMedium)
                                }
                            }
                        }
                    }
                }
            }

            item {
                MedicalSafetyBanner(
                    text = "Educational pharmaceutical monograph for reference only. Follow your prescribing physician's exact dosing instructions and product labeling."
                )
            }

            items(monographSections, key = { it.first }) { (key, title, body) ->
                val expanded = expandedMap[key] ?: false
                GlassMedicalCard(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { expandedMap[key] = !expanded }
                                .padding(horizontal = 16.dp, vertical = 14.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(title, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
                            Icon(
                                imageVector = if (expanded) Icons.Rounded.ExpandLess else Icons.Rounded.ExpandMore,
                                contentDescription = "Toggle $title",
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                        AnimatedVisibility(visible = expanded) {
                            Column(modifier = Modifier.padding(start = 16.dp, end = 16.dp, bottom = 14.dp)) {
                                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                                Spacer(Modifier.height(10.dp))
                                Text(body, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurface)
                            }
                        }
                    }
                }
            }
        }
    }
}

// =========================================================================
// FEATURE 10: MECHANISM OF ACTION ("How Medicines Work")
// =========================================================================

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MechanismOfActionScreen(onBack: () -> Unit) {
    var search by remember { mutableStateOf("") }
    var selectedMed by remember { mutableStateOf(PharmaCatalog.medicines.first()) }
    var modeIndex by remember { mutableIntStateOf(0) } // 0 = Simple Explanation, 1 = Advanced Pharmacology

    val matches = remember(search) { PharmaCatalog.searchMedicines(search) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("How Medicines Work") },
                navigationIcon = {
                    IconButton(onClick = onBack, modifier = Modifier.testTag("moa_back_button")) {
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
            item {
                OutlinedTextField(
                    value = search,
                    onValueChange = { search = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("moa_search_input"),
                    placeholder = { Text("Select a medicine (e.g., Metformin, Omeprazole)...") },
                    leadingIcon = { Icon(Icons.Rounded.Search, contentDescription = null) },
                    singleLine = true,
                    shape = RoundedCornerShape(16.dp)
                )
            }

            item {
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(matches.take(10), key = { it.id }) { med ->
                        FilterChip(
                            selected = selectedMed.id == med.id,
                            onClick = { selectedMed = med },
                            label = { Text(med.genericName.substringBefore(" (")) }
                        )
                    }
                }
            }

            // Animated Molecular Receptor Binding Visualization
            item {
                Card(
                    shape = RoundedCornerShape(24.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Brush.verticalGradient(listOf(DeepNavy, Color(0xFF103766))))
                            .padding(16.dp)
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                StatusPillBadge(
                                    label = "MOLECULAR BINDING SIMULATION",
                                    color = ElectricCyan,
                                    backgroundColor = Color.White.copy(alpha = 0.14f)
                                )
                                Text(selectedMed.category, style = MaterialTheme.typography.labelSmall, color = Color.White.copy(alpha = 0.8f))
                            }
                            Spacer(Modifier.height(8.dp))
                            MolecularMechanismCanvas(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(170.dp),
                                accentPrimary = ElectricCyan,
                                accentSecondary = MedicalTeal
                            )
                            Text(
                                text = "${selectedMed.genericName} → ${selectedMed.molecularTarget}",
                                style = MaterialTheme.typography.labelMedium,
                                color = Color.White,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }
            }

            // Simple vs Advanced Pharmacology Toggle
            item {
                SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
                    listOf("Simple Explanation", "Advanced Pharmacology").forEachIndexed { idx, label ->
                        SegmentedButton(
                            selected = modeIndex == idx,
                            onClick = { modeIndex = idx },
                            shape = SegmentedButtonDefaults.itemShape(index = idx, count = 2),
                            modifier = Modifier.testTag("moa_mode_$idx")
                        ) {
                            Text(label, style = MaterialTheme.typography.labelMedium)
                        }
                    }
                }
            }

            // Primary Explanation Card
            item {
                GlassMedicalCard(
                    modifier = Modifier.fillMaxWidth(),
                    borderColor = MedicalBlue.copy(alpha = 0.35f)
                ) {
                    Column(modifier = Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Text(
                            text = if (modeIndex == 0) "Patient-Friendly Explanation" else "Advanced Pharmacological Mechanism",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = if (modeIndex == 0) selectedMed.simpleMoa else selectedMed.advancedMoa,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }

            // Structured Molecular Pathways Card
            item {
                GlassMedicalCard(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Text("Pharmacological Pathway Breakdown", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                        MoaInfoRow("Active Ingredient", selectedMed.activeIngredients.joinToString(", "))
                        MoaInfoRow("Drug Class", "${selectedMed.drugClass} (${selectedMed.subclass})")
                        MoaInfoRow("Molecular Target", selectedMed.molecularTarget)
                        MoaInfoRow("Receptor Interaction", selectedMed.receptorInteraction)
                        MoaInfoRow("Enzyme Involvement", selectedMed.enzymeInvolvement)
                        MoaInfoRow("Biological Pathway", selectedMed.biologicalPathway)
                        MoaInfoRow("Physiological Effects", selectedMed.physiologicalEffects)
                        MoaInfoRow("Pharmacokinetics", "Half-life: ${selectedMed.halfLifeHours} • Bioavailability: ${selectedMed.bioavailability} • Metabolism: ${selectedMed.metabolism}")
                    }
                }
            }
        }
    }
}

@Composable
private fun MoaInfoRow(label: String, value: String) {
    Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
        Text(label, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.SemiBold)
        Text(value, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurface)
        Spacer(Modifier.height(4.dp))
        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.6f))
    }
}

// =========================================================================
// FEATURE 11: HEALTH CONDITIONS LIBRARY (8 Categories, 10 Detail Sections)
// =========================================================================

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HealthConditionsScreen(onBack: () -> Unit) {
    val categories = remember {
        listOf("All", "Cardiovascular", "Respiratory", "Gastrointestinal", "Neurological", "Endocrine", "Infectious", "Skin", "Musculoskeletal")
    }
    var selectedCategory by remember { mutableStateOf("All") }
    var selectedConditionId by remember { mutableStateOf(PharmaCatalog.healthConditions.first().id) }

    val filteredConditions = remember(selectedCategory) {
        if (selectedCategory == "All") PharmaCatalog.healthConditions
        else PharmaCatalog.healthConditions.filter { it.category.equals(selectedCategory, ignoreCase = true) }
    }

    val activeCondition = remember(selectedConditionId, filteredConditions) {
        filteredConditions.firstOrNull { it.id == selectedConditionId } ?: filteredConditions.first()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Health Conditions Library") },
                navigationIcon = {
                    IconButton(onClick = onBack, modifier = Modifier.testTag("conditions_back_button")) {
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
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            item {
                MedicalSafetyBanner(
                    text = "Educational overview only. DRxGuard does not diagnose medical conditions. Consult a qualified clinician for evaluation and individualized care."
                )
            }

            item {
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(categories) { cat ->
                        FilterChip(
                            selected = selectedCategory == cat,
                            onClick = {
                                selectedCategory = cat
                                val firstInCat = if (cat == "All") PharmaCatalog.healthConditions.first()
                                else PharmaCatalog.healthConditions.firstOrNull { it.category == cat }
                                if (firstInCat != null) selectedConditionId = firstInCat.id
                            },
                            label = { Text(cat) }
                        )
                    }
                }
            }

            item {
                LazyRow(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    items(filteredConditions, key = { it.id }) { cond ->
                        val isSelected = cond.id == activeCondition.id
                        Surface(
                            shape = RoundedCornerShape(16.dp),
                            color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
                            modifier = Modifier
                                .clip(RoundedCornerShape(16.dp))
                                .clickable { selectedConditionId = cond.id }
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    Icons.Rounded.LocalHospital,
                                    contentDescription = null,
                                    tint = if (isSelected) Color.White else MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(Modifier.width(6.dp))
                                Text(
                                    text = cond.name,
                                    style = MaterialTheme.typography.labelLarge,
                                    color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                    }
                }
            }

            // 10-Section Condition Educational Guide
            item {
                GlassMedicalCard(
                    modifier = Modifier.fillMaxWidth(),
                    borderColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.35f)
                ) {
                    Column(modifier = Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        StatusPillBadge(
                            label = activeCondition.category.uppercase(),
                            color = MedicalTeal,
                            backgroundColor = MedicalTeal.copy(alpha = 0.14f)
                        )
                        Text(activeCondition.name, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
                        ConditionSection("1. Definition", activeCondition.definition)
                        ConditionSection("2. Types & Classifications", activeCondition.types.joinToString("\n• ", prefix = "• "))
                        ConditionSection("3. Common Symptoms", activeCondition.symptoms.joinToString("\n• ", prefix = "• "))
                        ConditionSection("4. Underlying Causes", activeCondition.causes.joinToString("\n• ", prefix = "• "))
                        ConditionSection("5. Risk Factors", activeCondition.riskFactors.joinToString("\n• ", prefix = "• "))
                        ConditionSection("6. Potential Complications", activeCondition.complications.joinToString("\n• ", prefix = "• "))
                        ConditionSection("7. Typical Diagnostic Evaluations", activeCondition.diagnostics.joinToString("\n• ", prefix = "• "))
                        ConditionSection("8. General Treatment Information", activeCondition.treatmentInfo)
                        ConditionSection("9. Prevention & Lifestyle Wellness", activeCondition.prevention.joinToString("\n• ", prefix = "• "))
                        Surface(
                            shape = RoundedCornerShape(14.dp),
                            color = LocalDRxColors.current.dangerContainer,
                            border = BorderStroke(1.dp, DangerRed.copy(alpha = 0.35f))
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Text(
                                    "10. When to Seek Urgent Medical Care",
                                    style = MaterialTheme.typography.titleSmall,
                                    color = DangerRed,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(Modifier.height(4.dp))
                                Text(
                                    activeCondition.seekCareWhen.joinToString("\n• ", prefix = "• "),
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ConditionSection(title: String, body: String) {
    Column(verticalArrangement = Arrangement.spacedBy(3.dp)) {
        Text(title, style = MaterialTheme.typography.titleSmall, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
        Text(body, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurface)
    }
}

// =========================================================================
// FEATURE 12: DRUG CLASSES EXPLORER (Interactive Tree)
// =========================================================================

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DrugClassesScreen(
    onOpenMedicineDetail: (String) -> Unit,
    onBack: () -> Unit
) {
    var selectedClass by remember { mutableStateOf(PharmaCatalog.drugClasses.first()) }
    var selectedSubclass by remember(selectedClass) { mutableStateOf(selectedClass.subclasses.keys.firstOrNull().orEmpty()) }
    var selectedMedId by remember(selectedSubclass) {
        mutableStateOf(selectedClass.subclasses[selectedSubclass]?.firstOrNull())
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Drug Classes Explorer") },
                navigationIcon = {
                    IconButton(onClick = onBack, modifier = Modifier.testTag("drug_classes_back")) {
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
            item {
                Text(
                    "Select from 10 Therapeutic Drug Classes",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }

            item {
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(PharmaCatalog.drugClasses, key = { it.name }) { node ->
                        FilterChip(
                            selected = selectedClass.name == node.name,
                            onClick = { selectedClass = node },
                            label = { Text(node.name) }
                        )
                    }
                }
            }

            // Interactive Classification Hierarchy Tree
            item {
                GlassMedicalCard(
                    modifier = Modifier.fillMaxWidth(),
                    borderColor = MedicalTeal.copy(alpha = 0.4f)
                ) {
                    Column(modifier = Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Rounded.AccountTree, contentDescription = null, tint = MedicalTeal)
                            Spacer(Modifier.width(8.dp))
                            Text("Interactive Classification Tree", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                        }

                        // Level 1: Drug Class
                        TreeNodeBox(
                            level = "LEVEL 1 • DRUG CLASS",
                            title = selectedClass.name,
                            accent = MedicalBlue
                        )

                        // Level 2: Subclasses
                        Text("Level 2 • Subclasses (Tap to inspect)", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            items(selectedClass.subclasses.keys.toList()) { sub ->
                                FilterChip(
                                    selected = selectedSubclass == sub,
                                    onClick = { selectedSubclass = sub },
                                    label = { Text(sub) }
                                )
                            }
                        }

                        // Level 3: Generic Medicines in Subclass
                        val medIds = selectedClass.subclasses[selectedSubclass].orEmpty()
                        Text("Level 3 • Generic Medicines", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        medIds.forEach { medId ->
                            val med = PharmaCatalog.findById(medId)
                            if (med != null) {
                                val isActiveMed = selectedMedId == medId
                                Surface(
                                    shape = RoundedCornerShape(14.dp),
                                    color = if (isActiveMed) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.55f),
                                    border = BorderStroke(1.dp, if (isActiveMed) MaterialTheme.colorScheme.primary else Color.Transparent),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(14.dp))
                                        .clickable { selectedMedId = medId }
                                ) {
                                    Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text(med.genericName, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                                            TextButton(onClick = { onOpenMedicineDetail(med.id) }) {
                                                Text("Monograph")
                                            }
                                        }
                                        // Level 4: Brand Medicines
                                        Text(
                                            "Level 4 • Brand Examples: ${med.brandNames.joinToString(" · ")}",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MedicalTeal
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Class Details Card
            item {
                GlassMedicalCard(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Text("${selectedClass.name} — Clinical Overview", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                        MoaInfoRow("Definition", selectedClass.definition)
                        MoaInfoRow("Pharmacological Action", selectedClass.pharmacologicalAction)
                        MoaInfoRow("Therapeutic Applications", selectedClass.therapeuticApplications.joinToString("\n• ", prefix = "• "))
                        MoaInfoRow("Mechanism Summary", selectedClass.mechanismSummary)
                        MoaInfoRow("Common Adverse Effects", selectedClass.commonAdverseEffects.joinToString("\n• ", prefix = "• "))
                        MoaInfoRow("Key Precautions", selectedClass.precautions.joinToString("\n• ", prefix = "• "))
                    }
                }
            }
        }
    }
}

@Composable
private fun TreeNodeBox(level: String, title: String, accent: Color) {
    Surface(
        shape = RoundedCornerShape(14.dp),
        color = accent.copy(alpha = 0.12f),
        border = BorderStroke(1.dp, accent.copy(alpha = 0.35f)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(level, style = MaterialTheme.typography.labelSmall, color = accent, fontWeight = FontWeight.Bold)
            Text(title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        }
    }
}

// =========================================================================
// FEATURE 14: MEDICINE CHANGE SIMULATOR ("Explore Medicine Differences")
// =========================================================================

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MedicineChangeSimulatorScreen(onBack: () -> Unit) {
    var originalMed by remember { mutableStateOf(PharmaCatalog.medicines[3]) } // Omeprazole
    var alternativeMed by remember { mutableStateOf(PharmaCatalog.medicines[4]) } // Pantoprazole
    var pickingFor by remember { mutableStateOf<String?>(null) }

    val sameClass = originalMed.drugClass == alternativeMed.drugClass
    val sameSalt = originalMed.saltComposition.equals(alternativeMed.saltComposition, ignoreCase = true)
    val sameRoute = originalMed.route == alternativeMed.route

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Explore Medicine Differences") },
                navigationIcon = {
                    IconButton(onClick = onBack, modifier = Modifier.testTag("simulator_back_button")) {
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
            // MANDATORY SAFETY DISCLAIMER
            item {
                MedicalSafetyBanner(
                    text = "Educational comparison only. This tool does not establish therapeutic equivalence or authorize medicine substitution.",
                    isUrgent = true
                )
            }

            // Original vs Alternative Selectors
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    SimulatorSelectorTile(
                        label = "ORIGINAL MEDICINE",
                        med = originalMed,
                        accent = MedicalBlue,
                        onClick = { pickingFor = "ORIGINAL" },
                        modifier = Modifier
                            .weight(1f)
                            .testTag("sim_select_original")
                    )
                    SimulatorSelectorTile(
                        label = "ALTERNATIVE",
                        med = alternativeMed,
                        accent = PremiumPurple,
                        onClick = { pickingFor = "ALTERNATIVE" },
                        modifier = Modifier
                            .weight(1f)
                            .testTag("sim_select_alternative")
                    )
                }
            }

            // Animated Side-by-Side Molecular Graphic + Match Indicators
            item {
                Card(shape = RoundedCornerShape(24.dp), modifier = Modifier.fillMaxWidth()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Brush.linearGradient(listOf(DeepNavy, Color(0xFF1C2C54))))
                            .padding(16.dp)
                    ) {
                        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Rounded.Compare, contentDescription = null, tint = ElectricCyan)
                                Spacer(Modifier.width(8.dp))
                                Text("Pharmacological Delta Visual", style = MaterialTheme.typography.titleSmall, color = Color.White, fontWeight = FontWeight.Bold)
                            }
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                MolecularMechanismCanvas(
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(110.dp),
                                    accentPrimary = ElectricCyan,
                                    accentSecondary = MedicalBlue
                                )
                                MolecularMechanismCanvas(
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(110.dp),
                                    accentPrimary = PremiumPurple,
                                    accentSecondary = MedicalTeal
                                )
                            }
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceEvenly
                            ) {
                                StatusPillBadge(
                                    label = if (sameSalt) "SAME SALT" else "DIFFERENT SALT",
                                    color = if (sameSalt) SuccessGreen else WarningAmber,
                                    backgroundColor = Color.White.copy(alpha = 0.14f)
                                )
                                StatusPillBadge(
                                    label = if (sameClass) "SAME CLASS" else "DIFFERENT CLASS",
                                    color = if (sameClass) SuccessGreen else DangerRed,
                                    backgroundColor = Color.White.copy(alpha = 0.14f)
                                )
                            }
                        }
                    }
                }
            }

            // Side-by-Side Comparison Matrix
            item {
                GlassMedicalCard(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Text("Side-by-Side Pharmacological Breakdown", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                        SimCompareRow("Salt Composition", originalMed.saltComposition, alternativeMed.saltComposition)
                        SimCompareRow("Drug Class", originalMed.drugClass, alternativeMed.drugClass)
                        SimCompareRow("Strengths Available", originalMed.strengths.joinToString(", "), alternativeMed.strengths.joinToString(", "))
                        SimCompareRow("Dosage Forms & Route", "${originalMed.dosageForms.joinToString(", ")} (${originalMed.route})", "${alternativeMed.dosageForms.joinToString(", ")} (${alternativeMed.route})")
                        SimCompareRow("Mechanism of Action", originalMed.simpleMoa, alternativeMed.simpleMoa)
                        SimCompareRow("Half-Life", originalMed.halfLifeHours, alternativeMed.halfLifeHours)
                        SimCompareRow("Bioavailability", originalMed.bioavailability, alternativeMed.bioavailability)
                        SimCompareRow("Metabolism / CYP Profile", originalMed.metabolism, alternativeMed.metabolism)
                        SimCompareRow("Key Precautions", originalMed.precautions.firstOrNull().orEmpty(), alternativeMed.precautions.firstOrNull().orEmpty())
                    }
                }
            }

            // Known Clinical Differences Summary
            item {
                GlassMedicalCard(
                    modifier = Modifier.fillMaxWidth(),
                    borderColor = WarningAmber.copy(alpha = 0.45f)
                ) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Rounded.HealthAndSafety, contentDescription = null, tint = WarningAmber)
                            Spacer(Modifier.width(8.dp))
                            Text("Educational Takeaways & Pharmacist Check", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                        }
                        Text(
                            text = buildString {
                                append("• Salt Match: ${if (sameSalt) "Identical active salt composition." else "Different active molecules — clinical dosing and enzyme interactions are NOT interchangeable 1:1."}\n")
                                append("• Class Alignment: ${if (sameClass) "Both belong to ${originalMed.drugClass}." else "${originalMed.drugClass} vs. ${alternativeMed.drugClass}."}\n")
                                append("• Administration Route: ${if (sameRoute) "Both use ${originalMed.route}." else "Routes differ (${originalMed.route} vs. ${alternativeMed.route})."}\n")
                                append("• Safety Rule: DRxGuard never calculates patient-specific dose conversions. Consult your prescribing doctor or pharmacist before making any therapy change.")
                            },
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }
        }
    }

    if (pickingFor != null) {
        AlertDialog(
            onDismissRequest = { pickingFor = null },
            title = { Text("Select ${if (pickingFor == "ORIGINAL") "Original" else "Alternative"} Medicine") },
            text = {
                LazyColumn(
                    modifier = Modifier.height(340.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(PharmaCatalog.medicines, key = { it.id }) { m ->
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .clickable {
                                    if (pickingFor == "ORIGINAL") originalMed = m else alternativeMed = m
                                    pickingFor = null
                                }
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text(m.genericName, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                                Text("${m.drugClass} • ${m.brandNames.take(2).joinToString(", ")}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { pickingFor = null }) { Text("Close") }
            }
        )
    }
}

@Composable
private fun SimulatorSelectorTile(
    label: String,
    med: MedicineMonograph,
    accent: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    GlassMedicalCard(
        modifier = modifier.clickable(onClick = onClick),
        borderColor = accent.copy(alpha = 0.4f)
    ) {
        Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(label, style = MaterialTheme.typography.labelSmall, color = accent, fontWeight = FontWeight.Bold)
            Text(med.genericName, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, maxLines = 1, overflow = TextOverflow.Ellipsis)
            Text(med.brandNames.firstOrNull().orEmpty(), style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text("Tap to change", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.primary)
        }
    }
}

@Composable
private fun SimCompareRow(parameter: String, leftVal: String, rightVal: String) {
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Text(parameter, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            Surface(
                shape = RoundedCornerShape(10.dp),
                color = MedicalBlue.copy(alpha = 0.08f),
                modifier = Modifier.weight(1f)
            ) {
                Text(leftVal, style = MaterialTheme.typography.bodySmall, modifier = Modifier.padding(10.dp))
            }
            Surface(
                shape = RoundedCornerShape(10.dp),
                color = PremiumPurple.copy(alpha = 0.08f),
                modifier = Modifier.weight(1f)
            ) {
                Text(rightVal, style = MaterialTheme.typography.bodySmall, modifier = Modifier.padding(10.dp))
            }
        }
    }
}
