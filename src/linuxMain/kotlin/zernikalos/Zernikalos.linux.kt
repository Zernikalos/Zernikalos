import zernikalos.ZernikalosBase

class Zernikalos: ZernikalosBase() {

    fun sayHello() {
        println("Hello from Zernikalos Linux")
        println(stats.platform.name)
    }

}