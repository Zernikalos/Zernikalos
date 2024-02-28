package zernikalos.loader

import zernikalos.components.ZComponent

class ZLoaderContext {

    private val componentsMap: HashMap<Int, ZComponent<*, *>> = hashMapOf()

    fun addComponent(refId: Int, component: ZComponent<*, *>?) {
        if (component == null) {
            return
        }
        componentsMap[refId] = component
    }

    fun getComponent(refId: Int): ZComponent<*, *>? {
        if (!componentsMap.containsKey(refId)) {
            throw Error("Unable to find the required component with refId: $refId")
        }
        return componentsMap[refId]
    }
}