package zernikalos.generators.shadergenerator

import zernikalos.components.material.ZMaterial
import zernikalos.components.material.ZPbrMaterialData
import zernikalos.components.material.ZTexture
import zernikalos.components.mesh.ZMesh
import zernikalos.components.shader.ZAttributeId
import zernikalos.components.shader.ZShaderProgram
import zernikalos.components.skeleton.ZBone
import zernikalos.math.ZColor
import zernikalos.objects.ZModel
import zernikalos.objects.ZSkeleton
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class PipelineCapabilitiesBuilderTest {

    @Test
    fun builderEnablesCapabilitiesAndMatchingMeshBuffers() {
        val model = ZModel().apply {
            mesh = ZMesh().apply {
                addVec3Buffer(ZAttributeId.POSITION, 1, floatArrayOf(0f, 0f, 0f))
                addVec3Buffer(ZAttributeId.NORMAL, 1, floatArrayOf(0f, 1f, 0f))
                addVec3Buffer(ZAttributeId.COLOR, 1, floatArrayOf(1f, 0f, 0f))
                addVec2Buffer(ZAttributeId.UV, 1, floatArrayOf(0f, 0f))
                addUShortIndexBuffer(shortArrayOf(0))
            }
        }

        val capabilities = pipelineCapabilitiesBuilder(model, ZShaderProgram())

        assertTrue(capabilities.usePosition)
        assertTrue(capabilities.useNormals)
        assertTrue(capabilities.useColors)
        assertFalse(capabilities.useTextures)
        assertTrue(model.mesh.position!!.enabled)
        assertTrue(model.mesh.normal!!.enabled)
        assertTrue(model.mesh.color!!.enabled)
        assertFalse(model.mesh.uv!!.enabled)
        assertTrue(model.mesh.indexBuffer!!.enabled)
    }

    @Test
    fun builderEnablesTextureOnlyWhenModelHasTextureAndUvBuffer() {
        val material = ZMaterial().apply {
            texture = ZTexture("texture", 1, 1, flipX = false, flipY = true, dataArray = byteArrayOf())
        }
        val modelWithoutUv = ZModel().apply {
            this.material = material
        }
        val modelWithUv = ZModel().apply {
            this.material = material
            mesh = ZMesh().apply {
                addVec2Buffer(ZAttributeId.UV, 1, floatArrayOf(0f, 0f))
            }
        }

        val withoutUv = pipelineCapabilitiesBuilder(modelWithoutUv, ZShaderProgram())
        val withUv = pipelineCapabilitiesBuilder(modelWithUv, ZShaderProgram())

        assertFalse(withoutUv.useTextures)
        assertFalse(withoutUv.flipTextureY)
        assertTrue(withUv.useTextures)
        assertTrue(withUv.flipTextureY)
        assertTrue(modelWithUv.mesh.uv!!.enabled)
    }

    @Test
    fun builderEnablesSkinningFromSkeleton() {
        val root = ZBone().apply { id = "root" }
        root.addChild(ZBone().apply { id = "child" })
        val model = ZModel().apply {
            skeleton = ZSkeleton().apply { this.root = root }
        }

        val capabilities = pipelineCapabilitiesBuilder(model, ZShaderProgram())

        assertTrue(capabilities.useSkinning)
        assertEquals(2, capabilities.maxBones)
    }

    @Test
    fun builderEnablesLightingOnlyWithNormalsAndLitMaterial() {
        val material = ZMaterial().apply {
            pbr = ZPbrMaterialData(
                ZColor.WHITE,
                ZColor.BLACK,
                0f,
                0f,
                1f,
            )
        }
        val modelWithoutNormals = ZModel().apply {
            this.material = material
        }
        val modelWithNormals = ZModel().apply {
            this.material = material
            mesh = ZMesh().apply {
                addVec3Buffer(ZAttributeId.NORMAL, 1, floatArrayOf(0f, 1f, 0f))
            }
        }

        val withoutNormals = pipelineCapabilitiesBuilder(modelWithoutNormals, ZShaderProgram())
        val withNormals = pipelineCapabilitiesBuilder(modelWithNormals, ZShaderProgram())

        assertTrue(withoutNormals.usePbrMaterial)
        assertFalse(withoutNormals.useLighting)
        assertTrue(withNormals.usePbrMaterial)
        assertTrue(withNormals.useLighting)
    }
}
