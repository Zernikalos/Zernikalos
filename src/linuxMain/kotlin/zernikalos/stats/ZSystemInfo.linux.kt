package zernikalos.stats


actual fun getZPlatformInfo(): ZPlatformInfo {
    return ZPlatformInfo(version = "", name = ZPlatformName.LINUX)
}