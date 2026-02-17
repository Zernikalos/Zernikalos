package zernikalos.utils

internal fun computeRefId(str: String): Int {
    val hashValue = crc32FromStr(str)
    return if (hashValue < 0) hashValue.inv() else hashValue
}