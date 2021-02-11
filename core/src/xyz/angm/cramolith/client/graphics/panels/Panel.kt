/*
 * Developed as part of the Cramolith project.
 * This file was last modified at 2/11/21, 6:51 PM.
 * Copyright 2021, see git repository at git.angm.xyz for authors and other info.
 * This file is under the GPL3 license. See LICENSE in the root directory of this repository for details.
 */

package xyz.angm.cramolith.client.graphics.panels

import com.badlogic.gdx.Input
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.kotcrab.vis.ui.widget.VisTextButton
import ktx.actors.onClick
import ktx.actors.onKeyDown
import ktx.scene2d.Scene2DSkin
import ktx.scene2d.vis.KVisTable
import xyz.angm.cramolith.client.graphics.Skin
import xyz.angm.cramolith.client.graphics.screens.MenuScreen
import xyz.angm.cramolith.client.resources.I18N

/** A panel is overlaid onto a screen, and used for UI.
 * @param screen The screen currently active
 * @property focusedActor The actor to receive keyboard & scroll focus. Defaults to the panel itself. */
@Suppress("LeakingThis") // While this can be an issue, the methods called should not be overridden anyways
abstract class Panel(screen: MenuScreen) : Table(Scene2DSkin.defaultSkin) {

    protected open var focusedActor: Actor = this

    init {
        setFillParent(true)
        background = skin.getDrawable("black-transparent")

        onKeyDown { keycode ->
            if (keycode == Input.Keys.ESCAPE) screen.popPanel()
        }
    }

    /** A function that will add a back button to a panel constructed with KTX,
     * see most panels in menu for an example. */
    internal fun KVisTable.backButton(screen: MenuScreen) = textBtn("back") { screen.popPanel() }

    override fun setStage(stage: Stage?) {
        super.setStage(stage)
        stage?.keyboardFocus = focusedActor
        stage?.scrollFocus = focusedActor
    }

    override fun setVisible(visible: Boolean) {
        super.setVisible(visible)
        if (visible) {
            stage?.keyboardFocus = focusedActor
            stage?.scrollFocus = focusedActor
        }
    }
}

/** A function that will add a back button to a panel constructed with KTX,
 * see most panels in menu for an example. */
inline fun Table.textBtn(text: String, row: Boolean = true, colspan: Int = 1, crossinline clicked: VisTextButton.() -> Unit): VisTextButton {
    val btn = VisTextButton(I18N.tryGet(text) ?: text)
    val cell = add(btn).height(Skin.textButtonHeight).width(Skin.textButtonWidth).pad(8f).colspan(colspan)
    if (row) cell.row()
    btn.onClick(clicked)
    return btn
}
