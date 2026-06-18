file(READ "${VERT_NUM_FILE}" VERT_WORDS)
file(READ "${FRAG_NUM_FILE}" FRAG_WORDS)

string(STRIP "${VERT_WORDS}" VERT_WORDS)
string(STRIP "${FRAG_WORDS}" FRAG_WORDS)

file(WRITE "${OUTPUT_HEADER}" "#pragma once\n\n")
file(APPEND "${OUTPUT_HEADER}" "#include <cstdint>\n\n")
file(APPEND "${OUTPUT_HEADER}" "static constexpr uint32_t kZernikalosVulkanVertexShaderSpv[] = {\n${VERT_WORDS}\n};\n\n")
file(APPEND "${OUTPUT_HEADER}" "static constexpr uint32_t kZernikalosVulkanFragmentShaderSpv[] = {\n${FRAG_WORDS}\n};\n\n")
file(APPEND "${OUTPUT_HEADER}" "static constexpr uint32_t kZernikalosVulkanVertexShaderSpvWordCount = sizeof(kZernikalosVulkanVertexShaderSpv) / sizeof(kZernikalosVulkanVertexShaderSpv[0]);\n")
file(APPEND "${OUTPUT_HEADER}" "static constexpr uint32_t kZernikalosVulkanFragmentShaderSpvWordCount = sizeof(kZernikalosVulkanFragmentShaderSpv) / sizeof(kZernikalosVulkanFragmentShaderSpv[0]);\n")
