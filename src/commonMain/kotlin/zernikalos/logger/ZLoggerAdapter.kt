package zernikalos.logger

abstract class ZLoggerAdapter {
    abstract fun debug(message: String)
}

expect class ZLoggerAdapterConsole(): ZLoggerAdapter {
    override fun debug(message: String)
}