/*
 * Developed as part of the Cramolith project.
 * This file was last modified at 2/10/21, 2:01 AM.
 * Copyright 2021, see git repository at git.angm.xyz for authors and other info.
 * This file is under the GPL3 license. See LICENSE in the root directory of this repository for details.
 */

package xyz.angm.cramolith.editor

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.math.Vector2
import com.kotcrab.vis.ui.widget.VisImage
import ktx.actors.onClickEvent
import xyz.angm.cramolith.common.world.WorldMap
import kotlin.math.abs
import kotlin.math.min
import kotlin.math.roundToInt

class Map(val map: WorldMap) : VisImage(map.texture) {

    private val tmp = Vector2()
    private var scrolled = false

    var mode: EditingMode? = null
    private val shape = ShapeRenderer()

    init {
        scaleBy(5f)
        onClickEvent { event, x, y ->
            if (scrolled) {
                scrolled = false
                return@onClickEvent
            }

            when (val mode = mode) {
                is FirstTriggerMode -> this.mode = SecondTriggerMode(Vector2(x.roundToInt().toFloat(), y.roundToInt().toFloat()), mode.type)
                is SecondTriggerMode -> {
                    val x1 = x.roundToInt()
                    val y1 = y.roundToInt()
                    val x2 = mode.first.x.roundToInt()
                    val y2 = mode.first.y.roundToInt()
                    map.triggers.add(
                        WorldMap.Trigger(
                            mode.type,
                            min(x1, x2),
                            min(y1, y2),
                            abs(x1 - x2),
                            abs(y1 - y2),
                            -1
                        )
                    )

                    this.mode = FirstTriggerMode(mode.type)
                }
            }
        }
    }

    override fun draw(batch: Batch, parentAlpha: Float) {
        super.draw(batch, parentAlpha)

        batch.end()
        Gdx.gl.glEnable(GL20.GL_BLEND)

        shape.projectionMatrix = batch.projectionMatrix
        shape.begin(ShapeRenderer.ShapeType.Filled)

        when (val mode = mode) {
            is FirstTriggerMode -> {
                shape.color = mode.type.color

                tmp.set(Gdx.input.x.toFloat(), stage.height - Gdx.input.y)
                screenToLocalCoordinates(tmp)
                tmp.x = tmp.x.roundToInt().toFloat()
                tmp.y = tmp.y.roundToInt().toFloat()
                localToScreenCoordinates(tmp)
                shape.rect(tmp.x, tmp.y, scaleX, scaleY)
            }

            is SecondTriggerMode -> {
                shape.color = mode.type.color
                shape.color.a = 0.5f

                // Holy fuck never touch this ever again
                tmp.set(mode.first)
                localToScreenCoordinates(tmp)
                tmp.y = stage.height - tmp.y
                val x1 = Gdx.input.x.toFloat()
                val y1 = (stage.height - Gdx.input.y)
                shape.rect(min(x1, tmp.x), min(y1, tmp.y), abs(x1 - tmp.x), abs(y1 - tmp.y))
            }
        }

        for (trigger in map.triggers) {
            shape.color = trigger.type.color
            shape.color.a = 0.35f
            tmp.set(trigger.x.toFloat(), trigger.y.toFloat())
            localToScreenCoordinates(tmp)
            tmp.y = stage.height - tmp.y
            shape.rect(tmp.x, tmp.y, trigger.width * scaleX, trigger.height * scaleY)
        }

        shape.end()
        batch.begin()
    }

    fun scroll(x: Float, y: Float) {
        this.x += x
        this.y += y
        scrolled = true
    }

    fun zoom(amount: Float) {
        if (mode is SecondTriggerMode) return
        x /= scaleX
        y /= scaleY
        scaleBy(amount)
        x *= scaleX
        y *= scaleY
    }
}

sealed class EditingMode
class FirstTriggerMode(val type: WorldMap.TriggerType) : EditingMode()
class SecondTriggerMode(val first: Vector2, val type: WorldMap.TriggerType) : EditingMode()
