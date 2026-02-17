package zernikalos.loader

import kotlinx.serialization.Serializable
import kotlinx.serialization.protobuf.ProtoNumber
import zernikalos.objects.ZObject

@Serializable
data class ZkoHierarchyNode(
    @ProtoNumber(1) val refId: String,
    @ProtoNumber(100) val children: List<ZkoHierarchyNode>? = emptyList()
) {
    companion object {
        /**
         * Recursively transforms a hierarchy of `ZkoHierarchyNode` objects into corresponding `ZObject` structures,
         * linking child objects to their respective parents.
         *
         * @param hierarchyNode The root node of the hierarchy to transform.
         * @param objects A map of reference IDs to `ZkoObjectProto` objects, used to retrieve the corresponding `ZObject`.
         * @return The transformed `ZObject` representing the root of the hierarchy.
         * @throws Error If a reference ID in the hierarchy cannot be found in the `objectsMap`.
         */
        fun transformHierarchy(hierarchyNode: ZkoHierarchyNode, objects: List<ZkoObjectProto>): ZObject {
            val objectsMap = objects.associateBy { it.refId }
            return internalTransformHierarchy(hierarchyNode, objectsMap)
        }

        private fun internalTransformHierarchy(hierarchyNode: ZkoHierarchyNode, objectsMap: Map<String, ZkoObjectProto>): ZObject {
            val objProto = objectsMap[hierarchyNode.refId] ?: throw Error("Object not found")
            val obj = objProto.zObject
            hierarchyNode.children?.forEach {
                obj.addChild(internalTransformHierarchy(it, objectsMap))
            }
            return obj
        }
    }
}