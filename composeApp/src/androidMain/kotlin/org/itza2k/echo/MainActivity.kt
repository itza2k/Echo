package org.itza2k.echo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import org.itza2k.echo.data.db.DatabaseHelper
import org.itza2k.echo.data.db.initializeAndroidContext

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize the Android context for the database
        initializeAndroidContext(this)

        // Initialize the database
        lifecycleScope.launch {
            val databaseHelper = DatabaseHelper.getInstance()
            databaseHelper.initializeDatabase()
        }

        setContent {
            App()
        }
    }
}

@Preview
@Composable
fun AppAndroidPreview() {
    App()
}
