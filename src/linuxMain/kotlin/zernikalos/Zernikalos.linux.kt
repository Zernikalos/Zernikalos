import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.*
import zernikalos.ZernikalosBase

import libglfw.*
//import libgl.*
import libvulkan.*
//import libgl.*

class Zernikalos: ZernikalosBase() {

    fun sayHello() {
        println("Hello from Zernikalos Linux")
        println(stats.platform.name)
    }

    @OptIn(ExperimentalForeignApi::class)
    fun openWindow() {
        glfwInit()

        // glfwWindowHint(GLFW_CLIENT_API, GLFW_NO_API)
        val window = glfwCreateWindow(640, 480, "Zernikalos", null, null)

        glfwMakeContextCurrent(window)

        if (glfwVulkanSupported() != 1)
        {
            println("No Vulkan supported")
        }

        //val a = libgl.glUniformMatrix4fv()

        //VkSurfaceKHR surface
        //val err = glfwCreateWindowSurface(instance, window, NULL, &surface);

//        val pnfCreateInstance = glfwGetInstanceProcAddress(null, "vkCreateInstance")

        //vkCreateInstance()

        //PFN_vkCreateInstance pfnCreateInstance = (PFN_vkCreateInstance)
        //glfwGetInstanceProcAddress(NULL, "vkCreateInstance");


        memScoped {
            val appInfo = cValue<VkApplicationInfo> {
                sType = VK_STRUCTURE_TYPE_APPLICATION_INFO
                pApplicationName = "Hello Triangle".cstr.ptr
                applicationVersion = VK_API_VERSION_1_0
                pEngineName = "No Engine".cstr.ptr
                engineVersion = VK_API_VERSION_1_0
                apiVersion = VK_API_VERSION_1_0
            }

            val extensionCount = alloc<UIntVar>()
            val extensions = glfwGetRequiredInstanceExtensions(extensionCount.ptr)

            val createInfo = cValue<VkInstanceCreateInfo> {
                sType = VK_STRUCTURE_TYPE_INSTANCE_CREATE_INFO
                pApplicationInfo = appInfo.ptr
                enabledExtensionCount = extensionCount.value
                ppEnabledExtensionNames = extensions
                enabledLayerCount = 0.toUInt()
            }

            val instance = alloc<VkInstanceVar>()
            if (vkCreateInstance(createInfo.ptr, null, instance.ptr) != VK_SUCCESS) {
                throw Error("Failed to create instance")
            } else {
                println("Instance created")
            }

        }




        while (glfwWindowShouldClose(window) == 0) {
            //libgl.glClear(libgl.GL_COLOR_BUFFER_BIT.toUInt() or libgl.GL_DEPTH_BUFFER_BIT.toUInt())

            //libgl.glu

            // val a = libglfw.glUniformMatrix4fv
            //glClearColor(1.0f, 0.0f, 0.0f, 1.0f)
            //glClear(GL_COLOR_BUFFER_BIT.toUInt())

            glfwSwapBuffers(window)
            glfwPollEvents()
        }

        glfwTerminate()
    }

}