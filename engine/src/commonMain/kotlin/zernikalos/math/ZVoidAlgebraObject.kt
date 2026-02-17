package zernikalos.math

import zernikalos.ZDataType
import zernikalos.ZTypes

class ZVoidAlgebraObject: ZAlgebraObject {
    override val dataType: ZDataType = ZTypes.NONE
    override val floatArray: FloatArray = floatArrayOf()
    override val byteArray: ByteArray = byteArrayOf()
    override val byteSize: Int = 0
    override val size: Int = 0
    override val count: Int = 0
}
