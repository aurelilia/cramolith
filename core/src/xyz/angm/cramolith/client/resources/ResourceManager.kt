/*
 * Developed as part of the Cramolith project.
 * This file was last modified at 2/9/21, 7:06 PM.
 * Copyright 2021, see git repository at git.angm.xyz for authors and other info.
 * This file is under the GPL3 license. See LICENSE in the root directory of this repository for details.
 */

package xyz.angm.cramolith.client.resources

import com.badlogic.gdx.assets.AssetDescriptor
import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.files.FileHandle
import com.badlogic.gdx.graphics.Texture
import ktx.assets.file
import xyz.angm.cramolith.client.graphics.Skin
import xyz.angm.cramolith.common.pokemon.Species

/** Object for retrieving resources. */
object ResourceManager {

    private val assets = AssetManager()

    /** Called after libGDX application has been created, for initialization. */
    fun init() {
        loadMenuAssets()
        loadGameAssets()
    }

    /** Returns a resource from the asset manager. */
    fun <T> get(file: String): T = assets.get(file)

    private inline fun <reified T : Any> load(file: String) = assets.load(file, T::class.java)
    private inline fun <reified T : Any> load(file: FileHandle) = assets.load(AssetDescriptor(file, T::class.java))

    /** Continues loading game assets. Returns loading progress as a float with value range 0-1. 1 means loading is finished.
     * @param processingTime How long to process, in milliseconds. */
    fun continueLoading(processingTime: Int = 10): Float {
        val time = System.currentTimeMillis()
        while (!assets.isFinished && (System.currentTimeMillis() - time) < processingTime)
            assets.update()
        return assets.progress
    }

    private fun loadGameAssets() {
        load<Texture>("sprites/player.png")
        for (mon in Species.all()) {
            load<Texture>("sprites/pokemon/${mon.ident}.png")
            load<Texture>("sprites/pokemon/icon/${mon.ident}.png")
        }
        file("map/").list(".png").forEach { load<Texture>(it) }
    }

    private fun loadMenuAssets() {
        Skin.reload()
    }
}