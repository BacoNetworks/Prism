/*
 * This file is part of Prism, licensed under the MIT License (MIT).
 *
 * Copyright (c) 2015 Helion3 http://helion3.com/
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package com.helion3.prism.listeners;

import com.helion3.prism.Prism;
import com.helion3.prism.api.records.PrismRecord;
import com.helion3.prism.util.PrismEvents;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.hanging.Hanging;
import org.spongepowered.api.entity.living.ArmorStand;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.Order;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.event.command.SendCommandEvent;
import org.spongepowered.api.event.entity.DestructEntityEvent;
import org.spongepowered.api.event.entity.SpawnEntityEvent;
import org.spongepowered.api.event.filter.Getter;
import org.spongepowered.api.event.filter.cause.Root;
import org.spongepowered.api.event.network.ClientConnectionEvent;

import java.util.List;

public class EntityListener {

    /**
     * Saves event records when a player disconnects.
     *
     * @param event ClientConnectionEvent.Disconnect
     */
    @Listener(order = Order.POST)
    public void onClientConnectionDisconnect(ClientConnectionEvent.Disconnect event, @Getter("getTargetEntity") Player player) {
        if (!Prism.getInstance().getConfig().getEventCategory().isPlayerDisconnect()) {
            return;
        }

        PrismRecord.create()
                .player(player)
                .event(PrismEvents.PLAYER_DISCONNECT)
                .location(player.getLocation())
                .buildAndSave();
    }

    /**
     * Saves event records when a player joins.
     *
     * @param event ClientConnectionEvent.Join
     */
    @Listener(order = Order.POST)
    public void onClientConnectionJoin(ClientConnectionEvent.Join event, @Getter("getTargetEntity") Player player) {
        if (!Prism.getInstance().getConfig().getEventCategory().isPlayerJoin()) {
            return;
        }

        PrismRecord.create()
                .player(player)
                .event(PrismEvents.PLAYER_JOIN)
                .location(player.getLocation())
                .buildAndSave();
    }

    /**
     * Saves event records when an entity dies. And also breaking of armor stands, paintings and item frames
     *
     * @param event DestructEntityEvent
     */
    @Listener(order = Order.POST)
    public void onDestructEntityDeath(DestructEntityEvent event, @Getter("getTargetEntity") Entity entity) {
        if (Prism.getInstance().getConfig().getEventCategory().isBlockBreak()) {
            if (entity instanceof Hanging || entity instanceof ArmorStand) {
                final Cause cause = event.getCause();
                final String targetFinal = entity.getType().getId().replace("_", " ");
                PrismRecord.create()
                        .source(cause)
                        .target(targetFinal)
                        .location(entity.getLocation())
                        .event(PrismEvents.BLOCK_BREAK).
                        buildAndSave();
                return;
            }
        }
        if (!Prism.getInstance().getConfig().getEventCategory().isEntityDeath() || !(event instanceof DestructEntityEvent.Death)) {
            return;
        }
        PrismRecord.create()
                .source(event.getCause())
                .event(PrismEvents.ENTITY_DEATH)
                .entity(event.getTargetEntity())
                .buildAndSave();
    }

    /**
     * Saves event records when an entity is spawned (Used for tracking when item frames, armor stands and paintings are placed)
     *
     * @param event SpawnEntityEvent
     */
    @Listener
    public void onEntitySpawn(SpawnEntityEvent event, @Getter("getEntities") List<Entity> entities) {
        if (Prism.getInstance().getConfig().getEventCategory().isBlockPlace()) {
            for (int i = 0, entitiesSize = entities.size(); i < entitiesSize; i++) {
                Entity entity = entities.get(i);
                if (entity instanceof Hanging || entity instanceof ArmorStand) {
                    final Cause cause = event.getCause();
                    final String targetFinal = entity.getType().getId().replace("_", " ");
                    PrismRecord.create()
                            .source(cause)
                            .target(targetFinal)
                            .location(entity.getLocation())
                            .event(PrismEvents.BLOCK_PLACE).
                            buildAndSave();
                }
            }
        }
    }

    /**
     * Saves event records when a player executes a command.
     *
     * @param event SendCommandEvent
     */
    @Listener(order = Order.POST)
    public void onSendCommand(SendCommandEvent event, @Root Player player) {
        if (!Prism.getInstance().getConfig().getEventCategory().isCommandExecute()) {
            return;
        }

        PrismRecord.create()
                .source(event.getCause())
                .event(PrismEvents.COMMAND_EXECUTE)
                .location(player.getLocation())
                .target(event.getCommand())
                .buildAndSave();
    }
}