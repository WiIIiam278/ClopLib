package net.william278.cloplib.listener;

import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.william278.cloplib.operation.Operation;
import net.william278.cloplib.operation.OperationType;
import org.jetbrains.annotations.NotNull;

public interface FabricFireListener extends FabricListener {

    @NotNull
    default ActionResult onFireSpread(@NotNull World world, @NotNull BlockPos pos) {
        if (getHandler().cancelOperation(Operation.of(
                OperationType.FIRE_SPREAD,
                getPosition(pos, world)
        ))) {
            return ActionResult.FAIL;
        }
        return ActionResult.PASS;
    }

    @NotNull
    default ActionResult onBlockBurn(@NotNull World world, @NotNull BlockPos pos) {
        if (getHandler().cancelOperation(Operation.of(
                OperationType.FIRE_BURN,
                getPosition(pos, world)
        ))) {
            return ActionResult.FAIL;
        }
        return ActionResult.PASS;
    }

}
