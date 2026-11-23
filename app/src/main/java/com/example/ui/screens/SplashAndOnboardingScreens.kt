package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowForward
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.R
import com.example.ui.components.AnimatedDnaHelixCanvas
import com.example.ui.components.DRxGuardLogoMark
import com.example.ui.components.ScientificLoader
import com.example.ui.theme.DeepNavy
import com.example.ui.theme.ElectricCyan
import com.example.ui.theme.MedicalBlue
import com.example.ui.theme.MedicalTeal
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun SplashScreen(onFinished: () -> Unit) {
    var showCross by remember { mutableStateOf(false) }
    var showWordmark by remember { mutableStateOf(false) }
    var showTagline by remember { mutableStateOf(false) }

    val crossScale by animateFloatAsState(
        targetValue = if (showCross) 1f else 0.4f,
        animationSpec = tween(650, easing = FastOutSlowInEasing),
        label = "cross_scale"
    )

    LaunchedEffect(Unit) {
        showCross = true
        delay(450)
        showWordmark = true
        delay(350)
        showTagline = true
        delay(1450)
        onFinished()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(DeepNavy, MedicalBlue, ElectricCyan)
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        // Animated DNA helix & molecular particles
        AnimatedDnaHelixCanvas(
            modifier = Modifier.fillMaxSize(),
            primaryColor = ElectricCyan,
            secondaryColor = Color.White
        )

        Column(
            modifier = Modifier
                .padding(horizontal = 32.dp)
                .statusBarsPadding()
                .navigationBarsPadding(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            DRxGuardLogoMark(
                size = 104.dp,
                glowing = true,
                modifier = Modifier.scale(crossScale)
            )
            Spacer(Modifier.height(24.dp))
            AnimatedVisibility(
                visible = showWordmark,
                enter = fadeIn(tween(500)) + slideInVertically(tween(500)) { it / 3 }
            ) {
                Text(
                    text = "DRxGuard",
                    style = MaterialTheme.typography.displayLarge,
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
            }
            Spacer(Modifier.height(8.dp))
            AnimatedVisibility(
                visible = showTagline,
                enter = fadeIn(tween(500))
            ) {
                Text(
                    text = "Your Intelligent Medicine Companion",
                    style = MaterialTheme.typography.titleMedium,
                    color = Color.White.copy(alpha = 0.92f),
                    textAlign = TextAlign.Center
                )
            }
            Spacer(Modifier.height(44.dp))
            ScientificLoader(size = 52.dp, color = Color.White)
            Spacer(Modifier.height(12.dp))
            Text(
                text = "Initializing Pharmaceutical Intelligence...",
                style = MaterialTheme.typography.labelSmall,
                color = Color.White.copy(alpha = 0.78f)
            )
        }
    }
}

private data class OnboardingSlide(
    val headline: String,
    val description: String,
    val imageRes: Int,
    val badge: String,
    val accent: Color
)

@Composable
fun OnboardingScreen(onComplete: () -> Unit) {
    val slides = remember {
        listOf(
            OnboardingSlide(
                headline = "Understand Your Medicines Better",
                description = "Explore medicine information, drug interactions, precautions, mechanisms of action, and verified pharmaceutical knowledge.",
                imageRes = R.drawable.img_capsule_molecules,
                badge = "PHARMACEUTICAL INTELLIGENCE",
                accent = MedicalBlue
            ),
            OnboardingSlide(
                headline = "Smarter Medication Management",
                description = "Manage prescribed medicine schedules, digital cabinet expiry alerts, BMI trends, and LabGuard reports in one unified hub.",
                imageRes = R.drawable.img_pill_organizer,
                badge = "SMART SAFETY ECOSYSTEM",
                accent = MedicalTeal
            ),
            OnboardingSlide(
                headline = "Healthcare Intelligence Powered by AI",
                description = "Get structured pharmaceutical explanations, pill visual comparison, and healthcare education in 9 languages with DRxGuard AI.",
                imageRes = R.drawable.img_hero_ai_pharmacist,
                badge = "MULTILINGUAL AI PHARMACIST",
                accent = ElectricCyan
            )
        )
    }

    val pagerState = rememberPagerState(pageCount = { slides.size })
    val scope = rememberCoroutineScope()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .statusBarsPadding()
            .navigationBarsPadding(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .widthIn(max = 600.dp)
                .padding(horizontal = 24.dp, vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    DRxGuardLogoMark(size = 32.dp, glowing = false)
                    Spacer(Modifier.width(8.dp))
                    Text(
                        text = "DRxGuard",
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                }
                TextButton(
                    onClick = onComplete,
                    modifier = Modifier.testTag("onboarding_skip_button")
                ) {
                    Text("Skip", style = MaterialTheme.typography.labelLarge)
                }
            }

            Spacer(Modifier.height(12.dp))

            HorizontalPager(
                state = pagerState,
                modifier = Modifier.weight(1f)
            ) { page ->
                val slide = slides[page]
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Surface(
                        shape = RoundedCornerShape(30.dp),
                        shadowElevation = 8.dp,
                        color = MaterialTheme.colorScheme.surface,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(290.dp)
                    ) {
                        Image(
                            painter = painterResource(id = slide.imageRes),
                            contentDescription = slide.headline,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                    }

                    Spacer(Modifier.height(28.dp))

                    Surface(
                        shape = CircleShape,
                        color = slide.accent.copy(alpha = 0.14f)
                    ) {
                        Text(
                            text = slide.badge,
                            style = MaterialTheme.typography.labelSmall,
                            color = slide.accent,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)
                        )
                    }

                    Spacer(Modifier.height(14.dp))

                    Text(
                        text = slide.headline,
                        style = MaterialTheme.typography.headlineMedium,
                        color = MaterialTheme.colorScheme.onBackground,
                        textAlign = TextAlign.Center
                    )

                    Spacer(Modifier.height(12.dp))

                    Text(
                        text = slide.description,
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(horizontal = 8.dp)
                    )
                }
            }

            // Page indicators
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(vertical = 20.dp)
            ) {
                repeat(slides.size) { index ->
                    val selected = pagerState.currentPage == index
                    Box(
                        modifier = Modifier
                            .height(8.dp)
                            .width(if (selected) 28.dp else 8.dp)
                            .clip(CircleShape)
                            .background(
                                if (selected) MaterialTheme.colorScheme.primary
                                else MaterialTheme.colorScheme.outline
                            )
                    )
                }
            }

            val isLast = pagerState.currentPage == slides.lastIndex
            Button(
                onClick = {
                    if (isLast) onComplete()
                    else scope.launch { pagerState.animateScrollToPage(pagerState.currentPage + 1) }
                },
                shape = RoundedCornerShape(18.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp)
                    .testTag(if (isLast) "onboarding_get_started_button" else "onboarding_next_button")
            ) {
                Text(
                    text = if (isLast) "Get Started" else "Next",
                    style = MaterialTheme.typography.labelLarge
                )
                Spacer(Modifier.width(8.dp))
                Icon(Icons.AutoMirrored.Rounded.ArrowForward, contentDescription = null)
            }
            Spacer(Modifier.height(8.dp))
        }
    }
}
