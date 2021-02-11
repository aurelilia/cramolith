/*
 * Developed as part of the Cramolith project.
 * This file was last modified at 2/11/21, 7:37 PM.
 * Copyright 2021, see git repository at git.angm.xyz for authors and other info.
 * This file is under the GPL3 license. See LICENSE in the root directory of this repository for details.
 */

package xyz.angm.cramolith.client.world

import com.kotcrab.vis.ui.util.dialog.Dialogs
import com.kotcrab.vis.ui.widget.VisLabel
import com.kotcrab.vis.ui.widget.VisTextButton
import com.kotcrab.vis.ui.widget.VisWindow
import ktx.actors.onClick
import ktx.actors.plusAssign
import ktx.collections.*
import xyz.angm.cramolith.client.graphics.screens.GameScreen
import xyz.angm.cramolith.client.resources.I18N
import xyz.angm.cramolith.common.ecs.playerM
import xyz.angm.cramolith.common.world.WorldActor

/** A script that is executed when the player interacts with an actor
 * or steps on an Actor trigger. Script is part of the actor definition.
 *
 * Uses a small custom scripting language with following instructions:
 * - FIRST INSTRUCTION: Specifies the script id. See other instructions.
 * - `dialog X`: Display the next X dialog boxes, where the text for each box is I18N `dialog.$scriptid.$index`. Starts
 *               at 0 and counts up, remembering previous `dialog` instructions.
 * - `title X`: Sets the title of dialog boxes (the speaker) to I18N `dialogtitle.$X`.
 * - `disable trigger`: Sets this script as done, preventing the player from triggering ever it again.
 *                      Not putting this at the end will cause a script to be executable infinitely often. */
class Script(private val screen: GameScreen, private val actor: WorldActor, private val completed: () -> Unit) {

    private val id: String = actor.script[0]
    private var title = ""
    private var index = 1
    private var dialogIdx = 0

    init {
        next()
    }

    fun next() {
        if (actor.script.size == index) {
            completed()
            return
        }
        val line = actor.script[index++]
        val idx = line.indexOfFirst { it == ' ' } - 1
        val command = line.substring(0..idx)
        val operands = line.substring((idx + 2) until line.length)

        when (command) {
            "dialog" -> {
                val count = Integer.parseInt(operands)
                val list = GdxArray<String>()
                for (i in count - 1 downTo 0) {
                    list.add(I18N["dialog.$id.${dialogIdx + i}"])
                }
                dialogIdx += count
                screen.stage += TextWindow(title, list) { next() }
            }

            "title" -> {
                title = I18N["dialogtitle.$operands"]
                next()
            }

            "disable" -> {
                val map = screen.player[playerM].actorsTriggered.getOrPut(screen.world.map.index, { HashSet() })
                map.add(actor.index)
                next()
            }

            else -> {
                Dialogs.showErrorDialog(
                    screen.stage, "Encountered unknown command $command while executing actor script. Aborting, please report to devs."
                )
                completed()
            }
        }
    }

    class TextWindow(title: String, lines: GdxArray<String>, finished: () -> Unit) : VisWindow(title) {

        init {
            val label = VisLabel(lines.pop())
            add(label).width(400f)
            val btn = VisTextButton(I18N["dialogui.next"])
            add(btn).pad(5f)
            btn.onClick {
                if (lines.isEmpty) {
                    this@TextWindow.remove()
                    finished()
                } else {
                    val line = lines.pop()
                    label.setText(line)
                }
            }

            pack()
            centerWindow()
        }
    }
}