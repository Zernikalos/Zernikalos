/*
 * Copyright (c) 2026. Aarón Negrín - Zernikalos Engine.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package zernikalos.context

import zernikalos.math.ZMatrix4

/**
 * Returns the clip-space correction matrix for the active graphics backend.
 *
 * Canonical projection ([zernikalos.math.ZMatrix4.perspective]) is OpenGL-style.
 * Each backend `actual` defines its own matrix (they are not required to match):
 * - OpenGL / WebGPU: identity for this engine's coordinate convention
 * - Metal: Z remap only ([-1, 1] → [0, 1])
 */
internal expect fun backendCorrectionMatrix(): ZMatrix4
