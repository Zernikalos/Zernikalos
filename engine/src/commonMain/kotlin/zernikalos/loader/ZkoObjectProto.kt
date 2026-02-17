/*
 * Copyright (c) 2024. Aarón Negrín - Zernikalos Engine.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package zernikalos.loader

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.protobuf.ProtoNumber
import zernikalos.objects.*

@Serializable
data class ZkoObjectProto(
    val type: ZObjectType,
    val refId: String,
    val isReference: Boolean,
    val zObject: ZObject
)

@Serializable
data class ZkoObjectProtoDef(
    @ProtoNumber(1) val type: ZObjectType,
    @ProtoNumber(2) val refId: String,
    @ProtoNumber(3) val isReference: Boolean,

    @ProtoNumber(100) val scene: ZScene?,
    @ProtoNumber(101) val group: ZGroup?,
    @ProtoNumber(102) val model: ZModel?,
    @ProtoNumber(103) val camera: ZCamera?,
    @ProtoNumber(104) val skeleton: ZSkeleton?,
    @ProtoNumber(106) val light: ZLight?,
)

class ZkoObjectProtoSerializer(private val loaderContext: ZLoaderContext): KSerializer<ZkoObjectProto> {
    override val descriptor: SerialDescriptor
        get() = ZkoObjectProtoDef.serializer().descriptor

    override fun serialize(encoder: Encoder, value: ZkoObjectProto) {
        TODO("Not yet implemented")
    }

    override fun deserialize(decoder: Decoder): ZkoObjectProto {
        val data = decoder.decodeSerializableValue(ZkoObjectProtoDef.serializer())
        val zobj = detectZObject(data)
        loaderContext.addComponent(zobj.refId, zobj)
        return ZkoObjectProto(
            data.type,
            data.refId,
            data.isReference,
            zobj
        )
    }

    private fun detectZObject(data: ZkoObjectProtoDef): ZObject {
        when (data.type) {
            ZObjectType.SCENE -> return data.scene!!
            ZObjectType.GROUP -> return data.group!!
            ZObjectType.MODEL -> return data.model!!
            ZObjectType.CAMERA -> return data.camera!!
            ZObjectType.SKELETON -> return data.skeleton!!
            ZObjectType.LIGHT -> return data.light!!
        }
        throw Error("Type has not been found on object")
    }

}
