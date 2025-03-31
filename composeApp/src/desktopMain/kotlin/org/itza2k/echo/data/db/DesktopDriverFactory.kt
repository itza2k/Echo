package org.itza2k.echo.data.db

import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver
import java.io.File
import org.itza2k.echo.data.db.EkoDatabase

/**
 * Desktop-specific implementation of DriverFactory.
 * Creates a JdbcSqliteDriver for the EkoDatabase.
 */
class DesktopDriverFactory : DriverFactory {
    /**
     * Create a SqlDriver for the given database name.
     * 
     * @param databaseName The name of the database to create a driver for.
     * @return A SqlDriver instance for the specified database.
     */
    override fun createDriver(databaseName: String): SqlDriver {
        // Create the database directory if it doesn't exist
        val databasePath = "databases"
        val databaseDir = File(databasePath)
        if (!databaseDir.exists()) {
            databaseDir.mkdirs()
        }
        
        // Create the database file path
        val databaseFile = File(databaseDir, "$databaseName.db")
        val driver = JdbcSqliteDriver("jdbc:sqlite:${databaseFile.absolutePath}")
        
        // Create the database schema if it doesn't exist
        if (!databaseFile.exists()) {
            EkoDatabase.Schema.create(driver)
        }
        
        return driver
    }
}

/**
 * Actual implementation of the expected function from common code.
 */
actual fun createPlatformDriverFactory(): DriverFactory {
    return DesktopDriverFactory()
}