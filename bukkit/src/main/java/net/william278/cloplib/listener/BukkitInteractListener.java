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
import org.bukkit.block.Lectern;
import org.bukkit.block.Sign;
import org.bukkit.block.data.type.Switch;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Vehicle;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerArmorStandManipulateEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerTakeLecternBookEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
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
                    if (getHandler().cancelOperation(Operation.of(
                            getUser(e.getPlayer()),
                            switch (getInteractBehaviour(block)) {
                                case EDIT_SIGN -> OperationType.BLOCK_PLACE;
                                case REDSTONE_SWITCHED -> OperationType.REDSTONE_INTERACT;
                                case FARM_BLOCK -> OperationType.FARM_BLOCK_INTERACT;
                                case CONTAINER_OPENS -> OperationType.CONTAINER_OPEN;
                                default -> OperationType.BLOCK_INTERACT;
                            },
                            getPosition(block.getLocation()),
                            e.getHand() == EquipmentSlot.OFF_HAND
                    ))) {
                        // Allow eating while clicking blocks in others' claims
                        final Material usedInHand = e.getItem() != null ? e.getItem().getType() : null;
                        e.setUseInteractedBlock(Event.Result.DENY);
                        if (usedInHand != null && !(usedInHand == Material.AIR || usedInHand.isEdible())) {
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
                    if (getChecker().isPressureSensitiveMaterial(block.getType().getKey().toString())) {
                        if (!isPlayerNpc(e.getPlayer()) && getHandler().cancelOperation(Operation.of(
                                getUser(e.getPlayer()),
                                OperationType.REDSTONE_INTERACT,
                                getPosition(block.getLocation()),
                                true
                        ))) {
                            e.setUseInteractedBlock(Event.Result.DENY);
                        }
                        return;
                    }

                    if (!isPlayerNpc(e.getPlayer()) && getHandler().cancelOperation(Operation.of(
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
        final ItemStack item = e.getPlayer().getInventory().getItemInMainHand();
        final InspectionTool tool = getTool(item);
        if (!getInspectionToolHandlers().containsKey(tool)) {
            return false;
        }

        // Consume the item interact event
        e.setUseInteractedBlock(Event.Result.DENY);
        e.setUseItemInHand(Event.Result.DENY);

        // Execute the callback
        final BiConsumer<OperationUser, OperationPosition> callback = getInspectionToolHandlers().get(tool);
        final Block block = e.getPlayer().getTargetBlockExact(getInspectionDistance(), FluidCollisionMode.NEVER);
        if (block != null) {
            callback.accept(getUser(e.getPlayer()), getPosition(block.getLocation()));
        }
        return true;
    }

    @NotNull
    private InspectionTool getTool(@NotNull ItemStack item) {
        final InspectionTool.InspectionToolBuilder builder = InspectionTool.builder()
                .material(item.getType().getKey().toString());
        if (item.hasItemMeta() && item.getItemMeta() != null && item.getItemMeta().hasCustomModelData()) {
            builder.useCustomModelData(true).customModelData(item.getItemMeta().getCustomModelData());
        }
        return builder.build();
    }

    // Returns true if a spawn egg operation was handled
    default boolean handleSpawnEggs(@NotNull PlayerInteractEvent e) {
        final Material item = e.getPlayer().getInventory().getItemInMainHand().getType();
        if (item.getKey().toString().toLowerCase().contains(SPAWN_EGG_NAME)) {
            if (!isPlayerNpc(e.getPlayer()) && getHandler().cancelOperation(Operation.of(
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
        final Entity entity = e.getRightClicked();
        if (entity instanceof Player) {
            return;
        }

        // Check against interacting with container vehicles
        if (entity instanceof Vehicle && entity instanceof InventoryHolder && !isPlayerNpc(e.getPlayer())
            && getHandler().cancelOperation(Operation.of(
                getUser(e.getPlayer()),
                OperationType.CONTAINER_OPEN,
                getPosition(e.getRightClicked().getLocation()),
                e.getHand() == EquipmentSlot.OFF_HAND
        ))) {
            e.setCancelled(true);
            return;
        }

        if (!isPlayerNpc(e.getPlayer()) && getHandler().cancelOperation(Operation.of(
                getUser(e.getPlayer()),
                OperationType.ENTITY_INTERACT,
                getPosition(e.getRightClicked().getLocation()),
                e.getHand() == EquipmentSlot.OFF_HAND
        ))) {
            e.setCancelled(true);
        }
    }

    // When a player manipulates an armour stand
    @EventHandler(ignoreCancelled = true)
    default void onPlayerArmorStand(@NotNull PlayerArmorStandManipulateEvent e) {
        if (!isPlayerNpc(e.getPlayer()) && getHandler().cancelOperation(Operation.of(
                getUser(e.getPlayer()),
                OperationType.CONTAINER_OPEN,
                getPosition(e.getRightClicked().getLocation()),
                e.getHand() == EquipmentSlot.OFF_HAND
        ))) {
            e.setCancelled(true);
        }
    }

    // When a player takes a book from a lectern
    @EventHandler(ignoreCancelled = true)
    default void onPlayerTakeLecternBook(@NotNull PlayerTakeLecternBookEvent e) {
        if (!isPlayerNpc(e.getPlayer()) && getHandler().cancelOperation(Operation.of(
                getUser(e.getPlayer()),
                OperationType.CONTAINER_OPEN,
                getPosition(e.getLectern().getLocation())
        ))) {
            e.setCancelled(true);
        }
    }

    // Get the behaviour of a block
    @NotNull
    private BukkitInteractListener.InteractBehaviour getInteractBehaviour(@NotNull Block block) {
        final String blockId = block.getType().getKey().toString();
        if (block.getState() instanceof Lectern) {
            return InteractBehaviour.LECTERN_OPENS;
        }
        if (getChecker().isFarmMaterial(blockId)) {
            return InteractBehaviour.FARM_BLOCK;
        }
        if (block.getState() instanceof InventoryHolder) {
            return InteractBehaviour.CONTAINER_OPENS;
        }
        if (block.getBlockData() instanceof Switch) {
            return InteractBehaviour.REDSTONE_SWITCHED;
        }
        if (block.getState() instanceof Sign) {
            return InteractBehaviour.EDIT_SIGN;
        }
        return InteractBehaviour.STANDARD;
    }

    // Represents different behaviours when a block is interacted with
    enum InteractBehaviour {
        EDIT_SIGN,
        FARM_BLOCK,
        LECTERN_OPENS,
        CONTAINER_OPENS,
        REDSTONE_SWITCHED,
        STANDARD
    }

}
