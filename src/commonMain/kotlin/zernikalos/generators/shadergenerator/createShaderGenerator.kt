/*
 * Copyright (c) 2024. Aarón Negrín - Zernikalos Engine.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package zernikalos.generators.shadergenerator

/**
 * Creates a shader generator based on the specified type.
 *
 * @param type The type of shader generator to create.
 * @return A shader generator corresponding to the specified type.
 */
internal expect fun createShaderGenerator(type: ZShaderGeneratorType): ZShaderGenerator