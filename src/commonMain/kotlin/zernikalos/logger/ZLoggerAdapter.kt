package zernikalos.logger

abstract class ZLoggerAdapter {
    abstract fun debug(message: String)
}

expect class ZLoggerAdapterConsole(): ZLoggerAdapter {

}