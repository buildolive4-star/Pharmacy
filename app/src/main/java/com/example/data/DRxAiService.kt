package com.example.data

import android.graphics.Bitmap
import android.util.Log
import com.google.firebase.Firebase
import com.google.firebase.ai.ai
import com.google.firebase.ai.type.Schema
import com.google.firebase.ai.type.content
import com.google.firebase.ai.type.generationConfig
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withTimeout
import org.json.JSONObject

private const val AI_TAG = "DRxAiService"

data class PillMatchCandidate(
    val possibleName: String,
    val genericIngredient: String,
    val strength: String,
    val shape: String,
    val color: String,
    val visibleImprint: String,
    val sizeEstimate: String,
    val confidenceLevel: String,
    val supportingDetails: String
)

data class PillScanAnalysis(
    val identified: Boolean,
    val observedShape: String,
    val observedColor: String,
    val observedImprint: String,
    val candidates: List<PillMatchCandidate>,
    val safetyNote: String
)

data class LabAiInterpretation(
    val overallSummary: String,
    val parameterExplanations: List<Pair<String, String>>,
    val urgentFlags: List<String>,
    val followUpGuidance: String
)

class DRxAiService {

    private val pharmacistModel by lazy {
        Firebase.ai.generativeModel(
            modelName = "gemini-3.5-flash-lite",
            generationConfig = generationConfig {
                temperature = 0.35f
                maxOutputTokens = 750
            },
            systemInstruction = content {
                text(
                    """
                    You are DRxGuard AI, a fast, accurate, friendly pharmaceutical education assistant.
                    SAFETY RULES (MANDATORY):
                    - Clearly distinguish general educational information from personalized medical advice.
                    - NEVER prescribe medications, calculate patient-specific doses, or advise stopping prescribed therapy.
                    - NEVER fabricate drug interactions, pill identities, or compositions. If uncertain, state uncertainty clearly.
                    RESPONSE STYLE:
                    - Respond strictly in the user's selected language (if Hinglish, write natural conversational Hindi in Latin/Roman script).
                    - For casual greetings or small talk (e.g., "hi", "hello", "thanks", "kaise ho"), reply warmly in 1-2 short sentences and ask what medicine or health topic they want to check. Do NOT use the 6-section template for greetings.
                    - For actual medicine, symptom, lab, or pharmacology questions, be crisp, concise (under 180 words total), and use these 6 short headings:
                      ### 1. Direct Answer
                      ### 2. Scientific Explanation
                      ### 3. Important Precautions
                      ### 4. Relevant Warnings
                      ### 5. Reliable Sources
                      ### 6. When to Consult a Professional
                    """.trimIndent()
                )
            }
        )
    }

    private val pillScannerSchema = Schema.obj(
        properties = mapOf(
            "identified" to Schema.boolean(description = "True only if visual features match known pharmaceutical imprints with reasonable visual cues; false if blurry, ambiguous, or uncertain"),
            "observedShape" to Schema.string(description = "Observed tablet or capsule shape (e.g., Round, Oval, Oblong, Capsule, Unknown)"),
            "observedColor" to Schema.string(description = "Observed surface color(s)"),
            "observedImprint" to Schema.string(description = "Any legible debossed/printed imprint or 'None clearly visible'"),
            "safetyNote" to Schema.string(description = "Mandatory verification warning stating image alone cannot definitively identify a pill"),
            "candidates" to Schema.array(
                items = Schema.obj(
                    properties = mapOf(
                        "possibleName" to Schema.string(),
                        "genericIngredient" to Schema.string(),
                        "strength" to Schema.string(),
                        "shape" to Schema.string(),
                        "color" to Schema.string(),
                        "visibleImprint" to Schema.string(),
                        "sizeEstimate" to Schema.string(),
                        "confidenceLevel" to Schema.enumeration(listOf("Low (Needs Verification)", "Moderate (Needs Verification)", "Uncertain")),
                        "supportingDetails" to Schema.string()
                    )
                ),
                description = "Up to 3 possible matches for pharmacist comparison; empty if unidentified"
            )
        )
    )

    private val pillVisionModel by lazy {
        Firebase.ai.generativeModel(
            modelName = "gemini-3.8-flash",
            generationConfig = generationConfig {
                responseMimeType = "application/json"
                responseSchema = pillScannerSchema
            }
        )
    }

    private val labGuardSchema = Schema.obj(
        properties = mapOf(
            "overallSummary" to Schema.string(description = "Clear plain-language educational summary of the lab values without diagnosing any disease"),
            "urgentFlags" to Schema.array(
                items = Schema.string(),
                description = "Any critically out-of-range markers requiring prompt clinician review; empty if none"
            ),
            "parameterExplanations" to Schema.array(
                items = Schema.obj(
                    properties = mapOf(
                        "parameter" to Schema.string(),
                        "explanation" to Schema.string()
                    )
                ),
                description = "Simple explanation and possible educational clinical significance per parameter"
            ),
            "followUpGuidance" to Schema.string(description = "Guidance emphasizing lab reference ranges and consulting the ordering clinician")
        )
    )

    private val labGuardModel by lazy {
        Firebase.ai.generativeModel(
            modelName = "gemini-3.8-flash",
            generationConfig = generationConfig {
                responseMimeType = "application/json"
                responseSchema = labGuardSchema
            }
        )
    }

    fun streamPharmacistResponse(
        question: String,
        language: String,
        attachedImage: Bitmap? = null,
        recentTurns: List<Pair<Boolean, String>> = emptyList()
    ): Flow<String> = flow {
        val accumulated = StringBuilder()
        val historyPrefix = if (recentTurns.isNotEmpty()) {
            buildString {
                append("Recent conversation context:\n")
                recentTurns.takeLast(4).forEach { (fromUser, msg) ->
                    val role = if (fromUser) "User" else "Assistant"
                    append("$role: ${msg.take(260)}\n")
                }
                append("\n")
            }
        } else {
            ""
        }
        val promptText = "${historyPrefix}Response language: $language.\nUser message: $question"
        withTimeout(60_000L) {
            val stream = if (attachedImage != null) {
                pharmacistModel.generateContentStream(
                    content {
                        image(attachedImage)
                        text(promptText)
                    }
                )
            } else {
                pharmacistModel.generateContentStream(promptText)
            }
            stream.collect { chunk ->
                val text = chunk.text.orEmpty()
                if (text.isNotEmpty()) {
                    accumulated.append(text)
                    emit(accumulated.toString())
                }
            }
        }
        if (accumulated.isBlank()) {
            throw IllegalStateException("DRxGuard AI returned an empty response. Please try again.")
        }
    }

    suspend fun analyzePillImage(bitmap: Bitmap?, manualHint: String = ""): PillScanAnalysis {
        try {
            val prompt = buildString {
                append("Inspect this pill/tablet/capsule photo for educational visual feature extraction. ")
                append("NEVER claim definitive identification from an image alone. ")
                append("If no pill is visible or imprint/features are unclear, set identified=false and return an empty candidates array. ")
                if (manualHint.isNotBlank()) {
                    append("User visual notes: $manualHint. ")
                }
                append("Always require verification via original pharmacy packaging, imprint lookup, and a licensed pharmacist.")
            }
            val response = withTimeout(60_000L) {
                if (bitmap != null) {
                    pillVisionModel.generateContent(
                        content {
                            image(bitmap)
                            text(prompt)
                        }
                    )
                } else {
                    pillVisionModel.generateContent(prompt)
                }
            }
            val jsonStr = response.text ?: throw IllegalStateException("Empty response from Pill Scanner AI.")
            val json = JSONObject(jsonStr)
            val candidatesArr = json.optJSONArray("candidates")
            val candidates = mutableListOf<PillMatchCandidate>()
            if (candidatesArr != null) {
                for (i in 0 until candidatesArr.length()) {
                    val obj = candidatesArr.getJSONObject(i)
                    candidates.add(
                        PillMatchCandidate(
                            possibleName = obj.optString("possibleName", "Unverified Candidate"),
                            genericIngredient = obj.optString("genericIngredient", "Unknown"),
                            strength = obj.optString("strength", "Verify on packaging"),
                            shape = obj.optString("shape", "Unknown"),
                            color = obj.optString("color", "Unknown"),
                            visibleImprint = obj.optString("visibleImprint", "Not clearly visible"),
                            sizeEstimate = obj.optString("sizeEstimate", "Not measurable from photo"),
                            confidenceLevel = obj.optString("confidenceLevel", "Uncertain"),
                            supportingDetails = obj.optString("supportingDetails", "")
                        )
                    )
                }
            }
            return PillScanAnalysis(
                identified = json.optBoolean("identified", false) && candidates.isNotEmpty(),
                observedShape = json.optString("observedShape", "Unverified"),
                observedColor = json.optString("observedColor", "Unverified"),
                observedImprint = json.optString("observedImprint", "None clearly visible"),
                candidates = candidates,
                safetyNote = json.optString(
                    "safetyNote",
                    "Visual recognition cannot definitively identify a medication. Always verify using original packaging, imprint records, and a registered pharmacist."
                )
            )
        } catch (e: TimeoutCancellationException) {
            Log.e(AI_TAG, "Pill analysis timed out", e)
            throw IllegalStateException("Pill analysis timed out after 60 seconds. Please try again.")
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Log.e(AI_TAG, "Pill analysis failed", e)
            throw e
        }
    }

    suspend fun analyzeLabReport(
        summaryText: String,
        attachedImage: Bitmap? = null
    ): LabAiInterpretation {
        try {
            val prompt = """
                Provide an educational, plain-language explanation of the following laboratory test data.
                RULES:
                - Prioritize the reference ranges printed by the reporting laboratory.
                - Do NOT diagnose any disease from lab values alone.
                - Clearly highlight any potentially urgent or significantly abnormal markers and advise timely medical evaluation.
                Lab data:
                $summaryText
            """.trimIndent()

            val response = withTimeout(60_000L) {
                if (attachedImage != null) {
                    labGuardModel.generateContent(
                        content {
                            image(attachedImage)
                            text(prompt)
                        }
                    )
                } else {
                    labGuardModel.generateContent(prompt)
                }
            }
            val jsonStr = response.text ?: throw IllegalStateException("Empty response from LabGuard AI.")
            val json = JSONObject(jsonStr)
            val flags = mutableListOf<String>()
            val flagsArr = json.optJSONArray("urgentFlags")
            if (flagsArr != null) {
                for (i in 0 until flagsArr.length()) {
                    val f = flagsArr.optString(i)
                    if (f.isNotBlank()) flags.add(f)
                }
            }
            val explanations = mutableListOf<Pair<String, String>>()
            val expArr = json.optJSONArray("parameterExplanations")
            if (expArr != null) {
                for (i in 0 until expArr.length()) {
                    val obj = expArr.getJSONObject(i)
                    explanations.add(
                        obj.optString("parameter", "Marker") to obj.optString("explanation", "")
                    )
                }
            }
            return LabAiInterpretation(
                overallSummary = json.optString("overallSummary", ""),
                parameterExplanations = explanations,
                urgentFlags = flags,
                followUpGuidance = json.optString(
                    "followUpGuidance",
                    "Always review laboratory results with the ordering healthcare provider using your laboratory's printed reference ranges."
                )
            )
        } catch (e: TimeoutCancellationException) {
            Log.e(AI_TAG, "LabGuard AI timed out", e)
            throw IllegalStateException("LabGuard analysis timed out after 60 seconds. Please try again.")
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Log.e(AI_TAG, "LabGuard AI failed", e)
            throw e
        }
    }

    suspend fun analyzePregnancySymptoms(symptoms: String, week: Int): String {
        try {
            val prompt = """
                A pregnant user at approximately week $week reports these symptoms: "$symptoms".
                Provide cautious, supportive educational information structured into:
                1. Reported Symptoms Overview
                2. Possible General Reasons (presented cautiously, non-diagnostic)
                3. Red-Flag Warning Signs Requiring Urgent Care
                4. When to Contact Your Obstetrician or Midwife
                Do NOT diagnose pregnancy complications or prescribe medications.
            """.trimIndent()
            val response = withTimeout(60_000L) {
                Firebase.ai.generativeModel("gemini-3.8-flash").generateContent(prompt)
            }
            return response.text?.takeIf { it.isNotBlank() }
                ?: throw IllegalStateException("No educational guidance returned.")
        } catch (e: TimeoutCancellationException) {
            throw IllegalStateException("Symptom guidance timed out. Please try again.")
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Log.e(AI_TAG, "Pregnancy symptom analysis failed", e)
            throw e
        }
    }
}
