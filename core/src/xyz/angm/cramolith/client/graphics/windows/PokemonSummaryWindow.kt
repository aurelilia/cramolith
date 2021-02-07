/*
 * Developed as part of the Cramolith project.
 * This file was last modified at 2/7/21, 12:59 AM.
 * Copyright 2021, see git repository at git.angm.xyz for authors and other info.
 * This file is under the GPL3 license. See LICENSE in the root directory of this repository for details.
 */

package xyz.angm.cramolith.client.graphics.windows

import ktx.actors.plusAssign
import ktx.scene2d.scene2d
import ktx.scene2d.vis.visImage
import ktx.scene2d.vis.visTable
import xyz.angm.cramolith.client.graphics.screens.GameScreen
import xyz.angm.cramolith.common.pokemon.Pokemon

class PokemonSummaryWindow(screen: GameScreen, pokemon: Pokemon) : Window("summary") {

    init {
        addCloseButton()
        this += scene2d.visTable {
            visImage(pokemon.species.sprite)

            setFillParent(true)
        }
    }
}