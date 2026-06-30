/*
 * Copyright (c) 2024. Aarón Negrín - Zernikalos Engine.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package zernikalos.loader

import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.memScoped
import kotlinx.cinterop.readBytes
import kotlinx.coroutines.coroutineScope
import platform.Foundation.NSBundle
import platform.Foundation.NSData
import platform.Foundation.dataWithContentsOfURL
import kotlin.experimental.ExperimentalObjCName

@OptIn(ExperimentalObjCName::class)
@ObjCName("ZkoLoader")
class ZkoLoader {

    companion object {
        /**
         * Resolves a bundle resource path such as "Fox" or "gltf/Fox" (extension omitted).
         */
        @OptIn(ExperimentalForeignApi::class)
        private fun bundleURLForZkoPath(path: String): platform.Foundation.NSURL? {
            val normalized = path.trim('/')
            val slash = normalized.lastIndexOf('/')
            val name = if (slash >= 0) normalized.substring(slash + 1) else normalized
            val subdirectory = if (slash >= 0) normalized.substring(0, slash) else null
            return NSBundle.mainBundle.URLForResource(name, "zko", subdirectory)
        }

        @OptIn(ExperimentalForeignApi::class)
        private fun loadZkoFromBundleURL(fileURL: platform.Foundation.NSURL?): ZKo? {
            if (fileURL == null) {
                println("Unable to find the file.")
                return null
            }
            memScoped {
                val data = NSData.dataWithContentsOfURL(fileURL)
                if (data != null) {
                    val byteArray = data.bytes!!.readBytes(data.length.toInt())
                    return loadFromProto(byteArray)
                }
                println("Error loading the file.")
            }
            return null
        }

        @OptIn(ExperimentalForeignApi::class)
        suspend fun loadFromMainBundlePath(fileName: String): ZKo? = coroutineScope {
            return@coroutineScope loadZkoFromBundleURL(bundleURLForZkoPath(fileName))
        }

        @OptIn(ExperimentalForeignApi::class)
        fun loadFromMainBundlePathSync(fileName: String): ZKo? {
            return loadZkoFromBundleURL(bundleURLForZkoPath(fileName))
        }
    }
}


