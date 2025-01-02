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

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.mob.Angerable;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.william278.cloplib.handler.Handler;
import net.william278.cloplib.handler.TypeChecker;
import net.william278.cloplib.operation.OperationPosition;
import net.william278.cloplib.operation.OperationUser;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.function.BiConsumer;

public interface FabricListener extends InspectorCallbackProvider {

    @NotNull
    OperationPosition getPosition(@NotNull Vec3d pos, @NotNull net.minecraft.world.World world, float yaw, float pitch);

    @NotNull
    default OperationPosition getPosition(@NotNull BlockPos pos, @NotNull net.minecraft.world.World world) {
        return this.getPosition(pos.toCenterPos(), world, 0.0f, 0.0f);
    }

    @NotNull
    OperationUser getUser(@NotNull PlayerEntity player);

    @NotNull
    Handler getHandler();

    @NotNull
    TypeChecker getChecker();

    // Handle claim inspection callbacks
    @NotNull
    default ActionResult handleInspectionCallbacks(ServerPlayerEntity player, World world, ItemStack item) {
        final InspectionTool tool = getTool(item);
        if (!getInspectionToolHandlers().containsKey(tool)) {
            return ActionResult.PASS;
        }

        // Execute the callback
        final BiConsumer<OperationUser, OperationPosition> callback = getInspectionToolHandlers().get(tool);
        final HitResult hit = player.raycast(getInspectionDistance(), 0.0f, false);
        if (hit.getType() == HitResult.Type.BLOCK) {
            callback.accept(getUser(player), getPosition(((BlockHitResult) hit).getBlockPos(), world));
        }
        return ActionResult.FAIL;
    }

    @NotNull
    private InspectorCallbackProvider.InspectionTool getTool(@NotNull ItemStack item) {
        final InspectorCallbackProvider.InspectionTool.InspectionToolBuilder builder = InspectorCallbackProvider
                .InspectionTool.builder()
                .material(FabricListener.getId(item.getItem()));
        //todo Custom Model Data feature in fabric (NBT check?)
//        if (item.getDefaultComponents().mo && item.getItemMeta() != null && item.getItemMeta().hasCustomModelData()) {
//            builder.useCustomModelData(true).customModelData(item.getItemMeta().getCustomModelData());
//        }
        return builder.build();
    }

    default Optional<ServerPlayerEntity> getPlayerSource(@Nullable Entity e) {
        if (e == null) {
            return Optional.empty();
        }
        if (e instanceof ServerPlayerEntity player) {
            return Optional.of(player);
        }
        if (e instanceof ProjectileEntity projectile && projectile.getOwner() instanceof ServerPlayerEntity player) {
            return Optional.of(player);
        }
        return e.getPassengerList().stream()
                .filter(p -> p instanceof ServerPlayerEntity)
                .map(p -> (ServerPlayerEntity) p)
                .findFirst();
    }

    default boolean isMonster(@Nullable Entity entity) {
        return entity instanceof HostileEntity || entity instanceof Angerable;
    }

    @NotNull
    static String getId(@NotNull Item item) {
        return Registries.ITEM.getId(item).toString();
    }

    @NotNull
    static String getId(@NotNull Block block) {
        return Registries.BLOCK.getId(block).toString();
    }

}
