package mr.robotto.ui

import mr.robotto.MrRenderingContext
import mr.robotto.MrSceneContext
import mr.robotto.MrSceneStateHandler

class MrSurfaceStateHandlerBridge {

    private var _stateHandler: MrSceneStateHandler? = null
    private var isReady = false
    private lateinit var renderingContext: MrRenderingContext
    private lateinit var sceneContext: MrSceneContext

    var stateHandler: MrSceneStateHandler?
        get() = _stateHandler
        set(value) {
            if (value != null) attachStateHandler(value)
        }

    private fun attachStateHandler(stateHandler: MrSceneStateHandler) {
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

    fun onReady(renderingContext: MrRenderingContext) {
        isReady = true
        this.renderingContext = renderingContext
        readyWithHandler()
    }

    fun onRender(renderingContext: MrRenderingContext) {
        _stateHandler?.onRender(sceneContext, renderingContext)
    }

}