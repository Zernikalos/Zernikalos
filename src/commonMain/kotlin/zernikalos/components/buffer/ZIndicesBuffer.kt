package zernikalos.components.buffer

import kotlinx.serialization.Serializable
import kotlinx.serialization.protobuf.ProtoNumber

@Serializable
class ZIndicesBuffer: ZBuffer() {

    @ProtoNumber(3)
    var size: Int = 0
    @ProtoNumber(4)
    var count: Int = 0

}