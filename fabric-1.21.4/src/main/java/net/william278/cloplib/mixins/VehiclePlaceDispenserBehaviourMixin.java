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

package net.william278.cloplib.mixins;

import net.minecraft.block.DispenserBlock;
import net.minecraft.block.dispenser.BoatDispenserBehavior;
import net.minecraft.block.dispenser.DispenserBehavior;
import net.minecraft.block.dispenser.ItemDispenserBehavior;
import net.minecraft.block.dispenser.MinecartDispenserBehavior;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPointer;
import net.minecraft.util.math.Direction;
import net.william278.cloplib.events.DispenserEvents;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = {BoatDispenserBehavior.class, MinecartDispenserBehavior.class})
public abstract class VehiclePlaceDispenserBehaviourMixin {

    @Unique
    private static final DispenserBehavior CLOPLIB_VEHICLE_FALLBACK_BEHAVIOUR = new ItemDispenserBehavior();

    @Inject(method = "dispenseSilently", at = @At("HEAD"), cancellable = true)
    private void dispenseSilentlyMixin(BlockPointer pointer, ItemStack stack, CallbackInfoReturnable<ItemStack> cir) {
        final Direction direction = pointer.state().get(DispenserBlock.FACING);
        final ActionResult result = DispenserEvents.BEFORE_PLACE.invoker().place(
                pointer.world(), pointer.pos(), pointer.pos().offset(direction)
        );
        if (result == ActionResult.FAIL) {
            cir.setReturnValue(CLOPLIB_VEHICLE_FALLBACK_BEHAVIOUR.dispense(pointer, stack));
            cir.cancel();
        }
    }

}
