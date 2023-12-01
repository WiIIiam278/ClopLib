/*
 * This file is part of ClopLib, licensed under the Apache License 2.0.
 *
 *  Copyright (c) William278 <will27528@gmail.com>
 *  Copyright (c) contributors
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package net.william278.cloplib.listener;

import net.william278.cloplib.operation.Operation;
import net.william278.cloplib.operation.OperationPosition;
import net.william278.cloplib.operation.OperationType;
import net.william278.cloplib.operation.OperationUser;
import org.bukkit.FluidCollisionMode;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.data.type.Sign;
import org.bukkit.block.data.type.Switch;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerArmorStandManipulateEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.InventoryHolder;
import org.jetbrains.annotations.NotNull;

import java.util.function.BiConsumer;

public interface BukkitInteractListener extends BukkitListener {

    String SPAWN_EGG_NAME = "spawn_egg";

    // Handle player interaction with blocks.
    // We must not ignoreCancelled here as clicking air fires this event in a canceled state.
    @EventHandler
    default void onPlayerInteract(@NotNull PlayerInteractEvent e) {
        switch (e.getAction()) {
            case RIGHT_CLICK_AIR -> {
                if (e.getHand() == EquipmentSlot.HAND) {
                    handleItemInteraction(e);
                }
            }
            case RIGHT_CLICK_BLOCK -> {
                if (e.getHand() == EquipmentSlot.HAND) {
                    if (handleItemInteraction(e)) {
                        return;
                    }
                }

                // Check against containers, switches and other block interactions
                final Block block = e.getClickedBlock();
                if (block != null && e.useInteractedBlock() != Event.Result.DENY) {
                    final String blockId = block.getType().getKey().toString();
                    if (getHandler().cancelOperation(Operation.of(
                            getUser(e.getPlayer()),
                            block.getState() instanceof InventoryHolder ? OperationType.CONTAINER_OPEN
                                    : getTypeChecker().isFarmMaterial(blockId) ? OperationType.FARM_BLOCK_INTERACT
                                    : block.getBlockData() instanceof Switch ? OperationType.REDSTONE_INTERACT
                                    : block.getBlockData() instanceof Sign ? OperationType.BLOCK_PLACE
                                    : OperationType.BLOCK_INTERACT,
                            getPosition(block.getLocation()),
                            e.getHand() == EquipmentSlot.OFF_HAND
                    ))) {
                        e.setUseInteractedBlock(Event.Result.DENY);
                        if (e.getItem() != null && e.getItem().getType() != Material.AIR) {
                            e.setUseItemInHand(Event.Result.DENY);
                        }
                    }
                }
            }
            case PHYSICAL -> {
                if (e.useInteractedBlock() == Event.Result.DENY) {
                    return;
                }

                final Block block = e.getClickedBlock();
                if (block != null && block.getType() != Material.AIR) {
                    if (getTypeChecker().isPressureSensitiveMaterial(block.getType().getKey().toString())) {
                        if (getHandler().cancelOperation(Operation.of(
                                getUser(e.getPlayer()),
                                OperationType.REDSTONE_INTERACT,
                                getPosition(block.getLocation())
                        ))) {
                            e.setUseInteractedBlock(Event.Result.DENY);
                        }
                        return;
                    }

                    if (getHandler().cancelOperation(Operation.of(
                            getUser(e.getPlayer()),
                            OperationType.BLOCK_INTERACT,
                            getPosition(block.getLocation())
                    ))) {
                        e.setUseInteractedBlock(Event.Result.DENY);
                    }
                }
            }
        }
    }

    // Handle using spawn eggs
    default boolean handleItemInteraction(@NotNull PlayerInteractEvent e) {
        // Check if the user was allowed to perform an action using an item in their main hand
        if (e.useItemInHand() != Event.Result.DENY) {
            return handleInspectionCallbacks(e) || handleSpawnEggs(e);
        }

        // Otherwise, the event was handled provided the user didn't right-click a block
        return e.getAction() != Action.RIGHT_CLICK_BLOCK;
    }

    // Handle claim inspection callbacks
    default boolean handleInspectionCallbacks(@NotNull PlayerInteractEvent e) {
        final String item = e.getPlayer().getInventory().getItemInMainHand().getType().getKey().getKey();
        if (!getInspectionHandlers().containsKey(item)) {
            return false;
        }

        // Consume the interact event
        e.setUseInteractedBlock(Event.Result.DENY);
        e.setUseItemInHand(Event.Result.DENY);

        // Execute the callback
        final BiConsumer<OperationUser, OperationPosition> callback = getInspectionHandlers().get(item);
        final Block block = e.getPlayer().getTargetBlockExact(getInspectionDistance(), FluidCollisionMode.NEVER);
        if (block != null) {
            callback.accept(getUser(e.getPlayer()), getPosition(block.getLocation()));
        }
        return true;
    }

    // Returns true if a spawn egg operation was handled
    default boolean handleSpawnEggs(@NotNull PlayerInteractEvent e) {
        final Material item = e.getPlayer().getInventory().getItemInMainHand().getType();
        if (item.getKey().toString().toLowerCase().contains(SPAWN_EGG_NAME)) {
            if (getHandler().cancelOperation(Operation.of(
                    getUser(e.getPlayer()),
                    OperationType.USE_SPAWN_EGG,
                    getPosition(e.getPlayer().getLocation())
            ))) {
                e.setUseItemInHand(Event.Result.DENY);
                e.setUseInteractedBlock(Event.Result.DENY);
            }
            return true;
        }
        return false;
    }

    @EventHandler(ignoreCancelled = true)
    default void onPlayerInteractEntity(@NotNull PlayerInteractEntityEvent e) {
        if (e.getRightClicked() instanceof Player) {
            return;
        }
        if (e.getHand() == EquipmentSlot.HAND) {
            if (getHandler().cancelOperation(Operation.of(
                    getUser(e.getPlayer()),
                    OperationType.ENTITY_INTERACT,
                    getPosition(e.getRightClicked().getLocation())
            ))) {
                e.setCancelled(true);
            }
        } else if (e.getHand() == EquipmentSlot.OFF_HAND) {
            if (getHandler().cancelOperation(Operation.of(
                    getUser(e.getPlayer()),
                    OperationType.ENTITY_INTERACT,
                    getPosition(e.getRightClicked().getLocation())
            ))) {
                e.setCancelled(true);
            }
        }
    }

    @EventHandler(ignoreCancelled = true)
    default void onPlayerArmorStand(@NotNull PlayerArmorStandManipulateEvent e) {
        if (getHandler().cancelOperation(Operation.of(
                getUser(e.getPlayer()),
                OperationType.CONTAINER_OPEN,
                getPosition(e.getRightClicked().getLocation())
        ))) {
            e.setCancelled(true);
        }
    }

}
