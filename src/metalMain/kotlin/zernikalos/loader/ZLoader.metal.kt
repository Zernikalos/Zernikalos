package zernikalos.loader

import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.memScoped
import kotlinx.cinterop.readBytes
import kotlinx.coroutines.coroutineScope
import platform.Foundation.NSBundle
import platform.Foundation.NSData
import platform.Foundation.dataWithContentsOfURL
import zernikalos.objects.ZObject

@OptIn(ExperimentalForeignApi::class)
suspend fun loadFromFile(fileName: String): ZObject? = coroutineScope {
    // Get the URL for the file iOS
    val fileURL = NSBundle.mainBundle.URLForResource(fileName, null)

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
