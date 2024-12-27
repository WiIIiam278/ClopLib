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

package net.william278.cloplib.listener.mixins;

import net.minecraft.block.BlockState;
import net.minecraft.fluid.FlowableFluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import net.william278.cloplib.listener.events.FluidFlowsHorizontally;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Set;

@Mixin(FlowableFluid.class)
public abstract class FlowableFluidMixin {

    // Ignore up/down fluid flow (claims occupy the world height)
    @Unique
    private static final Set<Direction> CLOPLIB_IGNORED_DIRECTIONS = Set.of(Direction.UP, Direction.DOWN);

    @Inject(method = "canFlowThrough(Lnet/minecraft/world/BlockView;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/BlockState;Lnet/minecraft/util/math/Direction;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/BlockState;Lnet/minecraft/fluid/FluidState;)Z", at = @At("HEAD"), cancellable = true)
    private void canFlowThroughMixin(BlockView blockView, BlockPos toPos, BlockState toState, Direction flowDirection,
                                     BlockPos fromPos, BlockState fromState,
                                     FluidState fluidState, CallbackInfoReturnable<Boolean> cir) {
        if (!(blockView instanceof ServerWorld world) || CLOPLIB_IGNORED_DIRECTIONS.contains(flowDirection)) {
            return;
        }

        final ActionResult result = FluidFlowsHorizontally.EVENT.invoker().flow(world, fromPos, toPos);
        if (result == ActionResult.FAIL) {
            cir.setReturnValue(false);
        }
    }

}
