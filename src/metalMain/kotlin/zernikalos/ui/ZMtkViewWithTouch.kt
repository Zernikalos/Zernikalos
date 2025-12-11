/*
 * Copyright (c) 2025. Aarón Negrín - Zernikalos Engine.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package zernikalos.ui

import kotlinx.cinterop.ExperimentalForeignApi
import platform.Foundation.NSSet
import platform.MetalKit.MTKView
import platform.UIKit.UIEvent

/**
 * Custom MTKView subclass that handles touch events.
 * This class overrides UIResponder methods to capture touch events.
 */
@OptIn(ExperimentalForeignApi::class)
class ZMtkViewWithTouch : MTKView {
    var touchHandler: MetalTouchHandler? = null

    @Suppress("CONSTRUCTOR_NOT_DELEGATED_TO_SUPER")
    constructor(frame: platform.CoreGraphics.CGRect, device: platform.Metal.MTLDeviceProtocol) : super(frame, device) {
        setupTouchHandling()
    }

    private fun setupTouchHandling() {
        userInteractionEnabled = true
        multipleTouchEnabled = true
    }

    override fun touchesBegan(touches: NSSet, withEvent: UIEvent?) {
        super.touchesBegan(touches, withEvent)
        touchHandler?.handleTouchesBegan(touches, withEvent)
    }

    override fun touchesMoved(touches: NSSet, withEvent: UIEvent?) {
        super.touchesMoved(touches, withEvent)
        touchHandler?.handleTouchesMoved(touches, withEvent)
    }

    override fun touchesEnded(touches: NSSet, withEvent: UIEvent?) {
        super.touchesEnded(touches, withEvent)
        touchHandler?.handleTouchesEnded(touches, withEvent)
    }

    override fun touchesCancelled(touches: NSSet, withEvent: UIEvent?) {
        super.touchesCancelled(touches, withEvent)
        touchHandler?.handleTouchesCancelled(touches, withEvent)
    }
}



