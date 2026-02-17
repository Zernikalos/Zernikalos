package zernikalos.generators.shadergenerator

import zernikalos.components.shader.ZAttributeId

class ZShaderProgramParameters() {
    var usePosition: Boolean = true
    var useNormals: Boolean = false
    var useColors: Boolean = false
    var usePbrMaterial: Boolean = false
    var usePhongMaterial: Boolean = false
    var useTextures: Boolean = false
    var useSkinning: Boolean = false
    var flipTextureY: Boolean = false

    var maxBones: Int = 0

    constructor(attributes: Set<ZAttributeId>): this() {
        attributes.forEach {
            when(it) {
                ZAttributeId.POSITION -> usePosition = true
                ZAttributeId.NORMAL -> useNormals = true
                ZAttributeId.COLOR -> useColors = true
                ZAttributeId.UV -> useTextures = true
                ZAttributeId.BONE_INDEX -> useSkinning = true
                ZAttributeId.BONE_WEIGHT -> useSkinning = true
                else -> {}
            }
        }
    }
}