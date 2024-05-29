/*
 * Copyright (c) 2024. Aarón Negrín - Zernikalos Engine.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package zernikalos.loader

import org.khronos.webgl.Uint8Array
import zernikalos.objects.ZObject

@JsExport
fun loadFromUint8Array(array: Uint8Array): ZObject {
    return loadFromProto(array.asDynamic() as ByteArray)
}