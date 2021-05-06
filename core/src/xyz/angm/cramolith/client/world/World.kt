/*
 * Developed as part of the Cramolith project.
 * This file was last modified at 5/6/21, 7:36 PM.
 * Copyright 2021, see git repository at git.angm.xyz for authors and other info.
 * This file is under the GPL3 license. See LICENSE in the root directory of this repository for details.
 */

package xyz.angm.cramolith.client.world

import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.math.Vector3
import com.badlogic.gdx.scenes.scene2d.Group
import com.kotcrab.vis.ui.widget.VisImage
import ktx.actors.plusAssign
import xyz.angm.cramolith.client.graphics.screens.GameScreen
import xyz.angm.cramolith.common.HUMAN_SIZE
import xyz.angm.cramolith.common.ecs.position
import xyz.angm.cramolith.common.ecs.renderable
import xyz.angm.cramolith.common.world.WorldMap
import xyz.angm.rox.Family.Companion.allOf


const val PLAYER_CENTER = HUMAN_SIZE / 2f
const val DEFAULT_SCALE = 5f
const val SCALE_SPEED = 0.1f

private val tmp = Vector2()
private val tmpP = Vector3()

/** In-game world, containing all in-world actors and the map. */
class World(private val screen: GameScreen) : Group() {

    private val renderables = allOf(renderable)
    var map = WorldMap.of(screen.player[position].map)
        set(value) {
            field = value
            mapChanged()
        }

    private var goalScale = DEFAULT_SCALE

    init {
        scaleBy(DEFAULT_SCALE)
        mapChanged()
    }

    override fun act(delta: Float) {
        super.act(delta)
        if (scaleX != goalScale) {
            scaleBy((goalScale - scaleX) * SCALE_SPEED)
        }
    }

    override fun draw(batch: Batch, parentAlpha: Float) {
        batch.end()

        val prev = tmpP.set(stage.viewport.camera.position)
        val camera = stage.viewport.camera
        camera.position.set(tmp.set(screen.player[position]).add(PLAYER_CENTER, PLAYER_CENTER).scl(scaleX), 0f)
        camera.update()
        batch.projectionMatrix = camera.combined

        batch.begin()
        super.draw(batch, parentAlpha)
        batch.end()

        camera.position.set(prev)
        camera.update()
        batch.projectionMatrix = camera.combined
        batch.begin()
    }

    private fun mapChanged() {
        clearChildren()
        this += VisImage(map.texture)
        for (actor in screen.engine[renderables]) {
            this += actor[renderable].actor
        }
        for (actor in map.actorsId.values()) {
            val img = VisImage(actor.drawable)
            img.x = actor.x.toFloat()
            img.y = actor.y.toFloat()
            img.setSize(HUMAN_SIZE, HUMAN_SIZE)
            this += img
        }
    }

    fun zoom(amount: Float) {
        goalScale = (goalScale + amount).coerceIn(0.2f, 20f)
    }
}