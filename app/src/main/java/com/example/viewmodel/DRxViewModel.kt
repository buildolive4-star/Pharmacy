package com.example.viewmodel

import android.graphics.Bitmap
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.AdminMedicine
import com.example.data.AppNotification
import com.example.data.BmiEntry
import com.example.data.CabinetMedicine
import com.example.data.DRxAiService
import com.example.data.DRxRepository
import com.example.data.LabAiInterpretation
import com.example.data.MedicineReminder
import com.example.data.PharmaCatalog
import com.example.data.PillScanAnalysis
import com.example.data.SavedReport
import com.example.data.UserProfile
import com.google.firebase.firestore.FirebaseFirestoreException
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

private const val VM_TAG = "DRxViewModel"

sealed interface UiState<out T> {
    data object Loading : UiState<Nothing>
    data class Success<T>(val data: T) : UiState<T>
    data class Error(val message: String) : UiState<Nothing>
}

sealed interface AiCallState<out T> {
    data object Idle : AiCallState<Nothing>
    data object Loading : AiCallState<Nothing>
    data class Streaming(val partial: String) : AiCallState<Nothing>
    data class Success<T>(val data: T) : AiCallState<T>
    data class Error(val message: String) : AiCallState<Nothing>
}

data class ChatMessage(
    val id: String,
    val fromUser: Boolean,
    val text: String,
    val language: String = "English",
    val hasImage: Boolean = false
)

class DRxViewModel(
    private val repository: DRxRepository,
    private val aiService: DRxAiService,
    val currentUserId: String,
    val initialName: String,
    val initialEmail: String
) : ViewModel() {

    private fun <T> Flow<T>.toUiState(label: String): StateFlow<UiState<T>> =
        this.map<T, UiState<T>> { UiState.Success(it) }
            .catch { error ->
                val firestoreError = generateSequence(error) { it.cause }
                    .filterIsInstance<FirebaseFirestoreException>()
                    .firstOrNull()
                if (firestoreError == null && error is CancellationException) throw error
                val rootError = firestoreError ?: error
                Log.w(VM_TAG, "Error observing $label", rootError)
                emit(UiState.Error(rootError.message ?: "Failed to load $label"))
            }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000L),
                initialValue = UiState.Loading
            )

    val profileState: StateFlow<UiState<UserProfile?>> =
        repository.observeProfile(currentUserId).toUiState("profile")

    val remindersState: StateFlow<UiState<List<MedicineReminder>>> =
        repository.observeReminders(currentUserId).toUiState("reminders")

    val cabinetState: StateFlow<UiState<List<CabinetMedicine>>> =
        repository.observeCabinet(currentUserId).toUiState("medicine cabinet")

    val bmiHistoryState: StateFlow<UiState<List<BmiEntry>>> =
        repository.observeBmiHistory(currentUserId).toUiState("BMI history")

    val reportsState: StateFlow<UiState<List<SavedReport>>> =
        repository.observeReports(currentUserId).toUiState("saved reports")

    val notificationsState: StateFlow<UiState<List<AppNotification>>> =
        repository.observeNotifications(currentUserId).toUiState("notifications")

    val adminMedicinesState: StateFlow<UiState<List<AdminMedicine>>> =
        repository.observeAdminMedicines(currentUserId).toUiState("admin medicines")

    private val _selectedLanguage = MutableStateFlow("English")
    val selectedLanguage: StateFlow<String> = _selectedLanguage.asStateFlow()

    private val _darkModeOverride = MutableStateFlow<Boolean?>(null)
    val darkModeOverride: StateFlow<Boolean?> = _darkModeOverride.asStateFlow()

    private val _recentlyViewedMedicineIds = MutableStateFlow(
        listOf("med_paracetamol", "med_metformin", "med_amoxicillin", "med_amlodipine")
    )
    val recentlyViewedMedicineIds: StateFlow<List<String>> = _recentlyViewedMedicineIds.asStateFlow()

    private val _recentlyUsedTools = MutableStateFlow(
        listOf("Drug Interaction", "Pill Scanner", "LabGuard", "AI Pharmacist")
    )
    val recentlyUsedTools: StateFlow<List<String>> = _recentlyUsedTools.asStateFlow()

    private val _bannerMessage = MutableStateFlow<String?>(null)
    val bannerMessage: StateFlow<String?> = _bannerMessage.asStateFlow()

    // --- AI Pharmacist State ---
    private val _chatMessages = MutableStateFlow<List<ChatMessage>>(
        listOf(
            ChatMessage(
                id = "welcome_ai",
                fromUser = false,
                text = "### 1. Direct Answer\nWelcome to **DRxGuard AI**! Ask any pharmaceutical question in your preferred language, upload a medicine packaging photo, or tap a suggested prompt below.\n\n### 6. When to Consult a Professional\nDRxGuard AI provides general pharmaceutical education only and does not replace personalized care from a licensed physician or pharmacist."
            )
        )
    )
    val chatMessages: StateFlow<List<ChatMessage>> = _chatMessages.asStateFlow()

    private val _aiChatState = MutableStateFlow<AiCallState<String>>(AiCallState.Idle)
    val aiChatState: StateFlow<AiCallState<String>> = _aiChatState.asStateFlow()

    private var lastAiQuestion: String? = null
    private var lastAiImage: Bitmap? = null

    // --- Pill Scanner AI State ---
    private val _pillScanState = MutableStateFlow<AiCallState<PillScanAnalysis>>(AiCallState.Idle)
    val pillScanState: StateFlow<AiCallState<PillScanAnalysis>> = _pillScanState.asStateFlow()

    // --- LabGuard AI State ---
    private val _labAiState = MutableStateFlow<AiCallState<LabAiInterpretation>>(AiCallState.Idle)
    val labAiState: StateFlow<AiCallState<LabAiInterpretation>> = _labAiState.asStateFlow()

    // --- Pregnancy Symptom AI State ---
    private val _pregnancySymptomState = MutableStateFlow<AiCallState<String>>(AiCallState.Idle)
    val pregnancySymptomState: StateFlow<AiCallState<String>> = _pregnancySymptomState.asStateFlow()

    // --- Admin Metrics Counter (live session + Firestore data) ---
    private val _aiQueriesCount = MutableStateFlow(12)
    val aiQueriesCount: StateFlow<Int> = _aiQueriesCount.asStateFlow()

    private val _interactionChecksCount = MutableStateFlow(8)
    val interactionChecksCount: StateFlow<Int> = _interactionChecksCount.asStateFlow()

    private val _scannerRequestsCount = MutableStateFlow(5)
    val scannerRequestsCount: StateFlow<Int> = _scannerRequestsCount.asStateFlow()

    private val _labGuardRequestsCount = MutableStateFlow(4)
    val labGuardRequestsCount: StateFlow<Int> = _labGuardRequestsCount.asStateFlow()

    init {
        viewModelScope.launch {
            repository.ensureUserProfile(
                fullName = initialName.ifBlank { "DRxGuard Member" },
                email = initialEmail,
                preferredLanguage = _selectedLanguage.value
            )
        }
    }

    fun clearBannerMessage() {
        _bannerMessage.value = null
    }

    fun recordMedicineViewed(medicineId: String) {
        _recentlyViewedMedicineIds.update { current ->
            (listOf(medicineId) + current.filterNot { it == medicineId }).take(8)
        }
    }

    fun recordToolUsed(toolName: String) {
        _recentlyUsedTools.update { current ->
            (listOf(toolName) + current.filterNot { it == toolName }).take(6)
        }
    }

    fun recordInteractionCheck() {
        _interactionChecksCount.update { it + 1 }
        recordToolUsed("Drug Interaction")
    }

    fun setLanguage(language: String) {
        _selectedLanguage.value = language
        viewModelScope.launch {
            repository.updateUserProfile(mapOf("preferredLanguage" to language))
        }
    }

    fun setDarkMode(enabled: Boolean) {
        _darkModeOverride.value = enabled
        viewModelScope.launch {
            repository.updateUserProfile(mapOf("darkMode" to enabled))
        }
    }

    fun updateProfile(
        fullName: String,
        mobileNumber: String,
        dateOfBirth: String,
        preferredLanguage: String,
        healthConsentGranted: Boolean,
        heightCm: Double?,
        weightKg: Double?,
        allergies: String,
        conditions: String,
        emergencyContact: String
    ) {
        _selectedLanguage.value = preferredLanguage
        viewModelScope.launch {
            val updates = mutableMapOf<String, Any?>(
                "fullName" to fullName.trim().ifBlank { "DRxGuard Member" }.take(100),
                "mobileNumber" to mobileNumber.trim().take(30),
                "dateOfBirth" to dateOfBirth.trim().take(30),
                "preferredLanguage" to preferredLanguage.take(40),
                "healthConsentGranted" to healthConsentGranted
            )
            if (healthConsentGranted) {
                if (heightCm != null) updates["heightCm"] = heightCm.coerceIn(0.0, 300.0)
                if (weightKg != null) updates["weightKg"] = weightKg.coerceIn(0.0, 500.0)
                updates["allergies"] = allergies.trim().take(400)
                updates["conditions"] = conditions.trim().take(400)
                updates["emergencyContact"] = emergencyContact.trim().take(120)
            }
            val result = repository.updateUserProfile(updates)
            _bannerMessage.value = if (result.isSuccess) {
                "Profile saved to cloud."
            } else {
                result.exceptionOrNull()?.localizedMessage ?: "Unable to update profile."
            }
        }
    }

    fun toggleSavedMedicine(medicineId: String) {
        val currentSaved = (profileState.value as? UiState.Success)?.data?.savedMedicineIds ?: emptyList()
        val adding = medicineId !in currentSaved
        viewModelScope.launch {
            val res = repository.toggleSavedMedicine(currentSaved, medicineId)
            if (res.isSuccess) {
                val medName = PharmaCatalog.findById(medicineId)?.genericName ?: "Medicine"
                _bannerMessage.value = if (adding) "Saved $medName to bookmarks" else "Removed $medName from bookmarks"
            } else {
                _bannerMessage.value = res.exceptionOrNull()?.localizedMessage ?: "Failed to update bookmarks"
            }
        }
    }

    // --- Reminders ---

    fun addReminder(reminder: MedicineReminder) {
        viewModelScope.launch {
            val res = repository.addReminder(reminder)
            if (res.isSuccess) {
                repository.addNotification(
                    category = "Medicine reminders",
                    title = "Reminder Scheduled: ${reminder.medicineName}",
                    message = "${reminder.prescribedDose} (${reminder.scheduleType}) at ${reminder.reminderTimes.joinToString(", ")}"
                )
                _bannerMessage.value = "Reminder added & synced."
            } else {
                _bannerMessage.value = res.exceptionOrNull()?.localizedMessage ?: "Failed to save reminder."
            }
        }
    }

    fun updateReminderStatus(reminderId: String, status: String) {
        viewModelScope.launch {
            val res = repository.updateReminderStatus(reminderId, status)
            _bannerMessage.value = if (res.isSuccess) {
                "Reminder marked as ${status.lowercase().replaceFirstChar { it.uppercase() }}"
            } else {
                res.exceptionOrNull()?.localizedMessage ?: "Failed to update reminder."
            }
        }
    }

    fun deleteReminder(reminderId: String) {
        viewModelScope.launch {
            val res = repository.deleteReminder(reminderId)
            if (res.isFailure) {
                _bannerMessage.value = res.exceptionOrNull()?.localizedMessage ?: "Failed to delete reminder."
            }
        }
    }

    // --- Medicine Cabinet / Expiry Tracker ---

    fun addCabinetMedicine(item: CabinetMedicine) {
        viewModelScope.launch {
            val res = repository.addCabinetMedicine(item)
            if (res.isSuccess) {
                repository.addNotification(
                    category = "Expiry alerts",
                    title = "Cabinet Updated: ${item.medicineName}",
                    message = "Batch ${item.batchNumber.ifBlank { "N/A" }} expires ${item.expiryDate}. Alert set ${item.alertDaysBefore} days prior."
                )
                _bannerMessage.value = "${item.medicineName} added to Medicine Cabinet."
            } else {
                _bannerMessage.value = res.exceptionOrNull()?.localizedMessage ?: "Failed to save cabinet item."
            }
        }
    }

    fun deleteCabinetMedicine(itemId: String) {
        viewModelScope.launch {
            repository.deleteCabinetMedicine(itemId)
        }
    }

    // --- BMI History ---

    fun saveBmiEntry(bmi: Double, category: String, heightCm: Double, weightKg: Double, date: String) {
        viewModelScope.launch {
            val res = repository.addBmiEntry(
                BmiEntry(
                    bmiValue = bmi,
                    category = category,
                    heightCm = heightCm,
                    weightKg = weightKg,
                    recordedDate = date
                )
            )
            _bannerMessage.value = if (res.isSuccess) {
                "BMI (${String.format("%.1f", bmi)}) saved to history."
            } else {
                res.exceptionOrNull()?.localizedMessage ?: "Could not save BMI entry."
            }
        }
    }

    // --- Reports ---

    fun saveReport(reportType: String, title: String, summary: String, severity: String) {
        viewModelScope.launch {
            val res = repository.saveReport(reportType, title, summary, severity)
            if (res.isSuccess) {
                repository.addNotification(
                    category = "Report analysis updates",
                    title = "Report Saved: $title",
                    message = summary.take(140)
                )
                _bannerMessage.value = "Report saved to My Reports."
            } else {
                _bannerMessage.value = res.exceptionOrNull()?.localizedMessage ?: "Failed to save report."
            }
        }
    }

    fun deleteReport(reportId: String) {
        viewModelScope.launch { repository.deleteReport(reportId) }
    }

    // --- Notifications ---

    fun markNotificationRead(id: String) {
        viewModelScope.launch { repository.markNotificationRead(id) }
    }

    fun triggerSimulatedNotification(category: String, title: String, message: String) {
        viewModelScope.launch {
            val res = repository.addNotification(category, title, message)
            if (res.isSuccess) {
                _bannerMessage.value = "Notification created in Notification Center."
            }
        }
    }

    // --- AI Pharmacist ---

    fun askAiPharmacist(question: String, attachedImage: Bitmap? = null) {
        val trimmed = question.trim()
        if (trimmed.isEmpty() && attachedImage == null) return
        val lang = _selectedLanguage.value
        val displayQ = trimmed.ifEmpty { "Please provide educational information about this uploaded medicine image." }
        lastAiQuestion = displayQ
        lastAiImage = attachedImage

        val userMsg = ChatMessage(
            id = "u_${System.currentTimeMillis()}",
            fromUser = true,
            text = displayQ,
            language = lang,
            hasImage = attachedImage != null
        )
        _chatMessages.update { it + userMsg }
        _aiQueriesCount.update { it + 1 }
        recordToolUsed("AI Pharmacist")

        executeAiPharmacistCall(displayQ, lang, attachedImage)
    }

    fun retryLastAiQuestion() {
        val q = lastAiQuestion ?: return
        executeAiPharmacistCall(q, _selectedLanguage.value, lastAiImage)
    }

    private fun executeAiPharmacistCall(question: String, lang: String, attachedImage: Bitmap?) {
        val recentTurns = _chatMessages.value
            .dropLast(1)
            .filter { it.id != "welcome_ai" && it.id != "welcome_reset" }
            .takeLast(4)
            .map { it.fromUser to it.text }
        viewModelScope.launch {
            _aiChatState.value = AiCallState.Loading
            var finalReply = ""
            try {
                aiService.streamPharmacistResponse(question, lang, attachedImage, recentTurns).collect { partial ->
                    finalReply = partial
                    _aiChatState.value = AiCallState.Streaming(partial)
                }
                _chatMessages.update {
                    it + ChatMessage(
                        id = "ai_${System.currentTimeMillis()}",
                        fromUser = false,
                        text = finalReply,
                        language = lang
                    )
                }
                _aiChatState.value = AiCallState.Success(finalReply)
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                Log.e(VM_TAG, "AI Pharmacist error", e)
                _aiChatState.value = AiCallState.Error(e.localizedMessage ?: "DRxGuard AI request failed.")
            }
        }
    }

    fun clearAiChat() {
        lastAiQuestion = null
        lastAiImage = null
        _chatMessages.value = listOf(
            ChatMessage(
                id = "welcome_reset",
                fromUser = false,
                text = "### 1. Direct Answer\nChat cleared. Ask any medicine, drug interaction, or laboratory question in **${_selectedLanguage.value}**."
            )
        )
        _aiChatState.value = AiCallState.Idle
    }

    // --- Pill Identification Scanner ---

    fun scanPillWithAi(bitmap: Bitmap?, manualHint: String) {
        _scannerRequestsCount.update { it + 1 }
        recordToolUsed("Pill Scanner")
        viewModelScope.launch {
            _pillScanState.value = AiCallState.Loading
            try {
                val result = aiService.analyzePillImage(bitmap, manualHint)
                _pillScanState.value = AiCallState.Success(result)
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                Log.e(VM_TAG, "Pill Scanner error", e)
                _pillScanState.value = AiCallState.Error(e.localizedMessage ?: "Pill scan failed.")
            }
        }
    }

    fun resetPillScan() {
        _pillScanState.value = AiCallState.Idle
    }

    // --- LabGuard AI ---

    fun analyzeLabWithAi(summaryText: String, attachedImage: Bitmap? = null) {
        _labGuardRequestsCount.update { it + 1 }
        recordToolUsed("LabGuard")
        viewModelScope.launch {
            _labAiState.value = AiCallState.Loading
            try {
                val interpretation = aiService.analyzeLabReport(summaryText, attachedImage)
                _labAiState.value = AiCallState.Success(interpretation)
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                Log.e(VM_TAG, "LabGuard AI error", e)
                _labAiState.value = AiCallState.Error(e.localizedMessage ?: "LabGuard AI analysis failed.")
            }
        }
    }

    // --- Pregnancy Symptom AI ---

    fun analyzePregnancySymptoms(symptoms: String, week: Int) {
        if (symptoms.isBlank()) return
        viewModelScope.launch {
            _pregnancySymptomState.value = AiCallState.Loading
            try {
                val guidance = aiService.analyzePregnancySymptoms(symptoms, week)
                _pregnancySymptomState.value = AiCallState.Success(guidance)
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                Log.e(VM_TAG, "Pregnancy symptom error", e)
                _pregnancySymptomState.value = AiCallState.Error(e.localizedMessage ?: "Could not generate educational guidance.")
            }
        }
    }

    // --- Admin Panel ---

    fun addAdminMedicine(med: AdminMedicine) {
        viewModelScope.launch {
            val res = repository.addAdminMedicine(med)
            _bannerMessage.value = if (res.isSuccess) {
                "Published ${med.genericName} to DRxGuard cloud catalog."
            } else {
                res.exceptionOrNull()?.localizedMessage ?: "Admin privileges required to publish to cloud catalog."
            }
        }
    }

    fun deleteAllAccountData(onDeleted: () -> Unit) {
        viewModelScope.launch {
            val res = repository.deleteUserAccountData()
            if (res.isSuccess) {
                onDeleted()
            } else {
                _bannerMessage.value = res.exceptionOrNull()?.localizedMessage ?: "Could not delete account data."
            }
        }
    }
}
