/*
 * Developed as part of the Cramolith project.
 * This file was last modified at 2/10/21, 5:35 PM.
 * Copyright 2021, see git repository at git.angm.xyz for authors and other info.
 * This file is under the GPL3 license. See LICENSE in the root directory of this repository for details.
 */

package xyz.angm.cramolith.editor.modes

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.g2d.Batch
import xyz.angm.cramolith.common.world.WorldActor
import xyz.angm.cramolith.editor.Map
import xyz.angm.cramolith.editor.tmp
import kotlin.math.roundToInt

class PlaceActorMode(private val actor: WorldActor) : EditingMode() {
    override fun handleClick(map: Map, x: Float, y: Float) {
        actor.x = x.roundToInt()
        actor.y = y.roundToInt()
        map.mode = null
    }

    override fun drawBatch(batch: Batch, map: Map) = map.run {
        tmp.set(Gdx.input.x.toFloat(), (stage.height - Gdx.input.y))
        screenToLocalCoordinates(tmp)
        tmp.x = tmp.x.roundToInt().toFloat()
        tmp.y = tmp.y.roundToInt().toFloat()
        localToScreenCoordinates(tmp)
        batch.draw(actor.drawable, tmp.x, tmp.y, scaleX * actor.drawable.width, scaleY * actor.drawable.height)
    }

    override fun cancel(map: Map) {
        map.mode = null
    }
}