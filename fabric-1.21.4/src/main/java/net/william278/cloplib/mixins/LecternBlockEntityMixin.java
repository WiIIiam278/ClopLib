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

import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.LecternBlockEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.screen.PropertyDelegate;
import net.minecraft.screen.ScreenHandler;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(LecternBlockEntity.class)
public abstract class LecternBlockEntityMixin {

    @Shadow
    @Final
    private Inventory inventory;

    @Shadow
    @Final
    private PropertyDelegate propertyDelegate;

    @Redirect(method = "createMenu", at = @At(value = "INVOKE_ASSIGN", target = "Lnet/minecraft/screen/LecternScreenHandler;<init>(ILnet/minecraft/inventory/Inventory;Lnet/minecraft/screen/PropertyDelegate;)V"))
    private ScreenHandler createMenuMixin(int i, Inventory inventory, PropertyDelegate propertyDelegate) {
        return new TrackedLecternScreenHandler(i, this.inventory, this.propertyDelegate, ((BlockEntity) (Object) this));
    }

}
