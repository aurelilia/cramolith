/*
 * Developed as part of the Cramolith project.
 * This file was last modified at 2/10/21, 4:42 PM.
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
import com.kotcrab.vis.ui.util.Validators
import com.kotcrab.vis.ui.util.dialog.Dialogs
import com.kotcrab.vis.ui.util.dialog.InputDialogAdapter
import com.kotcrab.vis.ui.widget.VisImage
import ktx.actors.onClickEvent
import xyz.angm.cramolith.common.world.Trigger
import xyz.angm.cramolith.common.world.TriggerType
import xyz.angm.cramolith.common.world.WorldMap
import kotlin.math.abs
import kotlin.math.min
import kotlin.math.roundToInt

private val tmp = Vector2()

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

        mode?.draw(batch, this)
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

sealed class EditingMode {
    abstract fun handleClick(map: Map, x: Float, y: Float)
    abstract fun draw(batch: Batch, map: Map)
    abstract fun cancel(map: Map)
}

class FirstTriggerMode(private val type: TriggerType) : EditingMode() {

    override fun handleClick(map: Map, x: Float, y: Float) {
        map.mode = SecondTriggerMode(Vector2(x.roundToInt().toFloat(), y.roundToInt().toFloat()), type)
    }

    override fun draw(batch: Batch, map: Map): Unit = map.run {
        shape.color = type.color

        tmp.set(Gdx.input.x.toFloat(), (stage.height - Gdx.input.y))
        screenToLocalCoordinates(tmp)
        tmp.x = tmp.x.roundToInt().toFloat()
        tmp.y = tmp.y.roundToInt().toFloat()
        localToScreenCoordinates(tmp)
        shape.rect(tmp.x, tmp.y, scaleX, scaleY)
    }

    override fun cancel(map: Map) {
        map.mode = null
    }
}

class SecondTriggerMode(private val first: Vector2, private val type: TriggerType) : EditingMode() {

    override fun handleClick(map: Map, x: Float, y: Float): Unit = map.run {
        mode = null
        val x1 = x.roundToInt()
        val y1 = y.roundToInt()
        val x2 = first.x.roundToInt()
        val y2 = first.y.roundToInt()

        val add = { idx: Int ->
            map.map.triggers.add(
                Trigger(
                    type,
                    min(x1, x2),
                    min(y1, y2),
                    abs(x1 - x2),
                    abs(y1 - y2),
                    idx
                )
            )

            mode = FirstTriggerMode(type)
        }

        if (type.indexSays == null) add(-1)
        else {
            Dialogs.showInputDialog(stage, "Enter ${type.indexSays}", null, Validators.INTEGERS, object : InputDialogAdapter() {
                override fun finished(input: String) = add(Integer.parseInt(input))
            })
        }
    }

    override fun draw(batch: Batch, map: Map) = map.run {
        shape.color = type.color
        shape.color.a = 0.7f

        // Holy fuck never touch this ever again
        tmp.set(first)
        localToScreenCoordinates(tmp)
        tmp.y = stage.height - tmp.y
        val x1 = Gdx.input.x.toFloat()
        val y1 = (stage.height - Gdx.input.y)
        shape.rect(min(x1, tmp.x), min(y1, tmp.y), abs(x1 - tmp.x), abs(y1 - tmp.y))
    }

    override fun cancel(map: Map) {
        map.mode = FirstTriggerMode(type)
    }
}
