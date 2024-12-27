package net.william278.cloplib.listener.mixins;

import com.google.common.collect.Streams;
import net.minecraft.block.piston.PistonHandler;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.william278.cloplib.listener.events.PistonTryActuate;
import org.jetbrains.annotations.Unmodifiable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(PistonHandler.class)
public abstract class PistonHandlerMixin {

    @Final
    @Shadow
    private World world;
    @Final
    @Shadow
    private BlockPos posFrom; // Piston location
    @Final
    @Shadow
    private List<BlockPos> movedBlocks;
    @Final
    @Shadow
    private List<BlockPos> brokenBlocks;

    // When one block is destroyed.
    @Inject(method = "calculatePush", at = @At(value = "RETURN", ordinal = 0), cancellable = true)
    private void calculatePushSingleDestroyMixin(CallbackInfoReturnable<Boolean> cir) {
        final ActionResult result = PistonTryActuate.EVENT.invoker().actuate(world, posFrom, getChangedBlocks());
        if (result == ActionResult.FAIL) {
            cir.setReturnValue(false);
            cir.cancel();
        }
    }

    // When one or more blocks are moved or destroyed
    @Inject(method = "calculatePush", at = @At(value = "RETURN", ordinal = 4), cancellable = true)
    private void calculatePushOneOrMoreMoveMixin(CallbackInfoReturnable<Boolean> cir) {
        final ActionResult result = PistonTryActuate.EVENT.invoker().actuate(world, posFrom, getChangedBlocks());
        if (result == ActionResult.FAIL) {
            cir.setReturnValue(false);
            cir.cancel();
        }
    }

    @Unique
    @Unmodifiable
    private List<BlockPos> getChangedBlocks() {
        return Streams.concat(movedBlocks.stream(), brokenBlocks.stream()).toList();
    }

}
