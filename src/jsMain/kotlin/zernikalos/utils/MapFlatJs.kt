package zernikalos.utils

@JsExport
data class MapPairJs(val key: Any, val value: Any)

@JsExport
fun mapFlatJs(m: Map<*, *>): Array<MapPairJs> {
    val l: ArrayList<MapPairJs> = arrayListOf()
    m.entries.forEach {
        val pair = MapPairJs(it.key!!, it.value!!)
        l.add(pair)
    }
    return l.toTypedArray()
}