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

import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.CampfireBlockEntity;
import net.minecraft.block.entity.SignBlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.registry.Registries;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.william278.cloplib.handler.TypeChecker;
import net.william278.cloplib.operation.Operation;
import net.william278.cloplib.operation.OperationType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.Optional;
import java.util.function.BiPredicate;

public interface FabricUseBlockListener extends FabricListener {

    // Map of use block predicates to operation types
    Map<BiPredicate<Block, TypeChecker>, OperationType> USE_BLOCK_PREDICATE_MAP = Map.of(
            (b, c) -> b instanceof CampfireBlock, OperationType.CONTAINER_OPEN,
            (b, c) -> b instanceof AbstractSignBlock, OperationType.BLOCK_PLACE,
            (b, c) -> c.isFarmMaterial(FabricListener.getId(b)), OperationType.FARM_BLOCK_INTERACT,
            (b, c) -> c.isPressureSensitiveMaterial(FabricListener.getId(b)) || b instanceof LeverBlock ||
                    b instanceof ButtonBlock || b instanceof RedstoneOreBlock, OperationType.REDSTONE_INTERACT
    );

    @NotNull
    Map<Block, OperationType> getPrecalculatedBlockMap();

    private Optional<OperationType> testBlockPredicate(@NotNull Block block) {
        return USE_BLOCK_PREDICATE_MAP.entrySet().stream()
                .filter(e -> e.getKey().test(block, getChecker()))
                .map(Map.Entry::getValue).findFirst();
    }

    default void precalculateBlocks(@NotNull Map<Block, OperationType> map) {
        Registries.BLOCK.forEach(i -> testBlockPredicate(i).ifPresent(type -> map.put(i, type)));
    }

    @NotNull
    default ActionResult onPlayerUseBlock(PlayerEntity playerEntity, World world, Hand hand, BlockHitResult blockHit) {
        if (blockHit.getType() != HitResult.Type.BLOCK || !(playerEntity instanceof ServerPlayerEntity player)
                || player.isSneaking()) {
            return ActionResult.PASS;
        }

        // Check clicked block is not air
        final BlockState blockState = world.getBlockState(blockHit.getBlockPos());
        if (blockState == null || blockState.isAir()) {
            return ActionResult.PASS;
        }

        // Check entity
        OperationType operationType = checkUseBlockEntity(player, world, world.getBlockEntity(blockHit.getBlockPos()));
        if (operationType != null && getHandler().cancelOperation(Operation.of(
                getUser(player),
                operationType,
                getPosition(blockHit.getBlockPos(), world),
                hand == Hand.OFF_HAND
        ))) {
            return ActionResult.FAIL;
        }

        // Check precalculated block operation map
        operationType = getPrecalculatedBlockMap().get(blockState.getBlock());
        if (getHandler().cancelOperation(Operation.of(
                getUser(player),
                operationType != null ? operationType : OperationType.BLOCK_INTERACT,
                getPosition(blockHit.getBlockPos(), world),
                hand == Hand.OFF_HAND
        ))) {
            return ActionResult.FAIL;
        }
        return ActionResult.PASS;
    }

    @Nullable
    private OperationType checkUseBlockEntity(@NotNull ServerPlayerEntity player, @NotNull World world,
                                              @Nullable BlockEntity blockEntity) {
        if (blockEntity == null) {
            return null;
        }
        if (blockEntity instanceof InventoryProvider || blockEntity instanceof Inventory ||
                blockEntity instanceof CampfireBlockEntity) {
            return OperationType.CONTAINER_OPEN;
        }
        if (blockEntity instanceof SignBlockEntity) {
            return OperationType.BLOCK_PLACE;
        }
        return null;
    }

    @NotNull
    default ActionResult onEntityPhysicallyInteract(World world, BlockPos blockPos, BlockState state, Entity entity) {
        // Check if the block was a pressure sensitive block
        if (!getChecker().isPressureSensitiveMaterial(FabricListener.getId(state.getBlock()))) {
            return ActionResult.PASS;
        }

        // Get the player source
        final Optional<ServerPlayerEntity> playerSource = getPlayerSource(entity);
        if (playerSource.isEmpty() || playerSource.get().isSpectator()) {
            return ActionResult.PASS;
        }

        // Check if this is allowed
        final ServerPlayerEntity player = playerSource.get();
        if (getHandler().cancelOperation(Operation.of(
                getUser(player),
                OperationType.REDSTONE_INTERACT,
                getPosition(blockPos, world),
                true
        ))) {
            return ActionResult.FAIL;
        }
        return ActionResult.PASS;
    }

    @NotNull
    default ActionResult onPlayerTakeLecternBook(World world, BlockPos blockPos, PlayerEntity playerEntity) {
        if (!(playerEntity instanceof ServerPlayerEntity player)) {
            return ActionResult.PASS;
        }

        // Check if this is allowed
        if (getHandler().cancelOperation(Operation.of(
                getUser(player),
                OperationType.CONTAINER_OPEN,
                getPosition(blockPos, world),
                true
        ))) {
            return ActionResult.FAIL;
        }
        return ActionResult.PASS;
    }

    @NotNull
    default ActionResult onProjectileHitBlock(BlockPos blockPos, World world, ProjectileEntity projectile,
                                              @Nullable Entity shooter) {
        return ActionResult.PASS;
    }

}
