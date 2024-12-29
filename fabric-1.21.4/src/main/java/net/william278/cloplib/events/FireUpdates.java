package net.william278.cloplib.events;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;

public final class FireUpdates {

    @NotNull
    public static final Event<BeforeFireSpreads> BEFORE_FIRE_SPREAD = EventFactory.createArrayBacked(
            BeforeFireSpreads.class,
            (callbacks) -> (world, pos) -> {
                for (BeforeFireSpreads listener : callbacks) {
                    final ActionResult result = listener.spreads(world, pos);
                    if (result != ActionResult.PASS) {
                        return result;
                    }
                }

                return ActionResult.PASS;
            }
    );

    @NotNull
    public static final Event<BeforeFireBurns> BEFORE_FIRE_BURNS = EventFactory.createArrayBacked(
            BeforeFireBurns.class,
            (callbacks) -> (world, pos) -> {
                for (BeforeFireBurns listener : callbacks) {
                    final ActionResult result = listener.burns(world, pos);
                    if (result != ActionResult.PASS) {
                        return result;
                    }
                }

                return ActionResult.PASS;
            }
    );

    @FunctionalInterface
    public interface BeforeFireSpreads {

        @NotNull
        ActionResult spreads(World world, BlockPos pos);

    }

    @FunctionalInterface
    public interface BeforeFireBurns {

        @NotNull
        ActionResult burns(World world, BlockPos pos);

    }

}
