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
import net.minecraft.entity.decoration.ArmorStandEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.vehicle.VehicleEntity;
import net.minecraft.entity.vehicle.VehicleInventory;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.world.World;
import net.william278.cloplib.operation.Operation;
import net.william278.cloplib.operation.OperationType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface FabricUseEntityListener extends FabricListener {

    @NotNull
    default ActionResult onPlayerUseEntity(PlayerEntity playerEntity, World world, Hand hand, Entity entity,
                                           @Nullable EntityHitResult entityHitResult) {
        if (entity instanceof PlayerEntity) {
            return ActionResult.PASS;
        }

        // Check against interacting with container vehicles
        if (((entity instanceof VehicleEntity && entity instanceof VehicleInventory) ||
                (entity instanceof ArmorStandEntity)) && getHandler().cancelOperation(Operation.of(
                getUser(playerEntity),
                OperationType.CONTAINER_OPEN,
                getPosition(entity.getPos(), world, entity.getYaw(), entity.getPitch()),
                hand == Hand.OFF_HAND
        ))) {
            return ActionResult.FAIL;
        }

        if (getHandler().cancelOperation(Operation.of(
                getUser(playerEntity),
                OperationType.ENTITY_INTERACT,
                getPosition(entity.getPos(), world, entity.getYaw(), entity.getPitch()),
                hand == Hand.OFF_HAND
        ))) {
            return ActionResult.FAIL;
        }
        return ActionResult.PASS;
    }

}