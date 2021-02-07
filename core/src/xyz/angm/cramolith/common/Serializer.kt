/*
 * Developed as part of the Cramolith project.
 * This file was last modified at 2/7/21, 9:32 PM.
 * Copyright 2021, see git repository at git.angm.xyz for authors and other info.
 * This file is under the GPL3 license. See LICENSE in the root directory of this repository for details.
 */

package xyz.angm.cramolith.common

import com.badlogic.gdx.math.Vector2
import com.charleskorn.kaml.Yaml
import org.nustaq.serialization.FSTConfiguration
import xyz.angm.cramolith.common.ecs.components.*
import xyz.angm.cramolith.common.ecs.components.specific.PlayerComponent
import xyz.angm.cramolith.common.ecs.ignoreSync
import xyz.angm.cramolith.common.ecs.renderable
import xyz.angm.cramolith.common.networking.ChatMessagePacket
import xyz.angm.cramolith.common.networking.InitPacket
import xyz.angm.cramolith.common.networking.JoinPacket
import xyz.angm.cramolith.common.pokemon.Pokemon
import xyz.angm.rox.Component
import xyz.angm.rox.Entity
import xyz.angm.rox.FSTEntitySerializer
import xyz.angm.rox.util.Bits
import kotlin.reflect.KClass

/** A simple YAML serializer used for configuration files and some game data. */
val yaml = Yaml()

/** A FST serializer used for network communication and world storage. */
val fst = createFST(
    // Packets
    JoinPacket::class, InitPacket::class, ChatMessagePacket::class,

    // Components
    Component::class, VectoredComponent::class,
    PositionComponent::class, VelocityComponent::class, PlayerComponent::class,
    RemoveFlag::class, NetworkSyncComponent::class,

    // Various
    Vector2::class, Entity::class, ArrayList::class, Pokemon::class,
)

private fun createFST(vararg classes: KClass<out Any>): FSTConfiguration {
    val fst = FSTConfiguration.createDefaultConfiguration()
    classes.forEach { fst.registerClass(it.java) }

    val ignore = Bits()
    ignore.set(ignoreSync.index)
    ignore.set(renderable.index)
    fst.registerSerializer(Entity::class.java, FSTEntitySerializer(ignore), true)

    return fst
}
