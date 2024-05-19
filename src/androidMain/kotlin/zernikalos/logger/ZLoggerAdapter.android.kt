package zernikalos.logger

import android.util.Log

actual class ZLoggerAdapterConsole : ZLoggerAdapter {
    private val TAG = "zernikalos"
    actual override fun debug(message: String) {
        Log.d(TAG, message)
    }

    actual override fun info(message: String) {
        Log.i(TAG, message)
    }

    actual override fun warn(message: String) {
        Log.w(TAG, message)
    }

    actual override fun error(message: String) {
        Log.e(TAG, message)
    }

}