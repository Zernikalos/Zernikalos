package zernikalos.logger

import android.util.Log

actual class ZLoggerAdapterConsole : ZLoggerAdapter() {
    actual override fun debug(message: String) {
        Log.d("zernikalos", message)
    }

}