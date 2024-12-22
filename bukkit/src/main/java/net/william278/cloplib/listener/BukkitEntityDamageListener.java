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
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityCombustByEntityEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityEvent;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.projectiles.BlockProjectileSource;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public interface BukkitEntityDamageListener extends BukkitListener {

    @EventHandler(ignoreCancelled = true)
    default void onEntityDamageEntity(@NotNull EntityDamageByEntityEvent e) {
        // Protect against player & projectile-player damage source
        final Optional<Player> damaging = getPlayerSource(e.getDamager());
        if (damaging.isPresent()) {
            this.handlePlayerDamager(damaging.get(), e);
            return;
        }
        if (e.getDamager() instanceof Projectile projectile) {
            this.handleProjectileDamager(projectile, e);
            return;
        }

        // Protect against mobs being hurt by explosions
        final EntityDamageEvent.DamageCause cause = e.getCause();
        if (cause == EntityDamageEvent.DamageCause.BLOCK_EXPLOSION
            || cause == EntityDamageEvent.DamageCause.ENTITY_EXPLOSION
               && !(e.getEntity() instanceof Monster)) {
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
            this.handlePlayerDamager(damager.get(), e);
            return;
        }
        if (e.getCombuster() instanceof Projectile projectile) {
            this.handleProjectileDamager(projectile, e);
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

    private <E extends EntityEvent & Cancellable> void handlePlayerDamager(@NotNull Player damager, @NotNull E e) {
        final Optional<Player> damaged = getPlayerSource(e.getEntity());
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
                getPlayerDamageType(e.getEntity()),
                getPosition(e.getEntity().getLocation())
        ))) {
            e.setCancelled(true);
        }
    }

    private <E extends EntityEvent & Cancellable> void handleProjectileDamager(@NotNull Projectile proj, @NotNull E e) {
        // Prevent projectiles dispensed outside of claims from harming stuff in claims
        if (proj.getShooter() instanceof BlockProjectileSource shooter) {
            final OperationPosition blockLocation = getPosition(shooter.getBlock().getLocation());
            if (getHandler().cancelNature(
                    blockLocation.getWorld(),
                    blockLocation,
                    getPosition(e.getEntity().getLocation())
            )) {
                e.setCancelled(true);
            }
            return;
        }

        // Prevent projectiles shot by mobs from harming passive mobs, hanging entities & armor stands
        if (!(e.getEntity() instanceof Player || e.getEntity() instanceof Monster)
            && proj.getShooter() instanceof Monster) {
            if (getHandler().cancelOperation(Operation.of(
                    OperationType.MONSTER_DAMAGE_TERRAIN,
                    getPosition(e.getEntity().getLocation())
            ))) {
                e.setCancelled(true);
            }
        }
    }

    @NotNull
    private OperationType getPlayerDamageType(@NotNull Entity entity) {
        OperationType type = OperationType.PLAYER_DAMAGE_ENTITY;
        if (entity instanceof Monster) {
            type = OperationType.PLAYER_DAMAGE_MONSTER;
        } else if (entity instanceof Vehicle vehicle) {
            type = vehicle instanceof InventoryHolder ? OperationType.BLOCK_BREAK : OperationType.BREAK_VEHICLE;
        } else if (entity instanceof LivingEntity living && !living.getRemoveWhenFarAway()
                   || entity.getCustomName() != null) {
            type = OperationType.PLAYER_DAMAGE_PERSISTENT_ENTITY;
        }
        return type;
    }

}
