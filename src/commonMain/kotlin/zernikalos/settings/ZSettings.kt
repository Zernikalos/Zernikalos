package zernikalos.settings

import zernikalos.logger.ZLogLevel
import kotlin.js.JsExport

/**
 * Provides the settings for the Zernikalos Engine.
 */
@JsExport
class ZSettings private constructor() {
    // Holds the logger settings for the application.
    val loggerSettings: ZLoggerSettings = ZLoggerSettings()

    // Delegates the log level setting to the loggerSettings instance.
    var logLevel: ZLogLevel by loggerSettings::logLevel

    companion object {
        // The single instance of ZSettings.
        private val instance: ZSettings = ZSettings()

        /**
         * Provides access to the singleton instance of ZSettings.
         * @return The singleton instance of ZSettings.
         */
        fun getInstance(): ZSettings {
            return instance as ZSettings
        }
    }
}
