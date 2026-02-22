package zernikalos.components.shader

class ZUniformCollection(): MutableMap<String, ZBaseUniform> {

    private val uniforms: LinkedHashMap<String, ZUniform> = LinkedHashMap()

    private val uniformDatas: LinkedHashMap<String, ZUniformData> = LinkedHashMap()

    /** Block-level uniforms only (excludes flattened member entries). Use for filling via [ZUniform.computeValue]. */
    val blocks: Map<String, ZUniform>
        get() = uniforms

    val all: Iterator<ZUniform>
        get() = uniforms.values.iterator()

    val datas: Iterator<ZUniformData>
        get() = uniformDatas.values.iterator()

    override val keys: MutableSet<String>
        get() = (uniforms.keys + uniformDatas.keys) as MutableSet<String>

    override val values: MutableCollection<ZBaseUniform>
        get() = (uniforms.values + uniformDatas.values).toMutableList()

    override val entries: MutableSet<MutableMap.MutableEntry<String, ZBaseUniform>>
        get() = (uniforms.entries + uniformDatas.entries) as MutableSet<MutableMap.MutableEntry<String, ZBaseUniform>>

    val dataEntries: Iterator<MutableMap.MutableEntry<String, ZUniformData>>
        get() = uniformDatas.entries.iterator()

    override val size: Int
        get() = uniforms.size

    override operator fun get(key: String): ZBaseUniform? {
        return uniforms[key] ?: uniformDatas[key]
    }

    override fun put(
        key: String,
        value: ZBaseUniform
    ): ZBaseUniform? {
        if (value is ZUniform) {
            uniforms[key] = value
            value.uniforms.forEach { (name, uniform) ->
                uniformDatas[name] = uniform
            }
        }
        return value
    }

    override fun remove(key: String): ZBaseUniform? {
        return uniforms.remove(key)
    }

    override fun putAll(from: Map<out String, ZBaseUniform>) {
        from.forEach { (key, value) -> put(key, value) }
    }

    override fun clear() {
        uniforms.clear()
        uniformDatas.clear()
    }

    override fun isEmpty(): Boolean {
        return uniforms.isEmpty()
    }

    override fun containsKey(key: String): Boolean {
        return uniforms.containsKey(key) || uniformDatas.containsKey(key)
    }

    override fun containsValue(value: ZBaseUniform): Boolean {
        return uniforms.containsValue(value) || uniformDatas.containsValue(value)
    }

}
