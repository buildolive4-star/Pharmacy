package com.example.data

enum class InteractionSeverity(val label: String) {
    HIGH("High Severity"),
    MODERATE("Moderate Severity"),
    LOW("Low Severity"),
    NONE("No Known Interaction Found")
}

data class SideEffectDetail(
    val name: String,
    val description: String,
    val frequency: String,
    val severity: String,
    val warning: String,
    val source: String
)

data class MedicineMonograph(
    val id: String,
    val genericName: String,
    val brandNames: List<String>,
    val activeIngredients: List<String>,
    val saltComposition: String,
    val category: String,
    val drugClass: String,
    val subclass: String,
    val therapeuticUses: List<String>,
    val strengths: List<String>,
    val dosageForms: List<String>,
    val route: String,
    val simpleMoa: String,
    val advancedMoa: String,
    val molecularTarget: String,
    val receptorInteraction: String,
    val enzymeInvolvement: String,
    val biologicalPathway: String,
    val physiologicalEffects: String,
    val commonSideEffects: List<SideEffectDetail>,
    val seriousSideEffects: List<SideEffectDetail>,
    val rareSideEffects: List<SideEffectDetail>,
    val contraindications: List<String>,
    val precautions: List<String>,
    val keyInteractions: List<String>,
    val storageInfo: String,
    val manufacturerInfo: String,
    val referenceSources: String,
    val lastUpdated: String,
    val halfLifeHours: String,
    val bioavailability: String,
    val metabolism: String
)

data class BrandAlternative(
    val brandName: String,
    val genericId: String,
    val genericComposition: String,
    val strength: String,
    val dosageForm: String,
    val manufacturer: String,
    val exactMatch: Boolean,
    val availableInfo: String,
    val relevantDifferences: String
)

data class DrugInteractionRecord(
    val drugAId: String,
    val drugBId: String,
    val severity: InteractionSeverity,
    val description: String,
    val clinicalEffects: String,
    val supportingEvidence: String,
    val precautions: String,
    val source: String,
    val lastUpdated: String
)

data class HealthConditionGuide(
    val id: String,
    val name: String,
    val category: String,
    val definition: String,
    val types: List<String>,
    val symptoms: List<String>,
    val causes: List<String>,
    val riskFactors: List<String>,
    val complications: List<String>,
    val diagnostics: List<String>,
    val treatmentInfo: String,
    val prevention: List<String>,
    val seekCareWhen: List<String>
)

data class DrugClassNode(
    val name: String,
    val definition: String,
    val pharmacologicalAction: String,
    val therapeuticApplications: List<String>,
    val mechanismSummary: String,
    val commonAdverseEffects: List<String>,
    val precautions: List<String>,
    val subclasses: Map<String, List<String>> // Subclass -> List of medicine IDs
)

data class PregnancyWeekMilestone(
    val week: Int,
    val trimester: Int,
    val sizeComparison: String,
    val lengthCm: String,
    val developmentSummary: String,
    val milestones: List<String>,
    val maternalWellness: String,
    val appointmentReminder: String
)

data class LabTestParameter(
    val id: String,
    val category: String,
    val name: String,
    val unit: String,
    val refLow: Double,
    val refHigh: Double,
    val criticalLow: Double?,
    val criticalHigh: Double?,
    val defaultSampleValue: Double,
    val historicalTrend: List<Pair<String, Double>>,
    val simpleExplanation: String,
    val clinicalSignificance: String
)

data class PharmaArticle(
    val id: String,
    val title: String,
    val category: String,
    val readMinutes: Int,
    val summary: String,
    val source: String
)

object PharmaCatalog {

    val supportedLanguages = listOf(
        "English", "Hindi", "Hinglish", "Kannada", "Telugu",
        "Tamil", "Bengali", "Marathi", "Gujarati"
    )

    val medicineCategories = listOf(
        "All", "Antibiotics", "Analgesics", "Antipyretics", "Antacids",
        "Gastrointestinal", "Respiratory", "Antihistamines", "Antidiabetics",
        "Antihypertensives", "Endocrine", "Neurology", "Antidepressants",
        "Vitamins", "Antifungals", "Anticoagulants", "Topicals"
    )

    val medicines: List<MedicineMonograph> = listOf(
        MedicineMonograph(
            id = "med_paracetamol",
            genericName = "Paracetamol (Acetaminophen)",
            brandNames = listOf("Dolo 650", "Crocin Advance", "Tylenol", "Calpol"),
            activeIngredients = listOf("Paracetamol (Acetaminophen)"),
            saltComposition = "Paracetamol IP/USP",
            category = "Antipyretics",
            drugClass = "Analgesic & Antipyretic",
            subclass = "Para-aminophenol Derivatives",
            therapeuticUses = listOf("Mild-to-moderate pain relief", "Fever reduction", "Headache and musculoskeletal discomfort"),
            strengths = listOf("325 mg", "500 mg", "650 mg", "1000 mg"),
            dosageForms = listOf("Tablet", "Oral Suspension", "Effervescent Tablet", "Suppository"),
            route = "Oral / Rectal",
            simpleMoa = "Calms pain and fever chemical messengers inside the brain and spinal cord without irritating the stomach lining like NSAIDs.",
            advancedMoa = "Inhibits central prostaglandin synthesis via peroxidase-site inhibition of COX-1/COX-2 isoforms (especially COX-2 in low-peroxide CNS environments) and activates descending serotonergic inhibitory pathways via AM404 metabolite interaction with TRPV1/CB1 receptors.",
            molecularTarget = "Central Cyclooxygenase (COX peroxidase site) & TRPV1/CB1 (via AM404)",
            receptorInteraction = "Indirect activation of hypothalamic & spinal cannabinoid (CB1) and TRPV1 channels",
            enzymeInvolvement = "Central COX-2 peroxidase site; metabolized by UGT1A1, SULT, and CYP2E1 (NAPQI pathway)",
            biologicalPathway = "Hypothalamic thermoregulatory set-point modulation & central nociceptive suppression",
            physiologicalEffects = "Antipyresis via cutaneous vasodilation/sweating and central analgesia with minimal peripheral antiplatelet effect.",
            commonSideEffects = listOf(
                SideEffectDetail("Mild nausea", "Occasional gentle stomach upset when taken on an empty stomach.", "Uncommon (1-3%)", "Low", "Take with water; avoid exceeding daily total across combination products.", "FDA DailyMed / BNF 2026")
            ),
            seriousSideEffects = listOf(
                SideEffectDetail("Hepatotoxicity (Liver injury)", "Severe liver toxicity caused by NAPQI accumulation upon overdose or chronic high alcohol intake.", "Rare at therapeutic doses; High in overdose", "High", "Never exceed prescribed maximum daily dose; watch for yellowing skin/eyes or upper right abdominal pain.", "FDA Boxed Warning / WHO 2026"),
                SideEffectDetail("Severe cutaneous reactions (SJS/TEN)", "Widespread blistering rash or skin peeling.", "Very Rare (<0.01%)", "High", "Stop immediately and seek emergency medical care at the first sign of severe rash.", "FDA Safety Communication")
            ),
            rareSideEffects = listOf(
                SideEffectDetail("Thrombocytopenia", "Transient reduction in blood platelet count.", "Very Rare (<0.01%)", "Moderate", "Report unexplained bruising or bleeding.", "BNF Reference 2026")
            ),
            contraindications = listOf("Severe hepatic impairment or active liver failure", "Known hypersensitivity to acetaminophen"),
            precautions = listOf("Check all cold/flu combination medicines to avoid duplicate paracetamol", "Use caution in chronic alcohol use or low body weight"),
            keyInteractions = listOf("Warfarin (elevated INR with regular high doses)", "Alcohol (increased hepatotoxicity risk)", "Isoniazid / Carbamazepine (CYP2E1 induction)"),
            storageInfo = "Store below 25°C in a dry place away from moisture and direct sunlight.",
            manufacturerInfo = "Micro Labs (Dolo), GSK/Haleon (Crocin), Kenvue (Tylenol) & global generics.",
            referenceSources = "FDA DailyMed, WHO Model Formulary, British National Formulary (BNF)",
            lastUpdated = "Nov 2026",
            halfLifeHours = "2–3 hours",
            bioavailability = "70–90% oral",
            metabolism = "Hepatic glucuronidation & sulfation; minor CYP2E1"
        ),
        MedicineMonograph(
            id = "med_ibuprofen",
            genericName = "Ibuprofen",
            brandNames = listOf("Brufen", "Advil", "Motrin", "Nurofen"),
            activeIngredients = listOf("Ibuprofen"),
            saltComposition = "Ibuprofen BP/USP",
            category = "Analgesics",
            drugClass = "NSAID (Non-Steroidal Anti-Inflammatory)",
            subclass = "Propionic Acid Derivatives",
            therapeuticUses = listOf("Inflammatory joint pain", "Dysmenorrhea", "Dental pain", "Fever and headache"),
            strengths = listOf("200 mg", "400 mg", "600 mg"),
            dosageForms = listOf("Tablet", "Liquid-Filled Capsule", "Oral Suspension", "Topical Gel"),
            route = "Oral / Topical",
            simpleMoa = "Blocks enzymes at the site of injury that create swelling, redness, fever, and pain signals.",
            advancedMoa = "Non-selective, reversible competitive inhibition of cyclooxygenase-1 (COX-1) and cyclooxygenase-2 (COX-2), reducing conversion of arachidonic acid to prostaglandins (PGE2, PGI2) and thromboxane A2.",
            molecularTarget = "Cyclooxygenase-1 (COX-1) & Cyclooxygenase-2 (COX-2)",
            receptorInteraction = "Decreased downstream EP1-EP4 prostaglandin receptor activation",
            enzymeInvolvement = "COX-1 / COX-2 inhibition; hepatic CYP2C9 metabolism",
            biologicalPathway = "Arachidonic acid -> Prostaglandin G2/H2 inflammatory cascade",
            physiologicalEffects = "Peripheral anti-inflammatory, analgesic, and antipyretic actions; reduces gastric mucosal prostaglandin protection.",
            commonSideEffects = listOf(
                SideEffectDetail("Dyspepsia / Heartburn", "Upper abdominal discomfort or acid irritation.", "Common (5-10%)", "Low", "Take with food or milk to reduce stomach irritation.", "FDA DailyMed 2026"),
                SideEffectDetail("Mild dizziness", "Transient lightheadedness.", "Common (2-5%)", "Low", "Rise slowly and stay hydrated.", "BNF 2026")
            ),
            seriousSideEffects = listOf(
                SideEffectDetail("Gastrointestinal bleeding / Ulceration", "Stomach or intestinal mucosal erosion and bleeding.", "Uncommon but serious", "High", "Seek urgent care for black tarry stools or vomiting blood.", "FDA Boxed Warning"),
                SideEffectDetail("Acute kidney injury & Fluid retention", "Reduced renal prostaglandin-mediated afferent arteriolar dilation.", "Uncommon", "High", "Avoid in dehydration, advanced CKD, or alongside ACEi + diuretics.", "KDIGO / FDA 2026")
            ),
            rareSideEffects = listOf(
                SideEffectDetail("Bronchospasm in aspirin-sensitive asthma", "Acute airway narrowing in NSAID-exacerbated respiratory disease.", "Rare (<0.1%)", "High", "Avoid if prior NSAID-triggered wheezing.", "BNF 2026")
            ),
            contraindications = listOf("Active peptic ulcer or GI hemorrhage", "Third trimester of pregnancy", "Severe renal or heart failure", "NSAID-induced asthma"),
            precautions = listOf("Use lowest effective dose for shortest duration", "Monitor blood pressure and kidney function in older adults"),
            keyInteractions = listOf("Warfarin (high bleeding risk)", "Aspirin (competitive COX-1 binding & GI bleed)", "Lisinopril/ACEi (reduced antihypertensive effect & renal strain)"),
            storageInfo = "Store at room temperature (20–25°C) tightly closed.",
            manufacturerInfo = "Abbott (Brufen), Haleon (Advil), Kenvue (Motrin) & generics.",
            referenceSources = "FDA DailyMed, BNF, WHO Essential Medicines",
            lastUpdated = "Nov 2026",
            halfLifeHours = "1.8–2.5 hours",
            bioavailability = "80–90% oral",
            metabolism = "Hepatic CYP2C9 oxidation & glucuronidation"
        ),
        MedicineMonograph(
            id = "med_amoxicillin",
            genericName = "Amoxicillin",
            brandNames = listOf("Mox 500", "Amoxil", "Novamox", "Trimox"),
            activeIngredients = listOf("Amoxicillin Trihydrate"),
            saltComposition = "Amoxicillin Trihydrate eq. to Amoxicillin",
            category = "Antibiotics",
            drugClass = "Antibiotic (Beta-Lactam)",
            subclass = "Aminopenicillins",
            therapeuticUses = listOf("Bacterial respiratory tract infections", "Acute otitis media", "Dental bacterial infections", "H. pylori combination therapy"),
            strengths = listOf("250 mg", "500 mg", "875 mg"),
            dosageForms = listOf("Capsule", "Tablet", "Powder for Oral Suspension"),
            route = "Oral",
            simpleMoa = "Stops susceptible bacteria from building strong protective cell walls, causing them to burst and clear.",
            advancedMoa = "Binds irreversibly to Penicillin-Binding Proteins (PBPs) on the inner bacterial membrane, inhibiting the transpeptidation cross-linking step of peptidoglycan cell wall synthesis and triggering bacterial autolysins.",
            molecularTarget = "Bacterial Penicillin-Binding Proteins (PBPs)",
            receptorInteraction = "Covalent acylation of active-site serine on PBP transpeptidases",
            enzymeInvolvement = "Inhibits D-alanyl-D-alanine transpeptidase; susceptible to bacterial beta-lactamases",
            biologicalPathway = "Bacterial peptidoglycan cross-linking -> Osmotic cell lysis",
            physiologicalEffects = "Bactericidal activity against susceptible Gram-positive and select Gram-negative organisms; ineffective against viruses.",
            commonSideEffects = listOf(
                SideEffectDetail("Mild diarrhea", "Altered gut flora causing loose stools.", "Common (5-10%)", "Low", "Maintain hydration; contact clinician if severe or bloody.", "FDA DailyMed 2026"),
                SideEffectDetail("Nausea or mild skin rash", "Mild gastrointestinal upset or non-allergic maculopapular rash.", "Common (3-7%)", "Low", "Distinguish delayed rash from immediate hives/anaphylaxis.", "BNF 2026")
            ),
            seriousSideEffects = listOf(
                SideEffectDetail("Anaphylaxis / Severe penicillin allergy", "Swelling of lips/tongue, hives, wheezing, or drop in blood pressure.", "Rare (0.01-0.05%)", "High", "Emergency medical care required immediately.", "FDA DailyMed"),
                SideEffectDetail("C. difficile-associated diarrhea", "Severe watery diarrhea during or weeks after antibiotic therapy.", "Rare", "High", "Do not self-treat with anti-diarrheals; consult physician.", "IDSA / FDA 2026")
            ),
            rareSideEffects = listOf(
                SideEffectDetail("Cholestatic hepatitis", "Reversible elevation of liver enzymes.", "Very Rare", "Moderate", "Report jaundice or dark urine.", "BNF 2026")
            ),
            contraindications = listOf("History of severe hypersensitivity to penicillins or beta-lactams"),
            precautions = listOf("Complete the full prescribed course unless instructed otherwise", "Does not treat viral colds or flu", "Adjust dose in severe renal impairment"),
            keyInteractions = listOf("Warfarin (possible INR increase)", "Methotrexate (reduced renal clearance)", "Allopurinol (increased skin rash frequency)"),
            storageInfo = "Store capsules below 25°C; refrigerate reconstituted oral suspension and discard after 14 days.",
            manufacturerInfo = "Sun Pharma (Mox), Cipla (Novamox), GSK & global generics.",
            referenceSources = "FDA DailyMed, WHO AWaRe Antibiotic Book, BNF",
            lastUpdated = "Nov 2026",
            halfLifeHours = "1–1.5 hours",
            bioavailability = "74–92% oral",
            metabolism = "Minimal hepatic; ~60% excreted unchanged in urine"
        ),
        MedicineMonograph(
            id = "med_azithromycin",
            genericName = "Azithromycin",
            brandNames = listOf("Azithral", "Zithromax", "Azee 500"),
            activeIngredients = listOf("Azithromycin Dihydrate"),
            saltComposition = "Azithromycin Dihydrate",
            category = "Antibiotics",
            drugClass = "Antibiotic (Macrolide)",
            subclass = "Azalides",
            therapeuticUses = listOf("Atypical community-acquired pneumonia", "Bacterial sinusitis", "Certain sexually transmitted bacterial infections"),
            strengths = listOf("250 mg", "500 mg"),
            dosageForms = listOf("Tablet", "Oral Suspension"),
            route = "Oral",
            simpleMoa = "Slows down bacteria by blocking the microscopic protein factories they need to grow and multiply.",
            advancedMoa = "Binds reversibly to the 23S rRNA component of the 50S bacterial ribosomal subunit, obstructing the nascent peptide exit tunnel and preventing aminoacyl-tRNA translocation during protein synthesis.",
            molecularTarget = "Bacterial 50S Ribosomal Subunit (23S rRNA)",
            receptorInteraction = "Non-covalent binding within the peptidyl transferase exit tunnel",
            enzymeInvolvement = "No major CYP3A4 inhibition (unlike erythromycin); P-glycoprotein substrate",
            biologicalPathway = "Bacterial mRNA translation inhibition -> Bacteriostatic growth arrest",
            physiologicalEffects = "High intracellular tissue concentration with prolonged elimination half-life enabling short courses.",
            commonSideEffects = listOf(
                SideEffectDetail("Abdominal cramping or diarrhea", "Motilin-like GI stimulation and microbiome shift.", "Common (5-9%)", "Low", "Take with food if stomach discomfort occurs.", "FDA DailyMed 2026")
            ),
            seriousSideEffects = listOf(
                SideEffectDetail("QT interval prolongation / Arrhythmia", "Electrical rhythm disturbance (Torsades de Pointes risk) especially with other QT-prolonging drugs.", "Rare", "High", "Seek urgent care for palpitations, fainting, or dizziness.", "FDA Drug Safety Alert")
            ),
            rareSideEffects = listOf(
                SideEffectDetail("Transient hearing loss", "Reversible ototoxicity mostly at prolonged high doses.", "Very Rare", "Moderate", "Report ringing in ears or muffled hearing.", "BNF 2026")
            ),
            contraindications = listOf("Prior cholestatic jaundice with azithromycin", "Macrolide hypersensitivity"),
            precautions = listOf("Use caution in congenital long QT syndrome, low potassium/magnesium, or bradycardia"),
            keyInteractions = listOf("Amiodarone / Ondansetron (additive QT prolongation)", "Warfarin (monitor INR)", "Aluminum/Magnesium antacids (lower peak levels)"),
            storageInfo = "Store tablets at 15–30°C in original blister pack.",
            manufacturerInfo = "Alembic (Azithral), Pfizer (Zithromax), Cipla (Azee).",
            referenceSources = "FDA DailyMed, BNF, IDSA Guidelines",
            lastUpdated = "Nov 2026",
            halfLifeHours = "68 hours (tissue)",
            bioavailability = "37% oral",
            metabolism = "Primarily biliary excretion unchanged"
        ),
        MedicineMonograph(
            id = "med_metformin",
            genericName = "Metformin",
            brandNames = listOf("Glycomet", "Glucophage", "Obimet"),
            activeIngredients = listOf("Metformin Hydrochloride"),
            saltComposition = "Metformin HCl IP/USP",
            category = "Antidiabetics",
            drugClass = "Antidiabetic (Biguanide)",
            subclass = "Biguanides",
            therapeuticUses = listOf("Type 2 diabetes mellitus glycemic control", "Insulin resistance support under physician supervision"),
            strengths = listOf("500 mg", "850 mg", "1000 mg"),
            dosageForms = listOf("Immediate-Release Tablet", "Extended-Release (SR/XR) Tablet"),
            route = "Oral",
            simpleMoa = "Reduces excess sugar released by the liver and helps body muscles respond better to natural insulin without typically causing low blood sugar alone.",
            advancedMoa = "Inhibits hepatic mitochondrial respiratory chain Complex I (and mitochondrial glycerophosphate dehydrogenase), elevating AMP:ATP ratio to activate AMP-activated protein kinase (AMPK), suppressing hepatic gluconeogenesis and enhancing peripheral GLUT4 glucose uptake.",
            molecularTarget = "Mitochondrial Complex I & AMPK Signaling Axis",
            receptorInteraction = "Intracellular metabolic enzyme modulation (OCT1 hepatic uptake)",
            enzymeInvolvement = "Activates AMPK; inhibits hepatic PEPCK and G6Pase gene transcription",
            biologicalPathway = "Hepatic gluconeogenesis suppression + peripheral insulin sensitization",
            physiologicalEffects = "Lowers fasting and postprandial blood glucose and HbA1c with neutral-to-modest weight effect.",
            commonSideEffects = listOf(
                SideEffectDetail("GI discomfort / Nausea / Loose stools", "Common during initiation or dose increases.", "Very Common (15-25%)", "Low", "Take with meals; extended-release formulations often improve tolerability.", "ADA / FDA 2026"),
                SideEffectDetail("Metallic taste", "Mild altered taste sensation.", "Common (3%)", "Low", "Usually subsides over time.", "BNF 2026")
            ),
            seriousSideEffects = listOf(
                SideEffectDetail("Metformin-associated lactic acidosis (MALA)", "Rare buildup of lactic acid in blood, mainly in severe kidney failure, severe dehydration, or hypoxia.", "Very Rare (<0.01%)", "High", "Seek immediate emergency care for severe fatigue, rapid breathing, cold limbs, or muscle pain.", "FDA Boxed Warning"),
                SideEffectDetail("Vitamin B12 deficiency", "Reduced ileal absorption with long-term therapy.", "Common with multi-year use", "Moderate", "Periodic B12 monitoring recommended by ADA guidelines.", "ADA Standards of Care 2026")
            ),
            rareSideEffects = listOf(
                SideEffectDetail("Mild erythema / pruritus", "Uncommon skin irritation.", "Very Rare", "Low", "Inform doctor if persistent.", "BNF 2026")
            ),
            contraindications = listOf("Severe renal impairment (eGFR < 30 mL/min/1.73m²)", "Acute or chronic metabolic acidosis / DKA"),
            precautions = listOf("Temporarily hold around iodinated contrast procedures per radiologist/doctor protocol", "Avoid excessive alcohol intake"),
            keyInteractions = listOf("Iodinated contrast agents (acute renal decline -> MALA risk)", "Excess alcohol (potentiates lactate accumulation)", "Cimetidine / Dolutegravir (OCT2/MATE inhibition)"),
            storageInfo = "Store at 20–25°C in a light-resistant, moisture-tight container.",
            manufacturerInfo = "USV (Glycomet), Merck (Glucophage), Abbott.",
            referenceSources = "ADA Standards of Care, FDA DailyMed, WHO Essential Medicines",
            lastUpdated = "Nov 2026",
            halfLifeHours = "4–8.7 hours (plasma)",
            bioavailability = "50–60% oral",
            metabolism = "Not metabolized; 100% renal tubular excretion"
        ),
        MedicineMonograph(
            id = "med_amlodipine",
            genericName = "Amlodipine",
            brandNames = listOf("Amlong", "Norvasc", "Stamlo"),
            activeIngredients = listOf("Amlodipine Besylate"),
            saltComposition = "Amlodipine Besylate eq. to Amlodipine",
            category = "Antihypertensives",
            drugClass = "Antihypertensive (Calcium Channel Blocker)",
            subclass = "Dihydropyridine CCBs",
            therapeuticUses = listOf("Hypertension (high blood pressure)", "Chronic stable angina", "Vasospastic angina"),
            strengths = listOf("2.5 mg", "5 mg", "10 mg"),
            dosageForms = listOf("Tablet", "Oral Solution"),
            route = "Oral",
            simpleMoa = "Gently relaxes the smooth muscle walls of arteries so blood flows more easily and blood pressure drops smoothly over 24 hours.",
            advancedMoa = "Selectively inhibits transmembrane influx of Ca²⁺ ions through voltage-gated L-type (Cav1.2) calcium channels in vascular smooth muscle cells more strongly than cardiac myocytes, lowering peripheral systemic vascular resistance.",
            molecularTarget = "Vascular L-Type Voltage-Gated Calcium Channels (Cav1.2)",
            receptorInteraction = "State-dependent dihydropyridine binding site antagonism",
            enzymeInvolvement = "Extensive hepatic CYP3A4 metabolism",
            biologicalPathway = "Reduced intracellular Ca²⁺ -> Calmodulin/MLCK inactivation -> Arterial vasodilation",
            physiologicalEffects = "Sustained peripheral vasodilation reducing afterload and myocardial oxygen demand.",
            commonSideEffects = listOf(
                SideEffectDetail("Peripheral ankle edema", "Dose-dependent fluid pooling around ankles due to precapillary dilation.", "Common (5-11%)", "Low", "Elevate legs; consult doctor before altering dose.", "FDA DailyMed 2026"),
                SideEffectDetail("Flushing or mild headache", "Warmth or fullness due to vasodilation.", "Common (3-7%)", "Low", "Often improves after the first 2 weeks.", "BNF 2026")
            ),
            seriousSideEffects = listOf(
                SideEffectDetail("Symptomatic hypotension", "Marked dizziness or fainting from excessive blood pressure reduction.", "Uncommon", "High", "Sit or lie down and seek medical review if fainting occurs.", "FDA DailyMed")
            ),
            rareSideEffects = listOf(
                SideEffectDetail("Gingival hyperplasia", "Overgrowth of gum tissue with chronic use.", "Rare (<0.1%)", "Low", "Maintain dental hygiene and discuss with clinician.", "BNF 2026")
            ),
            contraindications = listOf("Cardiogenic shock", "Severe aortic stenosis", "Hypersensitivity to dihydropyridines"),
            precautions = listOf("Start at lower doses in hepatic impairment or older adults", "Do not abruptly discontinue without medical guidance"),
            keyInteractions = listOf("Simvastatin (increases simvastatin levels -> myopathy risk; max 20 mg simvastatin)", "Strong CYP3A4 inhibitors (Clarithromycin, Ketoconazole)"),
            storageInfo = "Store at 15–30°C protected from light.",
            manufacturerInfo = "Micro Labs (Amlong), Viatris (Norvasc), Dr. Reddy's (Stamlo).",
            referenceSources = "ACC/AHA Hypertension Guidelines, FDA DailyMed, BNF",
            lastUpdated = "Nov 2026",
            halfLifeHours = "30–50 hours",
            bioavailability = "64–90% oral",
            metabolism = "Hepatic CYP3A4 (~90%)"
        ),
        MedicineMonograph(
            id = "med_lisinopril",
            genericName = "Lisinopril",
            brandNames = listOf("Listril", "Zestril", "Prinivil"),
            activeIngredients = listOf("Lisinopril Dihydrate"),
            saltComposition = "Lisinopril IP/USP",
            category = "Antihypertensives",
            drugClass = "Antihypertensive (ACE Inhibitor)",
            subclass = "Angiotensin-Converting Enzyme Inhibitors",
            therapeuticUses = listOf("Hypertension", "Heart failure with reduced ejection fraction", "Post-myocardial infarction protection", "Diabetic kidney protection"),
            strengths = listOf("2.5 mg", "5 mg", "10 mg", "20 mg"),
            dosageForms = listOf("Tablet", "Oral Solution"),
            route = "Oral",
            simpleMoa = "Prevents the body from producing a vessel-tightening hormone (angiotensin II), relaxing blood vessels and easing strain on the heart and kidneys.",
            advancedMoa = "Competitively inhibits Angiotensin-Converting Enzyme (peptidyl-dipeptidase A), blocking conversion of Angiotensin I to vasoconstrictor Angiotensin II, decreasing aldosterone secretion, and slowing bradykinin degradation.",
            molecularTarget = "Angiotensin-Converting Enzyme (ACE / CD143)",
            receptorInteraction = "Zinc-chelating active site inhibition on endothelial ACE",
            enzymeInvolvement = "Directly inhibits ACE; not a prodrug (does not require hepatic activation)",
            biologicalPathway = "RAAS (Renin-Angiotensin-Aldosterone System) downregulation + Bradykinin preservation",
            physiologicalEffects = "Arterial/venous dilation, natriuresis, and reduced efferent glomerular arteriolar pressure.",
            commonSideEffects = listOf(
                SideEffectDetail("Persistent dry cough", "Tickly, non-productive cough caused by accumulated bradykinin.", "Common (5-15%)", "Low", "Discuss with doctor; switching class may be considered by prescriber.", "FDA DailyMed 2026"),
                SideEffectDetail("Mild dizziness on standing", "First-dose orthostatic blood pressure drop.", "Common (4-8%)", "Low", "Stand up gradually.", "BNF 2026")
            ),
            seriousSideEffects = listOf(
                SideEffectDetail("Angioedema", "Rapid swelling of face, lips, tongue, or airway.", "Rare (0.1-0.5%)", "High", "Medical emergency — call emergency services immediately.", "FDA Boxed Warning"),
                SideEffectDetail("Hyperkalemia / Fetal toxicity", "Elevated blood potassium; strictly harmful to developing fetus.", "Serious", "High", "Contraindicated in pregnancy; monitor electrolytes and creatinine.", "FDA Boxed Warning")
            ),
            rareSideEffects = listOf(
                SideEffectDetail("Neutropenia", "Decreased white blood cell count mostly in autoimmune renal disease.", "Very Rare", "High", "Report unexplained fever or sore throat.", "BNF 2026")
            ),
            contraindications = listOf("Pregnancy (fetal toxicity)", "History of ACE-inhibitor angioedema", "Concomitant sacubitril/valsartan within 36 hours"),
            precautions = listOf("Monitor serum potassium and renal function", "Avoid potassium salt substitutes unless approved"),
            keyInteractions = listOf("Ibuprofen/NSAIDs (attenuates BP lowering & increases renal injury risk)", "Spironolactone / Potassium supplements (hyperkalemia)", "Lithium (increased lithium toxicity)"),
            storageInfo = "Store at 20–25°C away from moisture.",
            manufacturerInfo = "Torrent (Listril), AstraZeneca (Zestril), Merck.",
            referenceSources = "FDA DailyMed, ACC/AHA Guidelines, BNF",
            lastUpdated = "Nov 2026",
            halfLifeHours = "12 hours",
            bioavailability = "25% oral",
            metabolism = "Unchanged renal excretion"
        ),
        MedicineMonograph(
            id = "med_omeprazole",
            genericName = "Omeprazole",
            brandNames = listOf("Omez", "Prilosec", "Losec"),
            activeIngredients = listOf("Omeprazole"),
            saltComposition = "Omeprazole Gastro-Resistant",
            category = "Antacids",
            drugClass = "Proton Pump Inhibitor (PPI)",
            subclass = "Substituted Benzimidazoles",
            therapeuticUses = listOf("GERD (acid reflux)", "Peptic ulcer disease", "H. pylori eradication regimen", "NSAID-associated ulcer prevention"),
            strengths = listOf("10 mg", "20 mg", "40 mg"),
            dosageForms = listOf("Delayed-Release Capsule", "Enteric-Coated Tablet"),
            route = "Oral",
            simpleMoa = "Turns down the tiny acid pumps inside stomach lining cells so excess stomach acid drops and irritated tissues can heal.",
            advancedMoa = "Weak-base prodrug concentrated in acidic parietal cell canaliculi, converted to active sulfenamide that forms a covalent disulfide bond with cysteine residues of the H⁺/K⁺-ATPase proton pump, irreversibly inhibiting final-step acid secretion.",
            molecularTarget = "Gastric Parietal H⁺/K⁺-ATPase Proton Pump",
            receptorInteraction = "Irreversible covalent disulfide binding at Cys813 on catalytic alpha-subunit",
            enzymeInvolvement = "Substrate and strong inhibitor of CYP2C19",
            biologicalPathway = "Final common pathway of stimulated & basal gastric HCl secretion",
            physiologicalEffects = "Profound >24-hour gastric acid suppression until new pump proteins are synthesized.",
            commonSideEffects = listOf(
                SideEffectDetail("Headache or mild abdominal gas", "Generally mild and self-limiting.", "Common (2-6%)", "Low", "Take 30–60 minutes before a meal for optimal activation.", "FDA DailyMed 2026")
            ),
            seriousSideEffects = listOf(
                SideEffectDetail("Hypomagnesemia & B12/fracture risk (long-term)", "Nutrient malabsorption with prolonged unsupervised use (>1 year).", "Uncommon (chronic use)", "Moderate", "Review ongoing need periodically with physician.", "FDA Safety Communication")
            ),
            rareSideEffects = listOf(
                SideEffectDetail("Acute interstitial nephritis", "Immune-mediated kidney inflammation.", "Rare", "High", "Report rash, fever, or reduced urination.", "BNF 2026")
            ),
            contraindications = listOf("Hypersensitivity to substituted benzimidazoles", "Co-administration with rilpivirine-containing products"),
            precautions = listOf("Evaluate alarm symptoms (weight loss, difficulty swallowing) before prolonged self-use"),
            keyInteractions = listOf("Clopidogrel (CYP2C19 inhibition reduces antiplatelet activation)", "Methotrexate (elevated levels)", "Warfarin (may increase INR)"),
            storageInfo = "Store tightly sealed below 25°C; protect from humidity.",
            manufacturerInfo = "Dr. Reddy's (Omez), Procter & Gamble (Prilosec), AstraZeneca (Losec).",
            referenceSources = "FDA DailyMed, ACG GERD Guidelines, BNF",
            lastUpdated = "Nov 2026",
            halfLifeHours = "0.5–1 hour (plasma); >24h pharmacodynamic",
            bioavailability = "30–40% oral",
            metabolism = "Hepatic CYP2C19 & CYP3A4"
        ),
        MedicineMonograph(
            id = "med_cetirizine",
            genericName = "Cetirizine",
            brandNames = listOf("Cetzine", "Zyrtec", "Alerid", "Okacet"),
            activeIngredients = listOf("Cetirizine Dihydrochloride"),
            saltComposition = "Cetirizine Dihydrochloride IP/USP",
            category = "Antihistamines",
            drugClass = "Antihistamine (2nd-Generation)",
            subclass = "Piperazine H1-Receptor Antagonists",
            therapeuticUses = listOf("Allergic rhinitis (hay fever)", "Chronic spontaneous urticaria (hives)", "Allergic sneezing and itchy watery eyes"),
            strengths = listOf("5 mg", "10 mg"),
            dosageForms = listOf("Tablet", "Oral Syrup", "Chewable Tablet"),
            route = "Oral",
            simpleMoa = "Blocks histamine allergy receptors outside the brain to calm sneezing, itching, and hives with much less drowsiness than older antihistamines.",
            advancedMoa = "Selective, zwitterionic inverse agonist/antagonist at peripheral histamine H1 Gq-coupled receptors with minimal blood-brain barrier penetration via P-glycoprotein efflux.",
            molecularTarget = "Peripheral Histamine H1 Receptor (HRH1)",
            receptorInteraction = "Competitive inverse agonism stabilizing inactive H1 conformation",
            enzymeInvolvement = "Minimal hepatic CYP metabolism; predominantly renal excretion",
            biologicalPathway = "Blocks Gq/PLC/IP3 histamine-mediated vasodilation & sensory nerve pruritus",
            physiologicalEffects = "Rapid 1-hour onset allergy relief lasting 24 hours.",
            commonSideEffects = listOf(
                SideEffectDetail("Mild drowsiness or dry mouth", "More sedating than fexofenadine in sensitive individuals.", "Common (5-14%)", "Low", "Assess alertness before driving or operating machinery.", "FDA DailyMed 2026")
            ),
            seriousSideEffects = listOf(
                SideEffectDetail("Severe paradoxical agitation or allergic reaction", "Very uncommon hypersensitivity or urinary retention.", "Very Rare", "Moderate", "Seek care if hives worsen or breathing changes.", "BNF 2026")
            ),
            rareSideEffects = listOf(
                SideEffectDetail("Rebound pruritus on abrupt discontinuation after multi-year use", "Intense itching a few days after stopping chronic daily therapy.", "Rare", "Low", "Consult clinician if itching returns after stopping.", "FDA Safety Communication")
            ),
            contraindications = listOf("Severe renal failure without dose adjustment", "Hydroxyzine/cetirizine allergy"),
            precautions = listOf("Avoid combining with alcohol or CNS depressants"),
            keyInteractions = listOf("Alcohol & sedatives (additive drowsiness)", "Theophylline (slight clearance reduction)"),
            storageInfo = "Store at 20–25°C in a dry place.",
            manufacturerInfo = "GSK (Cetzine), Kenvue (Zyrtec), Cipla (Alerid/Okacet).",
            referenceSources = "FDA DailyMed, EAACI Allergy Guidelines, BNF",
            lastUpdated = "Nov 2026",
            halfLifeHours = "8.3 hours",
            bioavailability = "70% oral",
            metabolism = "Minimal hepatic; 70% renal excretion"
        ),
        MedicineMonograph(
            id = "med_warfarin",
            genericName = "Warfarin",
            brandNames = listOf("Warf", "Coumadin", "Jantoven"),
            activeIngredients = listOf("Warfarin Sodium"),
            saltComposition = "Warfarin Sodium Clathrate",
            category = "Anticoagulants",
            drugClass = "Anticoagulant (Vitamin K Antagonist)",
            subclass = "Coumarin Derivatives",
            therapeuticUses = listOf("Stroke prevention in atrial fibrillation", "Deep vein thrombosis (DVT) & pulmonary embolism", "Mechanical heart valve thrombosis prevention"),
            strengths = listOf("1 mg", "2 mg", "3 mg", "5 mg"),
            dosageForms = listOf("Scored Tablet"),
            route = "Oral",
            simpleMoa = "Slows down blood clot formation by limiting Vitamin K recycling needed by the liver to build clotting proteins.",
            advancedMoa = "Competitively inhibits Vitamin K Epoxide Reductase Complex Subunit 1 (VKORC1), depleting reduced Vitamin K hydroquinone required for gamma-carboxylation of clotting factors II, VII, IX, X and anticoagulant proteins C and S.",
            molecularTarget = "Vitamin K Epoxide Reductase (VKORC1)",
            receptorInteraction = "Enzyme active-site inhibition in hepatic microsomes",
            enzymeInvolvement = "S-warfarin (more potent) metabolized by CYP2C9; R-warfarin by CYP1A2/CYP3A4",
            biologicalPathway = "Vitamin K cycle -> Coagulation cascade (Factors II, VII, IX, X)",
            physiologicalEffects = "Prolongs Prothrombin Time / INR; requires close laboratory INR monitoring.",
            commonSideEffects = listOf(
                SideEffectDetail("Easy bruising or minor gum bleeding", "Expected sign of thinned blood clotting.", "Common", "Moderate", "Maintain regular INR lab checks and consistent leafy-green Vitamin K intake.", "FDA DailyMed 2026")
            ),
            seriousSideEffects = listOf(
                SideEffectDetail("Major internal or intracranial hemorrhage", "Life-threatening bleeding risk especially if INR exceeds target range.", "Serious", "High", "Emergency medical evaluation for severe headache, blood in urine/stool, or uncontrolled bleeding.", "FDA Boxed Warning")
            ),
            rareSideEffects = listOf(
                SideEffectDetail("Warfarin-induced skin necrosis", "Microvascular thrombosis during initiation in Protein C deficiency.", "Very Rare", "High", "Report painful dark skin lesions immediately.", "BNF 2026")
            ),
            contraindications = listOf("Active major bleeding", "Pregnancy (except specific mechanical valve specialist protocols)", "Severe uncontrolled hypertension"),
            precautions = listOf("Narrow therapeutic index — check EVERY new medicine, OTC painkiller, or herbal supplement for interactions"),
            keyInteractions = listOf("Aspirin & Ibuprofen (HIGH bleeding risk)", "Amoxicillin/Azithromycin (INR elevation)", "Omeprazole (CYP inhibition -> elevated INR)"),
            storageInfo = "Store at 15–30°C protected from light.",
            manufacturerInfo = "Cipla (Warf), Bristol Myers Squibb (Coumadin).",
            referenceSources = "FDA DailyMed, CHEST Antithrombotic Guidelines, BNF",
            lastUpdated = "Nov 2026",
            halfLifeHours = "36–42 hours",
            bioavailability = ">95% oral",
            metabolism = "Hepatic CYP2C9, CYP1A2, CYP3A4"
        ),
        MedicineMonograph(
            id = "med_aspirin",
            genericName = "Aspirin (Acetylsalicylic Acid)",
            brandNames = listOf("Ecosprin", "Disprin", "Bayer Aspirin"),
            activeIngredients = listOf("Acetylsalicylic Acid"),
            saltComposition = "Acetylsalicylic Acid IP/USP",
            category = "Analgesics",
            drugClass = "Antiplatelet / Salicylate NSAID",
            subclass = "Salicylates",
            therapeuticUses = listOf("Low-dose cardiovascular secondary prevention", "Acute coronary syndrome protocol", "Pain and fever (adults only)"),
            strengths = listOf("75 mg", "81 mg", "150 mg", "325 mg"),
            dosageForms = listOf("Enteric-Coated Tablet", "Dispersible Tablet", "Chewable Tablet"),
            route = "Oral",
            simpleMoa = "Prevents blood platelets from clumping together at low doses, and eases pain or inflammation at higher doses.",
            advancedMoa = "Irreversibly acetylates serine-529 of platelet COX-1, abolishing Thromboxane A2 (TXA2) synthesis for the 7–10 day lifespan of the platelet.",
            molecularTarget = "Platelet Cyclooxygenase-1 (COX-1 Ser529)",
            receptorInteraction = "Irreversible covalent acetylation",
            enzymeInvolvement = "Blocks platelet COX-1 & vascular COX-2; hydrolyzed to salicylic acid",
            biologicalPathway = "Arachidonic acid -> Thromboxane A2 platelet aggregation pathway",
            physiologicalEffects = "Durable antiplatelet inhibition; dose-dependent analgesia and antipyresis.",
            commonSideEffects = listOf(
                SideEffectDetail("Gastric irritation / Heartburn", "Direct and prostaglandin-mediated mucosal irritation.", "Common (5-10%)", "Low", "Take with food or as enteric-coated formulation per clinician.", "FDA DailyMed 2026")
            ),
            seriousSideEffects = listOf(
                SideEffectDetail("GI hemorrhage / Reye's syndrome in children", "Bleeding risk in adults; Reye's syndrome encephalopathy in children/teens with viral illness.", "Serious", "High", "Never give aspirin to children or teenagers with viral fever unless directed by a specialist.", "FDA Warning / CDC")
            ),
            rareSideEffects = listOf(
                SideEffectDetail("Tinnitus (Salicylism)", "Ringing in ears indicating elevated salicylate levels.", "Rare (high doses)", "Moderate", "Report ear ringing promptly.", "BNF 2026")
            ),
            contraindications = listOf("Children/adolescents with viral infections", "Active GI ulcer or bleeding disorder", "Aspirin-sensitive asthma"),
            precautions = listOf("Inform dentists and surgeons about daily low-dose aspirin therapy"),
            keyInteractions = listOf("Warfarin (HIGH bleeding risk)", "Ibuprofen (may interfere with cardioprotective COX-1 acetylation)", "Clopidogrel (dual antiplatelet bleeding monitoring)"),
            storageInfo = "Store tightly closed below 25°C; discard if strong vinegar odor develops.",
            manufacturerInfo = "USV (Ecosprin), Reckitt (Disprin), Bayer.",
            referenceSources = "ACC/AHA Guidelines, FDA DailyMed, BNF",
            lastUpdated = "Nov 2026",
            halfLifeHours = "15–20 min (aspirin); 2–3h (salicylate); 7–10d platelet effect",
            bioavailability = "68% oral",
            metabolism = "Plasma/hepatic esterases & glycine conjugation"
        ),
        MedicineMonograph(
            id = "med_clopidogrel",
            genericName = "Clopidogrel",
            brandNames = listOf("Clopilet", "Plavix", "Deplatt"),
            activeIngredients = listOf("Clopidogrel Bisulfate"),
            saltComposition = "Clopidogrel Bisulfate",
            category = "Anticoagulants",
            drugClass = "Antiplatelet (P2Y12 Inhibitor)",
            subclass = "Thienopyridines",
            therapeuticUses = listOf("Post-stent thrombosis prevention", "Secondary prevention of stroke and heart attack", "Peripheral arterial disease"),
            strengths = listOf("75 mg", "300 mg"),
            dosageForms = listOf("Film-Coated Tablet"),
            route = "Oral",
            simpleMoa = "Keeps platelets from sticking together inside arteries and stents by blocking an activation signal on their surface.",
            advancedMoa = "Prodrug bioactivated via two-step hepatic CYP2C19 oxidation into an active thiol metabolite that irreversibly binds platelet P2Y12 ADP receptors, blocking GPIIb/IIIa complex activation.",
            molecularTarget = "Platelet P2Y12 ADP Receptor",
            receptorInteraction = "Irreversible disulfide bond to P2Y12 purinergic receptor",
            enzymeInvolvement = "Requires CYP2C19 hepatic bioactivation",
            biologicalPathway = "ADP -> P2Y12 -> Gi signaling -> GPIIb/IIIa fibrinogen cross-linking",
            physiologicalEffects = "Sustained platelet aggregation inhibition for platelet lifespan.",
            commonSideEffects = listOf(
                SideEffectDetail("Easy bruising or nosebleeds", "Increased bleeding time from platelet inhibition.", "Common (5-8%)", "Low", "Never stop suddenly after a cardiac stent without cardiologist approval.", "FDA DailyMed 2026")
            ),
            seriousSideEffects = listOf(
                SideEffectDetail("Major gastrointestinal or intracranial bleeding", "Serious bleeding events.", "Uncommon", "High", "Seek emergency help for persistent bleeding or dark stools.", "FDA Boxed Warning")
            ),
            rareSideEffects = listOf(
                SideEffectDetail("Thrombotic thrombocytopenic purpura (TTP)", "Rare microvascular clotting & low platelets.", "Very Rare", "High", "Urgent evaluation for fever, purple skin spots, and confusion.", "FDA DailyMed")
            ),
            contraindications = listOf("Active pathological bleeding (peptic ulcer, intracranial bleed)"),
            precautions = listOf("Strong CYP2C19 inhibitors such as omeprazole reduce clopidogrel effectiveness"),
            keyInteractions = listOf("Omeprazole (HIGH — reduces active clopidogrel formation)", "Warfarin / Aspirin (elevated bleeding risk)"),
            storageInfo = "Store at 20–25°C.",
            manufacturerInfo = "Sun Pharma (Clopilet), Sanofi (Plavix), Torrent (Deplatt).",
            referenceSources = "FDA DailyMed, ESC/ACC Cardiology Guidelines, BNF",
            lastUpdated = "Nov 2026",
            halfLifeHours = "6 hours (parent); 7–10 days platelet effect",
            bioavailability = ">50% oral",
            metabolism = "Hepatic esterases & CYP2C19 bioactivation"
        ),
        MedicineMonograph(
            id = "med_fluoxetine",
            genericName = "Fluoxetine",
            brandNames = listOf("Fludac", "Prozac", "Flunil"),
            activeIngredients = listOf("Fluoxetine Hydrochloride"),
            saltComposition = "Fluoxetine HCl IP/USP",
            category = "Antidepressants",
            drugClass = "Antidepressant (SSRI)",
            subclass = "Selective Serotonin Reuptake Inhibitors",
            therapeuticUses = listOf("Major depressive disorder", "Obsessive-compulsive disorder (OCD)", "Panic disorder"),
            strengths = listOf("10 mg", "20 mg", "40 mg"),
            dosageForms = listOf("Capsule", "Tablet", "Oral Solution"),
            route = "Oral",
            simpleMoa = "Helps keep natural mood-regulating serotonin active longer between brain nerve cells.",
            advancedMoa = "Selectively inhibits the presynaptic Serotonin Transporter (SERT / SLC6A4), increasing synaptic cleft 5-HT concentration and inducing gradual downregulation of 5-HT1A autoreceptors.",
            molecularTarget = "Presynaptic Serotonin Transporter (SERT)",
            receptorInteraction = "High-affinity competitive SERT blockade",
            enzymeInvolvement = "Strong CYP2D6 inhibitor; active metabolite norfluoxetine",
            biologicalPathway = "Central serotonergic neurotransmission & neuroplasticity (BDNF)",
            physiologicalEffects = "Gradual antidepressant and anxiolytic response typically over 2–6 weeks.",
            commonSideEffects = listOf(
                SideEffectDetail("Mild insomnia, nausea, or vivid dreams", "Early serotonergic adaptation effects.", "Common (10-15%)", "Low", "Taking in the morning may reduce sleep disruption.", "FDA DailyMed 2026")
            ),
            seriousSideEffects = listOf(
                SideEffectDetail("Serotonin syndrome (with interacting drugs)", "Agitation, sweating, muscle rigidity, rapid heart rate, fever.", "Rare", "High", "Emergency care needed; avoid MAOIs and monitor serotonergic combinations.", "FDA Warning")
            ),
            rareSideEffects = listOf(
                SideEffectDetail("Hyponatremia (SIADH)", "Low blood sodium mainly in older adults.", "Rare", "Moderate", "Report confusion or severe weakness.", "BNF 2026")
            ),
            contraindications = listOf("Concomitant MAOI therapy (requires washout period)", "Pimozide or thioridazine co-administration"),
            precautions = listOf("Monitor mood closely during early treatment weeks", "Do not stop abruptly without physician guidance"),
            keyInteractions = listOf("Warfarin / NSAIDs (increased GI bleeding)", "Tramadol / Triptans (serotonin syndrome risk)", "MAOIs (contraindicated)"),
            storageInfo = "Store at 20–25°C away from light.",
            manufacturerInfo = "Cadila (Fludac), Eli Lilly (Prozac), Intas (Flunil).",
            referenceSources = "APA Guidelines, FDA DailyMed, BNF",
            lastUpdated = "Nov 2026",
            halfLifeHours = "1–3 days (parent); 4–16 days (norfluoxetine)",
            bioavailability = "60–80% oral",
            metabolism = "Hepatic CYP2D6 / CYP2C9"
        ),
        MedicineMonograph(
            id = "med_fluconazole",
            genericName = "Fluconazole",
            brandNames = listOf("Forcan", "Diflucan", "Zocon"),
            activeIngredients = listOf("Fluconazole"),
            saltComposition = "Fluconazole IP/USP",
            category = "Antifungals",
            drugClass = "Antifungal (Triazole)",
            subclass = "Azole Antifungals",
            therapeuticUses = listOf("Candidiasis (oral, esophageal, vaginal)", "Dermatophyte skin infections", "Cryptococcal prophylaxis"),
            strengths = listOf("50 mg", "150 mg", "200 mg"),
            dosageForms = listOf("Tablet", "Oral Suspension"),
            route = "Oral",
            simpleMoa = "Stops fungal cells from producing ergosterol, a key building block that keeps their outer membrane intact.",
            advancedMoa = "Inhibits fungal cytochrome P450 enzyme lanosterol 14-alpha-demethylase (CYP51), depleting membrane ergosterol and accumulating toxic 14-alpha-methyl sterols.",
            molecularTarget = "Fungal Lanosterol 14-alpha-Demethylase (CYP51)",
            receptorInteraction = "Triazole nitrogen coordination with heme iron in fungal CYP51",
            enzymeInvolvement = "Potent inhibitor of human CYP2C9, CYP2C19, and moderate CYP3A4 inhibitor",
            biologicalPathway = "Fungal ergosterol biosynthesis -> Membrane permeability disruption",
            physiologicalEffects = "Fungistatic suppression of susceptible Candida and Cryptococcus species.",
            commonSideEffects = listOf(
                SideEffectDetail("Mild nausea or headache", "Usually brief and mild.", "Common (2-4%)", "Low", "Hydrate well.", "FDA DailyMed 2026")
            ),
            seriousSideEffects = listOf(
                SideEffectDetail("Hepatotoxicity or QT prolongation", "Liver enzyme elevation or cardiac rhythm changes with interacting agents.", "Rare", "High", "Review all current medications before taking.", "FDA DailyMed")
            ),
            rareSideEffects = listOf(
                SideEffectDetail("Exfoliative skin disorders", "Rare severe rash.", "Very Rare", "High", "Seek immediate care for blistering rash.", "BNF 2026")
            ),
            contraindications = listOf("Co-administration with QT-prolonging CYP3A4 substrates (e.g., cisapride)"),
            precautions = listOf("Caution in pregnancy (high/repeated doses)", "Check CYP2C9 interactions with warfarin"),
            keyInteractions = listOf("Warfarin (HIGH — markedly increases INR)", "Simvastatin/Atorvastatin (myopathy risk)", "Azithromycin (additive QT prolongation)"),
            storageInfo = "Store below 30°C.",
            manufacturerInfo = "Cipla (Forcan), Pfizer (Diflucan), FDC (Zocon).",
            referenceSources = "FDA DailyMed, IDSA Candidiasis Guidelines, BNF",
            lastUpdated = "Nov 2026",
            halfLifeHours = "30 hours",
            bioavailability = ">90% oral",
            metabolism = "80% excreted unchanged in urine"
        ),
        MedicineMonograph(
            id = "med_vitamind3",
            genericName = "Cholecalciferol (Vitamin D3)",
            brandNames = listOf("Uprise-D3", "Calcirol", "Nature Made D3"),
            activeIngredients = listOf("Cholecalciferol (Vitamin D3)"),
            saltComposition = "Cholecalciferol IP/USP",
            category = "Vitamins",
            drugClass = "Fat-Soluble Vitamin / Calcium Regulator",
            subclass = "Secosteroids",
            therapeuticUses = listOf("Vitamin D deficiency correction", "Bone mineral density support", "Adjunct calcium absorption support"),
            strengths = listOf("1000 IU", "2000 IU", "60,000 IU (weekly prescription)"),
            dosageForms = listOf("Softgel Capsule", "Oral Granules", "Drops"),
            route = "Oral",
            simpleMoa = "Converts into an active hormone that helps the gut absorb calcium and phosphorus for strong bones and balanced immunity.",
            advancedMoa = "Hydroxylated in the liver to 25(OH)D3 (calcidiol) and in the kidney via CYP27B1 to 1,25(OH)2D3 (calcitriol), which binds nuclear Vitamin D Receptors (VDR) to upregulate intestinal TRPV6 calcium channels and calbindin.",
            molecularTarget = "Nuclear Vitamin D Receptor (VDR / RXR Heterodimer)",
            receptorInteraction = "Ligand-activated nuclear transcription factor binding",
            enzymeInvolvement = "Hepatic CYP2R1 & renal CYP27B1 hydroxylation",
            biologicalPathway = "Intestinal calcium/phosphate transport & osteoblast mineralization",
            physiologicalEffects = "Maintains serum calcium homeostasis and skeletal integrity.",
            commonSideEffects = listOf(
                SideEffectDetail("Generally well tolerated at recommended doses", "Minimal adverse effects within standard intake.", "Commonly well tolerated", "Low", "Take with a meal containing healthy fat for optimal absorption.", "Endocrine Society 2026")
            ),
            seriousSideEffects = listOf(
                SideEffectDetail("Hypercalcemia (Vitamin D toxicity)", "Excessive high-dose supplementation causing high blood calcium, nausea, and kidney stones.", "Rare (excessive dosing)", "High", "Do not take high-dose weekly 60,000 IU sachets daily.", "FDA / Endocrine Society")
            ),
            rareSideEffects = listOf(
                SideEffectDetail("Nephrocalcinosis", "Renal calcium deposition in chronic overdose.", "Very Rare", "High", "Follow periodic 25(OH)D lab checks when on therapeutic replacement.", "BNF 2026")
            ),
            contraindications = listOf("Hypercalcemia", "Vitamin D toxicity"),
            precautions = listOf("Use caution in granulomatous disorders (e.g., sarcoidosis) or kidney stone history"),
            keyInteractions = listOf("Thiazide diuretics (hypercalcemia risk)", "Orlistat / Cholestyramine (reduced fat-soluble absorption)"),
            storageInfo = "Store below 25°C away from heat and light.",
            manufacturerInfo = "Alkem (Uprise-D3), Cadila (Calcirol), Pharmavite.",
            referenceSources = "Endocrine Society Clinical Practice Guideline, NIH ODS, BNF",
            lastUpdated = "Nov 2026",
            halfLifeHours = "15 days (25-OH-D)",
            bioavailability = "~80% with dietary fat",
            metabolism = "Hepatic & renal hydroxylation"
        ),
        MedicineMonograph(
            id = "med_simvastatin",
            genericName = "Simvastatin",
            brandNames = listOf("Simvotin", "Zocor", "Simcard"),
            activeIngredients = listOf("Simvastatin"),
            saltComposition = "Simvastatin IP/USP",
            category = "Antihypertensives",
            drugClass = "Antihyperlipidemic (Statin)",
            subclass = "HMG-CoA Reductase Inhibitors",
            therapeuticUses = listOf("LDL cholesterol reduction", "Cardiovascular event risk reduction"),
            strengths = listOf("10 mg", "20 mg", "40 mg"),
            dosageForms = listOf("Film-Coated Tablet"),
            route = "Oral",
            simpleMoa = "Slows cholesterol production in the liver so the liver clears more 'bad' LDL cholesterol out of the bloodstream.",
            advancedMoa = "Lactone prodrug hydrolyzed to active beta-hydroxyacid that competitively inhibits HMG-CoA reductase (rate-limiting enzyme in mevalonate synthesis), triggering SREBP-2 upregulation of hepatic LDL receptors.",
            molecularTarget = "HMG-CoA Reductase (HMGCR)",
            receptorInteraction = "Competitive catalytic pocket inhibition",
            enzymeInvolvement = "Major CYP3A4 substrate",
            biologicalPathway = "Mevalonate cholesterol biosynthesis -> Hepatic LDLR upregulation",
            physiologicalEffects = "Reduces plasma LDL-C by 30–45% and stabilizes atherosclerotic plaques.",
            commonSideEffects = listOf(
                SideEffectDetail("Mild muscle aches (myalgia)", "Aching or stiffness in large muscle groups.", "Common (2-5%)", "Low", "Report persistent or severe muscle pain to your physician.", "ACC/AHA / FDA 2026")
            ),
            seriousSideEffects = listOf(
                SideEffectDetail("Rhabdomyolysis", "Severe muscle breakdown causing dark tea-colored urine and kidney strain.", "Rare (<0.1%)", "High", "Seek urgent medical care for severe muscle weakness and dark urine.", "FDA DailyMed")
            ),
            rareSideEffects = listOf(
                SideEffectDetail("Hepatic transaminase elevation", "Reversible liver enzyme rise.", "Rare", "Moderate", "Check baseline liver panel as advised.", "BNF 2026")
            ),
            contraindications = listOf("Active liver disease", "Pregnancy and breastfeeding", "Strong CYP3A4 inhibitors (e.g., clarithromycin)"),
            precautions = listOf("Avoid large amounts of grapefruit juice", "Limit dose to 20 mg when combined with amlodipine"),
            keyInteractions = listOf("Amlodipine (MODERATE — increases simvastatin exposure)", "Fluconazole / Clarithromycin (HIGH — myopathy risk)"),
            storageInfo = "Store at 15–30°C tightly closed.",
            manufacturerInfo = "Sun Pharma (Simvotin), Organon (Zocor), Cipla (Simcard).",
            referenceSources = "ACC/AHA Cholesterol Guideline, FDA DailyMed, BNF",
            lastUpdated = "Nov 2026",
            halfLifeHours = "2–3 hours",
            bioavailability = "<5% active systemic",
            metabolism = "Hepatic CYP3A4"
        )
    ) + catalogMedicinesPartA + catalogMedicinesPartB + catalogMedicinesPartC

    val brandAlternatives: List<BrandAlternative> = listOf(
        // Paracetamol exact matches
        BrandAlternative("Dolo 650", "med_paracetamol", "Paracetamol 650 mg", "650 mg", "Tablet", "Micro Labs Ltd.", true, "Standard immediate-release uncoated tablet", "Identical active salt & strength (650 mg)."),
        BrandAlternative("Calpol 650", "med_paracetamol", "Paracetamol 650 mg", "650 mg", "Tablet", "GSK Pharmaceuticals", true, "Film-coated immediate-release tablet", "Film coating may ease swallowing; identical 650 mg salt."),
        BrandAlternative("Crocin 650", "med_paracetamol", "Paracetamol 650 mg", "650 mg", "Tablet", "Haleon", true, "Optizorb rapid-release formulation", "Excipient release profile differs slightly; verify with pharmacist."),
        BrandAlternative("Tylenol Regular", "med_paracetamol", "Acetaminophen 500 mg", "500 mg", "Caplet", "Kenvue", false, "Lower 500 mg strength per unit", "Different strength (500 mg vs 650 mg) — do not auto-substitute."),
        BrandAlternative("Brufen 400", "med_ibuprofen", "Ibuprofen 400 mg", "400 mg", "Tablet", "Abbott", false, "NSAID analgesic/antipyretic", "DIFFERENT active ingredient (Ibuprofen NSAID) — similar symptom category only; different GI/renal risk profile."),
        // Metformin exact & therapeutic
        BrandAlternative("Glycomet 500", "med_metformin", "Metformin HCl 500 mg", "500 mg", "Tablet", "USV Pvt. Ltd.", true, "Immediate-release oral tablet", "Exact salt & strength match."),
        BrandAlternative("Glucophage 500", "med_metformin", "Metformin HCl 500 mg", "500 mg", "Tablet", "Merck", true, "Reference innovator brand", "Exact salt & strength match."),
        BrandAlternative("Obimet SR 500", "med_metformin", "Metformin HCl 500 mg (Sustained Release)", "500 mg", "SR Tablet", "Abbott", false, "Sustained-release matrix tablet", "Different release kinetics (SR vs IR) — requires prescriber verification."),
        // Amoxicillin
        BrandAlternative("Mox 500", "med_amoxicillin", "Amoxicillin Trihydrate 500 mg", "500 mg", "Capsule", "Sun Pharma", true, "Hard gelatin capsule", "Exact active ingredient & strength match."),
        BrandAlternative("Novamox 500", "med_amoxicillin", "Amoxicillin Trihydrate 500 mg", "500 mg", "Capsule", "Cipla", true, "Hard gelatin capsule", "Exact active ingredient & strength match."),
        BrandAlternative("Azithral 500", "med_azithromycin", "Azithromycin 500 mg", "500 mg", "Tablet", "Alembic", false, "Macrolide antibiotic", "DIFFERENT antibiotic class & spectrum — never substitute without prescriber."),
        // Amlodipine
        BrandAlternative("Amlong 5", "med_amlodipine", "Amlodipine Besylate 5 mg", "5 mg", "Tablet", "Micro Labs", true, "Scored oral tablet", "Exact salt & strength match."),
        BrandAlternative("Norvasc 5", "med_amlodipine", "Amlodipine Besylate 5 mg", "5 mg", "Tablet", "Viatris", true, "Reference brand tablet", "Exact salt & strength match."),
        BrandAlternative("Stamlo 5", "med_amlodipine", "Amlodipine Besylate 5 mg", "5 mg", "Tablet", "Dr. Reddy's", true, "Oral tablet", "Exact salt & strength match."),
        BrandAlternative("Listril 5", "med_lisinopril", "Lisinopril 5 mg", "5 mg", "Tablet", "Torrent", false, "ACE Inhibitor antihypertensive", "DIFFERENT drug class (ACEi vs CCB) — similar therapeutic category only."),
        // Omeprazole
        BrandAlternative("Omez 20", "med_omeprazole", "Omeprazole 20 mg", "20 mg", "Delayed-Release Capsule", "Dr. Reddy's", true, "Enteric-coated pellets in capsule", "Exact active ingredient & strength match."),
        BrandAlternative("Prilosec 20", "med_omeprazole", "Omeprazole 20 mg", "20 mg", "Delayed-Release Tablet", "P&G", true, "Delayed-release tablet", "Different dosage form (tablet vs capsule), same 20 mg active salt."),
        // Cetirizine
        BrandAlternative("Cetzine 10", "med_cetirizine", "Cetirizine HCl 10 mg", "10 mg", "Tablet", "GSK", true, "Film-coated tablet", "Exact match."),
        BrandAlternative("Zyrtec 10", "med_cetirizine", "Cetirizine HCl 10 mg", "10 mg", "Tablet", "Kenvue", true, "Film-coated tablet", "Exact match."),
        BrandAlternative("Okacet 10", "med_cetirizine", "Cetirizine HCl 10 mg", "10 mg", "Tablet", "Cipla", true, "Oral tablet", "Exact match.")
    )

    val interactions: List<DrugInteractionRecord> = listOf(
        DrugInteractionRecord(
            drugAId = "med_warfarin",
            drugBId = "med_aspirin",
            severity = InteractionSeverity.HIGH,
            description = "Concurrent warfarin and aspirin significantly elevates the risk of severe gastrointestinal and systemic bleeding via additive anticoagulant and irreversible antiplatelet mechanisms.",
            clinicalEffects = "Increased mucosal bleeding, ecchymosis, GI ulcer hemorrhage, and elevated risk of major bleeding events.",
            supportingEvidence = "Established clinical evidence (CHEST Guidelines, FDA Warfarin Boxed Warning, Multiple RCTs).",
            precautions = "Avoid unsupervised combination. If prescribed together by a cardiologist (e.g., mechanical valve), strict INR monitoring and gastroprotection are typically required.",
            source = "FDA DailyMed / BNF Interaction Database",
            lastUpdated = "Nov 2026"
        ),
        DrugInteractionRecord(
            drugAId = "med_warfarin",
            drugBId = "med_ibuprofen",
            severity = InteractionSeverity.HIGH,
            description = "Ibuprofen inhibits platelet function, erodes gastric mucosa, and can displace/inhibit CYP2C9 warfarin clearance, compounding hemorrhage risk.",
            clinicalEffects = "3-to-4x elevated risk of upper GI bleeding and unpredictable INR spikes.",
            supportingEvidence = "High-grade clinical cohort & pharmacovigilance evidence (FDA / BNF).",
            precautions = "Avoid OTC NSAIDs while on warfarin unless explicitly approved by your anticoagulation clinic.",
            source = "BNF / Lexicomp Clinical Monograph",
            lastUpdated = "Nov 2026"
        ),
        DrugInteractionRecord(
            drugAId = "med_omeprazole",
            drugBId = "med_clopidogrel",
            severity = InteractionSeverity.HIGH,
            description = "Omeprazole strongly inhibits CYP2C19, the hepatic enzyme required to convert clopidogrel into its active antiplatelet metabolite.",
            clinicalEffects = "Substantially reduced antiplatelet protection and increased risk of cardiovascular thrombosis or stent occlusion.",
            supportingEvidence = "FDA Boxed Warning & pharmacokinetic crossover trials showing ~45% reduction in active clopidogrel metabolite.",
            precautions = "Consult prescribing cardiologist/pharmacist; clinicians often select a PPI with minimal CYP2C19 inhibition (such as pantoprazole) when gastroprotection is required.",
            source = "FDA Safety Communication / ACC-AHA",
            lastUpdated = "Nov 2026"
        ),
        DrugInteractionRecord(
            drugAId = "med_warfarin",
            drugBId = "med_fluconazole",
            severity = InteractionSeverity.HIGH,
            description = "Fluconazole potently inhibits CYP2C9 metabolism of the active S-warfarin enantiomer, rapidly raising blood warfarin levels.",
            clinicalEffects = "Marked INR prolongation within 2–5 days and high risk of serious bleeding.",
            supportingEvidence = "Established pharmacokinetic and clinical case-control studies.",
            precautions = "Requires proactive clinician review, close INR monitoring, and potential temporary warfarin dose supervision.",
            source = "FDA DailyMed / BNF",
            lastUpdated = "Nov 2026"
        ),
        DrugInteractionRecord(
            drugAId = "med_fluconazole",
            drugBId = "med_simvastatin",
            severity = InteractionSeverity.HIGH,
            description = "Fluconazole inhibits CYP3A4-mediated breakdown of simvastatin, causing markedly elevated statin blood concentrations.",
            clinicalEffects = "Increased risk of severe myopathy and rhabdomyolysis.",
            supportingEvidence = "Established CYP3A4 pharmacokinetic evidence & FDA statin labeling.",
            precautions = "Consult physician before combining; temporary statin hold or alternative agent is often evaluated by clinicians.",
            source = "FDA Statin Safety Guidance / BNF",
            lastUpdated = "Nov 2026"
        ),
        DrugInteractionRecord(
            drugAId = "med_ibuprofen",
            drugBId = "med_lisinopril",
            severity = InteractionSeverity.MODERATE,
            description = "NSAIDs like ibuprofen blunt prostaglandin-mediated renal vasodilation, reducing lisinopril's blood-pressure-lowering effect and straining kidney perfusion.",
            clinicalEffects = "Elevated blood pressure, fluid retention, and increased risk of acute kidney injury (especially if dehydrated or on a diuretic).",
            supportingEvidence = "Well-documented clinical pharmacology evidence (ACC/AHA, KDIGO).",
            precautions = "Monitor blood pressure and renal function; discuss non-NSAID pain relief options with a pharmacist.",
            source = "KDIGO / BNF Reference",
            lastUpdated = "Nov 2026"
        ),
        DrugInteractionRecord(
            drugAId = "med_amlodipine",
            drugBId = "med_simvastatin",
            severity = InteractionSeverity.MODERATE,
            description = "Amlodipine modestly inhibits CYP3A4 metabolism of simvastatin, increasing simvastatin systemic exposure.",
            clinicalEffects = "Higher likelihood of statin-associated muscle pain (myalgia) or myopathy at higher simvastatin doses.",
            supportingEvidence = "FDA Drug Safety Communication & clinical pharmacokinetic trials.",
            precautions = "FDA labeling advises not exceeding simvastatin 20 mg daily when taken with amlodipine; confirm dose with your doctor.",
            source = "FDA Drug Safety Communication",
            lastUpdated = "Nov 2026"
        ),
        DrugInteractionRecord(
            drugAId = "med_ibuprofen",
            drugBId = "med_aspirin",
            severity = InteractionSeverity.MODERATE,
            description = "Ibuprofen can competitively occupy the platelet COX-1 binding site, interfering with low-dose aspirin's irreversible antiplatelet effect while increasing GI irritation.",
            clinicalEffects = "Potential attenuation of cardioprotective antiplatelet action and higher stomach ulcer risk.",
            supportingEvidence = "FDA Science Paper & platelet aggregation trials.",
            precautions = "Discuss analgesic alternatives and timing separation with a licensed pharmacist.",
            source = "FDA DailyMed / ACC",
            lastUpdated = "Nov 2026"
        ),
        DrugInteractionRecord(
            drugAId = "med_warfarin",
            drugBId = "med_paracetamol",
            severity = InteractionSeverity.MODERATE,
            description = "Regular high-dose paracetamol (>2 g/day for several consecutive days) may enhance warfarin's anticoagulant effect via vitamin K-dependent factor inhibition.",
            clinicalEffects = "Gradual INR rise after 3–5 days of frequent use.",
            supportingEvidence = "Randomized double-blind placebo-controlled trials.",
            precautions = "Occasional standard doses are usually better tolerated than NSAIDs, but report multi-day paracetamol use to your anticoagulation provider.",
            source = "BNF / CHEST Guidelines",
            lastUpdated = "Nov 2026"
        ),
        DrugInteractionRecord(
            drugAId = "med_fluoxetine",
            drugBId = "med_ibuprofen",
            severity = InteractionSeverity.MODERATE,
            description = "SSRIs deplete platelet serotonin stores needed for clot aggregation; combining with NSAIDs additively increases upper GI bleeding risk.",
            clinicalEffects = "Increased risk of gastric mucosal bleeding and bruising.",
            supportingEvidence = "Meta-analyses of observational clinical cohorts.",
            precautions = "Discuss gastroprotection or alternative pain strategies with your clinician.",
            source = "BNF / FDA DailyMed",
            lastUpdated = "Nov 2026"
        ),
        DrugInteractionRecord(
            drugAId = "med_azithromycin",
            drugBId = "med_fluconazole",
            severity = InteractionSeverity.MODERATE,
            description = "Both azithromycin and fluconazole can prolong cardiac ventricular repolarization (QTc interval).",
            clinicalEffects = "Additive QT interval prolongation, particularly in patients with electrolyte imbalance or cardiac history.",
            supportingEvidence = "CredibleMeds QT database & pharmacovigilance data.",
            precautions = "Verify necessity with prescriber and report palpitations or dizziness immediately.",
            source = "CredibleMeds / FDA",
            lastUpdated = "Nov 2026"
        ),
        DrugInteractionRecord(
            drugAId = "med_warfarin",
            drugBId = "med_amoxicillin",
            severity = InteractionSeverity.LOW,
            description = "Broad-spectrum antibiotics may alter intestinal flora producing Vitamin K and occasionally increase INR sensitivity during acute infection.",
            clinicalEffects = "Mild-to-moderate transient INR elevation in a subset of patients.",
            supportingEvidence = "Clinical pharmacovigilance cohorts.",
            precautions = "Monitor for unusual bruising and consider an extra INR check during antibiotic therapy.",
            source = "BNF Reference",
            lastUpdated = "Nov 2026"
        )
    )

    fun checkInteractions(selectedIds: List<String>): List<DrugInteractionRecord> {
        val unique = selectedIds.distinct()
        val found = mutableListOf<DrugInteractionRecord>()
        for (i in unique.indices) {
            for (j in i + 1 until unique.size) {
                val a = unique[i]
                val b = unique[j]
                val match = interactions.firstOrNull {
                    (it.drugAId == a && it.drugBId == b) || (it.drugAId == b && it.drugBId == a)
                }
                if (match != null) found.add(match)
            }
        }
        return found.sortedBy { it.severity.ordinal }
    }

    fun findById(id: String): MedicineMonograph? = medicines.firstOrNull { it.id == id }

    fun searchMedicines(query: String): List<MedicineMonograph> {
        val q = query.trim().lowercase()
        if (q.isEmpty()) return medicines
        return medicines.filter { med ->
            med.genericName.lowercase().contains(q) ||
                med.category.lowercase().contains(q) ||
                med.drugClass.lowercase().contains(q) ||
                med.brandNames.any { it.lowercase().contains(q) } ||
                med.activeIngredients.any { it.lowercase().contains(q) }
        }
    }

    val drugClasses: List<DrugClassNode> = listOf(
        DrugClassNode(
            name = "Antibiotics",
            definition = "Antimicrobial agents that inhibit bacterial growth (bacteriostatic) or destroy susceptible bacteria (bactericidal).",
            pharmacologicalAction = "Target bacterial cell wall synthesis, protein synthesis (30S/50S ribosomes), nucleic acid replication, or folate pathways.",
            therapeuticApplications = listOf("Bacterial respiratory infections", "Urinary tract infections", "Skin & soft-tissue bacterial infections"),
            mechanismSummary = "Exploit structural differences between prokaryotic bacterial cells and human eukaryotic cells.",
            commonAdverseEffects = listOf("GI upset/diarrhea", "Microbiome disruption", "Hypersensitivity rashes"),
            precautions = listOf("Ineffective against viral illnesses", "Complete prescribed duration to minimize antimicrobial resistance"),
            subclasses = mapOf(
                "Beta-Lactams (Aminopenicillins)" to listOf("med_amoxicillin"),
                "Macrolides / Azalides" to listOf("med_azithromycin")
            )
        ),
        DrugClassNode(
            name = "Analgesics & Antipyretics",
            definition = "Medicines formulated to relieve pain signals and lower elevated body temperature.",
            pharmacologicalAction = "Modulate peripheral and central prostaglandin synthesis and nociceptive signaling.",
            therapeuticApplications = listOf("Headache & fever", "Musculoskeletal pain", "Inflammatory joint conditions"),
            mechanismSummary = "Inhibit COX-1/COX-2 enzymes peripherally (NSAIDs) or modulate central COX/serotonergic pathways (Paracetamol).",
            commonAdverseEffects = listOf("GI irritation (NSAIDs)", "Hepatic risk on excess dose (Paracetamol)"),
            precautions = listOf("Check combination products for duplicate active ingredients", "Review kidney and stomach ulcer history before NSAID use"),
            subclasses = mapOf(
                "Para-aminophenol Derivatives" to listOf("med_paracetamol"),
                "Propionic Acid NSAIDs" to listOf("med_ibuprofen"),
                "Salicylates" to listOf("med_aspirin")
            )
        ),
        DrugClassNode(
            name = "Antihypertensives",
            definition = "Cardiovascular therapies that reduce systemic arterial blood pressure and protect target organs.",
            pharmacologicalAction = "Dilate arterial smooth muscle, modulate the Renin-Angiotensin-Aldosterone System (RAAS), or reduce cardiac output.",
            therapeuticApplications = listOf("Essential hypertension", "Heart failure", "Cardiovascular & renal protection"),
            mechanismSummary = "Lower systemic vascular resistance via calcium channel blockade or ACE inhibition.",
            commonAdverseEffects = listOf("Dizziness on standing", "Ankle swelling (CCBs)", "Dry cough (ACE inhibitors)"),
            precautions = listOf("Do not abruptly stop therapy", "ACE inhibitors are contraindicated in pregnancy"),
            subclasses = mapOf(
                "Dihydropyridine Calcium Channel Blockers" to listOf("med_amlodipine"),
                "ACE Inhibitors" to listOf("med_lisinopril"),
                "HMG-CoA Reductase Inhibitors (Cardiometabolic Adjunct)" to listOf("med_simvastatin")
            )
        ),
        DrugClassNode(
            name = "Antidiabetics",
            definition = "Glucose-lowering agents supporting glycemic control in diabetes mellitus.",
            pharmacologicalAction = "Suppress hepatic gluconeogenesis, improve peripheral insulin sensitivity, or enhance incretin/insulin pathways.",
            therapeuticApplications = listOf("Type 2 diabetes mellitus", "Prediabetes / insulin resistance (clinician-guided)"),
            mechanismSummary = "Activate hepatic AMPK signaling to decrease excess fasting glucose output.",
            commonAdverseEffects = listOf("Early GI discomfort", "Long-term Vitamin B12 decline (Metformin)"),
            precautions = listOf("Monitor kidney function (eGFR) and HbA1c regularly"),
            subclasses = mapOf(
                "Biguanides" to listOf("med_metformin")
            )
        ),
        DrugClassNode(
            name = "Antacids & Acid Suppressants",
            definition = "Agents that neutralize existing gastric acid or suppress parietal cell acid production.",
            pharmacologicalAction = "Irreversibly inhibit gastric H⁺/K⁺-ATPase proton pumps or buffer luminal HCl.",
            therapeuticApplications = listOf("GERD & acid reflux", "Peptic ulcer healing", "NSAID gastroprotection"),
            mechanismSummary = "Block the final common step of stomach acid secretion.",
            commonAdverseEffects = listOf("Mild headache", "Long-term magnesium/B12 depletion"),
            precautions = listOf("Take PPIs 30–60 minutes before breakfast; check CYP2C19 drug interactions"),
            subclasses = mapOf(
                "Proton Pump Inhibitors (PPIs)" to listOf("med_omeprazole")
            )
        ),
        DrugClassNode(
            name = "Antihistamines",
            definition = "Histamine H1-receptor antagonists that relieve allergic symptoms.",
            pharmacologicalAction = "Stabilize H1 receptors in their inactive conformation on endothelial and nerve cells.",
            therapeuticApplications = listOf("Allergic rhinitis", "Urticaria (hives)", "Allergic conjunctivitis"),
            mechanismSummary = "Block peripheral histamine-triggered sneezing, itching, and swelling.",
            commonAdverseEffects = listOf("Mild drowsiness", "Dry mouth"),
            precautions = listOf("Assess sedation before driving; avoid combining with alcohol"),
            subclasses = mapOf(
                "2nd-Generation Piperazines" to listOf("med_cetirizine")
            )
        ),
        DrugClassNode(
            name = "Antidepressants",
            definition = "Neuropsychiatric medications regulating monoamine neurotransmission for mood and anxiety disorders.",
            pharmacologicalAction = "Inhibit presynaptic reuptake of serotonin (SSRIs) and/or norepinephrine.",
            therapeuticApplications = listOf("Major depressive disorder", "OCD", "Generalized anxiety & panic disorder"),
            mechanismSummary = "Increase synaptic serotonin availability and promote downstream neuroplasticity.",
            commonAdverseEffects = listOf("Sleep changes", "Mild nausea", "Dry mouth"),
            precautions = listOf("Allow 2–6 weeks for therapeutic effect; taper gradually under medical supervision"),
            subclasses = mapOf(
                "Selective Serotonin Reuptake Inhibitors (SSRIs)" to listOf("med_fluoxetine")
            )
        ),
        DrugClassNode(
            name = "Anticoagulants & Antiplatelets",
            definition = "Antithrombotic medicines that prevent pathological blood clot formation inside vessels.",
            pharmacologicalAction = "Inhibit hepatic clotting factor synthesis (VKAs) or block platelet aggregation receptors (P2Y12 / COX-1).",
            therapeuticApplications = listOf("Atrial fibrillation stroke prevention", "DVT/PE", "Post-stent arterial protection"),
            mechanismSummary = "Interrupt coagulation cascade amplification or platelet cross-linking.",
            commonAdverseEffects = listOf("Easy bruising", "Bleeding gums", "Major hemorrhage risk"),
            precautions = listOf("Always check interactions before taking any OTC medicine or supplement"),
            subclasses = mapOf(
                "Vitamin K Antagonists" to listOf("med_warfarin"),
                "P2Y12 Antiplatelets" to listOf("med_clopidogrel")
            )
        ),
        DrugClassNode(
            name = "Antifungals",
            definition = "Agents treating superficial and systemic fungal infections.",
            pharmacologicalAction = "Disrupt fungal cell membrane ergosterol synthesis.",
            therapeuticApplications = listOf("Candidiasis", "Dermatophytosis", "Systemic mycoses"),
            mechanismSummary = "Inhibit fungal CYP51 lanosterol 14-alpha-demethylase.",
            commonAdverseEffects = listOf("Mild GI upset", "Liver enzyme elevation"),
            precautions = listOf("Screen CYP2C9/CYP3A4 drug interactions prior to use"),
            subclasses = mapOf(
                "Triazoles" to listOf("med_fluconazole")
            )
        ),
        DrugClassNode(
            name = "Vitamins & Micronutrients",
            definition = "Essential biological cofactors and pro-hormones required for metabolic and skeletal health.",
            pharmacologicalAction = "Bind nuclear receptors or serve as enzymatic cofactors.",
            therapeuticApplications = listOf("Documented deficiency states", "Bone health support"),
            mechanismSummary = "Regulate calcium transport and cellular homeostasis.",
            commonAdverseEffects = listOf("Toxicity only with prolonged excess dosing"),
            precautions = listOf("Follow lab-guided replacement schedules"),
            subclasses = mapOf(
                "Fat-Soluble Secosteroids" to listOf("med_vitamind3")
            )
        )
    )

    val healthConditions: List<HealthConditionGuide> = listOf(
        HealthConditionGuide(
            id = "cond_hypertension",
            name = "Hypertension (High Blood Pressure)",
            category = "Cardiovascular",
            definition = "A chronic condition in which the force of blood against arterial walls remains persistently elevated (typically >=130/80 mmHg per ACC/AHA).",
            types = listOf("Primary (Essential) Hypertension", "Secondary Hypertension (renal, endocrine, or medication-related)", "Gestational Hypertension"),
            symptoms = listOf("Often asymptomatic ('silent condition')", "Occasional morning headache or visual blurring in severe elevations"),
            causes = listOf("Complex interplay of genetics, arterial stiffness, sodium sensitivity, and RAAS activation"),
            riskFactors = listOf("High sodium diet", "Sedentary lifestyle", "Family history", "Chronic kidney disease", "Obesity & sleep apnea"),
            complications = listOf("Coronary artery disease", "Stroke", "Heart failure", "Chronic kidney disease", "Hypertensive retinopathy"),
            diagnostics = listOf("Repeated resting clinic BP readings", "24-hour Ambulatory BP Monitoring (ABPM)", "Home BP log, ECG, renal & lipid panel"),
            treatmentInfo = "Lifestyle foundations (DASH nutrition, regular aerobic exercise, sodium moderation) combined with clinician-prescribed first-line classes such as ACE inhibitors, ARBs, CCBs, or thiazide-like diuretics.",
            prevention = listOf("Keep dietary sodium <2,300 mg/day", "150 min/week moderate physical activity", "Regular home BP screening"),
            seekCareWhen = listOf("BP >=180/120 mmHg with chest pain, shortness of breath, neurological weakness, or severe headache — seek emergency care immediately.")
        ),
        HealthConditionGuide(
            id = "cond_asthma",
            name = "Bronchial Asthma",
            category = "Respiratory",
            definition = "A chronic inflammatory disorder of the airways characterized by reversible airflow narrowing and airway hyperresponsiveness.",
            types = listOf("Allergic (Atopic) Asthma", "Non-Allergic Asthma", "Exercise-Induced Bronchoconstriction", "Occupational Asthma"),
            symptoms = listOf("Recurrent wheezing", "Shortness of breath", "Chest tightness", "Nighttime or early-morning cough"),
            causes = listOf("Chronic eosinophilic/Th2 airway inflammation triggered by allergens, viral infections, cold air, or pollutants"),
            riskFactors = listOf("Atopy/eczema/allergic rhinitis", "Family history", "Urban air pollution & tobacco smoke"),
            complications = listOf("Severe asthma exacerbations", "Airway remodeling if uncontrolled"),
            diagnostics = listOf("Spirometry with bronchodilator reversibility", "Peak Expiratory Flow (PEF) tracking", "FeNO airway inflammation test"),
            treatmentInfo = "Modern GINA guidelines prioritize inhaled anti-inflammatory controller therapy (ICS-formoterol) alongside trigger avoidance and a written Asthma Action Plan.",
            prevention = listOf("Identify and minimize dust/pollen/smoke triggers", "Verify proper inhaler technique with a pharmacist", "Stay updated on respiratory vaccines"),
            seekCareWhen = listOf("Difficulty speaking full sentences, bluish lips, or rescue inhaler failing to relieve breathlessness — seek emergency care.")
        ),
        HealthConditionGuide(
            id = "cond_gerd",
            name = "GERD (Gastroesophageal Reflux Disease)",
            category = "Gastrointestinal",
            definition = "Chronic backward flow of stomach acid into the esophagus causing troublesome symptoms or mucosal irritation.",
            types = listOf("Non-Erosive Reflux Disease (NERD)", "Erosive Esophagitis", "Laryngopharyngeal Reflux"),
            symptoms = listOf("Retrosternal heartburn", "Sour acid regurgitation", "Worse after large meals or lying flat"),
            causes = listOf("Transient relaxation of the lower esophageal sphincter (LES) or hiatal hernia"),
            riskFactors = listOf("Late-night heavy meals", "Elevated abdominal pressure/obesity", "Smoking, caffeine, and alcohol"),
            complications = listOf("Esophageal erosions", "Peptic stricture", "Barrett's esophagus"),
            diagnostics = listOf("Clinical history", "Upper GI endoscopy (for alarm signs)", "24-hour esophageal pH monitoring"),
            treatmentInfo = "Meal timing adjustments, head-of-bed elevation, antacids/alginates for intermittent symptoms, and clinician-guided H2 blockers or PPIs when indicated.",
            prevention = listOf("Avoid lying down within 3 hours after eating", "Smaller evening meals", "Maintain healthy weight"),
            seekCareWhen = listOf("Difficulty or pain swallowing, unintended weight loss, vomiting blood, or chest pain mimicking cardiac ischemia.")
        ),
        HealthConditionGuide(
            id = "cond_migraine",
            name = "Migraine Disorder",
            category = "Neurological",
            definition = "A primary neurological headache disorder involving recurrent throbbing headaches often paired with sensory sensitivity.",
            types = listOf("Migraine without Aura", "Migraine with Visual/Sensory Aura", "Chronic Migraine (>=15 headache days/month)"),
            symptoms = listOf("Unilateral pulsing headache", "Nausea", "Photophobia (light sensitivity) & phonophobia (sound sensitivity)"),
            causes = listOf("Trigeminovascular activation and release of neuropeptides such as CGRP"),
            riskFactors = listOf("Family predisposition", "Sleep irregularity", "Dehydration, hormonal shifts, stress"),
            complications = listOf("Medication-overuse headache from frequent acute painkiller use"),
            diagnostics = listOf("Neurological clinical criteria (ICHD-3)", "Headache diary", "Neuroimaging only if red-flag features exist"),
            treatmentInfo = "Acute clinician-guided options (NSAIDs, triptans, gepants) paired with trigger tracking and preventive therapies for frequent attacks.",
            prevention = listOf("Consistent sleep and meal schedule", "Hydration", "Maintain a migraine trigger journal"),
            seekCareWhen = listOf("Sudden 'thunderclap' worst headache of life, fever/stiff neck, confusion, or weakness/slurred speech.")
        ),
        HealthConditionGuide(
            id = "cond_t2dm",
            name = "Type 2 Diabetes Mellitus",
            category = "Endocrine",
            definition = "A metabolic condition marked by progressive insulin resistance and impaired pancreatic beta-cell insulin secretion causing chronic hyperglycemia.",
            types = listOf("Prediabetes (HbA1c 5.7–6.4%)", "Type 2 Diabetes (HbA1c >=6.5%)"),
            symptoms = listOf("Increased thirst & urination", "Fatigue", "Blurred vision", "Slow-healing minor wounds", "Often silent early on"),
            causes = listOf("Peripheral insulin resistance in muscle/liver/adipose tissue plus gradual beta-cell decline"),
            riskFactors = listOf("Central adiposity", "Family history", "Sedentary routine", "Prior gestational diabetes"),
            complications = listOf("Retinopathy", "Nephropathy", "Peripheral neuropathy", "Cardiovascular disease"),
            diagnostics = listOf("HbA1c", "Fasting plasma glucose", "2-hour Oral Glucose Tolerance Test (OGTT)"),
            treatmentInfo = "Structured medical nutrition, resistance & aerobic exercise, glucose monitoring, and individualized pharmacotherapy (e.g., metformin, SGLT2i, GLP-1 RA).",
            prevention = listOf("High-fiber balanced meals", "Regular post-meal walking", "Annual screening if at risk"),
            seekCareWhen = listOf("Persistent very high blood sugar with nausea/vomiting, fruity breath, or confusion — seek urgent evaluation.")
        ),
        HealthConditionGuide(
            id = "cond_uti",
            name = "Urinary Tract Infection (UTI)",
            category = "Infectious",
            definition = "Bacterial colonization and inflammation of the urinary bladder (cystitis) or upper urinary tract/kidneys (pyelonephritis).",
            types = listOf("Uncomplicated Lower UTI (Cystitis)", "Complicated UTI", "Upper UTI (Pyelonephritis)"),
            symptoms = listOf("Burning sensation during urination", "Frequent urgent urination", "Cloudy or strong-smelling urine", "Lower pelvic discomfort"),
            causes = listOf("Ascension of uropathogenic bacteria, most commonly E. coli"),
            riskFactors = listOf("Dehydration", "Urinary tract obstruction", "Diabetes", "Pregnancy"),
            complications = listOf("Kidney infection (pyelonephritis)", "Urosepsis if untreated"),
            diagnostics = listOf("Urinalysis (leukocyte esterase/nitrites)", "Midstream urine culture & sensitivity"),
            treatmentInfo = "Targeted narrow-spectrum antibiotic therapy prescribed by a clinician based on local resistance and culture results, plus generous fluid intake.",
            prevention = listOf("Stay well hydrated", "Do not delay urination", "Avoid unnecessary broad-spectrum self-medication"),
            seekCareWhen = listOf("High fever, chills, flank/back pain, nausea/vomiting, or UTI symptoms during pregnancy.")
        ),
        HealthConditionGuide(
            id = "cond_eczema",
            name = "Atopic Dermatitis (Eczema)",
            category = "Skin",
            definition = "A chronic relapsing inflammatory skin condition featuring impaired epidermal barrier function and intense itch.",
            types = listOf("Infantile/Childhood Atopic Dermatitis", "Adult Flexural Eczema", "Hand Eczema"),
            symptoms = listOf("Dry scaly patches", "Persistent itching", "Redness or hyperpigmented plaques in skin folds"),
            causes = listOf("Skin barrier filaggrin dysfunction paired with cutaneous immune hyperreactivity"),
            riskFactors = listOf("Personal/family history of asthma or hay fever", "Dry winter climate", "Fragranced soaps"),
            complications = listOf("Secondary bacterial skin infection", "Sleep disruption"),
            diagnostics = listOf("Dermatologic clinical exam", "Patch testing if contact allergy suspected"),
            treatmentInfo = "Daily fragrance-free ceramide emollients, gentle lukewarm bathing, and clinician-directed topical anti-inflammatory creams during flares.",
            prevention = listOf("Moisturize within 3 minutes after showering", "Choose breathable cotton fabrics", "Avoid harsh detergents"),
            seekCareWhen = listOf("Spreading warmth, yellow crusting/pus, fever, or painful clustered blisters.")
        ),
        HealthConditionGuide(
            id = "cond_oa",
            name = "Osteoarthritis",
            category = "Musculoskeletal",
            definition = "A degenerative joint condition involving progressive articular cartilage wear, subchondral bone remodeling, and low-grade joint inflammation.",
            types = listOf("Primary Age-Related OA", "Post-Traumatic Secondary OA"),
            symptoms = listOf("Activity-related joint ache", "Brief morning stiffness (<30 minutes)", "Joint crepitus or reduced range of motion"),
            causes = listOf("Biomechanical joint load imbalance and cartilage matrix breakdown"),
            riskFactors = listOf("Advancing age", "Prior joint injury", "Excess joint load", "Repetitive occupational strain"),
            complications = listOf("Reduced mobility", "Muscle deconditioning"),
            diagnostics = listOf("Clinical joint assessment", "Weight-bearing joint X-ray"),
            treatmentInfo = "Targeted physical therapy, joint-strengthening exercises, weight optimization, topical NSAIDs, and clinician-guided pain management.",
            prevention = listOf("Low-impact strengthening (swimming, cycling)", "Ergonomic posture", "Maintain healthy body composition"),
            seekCareWhen = listOf("Sudden hot, swollen, intensely painful single joint with fever (requires urgent evaluation to rule out septic arthritis).")
        )
    )

    val pregnancyMilestones: List<PregnancyWeekMilestone> = listOf(
        PregnancyWeekMilestone(4, 1, "Poppy Seed", "0.2 cm", "Implantation completes and the amniotic sac and early yolk sac begin forming.", listOf("Blastocyst embeds into uterine lining", "Early hCG hormone rises", "Neural plate foundation starts"), "Focus on daily folic acid (as advised by your clinician), hydration, and gentle rest.", "Schedule your initial prenatal confirmation visit."),
        PregnancyWeekMilestone(8, 1, "Raspberry", "1.6 cm", "Major organs are taking shape and tiny limb buds and facial features are emerging.", listOf("Tiny fingers and toes begin separating", "Heart beats rhythmically", "Early brain pathways expand"), "Small frequent bland snacks and ginger or vitamin B6 (clinician-approved) may ease morning nausea.", "First prenatal lab panel & dating ultrasound window."),
        PregnancyWeekMilestone(12, 1, "Lime", "5.4 cm", "End of the first trimester! All vital organ systems are formed and continue maturing.", listOf("Reflexive tiny movements begin", "Fingernails start developing", "Placenta assumes primary nourishment"), "Energy levels often begin improving as first-trimester nausea settles.", "First-trimester screening & nuchal translucency discussion."),
        PregnancyWeekMilestone(16, 2, "Avocado", "11.6 cm", "Skeletal hardening continues and facial muscles can make subtle expressions.", listOf("Ears move into final position", "Fine lanugo hair develops", "Limbs lengthen proportionally"), "Sleep on your side comfortably with pillow support and maintain iron-rich meals.", "Routine mid-second-trimester prenatal checkup."),
        PregnancyWeekMilestone(20, 2, "Banana", "25.6 cm", "Halfway milestone! Swallowing amniotic fluid practices digestive and lung coordination.", listOf("Vernix caseosa protects delicate skin", "Auditory structures begin sensing muffled sounds", "First fluttering movements ('quickening') often felt"), "Stay active with gentle prenatal stretching and stay well hydrated.", "Detailed 20-week anatomy ultrasound scan."),
        PregnancyWeekMilestone(24, 2, "Ear of Corn", "30.0 cm", "Lungs develop microscopic branches and surfactant-producing cells begin maturing.", listOf("Distinct sleep-wake cycles emerge", "Taste buds form", "Footprints & fingerprints are set"), "Monitor for swelling or headaches and enjoy balanced protein & calcium intake.", "Gestational diabetes glucose screening window (24–28 weeks)."),
        PregnancyWeekMilestone(28, 3, "Eggplant", "37.6 cm", "Welcome to the third trimester! Eyelids can open and brain folds deepen rapidly.", listOf("REM sleep cycles begin", "Subcutaneous fat accumulates", "Responds to familiar voices"), "Begin noting daily baby movement patterns as advised by your OB/midwife.", "Tdap vaccination discussion & third-trimester blood count/Rh check."),
        PregnancyWeekMilestone(32, 3, "Squash", "42.4 cm", "Rapid weight gain prepares the baby for temperature regulation after birth.", listOf("Toenails and hair continue growing", "Lungs mature further", "Practice breathing motions intensify"), "Frequent smaller meals help relieve heartburn; elevate feet when resting.", "Bi-weekly prenatal visits typically begin."),
        PregnancyWeekMilestone(36, 3, "Papaya", "47.4 cm", "Baby typically settles head-down into the pelvis and gains ~200g per week.", listOf("Sucking muscles strengthen for feeding", "Circulation and immune transfer peak", "Soft skull bones stay flexible for delivery"), "Prepare hospital essentials and review labor signs with your birth team.", "Group B Strep (GBS) screening & weekly checkups start."),
        PregnancyWeekMilestone(40, 3, "Watermelon", "51.0 cm", "Full term! Organs, lungs, and reflexes are ready for life outside the womb.", listOf("Full newborn grasp and rooting reflexes", "Steady daily movements continue", "Ready for spontaneous or clinician-guided birth"), "Rest when possible and stay attentive to regular contractions or fluid leakage.", "Weekly fetal well-being review & delivery coordination.")
    )

    val labParameters: List<LabTestParameter> = listOf(
        LabTestParameter("lab_hb", "CBC", "Hemoglobin (Hb)", "g/dL", 12.0, 16.5, 7.0, 20.0, 11.2, listOf("Aug" to 12.4, "Sep" to 11.9, "Oct" to 11.5, "Nov" to 11.2), "Oxygen-carrying protein inside red blood cells.", "Values below reference range may be associated with iron, B12, or folate deficiency or chronic blood loss; evaluate with clinician."),
        LabTestParameter("lab_wbc", "CBC", "White Blood Cells (WBC)", "x10³/µL", 4.0, 11.0, 2.0, 30.0, 7.6, listOf("Aug" to 6.8, "Sep" to 7.1, "Oct" to 7.4, "Nov" to 7.6), "Immune defense cells responding to infection or inflammation.", "Within typical adult reference range."),
        LabTestParameter("lab_fbg", "Blood Glucose", "Fasting Blood Glucose", "mg/dL", 70.0, 99.0, 50.0, 300.0, 114.0, listOf("Aug" to 102.0, "Sep" to 108.0, "Oct" to 110.0, "Nov" to 114.0), "Blood sugar level after an 8–12 hour fast.", "Mildly elevated above fasting reference range (100–125 mg/dL often falls in impaired fasting glucose range); review with clinician alongside HbA1c."),
        LabTestParameter("lab_hba1c", "HbA1c", "Glycated Hemoglobin (HbA1c)", "%", 4.0, 5.6, null, 11.0, 6.1, listOf("Feb" to 5.7, "May" to 5.8, "Aug" to 6.0, "Nov" to 6.1), "Reflects average blood glucose levels over the past 2–3 months.", "Above typical normal reference range (<5.7%); discuss lifestyle and metabolic follow-up with your doctor."),
        LabTestParameter("lab_ldl", "Lipid Profile", "LDL Cholesterol", "mg/dL", 0.0, 100.0, null, 250.0, 138.0, listOf("May" to 148.0, "Aug" to 142.0, "Nov" to 138.0), "Low-density lipoprotein particle carrying cholesterol to arterial walls.", "Above optimal target (<100 mg/dL); clinical target depends on overall cardiovascular risk profile."),
        LabTestParameter("lab_alt", "Liver Function", "ALT (SGPT)", "U/L", 7.0, 45.0, null, 300.0, 34.0, listOf("Aug" to 38.0, "Sep" to 36.0, "Nov" to 34.0), "Enzyme primarily found in liver cells.", "Within standard adult laboratory reference range."),
        LabTestParameter("lab_creatinine", "Kidney Function", "Serum Creatinine", "mg/dL", 0.6, 1.2, null, 4.0, 0.92, listOf("Aug" to 0.90, "Sep" to 0.94, "Nov" to 0.92), "Muscle metabolism waste product filtered by the kidneys.", "Within standard adult reference range indicating stable filtration marker."),
        LabTestParameter("lab_tsh", "Thyroid Profile", "TSH (Thyroid Stimulating Hormone)", "mIU/L", 0.4, 4.2, 0.05, 20.0, 5.4, listOf("May" to 3.9, "Aug" to 4.6, "Nov" to 5.4), "Pituitary hormone that signals the thyroid gland to produce thyroid hormones.", "Mildly elevated above reference range; clinicians typically correlate with Free T4 and symptoms."),
        LabTestParameter("lab_potassium", "Electrolytes", "Serum Potassium (K⁺)", "mEq/L", 3.5, 5.1, 2.8, 6.2, 4.3, listOf("Aug" to 4.1, "Sep" to 4.2, "Nov" to 4.3), "Essential electrolyte supporting heart rhythm and nerve-muscle conduction.", "Within healthy reference interval.")
    )

    val articles: List<PharmaArticle> = listOf(
        PharmaArticle("art_1", "Why Drug-Drug Interactions Happen via CYP450 Enzymes", "Pharmacology", 4, "Learn how liver enzymes like CYP2C19 and CYP3A4 activate or clear common prescriptions — and why checking combinations protects you.", "DRxGuard Clinical Editorial"),
        PharmaArticle("art_2", "Understanding Generic vs. Brand Equivalence Safely", "Patient Safety", 3, "Active salt composition, release kinetics (IR vs. SR), and why pharmacist verification matters before substituting brands.", "WHO / FDA Patient Guide"),
        PharmaArticle("art_3", "Reading Your HbA1c & Lipid Panel Like a Pro", "Lab Literacy", 5, "How laboratory reference intervals work and which questions to bring to your next physician appointment.", "DRxGuard LabGuard Team")
    )
}
