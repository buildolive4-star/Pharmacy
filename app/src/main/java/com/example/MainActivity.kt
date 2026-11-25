package com.example

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AutoAwesome
import androidx.compose.material.icons.rounded.Explore
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.LocalPharmacy
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.credentials.CredentialManager
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import androidx.compose.ui.unit.dp
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.data.DRxAiService
import com.example.data.DRxRepository
import com.example.data.authStateFlow
import com.example.data.configureAppCheck
import com.example.data.signOutUser
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.flow.map
import com.example.ui.components.ScientificLoader
import com.example.ui.screens.AdminPanelScreen
import com.example.ui.screens.AiPharmacistScreen
import com.example.ui.screens.AuthScreen
import com.example.ui.screens.BmiCalculatorScreen
import com.example.ui.screens.CompareDrugsScreen
import com.example.ui.screens.DRxNavigationDrawerContent
import com.example.ui.screens.DRxRoute
import com.example.ui.screens.DrugClassesScreen
import com.example.ui.screens.DrugInteractionScreen
import com.example.ui.screens.ExploreHubScreen
import com.example.ui.screens.HealthConditionsScreen
import com.example.ui.screens.HomeDashboardScreen
import com.example.ui.screens.LabGuardScreen
import com.example.ui.screens.MechanismOfActionScreen
import com.example.ui.screens.MedicineAlternativesScreen
import com.example.ui.screens.MedicineChangeSimulatorScreen
import com.example.ui.screens.MedicineDetailScreen
import com.example.ui.screens.MedicineExpiryTrackerScreen
import com.example.ui.screens.MedicineLibraryScreen
import com.example.ui.screens.MyMedicinesHubScreen
import com.example.ui.screens.NotificationsScreen
import com.example.ui.screens.OnboardingScreen
import com.example.ui.screens.PillScannerScreen
import com.example.ui.screens.PregnancySupportScreen
import com.example.ui.screens.ProfileAndSettingsScreen
import com.example.ui.screens.SideEffectCheckerScreen
import com.example.ui.screens.SmartMedicineReminderScreen
import com.example.ui.screens.SplashScreen
import com.example.ui.theme.DRxGuardTheme
import com.example.viewmodel.DRxViewModel
import com.example.viewmodel.UiState
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch

private const val PREFS_NAME = "drxguard_prefs"
private const val KEY_ONBOARDING_DONE = "onboarding_done"
private const val ROUTE_MEDICINE_DETAIL = "medicine_detail/{medId}"

private data class BottomNavTab(
    val route: String,
    val label: String,
    val icon: ImageVector
)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // CRITICAL: Initialize Firebase App Check BEFORE any Firebase / ViewModel / setContent call
        configureAppCheck(this, intent)
        enableEdgeToEdge()

        setContent {
            DRxGuardRootApp()
        }
    }
}

private sealed interface AuthUiState {
    data object Loading : AuthUiState
    data object SignedOut : AuthUiState
    data class SignedIn(val user: FirebaseUser) : AuthUiState
}

@Composable
private fun DRxGuardRootApp() {
    val context = LocalContext.current
    val prefs = remember(context) { context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE) }

    var splashDone by rememberSaveable { mutableStateOf(false) }
    var onboardingDone by rememberSaveable {
        mutableStateOf(prefs.getBoolean(KEY_ONBOARDING_DONE, false))
    }

    val authState by remember {
        Firebase.auth.authStateFlow().map { user ->
            if (user != null) AuthUiState.SignedIn(user) else AuthUiState.SignedOut
        }
    }.collectAsStateWithLifecycle(initialValue = AuthUiState.Loading)

    val systemDark = isSystemInDarkTheme()

    if (!splashDone) {
        DRxGuardTheme(darkTheme = systemDark) {
            SplashScreen(onFinished = { splashDone = true })
        }
        return
    }

    if (!onboardingDone) {
        DRxGuardTheme(darkTheme = systemDark) {
            OnboardingScreen(
                onComplete = {
                    prefs.edit().putBoolean(KEY_ONBOARDING_DONE, true).apply()
                    onboardingDone = true
                }
            )
        }
        return
    }

    when (val state = authState) {
        AuthUiState.Loading -> {
            DRxGuardTheme(darkTheme = systemDark) {
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                    Box(contentAlignment = Alignment.Center) {
                        ScientificLoader(size = 56.dp)
                    }
                }
            }
        }

        AuthUiState.SignedOut -> {
            DRxGuardTheme(darkTheme = systemDark) {
                AuthScreen(onAuthSuccess = { /* authStateFlow transitions automatically */ })
            }
        }

        is AuthUiState.SignedIn -> {
            val user = state.user
            key(user.uid) {
                val appContext = context.applicationContext
                val databaseId = remember(appContext) {
                    appContext.getString(R.string.firestore_database_id)
                }
                val viewModel: DRxViewModel = viewModel(
                    key = user.uid,
                    factory = viewModelFactory {
                        initializer {
                            val db = FirebaseFirestore.getInstance(databaseId)
                            val repository = DRxRepository(db, Firebase.auth)
                            val aiService = DRxAiService()
                            DRxViewModel(
                                repository = repository,
                                aiService = aiService,
                                currentUserId = user.uid,
                                initialName = user.displayName ?: "DRxGuard Member",
                                initialEmail = user.email ?: ""
                            )
                        }
                    }
                )
                AuthenticatedDRxShell(
                    viewModel = viewModel,
                    userEmail = user.email ?: "",
                    systemDark = systemDark
                )
            }
        }
    }
}

@Composable
private fun AuthenticatedDRxShell(
    viewModel: DRxViewModel,
    userEmail: String,
    systemDark: Boolean
) {
    val context = LocalContext.current
    val credentialManager = remember(context) { CredentialManager.create(context) }
    val scope = rememberCoroutineScope()

    val profileState by viewModel.profileState.collectAsStateWithLifecycle()
    val remindersState by viewModel.remindersState.collectAsStateWithLifecycle()
    val cabinetState by viewModel.cabinetState.collectAsStateWithLifecycle()
    val bmiState by viewModel.bmiHistoryState.collectAsStateWithLifecycle()
    val reportsState by viewModel.reportsState.collectAsStateWithLifecycle()
    val notificationsState by viewModel.notificationsState.collectAsStateWithLifecycle()
    val adminMedsState by viewModel.adminMedicinesState.collectAsStateWithLifecycle()

    val selectedLanguage by viewModel.selectedLanguage.collectAsStateWithLifecycle()
    val darkModeOverride by viewModel.darkModeOverride.collectAsStateWithLifecycle()
    val recentlyViewedIds by viewModel.recentlyViewedMedicineIds.collectAsStateWithLifecycle()
    val recentlyUsedTools by viewModel.recentlyUsedTools.collectAsStateWithLifecycle()
    val bannerMessage by viewModel.bannerMessage.collectAsStateWithLifecycle()

    val chatMessages by viewModel.chatMessages.collectAsStateWithLifecycle()
    val aiChatState by viewModel.aiChatState.collectAsStateWithLifecycle()
    val pillScanState by viewModel.pillScanState.collectAsStateWithLifecycle()
    val labAiState by viewModel.labAiState.collectAsStateWithLifecycle()
    val pregnancySymptomState by viewModel.pregnancySymptomState.collectAsStateWithLifecycle()

    val aiQueriesCount by viewModel.aiQueriesCount.collectAsStateWithLifecycle()
    val interactionChecksCount by viewModel.interactionChecksCount.collectAsStateWithLifecycle()
    val scannerRequestsCount by viewModel.scannerRequestsCount.collectAsStateWithLifecycle()
    val labGuardRequestsCount by viewModel.labGuardRequestsCount.collectAsStateWithLifecycle()

    val profile = (profileState as? UiState.Success)?.data
    val reminders = (remindersState as? UiState.Success)?.data.orEmpty()
    val cabinet = (cabinetState as? UiState.Success)?.data.orEmpty()
    val bmiHistory = (bmiState as? UiState.Success)?.data.orEmpty()
    val reports = (reportsState as? UiState.Success)?.data.orEmpty()
    val notifications = (notificationsState as? UiState.Success)?.data.orEmpty()
    val adminMedicines = (adminMedsState as? UiState.Success)?.data.orEmpty()

    val darkTheme = darkModeOverride ?: profile?.darkMode ?: systemDark

    val navController = rememberNavController()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(bannerMessage) {
        val msg = bannerMessage
        if (!msg.isNullOrBlank()) {
            snackbarHostState.showSnackbar(msg)
            viewModel.clearBannerMessage()
        }
    }

    val bottomTabs = remember {
        listOf(
            BottomNavTab(DRxRoute.HOME, "Home", Icons.Rounded.Home),
            BottomNavTab(DRxRoute.EXPLORE, "Explore", Icons.Rounded.Explore),
            BottomNavTab(DRxRoute.AI_PHARMACIST, "AI Chat", Icons.Rounded.AutoAwesome),
            BottomNavTab(DRxRoute.MY_MEDICINES, "My Meds", Icons.Rounded.LocalPharmacy),
            BottomNavTab(DRxRoute.PROFILE, "Profile", Icons.Rounded.Person)
        )
    }
    val bottomTabRoutes = remember { bottomTabs.map { it.route }.toSet() }
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route ?: DRxRoute.HOME
    val density = LocalDensity.current
    val isImeOpen = WindowInsets.ime.getBottom(density) > 0
    val showBottomBar = currentRoute in bottomTabRoutes && !isImeOpen

    fun navigateTo(route: String) {
        scope.launch { drawerState.close() }
        if (route in bottomTabRoutes) {
            navController.navigate(route) {
                popUpTo(navController.graph.findStartDestination().id) {
                    saveState = true
                }
                launchSingleTop = true
                restoreState = true
            }
        } else {
            navController.navigate(route) {
                launchSingleTop = true
            }
        }
    }

    fun openMedicineDetail(medId: String) {
        viewModel.recordMedicineViewed(medId)
        navController.navigate("medicine_detail/$medId") {
            launchSingleTop = true
        }
    }

    fun performSignOut() {
        scope.launch {
            drawerState.close()
            signOutUser(credentialManager, scope)
        }
    }

    DRxGuardTheme(darkTheme = darkTheme) {
        ModalNavigationDrawer(
            drawerState = drawerState,
            drawerContent = {
                DRxNavigationDrawerContent(
                    profile = profile,
                    userEmail = userEmail,
                    onNavigate = { route -> navigateTo(route) },
                    onLogout = { performSignOut() }
                )
            }
        ) {
            Scaffold(
                snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
                contentWindowInsets = WindowInsets.safeDrawing,
                bottomBar = {
                    if (showBottomBar) {
                        Surface(
                            color = MaterialTheme.colorScheme.surface,
                            tonalElevation = 3.dp,
                            shadowElevation = 8.dp
                        ) {
                            Column {
                                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.65f))
                                NavigationBar(
                                    containerColor = MaterialTheme.colorScheme.surface,
                                    tonalElevation = 0.dp,
                                    windowInsets = WindowInsets.navigationBars
                                ) {
                                    bottomTabs.forEach { tab ->
                                        val selected = currentRoute == tab.route
                                        NavigationBarItem(
                                            selected = selected,
                                            onClick = { navigateTo(tab.route) },
                                            icon = { Icon(tab.icon, contentDescription = tab.label) },
                                            label = {
                                                Text(
                                                    text = tab.label,
                                                    maxLines = 1,
                                                    style = MaterialTheme.typography.labelSmall
                                                )
                                            },
                                            colors = NavigationBarItemDefaults.colors(
                                                selectedIconColor = MaterialTheme.colorScheme.primary,
                                                selectedTextColor = MaterialTheme.colorScheme.primary,
                                                indicatorColor = MaterialTheme.colorScheme.primaryContainer,
                                                unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                                unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
                                            ),
                                            modifier = Modifier.testTag("bottom_tab_${tab.route}")
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            ) { innerPadding ->
                NavHost(
                    navController = navController,
                    startDestination = DRxRoute.HOME,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                        .consumeWindowInsets(innerPadding)
                ) {
                    // --- 5 Primary Bottom Tabs ---
                    composable(DRxRoute.HOME) {
                        HomeDashboardScreen(
                            profile = profile,
                            reminders = reminders,
                            cabinet = cabinet,
                            notifications = notifications,
                            recentlyViewedIds = recentlyViewedIds,
                            recentlyUsedTools = recentlyUsedTools,
                            onOpenDrawer = { scope.launch { drawerState.open() } },
                            onNavigate = { navigateTo(it) },
                            onOpenMedicineDetail = { openMedicineDetail(it) }
                        )
                    }

                    composable(DRxRoute.EXPLORE) {
                        ExploreHubScreen(onNavigate = { navigateTo(it) })
                    }

                    composable(DRxRoute.AI_PHARMACIST) {
                        AiPharmacistScreen(
                            selectedLanguage = selectedLanguage,
                            messages = chatMessages,
                            aiState = aiChatState,
                            onSelectLanguage = { viewModel.setLanguage(it) },
                            onSendQuestion = { q, bmp -> viewModel.askAiPharmacist(q, bmp) },
                            onClearChat = { viewModel.clearAiChat() },
                            onRetry = { viewModel.retryLastAiQuestion() }
                        )
                    }

                    composable(DRxRoute.MY_MEDICINES) {
                        MyMedicinesHubScreen(
                            reminders = reminders,
                            cabinet = cabinet,
                            savedMedicineIds = profile?.savedMedicineIds.orEmpty(),
                            reports = reports,
                            onNavigate = { navigateTo(it) },
                            onOpenMedicineDetail = { openMedicineDetail(it) },
                            onDeleteReport = { viewModel.deleteReport(it) }
                        )
                    }

                    composable(DRxRoute.PROFILE) {
                        ProfileAndSettingsScreen(
                            profile = profile,
                            userEmail = userEmail,
                            darkModeEnabled = darkTheme,
                            selectedLanguage = selectedLanguage,
                            reminders = reminders,
                            cabinet = cabinet,
                            bmiHistory = bmiHistory,
                            reports = reports,
                            onToggleDarkMode = { viewModel.setDarkMode(it) },
                            onSelectLanguage = { viewModel.setLanguage(it) },
                            onSaveProfile = { name, mob, dob, lang, consent, h, w, allg, cond, emerg ->
                                viewModel.updateProfile(name, mob, dob, lang, consent, h, w, allg, cond, emerg)
                            },
                            onOpenAdminPanel = { navigateTo(DRxRoute.ADMIN_PANEL) },
                            onOpenNotifications = { navigateTo(DRxRoute.NOTIFICATIONS) },
                            onDeleteAccountData = {
                                viewModel.deleteAllAccountData { performSignOut() }
                            },
                            onSignOut = { performSignOut() }
                        )
                    }

                    // --- Feature 01–04: Drug Tools ---
                    composable(DRxRoute.DRUG_INTERACTION) {
                        DrugInteractionScreen(
                            onBack = { navController.popBackStack() },
                            onCheckLogged = { viewModel.recordInteractionCheck() },
                            onSaveReport = { title, summary, severity ->
                                viewModel.saveReport("INTERACTION", title, summary, severity)
                            }
                        )
                    }

                    composable(DRxRoute.SIDE_EFFECTS) {
                        SideEffectCheckerScreen(onBack = { navController.popBackStack() })
                    }

                    composable(DRxRoute.COMPARE_DRUGS) {
                        CompareDrugsScreen(onBack = { navController.popBackStack() })
                    }

                    composable(DRxRoute.ALTERNATIVES) {
                        MedicineAlternativesScreen(onBack = { navController.popBackStack() })
                    }

                    // --- Feature 05 & 06: Pregnancy Support & BMI ---
                    composable(DRxRoute.PREGNANCY) {
                        PregnancySupportScreen(
                            symptomAiState = pregnancySymptomState,
                            onAnalyzeSymptoms = { symptoms, week ->
                                viewModel.analyzePregnancySymptoms(symptoms, week)
                            },
                            onBack = { navController.popBackStack() }
                        )
                    }

                    composable(DRxRoute.BMI) {
                        BmiCalculatorScreen(
                            bmiHistory = bmiHistory,
                            onSaveBmi = { bmi, cat, h, w, date ->
                                viewModel.saveBmiEntry(bmi, cat, h, w, date)
                            },
                            onBack = { navController.popBackStack() }
                        )
                    }

                    // --- Feature 07 & 13: Pill Scanner & LabGuard ---
                    composable(DRxRoute.PILL_SCANNER) {
                        PillScannerScreen(
                            scanState = pillScanState,
                            onScanPill = { bmp, hint -> viewModel.scanPillWithAi(bmp, hint) },
                            onSaveReport = { title, summary, severity ->
                                viewModel.saveReport("PILL_SCAN", title, summary, severity)
                            },
                            onBack = { navController.popBackStack() }
                        )
                    }

                    composable(DRxRoute.LABGUARD) {
                        LabGuardScreen(
                            labAiState = labAiState,
                            onAnalyzeLab = { summary, bmp -> viewModel.analyzeLabWithAi(summary, bmp) },
                            onSaveReport = { title, summary, severity ->
                                viewModel.saveReport("LABGUARD", title, summary, severity)
                            },
                            onBack = { navController.popBackStack() }
                        )
                    }

                    // --- Feature 08 & 15: Smart Reminders & Expiry Tracker ---
                    composable(DRxRoute.REMINDERS) {
                        SmartMedicineReminderScreen(
                            reminders = reminders,
                            onAddReminder = { viewModel.addReminder(it) },
                            onUpdateStatus = { id, status -> viewModel.updateReminderStatus(id, status) },
                            onDeleteReminder = { viewModel.deleteReminder(it) },
                            onSimulateNotification = { title, msg ->
                                viewModel.triggerSimulatedNotification("Medicine reminders", title, msg)
                            },
                            onBack = { navController.popBackStack() }
                        )
                    }

                    composable(DRxRoute.EXPIRY_TRACKER) {
                        MedicineExpiryTrackerScreen(
                            cabinet = cabinet,
                            onAddCabinetMedicine = { viewModel.addCabinetMedicine(it) },
                            onDeleteCabinetMedicine = { viewModel.deleteCabinetMedicine(it) },
                            onBack = { navController.popBackStack() }
                        )
                    }

                    // --- Feature 09, 10, 11, 12, 14: Knowledge & Simulator ---
                    composable(DRxRoute.MEDICINE_LIBRARY) {
                        MedicineLibraryScreen(
                            savedMedicineIds = profile?.savedMedicineIds.orEmpty(),
                            adminMedicines = adminMedicines,
                            onToggleSave = { viewModel.toggleSavedMedicine(it) },
                            onOpenDetail = { openMedicineDetail(it) },
                            onBack = { navController.popBackStack() }
                        )
                    }

                    composable(
                        route = ROUTE_MEDICINE_DETAIL,
                        arguments = listOf(navArgument("medId") { type = NavType.StringType })
                    ) { entry ->
                        val medId = entry.arguments?.getString("medId") ?: "med_paracetamol"
                        MedicineDetailScreen(
                            medicineId = medId,
                            adminMedicines = adminMedicines,
                            isSaved = medId in profile?.savedMedicineIds.orEmpty(),
                            onToggleSave = { viewModel.toggleSavedMedicine(medId) },
                            onOpenMechanism = { navigateTo(DRxRoute.MECHANISM) },
                            onOpenInteractions = { navigateTo(DRxRoute.DRUG_INTERACTION) },
                            onBack = { navController.popBackStack() }
                        )
                    }

                    composable(DRxRoute.MECHANISM) {
                        MechanismOfActionScreen(onBack = { navController.popBackStack() })
                    }

                    composable(DRxRoute.HEALTH_CONDITIONS) {
                        HealthConditionsScreen(onBack = { navController.popBackStack() })
                    }

                    composable(DRxRoute.DRUG_CLASSES) {
                        DrugClassesScreen(
                            onOpenMedicineDetail = { openMedicineDetail(it) },
                            onBack = { navController.popBackStack() }
                        )
                    }

                    composable(DRxRoute.CHANGE_SIMULATOR) {
                        MedicineChangeSimulatorScreen(onBack = { navController.popBackStack() })
                    }

                    // --- Supporting Modules: Notifications & Admin Console ---
                    composable(DRxRoute.NOTIFICATIONS) {
                        NotificationsScreen(
                            notifications = notifications,
                            onMarkRead = { viewModel.markNotificationRead(it) },
                            onSimulateAlert = { cat, title, msg ->
                                viewModel.triggerSimulatedNotification(cat, title, msg)
                            },
                            onBack = { navController.popBackStack() }
                        )
                    }

                    composable(DRxRoute.ADMIN_PANEL) {
                        AdminPanelScreen(
                            userEmail = userEmail,
                            aiQueriesCount = aiQueriesCount,
                            interactionChecksCount = interactionChecksCount,
                            scannerRequestsCount = scannerRequestsCount,
                            labGuardRequestsCount = labGuardRequestsCount,
                            adminMedicines = adminMedicines,
                            onAddAdminMedicine = { viewModel.addAdminMedicine(it) },
                            onBack = { navController.popBackStack() }
                        )
                    }
                }
            }
        }
    }
}
