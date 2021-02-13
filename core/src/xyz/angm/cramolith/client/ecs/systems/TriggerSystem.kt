/*
 * Developed as part of the Cramolith project.
 * This file was last modified at 2/13/21, 2:25 AM.
 * Copyright 2021, see git repository at git.angm.xyz for authors and other info.
 * This file is under the GPL3 license. See LICENSE in the root directory of this repository for details.
 */

package xyz.angm.cramolith.client.ecs.systems

import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.math.Vector2
import ktx.collections.*
import xyz.angm.cramolith.client.graphics.screens.GameScreen
import xyz.angm.cramolith.client.world.Script
import xyz.angm.cramolith.common.HUMAN_SIZE
import xyz.angm.cramolith.common.ecs.components.PositionComponent
import xyz.angm.cramolith.common.ecs.network
import xyz.angm.cramolith.common.ecs.playerM
import xyz.angm.cramolith.common.ecs.position
import xyz.angm.cramolith.common.networking.PlayerMapChangedPacket
import xyz.angm.cramolith.common.world.Trigger
import xyz.angm.cramolith.common.world.TriggerType.*
import xyz.angm.cramolith.common.world.WorldMap
import xyz.angm.rox.systems.EntitySystem

const val MAX_TRIGGER_DIST = 500 * 500
const val MAX_UPDATE_DIST = 500 * 500

/** System responsible for processing the triggers a player steps on.
 * Triggers are part of the map and cause some action when the player intersects them. */
class TriggerSystem(private val screen: GameScreen) : EntitySystem() {

    private val player = screen.player
    private val playerRect = Rectangle(player[position].x, player[position].y, HUMAN_SIZE, HUMAN_SIZE)
    private val triggerRect = Rectangle()

    private val lastTriggerPos = player[position].cpy()
    private val lastPlayerPos = player[position].cpy()
    private val triggers = GdxArray<Trigger>()

    private var playerWasOnTeleport = true
    private var hitTeleport = false

    init {
        updateTriggers()
    }

    override fun update(delta: Float) {
        hitTeleport = false
        val pos = player[position]
        if (lastTriggerPos.dst2(pos) > MAX_UPDATE_DIST) updateTriggers()
        playerRect.setPosition(pos)

        for (trigger in triggers) {
            if (isTriggerHit(trigger)) triggerHit(pos, trigger)
        }

        lastPlayerPos.set(pos)
        playerWasOnTeleport = hitTeleport
    }

    private fun triggerHit(pos: PositionComponent, trigger: Trigger) {
        when (trigger.type) {
            Collision -> {
                playerRect.x = lastPlayerPos.x
                if (!isTriggerHit(trigger)) pos.x = lastPlayerPos.x
                else {
                    playerRect.setPosition(pos)
                    playerRect.y = lastPlayerPos.y
                    if (!isTriggerHit(trigger)) pos.y = lastPlayerPos.y
                }
                playerRect.setPosition(pos)
            }

            Teleport -> {
                hitTeleport = true
                if (playerWasOnTeleport) return
                val teleport = screen.world.map.teleports[trigger.idx]
                val newMap = WorldMap.of(teleport.map)
                val zone = newMap.triggers.find { it.type == Teleport && it.idx == teleport.target }!!
                pos.x = zone.x.toFloat()
                pos.y = zone.y.toFloat()
                pos.map = newMap.index
                screen.world.map = newMap
                updateTriggers()
                screen.client.send(PlayerMapChangedPacket(screen.player[network].id))
            }

            Actor -> {
                val playerC = player[playerM]
                if (playerC.isInCutscene) return
                val map = playerC.actorsTriggered.getOrPut(screen.world.map.index, { HashSet() })
                if (map.contains(trigger.idx)) return

                playerC.isInCutscene = true
                player[network].needsSync = true
                screen.inputHandler.disabled = true

                val actor = screen.world.map.actorsId[trigger.idx]
                Script(screen, actor) {
                    playerC.isInCutscene = false
                    player[network].needsSync = true
                    screen.inputHandler.disabled = false
                }
            }
        }
    }

    private fun isTriggerHit(trigger: Trigger): Boolean {
        triggerRect.set(trigger.x.toFloat(), trigger.y.toFloat(), trigger.width.toFloat(), trigger.height.toFloat())
        return triggerRect.overlaps(playerRect)
    }

    private fun updateTriggers() {
        val pos = player[position]
        triggers.clear()
        lastTriggerPos.set(pos)
        for (trigger in screen.world.map.triggers) {
            if (isInRange(pos, trigger.x.toFloat(), trigger.y.toFloat(), trigger.width, trigger.height)) triggers.add(trigger)
        }
    }

    private fun isInRange(pos: Vector2, x: Float, y: Float, width: Int, height: Int) =
        pos.dst2(x, y) < MAX_TRIGGER_DIST
                || pos.dst2(x + width, y) < MAX_TRIGGER_DIST
                || pos.dst2(x, y + height) < MAX_TRIGGER_DIST
                || pos.dst2(x + width, y + height) < MAX_TRIGGER_DIST
}