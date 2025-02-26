package zernikalos.utils

import kotlin.uuid.Uuid

fun genRefId(): String {
    return Uuid.random().toString()
}