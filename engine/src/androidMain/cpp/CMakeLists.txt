cmake_minimum_required(VERSION 3.22.1)

project(zernikalos_vulkan)

if (NOT ZK_SHADERC_LIB)
    message(FATAL_ERROR "ZK_SHADERC_LIB is required")
endif()

if (NOT ZK_SHADERC_INCLUDE)
    message(FATAL_ERROR "ZK_SHADERC_INCLUDE is required")
endif()

add_library(shaderc_static STATIC IMPORTED)
set_target_properties(
    shaderc_static
    PROPERTIES
        IMPORTED_LOCATION ${ZK_SHADERC_LIB}
        INTERFACE_INCLUDE_DIRECTORIES ${ZK_SHADERC_INCLUDE}
)

add_library(zernikalos_vulkan SHARED VulkanRenderer.cpp)

find_library(android-lib android)
find_library(log-lib log)
find_library(vulkan-lib vulkan)

target_compile_features(zernikalos_vulkan PRIVATE cxx_std_17)

target_link_libraries(
    zernikalos_vulkan
    shaderc_static
    ${android-lib}
    ${log-lib}
    ${vulkan-lib}
    atomic
)
