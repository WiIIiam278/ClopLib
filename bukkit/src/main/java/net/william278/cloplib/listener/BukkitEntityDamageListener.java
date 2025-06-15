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
import org.bukkit.entity.*;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityCombustByEntityEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.event.vehicle.VehicleDamageEvent;
import org.bukkit.event.vehicle.VehicleDestroyEvent;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.projectiles.BlockProjectileSource;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public interface BukkitEntityDamageListener extends BukkitListener {

    @EventHandler(ignoreCancelled = true)
    default void onEntityDamageEntity(@NotNull EntityDamageByEntityEvent e) {
        // Protect against player & projectile-player damage source
        final Optional<Player> damaging = getPlayerSource(e.getDamager());
        if (damaging.isPresent()) {
            this.handlePlayerDamager(damaging.get(), e.getEntity(), e);
            return;
        }
        if (e.getDamager() instanceof Projectile projectile) {
            this.handleProjectileDamager(projectile, e.getEntity(), e);
            return;
        }

        // Protect against mobs being hurt by explosions
        final EntityDamageEvent.DamageCause cause = e.getCause();
        if (cause == EntityDamageEvent.DamageCause.BLOCK_EXPLOSION
                || cause == EntityDamageEvent.DamageCause.ENTITY_EXPLOSION
                && !isMonster(e.getEntity())) {
            if (getHandler().cancelOperation(Operation.of(
                    OperationType.EXPLOSION_DAMAGE_ENTITY,
                    getPosition(e.getEntity().getLocation())
            ))) {
                e.setCancelled(true);
            }
        }
    }

    @EventHandler(ignoreCancelled = true)
    default void onEntityLightEntityOnFire(@NotNull EntityCombustByEntityEvent e) {
        // Protect against players & projectiles lighting players on fire
        final Optional<Player> damager = getPlayerSource(e.getCombuster());
        if (damager.isPresent()) {
            this.handlePlayerDamager(damager.get(), e.getEntity(), e);
            return;
        }
        if (e.getCombuster() instanceof Projectile projectile) {
            this.handleProjectileDamager(projectile, e.getEntity(), e);
        }
    }

    @EventHandler(ignoreCancelled = true)
    default void onPlayerFishEntity(@NotNull PlayerFishEvent e) {
        // Protect against players reeling in others with fishing rods
        if (e.getState() != PlayerFishEvent.State.CAUGHT_ENTITY || e.getCaught() == null) return;
        final OperationUser catcher = getUser(e.getPlayer());

        // Handle a player reeling in a player
        final Optional<Player> caughtPlayer = getPlayerSource(e.getCaught());
        if (caughtPlayer.isPresent()) {
            if (getHandler().cancelOperation(Operation.of(
                    catcher,
                    getUser(caughtPlayer.get()),
                    OperationType.PLAYER_DAMAGE_PLAYER,
                    getPosition(caughtPlayer.get().getLocation())
            ))) {
                e.setCancelled(true);
            }
            return;
        }

        // Determine the Operation type based on the entity being reeled in
        if (getHandler().cancelOperation(Operation.of(
                catcher,
                getPlayerDamageType(e.getCaught()),
                getPosition(e.getCaught().getLocation())
        ))) {
            e.setCancelled(true);
        }
    }

    // Handle breaking boats & mine carts
    @EventHandler(ignoreCancelled = true)
    default void onVehicleDamage(@NotNull VehicleDamageEvent e) {
        this.handleVehicleDamage(e.getVehicle(), e.getAttacker(), e);
    }

    @EventHandler(ignoreCancelled = true)
    default void onVehicleDestroy(@NotNull VehicleDestroyEvent e) {
        this.handleVehicleDamage(e.getVehicle(), e.getAttacker(), e);
    }

    private <E extends Event & Cancellable> void handlePlayerDamager(@NotNull Player damager, @NotNull Entity entity,
                                                                     @NotNull E e) {
        final Optional<Player> damaged = getPlayerSource(entity);
        if (damaged.isPresent()) {
            if (getHandler().cancelOperation(Operation.of(
                    getUser(damager),
                    getUser(damaged.get()),
                    OperationType.PLAYER_DAMAGE_PLAYER,
                    getPosition(damaged.get().getLocation())
            ))) {
                e.setCancelled(true);
            }
            return;
        }

        // Determine the Operation type based on the entity being damaged
        if (getHandler().cancelOperation(Operation.of(
                getUser(damager),
                getPlayerDamageType(entity),
                getPosition(entity.getLocation())
        ))) {
            e.setCancelled(true);
        }
    }

    private <E extends Event & Cancellable> void handleProjectileDamager(@NotNull Projectile proj, @NotNull Entity entity,
                                                                         @NotNull E e) {
        // Prevent projectiles dispensed outside of claims from harming stuff in claims
        if (proj.getShooter() instanceof BlockProjectileSource shooter) {
            final OperationPosition blockLocation = getPosition(shooter.getBlock().getLocation());
            if (getHandler().cancelNature(
                    blockLocation.getWorld(),
                    blockLocation,
                    getPosition(entity.getLocation())
            )) {
                e.setCancelled(true);
            }
            return;
        }

        // Prevent projectiles shot by mobs from harming passive mobs, hanging entities & armor stands
        if (!(entity instanceof Player || isMonster(entity))
                && isMonster(proj.getShooter())) {
            if (getHandler().cancelOperation(Operation.of(
                    OperationType.MONSTER_DAMAGE_TERRAIN,
                    getPosition(entity.getLocation())
            ))) {
                e.setCancelled(true);
            }
        }
    }

    default <E extends Event & Cancellable> void handleVehicleDamage(@NotNull Vehicle vehicle, @Nullable Entity attacker,
                                                                     @NotNull E event) {
        final Optional<Player> damaging = getPlayerSource(attacker);
        if (damaging.isPresent()) {
            this.handlePlayerDamager(damaging.get(), vehicle, event);
            return;
        }
        if (attacker instanceof Projectile projectile) {
            this.handleProjectileDamager(projectile, vehicle, event);
            return;
        }

        // Protect against mobs being hurt by explosions
        if (getHandler().cancelOperation(Operation.of(
                OperationType.BREAK_VEHICLE,
                getPosition(vehicle.getLocation()),
                true
        ))) {
            event.setCancelled(true);
        }
    }

    @NotNull
    private OperationType getPlayerDamageType(@NotNull Entity entity) {
        OperationType type = OperationType.PLAYER_DAMAGE_ENTITY;
        if (isMonster(entity)) {
            type = OperationType.PLAYER_DAMAGE_MONSTER;
        } else if (entity instanceof Vehicle vehicle && !(entity instanceof Mob)) {
            type = vehicle instanceof InventoryHolder ? OperationType.BLOCK_BREAK : OperationType.BREAK_VEHICLE;
        } else if (entity instanceof LivingEntity living && !living.getRemoveWhenFarAway()
                || entity.getCustomName() != null) {
            type = OperationType.PLAYER_DAMAGE_PERSISTENT_ENTITY;
        }
        return type;
    }

}
