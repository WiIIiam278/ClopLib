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
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.screen.LecternScreenHandler;
import net.minecraft.screen.PropertyDelegate;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.william278.cloplib.events.LecternEvents;
import org.jetbrains.annotations.NotNull;

public class TrackedLecternScreenHandler extends LecternScreenHandler {

    private final World lecternWorld;
    private final BlockPos lecternPos;

    TrackedLecternScreenHandler(int syncId, @NotNull Inventory inventory, @NotNull PropertyDelegate propertyDelegate,
                                @NotNull BlockEntity blockEntity) {
        super(syncId, inventory, propertyDelegate);
        this.lecternWorld = blockEntity.getWorld();
        this.lecternPos = blockEntity.getPos();
    }

    @Override
    public boolean onButtonClick(PlayerEntity player, int id) {
        if (id == TAKE_BOOK_BUTTON_ID) {
            final ActionResult result = LecternEvents.BEFORE_BOOK_TAKEN.invoker().bookTaken(lecternWorld, lecternPos, player);
            if (result == ActionResult.FAIL) {
                return false;
            }
        }
        return super.onButtonClick(player, id);
    }

}
