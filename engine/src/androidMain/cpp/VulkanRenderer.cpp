#include <android/log.h>
#include <android/native_window_jni.h>
#include <jni.h>
#define VK_USE_PLATFORM_ANDROID_KHR
#include <vulkan/vulkan.h>
#include <shaderc/shaderc.h>

#include <algorithm>
#include <array>
#include <cstddef>
#include <cstdint>
#include <cstring>
#include <limits>
#include <mutex>
#include <stdexcept>
#include <string>
#include <unordered_map>
#include <vector>

#define ZK_LOG_TAG "ZernikalosVulkan"
#define ZK_LOGE(...) __android_log_print(ANDROID_LOG_ERROR, ZK_LOG_TAG, __VA_ARGS__)
#define ZK_LOGI(...) __android_log_print(ANDROID_LOG_INFO, ZK_LOG_TAG, __VA_ARGS__)
#define ZK_LOGW(...) __android_log_print(ANDROID_LOG_WARN, ZK_LOG_TAG, __VA_ARGS__)

namespace {

constexpr uint32_t kVertexAttributeFieldCount = 5;

const char* kFixedVertexShaderSpvBase64 =
    "AwIjBwAAAQAKAA0AKgAAAAAAAAARAAIAAQAAAAsABgABAAAAR0xTTC5zdGQuNDUwAAAAAA4AAwAAAAAAAQAAAA8ACAAAAAAABAAAAG1haW4AAAAADQAAABIAAAAgAAAAAwADAAIAAADCAQAABAAKAEdMX0dPT0dMRV9jcHBfc3R5bGVfbGluZV9kaXJlY3RpdmUAAAQACABHTF9HT09HTEVfaW5jbHVkZV9kaXJlY3RpdmUABQAEAAQAAABtYWluAAAAAAUABgALAAAAZ2xfUGVyVmVydGV4AAAAAAYABgALAAAAAAAAAGdsX1Bvc2l0aW9uAAYABwALAAAAAQAAAGdsX1BvaW50U2l6ZQAAAAAGAAcACwAAAAIAAABnbF9DbGlwRGlzdGFuY2UABgAHAAsAAAADAAAAZ2xfQ3VsbERpc3RhbmNlAAUAAwANAAAAAAAAAAUABQASAAAAYV9wb3NpdGlvbgAABQAEACAAAAB2X2NvbG9yAEgABQALAAAAAAAAAAsAAAAAAAAASAAFAAsAAAABAAAACwAAAAEAAABIAAUACwAAAAIAAAALAAAAAwAAAEgABQALAAAAAwAAAAsAAAAEAAAARwADAAsAAAACAAAARwAEABIAAAAeAAAAAAAAAEcABAAgAAAAHgAAAAAAAAATAAIAAgAAACEAAwADAAAAAgAAABYAAwAGAAAAIAAAABcABAAHAAAABgAAAAQAAAAVAAQACAAAACAAAAAAAAAAKwAEAAgAAAAJAAAAAQAAABwABAAKAAAABgAAAAkAAAAeAAYACwAAAAcAAAAGAAAACgAAAAoAAAAgAAQADAAAAAMAAAALAAAAOwAEAAwAAAANAAAAAwAAABUABAAOAAAAIAAAAAEAAAArAAQADgAAAA8AAAAAAAAAFwAEABAAAAAGAAAAAwAAACAABAARAAAAAQAAABAAAAA7AAQAEQAAABIAAAABAAAAKwAEAAgAAAATAAAAAAAAACAABAAUAAAAAQAAAAYAAAArAAQACAAAABcAAAACAAAAKwAEAAYAAAAaAAAAAAAAACsABAAGAAAAGwAAAAAAgD8gAAQAHQAAAAMAAAAHAAAAIAAEAB8AAAADAAAAEAAAADsABAAfAAAAIAAAAAMAAAArAAQABgAAACMAAAAAAAA/KwAEAAYAAAAlAAAAmplZPzYABQACAAAABAAAAAAAAAADAAAA+AACAAUAAABBAAUAFAAAABUAAAASAAAAEwAAAD0ABAAGAAAAFgAAABUAAABBAAUAFAAAABgAAAASAAAAFwAAAD0ABAAGAAAAGQAAABgAAABQAAcABwAAABwAAAAWAAAAGQAAABoAAAAbAAAAQQAFAB0AAAAeAAAADQAAAA8AAAA+AAMAHgAAABwAAABBAAUAFAAAACEAAAASAAAAEwAAAD0ABAAGAAAAIgAAACEAAACBAAUABgAAACQAAAAiAAAAIwAAAEEABQAUAAAAJgAAABIAAAAXAAAAPQAEAAYAAAAnAAAAJgAAAIEABQAGAAAAKAAAACcAAAAjAAAAUAAGABAAAAApAAAAJAAAACUAAAAoAAAAPgADACAAAAApAAAA/QABADgAAQA=";

const char* kFixedFragmentShaderSpvBase64 =
    "AwIjBwAAAQAKAA0AEwAAAAAAAAARAAIAAQAAAAsABgABAAAAR0xTTC5zdGQuNDUwAAAAAA4AAwAAAAAAAQAAAA8ABwAEAAAABAAAAG1haW4AAAAACQAAAAwAAAAQAAMABAAAAAcAAAADAAMAAgAAAMIBAAAEAAoAR0xfR09PR0xFX2NwcF9zdHlsZV9saW5lX2RpcmVjdGl2ZQAABAAIAEdMX0dPT0dMRV9pbmNsdWRlX2RpcmVjdGl2ZQAFAAQABAAAAG1haW4AAAAABQAFAAkAAABvdXRfY29sb3IAAAAFAAQADAAAAHZfY29sb3IARwAEAAkAAAAeAAAAAAAAAEcABAAMAAAAHgAAAAAAAAATAAIAAgAAACEAAwADAAAAAgAAABYAAwAGAAAAIAAAABcABAAHAAAABgAAAAQAAAAgAAQACAAAAAMAAAAHAAAAOwAEAAgAAAAJAAAAAwAAABcABAAKAAAABgAAAAMAAAAgAAQACwAAAAEAAAAKAAAAOwAEAAsAAAAMAAAAAQAAACsABAAGAAAADgAAAAAAgD82AAUAAgAAAAQAAAAAAAAAAwAAAPgAAgAFAAAAPQAEAAoAAAANAAAADAAAAFEABQAGAAAADwAAAA0AAAAAAAAAUQAFAAYAAAAQAAAADQAAAAEAAABRAAUABgAAABEAAAANAAAAAgAAAFAABwAHAAAAEgAAAA8AAAAQAAAAEQAAAA4AAAA+AAMACQAAABIAAAD9AAEAOAABAA==";

std::vector<uint8_t> decodeBase64(const char* input) {
    static constexpr unsigned char kInvalid = 0xFF;
    unsigned char table[256];
    std::fill(table, table + 256, kInvalid);
    for (int i = 0; i < 26; ++i) {
        table[static_cast<unsigned char>('A' + i)] = static_cast<unsigned char>(i);
        table[static_cast<unsigned char>('a' + i)] = static_cast<unsigned char>(26 + i);
    }
    for (int i = 0; i < 10; ++i) {
        table[static_cast<unsigned char>('0' + i)] = static_cast<unsigned char>(52 + i);
    }
    table[static_cast<unsigned char>('+')] = 62;
    table[static_cast<unsigned char>('/')] = 63;

    std::vector<uint8_t> out;
    int value = 0;
    int bits = -8;
    for (const unsigned char* p = reinterpret_cast<const unsigned char*>(input); *p; ++p) {
        if (*p == '=') break;
        unsigned char decoded = table[*p];
        if (decoded == kInvalid) continue;
        value = (value << 6) + decoded;
        bits += 6;
        if (bits >= 0) {
            out.push_back(static_cast<uint8_t>((value >> bits) & 0xFF));
            bits -= 8;
        }
    }
    return out;
}

const char* vkResultName(VkResult result) {
    switch (result) {
        case VK_SUCCESS: return "VK_SUCCESS";
        case VK_NOT_READY: return "VK_NOT_READY";
        case VK_TIMEOUT: return "VK_TIMEOUT";
        case VK_EVENT_SET: return "VK_EVENT_SET";
        case VK_EVENT_RESET: return "VK_EVENT_RESET";
        case VK_INCOMPLETE: return "VK_INCOMPLETE";
        case VK_ERROR_OUT_OF_HOST_MEMORY: return "VK_ERROR_OUT_OF_HOST_MEMORY";
        case VK_ERROR_OUT_OF_DEVICE_MEMORY: return "VK_ERROR_OUT_OF_DEVICE_MEMORY";
        case VK_ERROR_INITIALIZATION_FAILED: return "VK_ERROR_INITIALIZATION_FAILED";
        case VK_ERROR_DEVICE_LOST: return "VK_ERROR_DEVICE_LOST";
        case VK_ERROR_MEMORY_MAP_FAILED: return "VK_ERROR_MEMORY_MAP_FAILED";
        case VK_ERROR_LAYER_NOT_PRESENT: return "VK_ERROR_LAYER_NOT_PRESENT";
        case VK_ERROR_EXTENSION_NOT_PRESENT: return "VK_ERROR_EXTENSION_NOT_PRESENT";
        case VK_ERROR_FEATURE_NOT_PRESENT: return "VK_ERROR_FEATURE_NOT_PRESENT";
        case VK_ERROR_INCOMPATIBLE_DRIVER: return "VK_ERROR_INCOMPATIBLE_DRIVER";
        case VK_ERROR_TOO_MANY_OBJECTS: return "VK_ERROR_TOO_MANY_OBJECTS";
        case VK_ERROR_FORMAT_NOT_SUPPORTED: return "VK_ERROR_FORMAT_NOT_SUPPORTED";
        case VK_ERROR_SURFACE_LOST_KHR: return "VK_ERROR_SURFACE_LOST_KHR";
        case VK_ERROR_NATIVE_WINDOW_IN_USE_KHR: return "VK_ERROR_NATIVE_WINDOW_IN_USE_KHR";
        case VK_SUBOPTIMAL_KHR: return "VK_SUBOPTIMAL_KHR";
        case VK_ERROR_OUT_OF_DATE_KHR: return "VK_ERROR_OUT_OF_DATE_KHR";
        case VK_ERROR_INCOMPATIBLE_DISPLAY_KHR: return "VK_ERROR_INCOMPATIBLE_DISPLAY_KHR";
        case VK_ERROR_VALIDATION_FAILED_EXT: return "VK_ERROR_VALIDATION_FAILED_EXT";
        default: return "VK_RESULT_UNKNOWN";
    }
}

void check(VkResult result, const char* op) {
    if (result != VK_SUCCESS) {
        ZK_LOGE("%s failed with %s (%d)", op, vkResultName(result), result);
        throw std::runtime_error(op);
    }
}

struct QueueFamily {
    uint32_t index = UINT32_MAX;
    bool valid() const { return index != UINT32_MAX; }
};

struct NativeMesh {
    VkBuffer vertexBuffer = VK_NULL_HANDLE;
    VkDeviceMemory vertexMemory = VK_NULL_HANDLE;
    VkBuffer indexBuffer = VK_NULL_HANDLE;
    VkDeviceMemory indexMemory = VK_NULL_HANDLE;
    uint32_t vertexCount = 0;
    uint32_t indexCount = 0;
};

struct NativeBuffer {
    VkBuffer buffer = VK_NULL_HANDLE;
    VkDeviceMemory memory = VK_NULL_HANDLE;
    VkDeviceSize size = 0;
    bool indexBuffer = false;
};

struct NativeUniformBuffer {
    VkBuffer buffer = VK_NULL_HANDLE;
    VkDeviceMemory memory = VK_NULL_HANDLE;
    VkDeviceSize size = 0;
    VkDescriptorSet descriptorSet = VK_NULL_HANDLE;
};

struct VertexAttribute {
    uint32_t location = 0;
    uint32_t binding = 0;
    uint32_t format = 3;
    uint32_t offset = 0;
    uint32_t stride = 0;
};

struct NativePipeline {
    VkDescriptorSetLayout descriptorSetLayout = VK_NULL_HANDLE;
    VkPipelineLayout layout = VK_NULL_HANDLE;
    VkPipeline pipeline = VK_NULL_HANDLE;
    uint32_t drawMode = 2;
    std::vector<VertexAttribute> vertexAttributes;
    std::vector<uint32_t> uniformBindings;
    std::vector<uint32_t> vertexShaderSpv;
    std::vector<uint32_t> fragmentShaderSpv;
};

struct NativeDescriptorSet {
    NativePipeline* pipeline = nullptr;
    VkDescriptorSet descriptorSet = VK_NULL_HANDLE;
};

enum class CommandType {
    SetPipeline,
    BindDescriptorSet,
    BindUniformBuffer,
    BindVertexBuffer,
    BindIndexBuffer,
    DrawIndexed,
    DrawMesh,
};

struct RenderCommand {
    CommandType type = CommandType::DrawIndexed;
    NativePipeline* pipeline = nullptr;
    NativeDescriptorSet* descriptorSet = nullptr;
    NativeBuffer* buffer = nullptr;
    NativeUniformBuffer* uniformBuffer = nullptr;
    NativeMesh* mesh = nullptr;
    uint32_t binding = 0;
    uint32_t slot = 0;
    uint32_t count = 0;
};

class VulkanRenderer {
public:
    VulkanRenderer(ANativeWindow* window, int width, int height)
        : window_(window), width_(width), height_(height) {
        ANativeWindow_acquire(window_);
        createInstance();
        createSurface();
        pickPhysicalDevice();
        createDevice();
        createDescriptorPool();
        createCommandPool();
        createSyncObjects();
        recreateSwapchain();
    }

    ~VulkanRenderer() {
        std::lock_guard<std::mutex> lock(mutex_);
        if (device_ != VK_NULL_HANDLE) {
            vkDeviceWaitIdle(device_);
        }
        for (NativeMesh* mesh : meshes_) {
            destroyMesh(mesh);
            delete mesh;
        }
        meshes_.clear();
        for (NativeBuffer* buffer : buffers_) {
            destroyNativeBuffer(buffer);
            delete buffer;
        }
        buffers_.clear();
        for (NativeUniformBuffer* uniformBuffer : uniformBuffers_) {
            destroyNativeUniformBuffer(uniformBuffer);
            delete uniformBuffer;
        }
        uniformBuffers_.clear();
        for (NativeDescriptorSet* descriptorSet : descriptorSets_) {
            destroyNativeDescriptorSet(descriptorSet);
            delete descriptorSet;
        }
        descriptorSets_.clear();
        for (NativePipeline* pipeline : pipelines_) {
            destroyPipelineObjects(pipeline);
            delete pipeline;
        }
        pipelines_.clear();
        commands_.clear();
        cleanupSwapchain();
        if (imageAvailable_ != VK_NULL_HANDLE) vkDestroySemaphore(device_, imageAvailable_, nullptr);
        if (renderFinished_ != VK_NULL_HANDLE) vkDestroySemaphore(device_, renderFinished_, nullptr);
        if (inFlight_ != VK_NULL_HANDLE) vkDestroyFence(device_, inFlight_, nullptr);
        if (commandPool_ != VK_NULL_HANDLE) vkDestroyCommandPool(device_, commandPool_, nullptr);
        if (descriptorPool_ != VK_NULL_HANDLE) vkDestroyDescriptorPool(device_, descriptorPool_, nullptr);
        if (device_ != VK_NULL_HANDLE) vkDestroyDevice(device_, nullptr);
        if (surface_ != VK_NULL_HANDLE) vkDestroySurfaceKHR(instance_, surface_, nullptr);
        if (instance_ != VK_NULL_HANDLE) vkDestroyInstance(instance_, nullptr);
        if (window_ != nullptr) ANativeWindow_release(window_);
    }

    void resize(ANativeWindow* window, int width, int height) {
        std::lock_guard<std::mutex> lock(mutex_);
        width_ = width;
        height_ = height;
        if (window != window_) {
            ANativeWindow_acquire(window);
            if (window_ != nullptr) ANativeWindow_release(window_);
            window_ = window;
        }
        if (device_ == VK_NULL_HANDLE || width_ <= 0 || height_ <= 0) return;
        vkDeviceWaitIdle(device_);
        recreateSwapchain();
    }

    void setLastError(const std::string& message) {
        std::lock_guard<std::mutex> lock(mutex_);
        lastError_ = message;
    }

    std::string getLastError() {
        std::lock_guard<std::mutex> lock(mutex_);
        return lastError_;
    }

    NativeMesh* createMesh(
        const uint8_t* positions,
        size_t positionsSize,
        uint32_t vertexCount,
        const uint8_t* indices,
        size_t indicesSize,
        uint32_t indexCount
    ) {
        std::lock_guard<std::mutex> lock(mutex_);
        if (device_ == VK_NULL_HANDLE || positions == nullptr || indices == nullptr) return nullptr;
        if (positionsSize < static_cast<size_t>(vertexCount) * sizeof(float) * 3) return nullptr;
        if (indicesSize < static_cast<size_t>(indexCount) * sizeof(uint16_t)) return nullptr;

        auto* mesh = new NativeMesh();
        mesh->vertexCount = vertexCount;
        mesh->indexCount = indexCount;
        try {
            createBuffer(
                positionsSize,
                VK_BUFFER_USAGE_VERTEX_BUFFER_BIT,
                VK_MEMORY_PROPERTY_HOST_VISIBLE_BIT | VK_MEMORY_PROPERTY_HOST_COHERENT_BIT,
                mesh->vertexBuffer,
                mesh->vertexMemory
            );
            uploadBuffer(mesh->vertexMemory, positions, positionsSize);
            createBuffer(
                indicesSize,
                VK_BUFFER_USAGE_INDEX_BUFFER_BIT,
                VK_MEMORY_PROPERTY_HOST_VISIBLE_BIT | VK_MEMORY_PROPERTY_HOST_COHERENT_BIT,
                mesh->indexBuffer,
                mesh->indexMemory
            );
            uploadBuffer(mesh->indexMemory, indices, indicesSize);
        } catch (...) {
            destroyMesh(mesh);
            delete mesh;
            throw;
        }
        meshes_.push_back(mesh);
        ZK_LOGI("Created Vulkan mesh vertices=%u indices=%u", vertexCount, indexCount);
        return mesh;
    }

    void queueMeshDraw(NativeMesh* mesh) {
        std::lock_guard<std::mutex> lock(mutex_);
        if (mesh != nullptr && mesh->vertexBuffer != VK_NULL_HANDLE && mesh->indexBuffer != VK_NULL_HANDLE) {
            RenderCommand command{};
            command.type = CommandType::DrawMesh;
            command.mesh = mesh;
            commands_.push_back(command);
        }
    }

    void disposeMesh(NativeMesh* mesh) {
        std::lock_guard<std::mutex> lock(mutex_);
        if (mesh == nullptr) return;
        commands_.erase(
            std::remove_if(
                commands_.begin(),
                commands_.end(),
                [mesh](const RenderCommand& command) { return command.mesh == mesh; }
            ),
            commands_.end()
        );
        meshes_.erase(std::remove(meshes_.begin(), meshes_.end(), mesh), meshes_.end());
        destroyMesh(mesh);
        delete mesh;
    }

    NativeBuffer* createNativeBuffer(const uint8_t* bytes, size_t byteSize, bool indexBuffer) {
        std::lock_guard<std::mutex> lock(mutex_);
        if (device_ == VK_NULL_HANDLE || bytes == nullptr || byteSize == 0) return nullptr;

        auto* buffer = new NativeBuffer();
        buffer->size = static_cast<VkDeviceSize>(byteSize);
        buffer->indexBuffer = indexBuffer;
        VkBufferUsageFlags usage = indexBuffer ? VK_BUFFER_USAGE_INDEX_BUFFER_BIT : VK_BUFFER_USAGE_VERTEX_BUFFER_BIT;
        try {
            createBuffer(
                buffer->size,
                usage,
                VK_MEMORY_PROPERTY_HOST_VISIBLE_BIT | VK_MEMORY_PROPERTY_HOST_COHERENT_BIT,
                buffer->buffer,
                buffer->memory
            );
            uploadBuffer(buffer->memory, bytes, byteSize);
        } catch (...) {
            destroyNativeBuffer(buffer);
            delete buffer;
            throw;
        }
        buffers_.push_back(buffer);
        ZK_LOGI("Created Vulkan NativeBuffer bytes=%zu index=%d", byteSize, indexBuffer ? 1 : 0);
        return buffer;
    }

    void disposeNativeBuffer(NativeBuffer* buffer) {
        std::lock_guard<std::mutex> lock(mutex_);
        if (buffer == nullptr) return;
        commands_.erase(
            std::remove_if(
                commands_.begin(),
                commands_.end(),
                [buffer](const RenderCommand& command) { return command.buffer == buffer; }
            ),
            commands_.end()
        );
        buffers_.erase(std::remove(buffers_.begin(), buffers_.end(), buffer), buffers_.end());
        destroyNativeBuffer(buffer);
        delete buffer;
        ZK_LOGI("Disposed Vulkan NativeBuffer");
    }

    NativeUniformBuffer* createNativeUniformBuffer(size_t byteSize) {
        std::lock_guard<std::mutex> lock(mutex_);
        if (device_ == VK_NULL_HANDLE || byteSize == 0 || descriptorPool_ == VK_NULL_HANDLE) return nullptr;

        auto* uniformBuffer = new NativeUniformBuffer();
        uniformBuffer->size = static_cast<VkDeviceSize>(byteSize);
        try {
            createBuffer(
                uniformBuffer->size,
                VK_BUFFER_USAGE_UNIFORM_BUFFER_BIT,
                VK_MEMORY_PROPERTY_HOST_VISIBLE_BIT | VK_MEMORY_PROPERTY_HOST_COHERENT_BIT,
                uniformBuffer->buffer,
                uniformBuffer->memory
            );
        } catch (...) {
            destroyNativeUniformBuffer(uniformBuffer);
            delete uniformBuffer;
            throw;
        }
        uniformBuffers_.push_back(uniformBuffer);
        ZK_LOGI("Created Vulkan NativeUniformBuffer bytes=%zu", byteSize);
        return uniformBuffer;
    }

    void updateNativeUniformBuffer(NativeUniformBuffer* uniformBuffer, const uint8_t* bytes, size_t byteSize) {
        std::lock_guard<std::mutex> lock(mutex_);
        if (
            uniformBuffer == nullptr ||
            uniformBuffer->memory == VK_NULL_HANDLE ||
            bytes == nullptr ||
            byteSize == 0
        ) {
            return;
        }
        uploadBuffer(uniformBuffer->memory, bytes, std::min<size_t>(byteSize, static_cast<size_t>(uniformBuffer->size)));
    }

    void disposeNativeUniformBuffer(NativeUniformBuffer* uniformBuffer) {
        std::lock_guard<std::mutex> lock(mutex_);
        if (uniformBuffer == nullptr) return;
        commands_.erase(
            std::remove_if(
                commands_.begin(),
                commands_.end(),
                [uniformBuffer](const RenderCommand& command) { return command.uniformBuffer == uniformBuffer; }
            ),
            commands_.end()
        );
        uniformBuffers_.erase(std::remove(uniformBuffers_.begin(), uniformBuffers_.end(), uniformBuffer), uniformBuffers_.end());
        destroyNativeUniformBuffer(uniformBuffer);
        delete uniformBuffer;
        ZK_LOGI("Disposed Vulkan NativeUniformBuffer");
    }

    NativePipeline* createNativePipeline(
        const std::string& vertexShaderSource,
        const std::string& fragmentShaderSource,
        const std::vector<VertexAttribute>& vertexAttributes,
        const std::vector<uint32_t>& uniformBindings,
        uint32_t drawMode
    ) {
        std::lock_guard<std::mutex> lock(mutex_);
        if (
            device_ == VK_NULL_HANDLE ||
            renderPass_ == VK_NULL_HANDLE ||
            vertexShaderSource.empty() ||
            fragmentShaderSource.empty() ||
            vertexAttributes.empty()
        ) {
            return nullptr;
        }

        auto* pipeline = new NativePipeline();
        pipeline->vertexAttributes = vertexAttributes;
        pipeline->uniformBindings = uniformBindings;
        pipeline->drawMode = drawMode;
        try {
            pipeline->vertexShaderSpv = compileShader(
                vertexShaderSource,
                shaderc_vertex_shader,
                "zernikalos_vulkan.vert"
            );
            pipeline->fragmentShaderSpv = compileShader(
                fragmentShaderSource,
                shaderc_fragment_shader,
                "zernikalos_vulkan.frag"
            );
            createPipelineObjects(pipeline);
        } catch (...) {
            destroyPipelineObjects(pipeline);
            delete pipeline;
            throw;
        }
        pipelines_.push_back(pipeline);
        ZK_LOGI(
            "Created Vulkan NativePipeline attributes=%zu uniforms=%zu drawMode=%u",
            vertexAttributes.size(),
            uniformBindings.size(),
            drawMode
        );
        return pipeline;
    }

    NativeDescriptorSet* createNativeDescriptorSet(
        NativePipeline* pipeline,
        const std::vector<uint32_t>& uniformBindings,
        const std::vector<NativeUniformBuffer*>& uniformBuffers
    ) {
        std::lock_guard<std::mutex> lock(mutex_);
        if (
            pipeline == nullptr ||
            pipeline->descriptorSetLayout == VK_NULL_HANDLE ||
            uniformBindings.size() != uniformBuffers.size()
        ) {
            return nullptr;
        }

        auto* descriptorSet = new NativeDescriptorSet();
        descriptorSet->pipeline = pipeline;
        try {
            allocateDescriptorSet(descriptorSet, uniformBindings, uniformBuffers);
        } catch (...) {
            destroyNativeDescriptorSet(descriptorSet);
            delete descriptorSet;
            throw;
        }
        descriptorSets_.push_back(descriptorSet);
        return descriptorSet;
    }

    void disposeNativeDescriptorSet(NativeDescriptorSet* descriptorSet) {
        std::lock_guard<std::mutex> lock(mutex_);
        if (descriptorSet == nullptr) return;
        commands_.erase(
            std::remove_if(
                commands_.begin(),
                commands_.end(),
                [descriptorSet](const RenderCommand& command) { return command.descriptorSet == descriptorSet; }
            ),
            commands_.end()
        );
        descriptorSets_.erase(std::remove(descriptorSets_.begin(), descriptorSets_.end(), descriptorSet), descriptorSets_.end());
        destroyNativeDescriptorSet(descriptorSet);
        delete descriptorSet;
    }

    void disposeNativePipeline(NativePipeline* pipeline) {
        std::lock_guard<std::mutex> lock(mutex_);
        if (pipeline == nullptr) return;
        commands_.erase(
            std::remove_if(
                commands_.begin(),
                commands_.end(),
                [pipeline](const RenderCommand& command) { return command.pipeline == pipeline; }
            ),
            commands_.end()
        );
        for (auto it = descriptorSets_.begin(); it != descriptorSets_.end();) {
            NativeDescriptorSet* descriptorSet = *it;
            if (descriptorSet != nullptr && descriptorSet->pipeline == pipeline) {
                destroyNativeDescriptorSet(descriptorSet);
                delete descriptorSet;
                it = descriptorSets_.erase(it);
            } else {
                ++it;
            }
        }
        pipelines_.erase(std::remove(pipelines_.begin(), pipelines_.end(), pipeline), pipelines_.end());
        destroyPipelineObjects(pipeline);
        delete pipeline;
        ZK_LOGI("Disposed Vulkan NativePipeline");
    }

    void queueSetPipeline(NativePipeline* pipeline) {
        std::lock_guard<std::mutex> lock(mutex_);
        if (pipeline == nullptr || pipeline->pipeline == VK_NULL_HANDLE) return;
        RenderCommand command{};
        command.type = CommandType::SetPipeline;
        command.pipeline = pipeline;
        commands_.push_back(command);
    }

    void queueBindDescriptorSet(NativeDescriptorSet* descriptorSet) {
        std::lock_guard<std::mutex> lock(mutex_);
        if (descriptorSet == nullptr || descriptorSet->descriptorSet == VK_NULL_HANDLE) return;
        RenderCommand command{};
        command.type = CommandType::BindDescriptorSet;
        command.descriptorSet = descriptorSet;
        commands_.push_back(command);
    }

    void setClearColor(float r, float g, float b, float a) {
        std::lock_guard<std::mutex> lock(mutex_);
        clearColor_ = {r, g, b, a};
    }

    void queueBindUniformBuffer(uint32_t binding, NativeUniformBuffer* uniformBuffer) {
        std::lock_guard<std::mutex> lock(mutex_);
        if (
            uniformBuffer == nullptr ||
            uniformBuffer->descriptorSet == VK_NULL_HANDLE
        ) {
            return;
        }
        RenderCommand command{};
        command.type = CommandType::BindUniformBuffer;
        command.uniformBuffer = uniformBuffer;
        command.binding = binding;
        commands_.push_back(command);
    }

    void queueBindVertexBuffer(uint32_t slot, NativeBuffer* buffer) {
        std::lock_guard<std::mutex> lock(mutex_);
        if (buffer == nullptr || buffer->buffer == VK_NULL_HANDLE) return;
        RenderCommand command{};
        command.type = CommandType::BindVertexBuffer;
        command.buffer = buffer;
        command.slot = slot;
        commands_.push_back(command);
    }

    void queueBindIndexBuffer(NativeBuffer* buffer) {
        std::lock_guard<std::mutex> lock(mutex_);
        if (buffer == nullptr || buffer->buffer == VK_NULL_HANDLE) return;
        RenderCommand command{};
        command.type = CommandType::BindIndexBuffer;
        command.buffer = buffer;
        commands_.push_back(command);
    }

    void queueDrawIndexed(uint32_t indexCount) {
        std::lock_guard<std::mutex> lock(mutex_);
        if (indexCount == 0) return;
        RenderCommand command{};
        command.type = CommandType::DrawIndexed;
        command.count = indexCount;
        commands_.push_back(command);
    }

    void render() {
        std::lock_guard<std::mutex> lock(mutex_);
        if (swapchain_ == VK_NULL_HANDLE || width_ <= 0 || height_ <= 0) return;

        check(vkWaitForFences(device_, 1, &inFlight_, VK_TRUE, UINT64_MAX), "vkWaitForFences");
        check(vkResetFences(device_, 1, &inFlight_), "vkResetFences");

        uint32_t imageIndex = 0;
        VkResult acquire = vkAcquireNextImageKHR(
            device_,
            swapchain_,
            UINT64_MAX,
            imageAvailable_,
            VK_NULL_HANDLE,
            &imageIndex
        );
        if (acquire == VK_ERROR_OUT_OF_DATE_KHR) {
            recreateSwapchain();
            return;
        }
        check(acquire, "vkAcquireNextImageKHR");

        VkCommandBuffer commandBuffer = commandBuffers_[imageIndex];
        recordCommandBuffer(commandBuffer, imageIndex);

        VkPipelineStageFlags waitStage = VK_PIPELINE_STAGE_COLOR_ATTACHMENT_OUTPUT_BIT;
        VkSubmitInfo submitInfo{VK_STRUCTURE_TYPE_SUBMIT_INFO};
        submitInfo.waitSemaphoreCount = 1;
        submitInfo.pWaitSemaphores = &imageAvailable_;
        submitInfo.pWaitDstStageMask = &waitStage;
        submitInfo.commandBufferCount = 1;
        submitInfo.pCommandBuffers = &commandBuffer;
        submitInfo.signalSemaphoreCount = 1;
        submitInfo.pSignalSemaphores = &renderFinished_;
        check(vkQueueSubmit(graphicsQueue_, 1, &submitInfo, inFlight_), "vkQueueSubmit");

        VkPresentInfoKHR presentInfo{VK_STRUCTURE_TYPE_PRESENT_INFO_KHR};
        presentInfo.waitSemaphoreCount = 1;
        presentInfo.pWaitSemaphores = &renderFinished_;
        presentInfo.swapchainCount = 1;
        presentInfo.pSwapchains = &swapchain_;
        presentInfo.pImageIndices = &imageIndex;
        VkResult present = vkQueuePresentKHR(presentQueue_, &presentInfo);
        if (present == VK_ERROR_OUT_OF_DATE_KHR || present == VK_SUBOPTIMAL_KHR) {
            recreateSwapchain();
            return;
        }
        check(present, "vkQueuePresentKHR");
    }

private:
    void createInstance() {
        uint32_t extensionCount = 0;
        VkResult extensionsResult = vkEnumerateInstanceExtensionProperties(nullptr, &extensionCount, nullptr);
        if (extensionsResult == VK_SUCCESS) {
            std::vector<VkExtensionProperties> availableExtensions(extensionCount);
            extensionsResult = vkEnumerateInstanceExtensionProperties(nullptr, &extensionCount, availableExtensions.data());
            if (extensionsResult == VK_SUCCESS) {
                bool hasSurface = false;
                bool hasAndroidSurface = false;
                for (const auto& extension : availableExtensions) {
                    if (std::strcmp(extension.extensionName, VK_KHR_SURFACE_EXTENSION_NAME) == 0) {
                        hasSurface = true;
                    }
                    if (std::strcmp(extension.extensionName, VK_KHR_ANDROID_SURFACE_EXTENSION_NAME) == 0) {
                        hasAndroidSurface = true;
                    }
                }
                ZK_LOGI(
                    "Vulkan instance extensions: count=%u surface=%d android_surface=%d",
                    extensionCount,
                    hasSurface ? 1 : 0,
                    hasAndroidSurface ? 1 : 0
                );
                if (!hasSurface || !hasAndroidSurface) {
                    ZK_LOGW("Required Android Vulkan surface extensions are not available");
                }
            }
        } else {
            ZK_LOGW(
                "vkEnumerateInstanceExtensionProperties failed with %s (%d)",
                vkResultName(extensionsResult),
                extensionsResult
            );
        }

        VkApplicationInfo appInfo{VK_STRUCTURE_TYPE_APPLICATION_INFO};
        appInfo.pApplicationName = "Zernikalos";
        appInfo.applicationVersion = VK_MAKE_VERSION(0, 10, 0);
        appInfo.pEngineName = "Zernikalos";
        appInfo.engineVersion = VK_MAKE_VERSION(0, 10, 0);
        appInfo.apiVersion = VK_API_VERSION_1_0;

        std::array<const char*, 2> extensions = {
            "VK_KHR_surface",
            "VK_KHR_android_surface",
        };

        VkInstanceCreateInfo createInfo{VK_STRUCTURE_TYPE_INSTANCE_CREATE_INFO};
        createInfo.pApplicationInfo = &appInfo;
        createInfo.enabledExtensionCount = static_cast<uint32_t>(extensions.size());
        createInfo.ppEnabledExtensionNames = extensions.data();
        VkResult result = vkCreateInstance(&createInfo, nullptr, &instance_);
        if (result == VK_ERROR_INITIALIZATION_FAILED || result == VK_ERROR_INCOMPATIBLE_DRIVER) {
            ZK_LOGE(
                "Vulkan instance creation failed before device selection. This usually means the Android driver/emulator does not expose a usable Vulkan ICD."
            );
        }
        check(result, "vkCreateInstance");
    }

    void createSurface() {
        VkAndroidSurfaceCreateInfoKHR createInfo{VK_STRUCTURE_TYPE_ANDROID_SURFACE_CREATE_INFO_KHR};
        createInfo.window = window_;
        check(vkCreateAndroidSurfaceKHR(instance_, &createInfo, nullptr, &surface_), "vkCreateAndroidSurfaceKHR");
    }

    QueueFamily findQueueFamily(VkPhysicalDevice device) {
        uint32_t count = 0;
        vkGetPhysicalDeviceQueueFamilyProperties(device, &count, nullptr);
        std::vector<VkQueueFamilyProperties> families(count);
        vkGetPhysicalDeviceQueueFamilyProperties(device, &count, families.data());

        for (uint32_t i = 0; i < count; ++i) {
            VkBool32 presentSupported = VK_FALSE;
            vkGetPhysicalDeviceSurfaceSupportKHR(device, i, surface_, &presentSupported);
            if ((families[i].queueFlags & VK_QUEUE_GRAPHICS_BIT) && presentSupported) {
                return QueueFamily{i};
            }
        }
        return QueueFamily{};
    }

    void pickPhysicalDevice() {
        uint32_t count = 0;
        check(vkEnumeratePhysicalDevices(instance_, &count, nullptr), "vkEnumeratePhysicalDevices");
        if (count == 0) throw std::runtime_error("No Vulkan physical devices available");

        std::vector<VkPhysicalDevice> devices(count);
        check(vkEnumeratePhysicalDevices(instance_, &count, devices.data()), "vkEnumeratePhysicalDevices");
        for (VkPhysicalDevice device : devices) {
            QueueFamily family = findQueueFamily(device);
            if (family.valid()) {
                physicalDevice_ = device;
                queueFamily_ = family.index;
                return;
            }
        }
        throw std::runtime_error("No Vulkan device with graphics+present queue");
    }

    void createDevice() {
        float priority = 1.0f;
        VkDeviceQueueCreateInfo queueInfo{VK_STRUCTURE_TYPE_DEVICE_QUEUE_CREATE_INFO};
        queueInfo.queueFamilyIndex = queueFamily_;
        queueInfo.queueCount = 1;
        queueInfo.pQueuePriorities = &priority;

        const char* swapchainExtension = VK_KHR_SWAPCHAIN_EXTENSION_NAME;
        VkDeviceCreateInfo createInfo{VK_STRUCTURE_TYPE_DEVICE_CREATE_INFO};
        createInfo.queueCreateInfoCount = 1;
        createInfo.pQueueCreateInfos = &queueInfo;
        createInfo.enabledExtensionCount = 1;
        createInfo.ppEnabledExtensionNames = &swapchainExtension;
        check(vkCreateDevice(physicalDevice_, &createInfo, nullptr, &device_), "vkCreateDevice");
        vkGetDeviceQueue(device_, queueFamily_, 0, &graphicsQueue_);
        presentQueue_ = graphicsQueue_;
    }

    void createDescriptorPool() {
        VkDescriptorPoolSize poolSize{};
        poolSize.type = VK_DESCRIPTOR_TYPE_UNIFORM_BUFFER;
        poolSize.descriptorCount = 512;

        VkDescriptorPoolCreateInfo createInfo{VK_STRUCTURE_TYPE_DESCRIPTOR_POOL_CREATE_INFO};
        createInfo.flags = VK_DESCRIPTOR_POOL_CREATE_FREE_DESCRIPTOR_SET_BIT;
        createInfo.maxSets = 256;
        createInfo.poolSizeCount = 1;
        createInfo.pPoolSizes = &poolSize;
        check(vkCreateDescriptorPool(device_, &createInfo, nullptr, &descriptorPool_), "vkCreateDescriptorPool");
    }

    void createCommandPool() {
        VkCommandPoolCreateInfo createInfo{VK_STRUCTURE_TYPE_COMMAND_POOL_CREATE_INFO};
        createInfo.flags = VK_COMMAND_POOL_CREATE_RESET_COMMAND_BUFFER_BIT;
        createInfo.queueFamilyIndex = queueFamily_;
        check(vkCreateCommandPool(device_, &createInfo, nullptr, &commandPool_), "vkCreateCommandPool");
    }

    void createSyncObjects() {
        VkSemaphoreCreateInfo semaphoreInfo{VK_STRUCTURE_TYPE_SEMAPHORE_CREATE_INFO};
        VkFenceCreateInfo fenceInfo{VK_STRUCTURE_TYPE_FENCE_CREATE_INFO};
        fenceInfo.flags = VK_FENCE_CREATE_SIGNALED_BIT;
        check(vkCreateSemaphore(device_, &semaphoreInfo, nullptr, &imageAvailable_), "vkCreateSemaphore");
        check(vkCreateSemaphore(device_, &semaphoreInfo, nullptr, &renderFinished_), "vkCreateSemaphore");
        check(vkCreateFence(device_, &fenceInfo, nullptr, &inFlight_), "vkCreateFence");
    }

    uint32_t findMemoryType(uint32_t typeFilter, VkMemoryPropertyFlags properties) {
        VkPhysicalDeviceMemoryProperties memoryProperties{};
        vkGetPhysicalDeviceMemoryProperties(physicalDevice_, &memoryProperties);
        for (uint32_t i = 0; i < memoryProperties.memoryTypeCount; ++i) {
            if (
                (typeFilter & (1u << i)) &&
                (memoryProperties.memoryTypes[i].propertyFlags & properties) == properties
            ) {
                return i;
            }
        }
        throw std::runtime_error("No compatible Vulkan memory type");
    }

    void createBuffer(
        VkDeviceSize size,
        VkBufferUsageFlags usage,
        VkMemoryPropertyFlags properties,
        VkBuffer& buffer,
        VkDeviceMemory& memory
    ) {
        VkBufferCreateInfo bufferInfo{VK_STRUCTURE_TYPE_BUFFER_CREATE_INFO};
        bufferInfo.size = size;
        bufferInfo.usage = usage;
        bufferInfo.sharingMode = VK_SHARING_MODE_EXCLUSIVE;
        check(vkCreateBuffer(device_, &bufferInfo, nullptr, &buffer), "vkCreateBuffer");

        VkMemoryRequirements requirements{};
        vkGetBufferMemoryRequirements(device_, buffer, &requirements);

        VkMemoryAllocateInfo allocInfo{VK_STRUCTURE_TYPE_MEMORY_ALLOCATE_INFO};
        allocInfo.allocationSize = requirements.size;
        allocInfo.memoryTypeIndex = findMemoryType(requirements.memoryTypeBits, properties);
        check(vkAllocateMemory(device_, &allocInfo, nullptr, &memory), "vkAllocateMemory");
        check(vkBindBufferMemory(device_, buffer, memory, 0), "vkBindBufferMemory");
    }

    void uploadBuffer(VkDeviceMemory memory, const uint8_t* bytes, size_t size) {
        void* mapped = nullptr;
        check(vkMapMemory(device_, memory, 0, static_cast<VkDeviceSize>(size), 0, &mapped), "vkMapMemory");
        std::memcpy(mapped, bytes, size);
        vkUnmapMemory(device_, memory);
    }

    void destroyMesh(NativeMesh* mesh) {
        if (mesh == nullptr || device_ == VK_NULL_HANDLE) return;
        if (mesh->vertexBuffer != VK_NULL_HANDLE) {
            vkDestroyBuffer(device_, mesh->vertexBuffer, nullptr);
            mesh->vertexBuffer = VK_NULL_HANDLE;
        }
        if (mesh->vertexMemory != VK_NULL_HANDLE) {
            vkFreeMemory(device_, mesh->vertexMemory, nullptr);
            mesh->vertexMemory = VK_NULL_HANDLE;
        }
        if (mesh->indexBuffer != VK_NULL_HANDLE) {
            vkDestroyBuffer(device_, mesh->indexBuffer, nullptr);
            mesh->indexBuffer = VK_NULL_HANDLE;
        }
        if (mesh->indexMemory != VK_NULL_HANDLE) {
            vkFreeMemory(device_, mesh->indexMemory, nullptr);
            mesh->indexMemory = VK_NULL_HANDLE;
        }
    }

    void destroyNativeBuffer(NativeBuffer* buffer) {
        if (buffer == nullptr || device_ == VK_NULL_HANDLE) return;
        if (buffer->buffer != VK_NULL_HANDLE) {
            vkDestroyBuffer(device_, buffer->buffer, nullptr);
            buffer->buffer = VK_NULL_HANDLE;
        }
        if (buffer->memory != VK_NULL_HANDLE) {
            vkFreeMemory(device_, buffer->memory, nullptr);
            buffer->memory = VK_NULL_HANDLE;
        }
        buffer->size = 0;
    }

    void allocateDescriptorSet(
        NativeDescriptorSet* descriptorSet,
        const std::vector<uint32_t>& uniformBindings,
        const std::vector<NativeUniformBuffer*>& uniformBuffers
    ) {
        if (descriptorSet == nullptr || descriptorSet->pipeline == nullptr) return;
        VkDescriptorSetAllocateInfo allocInfo{VK_STRUCTURE_TYPE_DESCRIPTOR_SET_ALLOCATE_INFO};
        allocInfo.descriptorPool = descriptorPool_;
        allocInfo.descriptorSetCount = 1;
        allocInfo.pSetLayouts = &descriptorSet->pipeline->descriptorSetLayout;
        check(vkAllocateDescriptorSets(device_, &allocInfo, &descriptorSet->descriptorSet), "vkAllocateDescriptorSets");

        std::vector<VkDescriptorBufferInfo> bufferInfos;
        std::vector<VkWriteDescriptorSet> writes;
        bufferInfos.reserve(uniformBuffers.size());
        writes.reserve(uniformBuffers.size());

        for (size_t i = 0; i < uniformBuffers.size(); ++i) {
            NativeUniformBuffer* uniformBuffer = uniformBuffers[i];
            if (uniformBuffer == nullptr || uniformBuffer->buffer == VK_NULL_HANDLE) continue;

            VkDescriptorBufferInfo bufferInfo{};
            bufferInfo.buffer = uniformBuffer->buffer;
            bufferInfo.offset = 0;
            bufferInfo.range = uniformBuffer->size;
            bufferInfos.push_back(bufferInfo);

            VkWriteDescriptorSet write{VK_STRUCTURE_TYPE_WRITE_DESCRIPTOR_SET};
            write.dstSet = descriptorSet->descriptorSet;
            write.dstBinding = uniformBindings[i];
            write.dstArrayElement = 0;
            write.descriptorType = VK_DESCRIPTOR_TYPE_UNIFORM_BUFFER;
            write.descriptorCount = 1;
            write.pBufferInfo = &bufferInfos.back();
            writes.push_back(write);
        }

        if (!writes.empty()) {
            vkUpdateDescriptorSets(device_, static_cast<uint32_t>(writes.size()), writes.data(), 0, nullptr);
        }
    }

    void destroyNativeDescriptorSet(NativeDescriptorSet* descriptorSet) {
        if (descriptorSet == nullptr || device_ == VK_NULL_HANDLE) return;
        if (descriptorSet->descriptorSet != VK_NULL_HANDLE && descriptorPool_ != VK_NULL_HANDLE) {
            vkFreeDescriptorSets(device_, descriptorPool_, 1, &descriptorSet->descriptorSet);
            descriptorSet->descriptorSet = VK_NULL_HANDLE;
        }
        descriptorSet->pipeline = nullptr;
    }

    void destroyNativeUniformBuffer(NativeUniformBuffer* uniformBuffer) {
        if (uniformBuffer == nullptr || device_ == VK_NULL_HANDLE) return;
        if (uniformBuffer->descriptorSet != VK_NULL_HANDLE && descriptorPool_ != VK_NULL_HANDLE) {
            vkFreeDescriptorSets(device_, descriptorPool_, 1, &uniformBuffer->descriptorSet);
            uniformBuffer->descriptorSet = VK_NULL_HANDLE;
        }
        if (uniformBuffer->buffer != VK_NULL_HANDLE) {
            vkDestroyBuffer(device_, uniformBuffer->buffer, nullptr);
            uniformBuffer->buffer = VK_NULL_HANDLE;
        }
        if (uniformBuffer->memory != VK_NULL_HANDLE) {
            vkFreeMemory(device_, uniformBuffer->memory, nullptr);
            uniformBuffer->memory = VK_NULL_HANDLE;
        }
        uniformBuffer->size = 0;
    }

    VkShaderModule createShaderModule(const char* base64) {
        std::vector<uint8_t> bytes = decodeBase64(base64);
        if (bytes.empty() || bytes.size() % sizeof(uint32_t) != 0) {
            throw std::runtime_error("Invalid embedded SPIR-V shader");
        }
        std::vector<uint32_t> words(bytes.size() / sizeof(uint32_t));
        std::memcpy(words.data(), bytes.data(), bytes.size());
        VkShaderModuleCreateInfo createInfo{VK_STRUCTURE_TYPE_SHADER_MODULE_CREATE_INFO};
        createInfo.codeSize = bytes.size();
        createInfo.pCode = words.data();
        VkShaderModule module = VK_NULL_HANDLE;
        check(vkCreateShaderModule(device_, &createInfo, nullptr, &module), "vkCreateShaderModule");
        return module;
    }

    VkShaderModule createShaderModule(const uint32_t* words, size_t wordCount) {
        if (words == nullptr || wordCount == 0) {
            throw std::runtime_error("Invalid generated SPIR-V shader");
        }
        VkShaderModuleCreateInfo createInfo{VK_STRUCTURE_TYPE_SHADER_MODULE_CREATE_INFO};
        createInfo.codeSize = wordCount * sizeof(uint32_t);
        createInfo.pCode = words;
        VkShaderModule module = VK_NULL_HANDLE;
        check(vkCreateShaderModule(device_, &createInfo, nullptr, &module), "vkCreateShaderModule");
        return module;
    }

    std::vector<uint32_t> compileShader(
        const std::string& source,
        shaderc_shader_kind kind,
        const char* inputFileName
    ) {
        shaderc_compiler_t compiler = shaderc_compiler_initialize();
        shaderc_compile_options_t options = shaderc_compile_options_initialize();
        shaderc_compile_options_set_target_env(options, shaderc_target_env_vulkan, shaderc_env_version_vulkan_1_0);
        shaderc_compile_options_set_source_language(options, shaderc_source_language_glsl);

        shaderc_compilation_result_t result = shaderc_compile_into_spv(
            compiler,
            source.data(),
            source.size(),
            kind,
            inputFileName,
            "main",
            options
        );

        shaderc_compilation_status status = shaderc_result_get_compilation_status(result);
        if (status != shaderc_compilation_status_success) {
            std::string message = shaderc_result_get_error_message(result);
            shaderc_result_release(result);
            shaderc_compile_options_release(options);
            shaderc_compiler_release(compiler);
            ZK_LOGE("Vulkan shader compilation failed: %s", message.c_str());
            throw std::runtime_error(message);
        }

        size_t byteCount = shaderc_result_get_length(result);
        const char* bytes = shaderc_result_get_bytes(result);
        std::vector<uint32_t> spirv(byteCount / sizeof(uint32_t));
        std::memcpy(spirv.data(), bytes, byteCount);

        shaderc_result_release(result);
        shaderc_compile_options_release(options);
        shaderc_compiler_release(compiler);
        return spirv;
    }

    VkFormat vertexFormat(uint32_t format) {
        switch (format) {
            case 1: return VK_FORMAT_R32_SFLOAT;
            case 2: return VK_FORMAT_R32G32_SFLOAT;
            case 3: return VK_FORMAT_R32G32B32_SFLOAT;
            case 4: return VK_FORMAT_R32G32B32A32_SFLOAT;
            case 5: return VK_FORMAT_R32_UINT;
            case 6: return VK_FORMAT_R32G32_UINT;
            case 7: return VK_FORMAT_R32G32B32_UINT;
            case 8: return VK_FORMAT_R32G32B32A32_UINT;
            default: return VK_FORMAT_R32G32B32_SFLOAT;
        }
    }

    VkPrimitiveTopology primitiveTopology(uint32_t drawMode) {
        switch (drawMode) {
            case 0: return VK_PRIMITIVE_TOPOLOGY_POINT_LIST;
            case 1: return VK_PRIMITIVE_TOPOLOGY_LINE_LIST;
            case 2: return VK_PRIMITIVE_TOPOLOGY_TRIANGLE_LIST;
            case 3: return VK_PRIMITIVE_TOPOLOGY_LINE_STRIP;
            case 4: return VK_PRIMITIVE_TOPOLOGY_TRIANGLE_STRIP;
            default: return VK_PRIMITIVE_TOPOLOGY_TRIANGLE_LIST;
        }
    }

    void createPipelineDescriptorSetLayout(NativePipeline* nativePipeline) {
        if (nativePipeline == nullptr || nativePipeline->uniformBindings.empty()) return;
        if (nativePipeline->descriptorSetLayout != VK_NULL_HANDLE) return;

        std::vector<VkDescriptorSetLayoutBinding> bindings;
        bindings.reserve(nativePipeline->uniformBindings.size());
        for (uint32_t uniformBinding : nativePipeline->uniformBindings) {
            VkDescriptorSetLayoutBinding binding{};
            binding.binding = uniformBinding;
            binding.descriptorType = VK_DESCRIPTOR_TYPE_UNIFORM_BUFFER;
            binding.descriptorCount = 1;
            binding.stageFlags = VK_SHADER_STAGE_VERTEX_BIT | VK_SHADER_STAGE_FRAGMENT_BIT;
            bindings.push_back(binding);
        }

        VkDescriptorSetLayoutCreateInfo createInfo{VK_STRUCTURE_TYPE_DESCRIPTOR_SET_LAYOUT_CREATE_INFO};
        createInfo.bindingCount = static_cast<uint32_t>(bindings.size());
        createInfo.pBindings = bindings.data();
        check(
            vkCreateDescriptorSetLayout(device_, &createInfo, nullptr, &nativePipeline->descriptorSetLayout),
            "vkCreateDescriptorSetLayout"
        );
    }

    void createPipelineObjects(NativePipeline* nativePipeline) {
        if (nativePipeline == nullptr || renderPass_ == VK_NULL_HANDLE) return;

        VkShaderModule vertexShader = createShaderModule(
            nativePipeline->vertexShaderSpv.data(),
            nativePipeline->vertexShaderSpv.size()
        );
        VkShaderModule fragmentShader = createShaderModule(
            nativePipeline->fragmentShaderSpv.data(),
            nativePipeline->fragmentShaderSpv.size()
        );

        VkPipelineShaderStageCreateInfo vertexStage{VK_STRUCTURE_TYPE_PIPELINE_SHADER_STAGE_CREATE_INFO};
        vertexStage.stage = VK_SHADER_STAGE_VERTEX_BIT;
        vertexStage.module = vertexShader;
        vertexStage.pName = "main";

        VkPipelineShaderStageCreateInfo fragmentStage{VK_STRUCTURE_TYPE_PIPELINE_SHADER_STAGE_CREATE_INFO};
        fragmentStage.stage = VK_SHADER_STAGE_FRAGMENT_BIT;
        fragmentStage.module = fragmentShader;
        fragmentStage.pName = "main";

        std::array<VkPipelineShaderStageCreateInfo, 2> stages = { vertexStage, fragmentStage };

        std::vector<VkVertexInputBindingDescription> bindings;
        std::vector<VkVertexInputAttributeDescription> attributes;
        bindings.reserve(nativePipeline->vertexAttributes.size());
        attributes.reserve(nativePipeline->vertexAttributes.size());

        for (const VertexAttribute& vertexAttribute : nativePipeline->vertexAttributes) {
            VkVertexInputBindingDescription binding{};
            binding.binding = vertexAttribute.binding;
            binding.stride = vertexAttribute.stride;
            binding.inputRate = VK_VERTEX_INPUT_RATE_VERTEX;
            bindings.push_back(binding);

            VkVertexInputAttributeDescription attribute{};
            attribute.location = vertexAttribute.location;
            attribute.binding = vertexAttribute.binding;
            attribute.format = vertexFormat(vertexAttribute.format);
            attribute.offset = vertexAttribute.offset;
            attributes.push_back(attribute);
        }

        VkPipelineVertexInputStateCreateInfo vertexInput{VK_STRUCTURE_TYPE_PIPELINE_VERTEX_INPUT_STATE_CREATE_INFO};
        vertexInput.vertexBindingDescriptionCount = static_cast<uint32_t>(bindings.size());
        vertexInput.pVertexBindingDescriptions = bindings.data();
        vertexInput.vertexAttributeDescriptionCount = static_cast<uint32_t>(attributes.size());
        vertexInput.pVertexAttributeDescriptions = attributes.data();

        VkPipelineInputAssemblyStateCreateInfo inputAssembly{VK_STRUCTURE_TYPE_PIPELINE_INPUT_ASSEMBLY_STATE_CREATE_INFO};
        inputAssembly.topology = primitiveTopology(nativePipeline->drawMode);
        inputAssembly.primitiveRestartEnable = VK_FALSE;

        VkViewport viewport{};
        viewport.x = 0.0f;
        viewport.y = 0.0f;
        viewport.width = static_cast<float>(extent_.width);
        viewport.height = static_cast<float>(extent_.height);
        viewport.minDepth = 0.0f;
        viewport.maxDepth = 1.0f;

        VkRect2D scissor{};
        scissor.offset = {0, 0};
        scissor.extent = extent_;

        VkPipelineViewportStateCreateInfo viewportState{VK_STRUCTURE_TYPE_PIPELINE_VIEWPORT_STATE_CREATE_INFO};
        viewportState.viewportCount = 1;
        viewportState.pViewports = &viewport;
        viewportState.scissorCount = 1;
        viewportState.pScissors = &scissor;

        VkPipelineRasterizationStateCreateInfo rasterizer{VK_STRUCTURE_TYPE_PIPELINE_RASTERIZATION_STATE_CREATE_INFO};
        rasterizer.depthClampEnable = VK_FALSE;
        rasterizer.rasterizerDiscardEnable = VK_FALSE;
        rasterizer.polygonMode = VK_POLYGON_MODE_FILL;
        rasterizer.lineWidth = 1.0f;
        rasterizer.cullMode = VK_CULL_MODE_NONE;
        rasterizer.frontFace = VK_FRONT_FACE_COUNTER_CLOCKWISE;
        rasterizer.depthBiasEnable = VK_FALSE;

        VkPipelineMultisampleStateCreateInfo multisample{VK_STRUCTURE_TYPE_PIPELINE_MULTISAMPLE_STATE_CREATE_INFO};
        multisample.rasterizationSamples = VK_SAMPLE_COUNT_1_BIT;

        VkPipelineColorBlendAttachmentState colorBlendAttachment{};
        colorBlendAttachment.colorWriteMask =
            VK_COLOR_COMPONENT_R_BIT |
            VK_COLOR_COMPONENT_G_BIT |
            VK_COLOR_COMPONENT_B_BIT |
            VK_COLOR_COMPONENT_A_BIT;
        colorBlendAttachment.blendEnable = VK_FALSE;

        VkPipelineColorBlendStateCreateInfo colorBlend{VK_STRUCTURE_TYPE_PIPELINE_COLOR_BLEND_STATE_CREATE_INFO};
        colorBlend.attachmentCount = 1;
        colorBlend.pAttachments = &colorBlendAttachment;

        createPipelineDescriptorSetLayout(nativePipeline);

        VkPipelineLayoutCreateInfo layoutInfo{VK_STRUCTURE_TYPE_PIPELINE_LAYOUT_CREATE_INFO};
        if (nativePipeline->descriptorSetLayout != VK_NULL_HANDLE) {
            layoutInfo.setLayoutCount = 1;
            layoutInfo.pSetLayouts = &nativePipeline->descriptorSetLayout;
        }
        check(vkCreatePipelineLayout(device_, &layoutInfo, nullptr, &nativePipeline->layout), "vkCreatePipelineLayout");

        VkPipelineDepthStencilStateCreateInfo depthStencil{VK_STRUCTURE_TYPE_PIPELINE_DEPTH_STENCIL_STATE_CREATE_INFO};
        depthStencil.depthTestEnable = VK_TRUE;
        depthStencil.depthWriteEnable = VK_TRUE;
        depthStencil.depthCompareOp = VK_COMPARE_OP_LESS;
        depthStencil.depthBoundsTestEnable = VK_FALSE;
        depthStencil.stencilTestEnable = VK_FALSE;

        VkGraphicsPipelineCreateInfo pipelineInfo{VK_STRUCTURE_TYPE_GRAPHICS_PIPELINE_CREATE_INFO};
        pipelineInfo.stageCount = static_cast<uint32_t>(stages.size());
        pipelineInfo.pStages = stages.data();
        pipelineInfo.pVertexInputState = &vertexInput;
        pipelineInfo.pInputAssemblyState = &inputAssembly;
        pipelineInfo.pViewportState = &viewportState;
        pipelineInfo.pRasterizationState = &rasterizer;
        pipelineInfo.pMultisampleState = &multisample;
        pipelineInfo.pDepthStencilState = &depthStencil;
        pipelineInfo.pColorBlendState = &colorBlend;
        pipelineInfo.layout = nativePipeline->layout;
        pipelineInfo.renderPass = renderPass_;
        pipelineInfo.subpass = 0;
        check(vkCreateGraphicsPipelines(device_, VK_NULL_HANDLE, 1, &pipelineInfo, nullptr, &nativePipeline->pipeline), "vkCreateGraphicsPipelines");

        vkDestroyShaderModule(device_, fragmentShader, nullptr);
        vkDestroyShaderModule(device_, vertexShader, nullptr);
    }

    void destroyPipelineObjects(NativePipeline* pipeline) {
        if (pipeline == nullptr || device_ == VK_NULL_HANDLE) return;
        if (pipeline->pipeline != VK_NULL_HANDLE) {
            vkDestroyPipeline(device_, pipeline->pipeline, nullptr);
            pipeline->pipeline = VK_NULL_HANDLE;
        }
        if (pipeline->layout != VK_NULL_HANDLE) {
            vkDestroyPipelineLayout(device_, pipeline->layout, nullptr);
            pipeline->layout = VK_NULL_HANDLE;
        }
        if (pipeline->descriptorSetLayout != VK_NULL_HANDLE) {
            vkDestroyDescriptorSetLayout(device_, pipeline->descriptorSetLayout, nullptr);
            pipeline->descriptorSetLayout = VK_NULL_HANDLE;
        }
    }

    void recreateNativePipelines() {
        for (NativePipeline* pipeline : pipelines_) {
            createPipelineObjects(pipeline);
        }
    }

    VkSurfaceFormatKHR chooseSurfaceFormat(const std::vector<VkSurfaceFormatKHR>& formats) {
        for (const auto& format : formats) {
            if (format.format == VK_FORMAT_R8G8B8A8_UNORM || format.format == VK_FORMAT_B8G8R8A8_UNORM) {
                return format;
            }
        }
        return formats[0];
    }

    VkFormat findSupportedFormat(
        const std::vector<VkFormat>& candidates,
        VkImageTiling tiling,
        VkFormatFeatureFlags features
    ) {
        for (VkFormat format : candidates) {
            VkFormatProperties properties{};
            vkGetPhysicalDeviceFormatProperties(physicalDevice_, format, &properties);
            if (tiling == VK_IMAGE_TILING_LINEAR && (properties.linearTilingFeatures & features) == features) {
                return format;
            }
            if (tiling == VK_IMAGE_TILING_OPTIMAL && (properties.optimalTilingFeatures & features) == features) {
                return format;
            }
        }
        throw std::runtime_error("No supported Vulkan depth format");
    }

    VkFormat findDepthFormat() {
        return findSupportedFormat(
            {
                VK_FORMAT_D32_SFLOAT,
                VK_FORMAT_D16_UNORM,
            },
            VK_IMAGE_TILING_OPTIMAL,
            VK_FORMAT_FEATURE_DEPTH_STENCIL_ATTACHMENT_BIT
        );
    }

    void recreateSwapchain() {
        cleanupSwapchain();

        VkSurfaceCapabilitiesKHR caps{};
        check(vkGetPhysicalDeviceSurfaceCapabilitiesKHR(physicalDevice_, surface_, &caps), "vkGetPhysicalDeviceSurfaceCapabilitiesKHR");

        uint32_t formatCount = 0;
        check(vkGetPhysicalDeviceSurfaceFormatsKHR(physicalDevice_, surface_, &formatCount, nullptr), "vkGetPhysicalDeviceSurfaceFormatsKHR");
        std::vector<VkSurfaceFormatKHR> formats(formatCount);
        check(vkGetPhysicalDeviceSurfaceFormatsKHR(physicalDevice_, surface_, &formatCount, formats.data()), "vkGetPhysicalDeviceSurfaceFormatsKHR");
        surfaceFormat_ = chooseSurfaceFormat(formats);

        extent_.width = std::clamp(
            static_cast<uint32_t>(width_),
            caps.minImageExtent.width,
            caps.maxImageExtent.width
        );
        extent_.height = std::clamp(
            static_cast<uint32_t>(height_),
            caps.minImageExtent.height,
            caps.maxImageExtent.height
        );

        uint32_t imageCount = caps.minImageCount + 1;
        if (caps.maxImageCount > 0) {
            imageCount = std::min(imageCount, caps.maxImageCount);
        }

        VkSwapchainCreateInfoKHR createInfo{VK_STRUCTURE_TYPE_SWAPCHAIN_CREATE_INFO_KHR};
        createInfo.surface = surface_;
        createInfo.minImageCount = imageCount;
        createInfo.imageFormat = surfaceFormat_.format;
        createInfo.imageColorSpace = surfaceFormat_.colorSpace;
        createInfo.imageExtent = extent_;
        createInfo.imageArrayLayers = 1;
        createInfo.imageUsage = VK_IMAGE_USAGE_COLOR_ATTACHMENT_BIT;
        createInfo.imageSharingMode = VK_SHARING_MODE_EXCLUSIVE;
        createInfo.preTransform = caps.currentTransform;
        createInfo.compositeAlpha = VK_COMPOSITE_ALPHA_OPAQUE_BIT_KHR;
        createInfo.presentMode = VK_PRESENT_MODE_FIFO_KHR;
        createInfo.clipped = VK_TRUE;
        check(vkCreateSwapchainKHR(device_, &createInfo, nullptr, &swapchain_), "vkCreateSwapchainKHR");

        depthFormat_ = findDepthFormat();
        createRenderPass();
        recreateNativePipelines();
        createImageViews();
        createDepthResources();
        createFramebuffers();
        createCommandBuffers();
        ZK_LOGI("Vulkan swapchain ready %ux%u", extent_.width, extent_.height);
    }

    void createRenderPass() {
        VkAttachmentDescription colorAttachment{};
        colorAttachment.format = surfaceFormat_.format;
        colorAttachment.samples = VK_SAMPLE_COUNT_1_BIT;
        colorAttachment.loadOp = VK_ATTACHMENT_LOAD_OP_CLEAR;
        colorAttachment.storeOp = VK_ATTACHMENT_STORE_OP_STORE;
        colorAttachment.stencilLoadOp = VK_ATTACHMENT_LOAD_OP_DONT_CARE;
        colorAttachment.stencilStoreOp = VK_ATTACHMENT_STORE_OP_DONT_CARE;
        colorAttachment.initialLayout = VK_IMAGE_LAYOUT_UNDEFINED;
        colorAttachment.finalLayout = VK_IMAGE_LAYOUT_PRESENT_SRC_KHR;

        VkAttachmentDescription depthAttachment{};
        depthAttachment.format = depthFormat_;
        depthAttachment.samples = VK_SAMPLE_COUNT_1_BIT;
        depthAttachment.loadOp = VK_ATTACHMENT_LOAD_OP_CLEAR;
        depthAttachment.storeOp = VK_ATTACHMENT_STORE_OP_DONT_CARE;
        depthAttachment.stencilLoadOp = VK_ATTACHMENT_LOAD_OP_DONT_CARE;
        depthAttachment.stencilStoreOp = VK_ATTACHMENT_STORE_OP_DONT_CARE;
        depthAttachment.initialLayout = VK_IMAGE_LAYOUT_UNDEFINED;
        depthAttachment.finalLayout = VK_IMAGE_LAYOUT_DEPTH_STENCIL_ATTACHMENT_OPTIMAL;

        VkAttachmentReference colorRef{};
        colorRef.attachment = 0;
        colorRef.layout = VK_IMAGE_LAYOUT_COLOR_ATTACHMENT_OPTIMAL;

        VkAttachmentReference depthRef{};
        depthRef.attachment = 1;
        depthRef.layout = VK_IMAGE_LAYOUT_DEPTH_STENCIL_ATTACHMENT_OPTIMAL;

        VkSubpassDescription subpass{};
        subpass.pipelineBindPoint = VK_PIPELINE_BIND_POINT_GRAPHICS;
        subpass.colorAttachmentCount = 1;
        subpass.pColorAttachments = &colorRef;
        subpass.pDepthStencilAttachment = &depthRef;

        std::array<VkSubpassDependency, 2> dependencies{};
        dependencies[0].srcSubpass = VK_SUBPASS_EXTERNAL;
        dependencies[0].dstSubpass = 0;
        dependencies[0].srcStageMask = VK_PIPELINE_STAGE_COLOR_ATTACHMENT_OUTPUT_BIT |
            VK_PIPELINE_STAGE_EARLY_FRAGMENT_TESTS_BIT;
        dependencies[0].dstStageMask = VK_PIPELINE_STAGE_COLOR_ATTACHMENT_OUTPUT_BIT |
            VK_PIPELINE_STAGE_EARLY_FRAGMENT_TESTS_BIT;
        dependencies[0].srcAccessMask = 0;
        dependencies[0].dstAccessMask = VK_ACCESS_COLOR_ATTACHMENT_WRITE_BIT |
            VK_ACCESS_DEPTH_STENCIL_ATTACHMENT_WRITE_BIT;

        dependencies[1].srcSubpass = 0;
        dependencies[1].dstSubpass = VK_SUBPASS_EXTERNAL;
        dependencies[1].srcStageMask = VK_PIPELINE_STAGE_COLOR_ATTACHMENT_OUTPUT_BIT;
        dependencies[1].dstStageMask = VK_PIPELINE_STAGE_BOTTOM_OF_PIPE_BIT;
        dependencies[1].srcAccessMask = VK_ACCESS_COLOR_ATTACHMENT_WRITE_BIT;
        dependencies[1].dstAccessMask = 0;

        std::array<VkAttachmentDescription, 2> attachments = {colorAttachment, depthAttachment};
        VkRenderPassCreateInfo createInfo{VK_STRUCTURE_TYPE_RENDER_PASS_CREATE_INFO};
        createInfo.attachmentCount = static_cast<uint32_t>(attachments.size());
        createInfo.pAttachments = attachments.data();
        createInfo.subpassCount = 1;
        createInfo.pSubpasses = &subpass;
        createInfo.dependencyCount = static_cast<uint32_t>(dependencies.size());
        createInfo.pDependencies = dependencies.data();
        check(vkCreateRenderPass(device_, &createInfo, nullptr, &renderPass_), "vkCreateRenderPass");
    }

    void createImageViews() {
        uint32_t count = 0;
        check(vkGetSwapchainImagesKHR(device_, swapchain_, &count, nullptr), "vkGetSwapchainImagesKHR");
        images_.resize(count);
        check(vkGetSwapchainImagesKHR(device_, swapchain_, &count, images_.data()), "vkGetSwapchainImagesKHR");
        imageViews_.resize(count);

        for (size_t i = 0; i < images_.size(); ++i) {
            VkImageViewCreateInfo createInfo{VK_STRUCTURE_TYPE_IMAGE_VIEW_CREATE_INFO};
            createInfo.image = images_[i];
            createInfo.viewType = VK_IMAGE_VIEW_TYPE_2D;
            createInfo.format = surfaceFormat_.format;
            createInfo.components.r = VK_COMPONENT_SWIZZLE_IDENTITY;
            createInfo.components.g = VK_COMPONENT_SWIZZLE_IDENTITY;
            createInfo.components.b = VK_COMPONENT_SWIZZLE_IDENTITY;
            createInfo.components.a = VK_COMPONENT_SWIZZLE_IDENTITY;
            createInfo.subresourceRange.aspectMask = VK_IMAGE_ASPECT_COLOR_BIT;
            createInfo.subresourceRange.baseMipLevel = 0;
            createInfo.subresourceRange.levelCount = 1;
            createInfo.subresourceRange.baseArrayLayer = 0;
            createInfo.subresourceRange.layerCount = 1;
            check(vkCreateImageView(device_, &createInfo, nullptr, &imageViews_[i]), "vkCreateImageView");
        }
    }

    void createImage(
        uint32_t width,
        uint32_t height,
        VkFormat format,
        VkImageTiling tiling,
        VkImageUsageFlags usage,
        VkMemoryPropertyFlags properties,
        VkImage& image,
        VkDeviceMemory& memory
    ) {
        VkImageCreateInfo imageInfo{VK_STRUCTURE_TYPE_IMAGE_CREATE_INFO};
        imageInfo.imageType = VK_IMAGE_TYPE_2D;
        imageInfo.extent.width = width;
        imageInfo.extent.height = height;
        imageInfo.extent.depth = 1;
        imageInfo.mipLevels = 1;
        imageInfo.arrayLayers = 1;
        imageInfo.format = format;
        imageInfo.tiling = tiling;
        imageInfo.initialLayout = VK_IMAGE_LAYOUT_UNDEFINED;
        imageInfo.usage = usage;
        imageInfo.samples = VK_SAMPLE_COUNT_1_BIT;
        imageInfo.sharingMode = VK_SHARING_MODE_EXCLUSIVE;
        check(vkCreateImage(device_, &imageInfo, nullptr, &image), "vkCreateImage");

        VkMemoryRequirements requirements{};
        vkGetImageMemoryRequirements(device_, image, &requirements);

        VkMemoryAllocateInfo allocInfo{VK_STRUCTURE_TYPE_MEMORY_ALLOCATE_INFO};
        allocInfo.allocationSize = requirements.size;
        allocInfo.memoryTypeIndex = findMemoryType(requirements.memoryTypeBits, properties);
        check(vkAllocateMemory(device_, &allocInfo, nullptr, &memory), "vkAllocateMemory");
        check(vkBindImageMemory(device_, image, memory, 0), "vkBindImageMemory");
    }

    VkImageView createImageView(VkImage image, VkFormat format, VkImageAspectFlags aspectMask) {
        VkImageViewCreateInfo createInfo{VK_STRUCTURE_TYPE_IMAGE_VIEW_CREATE_INFO};
        createInfo.image = image;
        createInfo.viewType = VK_IMAGE_VIEW_TYPE_2D;
        createInfo.format = format;
        createInfo.components.r = VK_COMPONENT_SWIZZLE_IDENTITY;
        createInfo.components.g = VK_COMPONENT_SWIZZLE_IDENTITY;
        createInfo.components.b = VK_COMPONENT_SWIZZLE_IDENTITY;
        createInfo.components.a = VK_COMPONENT_SWIZZLE_IDENTITY;
        createInfo.subresourceRange.aspectMask = aspectMask;
        createInfo.subresourceRange.baseMipLevel = 0;
        createInfo.subresourceRange.levelCount = 1;
        createInfo.subresourceRange.baseArrayLayer = 0;
        createInfo.subresourceRange.layerCount = 1;
        VkImageView imageView = VK_NULL_HANDLE;
        check(vkCreateImageView(device_, &createInfo, nullptr, &imageView), "vkCreateImageView");
        return imageView;
    }

    void createDepthResources() {
        createImage(
            extent_.width,
            extent_.height,
            depthFormat_,
            VK_IMAGE_TILING_OPTIMAL,
            VK_IMAGE_USAGE_DEPTH_STENCIL_ATTACHMENT_BIT,
            VK_MEMORY_PROPERTY_DEVICE_LOCAL_BIT,
            depthImage_,
            depthMemory_
        );
        depthImageView_ = createImageView(depthImage_, depthFormat_, VK_IMAGE_ASPECT_DEPTH_BIT);
    }

    void createFramebuffers() {
        framebuffers_.resize(imageViews_.size());
        for (size_t i = 0; i < imageViews_.size(); ++i) {
            std::array<VkImageView, 2> attachments = {
                imageViews_[i],
                depthImageView_,
            };
            VkFramebufferCreateInfo createInfo{VK_STRUCTURE_TYPE_FRAMEBUFFER_CREATE_INFO};
            createInfo.renderPass = renderPass_;
            createInfo.attachmentCount = static_cast<uint32_t>(attachments.size());
            createInfo.pAttachments = attachments.data();
            createInfo.width = extent_.width;
            createInfo.height = extent_.height;
            createInfo.layers = 1;
            check(vkCreateFramebuffer(device_, &createInfo, nullptr, &framebuffers_[i]), "vkCreateFramebuffer");
        }
    }

    void createCommandBuffers() {
        commandBuffers_.resize(framebuffers_.size());
        VkCommandBufferAllocateInfo allocInfo{VK_STRUCTURE_TYPE_COMMAND_BUFFER_ALLOCATE_INFO};
        allocInfo.commandPool = commandPool_;
        allocInfo.level = VK_COMMAND_BUFFER_LEVEL_PRIMARY;
        allocInfo.commandBufferCount = static_cast<uint32_t>(commandBuffers_.size());
        check(vkAllocateCommandBuffers(device_, &allocInfo, commandBuffers_.data()), "vkAllocateCommandBuffers");
    }

    void recordCommandBuffer(VkCommandBuffer commandBuffer, uint32_t imageIndex) {
        check(vkResetCommandBuffer(commandBuffer, 0), "vkResetCommandBuffer");
        VkCommandBufferBeginInfo beginInfo{VK_STRUCTURE_TYPE_COMMAND_BUFFER_BEGIN_INFO};
        check(vkBeginCommandBuffer(commandBuffer, &beginInfo), "vkBeginCommandBuffer");

        std::array<VkClearValue, 2> clearValues{};
        clearValues[0].color = {{clearColor_[0], clearColor_[1], clearColor_[2], clearColor_[3]}};
        clearValues[1].depthStencil = {1.0f, 0};

        VkRenderPassBeginInfo renderPassInfo{VK_STRUCTURE_TYPE_RENDER_PASS_BEGIN_INFO};
        renderPassInfo.renderPass = renderPass_;
        renderPassInfo.framebuffer = framebuffers_[imageIndex];
        renderPassInfo.renderArea.offset = {0, 0};
        renderPassInfo.renderArea.extent = extent_;
        renderPassInfo.clearValueCount = static_cast<uint32_t>(clearValues.size());
        renderPassInfo.pClearValues = clearValues.data();

        vkCmdBeginRenderPass(commandBuffer, &renderPassInfo, VK_SUBPASS_CONTENTS_INLINE);
        NativePipeline* activePipeline = nullptr;
        NativeBuffer* activeIndexBuffer = nullptr;
        VkDescriptorSet activeDescriptorSet = VK_NULL_HANDLE;
        for (const RenderCommand& command : commands_) {
            switch (command.type) {
                case CommandType::SetPipeline:
                    if (command.pipeline != nullptr && command.pipeline->pipeline != VK_NULL_HANDLE) {
                        activePipeline = command.pipeline;
                        activeDescriptorSet = VK_NULL_HANDLE;
                        vkCmdBindPipeline(commandBuffer, VK_PIPELINE_BIND_POINT_GRAPHICS, activePipeline->pipeline);
                    }
                    break;
                case CommandType::BindDescriptorSet:
                    if (
                        activePipeline != nullptr &&
                        command.descriptorSet != nullptr &&
                        command.descriptorSet->descriptorSet != VK_NULL_HANDLE
                    ) {
                        vkCmdBindDescriptorSets(
                            commandBuffer,
                            VK_PIPELINE_BIND_POINT_GRAPHICS,
                            activePipeline->layout,
                            0,
                            1,
                            &command.descriptorSet->descriptorSet,
                            0,
                            nullptr
                        );
                        activeDescriptorSet = command.descriptorSet->descriptorSet;
                    }
                    break;
                case CommandType::BindUniformBuffer:
                    if (
                        activePipeline != nullptr &&
                        command.uniformBuffer != nullptr &&
                        command.uniformBuffer->descriptorSet != VK_NULL_HANDLE
                    ) {
                        vkCmdBindDescriptorSets(
                            commandBuffer,
                            VK_PIPELINE_BIND_POINT_GRAPHICS,
                            activePipeline->layout,
                            0,
                            1,
                            &command.uniformBuffer->descriptorSet,
                            0,
                            nullptr
                        );
                        activeDescriptorSet = command.uniformBuffer->descriptorSet;
                    }
                    break;
                case CommandType::BindVertexBuffer:
                    if (command.buffer != nullptr && command.buffer->buffer != VK_NULL_HANDLE) {
                        VkDeviceSize offsets[] = {0};
                        VkBuffer buffers[] = {command.buffer->buffer};
                        vkCmdBindVertexBuffers(commandBuffer, command.slot, 1, buffers, offsets);
                    }
                    break;
                case CommandType::BindIndexBuffer:
                    if (command.buffer != nullptr && command.buffer->buffer != VK_NULL_HANDLE) {
                        activeIndexBuffer = command.buffer;
                        vkCmdBindIndexBuffer(commandBuffer, activeIndexBuffer->buffer, 0, VK_INDEX_TYPE_UINT16);
                    }
                    break;
                case CommandType::DrawIndexed:
                    if (
                        activePipeline != nullptr &&
                        activeDescriptorSet != VK_NULL_HANDLE &&
                        activeIndexBuffer != nullptr &&
                        command.count > 0
                    ) {
                        vkCmdDrawIndexed(commandBuffer, command.count, 1, 0, 0, 0);
                    }
                    break;
                case CommandType::DrawMesh:
                    if (
                        activePipeline != nullptr &&
                        activeDescriptorSet != VK_NULL_HANDLE &&
                        command.mesh != nullptr &&
                        command.mesh->indexCount > 0
                    ) {
                        VkDeviceSize offsets[] = {0};
                        vkCmdBindVertexBuffers(commandBuffer, 0, 1, &command.mesh->vertexBuffer, offsets);
                        vkCmdBindIndexBuffer(commandBuffer, command.mesh->indexBuffer, 0, VK_INDEX_TYPE_UINT16);
                        vkCmdDrawIndexed(commandBuffer, command.mesh->indexCount, 1, 0, 0, 0);
                    }
                    break;
            }
        }
        vkCmdEndRenderPass(commandBuffer);
        check(vkEndCommandBuffer(commandBuffer), "vkEndCommandBuffer");
        commands_.clear();
    }

    void cleanupSwapchain() {
        if (device_ == VK_NULL_HANDLE) return;
        for (NativePipeline* pipeline : pipelines_) {
            destroyPipelineObjects(pipeline);
        }
        if (!commandBuffers_.empty()) {
            vkFreeCommandBuffers(
                device_,
                commandPool_,
                static_cast<uint32_t>(commandBuffers_.size()),
                commandBuffers_.data()
            );
            commandBuffers_.clear();
        }
        for (VkFramebuffer framebuffer : framebuffers_) {
            vkDestroyFramebuffer(device_, framebuffer, nullptr);
        }
        framebuffers_.clear();
        for (VkImageView imageView : imageViews_) {
            vkDestroyImageView(device_, imageView, nullptr);
        }
        imageViews_.clear();
        images_.clear();
        if (depthImageView_ != VK_NULL_HANDLE) {
            vkDestroyImageView(device_, depthImageView_, nullptr);
            depthImageView_ = VK_NULL_HANDLE;
        }
        if (depthImage_ != VK_NULL_HANDLE) {
            vkDestroyImage(device_, depthImage_, nullptr);
            depthImage_ = VK_NULL_HANDLE;
        }
        if (depthMemory_ != VK_NULL_HANDLE) {
            vkFreeMemory(device_, depthMemory_, nullptr);
            depthMemory_ = VK_NULL_HANDLE;
        }
        if (renderPass_ != VK_NULL_HANDLE) {
            vkDestroyRenderPass(device_, renderPass_, nullptr);
            renderPass_ = VK_NULL_HANDLE;
        }
        if (swapchain_ != VK_NULL_HANDLE) {
            vkDestroySwapchainKHR(device_, swapchain_, nullptr);
            swapchain_ = VK_NULL_HANDLE;
        }
    }

    std::mutex mutex_;
    ANativeWindow* window_ = nullptr;
    int width_ = 0;
    int height_ = 0;

    VkInstance instance_ = VK_NULL_HANDLE;
    VkSurfaceKHR surface_ = VK_NULL_HANDLE;
    VkPhysicalDevice physicalDevice_ = VK_NULL_HANDLE;
    VkDevice device_ = VK_NULL_HANDLE;
    uint32_t queueFamily_ = 0;
    VkQueue graphicsQueue_ = VK_NULL_HANDLE;
    VkQueue presentQueue_ = VK_NULL_HANDLE;
    VkDescriptorPool descriptorPool_ = VK_NULL_HANDLE;
    VkCommandPool commandPool_ = VK_NULL_HANDLE;
    VkSemaphore imageAvailable_ = VK_NULL_HANDLE;
    VkSemaphore renderFinished_ = VK_NULL_HANDLE;
    VkFence inFlight_ = VK_NULL_HANDLE;

    VkSwapchainKHR swapchain_ = VK_NULL_HANDLE;
    VkSurfaceFormatKHR surfaceFormat_{};
    VkFormat depthFormat_ = VK_FORMAT_UNDEFINED;
    VkExtent2D extent_{};
    VkRenderPass renderPass_ = VK_NULL_HANDLE;
    VkImage depthImage_ = VK_NULL_HANDLE;
    VkDeviceMemory depthMemory_ = VK_NULL_HANDLE;
    VkImageView depthImageView_ = VK_NULL_HANDLE;
    std::vector<VkImage> images_;
    std::vector<VkImageView> imageViews_;
    std::vector<VkFramebuffer> framebuffers_;
    std::vector<VkCommandBuffer> commandBuffers_;
    std::vector<NativeMesh*> meshes_;
    std::vector<NativeBuffer*> buffers_;
    std::vector<NativeUniformBuffer*> uniformBuffers_;
    std::vector<NativeDescriptorSet*> descriptorSets_;
    std::vector<NativePipeline*> pipelines_;
    std::vector<RenderCommand> commands_;
    std::array<float, 4> clearColor_ = {0.02f, 0.35f, 0.75f, 1.0f};
    std::string lastError_;
};

VulkanRenderer* fromHandle(jlong handle) {
    return reinterpret_cast<VulkanRenderer*>(static_cast<intptr_t>(handle));
}

NativeMesh* meshFromHandle(jlong handle) {
    return reinterpret_cast<NativeMesh*>(static_cast<intptr_t>(handle));
}

NativeBuffer* bufferFromHandle(jlong handle) {
    return reinterpret_cast<NativeBuffer*>(static_cast<intptr_t>(handle));
}

NativeUniformBuffer* uniformBufferFromHandle(jlong handle) {
    return reinterpret_cast<NativeUniformBuffer*>(static_cast<intptr_t>(handle));
}

NativePipeline* pipelineFromHandle(jlong handle) {
    return reinterpret_cast<NativePipeline*>(static_cast<intptr_t>(handle));
}

NativeDescriptorSet* descriptorSetFromHandle(jlong handle) {
    return reinterpret_cast<NativeDescriptorSet*>(static_cast<intptr_t>(handle));
}

} // namespace

extern "C" JNIEXPORT jlong JNICALL
Java_zernikalos_ui_VulkanNativeBridge_nativeCreate(JNIEnv* env, jobject, jobject surface, jint width, jint height) {
    ANativeWindow* window = ANativeWindow_fromSurface(env, surface);
    if (window == nullptr) {
        ZK_LOGE("ANativeWindow_fromSurface returned null");
        return 0;
    }
    try {
        auto* renderer = new VulkanRenderer(window, width, height);
        ANativeWindow_release(window);
        return static_cast<jlong>(reinterpret_cast<intptr_t>(renderer));
    } catch (const std::exception& ex) {
        ZK_LOGE("Failed to create Vulkan renderer: %s", ex.what());
        ANativeWindow_release(window);
        return 0;
    }
}

extern "C" JNIEXPORT void JNICALL
Java_zernikalos_ui_VulkanNativeBridge_nativeResize(JNIEnv* env, jobject, jlong handle, jobject surface, jint width, jint height) {
    VulkanRenderer* renderer = fromHandle(handle);
    if (renderer == nullptr) return;
    ANativeWindow* window = ANativeWindow_fromSurface(env, surface);
    if (window == nullptr) return;
    try {
        renderer->resize(window, width, height);
    } catch (const std::exception& ex) {
        ZK_LOGE("Failed to resize Vulkan renderer: %s", ex.what());
    }
    ANativeWindow_release(window);
}

extern "C" JNIEXPORT void JNICALL
Java_zernikalos_ui_VulkanNativeBridge_nativeRender(JNIEnv*, jobject, jlong handle) {
    VulkanRenderer* renderer = fromHandle(handle);
    if (renderer == nullptr) return;
    try {
        renderer->render();
    } catch (const std::exception& ex) {
        ZK_LOGE("Failed to render Vulkan frame: %s", ex.what());
    }
}

extern "C" JNIEXPORT void JNICALL
Java_zernikalos_ui_VulkanNativeBridge_nativeDispose(JNIEnv*, jobject, jlong handle) {
    VulkanRenderer* renderer = fromHandle(handle);
    delete renderer;
}

extern "C" JNIEXPORT jstring JNICALL
Java_zernikalos_ui_VulkanNativeBridge_nativeGetLastError(JNIEnv* env, jobject, jlong handle) {
    VulkanRenderer* renderer = fromHandle(handle);
    if (renderer == nullptr) return env->NewStringUTF("");
    return env->NewStringUTF(renderer->getLastError().c_str());
}

extern "C" JNIEXPORT jlong JNICALL
Java_zernikalos_ui_VulkanNativeBridge_nativeCreateBuffer(
    JNIEnv* env,
    jobject,
    jlong rendererHandle,
    jbyteArray bytes,
    jboolean isIndexBuffer
) {
    VulkanRenderer* renderer = fromHandle(rendererHandle);
    if (renderer == nullptr || bytes == nullptr) return 0;

    jsize byteSize = env->GetArrayLength(bytes);
    if (byteSize <= 0) return 0;

    std::vector<uint8_t> bufferBytes(static_cast<size_t>(byteSize));
    env->GetByteArrayRegion(bytes, 0, byteSize, reinterpret_cast<jbyte*>(bufferBytes.data()));

    try {
        NativeBuffer* buffer = renderer->createNativeBuffer(
            bufferBytes.data(),
            bufferBytes.size(),
            isIndexBuffer == JNI_TRUE
        );
        return static_cast<jlong>(reinterpret_cast<intptr_t>(buffer));
    } catch (const std::exception& ex) {
        ZK_LOGE("Failed to create Vulkan buffer: %s", ex.what());
        return 0;
    }
}

extern "C" JNIEXPORT void JNICALL
Java_zernikalos_ui_VulkanNativeBridge_nativeDisposeBuffer(JNIEnv*, jobject, jlong rendererHandle, jlong bufferHandle) {
    VulkanRenderer* renderer = fromHandle(rendererHandle);
    NativeBuffer* buffer = bufferFromHandle(bufferHandle);
    if (renderer == nullptr || buffer == nullptr) return;
    renderer->disposeNativeBuffer(buffer);
}

extern "C" JNIEXPORT jlong JNICALL
Java_zernikalos_ui_VulkanNativeBridge_nativeCreateUniformBuffer(
    JNIEnv*,
    jobject,
    jlong rendererHandle,
    jint byteSize
) {
    VulkanRenderer* renderer = fromHandle(rendererHandle);
    if (renderer == nullptr || byteSize <= 0) return 0;

    try {
        NativeUniformBuffer* uniformBuffer = renderer->createNativeUniformBuffer(static_cast<size_t>(byteSize));
        return static_cast<jlong>(reinterpret_cast<intptr_t>(uniformBuffer));
    } catch (const std::exception& ex) {
        ZK_LOGE("Failed to create Vulkan uniform buffer: %s", ex.what());
        return 0;
    }
}

extern "C" JNIEXPORT void JNICALL
Java_zernikalos_ui_VulkanNativeBridge_nativeUpdateUniformBuffer(
    JNIEnv* env,
    jobject,
    jlong rendererHandle,
    jlong uniformBufferHandle,
    jbyteArray bytes
) {
    VulkanRenderer* renderer = fromHandle(rendererHandle);
    NativeUniformBuffer* uniformBuffer = uniformBufferFromHandle(uniformBufferHandle);
    if (renderer == nullptr || uniformBuffer == nullptr || bytes == nullptr) return;

    jsize byteSize = env->GetArrayLength(bytes);
    if (byteSize <= 0) return;

    std::vector<uint8_t> bufferBytes(static_cast<size_t>(byteSize));
    env->GetByteArrayRegion(bytes, 0, byteSize, reinterpret_cast<jbyte*>(bufferBytes.data()));

    try {
        renderer->updateNativeUniformBuffer(uniformBuffer, bufferBytes.data(), bufferBytes.size());
    } catch (const std::exception& ex) {
        ZK_LOGE("Failed to update Vulkan uniform buffer: %s", ex.what());
    }
}

extern "C" JNIEXPORT void JNICALL
Java_zernikalos_ui_VulkanNativeBridge_nativeDisposeUniformBuffer(
    JNIEnv*,
    jobject,
    jlong rendererHandle,
    jlong uniformBufferHandle
) {
    VulkanRenderer* renderer = fromHandle(rendererHandle);
    NativeUniformBuffer* uniformBuffer = uniformBufferFromHandle(uniformBufferHandle);
    if (renderer == nullptr || uniformBuffer == nullptr) return;
    renderer->disposeNativeUniformBuffer(uniformBuffer);
}

extern "C" JNIEXPORT jlong JNICALL
Java_zernikalos_ui_VulkanNativeBridge_nativeCreatePipeline(
    JNIEnv* env,
    jobject,
    jlong rendererHandle,
    jstring vertexShaderSource,
    jstring fragmentShaderSource,
    jintArray vertexAttributes,
    jintArray uniformBindings,
    jint drawMode
) {
    VulkanRenderer* renderer = fromHandle(rendererHandle);
    if (renderer == nullptr || vertexShaderSource == nullptr || fragmentShaderSource == nullptr || vertexAttributes == nullptr) return 0;

    const char* vertexSourceChars = env->GetStringUTFChars(vertexShaderSource, nullptr);
    const char* fragmentSourceChars = env->GetStringUTFChars(fragmentShaderSource, nullptr);
    std::string vertexSource = vertexSourceChars != nullptr ? vertexSourceChars : "";
    std::string fragmentSource = fragmentSourceChars != nullptr ? fragmentSourceChars : "";
    if (vertexSourceChars != nullptr) env->ReleaseStringUTFChars(vertexShaderSource, vertexSourceChars);
    if (fragmentSourceChars != nullptr) env->ReleaseStringUTFChars(fragmentShaderSource, fragmentSourceChars);

    jsize vertexAttributeValueCount = env->GetArrayLength(vertexAttributes);
    if (vertexAttributeValueCount <= 0 || vertexAttributeValueCount % kVertexAttributeFieldCount != 0) return 0;

    std::vector<jint> vertexAttributeValues(static_cast<size_t>(vertexAttributeValueCount));
    env->GetIntArrayRegion(vertexAttributes, 0, vertexAttributeValueCount, vertexAttributeValues.data());
    std::vector<VertexAttribute> nativeVertexAttributes;
    nativeVertexAttributes.reserve(vertexAttributeValues.size() / kVertexAttributeFieldCount);
    for (size_t i = 0; i < vertexAttributeValues.size(); i += kVertexAttributeFieldCount) {
        VertexAttribute attribute{};
        attribute.location = static_cast<uint32_t>(std::max(vertexAttributeValues[i], 0));
        attribute.binding = static_cast<uint32_t>(std::max(vertexAttributeValues[i + 1], 0));
        attribute.format = static_cast<uint32_t>(std::max(vertexAttributeValues[i + 2], 0));
        attribute.offset = static_cast<uint32_t>(std::max(vertexAttributeValues[i + 3], 0));
        attribute.stride = static_cast<uint32_t>(std::max(vertexAttributeValues[i + 4], 0));
        if (attribute.stride == 0) return 0;
        nativeVertexAttributes.push_back(attribute);
    }

    std::vector<uint32_t> nativeUniformBindings;
    if (uniformBindings != nullptr) {
        jsize uniformBindingCount = env->GetArrayLength(uniformBindings);
        std::vector<jint> uniformBindingValues(static_cast<size_t>(uniformBindingCount));
        env->GetIntArrayRegion(uniformBindings, 0, uniformBindingCount, uniformBindingValues.data());
        nativeUniformBindings.reserve(uniformBindingValues.size());
        for (jint uniformBinding : uniformBindingValues) {
            if (uniformBinding >= 0) nativeUniformBindings.push_back(static_cast<uint32_t>(uniformBinding));
        }
    }

    try {
        NativePipeline* pipeline = renderer->createNativePipeline(
            vertexSource,
            fragmentSource,
            nativeVertexAttributes,
            nativeUniformBindings,
            static_cast<uint32_t>(drawMode)
        );
        return static_cast<jlong>(reinterpret_cast<intptr_t>(pipeline));
    } catch (const std::exception& ex) {
        ZK_LOGE("Failed to create Vulkan pipeline: %s", ex.what());
        renderer->setLastError(ex.what());
        return 0;
    }
}

extern "C" JNIEXPORT void JNICALL
Java_zernikalos_ui_VulkanNativeBridge_nativeDisposePipeline(JNIEnv*, jobject, jlong rendererHandle, jlong pipelineHandle) {
    VulkanRenderer* renderer = fromHandle(rendererHandle);
    NativePipeline* pipeline = pipelineFromHandle(pipelineHandle);
    if (renderer == nullptr || pipeline == nullptr) return;
    renderer->disposeNativePipeline(pipeline);
}

extern "C" JNIEXPORT jlong JNICALL
Java_zernikalos_ui_VulkanNativeBridge_nativeCreateDescriptorSet(
    JNIEnv* env,
    jobject,
    jlong rendererHandle,
    jlong pipelineHandle,
    jintArray uniformBindings,
    jlongArray uniformBufferHandles
) {
    VulkanRenderer* renderer = fromHandle(rendererHandle);
    NativePipeline* pipeline = pipelineFromHandle(pipelineHandle);
    if (renderer == nullptr || pipeline == nullptr || uniformBindings == nullptr || uniformBufferHandles == nullptr) return 0;

    jsize uniformBindingCount = env->GetArrayLength(uniformBindings);
    jsize uniformBufferCount = env->GetArrayLength(uniformBufferHandles);
    if (uniformBindingCount != uniformBufferCount) return 0;

    std::vector<jint> uniformBindingValues(static_cast<size_t>(uniformBindingCount));
    std::vector<jlong> uniformBufferHandleValues(static_cast<size_t>(uniformBufferCount));
    env->GetIntArrayRegion(uniformBindings, 0, uniformBindingCount, uniformBindingValues.data());
    env->GetLongArrayRegion(uniformBufferHandles, 0, uniformBufferCount, uniformBufferHandleValues.data());

    std::vector<uint32_t> nativeUniformBindings;
    std::vector<NativeUniformBuffer*> nativeUniformBuffers;
    nativeUniformBindings.reserve(static_cast<size_t>(uniformBindingCount));
    nativeUniformBuffers.reserve(static_cast<size_t>(uniformBufferCount));
    for (jsize i = 0; i < uniformBindingCount; ++i) {
        if (uniformBindingValues[i] < 0) continue;
        NativeUniformBuffer* uniformBuffer = uniformBufferFromHandle(uniformBufferHandleValues[i]);
        if (uniformBuffer == nullptr) continue;
        nativeUniformBindings.push_back(static_cast<uint32_t>(uniformBindingValues[i]));
        nativeUniformBuffers.push_back(uniformBuffer);
    }

    try {
        NativeDescriptorSet* descriptorSet = renderer->createNativeDescriptorSet(
            pipeline,
            nativeUniformBindings,
            nativeUniformBuffers
        );
        return static_cast<jlong>(reinterpret_cast<intptr_t>(descriptorSet));
    } catch (const std::exception& ex) {
        ZK_LOGE("Failed to create Vulkan descriptor set: %s", ex.what());
        renderer->setLastError(ex.what());
        return 0;
    }
}

extern "C" JNIEXPORT void JNICALL
Java_zernikalos_ui_VulkanNativeBridge_nativeDisposeDescriptorSet(
    JNIEnv*,
    jobject,
    jlong rendererHandle,
    jlong descriptorSetHandle
) {
    VulkanRenderer* renderer = fromHandle(rendererHandle);
    NativeDescriptorSet* descriptorSet = descriptorSetFromHandle(descriptorSetHandle);
    if (renderer == nullptr || descriptorSet == nullptr) return;
    renderer->disposeNativeDescriptorSet(descriptorSet);
}

extern "C" JNIEXPORT void JNICALL
Java_zernikalos_ui_VulkanNativeBridge_nativeSetClearColor(
    JNIEnv*,
    jobject,
    jlong rendererHandle,
    jfloat r,
    jfloat g,
    jfloat b,
    jfloat a
) {
    VulkanRenderer* renderer = fromHandle(rendererHandle);
    if (renderer == nullptr) return;
    renderer->setClearColor(r, g, b, a);
}

extern "C" JNIEXPORT void JNICALL
Java_zernikalos_ui_VulkanNativeBridge_nativeSetPipeline(JNIEnv*, jobject, jlong rendererHandle, jlong pipelineHandle) {
    VulkanRenderer* renderer = fromHandle(rendererHandle);
    NativePipeline* pipeline = pipelineFromHandle(pipelineHandle);
    if (renderer == nullptr || pipeline == nullptr) return;
    renderer->queueSetPipeline(pipeline);
}

extern "C" JNIEXPORT void JNICALL
Java_zernikalos_ui_VulkanNativeBridge_nativeBindDescriptorSet(
    JNIEnv*,
    jobject,
    jlong rendererHandle,
    jlong descriptorSetHandle
) {
    VulkanRenderer* renderer = fromHandle(rendererHandle);
    NativeDescriptorSet* descriptorSet = descriptorSetFromHandle(descriptorSetHandle);
    if (renderer == nullptr || descriptorSet == nullptr) return;
    renderer->queueBindDescriptorSet(descriptorSet);
}

extern "C" JNIEXPORT void JNICALL
Java_zernikalos_ui_VulkanNativeBridge_nativeBindUniformBuffer(
    JNIEnv*,
    jobject,
    jlong rendererHandle,
    jint binding,
    jlong uniformBufferHandle
) {
    VulkanRenderer* renderer = fromHandle(rendererHandle);
    NativeUniformBuffer* uniformBuffer = uniformBufferFromHandle(uniformBufferHandle);
    if (renderer == nullptr || uniformBuffer == nullptr || binding < 0) return;
    renderer->queueBindUniformBuffer(static_cast<uint32_t>(binding), uniformBuffer);
}

extern "C" JNIEXPORT void JNICALL
Java_zernikalos_ui_VulkanNativeBridge_nativeBindVertexBuffer(
    JNIEnv*,
    jobject,
    jlong rendererHandle,
    jint slot,
    jlong bufferHandle
) {
    VulkanRenderer* renderer = fromHandle(rendererHandle);
    NativeBuffer* buffer = bufferFromHandle(bufferHandle);
    if (renderer == nullptr || buffer == nullptr || slot < 0) return;
    renderer->queueBindVertexBuffer(static_cast<uint32_t>(slot), buffer);
}

extern "C" JNIEXPORT void JNICALL
Java_zernikalos_ui_VulkanNativeBridge_nativeBindIndexBuffer(JNIEnv*, jobject, jlong rendererHandle, jlong bufferHandle) {
    VulkanRenderer* renderer = fromHandle(rendererHandle);
    NativeBuffer* buffer = bufferFromHandle(bufferHandle);
    if (renderer == nullptr || buffer == nullptr) return;
    renderer->queueBindIndexBuffer(buffer);
}

extern "C" JNIEXPORT void JNICALL
Java_zernikalos_ui_VulkanNativeBridge_nativeDrawIndexed(JNIEnv*, jobject, jlong rendererHandle, jint indexCount) {
    VulkanRenderer* renderer = fromHandle(rendererHandle);
    if (renderer == nullptr || indexCount <= 0) return;
    renderer->queueDrawIndexed(static_cast<uint32_t>(indexCount));
}

extern "C" JNIEXPORT jlong JNICALL
Java_zernikalos_ui_VulkanNativeBridge_nativeCreateMesh(
    JNIEnv* env,
    jobject,
    jlong rendererHandle,
    jbyteArray positions,
    jint vertexCount,
    jbyteArray indices,
    jint indexCount
) {
    VulkanRenderer* renderer = fromHandle(rendererHandle);
    if (renderer == nullptr || positions == nullptr || indices == nullptr || vertexCount <= 0 || indexCount <= 0) {
        return 0;
    }

    jsize positionsSize = env->GetArrayLength(positions);
    jsize indicesSize = env->GetArrayLength(indices);
    std::vector<uint8_t> positionBytes(static_cast<size_t>(positionsSize));
    std::vector<uint8_t> indexBytes(static_cast<size_t>(indicesSize));
    env->GetByteArrayRegion(
        positions,
        0,
        positionsSize,
        reinterpret_cast<jbyte*>(positionBytes.data())
    );
    env->GetByteArrayRegion(
        indices,
        0,
        indicesSize,
        reinterpret_cast<jbyte*>(indexBytes.data())
    );

    try {
        NativeMesh* mesh = renderer->createMesh(
            positionBytes.data(),
            positionBytes.size(),
            static_cast<uint32_t>(vertexCount),
            indexBytes.data(),
            indexBytes.size(),
            static_cast<uint32_t>(indexCount)
        );
        return static_cast<jlong>(reinterpret_cast<intptr_t>(mesh));
    } catch (const std::exception& ex) {
        ZK_LOGE("Failed to create Vulkan mesh: %s", ex.what());
        return 0;
    }
}

extern "C" JNIEXPORT void JNICALL
Java_zernikalos_ui_VulkanNativeBridge_nativeDrawMesh(JNIEnv*, jobject, jlong rendererHandle, jlong meshHandle) {
    VulkanRenderer* renderer = fromHandle(rendererHandle);
    NativeMesh* mesh = meshFromHandle(meshHandle);
    if (renderer == nullptr || mesh == nullptr) return;
    renderer->queueMeshDraw(mesh);
}

extern "C" JNIEXPORT void JNICALL
Java_zernikalos_ui_VulkanNativeBridge_nativeDisposeMesh(JNIEnv*, jobject, jlong rendererHandle, jlong meshHandle) {
    VulkanRenderer* renderer = fromHandle(rendererHandle);
    NativeMesh* mesh = meshFromHandle(meshHandle);
    if (renderer == nullptr || mesh == nullptr) return;
    renderer->disposeMesh(mesh);
}
