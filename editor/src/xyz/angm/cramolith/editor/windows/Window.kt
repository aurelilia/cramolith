/*
 * Developed as part of the Cramolith project.
 * This file was last modified at 2/10/21, 4:42 PM.
 * Copyright 2021, see git repository at git.angm.xyz for authors and other info.
 * This file is under the GPL3 license. See LICENSE in the root directory of this repository for details.
 */

package xyz.angm.cramolith.editor.windows

import com.kotcrab.vis.ui.widget.VisWindow
import xyz.angm.cramolith.editor.EditorScreen

abstract class Window(name: String) : VisWindow(name) {

    init {
        @Suppress("LeakingThis")
        setKeepWithinStage(false)
        isMovable = false
    }

    open fun mapChanged(screen: EditorScreen) {}
}