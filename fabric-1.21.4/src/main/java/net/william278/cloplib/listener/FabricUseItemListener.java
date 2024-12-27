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

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.*;
import net.minecraft.registry.Registries;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.World;
import net.william278.cloplib.handler.TypeChecker;
import net.william278.cloplib.operation.Operation;
import net.william278.cloplib.operation.OperationPosition;
import net.william278.cloplib.operation.OperationType;
import net.william278.cloplib.operation.OperationUser;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.BiPredicate;

public interface FabricUseItemListener extends FabricListener {

    // Map of use item predicates to operation types
    Map<BiPredicate<Item, TypeChecker>, OperationType> USE_ITEM_PREDICATE_MAP = Map.of(
            (i, c) -> c.isFarmMaterial(Registries.ITEM.getId(i).toString()), OperationType.FARM_BLOCK_PLACE,
            (i, c) -> i instanceof BlockItem && !c.isFarmMaterial(FabricListener.getId(i)), OperationType.BLOCK_PLACE,
            (i, c) -> i instanceof BucketItem b && b == Items.BUCKET, OperationType.FILL_BUCKET,
            (i, c) -> i instanceof BucketItem b && b != Items.BUCKET, OperationType.EMPTY_BUCKET,
            (i, c) -> i instanceof EnderPearlItem || i == Items.CHORUS_FRUIT, OperationType.ENDER_PEARL_TELEPORT,
            (i, c) -> i instanceof BoatItem || i instanceof MinecartItem, OperationType.PLACE_VEHICLE,
            (i, c) -> i instanceof DecorationItem, OperationType.PLACE_HANGING_ENTITY,
            (i, c) -> i instanceof SpawnEggItem || i instanceof EggItem, OperationType.USE_SPAWN_EGG
    );

    @NotNull
    Map<Item, OperationType> getPrecalculatedItemMap();

    private Optional<OperationType> testItemPredicate(@NotNull Item item) {
        return USE_ITEM_PREDICATE_MAP.entrySet().stream()
                .filter(e -> e.getKey().test(item, getChecker()))
                .map(Map.Entry::getValue).findFirst();
    }

    default void precalculateItems(@NotNull Map<Item, OperationType> map) {
        Registries.ITEM.forEach(i -> testItemPredicate(i).ifPresent(type -> map.put(i, type)));
    }

    @NotNull
    default ActionResult onPlayerUseItem(PlayerEntity playerEntity, World world, Hand hand) {
        final ItemStack item = playerEntity.getStackInHand(hand);
        if (item == null || item == ItemStack.EMPTY || !(playerEntity instanceof ServerPlayerEntity player)) {
            return ActionResult.PASS;
        }

        // Check inspection items
        if (hand == Hand.MAIN_HAND) {
            return this.handleInspectionCallbacks(player, world, item);
        }

        // Check precalculated item operation map
        final OperationType operationType = getPrecalculatedItemMap().get(item.getItem());
        if (operationType == null) {
            return ActionResult.PASS;
        }

        if (getHandler().cancelOperation(Operation.of(
                getUser(player),
                operationType,
                getUseItemPosition(player, world, hand, item),
                hand == Hand.OFF_HAND
        ))) {
            return ActionResult.FAIL;
        }
        return ActionResult.PASS;
    }

    @NotNull
    private OperationPosition getUseItemPosition(@NotNull ServerPlayerEntity player, @NotNull World world,
                                                 @NotNull Hand hand, @NotNull ItemStack item) {
        // Raycast where the player could place a block
        final Vec3d start = player.getCameraPosVec(0.0f);
        final Vec3d rot = player.getRotationVec(0.0f);
        final Vec3d end = start.add(
                rot.x * player.getBlockInteractionRange(),
                rot.y * player.getBlockInteractionRange(),
                rot.z * player.getBlockInteractionRange()
        );

        // On a hit perform the item placement contextual check
        final BlockHitResult result = world.raycast(new RaycastContext(start, end, RaycastContext.ShapeType.OUTLINE, RaycastContext.FluidHandling.SOURCE_ONLY, player));
        if (result.getType() == HitResult.Type.BLOCK) {
            getPosition(new ItemPlacementContext(player, hand, item, result).getBlockPos(), world);
        }
        return getPosition(player.getPos(), world, player.getYaw(), player.getPitch());
    }

    // Handle claim inspection callbacks
    @NotNull
    default ActionResult handleInspectionCallbacks(ServerPlayerEntity player, World world, ItemStack item) {
        final InspectorCallbackProvider.InspectionTool tool = getTool(item);
        if (!getInspectionToolHandlers().containsKey(tool)) {
            return ActionResult.PASS;
        }

        // Execute the callback
        final BiConsumer<OperationUser, OperationPosition> callback = getInspectionToolHandlers().get(tool);
        final HitResult hit = player.raycast(getInspectionDistance(), 0.0f, false);
        if (hit.getType() != HitResult.Type.BLOCK) {
            callback.accept(getUser(player), getPosition(hit.getPos(), world, 0.0f, 0.0f));
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

}
