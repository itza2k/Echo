package org.itza2k.echo.data.db

import app.cash.sqldelight.db.SqlDriver

/**
 * Interface for creating platform-specific SqlDriver instances.
 * This is implemented differently for each platform (Android, Desktop, etc.).
 */
interface DriverFactory {
    /**
     * Create a SqlDriver for the given database name.
     * 
     * @param databaseName The name of the database to create a driver for.
     * @return A SqlDriver instance for the specified database.
     */
    fun createDriver(databaseName: String): SqlDriver
}

/**
 * Expected declaration for the platform-specific DriverFactory.
 * Each platform will provide its own implementation.
 */
expect fun createPlatformDriverFactory(): DriverFactory