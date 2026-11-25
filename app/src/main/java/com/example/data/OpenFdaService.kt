package com.example.data

import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.ResponseBody
import org.json.JSONArray
import org.json.JSONObject
import retrofit2.Retrofit
import retrofit2.http.GET
import retrofit2.http.Query
import java.util.concurrent.TimeUnit

private const val FDA_TAG = "OpenFdaService"

interface OpenFdaApi {
    @GET("drug/label.json")
    suspend fun searchLabels(
        @Query("search") searchExpr: String,
        @Query("limit") limit: Int = 6
    ): ResponseBody
}

object OpenFdaService {

    private val okHttpClient by lazy {
        OkHttpClient.Builder()
            .connectTimeout(10, TimeUnit.SECONDS)
            .readTimeout(12, TimeUnit.SECONDS)
            .build()
    }

    private val api by lazy {
        Retrofit.Builder()
            .baseUrl("https://api.fda.gov/")
            .client(okHttpClient)
            .build()
            .create(OpenFdaApi::class.java)
    }

    suspend fun searchMedicines(query: String): List<MedicineMonograph> = withContext(Dispatchers.IO) {
        val sanitized = query.trim()
            .replace(Regex("[^a-zA-Z0-9\\s-]"), " ")
            .trim()
        if (sanitized.length < 2) return@withContext emptyList()

        try {
            val encodedTerm = "\"$sanitized\""
            val expr = "openfda.brand_name:$encodedTerm+openfda.generic_name:$encodedTerm"
            val rawJson = api.searchLabels(searchExpr = expr, limit = 6).use { it.string() }
            val root = JSONObject(rawJson)
            val results = root.optJSONArray("results") ?: return@withContext emptyList()
            val mapped = mutableListOf<MedicineMonograph>()

            for (i in 0 until results.length()) {
                val item = results.optJSONObject(i) ?: continue
                mapOpenFdaItem(item, i)?.let { mapped.add(it) }
            }
            mapped.distinctBy { it.genericName.lowercase() }
        } catch (e: Exception) {
            Log.w(FDA_TAG, "OpenFDA lookup skipped/empty for '$query': ${e.message}")
            emptyList()
        }
    }

    suspend fun fetchFeaturedLabels(limit: Int = 20): List<MedicineMonograph> = withContext(Dispatchers.IO) {
        try {
            val rawJson = api.searchLabels(
                searchExpr = "_exists_:openfda.generic_name+AND+_exists_:openfda.brand_name",
                limit = limit.coerceIn(1, 30)
            ).use { it.string() }
            val root = JSONObject(rawJson)
            val results = root.optJSONArray("results") ?: return@withContext emptyList()
            val mapped = mutableListOf<MedicineMonograph>()
            for (i in 0 until results.length()) {
                val item = results.optJSONObject(i) ?: continue
                mapOpenFdaItem(item, i)?.let { mapped.add(it) }
            }
            mapped.distinctBy { it.genericName.lowercase() }
        } catch (e: Exception) {
            Log.w(FDA_TAG, "OpenFDA initial batch skipped: ${e.message}")
            emptyList()
        }
    }

    private fun mapOpenFdaItem(item: JSONObject, index: Int): MedicineMonograph? {
        val openFda = item.optJSONObject("openfda") ?: JSONObject()
        val genericNames = jsonStringList(openFda.optJSONArray("generic_name"))
        val brandNames = jsonStringList(openFda.optJSONArray("brand_name"))
        val substances = jsonStringList(openFda.optJSONArray("substance_name"))
        val pharmClasses = jsonStringList(openFda.optJSONArray("pharm_class_epc"))
        val routes = jsonStringList(openFda.optJSONArray("route"))
        val dosageForms = jsonStringList(openFda.optJSONArray("dosage_form"))
        val manufacturers = jsonStringList(openFda.optJSONArray("manufacturer_name"))

        val primaryGeneric = genericNames.firstOrNull()
            ?: substances.firstOrNull()
            ?: brandNames.firstOrNull()
            ?: return null

        val setId = item.optString("set_id").ifBlank { "${primaryGeneric.hashCode()}_$index" }
        val cleanId = "fda_" + setId.replace(Regex("[^a-zA-Z0-9_]"), "").take(28)

        val indications = firstParagraph(item.optJSONArray("indications_and_usage"))
        val moaText = firstParagraph(item.optJSONArray("clinical_pharmacology"))
            .ifBlank { firstParagraph(item.optJSONArray("mechanism_of_action")) }
        val warningsText = firstParagraph(item.optJSONArray("warnings_and_cautions"))
            .ifBlank { firstParagraph(item.optJSONArray("warnings")) }
        val adverseText = firstParagraph(item.optJSONArray("adverse_reactions"))
        val interactionsText = firstParagraph(item.optJSONArray("drug_interactions"))
        val contraText = firstParagraph(item.optJSONArray("contraindications"))
        val storageText = firstParagraph(item.optJSONArray("storage_and_handling"))

        val drugClass = pharmClasses.firstOrNull()?.replace("[EPC]", "")?.trim()
            ?: "FDA Labeled Medication"

        val commonEffects = if (adverseText.isNotBlank()) {
            listOf(
                SideEffectDetail(
                    name = "Reported Adverse Reactions",
                    description = adverseText.take(360),
                    frequency = "Per FDA Prescribing Label",
                    severity = "Moderate",
                    warning = "Monitor symptoms and consult your pharmacist or doctor if persistent.",
                    source = "U.S. FDA DailyMed / OpenFDA"
                )
            )
        } else {
            emptyList()
        }

        val seriousEffects = if (warningsText.isNotBlank()) {
            listOf(
                SideEffectDetail(
                    name = "Warnings & Precautions",
                    description = warningsText.take(360),
                    frequency = "Label Warning",
                    severity = "High",
                    warning = "Seek prompt medical advice for severe allergic reactions or unexpected symptoms.",
                    source = "U.S. FDA DailyMed / OpenFDA"
                )
            )
        } else {
            emptyList()
        }

        return MedicineMonograph(
            id = cleanId,
            genericName = primaryGeneric.lowercase().replaceFirstChar { it.uppercase() },
            brandNames = brandNames.take(5).ifEmpty { listOf(primaryGeneric) },
            activeIngredients = substances.take(4).ifEmpty { listOf(primaryGeneric) },
            saltComposition = substances.take(4).joinToString(" + ").ifBlank { primaryGeneric },
            category = drugClass.take(40),
            drugClass = drugClass,
            subclass = "OpenFDA Official Monograph",
            therapeuticUses = if (indications.isNotBlank()) listOf(indications.take(320)) else listOf("Refer to official FDA label indications."),
            strengths = listOf("See product packaging"),
            dosageForms = dosageForms.take(4).ifEmpty { listOf("Oral / Label-Directed") },
            route = routes.take(3).joinToString(", ").ifBlank { "Per label" },
            simpleMoa = moaText.take(320).ifBlank { "Refer to official clinical pharmacology section on label." },
            advancedMoa = moaText.take(500).ifBlank { "Full pharmacology available in DailyMed label." },
            molecularTarget = "FDA-documented pharmacological target",
            receptorInteraction = "Per FDA label",
            enzymeInvolvement = "Per FDA label",
            biologicalPathway = "Systemic pathway",
            physiologicalEffects = indications.take(240).ifBlank { "See therapeutic indications." },
            commonSideEffects = commonEffects,
            seriousSideEffects = seriousEffects,
            rareSideEffects = emptyList(),
            contraindications = if (contraText.isNotBlank()) listOf(contraText.take(320)) else listOf("Known hypersensitivity to active ingredients."),
            precautions = if (warningsText.isNotBlank()) listOf(warningsText.take(320)) else listOf("Verify dose and precautions with a licensed pharmacist."),
            keyInteractions = if (interactionsText.isNotBlank()) listOf(interactionsText.take(340)) else listOf("Check Drug Interaction Checker for multi-drug regimens."),
            storageInfo = storageText.take(200).ifBlank { "Store at controlled room temperature away from moisture." },
            manufacturerInfo = manufacturers.take(2).joinToString(", ").ifBlank { "FDA Registered Labeler" },
            referenceSources = "U.S. FDA OpenFDA Drug Label API (api.fda.gov)",
            lastUpdated = item.optString("effective_time").takeIf { it.length >= 8 }?.let {
                "${it.substring(0, 4)}-${it.substring(4, 6)}-${it.substring(6, 8)}"
            } ?: "OpenFDA Live",
            halfLifeHours = "Per clinical pharmacology",
            bioavailability = "Per clinical pharmacology",
            metabolism = "Per clinical pharmacology"
        )
    }

    private fun jsonStringList(arr: JSONArray?): List<String> {
        if (arr == null) return emptyList()
        val list = mutableListOf<String>()
        for (i in 0 until arr.length()) {
            val s = arr.optString(i).trim()
            if (s.isNotBlank()) list.add(s)
        }
        return list
    }

    private fun firstParagraph(arr: JSONArray?): String {
        val raw = arr?.optString(0)?.trim().orEmpty()
        return raw.replace(Regex("\\s+"), " ").trim()
    }
}
