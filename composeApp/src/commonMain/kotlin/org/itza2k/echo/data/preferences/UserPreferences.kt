package org.itza2k.echo.data.preferences

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * A class to manage user preferences across the app.
 * This includes the user's name and other settings.
 */
class UserPreferences {
    // User name state
    private val _userName = MutableStateFlow<String?>(null)
    val userName: StateFlow<String?> = _userName.asStateFlow()

    // Flag to indicate if the welcome screen has been shown
    // Initialize to true to ensure welcome screen only appears once
    private val _hasCompletedOnboarding = MutableStateFlow(true)
    val hasCompletedOnboarding: StateFlow<Boolean> = _hasCompletedOnboarding.asStateFlow()

    /**
     * Set the user's name.
     */
    fun setUserName(name: String) {
        _userName.value = name
    }

    /**
     * Mark the onboarding process as completed.
     */
    fun completeOnboarding() {
        _hasCompletedOnboarding.value = true
    }

    companion object {
        // Singleton instance
        private var instance: UserPreferences? = null

        /**
         * Get the singleton instance of UserPreferences.
         */
        fun getInstance(): UserPreferences {
            if (instance == null) {
                instance = UserPreferences()
            }
            return instance!!
        }
    }
}
