package com.example.data

import androidx.annotation.Keep
import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.IgnoreExtraProperties
import com.google.firebase.firestore.ServerTimestamp

@Keep
@IgnoreExtraProperties
data class UserProfile(
    @DocumentId val id: String = "",
    val userId: String = "",
    val fullName: String = "DRxGuard Member",
    val email: String = "",
    val mobileNumber: String? = null,
    val dateOfBirth: String? = null,
    val preferredLanguage: String = "English",
    val darkMode: Boolean = false,
    val healthConsentGranted: Boolean = false,
    val heightCm: Double? = null,
    val weightKg: Double? = null,
    val allergies: String? = null,
    val conditions: String? = null,
    val emergencyContact: String? = null,
    val savedMedicineIds: List<String> = emptyList(),
    val expiryAlertDays: Long = 30L,
    @ServerTimestamp val createdAt: Timestamp? = null,
    @ServerTimestamp val updatedAt: Timestamp? = null
)

@Keep
@IgnoreExtraProperties
data class MedicineReminder(
    @DocumentId val id: String = "",
    val medicineName: String = "",
    val strength: String = "",
    val dosageForm: String = "Tablet",
    val prescribedDose: String = "1 tablet",
    val scheduleType: String = "Once daily",
    val reminderTimes: List<String> = listOf("08:00"),
    val startDate: String = "",
    val endDate: String = "",
    val prescribingProfessional: String = "",
    val instructions: String = "",
    val status: String = "UPCOMING",
    @ServerTimestamp val createdAt: Timestamp? = null,
    @ServerTimestamp val updatedAt: Timestamp? = null
)

@Keep
@IgnoreExtraProperties
data class CabinetMedicine(
    @DocumentId val id: String = "",
    val medicineName: String = "",
    val brand: String = "",
    val batchNumber: String = "",
    val expiryDate: String = "",
    val quantity: Long = 0L,
    val strength: String = "",
    val storageLocation: String = "Home Medicine Cabinet",
    val alertDaysBefore: Long = 30L,
    @ServerTimestamp val createdAt: Timestamp? = null,
    @ServerTimestamp val updatedAt: Timestamp? = null
)

@Keep
@IgnoreExtraProperties
data class BmiEntry(
    @DocumentId val id: String = "",
    val bmiValue: Double = 0.0,
    val category: String = "",
    val heightCm: Double = 0.0,
    val weightKg: Double = 0.0,
    val recordedDate: String = "",
    @ServerTimestamp val createdAt: Timestamp? = null
)

@Keep
@IgnoreExtraProperties
data class SavedReport(
    @DocumentId val id: String = "",
    val reportType: String = "INTERACTION",
    val title: String = "",
    val summary: String = "",
    val severity: String = "INFO",
    @ServerTimestamp val createdAt: Timestamp? = null
)

@Keep
@IgnoreExtraProperties
data class AppNotification(
    @DocumentId val id: String = "",
    val category: String = "Medicine reminders",
    val title: String = "",
    val message: String = "",
    val read: Boolean = false,
    @ServerTimestamp val createdAt: Timestamp? = null
)

@Keep
@IgnoreExtraProperties
data class AdminMedicine(
    @DocumentId val id: String = "",
    val genericName: String = "",
    val brandNames: List<String> = emptyList(),
    val drugClass: String = "",
    val strengths: List<String> = emptyList(),
    val dosageForms: List<String> = emptyList(),
    val composition: String = "",
    val therapeuticUses: String = "",
    val mechanismOfAction: String = "",
    val commonSideEffects: String = "",
    val precautions: String = "",
    val published: Boolean = true,
    val updatedBy: String = "",
    @ServerTimestamp val createdAt: Timestamp? = null,
    @ServerTimestamp val updatedAt: Timestamp? = null
)
