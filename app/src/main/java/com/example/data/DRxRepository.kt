package com.example.data

import android.content.Context
import com.example.R
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.snapshots
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await

class DRxRepository(
    private val db: FirebaseFirestore,
    private val auth: FirebaseAuth = Firebase.auth
) {
    constructor(context: Context) : this(
        FirebaseFirestore.getInstance(
            context.applicationContext.getString(R.string.firestore_database_id)
        )
    )

    private fun requireUserId(): String {
        return auth.currentUser?.uid
            ?: throw IllegalStateException("User must be signed in with Google before accessing Firestore.")
    }

    private fun unwrapAndLogFlowError(error: Throwable, op: OperationType, path: String, expectedUid: String) {
        val firestoreError = generateSequence(error) { it.cause }
            .filterIsInstance<FirebaseFirestoreException>()
            .firstOrNull()
        if (firestoreError == null && error is CancellationException) throw error
        val exceptionToLog = firestoreError ?: (error as? Exception)
        if (exceptionToLog != null && auth.currentUser?.uid == expectedUid) {
            handleFirestoreError(exceptionToLog, op, path)
        }
        throw error
    }

    // --- User Profile ---

    fun observeProfile(userId: String): Flow<UserProfile?> = flow {
        val path = "users/$userId"
        emitAll(
            db.collection("users").document(userId)
                .snapshots()
                .map { snapshot ->
                    if (snapshot.exists()) {
                        snapshot.toObject(UserProfile::class.java, DocumentSnapshot.ServerTimestampBehavior.ESTIMATE)
                    } else null
                }
                .catch { error -> unwrapAndLogFlowError(error, OperationType.GET, path, userId) }
        )
    }

    suspend fun ensureUserProfile(
        fullName: String,
        email: String,
        preferredLanguage: String = "English",
        darkMode: Boolean = false
    ): Result<Unit> {
        var docPath = "users/${auth.currentUser?.uid ?: "unauthenticated"}"
        return try {
            val uid = requireUserId()
            val docRef = db.collection("users").document(uid)
            docPath = docRef.path
            val snap = docRef.get().await()
            if (!snap.exists()) {
                val safeName = fullName.trim().ifBlank { "DRxGuard Member" }.take(100)
                val payload = mapOf(
                    "userId" to uid,
                    "fullName" to safeName,
                    "email" to email.trim().take(120),
                    "preferredLanguage" to preferredLanguage.take(40),
                    "darkMode" to darkMode,
                    "healthConsentGranted" to false,
                    "savedMedicineIds" to emptyList<String>(),
                    "expiryAlertDays" to 30L,
                    "createdAt" to FieldValue.serverTimestamp(),
                    "updatedAt" to FieldValue.serverTimestamp()
                )
                docRef.set(payload).await()
            }
            Result.success(Unit)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.WRITE, docPath)
            Result.failure(e)
        }
    }

    suspend fun updateUserProfile(updates: Map<String, Any?>): Result<Unit> {
        var docPath = "users/${auth.currentUser?.uid ?: "unauthenticated"}"
        return try {
            val uid = requireUserId()
            val docRef = db.collection("users").document(uid)
            docPath = docRef.path
            val clean = updates.filterValues { it != null }.toMutableMap()
            clean["updatedAt"] = FieldValue.serverTimestamp()
            docRef.update(clean).await()
            Result.success(Unit)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.UPDATE, docPath)
            Result.failure(e)
        }
    }

    suspend fun toggleSavedMedicine(currentSaved: List<String>, medicineId: String): Result<Unit> {
        val next = if (medicineId in currentSaved) {
            currentSaved - medicineId
        } else {
            (currentSaved + medicineId).take(100)
        }
        return updateUserProfile(mapOf("savedMedicineIds" to next))
    }

    // --- Smart Medicine Reminders ---

    fun observeReminders(userId: String): Flow<List<MedicineReminder>> = flow {
        val path = "users/$userId/reminders"
        emitAll(
            db.collection("users").document(userId).collection("reminders")
                .snapshots()
                .map { snapshot ->
                    snapshot.toObjects(MedicineReminder::class.java, DocumentSnapshot.ServerTimestampBehavior.ESTIMATE)
                        .sortedByDescending { it.createdAt?.seconds ?: 0L }
                }
                .catch { error -> unwrapAndLogFlowError(error, OperationType.LIST, path, userId) }
        )
    }

    suspend fun addReminder(reminder: MedicineReminder): Result<Unit> {
        var docPath = "users/${auth.currentUser?.uid ?: "unauthenticated"}/reminders"
        return try {
            val uid = requireUserId()
            val docRef = db.collection("users").document(uid).collection("reminders").document()
            docPath = docRef.path
            val times = reminder.reminderTimes.filter { it.isNotBlank() }.ifEmpty { listOf("08:00") }.take(12)
            val payload = mapOf(
                "medicineName" to reminder.medicineName.trim().ifBlank { "Medicine" }.take(120),
                "strength" to reminder.strength.trim().take(60),
                "dosageForm" to reminder.dosageForm.trim().ifBlank { "Tablet" }.take(60),
                "prescribedDose" to reminder.prescribedDose.trim().ifBlank { "1 dose" }.take(80),
                "scheduleType" to reminder.scheduleType.trim().ifBlank { "Once daily" }.take(60),
                "reminderTimes" to times,
                "startDate" to reminder.startDate.trim().ifBlank { "2026-11-23" }.take(30),
                "endDate" to reminder.endDate.trim().take(30),
                "prescribingProfessional" to reminder.prescribingProfessional.trim().take(100),
                "instructions" to reminder.instructions.trim().take(300),
                "status" to reminder.status.takeIf { it in setOf("UPCOMING", "TAKEN", "SNOOZED", "SKIPPED", "MISSED") }.orEmpty().ifBlank { "UPCOMING" },
                "createdAt" to FieldValue.serverTimestamp(),
                "updatedAt" to FieldValue.serverTimestamp()
            )
            docRef.set(payload).await()
            Result.success(Unit)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.CREATE, docPath)
            Result.failure(e)
        }
    }

    suspend fun updateReminderStatus(reminderId: String, newStatus: String): Result<Unit> {
        var docPath = "users/${auth.currentUser?.uid ?: "unauthenticated"}/reminders/$reminderId"
        return try {
            val uid = requireUserId()
            require(reminderId.isNotBlank())
            val safeStatus = newStatus.takeIf { it in setOf("UPCOMING", "TAKEN", "SNOOZED", "SKIPPED", "MISSED") } ?: "UPCOMING"
            val docRef = db.collection("users").document(uid).collection("reminders").document(reminderId)
            docPath = docRef.path
            docRef.update(
                mapOf(
                    "status" to safeStatus,
                    "updatedAt" to FieldValue.serverTimestamp()
                )
            ).await()
            Result.success(Unit)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.UPDATE, docPath)
            Result.failure(e)
        }
    }

    suspend fun deleteReminder(reminderId: String): Result<Unit> {
        var docPath = "users/${auth.currentUser?.uid ?: "unauthenticated"}/reminders/$reminderId"
        return try {
            val uid = requireUserId()
            val docRef = db.collection("users").document(uid).collection("reminders").document(reminderId)
            docPath = docRef.path
            docRef.delete().await()
            Result.success(Unit)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.DELETE, docPath)
            Result.failure(e)
        }
    }

    // --- Digital Medicine Cabinet / Expiry Tracker ---

    fun observeCabinet(userId: String): Flow<List<CabinetMedicine>> = flow {
        val path = "users/$userId/cabinet"
        emitAll(
            db.collection("users").document(userId).collection("cabinet")
                .snapshots()
                .map { snapshot ->
                    snapshot.toObjects(CabinetMedicine::class.java, DocumentSnapshot.ServerTimestampBehavior.ESTIMATE)
                        .sortedBy { it.expiryDate }
                }
                .catch { error -> unwrapAndLogFlowError(error, OperationType.LIST, path, userId) }
        )
    }

    suspend fun addCabinetMedicine(item: CabinetMedicine): Result<Unit> {
        var docPath = "users/${auth.currentUser?.uid ?: "unauthenticated"}/cabinet"
        return try {
            val uid = requireUserId()
            val docRef = db.collection("users").document(uid).collection("cabinet").document()
            docPath = docRef.path
            val payload = mapOf(
                "medicineName" to item.medicineName.trim().ifBlank { "Medicine" }.take(120),
                "brand" to item.brand.trim().take(100),
                "batchNumber" to item.batchNumber.trim().take(60),
                "expiryDate" to item.expiryDate.trim().ifBlank { "2027-06-30" }.take(30),
                "quantity" to item.quantity.coerceIn(0L, 10000L),
                "strength" to item.strength.trim().take(60),
                "storageLocation" to item.storageLocation.trim().take(100),
                "alertDaysBefore" to item.alertDaysBefore.coerceIn(1L, 180L),
                "createdAt" to FieldValue.serverTimestamp(),
                "updatedAt" to FieldValue.serverTimestamp()
            )
            docRef.set(payload).await()
            Result.success(Unit)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.CREATE, docPath)
            Result.failure(e)
        }
    }

    suspend fun deleteCabinetMedicine(itemId: String): Result<Unit> {
        var docPath = "users/${auth.currentUser?.uid ?: "unauthenticated"}/cabinet/$itemId"
        return try {
            val uid = requireUserId()
            val docRef = db.collection("users").document(uid).collection("cabinet").document(itemId)
            docPath = docRef.path
            docRef.delete().await()
            Result.success(Unit)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.DELETE, docPath)
            Result.failure(e)
        }
    }

    // --- BMI History ---

    fun observeBmiHistory(userId: String): Flow<List<BmiEntry>> = flow {
        val path = "users/$userId/bmi_history"
        emitAll(
            db.collection("users").document(userId).collection("bmi_history")
                .snapshots()
                .map { snapshot ->
                    snapshot.toObjects(BmiEntry::class.java, DocumentSnapshot.ServerTimestampBehavior.ESTIMATE)
                        .sortedBy { it.createdAt?.seconds ?: 0L }
                }
                .catch { error -> unwrapAndLogFlowError(error, OperationType.LIST, path, userId) }
        )
    }

    suspend fun addBmiEntry(entry: BmiEntry): Result<Unit> {
        var docPath = "users/${auth.currentUser?.uid ?: "unauthenticated"}/bmi_history"
        return try {
            val uid = requireUserId()
            val docRef = db.collection("users").document(uid).collection("bmi_history").document()
            docPath = docRef.path
            val payload = mapOf(
                "bmiValue" to entry.bmiValue.coerceIn(5.0, 120.0),
                "category" to entry.category.trim().ifBlank { "Normal" }.take(50),
                "heightCm" to entry.heightCm.coerceIn(30.0, 300.0),
                "weightKg" to entry.weightKg.coerceIn(2.0, 500.0),
                "recordedDate" to entry.recordedDate.trim().ifBlank { "2026-11-23" }.take(30),
                "createdAt" to FieldValue.serverTimestamp()
            )
            docRef.set(payload).await()
            Result.success(Unit)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.CREATE, docPath)
            Result.failure(e)
        }
    }

    // --- Saved Reports ---

    fun observeReports(userId: String): Flow<List<SavedReport>> = flow {
        val path = "users/$userId/reports"
        emitAll(
            db.collection("users").document(userId).collection("reports")
                .snapshots()
                .map { snapshot ->
                    snapshot.toObjects(SavedReport::class.java, DocumentSnapshot.ServerTimestampBehavior.ESTIMATE)
                        .sortedByDescending { it.createdAt?.seconds ?: 0L }
                }
                .catch { error -> unwrapAndLogFlowError(error, OperationType.LIST, path, userId) }
        )
    }

    suspend fun saveReport(reportType: String, title: String, summary: String, severity: String): Result<Unit> {
        var docPath = "users/${auth.currentUser?.uid ?: "unauthenticated"}/reports"
        return try {
            val uid = requireUserId()
            val docRef = db.collection("users").document(uid).collection("reports").document()
            docPath = docRef.path
            val safeType = reportType.takeIf { it in setOf("INTERACTION", "LABGUARD", "SCANNER", "PREGNANCY") } ?: "INTERACTION"
            val payload = mapOf(
                "reportType" to safeType,
                "title" to title.trim().ifBlank { "DRxGuard Report" }.take(150),
                "summary" to summary.trim().ifBlank { "Saved summary" }.take(2000),
                "severity" to severity.trim().ifBlank { "INFO" }.take(40),
                "createdAt" to FieldValue.serverTimestamp()
            )
            docRef.set(payload).await()
            Result.success(Unit)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.CREATE, docPath)
            Result.failure(e)
        }
    }

    suspend fun deleteReport(reportId: String): Result<Unit> {
        var docPath = "users/${auth.currentUser?.uid ?: "unauthenticated"}/reports/$reportId"
        return try {
            val uid = requireUserId()
            val docRef = db.collection("users").document(uid).collection("reports").document(reportId)
            docPath = docRef.path
            docRef.delete().await()
            Result.success(Unit)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.DELETE, docPath)
            Result.failure(e)
        }
    }

    // --- Universal Notifications ---

    fun observeNotifications(userId: String): Flow<List<AppNotification>> = flow {
        val path = "users/$userId/notifications"
        emitAll(
            db.collection("users").document(userId).collection("notifications")
                .snapshots()
                .map { snapshot ->
                    snapshot.toObjects(AppNotification::class.java, DocumentSnapshot.ServerTimestampBehavior.ESTIMATE)
                        .sortedByDescending { it.createdAt?.seconds ?: 0L }
                }
                .catch { error -> unwrapAndLogFlowError(error, OperationType.LIST, path, userId) }
        )
    }

    suspend fun addNotification(category: String, title: String, message: String): Result<Unit> {
        var docPath = "users/${auth.currentUser?.uid ?: "unauthenticated"}/notifications"
        return try {
            val uid = requireUserId()
            val docRef = db.collection("users").document(uid).collection("notifications").document()
            docPath = docRef.path
            val payload = mapOf(
                "category" to category.trim().ifBlank { "Medicine reminders" }.take(60),
                "title" to title.trim().ifBlank { "DRxGuard Alert" }.take(120),
                "message" to message.trim().ifBlank { "Health update available." }.take(500),
                "read" to false,
                "createdAt" to FieldValue.serverTimestamp()
            )
            docRef.set(payload).await()
            Result.success(Unit)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.CREATE, docPath)
            Result.failure(e)
        }
    }

    suspend fun markNotificationRead(notificationId: String): Result<Unit> {
        var docPath = "users/${auth.currentUser?.uid ?: "unauthenticated"}/notifications/$notificationId"
        return try {
            val uid = requireUserId()
            val docRef = db.collection("users").document(uid).collection("notifications").document(notificationId)
            docPath = docRef.path
            docRef.update(mapOf("read" to true)).await()
            Result.success(Unit)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.UPDATE, docPath)
            Result.failure(e)
        }
    }

    // --- Admin Medicines Catalog ---

    fun observeAdminMedicines(userId: String): Flow<List<AdminMedicine>> = flow {
        val path = "admin_medicines"
        emitAll(
            db.collection("admin_medicines")
                .whereEqualTo("published", true)
                .snapshots()
                .map { snapshot ->
                    snapshot.toObjects(AdminMedicine::class.java, DocumentSnapshot.ServerTimestampBehavior.ESTIMATE)
                        .sortedBy { it.genericName }
                }
                .catch { error -> unwrapAndLogFlowError(error, OperationType.LIST, path, userId) }
        )
    }

    suspend fun addAdminMedicine(med: AdminMedicine): Result<Unit> {
        var docPath = "admin_medicines"
        return try {
            val uid = requireUserId()
            val docRef = db.collection("admin_medicines").document()
            docPath = docRef.path
            val payload = mapOf(
                "genericName" to med.genericName.trim().ifBlank { "Unnamed Compound" }.take(120),
                "brandNames" to med.brandNames.filter { it.isNotBlank() }.take(20),
                "drugClass" to med.drugClass.trim().ifBlank { "General" }.take(80),
                "strengths" to med.strengths.filter { it.isNotBlank() }.take(20),
                "dosageForms" to med.dosageForms.filter { it.isNotBlank() }.take(15),
                "composition" to med.composition.trim().ifBlank { med.genericName }.take(200),
                "therapeuticUses" to med.therapeuticUses.trim().ifBlank { "Educational pharmaceutical reference." }.take(500),
                "mechanismOfAction" to med.mechanismOfAction.trim().ifBlank { "Refer to pharmacology monograph." }.take(600),
                "commonSideEffects" to med.commonSideEffects.trim().ifBlank { "Consult prescribing monograph." }.take(500),
                "precautions" to med.precautions.trim().ifBlank { "Verify with licensed pharmacist." }.take(500),
                "published" to true,
                "updatedBy" to uid,
                "createdAt" to FieldValue.serverTimestamp(),
                "updatedAt" to FieldValue.serverTimestamp()
            )
            docRef.set(payload).await()
            Result.success(Unit)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.CREATE, docPath)
            Result.failure(e)
        }
    }

    suspend fun deleteUserAccountData(): Result<Unit> {
        var docPath = "users/${auth.currentUser?.uid ?: "unauthenticated"}"
        return try {
            val uid = requireUserId()
            val userRef = db.collection("users").document(uid)
            docPath = userRef.path
            listOf("reminders", "cabinet", "bmi_history", "reports", "notifications").forEach { sub ->
                val docs = userRef.collection(sub).get().await()
                docs.documents.forEach { it.reference.delete().await() }
            }
            userRef.delete().await()
            Result.success(Unit)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.DELETE, docPath)
            Result.failure(e)
        }
    }
}
