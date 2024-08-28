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
import zernikalos.objects.ZObject
import kotlin.experimental.ExperimentalObjCName

@OptIn(ExperimentalObjCName::class)
@ObjCName("ZkoLoader")
class ZkoLoader {

    companion object {
        @OptIn(ExperimentalForeignApi::class)
        suspend fun loadFromMainBundlePath(fileName: String): ZKo? = coroutineScope {
            // Get the URL for the file iOS
            val fileURL = NSBundle.mainBundle.URLForResource(fileName, "zko")

            if (fileURL != null) {
                // Trying to load the file content
                memScoped {
                    val data = NSData.dataWithContentsOfURL(fileURL)
                    if (data != null) {
                        val byteArray = data.bytes!!.readBytes(data.length.toInt())
                        return@coroutineScope loadFromProto(byteArray)
                    } else {
                        println("Error loading the file.")
                    }
                }
            } else {
                println("Unable to find the file.")
            }

            return@coroutineScope null
        }

        @OptIn(ExperimentalForeignApi::class)
        fun loadFromMainBundlePathSync(fileName: String): ZKo? {
            // Get the URL for the file iOS
            val fileURL = NSBundle.mainBundle.URLForResource(fileName, "zko")

            if (fileURL != null) {
                // Trying to load the file content
                memScoped {
                    val data = NSData.dataWithContentsOfURL(fileURL)
                    if (data != null) {
                        val byteArray = data.bytes!!.readBytes(data.length.toInt())
                        return loadFromProto(byteArray)
                    } else {
                        println("Error loading the file.")
                    }
                }
            } else {
                println("Unable to find the file.")
            }

            return null
        }
    }
}


