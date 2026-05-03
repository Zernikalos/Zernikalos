/*
 * Copyright (c) 2024. Aarón Negrín - Zernikalos Engine.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package zernikalos.loader

import android.content.Context

/**
 * Loads a ZKo from the app's private file store ([Context.openFileInput]), not from assets or arbitrary paths.
 *
 * **Threading:** Performs blocking I/O and decoding on the caller's coroutine context. Do not call from the
 * main thread unless the call site wraps the invocation in `withContext` with a background dispatcher
 * (for example `Dispatchers.IO` from kotlinx.coroutines).
 */
suspend fun loadFromInternalFile(context: Context, fileName: String): ZKo {
    val content = context.openFileInput(fileName).buffered().readBytes()
    return loadFromProto(content)
}

/**
 * Loads a ZKo from [Context.getAssets] using a path relative to the assets root (e.g. `"gltf/model.zko"`).
 *
 * **Threading:** Performs blocking I/O and decoding on the caller's coroutine context. Do not call from the
 * main thread unless the call site wraps the invocation in `withContext` with a background dispatcher
 * (for example `Dispatchers.IO` from kotlinx.coroutines).
 */
suspend fun loadFromAssets(context: Context, assetPath: String): ZKo {
    val content = context.assets.open(assetPath).use { it.readBytes() }
    return loadFromProto(content)
}
