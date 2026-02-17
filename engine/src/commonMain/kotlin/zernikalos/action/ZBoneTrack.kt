package zernikalos.action

import kotlinx.serialization.Serializable
import kotlinx.serialization.protobuf.ProtoNumber
import kotlin.js.JsExport

@JsExport
@Serializable
data class ZBoneTrack(
    @ProtoNumber(1)
    val boneName: String = "",
    @ProtoNumber(2)
    val boneId: String = "",
) {
    @ProtoNumber(100)
    private val _positionTrack: ArrayList<ZPositionFrame> = arrayListOf()

    @ProtoNumber(101)
    private val _rotationTrack: ArrayList<ZRotationFrame> = arrayListOf()

    @ProtoNumber(102)
    private val _scaleTrack: ArrayList<ZScaleFrame> = arrayListOf()

    val positionTrack: Array<ZPositionFrame>
        get() = _positionTrack.toTypedArray()

    val rotationTrack: Array<ZRotationFrame>
        get() = _rotationTrack.toTypedArray()

    val scaleTrack: Array<ZScaleFrame>
        get() = _scaleTrack.toTypedArray()

    fun addPositionFrame(frame: ZPositionFrame) {
        _positionTrack.add(frame)
    }

    fun addRotationFrame(frame: ZRotationFrame) {
        _rotationTrack.add(frame)
    }

    fun addScaleFrame(frame: ZScaleFrame) {
        _scaleTrack.add(frame)
    }
}
