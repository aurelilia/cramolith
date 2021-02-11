/*
 * Developed as part of the Cramolith project.
 * This file was last modified at 2/11/21, 11:16 PM.
 * Copyright 2021, see git repository at git.angm.xyz for authors and other info.
 * This file is under the GPL3 license. See LICENSE in the root directory of this repository for details.
 */

package xyz.angm.cramolith.editor.modes

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.math.Vector2
import com.kotcrab.vis.ui.util.Validators
import com.kotcrab.vis.ui.util.dialog.Dialogs
import com.kotcrab.vis.ui.util.dialog.InputDialogAdapter
import xyz.angm.cramolith.common.world.Trigger
import xyz.angm.cramolith.common.world.TriggerType
import xyz.angm.cramolith.editor.Map
import xyz.angm.cramolith.editor.tmp
import kotlin.math.abs
import kotlin.math.min
import kotlin.math.roundToInt

class DeleteTriggerMode : EditingMode() {
    override fun handleClick(map: Map, x: Float, y: Float) {
        val rect = Rectangle()
        map.map.triggers.removeIf { trigger ->
            rect.set(trigger.x.toFloat(), trigger.y.toFloat(), trigger.width.toFloat(), trigger.height.toFloat())
            rect.contains(x, y)
        }
    }

    override fun drawShape(map: Map) = map.run {
        shape.color = Color.RED
        tmp.set(Gdx.input.x.toFloat(), (stage.height - Gdx.input.y))
        shape.rect(tmp.x - 10f, tmp.y - 10f, 20f, 20f)
    }

    override fun cancel(map: Map) {
        map.mode = null
    }
}

class FirstTriggerMode(private val type: TriggerType) : EditingMode() {

    override fun handleClick(map: Map, x: Float, y: Float) {
        map.mode = SecondTriggerMode(Vector2(x.roundToInt().toFloat(), y.roundToInt().toFloat()), type)
    }

    override fun drawShape(map: Map): Unit = map.run {
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

    override fun drawShape(map: Map) = map.run {
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
