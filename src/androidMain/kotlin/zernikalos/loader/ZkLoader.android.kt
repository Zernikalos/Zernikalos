package zernikalos.loader

import android.content.Context
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import zernikalos.objects.ZObject

suspend fun loadFromFile(context: Context, fileName: String): ZObject = coroutineScope {
    val loaded = async<ZObject> {
        val content = context.openFileInput(fileName).buffered().readBytes()
        return@async loadFromProto(content)
    }
    loaded.await()
}