package com.example.data

internal fun quickMed(
    id: String,
    generic: String,
    brands: List<String>,
    salts: String,
    category: String,
    drugClass: String,
    uses: List<String>,
    strengths: List<String>,
    forms: List<String>,
    moa: String,
    commonSe: List<String>,
    seriousSe: List<String>,
    precautions: List<String>,
    interactions: List<String>
): MedicineMonograph = MedicineMonograph(
    id = id,
    genericName = generic,
    brandNames = brands,
    activeIngredients = salts.split("+").map { it.trim() }.filter { it.isNotEmpty() },
    saltComposition = salts,
    category = category,
    drugClass = drugClass,
    subclass = "$category Formulary",
    therapeuticUses = uses,
    strengths = strengths,
    dosageForms = forms,
    route = if (forms.any { it.contains("Cream", true) || it.contains("Ointment", true) || it.contains("Gel", true) }) "Topical"
    else if (forms.any { it.contains("Inhaler", true) }) "Inhalation"
    else if (forms.any { it.contains("Drop", true) }) "Ophthalmic / Otic"
    else "Oral",
    simpleMoa = moa,
    advancedMoa = moa,
    molecularTarget = "$drugClass target receptor / enzyme",
    receptorInteraction = "Selective pharmacological modulation",
    enzymeInvolvement = "Hepatic / Renal clearance per label",
    biologicalPathway = "$category therapeutic pathway",
    physiologicalEffects = uses.joinToString(", "),
    commonSideEffects = commonSe.map {
        SideEffectDetail(it, "Commonly observed with $generic.", "Common (1-10%)", "Low", "Consult clinician if persistent.", "CDSCO / FDA DailyMed")
    },
    seriousSideEffects = seriousSe.map {
        SideEffectDetail(it, "Serious adverse event requiring prompt review.", "Uncommon / Rare", "High", "Seek urgent medical care if symptoms occur.", "FDA / BNF 2026")
    },
    rareSideEffects = emptyList(),
    contraindications = listOf("Known hypersensitivity to $salts or excipients."),
    precautions = precautions,
    keyInteractions = interactions,
    storageInfo = "Store below 25°C in a dry place away from sunlight and children.",
    manufacturerInfo = "Licensed pharmaceutical manufacturers (${brands.firstOrNull().orEmpty()})",
    referenceSources = "Indian Pharmacopoeia / FDA DailyMed / BNF",
    lastUpdated = "Nov 2026",
    halfLifeHours = "Per formulation label",
    bioavailability = "Per oral/topical bioavailability",
    metabolism = "Per monograph"
)

internal val catalogMedicinesPartA: List<MedicineMonograph> = listOf(
    quickMed("med_pantoprazole", "Pantoprazole", listOf("Pantop 40", "Pan 40", "Protonix"), "Pantoprazole Sodium", "Antacids", "Proton Pump Inhibitor (PPI)",
        listOf("Acid reflux (GERD)", "Gastritis & heartburn", "Peptic ulcer healing"), listOf("20 mg", "40 mg"), listOf("Gastro-Resistant Tablet"),
        "Irreversibly blocks stomach parietal cell H+/K+-ATPase proton pumps to cut acid secretion.",
        listOf("Mild headache", "Abdominal bloating", "Loose stools"), listOf("Severe hypomagnesemia (long-term)", "C. difficile diarrhea"),
        listOf("Take 30–60 minutes before breakfast."), listOf("Clopidogrel (mild)", "Methotrexate (high-dose)")),

    quickMed("med_pan_d", "Pantoprazole + Domperidone", listOf("Pan-D", "Pantocid-D", "Pantop-D SR"), "Pantoprazole 40 mg + Domperidone 30 mg", "Antacids", "PPI + Prokinetic Combination",
        listOf("Acid reflux with nausea", "Bloating and indigestion", "GERD"), listOf("40 mg / 30 mg SR"), listOf("SR Capsule"),
        "Reduces stomach acid secretion via pantoprazole while domperidone speeds gastric emptying.",
        listOf("Dry mouth", "Mild headache", "Drowsiness"), listOf("QT prolongation (high domperidone doses)"),
        listOf("Avoid in patients with known heart rhythm prolongation."), listOf("Ketoconazole", "Erythromycin", "Amiodarone")),

    quickMed("med_rabeprazole", "Rabeprazole", listOf("Rablet 20", "Razo 20", "Aciphex"), "Rabeprazole Sodium", "Antacids", "Proton Pump Inhibitor",
        listOf("GERD", "Duodenal ulcers", "Zollinger-Ellison syndrome"), listOf("10 mg", "20 mg"), listOf("Enteric-Coated Tablet"),
        "Rapidly inhibits gastric H+/K+-ATPase proton pumps with minimal CYP2C19 dependence.",
        listOf("Headache", "Nausea", "Constipation"), listOf("Vitamin B12 deficiency (prolonged use)"),
        listOf("Swallow tablet whole without crushing."), listOf("Warfarin", "Digoxin", "Ketoconazole")),

    quickMed("med_ranitidine_famotidine", "Famotidine", listOf("Famocid 20", "Pepcid", "Topcid"), "Famotidine", "Antacids", "H2 Receptor Antagonist",
        listOf("Heartburn relief", "Acid indigestion", "Gastric ulcer maintenance"), listOf("20 mg", "40 mg"), listOf("Tablet"),
        "Competitively blocks histamine H2 receptors on stomach parietal cells to lower acid output.",
        listOf("Mild dizziness", "Headache", "Constipation"), listOf("Confusion in severe renal impairment"),
        listOf("Dose adjustment needed in moderate-to-severe kidney impairment."), listOf("Dasatinib", "Cefuroxime")),

    quickMed("med_sucralfate", "Sucralfate", listOf("Sucrafil", "Carafate", "Pepsigard"), "Sucralfate", "Antacids", "Mucosal Protectant",
        listOf("Duodenal ulcer coating", "Stomach lining protection"), listOf("1 g / 10 mL", "1 g Tablet"), listOf("Suspension", "Tablet"),
        "Forms a protective viscous barrier over inflamed stomach and duodenal ulcers.",
        listOf("Constipation", "Mild dry mouth"), listOf("Aluminum accumulation in severe renal failure"),
        listOf("Space other medicines by at least 2 hours."), listOf("Fluoroquinolones", "Levothyroxine", "Phenytoin")),

    quickMed("med_digene_antacid", "Magaldrate + Simethicone", listOf("Digene", "Gelusil", "Mucaine Gel"), "Aluminum Hydroxide + Magnesium Hydroxide + Simethicone", "Antacids", "Antacid + Antifoaming Agent",
        listOf("Immediate acidity relief", "Gas and bloating", "Heartburn"), listOf("170 mL / 450 mL"), listOf("Oral Suspension", "Chewable Tablet"),
        "Directly neutralizes existing stomach acid and breaks up trapped gas bubbles.",
        listOf("Chalky taste", "Loose stools or constipation"), listOf("Electrolyte imbalance in kidney disease"),
        listOf("Take 2 hours apart from antibiotics or thyroid tablets."), listOf("Tetracyclines", "Ciprofloxacin", "Iron supplements")),

    quickMed("med_diclofenac", "Diclofenac", listOf("Voveran SR", "Reactin", "Voltaren"), "Diclofenac Sodium", "Analgesics", "NSAID Analgesic",
        listOf("Joint & arthritis pain", "Acute musculoskeletal sprains", "Dental pain"), listOf("50 mg", "75 mg", "100 mg SR", "1% Gel"), listOf("Tablet", "SR Tablet", "Topical Gel"),
        "Potently inhibits COX-1 and COX-2 enzymes to reduce inflammatory prostaglandins.",
        listOf("Acidity", "Stomach discomfort", "Nausea"), listOf("GI ulcer bleeding", "Cardiovascular thrombotic risk", "Kidney strain"),
        listOf("Take after food; use lowest effective dose for shortest duration."), listOf("Warfarin", "Aspirin", "Lisinopril", "Lithium")),

    quickMed("med_aceclofenac", "Aceclofenac", listOf("Zerodol", "Hifenac", "Aceclo"), "Aceclofenac", "Analgesics", "NSAID Anti-inflammatory",
        listOf("Osteoarthritis", "Rheumatoid arthritis", "Lower back pain"), listOf("100 mg"), listOf("Film-Coated Tablet"),
        "Preferentially suppresses COX-2 prostaglandin synthesis in inflamed joint tissues.",
        listOf("Indigestion", "Heartburn", "Mild dizziness"), listOf("Peptic ulceration", "Hepatic enzyme elevation"),
        listOf("Avoid in active stomach ulcers or severe liver/kidney disease."), listOf("Warfarin", "Methotrexate", "Diuretics")),

    quickMed("med_zerodol_sp", "Aceclofenac + Paracetamol + Serratiopeptidase", listOf("Zerodol-SP", "Hifenac-D", "Aldigesic-SP"), "Aceclofenac 100 mg + Paracetamol 325 mg + Serratiopeptidase 15 mg", "Analgesics", "NSAID + Proteolytic Combination",
        listOf("Post-surgical swelling & pain", "Dental inflammation", "Orthopedic injury"), listOf("100/325/15 mg"), listOf("Tablet"),
        "Combines anti-inflammatory COX inhibition, central analgesia, and proteolytic edema reduction.",
        listOf("Gastric acidity", "Nausea"), listOf("GI bleeding", "Hepatotoxicity if combined with other Paracetamol"),
        listOf("Do not combine with extra Paracetamol (Dolo/Crocin)."), listOf("Anticoagulants", "Other NSAIDs", "Alcohol")),

    quickMed("med_combiflam", "Ibuprofen + Paracetamol", listOf("Combiflam", "Ibugesic Plus", "Flexon"), "Ibuprofen 400 mg + Paracetamol 325 mg", "Analgesics", "Dual Analgesic / Antipyretic",
        listOf("Moderate fever", "Toothache", "Muscle and joint pain"), listOf("400 mg / 325 mg"), listOf("Tablet", "Suspension"),
        "Pairs peripheral NSAID prostaglandin blockade with central antipyretic analgesia.",
        listOf("Acidity", "Stomach upset"), listOf("GI ulceration", "Renal strain if dehydrated"),
        listOf("Always take with food; avoid exceeding 3 tablets daily without medical advice."), listOf("Warfarin", "Aspirin", "Lithium")),

    quickMed("med_naproxen", "Naproxen", listOf("Naprosyn", "Naxdom", "Aleve"), "Naproxen Sodium", "Analgesics", "Long-Acting NSAID",
        listOf("Migraine pain", "Arthritis stiffness", "Menstrual cramps"), listOf("250 mg", "500 mg"), listOf("Tablet"),
        "Inhibits COX-1/COX-2 prostaglandin synthesis with prolonged 12-hour duration.",
        listOf("Heartburn", "Mild drowsiness"), listOf("Stomach bleeding", "Renal impairment"),
        listOf("Take with meals or milk to minimize gastric irritation."), listOf("Warfarin", "SSRIs", "ACE Inhibitors")),

    quickMed("med_tramadol", "Tramadol", listOf("Ultracet", "Contramal", "Tramzac"), "Tramadol Hydrochloride", "Analgesics", "Centrally Acting Opioid Analgesic",
        listOf("Moderate-to-severe acute pain", "Post-operative pain"), listOf("50 mg", "37.5 mg + 325 mg"), listOf("Capsule", "Tablet"),
        "Weakly activates mu-opioid receptors while inhibiting serotonin and norepinephrine reuptake.",
        listOf("Drowsiness", "Nausea", "Constipation", "Dizziness"), listOf("Respiratory depression", "Serotonin syndrome", "Seizure risk"),
        listOf("Prescription-only; avoid driving or alcohol while taking."), listOf("SSRIs", "MAOIs", "Benzodiazepines", "Alcohol")),

    quickMed("med_mefenamic", "Mefenamic Acid", listOf("Meftal Spas", "Meftal 500", "Ponstan"), "Mefenamic Acid", "Analgesics", "Fenamate NSAID",
        listOf("Menstrual cramps (dysmenorrhea)", "Spasmodic abdominal pain", "Fever"), listOf("250 mg", "500 mg"), listOf("Tablet", "Suspension"),
        "Inhibits prostaglandin synthesis and directly antagonizes prostaglandin receptors.",
        listOf("Stomach upset", "Nausea", "Mild diarrhea"), listOf("GI bleeding", "Renal papillary strain"),
        listOf("Limit use to short courses (typically under 7 days) after food."), listOf("Warfarin", "Lithium", "Aspirin")),

    quickMed("med_augmentin", "Amoxicillin + Clavulanic Acid", listOf("Augmentin 625 Duo", "Moxikind-CV", "Clavam 625"), "Amoxicillin 500 mg + Clavulanate 125 mg", "Antibiotics", "Beta-Lactam / Beta-Lactamase Inhibitor",
        listOf("Sinus & throat bacterial infections", "Dental abscess", "Skin & urinary infections"), listOf("375 mg", "625 mg", "1000 mg"), listOf("Tablet", "Dry Syrup"),
        "Amoxicillin disrupts bacterial cell walls while clavulanate blocks resistance enzymes.",
        listOf("Loose stools", "Nausea", "Oral/vaginal thrush"), listOf("Penicillin anaphylaxis", "Cholestatic hepatitis (rare)"),
        listOf("Take at the start of a meal to reduce stomach upset; finish prescribed course."), listOf("Warfarin", "Allopurinol", "Methotrexate")),

    quickMed("med_cefixime", "Cefixime", listOf("Taxim-O 200", "Zifi 200", "Mahacef"), "Cefixime Trihydrate", "Antibiotics", "3rd-Gen Cephalosporin",
        listOf("Typhoid fever", "Urinary tract infection", "Respiratory bacterial infection"), listOf("100 mg", "200 mg"), listOf("Tablet", "DT", "Suspension"),
        "Inhibits bacterial cell wall peptidoglycan cross-linking via PBP binding.",
        listOf("Mild diarrhea", "Abdominal discomfort"), listOf("Severe hypersensitivity in beta-lactam allergy", "C. difficile colitis"),
        listOf("Inform doctor of any past penicillin or cephalosporin allergy."), listOf("Warfarin", "Probenecid")),

    quickMed("med_ciprofloxacin", "Ciprofloxacin", listOf("Ciplox 500", "Cifran 500", "Cipro"), "Ciprofloxacin HCl", "Antibiotics", "Fluoroquinolone Antibiotic",
        listOf("Complicated UTI", "Bacterial gastroenteritis", "Bone/joint infection"), listOf("250 mg", "500 mg", "0.3% Drops"), listOf("Tablet", "Eye/Ear Drops"),
        "Inhibits bacterial DNA gyrase and topoisomerase IV to halt DNA replication.",
        listOf("Nausea", "Mild headache", "Sun sensitivity"), listOf("Tendonitis/tendon rupture", "Peripheral neuropathy", "QT prolongation"),
        listOf("Avoid taking with milk, calcium, or antacids within 2 hours."), listOf("Theophylline", "Warfarin", "Tizanidine", "Calcium/Iron")),

    quickMed("med_levofloxacin", "Levofloxacin", listOf("Levomac 500", "Glevo 500", "Levaquin"), "Levofloxacin", "Antibiotics", "Respiratory Fluoroquinolone",
        listOf("Community-acquired pneumonia", "Acute bacterial sinusitis", "UTI"), listOf("250 mg", "500 mg", "750 mg"), listOf("Tablet"),
        "Bactericidal inhibition of bacterial DNA gyrase and topoisomerase IV.",
        listOf("Nausea", "Dizziness", "Insomnia"), listOf("Tendon rupture", "Dysglycemia", "Arrhythmia risk"),
        listOf("Stay well hydrated and monitor blood sugar if diabetic."), listOf("Insulin", "Sulfonylureas", "Antacids", "Warfarin")),

    quickMed("med_doxycycline", "Doxycycline", listOf("Doxy-1 L-DR", "Doxt-SL", "Vibramycin"), "Doxycycline Hyclate", "Antibiotics", "Tetracycline Antibiotic",
        listOf("Acne vulgaris", "Atypical respiratory infections", "Tick-borne & STI infections"), listOf("100 mg"), listOf("Capsule", "Tablet"),
        "Binds bacterial 30S ribosomal subunit to block aminoacyl-tRNA protein synthesis.",
        listOf("Esophageal irritation", "Nausea", "Photosensitivity"), listOf("Esophageal ulceration", "Tooth discoloration in children <8"),
        listOf("Take with a full glass of water and stay upright for 30 minutes."), listOf("Isotretinoin", "Calcium/Iron", "Warfarin")),

    quickMed("med_metronidazole", "Metronidazole", listOf("Flagyl 400", "Metrogyl 400", "Aristogyl"), "Metronidazole", "Antibiotics", "Nitroimidazole Antimicrobial",
        listOf("Amoebiasis & giardiasis", "Anaerobic bacterial infections", "Dental infections"), listOf("200 mg", "400 mg", "500 mg"), listOf("Tablet", "Suspension", "Gel"),
        "Forms cytotoxic free radicals inside anaerobic microbes and protozoa to fragment DNA.",
        listOf("Metallic taste", "Nausea", "Darkened urine (harmless)"), listOf("Peripheral neuropathy (prolonged use)", "Disulfiram-like reaction"),
        listOf("Strictly avoid alcohol during treatment and for 48 hours after."), listOf("Alcohol", "Warfarin", "Lithium")),

    quickMed("med_norflox_tz", "Norfloxacin + Tinidazole", listOf("Norflox-TZ", "Norilet-T", "Gramoneg-T"), "Norfloxacin 400 mg + Tinidazole 600 mg", "Antibiotics", "Fluoroquinolone + Nitroimidazole",
        listOf("Bacterial & protozoal infectious diarrhea", "Acute dysentery"), listOf("400 mg / 600 mg"), listOf("Tablet"),
        "Targets both aerobic Gram-negative bacteria and anaerobic intestinal protozoa.",
        listOf("Metallic taste", "Nausea", "Abdominal cramps"), listOf("Tendon pain", "Severe alcohol intolerance"),
        listOf("Avoid alcohol and maintain oral rehydration salts (ORS)."), listOf("Alcohol", "Warfarin", "Antacids")),

    quickMed("med_clindamycin", "Clindamycin", listOf("Dalacin C", "Cliface Gel", "Cleocin"), "Clindamycin Phosphate/HCl", "Antibiotics", "Lincosamide Antibiotic",
        listOf("Severe acne (topical)", "Dental & soft-tissue anaerobic infections"), listOf("150 mg", "300 mg", "1% Gel"), listOf("Capsule", "Topical Gel"),
        "Inhibits bacterial 50S ribosomal translocation to suppress protein synthesis.",
        listOf("Mild stomach upset", "Skin dryness (gel)"), listOf("Clostridioides difficile-associated colitis"),
        listOf("Stop and seek care if persistent watery diarrhea develops."), listOf("Erythromycin", "Neuromuscular blockers")),

    quickMed("med_montair_lc", "Montelukast + Levocetirizine", listOf("Montair LC", "Montek LC", "Telekast-L"), "Montelukast 10 mg + Levocetirizine 5 mg", "Antihistamines", "Leukotriene Antagonist + Antihistamine",
        listOf("Allergic rhinitis", "Seasonal sneezing & nasal congestion", "Allergic asthma support"), listOf("10 mg / 5 mg"), listOf("Tablet", "Kid Tablet"),
        "Blocks inflammatory leukotriene receptors in airways alongside H1 histamine blockade.",
        listOf("Mild evening drowsiness", "Dry mouth"), listOf("Rare mood/behavior changes (Montelukast warning)"),
        listOf("Usually taken in the evening; report unusual mood changes or vivid dreams."), listOf("Alcohol", "Sedatives", "Phenobarbital")),

    quickMed("med_allegra", "Fexofenadine", listOf("Allegra 120", "Allegra 180", "Fexova"), "Fexofenadine HCl", "Antihistamines", "Non-Sedating 2nd-Gen Antihistamine",
        listOf("Seasonal allergic rhinitis", "Chronic urticaria (hives)"), listOf("120 mg", "180 mg"), listOf("Tablet", "Suspension"),
        "Selectively blocks peripheral H1 receptors with minimal brain penetration.",
        listOf("Occasional mild headache"), listOf("Rare hypersensitivity"),
        listOf("Take with water — avoid fruit juices (orange/apple/grapefruit) which lower absorption."), listOf("Erythromycin", "Ketoconazole", "Antacids")),

    quickMed("med_loratadine", "Loratadine", listOf("Claritin", "Lorfast", "Alaspan"), "Loratadine", "Antihistamines", "Non-Sedating Antihistamine",
        listOf("Allergic sneezing", "Itchy watery eyes", "Skin hives"), listOf("10 mg"), listOf("Tablet", "Syrup"),
        "Long-acting peripheral H1 histamine receptor antagonist.",
        listOf("Mild headache", "Dry mouth"), listOf("Rare tachycardia"),
        listOf("Generally non-drowsy at standard 10 mg daily dose."), listOf("Ketoconazole", "Fluconazole")),

    quickMed("med_chlorpheniramine", "Chlorpheniramine + Phenylephrine + Paracetamol", listOf("Sinarest", "Wikoryl", "Cheston Cold"), "Paracetamol 500 mg + Phenylephrine 10 mg + CPM 2 mg", "Antihistamines", "Cold & Nasal Decongestant Combination",
        listOf("Common cold congestion", "Runny nose & body ache", "Sinus pressure"), listOf("500/10/2 mg"), listOf("Tablet", "Syrup"),
        "Combines fever/pain relief, alpha-1 nasal vasoconstriction, and H1 antihistamine action.",
        listOf("Drowsiness", "Dry mouth"), listOf("Blood pressure spike in uncontrolled hypertension"),
        listOf("Avoid combining with Dolo/Crocin (contains Paracetamol); use caution in high BP."), listOf("MAOIs", "Beta-blockers", "Alcohol"))
)
