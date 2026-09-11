/*
 * Copyright (c) 2024. Aarón Negrín - Zernikalos Engine.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package zernikalos.loader

import kotlin.native.concurrent.ThreadLocal

private class StackHolder {
    val stack = ArrayDeque<ZLoaderContext>()
}

@ThreadLocal
private val holder = StackHolder()

internal actual class LoaderContextStack {

    actual fun require(): ZLoaderContext =
        holder.stack.lastOrNull()
            ?: error(
                "ZLoaderContext not set. Deserialize via ZkoLoader.load or loadFromProto."
            )

    actual fun <T> withContext(context: ZLoaderContext, block: () -> T): T {
        holder.stack.addLast(context)
        try {
            return block()
        } finally {
            holder.stack.removeLast()
        }
    }
}
