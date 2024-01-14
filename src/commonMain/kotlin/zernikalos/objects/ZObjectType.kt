package zernikalos.objects

import kotlin.js.JsExport

@JsExport
enum class ZObjectType {
    OBJECT,
    SCENE,
    MODEL,
    GROUP,
    CAMERA,
    SKELETON;
}