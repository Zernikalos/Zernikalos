package zernikalos.loader

import kotlinx.serialization.Serializable
import kotlinx.serialization.protobuf.ProtoNumber

@Serializable
data class ZkoHeader(
    @ProtoNumber(1) val version: String
)

@Serializable
data class ZkoFormat(
    @ProtoNumber(1) val header: ZkoHeader,
    @ProtoNumber(2) val data: ProtoZkObject
)
