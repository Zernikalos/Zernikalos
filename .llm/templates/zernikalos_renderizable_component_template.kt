/*
 * Copyright (c) {{year}}, {{author}}
 * All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package zernikalos.components.{{component_type}}

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import kotlinx.serialization.protobuf.ProtoNumber
import zernikalos.ZDataType
import zernikalos.ZTypes
import zernikalos.components.*
import zernikalos.context.ZRenderingContext
import kotlin.js.JsExport
import kotlin.js.JsName

/**
 * @suppress
 */
@Serializable(with = Z{{ComponentName}}Serializer::class)
@JsExport
class Z{{ComponentName}} internal constructor(data: Z{{ComponentName}}Data):
    ZRenderizableComponent<Z{{ComponentName}}Data, Z{{ComponentName}}Renderer>(data),
    ZBindeable,
    ZRenderizable {

    @JsName("init")
    constructor(): this(Z{{ComponentName}}Data())

    override fun createRenderer(ctx: ZRenderingContext): ZBaseComponentRender {
        return Z{{ComponentName}}Renderer(ctx, data)
    }

    // TODO: Add specific component properties and methods here
}

/**
 * @suppress
 */
@Serializable
@JsExport
data class Z{{ComponentName}}Data internal constructor(
    // TODO: Add specific data properties here
): ZComponentData()

/**
 * @suppress
 */
@JsExport
expect class Z{{ComponentName}}Renderer internal constructor(
    ctx: ZRenderingContext,
    data: Z{{ComponentName}}Data
): ZBaseComponentRender {
    override fun initialize() {
        // TODO: Implement initialization
    }

    override fun render() {
        // TODO: Implement rendering
    }
}

/**
 * @suppress
 */
internal class Z{{ComponentName}}Serializer: ZComponentSerializer<Z{{ComponentName}}, ZRaw{{ComponentName}}Data>() {
    override val kSerializer: KSerializer<ZRaw{{ComponentName}}Data>
        get() = ZRaw{{ComponentName}}Data.serializer()

    override fun createComponentInstance(data: ZRaw{{ComponentName}}Data): Z{{ComponentName}} {
        val componentData = Z{{ComponentName}}Data()
        return Z{{ComponentName}}(componentData)
    }
}
