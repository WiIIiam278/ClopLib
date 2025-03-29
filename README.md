<!--suppress ALL -->
<p align="center">
    <img src="images/banner.png" alt="Claim Operations Library" />
    <a href="https://github.com/WiIIiam278/ClopLib/actions/workflows/ci.yml">
        <img src="https://img.shields.io/github/actions/workflow/status/WiIIiam278/ClopLib/ci.yml?branch=master&logo=github"/>
    </a> 
    <a href="https://repo.william278.net/#/releases/net/william278/cloplib/">
        <img src="https://repo.william278.net/api/badge/latest/releases/net/william278/cloplib/cloplib-common?color=00fb9a&name=Maven&prefix=v"/>
    </a> 
    <a href="https://discord.gg/tVYhJfyDWG">
        <img src="https://img.shields.io/discord/818135932103557162.svg?label=&logo=discord&logoColor=fff&color=7389D8&labelColor=6A7EC2" />
    </a> 
</p>
<br/>

**ClopLib** (_**Cl**aim **Op**erations **Lib**rary_) is a Minecraft server library for handling events that take place in varying positions within game worlds, to allow developers to contextually cancel or modify the outcome of operations such as block placement, block breaking, entity spawning, based on whether the type of operation was performed within a claimed region.

Currently, ClopLib targets `bukkit` servers running **Spigot/Paper 1.17.1+** (requires Java 17) and **Fabric 1.21.1/1.21.4/1.21.5** (requires Java 21).

## Setup
ClopLib is available [on Maven](https://repo.william278.net/#/releases/net/william278/cloplib/). You can browse the Javadocs [here](https://repo.william278.net/javadoc/releases/net/william278/cloplib/latest).

<details>
<summary>Gradle setup instructions</summary> 

First, add the Maven repository to your `build.gradle` file:
```groovy
repositories {
    maven { url "https://repo.william278.net/releases" }
}
```

Then, add the dependency itself. Replace `VERSION` with the latest release version. (e.g., `1.1`) and `PLATFORM` with the platform you are targeting (e.g., `bukkit`, `fabric`). If you want to target pre-release "snapshot" versions (not recommended), you should use the `/snapshots` repository instead.

On Fabric, you'll need to specify the targeted Minecraft version in the format `VERSION+MC_VERSION` (e.g. `net.william278.cloplib:cloplib-fabric:1.1+1.21.4`)

```groovy
dependencies {
    implementation "net.william278.cloplib:cloplib-PLATFORM:VERSION"
}
```
</details>

Using Maven/something else? There's instructions on how to include ClopLib on [the repo browser](https://repo.william278.net/#/releases/net/william278/cloplib).

## Using
* ✅ This library IS intended for people developing land protection or plugins & mods that want an easy way of contextually handling when the world or players do stuff.
* ❌ This library IS NOT intended for people wanting to develop hooks for HuskClaims or HuskTowns. Please see the API reference for [HuskClaims](https://william278.net/docs/huskclaims/api) and [HuskTowns](https://william278.net/docs/husktowns/api) respectively. You do not need to include this library or add it to your `/plugins` folder; ClopLib is shaded & relocated in the final jar for each plugin.

Documentation on using ClopLib in your projects is a WIP! At it's core, though, ClopLib exposes a:
* The `Handler` interface, which you should implement in your plugin  
* The `OperationUser` interface, which you should implement via your object representing online players
* The `OperationPosition/World/Chunk` interfaces, which you should implement via your object(s) representing game world positions 
* The `OperationListener` interface, and platform-specific interfaces such as `BukkitOperationListener`, the latter of which you should extend to register ClopLib's handlers and supply your implementing `OperationUser/Position/World/Chunk` objects. 

A good place to start would be looking at how HuskClaims' code implements ClopLib in its [common module](https://github.com/WiIIiam278/HuskClaims/blob/master/common/src/main/java/net/william278/huskclaims/claim/ClaimHandler.java), and then implements the [platform operation handler on bukkit](https://github.com/WiIIiam278/HuskClaims/blob/master/bukkit/src/main/java/net/william278/huskclaims/listener/BukkitListener.java).

## Building
To build ClopLib, run `clean build` in the root directory. The output JARs will be in `target/`.

## License
ClopLib is licensed under Apache-2.0. See [LICENSE](https://github.com/WiIIiam278/ClopLib/raw/master/LICENSE) for more information.
