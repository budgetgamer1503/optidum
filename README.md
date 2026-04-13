# Optidum

A comprehensive performance optimization mod for Minecraft, with enhanced Sodium compatibility.

## Overview

Optidum is a Fabric mod that significantly improves Minecraft's performance by optimizing entity ticks, chunk loading, network packets, memory usage, and render distance. It is designed to work seamlessly with Sodium and other performance‑focused mods.

## Features

- **Entity Tick Optimization** – Reduces entity update frequency based on distance, improving CPU usage.
- **Chunk Loading Optimization** – Smart chunk loading prioritizes player view direction and unloads distant chunks.
- **Network Optimization** – Compresses packets, aggregates small packets, and caches repetitive data.
- **Memory Optimization** – Implements entity pooling and garbage collection tuning.
- **Sodium Integration** – Enhanced compatibility with Sodium's rendering pipeline and chunk management.

## Installation

1. Install [Fabric Loader](https://fabricmc.net/use/) for Minecraft 26.1.1 or later.
2. Download the latest Optidum `.jar` from [CurseForge](https://www.curseforge.com/minecraft/mc-mods/optidum) or [Modrinth](https://modrinth.com/mod/optidum).
3. Place the `.jar` file in your `mods` folder.
4. Launch Minecraft and enjoy improved performance!

## Compatibility

- **Minecraft**: 26.1.1+
- **Fabric Loader**: ≥0.18.6
- **Fabric API**: Required
- **Sodium**: Recommended (≥0.6.0) but not required
- **Cloth Config**: Suggested for GUI configuration (≥13.0.121)

 ## License

This mod is licensed under CC0‑1.0 (public domain). See the [LICENSE](LICENSE) file for details.
