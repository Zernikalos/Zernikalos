/*
 * Copyright (c) 2024. Aarón Negrín - Zernikalos Engine.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package zernikalos.stats

import platform.UIKit.UIDevice

actual fun getZPlatformInfo(): ZPlatformInfo {
    return ZPlatformInfo(version = UIDevice().systemVersion, name = ZPlatformName.IOS)
}
