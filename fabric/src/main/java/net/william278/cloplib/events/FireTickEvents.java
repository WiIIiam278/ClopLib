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

package net.william278.cloplib.events;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;

public final class FireTickEvents {

    @NotNull
    public static final Event<BeforeSpreadCallback> BEFORE_SPREAD = EventFactory.createArrayBacked(
            BeforeSpreadCallback.class,
            (callbacks) -> (world, pos) -> {
                for (BeforeSpreadCallback listener : callbacks) {
                    final ActionResult result = listener.fireSpread(world, pos);
                    if (result != ActionResult.PASS) {
                        return result;
                    }
                }

                return ActionResult.PASS;
            }
    );

    @NotNull
    public static final Event<BeforeBurnCallback> BEFORE_BURN = EventFactory.createArrayBacked(
            BeforeBurnCallback.class,
            (callbacks) -> (world, pos) -> {
                for (BeforeBurnCallback listener : callbacks) {
                    final ActionResult result = listener.fireBurn(world, pos);
                    if (result != ActionResult.PASS) {
                        return result;
                    }
                }

                return ActionResult.PASS;
            }
    );

    @FunctionalInterface
    public interface BeforeSpreadCallback {

        @NotNull
        ActionResult fireSpread(World world, BlockPos pos);

    }

    @FunctionalInterface
    public interface BeforeBurnCallback {

        @NotNull
        ActionResult fireBurn(World world, BlockPos pos);

    }

}
