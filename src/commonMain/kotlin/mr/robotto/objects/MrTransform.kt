package mr.robotto.objects

import mr.robotto.math.MrMatrix4f
import mr.robotto.math.MrQuaternion
import mr.robotto.math.MrVector3f

class MrTransform {

    val matrix: MrMatrix4f = MrMatrix4f.Identity

    val location: MrVector3f = MrVector3f.Zero
    val rotation: MrQuaternion = MrQuaternion.Identity
    val scale: MrVector3f = MrVector3f.Ones

    val forward: MrVector3f = MrVector3f(1f, 0f, 0f)
    val right:   MrVector3f = MrVector3f(0f, 1f, 0f)
    val up:      MrVector3f = MrVector3f(0f, 0f, 1f)

}