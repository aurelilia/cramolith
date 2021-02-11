/*
 * Developed as part of the Cramolith project.
 * This file was last modified at 2/11/21, 6:20 PM.
 * Copyright 2021, see git repository at git.angm.xyz for authors and other info.
 * This file is under the GPL3 license. See LICENSE in the root directory of this repository for details.
 */

package xyz.angm.cramolith.client.graphics.screens

import com.badlogic.gdx.ScreenAdapter
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.utils.viewport.ScreenViewport
import ktx.assets.disposeSafely

/** A basic interface for a Screen. */
abstract class Screen : ScreenAdapter() {

    val stage = Stage(ScreenViewport())

    /** hide is called when the screen is no longer active, at which point a screen becomes dereferenced and needs to be disposed. */
    override fun hide() = dispose()

    override fun resize(width: Int, height: Int) = stage.viewport.update(width, height, true)

    override fun dispose() = stage.disposeSafely()
}
