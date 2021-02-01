/*
 * Developed as part of the PokeMMO project.
 * This file was last modified at 2/1/21, 5:10 PM.
 * Copyright 2020, see git repository at git.angm.xyz for authors and other info.
 * This file is under the GPL3 license. See LICENSE in the root directory of this repository for details.
 */

package xyz.angm.pokemmo.common.ecs

import xyz.angm.pokemmo.client.ecs.components.PlayerActorComponent
import xyz.angm.pokemmo.client.ecs.components.RenderableComponent
import xyz.angm.pokemmo.common.ecs.components.*
import xyz.angm.pokemmo.common.ecs.components.specific.PlayerComponent
import xyz.angm.rox.mapperFor

/*
* This file contains mappers for all components.
* Reusing them allows for better performance and prevents code duplication.
*/

val position = mapperFor<PositionComponent>()
val velocity = mapperFor<VelocityComponent>()

val playerM = mapperFor<PlayerComponent>()

val renderable = mapperFor<RenderableComponent>()

val network = mapperFor<NetworkSyncComponent>()
val ignoreSync = mapperFor<IgnoreSyncFlag>()
val remove = mapperFor<RemoveFlag>()
