package org.itza2k.echo.ui.components

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.List
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.itza2k.echo.data.model.EnergyLevel
import org.itza2k.echo.data.model.MoodEnergyEntry
import org.itza2k.echo.data.model.MoodLevel
import org.itza2k.echo.ui.theme.LocalResonanceColors
import kotlin.random.Random

/**
 * A component for tracking mood and energy levels.
 */
@Composable
fun MoodEnergyTracker(
    onEntryAdded: (MoodEnergyEntry) -> Unit = {},
    onHistoryClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val colors = LocalResonanceColors.current

    // State for the tracker
    var isExpanded by remember { mutableStateOf(false) }
    var selectedMood by remember { mutableStateOf<MoodLevel?>(null) }
    var selectedEnergy by remember { mutableStateOf<EnergyLevel?>(null) }
    var note by remember { mutableStateOf("") }

    // Card with subtle animation when expanded
    Card(
        modifier = modifier
            .fillMaxWidth()
            .animateContentSize(
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness = Spring.StiffnessLow
                )
            ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 4.dp
        ),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = colors.surface
        ),
        border = BorderStroke(
            width = 1.dp,
            color = colors.onSurface.copy(alpha = 0.12f)
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Track Your State",
                    style = MaterialTheme.typography.titleLarge,
                    color = colors.onSurface
                )

                // Expand/collapse button
                IconButton(onClick = { isExpanded = !isExpanded }) {
                    Icon(
                        imageVector = if (isExpanded) Icons.Default.CheckCircle else Icons.Default.Add,
                        contentDescription = if (isExpanded) "Collapse" else "Expand",
                        tint = colors.primary
                    )
                }
            }

            // Content when expanded
            if (isExpanded) {
                Spacer(modifier = Modifier.height(16.dp))

                // Mood selection
                Text(
                    text = "How are you feeling?",
                    style = MaterialTheme.typography.titleMedium,
                    color = colors.onSurface
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Mood options
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    MoodLevel.values().forEach { mood ->
                        MoodOption(
                            mood = mood,
                            isSelected = selectedMood == mood,
                            onSelect = { selectedMood = mood }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Energy selection
                Text(
                    text = "Energy level?",
                    style = MaterialTheme.typography.titleMedium,
                    color = colors.onSurface
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Energy options
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    EnergyLevel.values().forEach { energy ->
                        EnergyOption(
                            energy = energy,
                            isSelected = selectedEnergy == energy,
                            onSelect = { selectedEnergy = energy }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Note input
                OutlinedTextField(
                    value = note,
                    onValueChange = { note = it },
                    label = { Text("Add a note (optional)") },
                    modifier = Modifier.fillMaxWidth(),
                    maxLines = 3,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outline
                    )
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Save button
                Button(
                    onClick = {
                        // Only save if both mood and energy are selected
                        if (selectedMood != null && selectedEnergy != null) {
                            val now = Clock.System.now()
                            val localDateTime = now.toLocalDateTime(TimeZone.currentSystemDefault())

                            val entry = MoodEnergyEntry(
                                id = Random.nextLong().toString(), // Simple ID generation
                                moodLevel = selectedMood!!,
                                energyLevel = selectedEnergy!!,
                                note = note,
                                date = localDateTime.date,
                                time = localDateTime.time,
                                createdAt = now
                            )

                            onEntryAdded(entry)

                            // Reset the form
                            selectedMood = null
                            selectedEnergy = null
                            note = ""
                            isExpanded = false
                        }
                    },
                    enabled = selectedMood != null && selectedEnergy != null,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary,
                        disabledContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                        disabledContentColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                    )
                ) {
                    Text("Save Entry")
                }
            } else {
                // Collapsed state - show a summary or prompt
                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Track your mood and energy levels to identify your optimal work times",
                    style = MaterialTheme.typography.bodyMedium,
                    color = colors.onSurface.copy(alpha = 0.7f),
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Show history button
                TextButton(
                    onClick = onHistoryClick,
                    modifier = Modifier.align(Alignment.End),
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = MaterialTheme.colorScheme.primary
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.List,
                        contentDescription = "History"
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "History"
                    )
                }
            }
        }
    }
}

/**
 * A selectable mood option with distinct colors for each mood level.
 */
@Composable
private fun MoodOption(
    mood: MoodLevel,
    isSelected: Boolean,
    onSelect: () -> Unit
) {
    // Define distinct colors for each mood level
    val moodColor = when (mood) {
        MoodLevel.VERY_BAD -> Color(0xFFEF5350)  // Red
        MoodLevel.BAD -> Color(0xFFFF7043)       // Orange
        MoodLevel.NEUTRAL -> Color(0xFFFFCA28)   // Yellow
        MoodLevel.GOOD -> Color(0xFF66BB6A)      // Green
        MoodLevel.VERY_GOOD -> Color(0xFF42A5F5) // Blue
    }

    // Determine text color based on background color
    val textColor = Color.Black

    // Create a surface with the mood color
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = if (isSelected) moodColor else moodColor.copy(alpha = 0.3f),
        border = if (isSelected) BorderStroke(2.dp, moodColor) else null,
        modifier = Modifier
            .padding(4.dp)
            .clickable(onClick = onSelect)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(8.dp)
        ) {
            Text(
                text = mood.emoji,
                fontSize = 20.sp,
                modifier = Modifier.padding(vertical = 4.dp)
            )

            Text(
                text = mood.description,
                fontSize = 12.sp,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                color = textColor.copy(alpha = if (isSelected) 1f else 0.7f)
            )
        }
    }
}

/**
 * A selectable energy option with distinct colors for each energy level.
 */
@Composable
private fun EnergyOption(
    energy: EnergyLevel,
    isSelected: Boolean,
    onSelect: () -> Unit
) {
    // Define distinct colors for each energy level
    val energyColor = when (energy) {
        EnergyLevel.VERY_LOW -> Color(0xFFE57373)  // Red
        EnergyLevel.LOW -> Color(0xFFFFB74D)       // Orange
        EnergyLevel.MEDIUM -> Color(0xFFFFD54F)    // Yellow
        EnergyLevel.HIGH -> Color(0xFFAED581)       // Light Green
        EnergyLevel.VERY_HIGH -> Color(0xFF66BB6A) // Bright Green
    }

    // Determine text color based on background color
    val textColor = Color.Black

    // Create a surface with the energy color
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = if (isSelected) energyColor else energyColor.copy(alpha = 0.3f),
        border = if (isSelected) BorderStroke(2.dp, energyColor) else null,
        modifier = Modifier
            .padding(4.dp)
            .clickable(onClick = onSelect)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(8.dp)
        ) {
            Text(
                text = energy.emoji,
                fontSize = 20.sp,
                modifier = Modifier.padding(vertical = 4.dp)
            )

            Text(
                text = energy.description,
                fontSize = 12.sp,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                color = textColor.copy(alpha = if (isSelected) 1f else 0.7f)
            )
        }
    }
}
