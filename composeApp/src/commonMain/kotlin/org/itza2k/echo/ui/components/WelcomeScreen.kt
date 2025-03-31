package org.itza2k.echo.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Star
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import org.itza2k.echo.ui.theme.LocalResonanceColors

/**
 * A welcome screen that introduces the app to new users.
 * Includes a name input field to personalize the experience.
 */
@Composable
fun WelcomeScreen(
    onGetStarted: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = LocalResonanceColors.current
    val scrollState = rememberScrollState()

    // User name state
    var userName by remember { mutableStateOf("") }
    var nameError by remember { mutableStateOf(false) }

    // Animation states
    var showContent by remember { mutableStateOf(false) }
    var showNameInput by remember { mutableStateOf(false) }
    var showFeatures by remember { mutableStateOf(false) }
    var showButton by remember { mutableStateOf(false) }

    // Trigger animations sequentially
    LaunchedEffect(Unit) {
        showContent = true
        delay(300)
        showNameInput = true
        delay(300)
        showFeatures = true
        delay(300)
        showButton = true
    }

    // Pulsating animation for the logo
    val scale by animateFloatAsState(
        targetValue = if (showContent) 1.15f else 1.0f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        )
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(colors.background),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .verticalScroll(scrollState)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Modern app logo with animation
            AnimatedVisibility(
                visible = showContent,
                enter = fadeIn() + slideInVertically(initialOffsetY = { -it })
            ) {
                Box(
                    modifier = Modifier
                        .size(120.dp)
                        .clip(CircleShape)
                        .background(colors.primary)
                        .scale(scale),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = null,
                        tint = colors.onPrimary,
                        modifier = Modifier.size(70.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(28.dp))

            // Welcoming header with animation
            AnimatedVisibility(
                visible = showContent,
                enter = fadeIn() + slideInVertically(initialOffsetY = { -it })
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "Welcome to Echo",
                        fontSize = 40.sp,
                        fontWeight = FontWeight.Bold,
                        color = colors.primary,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "Your personal productivity companion",
                        fontSize = 22.sp,
                        color = colors.onBackground,
                        textAlign = TextAlign.Center,
                        fontWeight = FontWeight.Medium
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "Boost your productivity and focus with Echo",
                        fontSize = 16.sp,
                        color = colors.onBackground.copy(alpha = 0.7f),
                        textAlign = TextAlign.Center,
                        fontStyle = FontStyle.Italic
                    )
                }
            }

            Spacer(modifier = Modifier.height(36.dp))

            // Name input field with animation
            AnimatedVisibility(
                visible = showNameInput,
                enter = fadeIn() + slideInVertically(initialOffsetY = { it })
            ) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    elevation = 6.dp,
                    backgroundColor = colors.surface,
                    shape = RoundedCornerShape(20.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Let's personalize your experience",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = colors.primary,
                            textAlign = TextAlign.Center
                        )

                        Spacer(modifier = Modifier.height(20.dp))

                        OutlinedTextField(
                            value = userName,
                            onValueChange = { 
                                userName = it
                                nameError = false
                            },
                            label = { Text("What's your name?") },
                            leadingIcon = { 
                                Icon(
                                    imageVector = Icons.Default.Person,
                                    contentDescription = "Name",
                                    tint = colors.primary,
                                    modifier = Modifier.size(28.dp)
                                )
                            },
                            isError = nameError,
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            colors = TextFieldDefaults.outlinedTextFieldColors(
                                focusedBorderColor = colors.primary,
                                unfocusedBorderColor = colors.onSurface.copy(alpha = 0.3f),
                                errorBorderColor = Color.Red,
                                textColor = Color.White
                            )
                        )

                        if (nameError) {
                            Text(
                                text = "Please enter your name to continue",
                                color = Color.Red,
                                style = MaterialTheme.typography.caption,
                                modifier = Modifier.padding(start = 16.dp, top = 4.dp)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(40.dp))

            // Features section with animation
            AnimatedVisibility(
                visible = showFeatures,
                enter = fadeIn() + slideInVertically(initialOffsetY = { it })
            ) {
                Column {
                    Text(
                        text = "Discover Echo's Features",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = colors.primary,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(bottom = 20.dp)
                    )

                    FeatureItem(
                        title = "Track Your Emotions",
                        description = "Monitor your mood and energy levels throughout the day to identify patterns and optimize your productivity cycles",
                        icon = Icons.Default.Person
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    FeatureItem(
                        title = "Daily Reflections",
                        description = "Take time to reflect on your achievements and challenges to foster personal growth and continuous improvement",
                        icon = Icons.Default.Star
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    FeatureItem(
                        title = "Focus Sessions",
                        description = "Use the Pomodoro technique to maintain deep concentration and accomplish your goals with minimal distractions",
                        icon = Icons.Default.Star
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    FeatureItem(
                        title = "Task Management",
                        description = "Organize your tasks with priorities to stay on track, celebrate your progress, and achieve more every day",
                        icon = Icons.Default.Person
                    )
                }
            }

            Spacer(modifier = Modifier.height(40.dp))

            // Get started button with animation
            AnimatedVisibility(
                visible = showButton,
                enter = fadeIn() + slideInVertically(initialOffsetY = { it })
            ) {
                Button(
                    onClick = { 
                        if (userName.isBlank()) {
                            nameError = true
                        } else {
                            onGetStarted(userName)
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth(0.9f)
                        .height(70.dp),
                    shape = RoundedCornerShape(35.dp),
                    colors = ButtonDefaults.buttonColors(
                        backgroundColor = colors.primary,
                        contentColor = colors.onPrimary
                    ),
                    elevation = ButtonDefaults.elevation(
                        defaultElevation = 10.dp,
                        pressedElevation = 15.dp
                    )
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = null,
                            modifier = Modifier.size(28.dp)
                        )

                        Spacer(modifier = Modifier.width(12.dp))

                        Text(
                            text = "Begin Your Journey",
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

/**
 * A component that displays a feature with a title, description, and icon.
 */
@Composable
private fun FeatureItem(
    title: String,
    description: String,
    icon: ImageVector = Icons.Default.Star
) {
    val colors = LocalResonanceColors.current

    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = 4.dp,
        backgroundColor = colors.surface,
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Icon with circular background
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(colors.primary.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = colors.primary,
                    modifier = Modifier.size(28.dp)
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column {
                Text(
                    text = title,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = colors.primary
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = description,
                    fontSize = 14.sp,
                    color = colors.onSurface.copy(alpha = 0.7f)
                )
            }
        }
    }
}
