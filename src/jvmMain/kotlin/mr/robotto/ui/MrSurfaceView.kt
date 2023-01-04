package mr.robotto.ui

import org.lwjgl.glfw.*
import org.lwjgl.glfw.GLFW.*
import org.lwjgl.system.MemoryStack.*
import java.nio.IntBuffer

actual class MrSurfaceView {

    var window: Long = -1

    fun createWindow() {
        // Setup an error callback. The default implementation
        // will print the error message in System.err.
        // Setup an error callback. The default implementation
        // will print the error message in System.err.
        // GLFWErrorCallback.createPrint(System.err).set()

        // Initialize GLFW. Most GLFW functions will not work before doing this.

        // Initialize GLFW. Most GLFW functions will not work before doing this.
        check(glfwInit()) { "Unable to initialize GLFW" }

        // Configure GLFW

        // Configure GLFW
        glfwDefaultWindowHints() // optional, the current window hints are already the default

        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE) // the window will stay hidden after creation

        glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE) // the window will be resizable


        // Create the window

        // Create the window
        window = glfwCreateWindow(800, 600, "Hello World!", 0, 0)
        if (window == null) throw RuntimeException("Failed to create the GLFW window")

        // Setup a key callback. It will be called every time a key is pressed, repeated or released.

        // Setup a key callback. It will be called every time a key is pressed, repeated or released.
        glfwSetKeyCallback(window) { window, key, scancode, action, mods ->
            if (key === GLFW_KEY_ESCAPE && action === GLFW_RELEASE) glfwSetWindowShouldClose(
                window,
                true
            ) // We will detect this in the rendering loop
        }

        // Get the thread stack and push a new frame
        stackPush().use { stack ->
            val pWidth: IntBuffer = stack.mallocInt(1) // int*
            val pHeight: IntBuffer = stack.mallocInt(1) // int*

            // Get the window size passed to glfwCreateWindow
            glfwGetWindowSize(window, pWidth, pHeight)

            // Get the resolution of the primary monitor
            val vidmode: GLFWVidMode = glfwGetVideoMode(glfwGetPrimaryMonitor())!!

            // Center the window
            glfwSetWindowPos(
                window,
                (vidmode.width() - pWidth.get(0)) / 2,
                (vidmode.height() - pHeight.get(0)) / 2
            )
        }

        // Make the OpenGL context current

        // Make the OpenGL context current
        glfwMakeContextCurrent(window)
        // Enable v-sync
        // Enable v-sync
        glfwSwapInterval(1)

        // Make the window visible

        // Make the window visible
        glfwShowWindow(window)
    }
}