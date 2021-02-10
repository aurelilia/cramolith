/*
 * Developed as part of the Cramolith project.
 * This file was last modified at 2/10/21, 7:46 PM.
 * Copyright 2021, see git repository at git.angm.xyz for authors and other info.
 * This file is under the GPL3 license. See LICENSE in the root directory of this repository for details.
 */

package xyz.angm.cramolith.client.ecs.systems

import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.math.Vector2
import ktx.collections.*
import xyz.angm.cramolith.client.graphics.screens.GameScreen
import xyz.angm.cramolith.client.world.PLAYER_SPRITE_SIZE
import xyz.angm.cramolith.common.ecs.position
import xyz.angm.cramolith.common.world.Trigger
import xyz.angm.cramolith.common.world.TriggerType.*
import xyz.angm.rox.systems.EntitySystem

const val MAX_TRIGGER_DIST = 500 * 500
const val MAX_UPDATE_DIST = 500 * 500

class TriggerSystem(private val screen: GameScreen) : EntitySystem() {

    private val player = screen.player
    private val playerRect = Rectangle(player[position].x, player[position].y, PLAYER_SPRITE_SIZE, PLAYER_SPRITE_SIZE)
    private val triggerRect = Rectangle()

    private val lastTriggerPos = player[position].cpy()
    private val lastPlayerPos = player[position].cpy()
    private val triggers = GdxArray<Trigger>()

    init {
        updateTriggers()
    }

    override fun update(delta: Float) {
        val pos = player[position]
        if (lastTriggerPos.dst2(pos) > MAX_UPDATE_DIST) updateTriggers()
        playerRect.setPosition(pos)

        for (trigger in triggers) {
            if (isTriggerHit(trigger)) triggerHit(pos, trigger)
        }

        lastPlayerPos.set(pos)
    }

    private fun triggerHit(pos: Vector2, trigger: Trigger) {
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

            Teleport -> println("TP")

            Actor -> TODO()
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