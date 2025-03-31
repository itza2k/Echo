package org.itza2k.echo.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import org.itza2k.echo.ui.theme.LocalResonanceColors

/**
 * A component that displays simple statistics about completed tasks.
 */
@Composable
fun SimpleStatsDisplay(
    tasksCompletedToday: Int,
    tasksCompletedThisWeek: Int,
    totalTasksCompleted: Int,
    modifier: Modifier = Modifier
) {
    val colorScheme = LocalResonanceColors.current
    
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = colorScheme.surfaceVariant,
            contentColor = colorScheme.onSurfaceVariant
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Your Progress",
                style = MaterialTheme.typography.titleLarge,
                color = colorScheme.onSurfaceVariant
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Today's stats
            StatRow(
                label = "Tasks completed today:",
                value = tasksCompletedToday.toString()
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Weekly stats
            StatRow(
                label = "Tasks completed this week:",
                value = tasksCompletedThisWeek.toString()
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Total stats
            StatRow(
                label = "Total tasks completed:",
                value = totalTasksCompleted.toString()
            )
        }
    }
}

@Composable
private fun StatRow(
    label: String,
    value: String
) {
    val colorScheme = LocalResonanceColors.current
    
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyLarge,
            color = colorScheme.onSurfaceVariant.copy(alpha = 0.8f),
            modifier = Modifier.weight(1f)
        )
        
        Spacer(modifier = Modifier.width(8.dp))
        
        Text(
            text = value,
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.Bold,
            color = colorScheme.primary
        )
    }
}