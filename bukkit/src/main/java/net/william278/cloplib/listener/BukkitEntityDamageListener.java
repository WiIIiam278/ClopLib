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
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.projectiles.BlockProjectileSource;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public interface BukkitEntityDamageListener extends BukkitListener {

    @EventHandler(ignoreCancelled = true)
    default void onEntityDamageEntity(@NotNull EntityDamageByEntityEvent e) {
        final Optional<Player> damaged = getPlayerSource(e.getEntity());
        final Optional<Player> damaging = getPlayerSource(e.getDamager());
        if (damaging.isPresent()) {
            if (damaged.isPresent()) {
                if (getHandler().cancelOperation(Operation.of(
                        getUser(damaging.get()),
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
                    getUser(damaging.get()),
                    getPlayerDamageType(e),
                    getPosition(e.getEntity().getLocation())
            ))) {
                e.setCancelled(true);
            }
            return;
        }

        if (e.getDamager() instanceof Projectile projectile) {
            // Prevent projectiles dispensed outside of claims from harming stuff in claims
            if (projectile.getShooter() instanceof BlockProjectileSource shooter) {
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
                    && projectile.getShooter() instanceof Monster) {
                if (getHandler().cancelOperation(Operation.of(
                        OperationType.MONSTER_DAMAGE_TERRAIN,
                        getPosition(e.getEntity().getLocation())
                ))) {
                    e.setCancelled(true);
                }
            }
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

    @NotNull
    private OperationType getPlayerDamageType(@NotNull EntityDamageByEntityEvent e) {
        OperationType type = OperationType.PLAYER_DAMAGE_ENTITY;
        if (e.getEntity() instanceof Monster) {
            type = OperationType.PLAYER_DAMAGE_MONSTER;
        } else if (e.getEntity() instanceof LivingEntity living && !living.getRemoveWhenFarAway()
                || e.getEntity().getCustomName() != null) {
            type = OperationType.PLAYER_DAMAGE_PERSISTENT_ENTITY;
        }
        return type;
    }

}
