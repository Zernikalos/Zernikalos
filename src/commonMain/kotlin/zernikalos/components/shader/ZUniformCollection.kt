package zernikalos.components.shader

class ZUniformCollection(): MutableMap<String, ZBaseUniform> {

    private val uniformSingles: LinkedHashMap<String, ZUniform> = LinkedHashMap()

    private val uniformBlocks: LinkedHashMap<String, ZUniformBlock> = LinkedHashMap()

    val singles: Iterator<ZUniform>
        get() = uniformSingles.values.iterator()

    val blocks: Iterator<ZUniformBlock>
        get() = uniformBlocks.values.iterator()

    override val keys: MutableSet<String>
        get() = (uniformSingles.keys + uniformBlocks.keys) as MutableSet<String>

    override val values: MutableCollection<ZBaseUniform>
        get() = (uniformSingles.values + uniformBlocks.values).toMutableList()

    override val entries: MutableSet<MutableMap.MutableEntry<String, ZBaseUniform>>
        get() = (uniformSingles.entries + uniformBlocks.entries) as MutableSet<MutableMap.MutableEntry<String, ZBaseUniform>>

    override val size: Int
        get() = uniformSingles.size + uniformBlocks.size

    override operator fun get(key: String): ZBaseUniform? {
        return uniformSingles[key] ?: uniformBlocks[key]
    }

    override fun put(
        key: String,
        value: ZBaseUniform
    ): ZBaseUniform? {
        if (value is ZUniform) {
            uniformSingles[key] = value
        } else if (value is ZUniformBlock) {
            uniformBlocks[key] = value
        }
        return value
    }

    override fun remove(key: String): ZBaseUniform? {
        return uniformSingles.remove(key) ?: uniformBlocks.remove(key)
    }

    override fun putAll(from: Map<out String, ZBaseUniform>) {
        from.forEach { (key, value) -> put(key, value) }
    }

    override fun clear() {
        uniformSingles.clear()
        uniformBlocks.clear()
    }

    override fun isEmpty(): Boolean {
        return uniformSingles.isEmpty() && uniformBlocks.isEmpty()
    }

    override fun containsKey(key: String): Boolean {
        return uniformSingles.containsKey(key) || uniformBlocks.containsKey(key)
    }

    override fun containsValue(value: ZBaseUniform): Boolean {
        return uniformSingles.containsValue(value) || uniformBlocks.containsValue(value)
    }

}