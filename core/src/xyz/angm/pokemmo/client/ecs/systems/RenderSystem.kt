/*
 * Developed as part of the PokeMMO project.
 * This file was last modified at 2/3/21, 9:11 PM.
 * Copyright 2020, see git repository at git.angm.xyz for authors and other info.
 * This file is under the GPL3 license. See LICENSE in the root directory of this repository for details.
 */

package xyz.angm.pokemmo.client.ecs.systems

import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.ui.Image
import xyz.angm.pokemmo.client.PokeMMO
import xyz.angm.pokemmo.client.ecs.components.RenderableComponent
import xyz.angm.pokemmo.client.graphics.screens.GameScreen
import xyz.angm.pokemmo.client.resources.ResourceManager
import xyz.angm.pokemmo.common.ecs.playerM
import xyz.angm.pokemmo.common.ecs.position
import xyz.angm.pokemmo.common.ecs.renderable
import xyz.angm.rox.Entity
import xyz.angm.rox.EntityListener
import xyz.angm.rox.Family.Companion.allOf
import xyz.angm.rox.systems.IteratingSystem

/** A system that automatically updates the positions of all renderable components that need it.
 * Also an entity listener for adding the rendering component to new entities. */
class RenderSystem(private val screen: GameScreen) : IteratingSystem(allOf(renderable, position)), EntityListener {

    override val family = allOf()

    /** Set the correct position of the rendering component of the entity. */
    override fun process(entity: Entity, delta: Float) {
        val pos = entity[position]
        entity[renderable].actor.x = pos.x
        entity[renderable].actor.y = pos.y
    }

    /** Add the entities model. */
    override fun entityAdded(entity: Entity) {
        PokeMMO.postRunnable {
            val actor = actorFor(entity)
            val component = RenderableComponent(actor)
            entity.add(engine, component)
            screen.stage.addActor(actor)
        }
    }

    override fun entityRemoved(entity: Entity) {
        entity[renderable].actor.remove()
    }

    private fun actorFor(e: Entity): Actor = when {
        e has playerM -> Image(ResourceManager.get<Texture>("sprites/player.png"))
        else -> TODO()
    }
}