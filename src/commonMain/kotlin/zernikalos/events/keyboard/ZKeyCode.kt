/*
 * Copyright (c) 2024. Aarón Negrín - Zernikalos Engine.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package zernikalos.events.keyboard

import kotlin.js.JsExport

/**
 * Enumeration of keyboard key codes, similar to Unity's KeyCode.
 *
 * This enum provides a type-safe way to reference keyboard keys.
 * Use this enum to check which key was pressed in keyboard events.
 *
 * @example
 * ```kotlin
 * if (event.keyCode == ZKeyCode.A) {
 *     // Handle 'A' key press
 * }
 * if (event.keyCode == ZKeyCode.UpArrow) {
 *     // Handle up arrow key press
 * }
 * ```
 */
@JsExport
enum class ZKeyCode {
    // Letters
    A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U, V, W, X, Y, Z,

    // Numbers
    Digit0, Digit1, Digit2, Digit3, Digit4, Digit5, Digit6, Digit7, Digit8, Digit9,

    // Numpad
    Numpad0, Numpad1, Numpad2, Numpad3, Numpad4, Numpad5, Numpad6, Numpad7, Numpad8, Numpad9,
    NumpadAdd, NumpadSubtract, NumpadMultiply, NumpadDivide, NumpadEnter, NumpadDecimal,

    // Arrow keys
    UpArrow, DownArrow, LeftArrow, RightArrow,

    // Function keys
    F1, F2, F3, F4, F5, F6, F7, F8, F9, F10, F11, F12,

    // Modifier keys
    LeftShift, RightShift, LeftControl, RightControl, LeftAlt, RightAlt,
    LeftMeta, RightMeta,

    // Special keys
    Space, Enter, Tab, Backspace, Delete, Insert, Home, End, PageUp, PageDown,
    Escape, CapsLock, NumLock, ScrollLock, Pause,

    // Punctuation and symbols
    Semicolon, Equal, Comma, Minus, Period, Slash, Backquote, LeftBracket, Backslash, RightBracket, Quote,

    // Other
    Unknown;

    /**
     * Gets the DOM key code string that corresponds to this ZKeyCode.
     * This is used for mapping from DOM events to ZKeyCode.
     */
    val domCode: String
        get() = when (this) {
            A -> "KeyA"
            B -> "KeyB"
            C -> "KeyC"
            D -> "KeyD"
            E -> "KeyE"
            F -> "KeyF"
            G -> "KeyG"
            H -> "KeyH"
            I -> "KeyI"
            J -> "KeyJ"
            K -> "KeyK"
            L -> "KeyL"
            M -> "KeyM"
            N -> "KeyN"
            O -> "KeyO"
            P -> "KeyP"
            Q -> "KeyQ"
            R -> "KeyR"
            S -> "KeyS"
            T -> "KeyT"
            U -> "KeyU"
            V -> "KeyV"
            W -> "KeyW"
            X -> "KeyX"
            Y -> "KeyY"
            Z -> "KeyZ"
            Digit0 -> "Digit0"
            Digit1 -> "Digit1"
            Digit2 -> "Digit2"
            Digit3 -> "Digit3"
            Digit4 -> "Digit4"
            Digit5 -> "Digit5"
            Digit6 -> "Digit6"
            Digit7 -> "Digit7"
            Digit8 -> "Digit8"
            Digit9 -> "Digit9"
            Numpad0 -> "Numpad0"
            Numpad1 -> "Numpad1"
            Numpad2 -> "Numpad2"
            Numpad3 -> "Numpad3"
            Numpad4 -> "Numpad4"
            Numpad5 -> "Numpad5"
            Numpad6 -> "Numpad6"
            Numpad7 -> "Numpad7"
            Numpad8 -> "Numpad8"
            Numpad9 -> "Numpad9"
            NumpadAdd -> "NumpadAdd"
            NumpadSubtract -> "NumpadSubtract"
            NumpadMultiply -> "NumpadMultiply"
            NumpadDivide -> "NumpadDivide"
            NumpadEnter -> "NumpadEnter"
            NumpadDecimal -> "NumpadDecimal"
            UpArrow -> "ArrowUp"
            DownArrow -> "ArrowDown"
            LeftArrow -> "ArrowLeft"
            RightArrow -> "ArrowRight"
            F1 -> "F1"
            F2 -> "F2"
            F3 -> "F3"
            F4 -> "F4"
            F5 -> "F5"
            F6 -> "F6"
            F7 -> "F7"
            F8 -> "F8"
            F9 -> "F9"
            F10 -> "F10"
            F11 -> "F11"
            F12 -> "F12"
            LeftShift -> "ShiftLeft"
            RightShift -> "ShiftRight"
            LeftControl -> "ControlLeft"
            RightControl -> "ControlRight"
            LeftAlt -> "AltLeft"
            RightAlt -> "AltRight"
            LeftMeta -> "MetaLeft"
            RightMeta -> "MetaRight"
            Space -> "Space"
            Enter -> "Enter"
            Tab -> "Tab"
            Backspace -> "Backspace"
            Delete -> "Delete"
            Insert -> "Insert"
            Home -> "Home"
            End -> "End"
            PageUp -> "PageUp"
            PageDown -> "PageDown"
            Escape -> "Escape"
            CapsLock -> "CapsLock"
            NumLock -> "NumLock"
            ScrollLock -> "ScrollLock"
            Pause -> "Pause"
            Semicolon -> "Semicolon"
            Equal -> "Equal"
            Comma -> "Comma"
            Minus -> "Minus"
            Period -> "Period"
            Slash -> "Slash"
            Backquote -> "Backquote"
            LeftBracket -> "BracketLeft"
            Backslash -> "Backslash"
            RightBracket -> "BracketRight"
            Quote -> "Quote"
            Unknown -> ""
        }

    companion object {
        /**
         * Converts a DOM key code string to a ZKeyCode.
         *
         * @param domCode The DOM key code string (e.g., "KeyA", "ArrowUp")
         * @return The corresponding ZKeyCode, or Unknown if not found
         */
        fun fromDomCode(domCode: String): ZKeyCode {
            return values().firstOrNull { it.domCode == domCode } ?: Unknown
        }
    }
}
