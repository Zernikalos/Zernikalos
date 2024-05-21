/*
 * Copyright (c) 2024. Aarón Negrín - Zernikalos Engine.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

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