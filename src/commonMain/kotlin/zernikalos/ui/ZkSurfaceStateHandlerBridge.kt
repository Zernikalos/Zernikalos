package zernikalos.ui

import zernikalos.ZkSceneContext
import zernikalos.ZkRenderingContext
import zernikalos.ZkSceneStateHandler

class ZkSurfaceStateHandlerBridge {

    private var _stateHandler: ZkSceneStateHandler? = null
    private var isReady = false
    private lateinit var renderingContext: ZkRenderingContext
    private lateinit var sceneContext: ZkSceneContext

    var stateHandler: ZkSceneStateHandler?
        get() = _stateHandler
        set(value) {
            if (value != null) attachStateHandler(value)
        }

    private fun attachStateHandler(stateHandler: ZkSceneStateHandler) {
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

    fun onReady(renderingContext: ZkRenderingContext) {
        isReady = true
        this.renderingContext = renderingContext
        readyWithHandler()
    }

    fun onRender(renderingContext: ZkRenderingContext) {
        _stateHandler?.onRender(sceneContext, renderingContext)
    }

}