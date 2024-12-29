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

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.decoration.AbstractDecorationEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.entity.vehicle.VehicleEntity;
import net.minecraft.entity.vehicle.VehicleInventory;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.world.World;
import net.william278.cloplib.operation.Operation;
import net.william278.cloplib.operation.OperationPosition;
import net.william278.cloplib.operation.OperationType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public interface FabricEntityDamageListener extends FabricListener {

    @NotNull
    default ActionResult onPlayerAttackEntity(PlayerEntity playerEntity, World world, Hand hand, Entity entity,
                                              @Nullable EntityHitResult ignoredEntityHitResult) {
        return handlePlayerDamageEntity(playerEntity, entity);
    }

    @NotNull
    default ActionResult onProjectileHitEntity(Entity hit, ProjectileEntity projectile, @Nullable Entity shooter) {
        final Optional<ServerPlayerEntity> playerShooter = getPlayerSource(shooter);
        if (playerShooter.isPresent()) {
            return handlePlayerDamageEntity(playerShooter.get(), hit);
        }
        return ActionResult.PASS; //todo dispenser
    }

    @NotNull
    private ActionResult handlePlayerDamageEntity(PlayerEntity attackerEntity, Entity damaged) {
        if (!(attackerEntity instanceof ServerPlayerEntity attacker) || attacker.isSpectator()) {
            return ActionResult.PASS;
        }

        // Handle PvP between two players
        final OperationPosition damagedPos = getPosition(
                damaged.getPos(), damaged.getWorld(),
                damaged.getYaw(), damaged.getPitch()
        );
        if (damaged instanceof ServerPlayerEntity playerVictim) {
            return getHandler().cancelOperation(Operation.of(
                    getUser(attacker),
                    getUser(playerVictim),
                    OperationType.PLAYER_DAMAGE_PLAYER,
                    damagedPos
            )) ? ActionResult.FAIL : ActionResult.PASS;
        }

        // Handle based on hit entity type
        return getHandler().cancelOperation(Operation.of(
                getUser(attacker),
                getPlayerDamageType(damaged),
                damagedPos
        )) ? ActionResult.FAIL : ActionResult.PASS;
    }

    @NotNull
    private OperationType getPlayerDamageType(@NotNull Entity entity) {
        OperationType type = OperationType.PLAYER_DAMAGE_ENTITY;
        if (isMonster(entity)) {
            type = OperationType.PLAYER_DAMAGE_MONSTER;
        } else if (entity instanceof VehicleEntity vehicle) {
            type = vehicle instanceof VehicleInventory ? OperationType.BLOCK_BREAK : OperationType.BREAK_VEHICLE;
        } else if (entity instanceof AbstractDecorationEntity) {
            type = OperationType.BREAK_HANGING_ENTITY;
        } else if (!(entity instanceof LivingEntity)) {
            type = OperationType.BLOCK_BREAK;
        } else if (entity.hasCustomName()) {
            type = OperationType.PLAYER_DAMAGE_PERSISTENT_ENTITY;
        }
        return type;
    }
}
