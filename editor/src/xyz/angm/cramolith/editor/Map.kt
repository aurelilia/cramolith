/*
 * Developed as part of the Cramolith project.
 * This file was last modified at 2/10/21, 5:36 PM.
 * Copyright 2021, see git repository at git.angm.xyz for authors and other info.
 * This file is under the GPL3 license. See LICENSE in the root directory of this repository for details.
 */

package xyz.angm.cramolith.editor

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.math.Vector2
import com.kotcrab.vis.ui.VisUI
import com.kotcrab.vis.ui.widget.VisImage
import ktx.actors.onClickEvent
import xyz.angm.cramolith.common.world.WorldMap
import xyz.angm.cramolith.editor.modes.EditingMode
import xyz.angm.cramolith.editor.modes.SecondTriggerMode
import kotlin.math.roundToInt

val tmp = Vector2()

class Map(private val screen: EditorScreen, map: WorldMap) : VisImage(map.texture) {

    private var scrolled = false

    var mode: EditingMode? = null
    internal val shape = ShapeRenderer()
    private val font = VisUI.getSkin().get("big", BitmapFont::class.java)

    var map = map
        set(value) {
            field = value
            setDrawable(value.texture)
            screen.mapOrLayoutChanged()
        }

    init {
        scaleBy(5f)
        onClickEvent { _, x, y ->
            if (scrolled) scrolled = false
            else mode?.handleClick(this, x, y)
        }
    }

    override fun draw(batch: Batch, parentAlpha: Float) {
        super.draw(batch, parentAlpha)
        batch.end()
        Gdx.gl.glEnable(GL20.GL_BLEND)

        shape.projectionMatrix = batch.projectionMatrix
        shape.begin(ShapeRenderer.ShapeType.Filled)

        mode?.drawShape(this)
        for (trigger in map.triggers) {
            shape.color = trigger.type.color
            shape.color.a = 0.5f
            tmp.set(trigger.x.toFloat(), trigger.y.toFloat())
            localToScreenCoordinates(tmp)
            tmp.y = stage.height - tmp.y
            shape.rect(tmp.x, tmp.y, trigger.width * scaleX, trigger.height * scaleY)
        }

        shape.end()
        batch.begin()

        for (trigger in map.triggers) {
            if (trigger.idx < 0) continue
            tmp.set(trigger.x.toFloat(), trigger.y.toFloat())
            localToScreenCoordinates(tmp)
            tmp.y = stage.height - tmp.y
            font.draw(batch, trigger.idx.toString(), tmp.x + 5f, tmp.y + 55f)
        }

        for (actor in map.actors.values) {
            tmp.set(actor.x.toFloat(), actor.y.toFloat())
            localToScreenCoordinates(tmp)
            tmp.y = stage.height - tmp.y
            batch.draw(actor.drawable, tmp.x, tmp.y, scaleX * actor.drawable.width, scaleY * actor.drawable.height)
        }

        mode?.drawBatch(batch, this)
    }

    fun scroll(x: Float, y: Float) {
        this.x += x
        this.y += y
        scrolled = true
    }

    fun scrollSnap() {
        x = (x.roundToInt() / scaleX).toInt().toFloat() * scaleX
        y = (y.roundToInt() / scaleY).toInt().toFloat() * scaleY
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
