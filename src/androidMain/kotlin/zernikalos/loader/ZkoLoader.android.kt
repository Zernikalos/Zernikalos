/*
 * Copyright (c) 2024. Aarón Negrín - Zernikalos Engine.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package zernikalos.loader

import android.content.Context
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import zernikalos.objects.ZObject

suspend fun loadFromFile(context: Context, fileName: String): ZKo = coroutineScope {
    val loaded = async<ZKo> {
        val content = context.openFileInput(fileName).buffered().readBytes()
        return@async loadFromProto(content)
    }
    loaded.await()
}