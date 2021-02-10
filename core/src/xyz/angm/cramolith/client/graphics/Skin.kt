/*
 * Developed as part of the Cramolith project.
 * This file was last modified at 2/10/21, 3:14 AM.
 * Copyright 2021, see git repository at git.angm.xyz for authors and other info.
 * This file is under the GPL3 license. See LICENSE in the root directory of this repository for details.
 */

package xyz.angm.cramolith.client.graphics

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.Pixmap
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.Window
import com.kotcrab.vis.ui.VisUI
import com.kotcrab.vis.ui.widget.VisTextButton
import com.kotcrab.vis.ui.widget.VisTextField
import com.kotcrab.vis.ui.widget.tabbedpane.TabbedPane
import ktx.assets.disposeSafely
import ktx.assets.file
import ktx.scene2d.Scene2DSkin
import ktx.style.*

/** The skin used for all UI objects in the client. */
object Skin {

    /** The height of text buttons in most menus. */
    const val textButtonHeight = 48f

    /** The width of text buttons in most menus. */
    const val textButtonWidth = 400f

    private val colors5 = mapOf(
        Pair("white", Color.WHITE),
        Pair("light-grey", Color.LIGHT_GRAY),
        Pair("black-transparent", Color(0f, 0f, 0f, 0.5f)),
        Pair("black", Color.BLACK),
        Pair("dark-grey", Color.DARK_GRAY),
        Pair("transparent", Color(0f, 0f, 0f, 0f)),
        Pair("dark-green", Color(0.3f, 0.4f, 0.3f, 1f))
    )

    /** Reload the skin. Only needs to be called on init or when the resource pack changes. */
    fun reload() {
        val notoGen = FreeTypeFontGenerator(file("font/noto.ttf"))
        val monospaceGen = FreeTypeFontGenerator(file("font/monospace.ttf"))
        val parameter = FreeTypeFontGenerator.FreeTypeFontParameter()
        parameter.magFilter = Texture.TextureFilter.Linear

        VisUI.getSkin().disposeSafely()
        VisUI.dispose(false)
        VisUI.load()
        val it = VisUI.getSkin()
        Scene2DSkin.defaultSkin = VisUI.getSkin().apply {
            add("default", notoGen.generateFont(parameter))
            val noto = get<BitmapFont>()
            noto.data.markupEnabled = true
            add("monospace", monospaceGen.generateFont(parameter))

            parameter.size = 50
            add("big", notoGen.generateFont(parameter))

            colors5.forEach { color ->
                val pixmap = Pixmap(5, 5, Pixmap.Format.RGBA8888)
                pixmap.setColor(color.value)
                pixmap.fill()
                add(color.key, Texture(pixmap))
            }

            label("debug") {
                font = it["monospace"]
                fontColor = Color.WHITE
                background = it["black-transparent"]
            }

            progressBar("default-horizontal") {
                background = it["light-grey"]
                knobBefore = it["white"]
            }

            visTextField("chat-input") {
                font = get<VisTextField.VisTextFieldStyle>().font
                fontColor = Color.WHITE
                background = it["black-transparent"]
                cursor = it["white"]
                selection = it["dark-grey"]
            }

            button {
                up = it["black"]
                over = it["dark-grey"]
                checked = it["dark-green"]
            }

            button("list") {
                up = it["dark-grey"]
                over = it["transparent"]
                checked = it["dark-grey"]
            }

            scrollPane {}

            get<VisTextButton.VisTextButtonStyle>().font = noto
            get<VisTextField.VisTextFieldStyle>().font = noto
            get<Label.LabelStyle>().font = noto
            get<Window.WindowStyle>().titleFont = noto
            get<TabbedPane.TabbedPaneStyle>().buttonStyle.font = noto
        }

        monospaceGen.dispose()
    }
}


