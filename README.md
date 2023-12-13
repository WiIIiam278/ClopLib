<!--suppress ALL -->
<p align="center">
    <img src="images/banner.png" alt="Claim Operations Library" />
    <a href="https://github.com/WiIIiam278/ClopLib/actions/workflows/ci.yml">
        <img src="https://img.shields.io/github/actions/workflow/status/WiIIiam278/ClopLib/ci.yml?branch=master&logo=github"/>
    </a> 
    <a href="https://repo.william278.net/#/releases/net/william278/cloplib/">
        <img src="https://repo.william278.net/api/badge/latest/releases/net/william278/cloplib?color=00fb9a&name=Maven&prefix=v"/>
    </a> 
    <a href="https://discord.gg/tVYhJfyDWG">
        <img src="https://img.shields.io/discord/818135932103557162.svg?label=&logo=discord&logoColor=fff&color=7389D8&labelColor=6A7EC2" />
    </a> 
</p>
<br/>

**ClopLib** (_**C**laim **Op**erations **Lib**rary_) is a Minecraft server library for handling events that take place in varying positions within game worlds, to allow developers to contextually cancel or modify the outcome of operations such as block placement, block breaking, entity spawning, based on whether the type of operation was performed within a claimed region.

Currently, ClopLib targets `bukkit` servers running **Spigot/Paper 1.16.5+**. ClopLib requires **Java 17**.

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

Then, add the dependency itself. Replace `VERSION` with the latest release version. (e.g., `1.0`) and `PLATFORM` with the platform you are targeting (e.g., `bukkit`). If you want to target pre-release "snapshot" versions (not recommended), you should use the `/snapshots` repository instead.

```groovy
dependencies {
    implementation "net.william278:cloplib-PLATFORM:VERSION"
}
```
</details>

Using Maven/something else? There's instructions on how to include ClopLib on [the repo browser](https://repo.william278.net/#/releases/net/william278/cloplib).

## Building
To build ClopLib, run `clean build` in the root directory. The output JARs will be in `target/`.

## License
ClopLib is licensed under Apache-2.0. See [LICENSE](https://github.com/WiIIiam278/ClopLib/raw/master/LICENSE) for more information.