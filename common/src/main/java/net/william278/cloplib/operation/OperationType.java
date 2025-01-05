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
import net.kyori.adventure.key.Key;
import net.kyori.adventure.key.KeyPattern;
import org.intellij.lang.annotations.Subst;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;

import java.util.*;

import static net.kyori.adventure.key.Key.DEFAULT_SEPARATOR;

/**
 * Types of operations triggered by certain events
 * <p>
 * Some operations are silent by default, meaning that they will not notify a player when they are canceled.
 */
public final class OperationType {

    // Namespace for built in operation types
    private static final @KeyPattern.Namespace String DEFAULT_NAMESPACE = "cloplib";

    // The operation type registry map
    private static final Map<String, OperationType> REGISTRY = new HashMap<>();

    /**
     * When a player places a block
     *
     * @since 1.0
     */
    public static final OperationType BLOCK_PLACE = registerBuiltin("block_place");

    /**
     * When a player breaks a block
     *
     * @since 1.0
     */
    public static final OperationType BLOCK_BREAK = registerBuiltin("block_break");

    /**
     * When a player interacts with a block
     *
     * @since 1.0
     */
    public static final OperationType BLOCK_INTERACT = registerBuiltin("block_interact");

    /**
     * When a player interacts with redstone
     *
     * @since 1.0
     */
    public static final OperationType REDSTONE_INTERACT = registerBuiltin("redstone_interact", true);

    /**
     * When a player breaks a farm block
     *
     * @since 1.0
     */
    public static final OperationType FARM_BLOCK_BREAK = registerBuiltin("farm_block_break");

    /**
     * When a player places a farm block
     *
     * @since 1.0
     */
    public static final OperationType FARM_BLOCK_PLACE = registerBuiltin("farm_block_place");

    /**
     * When a player damages another player
     *
     * @since 1.0
     */
    public static final OperationType PLAYER_DAMAGE_PLAYER = registerBuiltin("player_damage_player");

    /**
     * When a player damages a hostile monster
     *
     * @since 1.0
     */
    public static final OperationType PLAYER_DAMAGE_MONSTER = registerBuiltin("player_damage_monster");

    /**
     * When a player damages a (non-hostile) mob
     *
     * @since 1.0
     */
    public static final OperationType PLAYER_DAMAGE_ENTITY = registerBuiltin("player_damage_entity");

    /**
     * When a player damages a mob that has been name-tagged or marked as persistent
     *
     * @since 1.0
     */
    public static final OperationType PLAYER_DAMAGE_PERSISTENT_ENTITY = registerBuiltin("player_damage_persistent_entity");

    /**
     * When a hostile mob spawns
     *
     * @since 1.0
     */
    public static final OperationType MONSTER_SPAWN = registerBuiltin("monster_spawn", true);

    /**
     * When a passive (non-hostile) mob spawns
     *
     * @since 1.0.2
     */
    public static final OperationType PASSIVE_MOB_SPAWN = registerBuiltin("passive_mob_spawn", true);

    /**
     * When a mob damages terrain (e.g. = register(); an Enderman picking up a block)
     *
     * @since 1.0
     */
    public static final OperationType MONSTER_DAMAGE_TERRAIN = registerBuiltin("monster_damage_terrain", true);

    /**
     * When an explosion damages terrain (breaks blocks)
     *
     * @since 1.0
     */
    public static final OperationType EXPLOSION_DAMAGE_TERRAIN = registerBuiltin("explosion_damage_terrain", true);

    /**
     * When an explosion causes an entity to take damage
     *
     * @since 1.0
     */
    public static final OperationType EXPLOSION_DAMAGE_ENTITY = registerBuiltin("explosion_damage_entity", true);

    /**
     * When fire destroys an ignited block
     *
     * @since 1.0
     */
    public static final OperationType FIRE_BURN = registerBuiltin("fire_burn", true);

    /**
     * When fire spreads from an ignited block to another block.
     * <p>
     * Note {@link #BLOCK_PLACE} is fired for when fire is "placed" by a player/dispenser
     *
     * @since 1.0
     */
    public static final OperationType FIRE_SPREAD = registerBuiltin("fire_spread", true);

    /**
     * When a player fills a bucket
     *
     * @since 1.0
     */
    public static final OperationType FILL_BUCKET = registerBuiltin("fill_bucket");

    /**
     * When a player empties a bucket
     *
     * @since 1.0
     */
    public static final OperationType EMPTY_BUCKET = registerBuiltin("empty_bucket");

    /**
     * When a player places a hanging entity (e.g. = register(); a Painting)
     *
     * @since 1.0
     */
    public static final OperationType PLACE_HANGING_ENTITY = registerBuiltin("place_hanging_entity");

    /**
     * When a player breaks a hanging entity (e.g. = register(); a Painting)
     *
     * @since 1.0
     */
    public static final OperationType BREAK_HANGING_ENTITY = registerBuiltin("break_hanging_entity");

    /**
     * When a player places a vehicle (e.g. Minecarts or Boats).
     * <p>
     * Note {@link #BLOCK_PLACE} is fired for when a vehicle that is also a container is placed (e.g. Boat with Chest).
     * This event is not fired for living entities such as horses or pigs.
     *
     * @since 1.0.14
     */
    public static final OperationType PLACE_VEHICLE = registerBuiltin("place_vehicle");

    /**
     * When a player breaks a vehicle (e.g. Minecarts or Boats)
     * <p>
     * Note {@link #BLOCK_BREAK} is fired for when a vehicle that is also a container is broken (e.g. Boat with Chest).
     * This event is not fired for living entities such as horses or pigs.
     *
     * @since 1.0.14
     */
    public static final OperationType BREAK_VEHICLE = registerBuiltin("break_vehicle");

    /**
     * When a player interacts with an entity in some way
     *
     * @since 1.0
     */
    public static final OperationType ENTITY_INTERACT = registerBuiltin("entity_interact");

    /**
     * When a player interacts with a farm block
     * in some way (e.g. = register(); Right-clicking crops with Bonemeal)
     *
     * @since 1.0
     */
    public static final OperationType FARM_BLOCK_INTERACT = registerBuiltin("farm_block_interact");

    /**
     * When a player uses a Spawn Egg = register(); or throws a Chicken Egg to try and hatch a chicken.
     *
     * @since 1.0
     */
    public static final OperationType USE_SPAWN_EGG = registerBuiltin("use_spawn_egg");

    /**
     * When a player teleports using an Ender Pearl or Chorus Fruit
     *
     * @since 1.0
     */
    public static final OperationType ENDER_PEARL_TELEPORT = registerBuiltin("ender_pearl_teleport");

    /**
     * When a player opens a container (e.g., Chests, Hoppers, Furnaces, etc.)
     *
     * @since 1.0
     */
    public static final OperationType CONTAINER_OPEN = registerBuiltin("container_open");

    /**
     * When a player starts a raid (i.e., by having the bad omen effect and entering a village)
     *
     * @since 1.0.9
     */
    public static final OperationType START_RAID = registerBuiltin("start_raid");

    @Getter
    private final boolean silent;
    @Getter
    private final Key key;
    private final int ordinal;

    private OperationType(@NotNull Key key, boolean silent) {
        this.silent = silent;
        this.key = key;
        this.ordinal = REGISTRY.size();
    }

    /**
     * Register an operation type
     *
     * @param type the type to register
     * @return the registered operation type
     * @since 1.1
     */
    @NotNull
    public static OperationType register(@NotNull OperationType type) {
        assert REGISTRY != null : "Registry was null";
        if (isRegistered(type.getKey())) {
            throw new IllegalArgumentException("Operation type already registered: %s".formatted(type.getKey()));
        }
        REGISTRY.put(type.getKey().asString(), type);
        return type;
    }

    /**
     * Unregister an operation type
     *
     * @param key the type to unregister
     * @return the unregistered operation type
     * @since 1.1
     */
    @NotNull
    public static OperationType unregister(@NotNull Key key) {
        assert REGISTRY != null : "Registry was null";
        if (!isRegistered(key)) {
            throw new IllegalArgumentException("Operation type not registered: %s".formatted(key));
        }
        return REGISTRY.remove(key.asString());
    }

    /**
     * Create an operation type
     *
     * @param key    key to create
     * @param silent whether the type should be silent. The silent flag indicates whether players should be notified
     *               when an operation of that type was cancelled by default.
     * @return the registered operation type
     * @since 1.1
     */
    @NotNull
    public static OperationType create(@NotNull Key key, boolean silent) {
        return new OperationType(key, silent);
    }

    /**
     * Get an operation type from the given key
     *
     * @param key The key of the operation type
     * @return The operation type, or an empty optional if not found
     * @since 1.1
     */
    public static Optional<OperationType> get(@NotNull Key key) {
        return Optional.ofNullable(REGISTRY.get(key.asString()));
    }

    /**
     * Get an operation type from the given key
     *
     * @param key The key of the operation type
     * @return The operation type, or an empty optional if not found
     * @since 1.1
     */
    public static Optional<OperationType> get(@NotNull String key) {
        return Optional.ofNullable(REGISTRY.get(!key.contains(DEFAULT_SEPARATOR + "")
                ? "%s%s%s".formatted(DEFAULT_NAMESPACE, DEFAULT_SEPARATOR, key) : key));
    }

    /**
     * Get whether an Operation Type has been registered
     *
     * @param key The key of the operation type
     * @return {@code true}  if the type was registered
     * @since 1.1
     */
    public static boolean isRegistered(@NotNull Key key) {
        return REGISTRY.containsKey(key.asString());
    }

    /**
     * Get whether an Operation Type has been registered
     *
     * @param key The key of the operation type
     * @return {@code true}  if the type was registered
     * @since 1.1
     */
    public static boolean isRegistered(@NotNull String key) {
        return REGISTRY.containsKey(!key.contains(DEFAULT_SEPARATOR + "")
                ? "%s%s%s".formatted(DEFAULT_NAMESPACE, DEFAULT_SEPARATOR, key) : key);
    }

    /**
     * Get the set of registered {@link OperationType}s
     *
     * @return the set of {@link OperationType}s
     * @since 1.1
     */
    @NotNull
    @Unmodifiable
    public static Collection<OperationType> getRegistered() {
        return REGISTRY.values();
    }

    /**
     * Get an array of registered OperationTypes
     *
     * @return the OperationType value array
     * @since 1.0
     * @deprecated use {@link #getRegistered()}
     */
    @NotNull
    @Unmodifiable
    @Deprecated(since = "1.1")
    public static OperationType[] values() {
        return REGISTRY.values().toArray(new OperationType[0]);
    }

    /**
     * Get an operation type from the given name
     *
     * @param id The name of the operation type
     * @return The operation type, or an empty optional if not found
     * @since 1.0
     * @deprecated use {@link #get(Key)} instead
     */
    @SuppressWarnings("unused")
    @Deprecated(since = "1.1")
    public static Optional<OperationType> fromId(@NotNull String id) {
        return get(id);
    }

    // Get or create a key
    @NotNull
    @ApiStatus.Internal
    static OperationType getOrCreate(@Nullable String key) {
        assert key != null : "Operation Type key is null";
        @Subst("ignored") final String keyString = !key.contains(DEFAULT_SEPARATOR + "")
                ? "%s%s%s".formatted(DEFAULT_NAMESPACE, DEFAULT_SEPARATOR, key) : key;
        if (!Key.parseable(keyString)) {
            throw new IllegalArgumentException("Invalid operation type key: %s".formatted(key));
        }
        return REGISTRY.getOrDefault(keyString, create(Key.key(keyString), false));
    }

    // Register a built-in operation type
    @NotNull
    private static OperationType registerBuiltin(@NotNull @KeyPattern.Value String name, boolean silent) {
        return register(create(Key.key(DEFAULT_NAMESPACE, name), silent));
    }

    // Register a built-in operation type (defaulting to non-silent)
    @NotNull
    private static OperationType registerBuiltin(@NotNull @KeyPattern.Value String name) {
        return registerBuiltin(name, false);
    }

    /**
     * Get the key value string of this OperationType.
     *
     * @return the key value string
     * @since 1.0
     * @deprecated get the {@link Key} instead
     */
    @NotNull
    @Deprecated(since = "1.1")
    public String name() {
        return key.value().toUpperCase(Locale.ENGLISH);
    }

    /**
     * Get the registration ordinal of this OperationType
     *
     * @return The ordinal (index order of when this operation type was registered)
     * @since 1.0
     * @deprecated unused since 1.1
     */
    @Deprecated(since = "1.1")
    public int ordinal() {
        return ordinal;
    }

    /**
     * Get the string representation of an OperationType (the key)
     *
     * @return the operation type key
     * @since 1.1
     */
    @Override
    @NotNull
    public String toString() {
        return getKey().asString();
    }

}
