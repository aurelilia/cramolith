/*
 * Developed as part of the Cramolith project.
 * This file was last modified at 2/4/21, 12:43 PM.
 * Copyright 2021, see git repository at git.angm.xyz for authors and other info.
 * This file is under the GPL3 license. See LICENSE in the root directory of this repository for details.
 */

package xyz.angm.cramolith.common.ecs

import xyz.angm.cramolith.client.ecs.components.RenderableComponent
import xyz.angm.cramolith.common.ecs.components.*
import xyz.angm.cramolith.common.ecs.components.specific.PlayerComponent
import xyz.angm.cramolith.common.ecs.components.specific.WildPokemonComponent
import xyz.angm.rox.mapperFor

/*
* This file contains mappers for all components.
* Reusing them allows for better performance and prevents code duplication.
*/

val position = mapperFor<PositionComponent>()
val velocity = mapperFor<VelocityComponent>()

val playerM = mapperFor<PlayerComponent>()
val wildPokemon = mapperFor<WildPokemonComponent>()

val renderable = mapperFor<RenderableComponent>()

val network = mapperFor<NetworkSyncComponent>()
val ignoreSync = mapperFor<IgnoreSyncFlag>()
val remove = mapperFor<RemoveFlag>()
