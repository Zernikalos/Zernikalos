package zernikalos.ui

import zernikalos.ZSceneContext
import zernikalos.ZRenderingContext
import zernikalos.ZSceneStateHandler

class ZSurfaceStateHandlerBridge {

    private var _stateHandler: ZSceneStateHandler? = null
    private var isReady = false
    private lateinit var renderingContext: ZRenderingContext
    private lateinit var sceneContext: ZSceneContext

    var stateHandler: ZSceneStateHandler?
        get() = _stateHandler
        set(value) {
            if (value != null) attachStateHandler(value)
        }

    private fun attachStateHandler(stateHandler: ZSceneStateHandler) {
        _stateHandler = stateHandler
        readyWithHandler()
    }

    private fun readyWithHandler() {
        if (!isReady || _stateHandler == null) {
            return
        }
        sceneContext = _stateHandler!!.createSceneContext()
        _stateHandler?.onReady(sceneContext, renderingContext)
    }

    fun onReady(renderingContext: ZRenderingContext) {
        isReady = true
        this.renderingContext = renderingContext
        readyWithHandler()
    }

    fun onRender(renderingContext: ZRenderingContext) {
        _stateHandler?.onRender(sceneContext, renderingContext)
    }

}