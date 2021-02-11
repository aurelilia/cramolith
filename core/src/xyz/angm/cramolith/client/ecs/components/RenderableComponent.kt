/*
 * Developed as part of the Cramolith project.
 * This file was last modified at 2/11/21, 6:32 PM.
 * Copyright 2021, see git repository at git.angm.xyz for authors and other info.
 * This file is under the GPL3 license. See LICENSE in the root directory of this repository for details.
 */

package xyz.angm.cramolith.client.ecs.components

import com.badlogic.gdx.scenes.scene2d.Actor
import xyz.angm.rox.Component

/** An entity with this component is able to be rendered into the game world.
 * See RenderSystem for usage and position updating. */
class RenderableComponent(val actor: Actor) : Component
