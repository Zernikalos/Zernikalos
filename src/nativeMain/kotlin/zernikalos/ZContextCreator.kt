package zernikalos

import zernikalos.ui.ZSurfaceView

class ZDefaultContextCreator: ZContextCreator {
    override fun createSceneContext(surfaceView: ZSurfaceView): ZSceneContext {
        return createDefaultSceneContext()
    }

    override fun createRenderingContext(surfaceView: ZSurfaceView): ZRenderingContext {
        return ZMtlRenderingContext(surfaceView)
    }
}

fun createDefaultContextCreator(): ZContextCreator {
    return ZDefaultContextCreator()
}