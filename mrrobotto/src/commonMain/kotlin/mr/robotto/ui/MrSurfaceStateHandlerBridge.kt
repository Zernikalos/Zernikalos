package mr.robotto.ui

import mr.robotto.MrRenderingContext
import mr.robotto.MrSceneStateHandler

class MrSurfaceStateHandlerBridge {

    private var _stateHandler: MrSceneStateHandler? = null
    var stateHandler: MrSceneStateHandler?
        get() = _stateHandler
        set(value) {
            _stateHandler = value
            if (isReady) {
                _stateHandler?.onReady(renderingContext)
            }
        }

    private var isReady = false
    private lateinit var renderingContext: MrRenderingContext

    fun onReady(renderingContext: MrRenderingContext) {
        isReady = true
        this.renderingContext = renderingContext
        if (stateHandler == null) {
            return
        }
        stateHandler?.onReady(renderingContext)
    }

}