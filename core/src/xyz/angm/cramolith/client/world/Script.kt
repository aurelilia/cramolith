/*
 * Developed as part of the Cramolith project.
 * This file was last modified at 2/10/21, 11:37 PM.
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
import xyz.angm.cramolith.client.graphics.click
import xyz.angm.cramolith.client.graphics.screens.GameScreen
import xyz.angm.cramolith.client.resources.I18N

class Script(private val screen: GameScreen, private val lines: MutableList<String>) {

    private val id: String = lines[0]
    private var title = ""
    private var index = 1
    private var dialogIdx = 0

    init {
        next()
    }

    fun next() {
        if (lines.size == index) return
        val line = lines[index++]
        val idx = line.indexOfFirst { it == ' ' } - 1
        val command = line.substring(0..idx)
        val operands = line.substring((idx + 2) until line.length)

        when (command) {
            "dialog" -> {
                val count = Integer.parseInt(operands)
                val list = GdxArray<String>()
                for (i in count - 1 downTo 0) {
                    list.add(I18N["dialog.$id.${dialogIdx + i}"])
                    println("dialog.$id.${dialogIdx + i}")
                }
                dialogIdx += count
                screen.stage += TextWindow(title, list) { next() }
            }

            "title" -> {
                title = I18N["dialogtitle.$operands"]
                next()
            }

            else -> Dialogs.showErrorDialog(
                screen.stage, "Encountered unknown command $command while executing actor script. Aborting, please report to devs."
            )
        }
    }

    class TextWindow(title: String, lines: GdxArray<String>, finished: () -> Unit) : VisWindow(title) {

        init {
            val label = VisLabel(lines.pop())
            add(label).width(400f)
            val btn = VisTextButton(I18N["dialogui.next"])
            add(btn).pad(5f)
            btn.click()
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