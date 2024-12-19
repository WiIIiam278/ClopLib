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

package net.william278.cloplib.operation;

import lombok.Getter;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Optional;

/**
 * Types of operations triggered by certain events
 * <p>
 * Some operations are silent by default, meaning that they will not notify a player when they are canceled.
 */
@Getter
public enum OperationType {

    /**
     * When a player places a block
     *
     * @since 1.0
     */
    BLOCK_PLACE,

    /**
     * When a player breaks a block
     *
     * @since 1.0
     */
    BLOCK_BREAK,

    /**
     * When a player interacts with a block
     *
     * @since 1.0
     */
    BLOCK_INTERACT,

    /**
     * When a player interacts with redstone
     *
     * @since 1.0
     */
    REDSTONE_INTERACT(true),

    /**
     * When a player breaks a farm block
     *
     * @since 1.0
     */
    FARM_BLOCK_BREAK,

    /**
     * When a player places a farm block
     *
     * @since 1.0
     */
    FARM_BLOCK_PLACE,

    /**
     * When a player damages another player
     *
     * @since 1.0
     */
    PLAYER_DAMAGE_PLAYER,

    /**
     * When a player damages a hostile monster
     *
     * @since 1.0
     */
    PLAYER_DAMAGE_MONSTER,

    /**
     * When a player damages a (non-hostile) mob
     *
     * @since 1.0
     */
    PLAYER_DAMAGE_ENTITY,

    /**
     * When a player damages a mob that has been name-tagged or marked as persistent
     *
     * @since 1.0
     */
    PLAYER_DAMAGE_PERSISTENT_ENTITY,

    /**
     * When a hostile mob spawns
     *
     * @since 1.0
     */
    MONSTER_SPAWN(true),

    /**
     * When a passive (non-hostile) mob spawns
     *
     * @since 1.0.2
     */
    PASSIVE_MOB_SPAWN(true),

    /**
     * When a mob damages terrain (e.g., an Enderman picking up a block)
     *
     * @since 1.0
     */
    MONSTER_DAMAGE_TERRAIN(true),

    /**
     * When an explosion damages terrain (breaks blocks)
     *
     * @since 1.0
     */
    EXPLOSION_DAMAGE_TERRAIN(true),

    /**
     * When an explosion causes an entity to take damage
     *
     * @since 1.0
     */
    EXPLOSION_DAMAGE_ENTITY(true),

    /**
     * When fire destroys an ignited block
     *
     * @since 1.0
     */
    FIRE_BURN(true),

    /**
     * When fire spreads from an ignited block to another block.
     * <p>
     * Note {@link #BLOCK_PLACE} is fired for when fire is "placed" by a player/dispenser
     *
     * @since 1.0
     */
    FIRE_SPREAD(true),

    /**
     * When a player fills a bucket
     *
     * @since 1.0
     */
    FILL_BUCKET,

    /**
     * When a player empties a bucket
     *
     * @since 1.0
     */
    EMPTY_BUCKET,

    /**
     * When a player places a hanging entity (e.g., a Painting)
     *
     * @since 1.0
     */
    PLACE_HANGING_ENTITY,

    /**
     * When a player breaks a hanging entity (e.g., a Painting)
     *
     * @since 1.0
     */
    BREAK_HANGING_ENTITY,

    /**
     * When a player places a vehicle (e.g. Minecarts or Boats).
     * <p>
     * Note {@link #BLOCK_PLACE} is fired for when a vehicle that is also a container is placed (e.g. Boat with Chest).
     * This event is not fired for living entities such as horses or pigs.
     *
     * @since 1.0.14
     */
    PLACE_VEHICLE,

    /**
     * When a player breaks a vehicle (e.g. Minecarts or Boats)
     * <p>
     * Note {@link #BLOCK_BREAK} is fired for when a vehicle that is also a container is broken (e.g. Boat with Chest).
     * This event is not fired for living entities such as horses or pigs.
     *
     * @since 1.0.14
     */
    BREAK_VEHICLE,

    /**
     * When a player interacts with an entity in some way
     *
     * @since 1.0
     */
    ENTITY_INTERACT,

    /**
     * When a player interacts with a farm block
     * in some way (e.g., Right-clicking crops with Bonemeal)
     *
     * @since 1.0
     */
    FARM_BLOCK_INTERACT,

    /**
     * When a player uses a Spawn Egg, or throws a Chicken Egg to try and hatch a chicken.
     *
     * @since 1.0
     */
    USE_SPAWN_EGG,

    /**
     * When a player teleports using an Ender Pearl or Chorus Fruit
     *
     * @since 1.0
     */
    ENDER_PEARL_TELEPORT,

    /**
     * When a player opens a container (e.g., Chests, Hoppers, Furnaces, etc.)
     *
     * @since 1.0
     */
    CONTAINER_OPEN,

    /**
     * When a player starts a raid (i.e., by having the bad omen effect and entering a village)
     *
     * @since 1.0.9
     */
    START_RAID(true);

    /**
     * Indicates whether by default this operation should not notify a player when it is canceled
     *
     * @since 1.0
     */
    private final boolean silent;

    OperationType(final boolean silent) {
        this.silent = silent;
    }

    OperationType() {
        this.silent = false;
    }

    /**
     * Get an operation type from the given name
     *
     * @param id The name of the operation type
     * @return The operation type, or an empty optional if not found
     * @since 1.0
     */
    @SuppressWarnings("unused")
    public static Optional<OperationType> fromId(@NotNull String id) {
        return Arrays.stream(values()).filter(flag -> flag.name().equalsIgnoreCase(id)).findFirst();
    }

}
