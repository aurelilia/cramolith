/*
 * Developed as part of the Cramolith project.
 * This file was last modified at 2/10/21, 5:10 PM.
 * Copyright 2021, see git repository at git.angm.xyz for authors and other info.
 * This file is under the GPL3 license. See LICENSE in the root directory of this repository for details.
 */

package xyz.angm.cramolith.editor.modes

import com.badlogic.gdx.graphics.g2d.Batch
import xyz.angm.cramolith.editor.Map

abstract class EditingMode {
    abstract fun handleClick(map: Map, x: Float, y: Float)
    open fun drawShape(map: Map) {}
    open fun drawBatch(batch: Batch, map: Map) {}
    abstract fun cancel(map: Map)
}
