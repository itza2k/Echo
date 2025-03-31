package org.itza2k.echo.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import org.itza2k.echo.ui.theme.ThemeMode

/**
 * A dialog for selecting the app theme.
 */
@Composable
fun ThemeDialog(
    currentTheme: ThemeMode,
    onThemeChange: (ThemeMode) -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Choose Theme",
                style = MaterialTheme.typography.headlineSmall
            )
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Light theme option
                ThemeOption(
                    text = "Light Theme",
                    icon = "☀️",
                    isSelected = currentTheme == ThemeMode.LIGHT,
                    onClick = { onThemeChange(ThemeMode.LIGHT) }
                )

                Spacer(modifier = Modifier.height(8.dp))

                // System theme option
                ThemeOption(
                    text = "System Default",
                    icon = "⚙️",
                    isSelected = currentTheme == ThemeMode.SYSTEM,
                    onClick = { onThemeChange(ThemeMode.SYSTEM) }
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Dark theme option
                ThemeOption(
                    text = "Dark Theme",
                    icon = "🌙",
                    isSelected = currentTheme == ThemeMode.DARK,
                    onClick = { onThemeChange(ThemeMode.DARK) }
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Divider
                Divider(
                    modifier = Modifier.fillMaxWidth(0.8f),
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f)
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Developer website link
                val uriHandler = LocalUriHandler.current

                OutlinedButton(
                    onClick = { 
                        uriHandler.openUri("https://itza2k.github.io/")
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = "Developer",
                            tint = MaterialTheme.colorScheme.primary
                        )

                        Spacer(modifier = Modifier.width(8.dp))

                        Text(
                            text = "View Developer Info",
                            color = MaterialTheme.colorScheme.primary,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = onDismiss
            ) {
                Text("Done")
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss
            ) {
                Text("Cancel")
            }
        }
    )
}

@Composable
private fun ThemeOption(
    text: String,
    icon: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        enabled = !isSelected
    ) {
        Text(
            text = "$icon  $text ${if (isSelected) "✓" else ""}",
            modifier = Modifier.padding(8.dp)
        )
    }
}
