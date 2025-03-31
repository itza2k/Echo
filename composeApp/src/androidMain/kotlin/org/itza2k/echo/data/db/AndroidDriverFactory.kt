package org.itza2k.echo.data.db

import android.content.Context
import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.android.AndroidSqliteDriver
import org.itza2k.echo.data.db.EkoDatabase

/**
 * Android-specific implementation of DriverFactory.
 * Creates an AndroidSqliteDriver for the EkoDatabase.
 */
class AndroidDriverFactory(private val context: Context) : DriverFactory {
    /**
     * Create a SqlDriver for the given database name.
     * 
     * @param databaseName The name of the database to create a driver for.
     * @return A SqlDriver instance for the specified database.
     */
    override fun createDriver(databaseName: String): SqlDriver {
        return AndroidSqliteDriver(
            schema = EkoDatabase.Schema,
            context = context,
            name = databaseName
        )
    }
}

/**
 * Android implementation of createPlatformDriverFactory.
 * This function is called from common code to get the platform-specific DriverFactory.
 * 
 * Note: This is a global variable that will be initialized in MainActivity.
 */
private lateinit var androidContext: Context

/**
 * Set the Android context for the DriverFactory.
 * This should be called from MainActivity.onCreate().
 */
fun initializeAndroidContext(context: Context) {
    androidContext = context.applicationContext
}

/**
 * Actual implementation of the expected function from common code.
 */
actual fun createPlatformDriverFactory(): DriverFactory {
    return AndroidDriverFactory(androidContext)
}
