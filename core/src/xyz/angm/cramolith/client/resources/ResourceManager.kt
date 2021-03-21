/*
 * Developed as part of the Cramolith project.
 * This file was last modified at 3/21/21, 10:00 PM.
 * Copyright 2021, see git repository at git.angm.xyz for authors and other info.
 * This file is under the GPL3 license. See LICENSE in the root directory of this repository for details.
 */

@file:Suppress("LibGDXStaticResource")

package xyz.angm.cramolith.client.resources

import com.badlogic.gdx.assets.AssetDescriptor
import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.files.FileHandle
import com.badlogic.gdx.graphics.Texture
import xyz.angm.cramolith.client.graphics.Skin
import xyz.angm.cramolith.common.pokemon.Species
import xyz.angm.cramolith.common.world.WorldMap

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

    fun loadTexture(file: String) {
        assets.load(file, Texture::class.java)
        assets.finishLoading()
    }

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
        load<Texture>("sprites/actors/actor_size.png")
        load<Texture>("sprites/actors/youngster.png")
        load<Texture>("sprites/actors/youngster_l.png")
        load<Texture>("sprites/actors/youngster_r.png")
        load<Texture>("sprites/actors/youngster_u.png")
        WorldMap.mapFileNames.forEach { load<Texture>("$it.png") }
    }

    private fun loadMenuAssets() {
        Skin.reload()
    }
}