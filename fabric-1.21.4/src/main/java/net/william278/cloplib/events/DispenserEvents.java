package net.william278.cloplib.events;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;

public final class DispenserEvents {

    @NotNull
    public static final Event<BeforePlaceCallback> BEFORE_PLACE = EventFactory.createArrayBacked(
            BeforePlaceCallback.class,
            (callbacks) -> (world, dispenser, placedPos) -> {
                for (BeforePlaceCallback listener : callbacks) {
                    final ActionResult result = listener.place(world, dispenser, placedPos);
                    if (result != ActionResult.PASS) {
                        return result;
                    }
                }

                return ActionResult.PASS;
            }
    );

    @FunctionalInterface
    public interface BeforePlaceCallback {

        @NotNull
        ActionResult place(World world, BlockPos dispenser, BlockPos placedPos);

    }

}
