package net.william278.cloplib.listener;

import net.minecraft.entity.Entity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.Vec3d;
import net.william278.cloplib.handler.Handler;
import net.william278.cloplib.handler.TypeChecker;
import net.william278.cloplib.operation.OperationPosition;
import net.william278.cloplib.operation.OperationUser;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public interface FabricListener {

    @NotNull
    OperationPosition getPosition(@NotNull Vec3d pos, float yaw, float pitch, @NotNull net.minecraft.world.World world);

    @NotNull
    OperationUser getUser(@NotNull ServerPlayerEntity player);

    @NotNull
    Handler getHandler();

    @NotNull
    TypeChecker getChecker();

    default Optional<ServerPlayerEntity> getPlayerSource(@Nullable Entity e) {
        if (e == null) {
            return Optional.empty();
        }
        if (e instanceof ServerPlayerEntity player) {
            return Optional.of(player);
        }
        if (e instanceof ProjectileEntity projectile && projectile.getOwner() instanceof ServerPlayerEntity player) {
            return Optional.of(player);
        }
        return e.getPassengerList().stream()
                .filter(p -> p instanceof ServerPlayerEntity)
                .map(p -> (ServerPlayerEntity) p)
                .findFirst();
    }
}
