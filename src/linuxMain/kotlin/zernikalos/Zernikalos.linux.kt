import kotlinx.cinterop.ExperimentalForeignApi
import zernikalos.ZernikalosBase

import libglfw.*

class Zernikalos: ZernikalosBase() {

    fun sayHello() {
        println("Hello from Zernikalos Linux")
        println(stats.platform.name)
    }

    @OptIn(ExperimentalForeignApi::class)
    fun openWindow() {
        glfwInit()
        val window = glfwCreateWindow(640, 480, "Zernikalos", null, null)

        glfwMakeContextCurrent(window)

        while (glfwWindowShouldClose(window) == 0) {
            glfwSwapBuffers(window)
            glfwPollEvents()
        }

        glfwTerminate()
    }

}