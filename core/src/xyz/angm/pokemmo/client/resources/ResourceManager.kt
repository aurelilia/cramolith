/*
 * Developed as part of the PokeMMO project.
 * This file was last modified at 2/3/21, 9:18 PM.
 * Copyright 2020, see git repository at git.angm.xyz for authors and other info.
 * This file is under the GPL3 license. See LICENSE in the root directory of this repository for details.
 */

package xyz.angm.pokemmo.client.resources

import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.graphics.Texture
import xyz.angm.pokemmo.client.graphics.Skin

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

    /** Continues loading game assets. Returns loading progress as a float with value range 0-1. 1 means loading is finished.
     * @param processingTime How long to process, in milliseconds. */
    fun continueLoading(processingTime: Int = 10): Float {
        val time = System.currentTimeMillis()
        while ((System.currentTimeMillis() - time) < processingTime)
            assets.update()
        return assets.progress
    }

    private fun loadGameAssets() {
        load<Texture>("sprites/player.png")
    }

    private fun loadMenuAssets() {
        Skin.reload()
    }
}