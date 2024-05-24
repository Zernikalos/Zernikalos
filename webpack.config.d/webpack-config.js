/*
 * Copyright (c) 2024. Aarón Negrín - Zernikalos Engine.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

config.output.library = {
    name: "zernikalos",
    type: "umd",
    export: "zernikalos"
};

// config.devtool = 'source-map';

// Related to this issue https://github.com/Kotlin/kotlinx.coroutines/issues/3874
// Need to modify the naming on the window
config.module = {
    rules: [
        {
            test: /\.js$/,
            loader: 'string-replace-loader',
            options: {
                search: 'coroutineDispatcher',
                replace: 'zkCoroutineDispatcher',
                flags: 'g'
            }
        }
    ]
}
