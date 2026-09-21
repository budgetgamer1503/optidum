# Optidum v1.6

**Optidum** is a high-performance companion optimization mod for Minecraft Fabric designed to synergize cleanly with **Sodium** and **Titanium**.

---

## What It Does

- **Entity AI Optimization**: Throttles expensive AI goal and pathfinding evaluation for distant mobs, animals, and ambient creatures without affecting gravity, velocity, item despawning, or combat physics.
- **Smart Chunk Prioritization**: Prioritizes chunk loading and transmission in the direction the player is looking, and prevents chunk load/unload churn along chunk borders.
- **Netty Packet Flush Consolidation**: Consolidates small, rapid Netty network packets into batched flushes, cutting socket syscall overhead and reducing ping jitter.
- **Low-Overhead Memory Management**: Monitors JVM heap pressure on a 30-second interval, freeing transient caches and clearing level caches on dimension changes to prevent GC stutter and memory leaks.
- **Dynamic FPS & Inactive Power Saver**: Automatically throttles framerate to a configurable cap (5–60 FPS) when the window is unfocused (Alt-Tabbed) or minimized, cutting GPU power draw, heat, and fan noise. Seamlessly yields to the `dynamic_fps` mod if installed.

---

## Non-Overlapping Architecture (Synergy with Titanium)

Optidum is designed to be fully compatible alongside [Titanium](https://curseforge.com/minecraft/mc-mods/titanium-sodium):

| Optimization Area | Handled By |
| --- | --- |
| **Frustum & Distance Entity Culling** | Titanium |
| **Tile Entity Culling (Chests, Hoppers, Signs)** | Titanium |
| **Particle Culling** | Titanium |
| **Dynamic Frame Pacing** | Titanium |
| **Entity AI Pathfinding Throttling** | **Optidum** |
| **Smart Chunk View Prioritization** | **Optidum** |
| **Netty Packet Batching & Flush Consolidation** | **Optidum** |
| **Dynamic FPS & Inactive Power Saver** | **Optidum** |
| **Dimension Cache Cleanup** | **Optidum** |

---

## Supported Minecraft Versions & Generated Artifacts

Optidum v1.6 provides dedicated build modules and independent distribution JARs for each supported Minecraft generation:

| Subproject | Minecraft Target | Fabric Range | Output Distribution JAR |
| --- | --- | --- | --- |
| **`:mc1165`** | `1.16.5` | `>=1.16.0 <=1.16.5` | `mc1165/build/libs/Optidum-mc1.16.5-1.6.jar` |
| **`:mc1171`** | `1.17.1` | `>=1.17.0 <=1.17.1` | `mc1171/build/libs/Optidum-mc1.17.1-1.6.jar` |
| **`:mc1182`** | `1.18.2` | `>=1.18.0 <=1.18.2` | `mc1182/build/libs/Optidum-mc1.18.2-1.6.jar` |
| **`:mc1194`** | `1.19.4` | `>=1.19.0 <=1.19.4` | `mc1194/build/libs/Optidum-mc1.19.4-1.6.jar` |
| **`:mc1201`** | `1.20.1` | `>=1.20.0 <=1.20.1` | `mc1201/build/libs/Optidum-mc1.20.1-1.6.jar` |
| **`:mc1211`** | `1.21.1` | `>=1.21.0 <=1.21.1` | `mc1211/build/libs/Optidum-mc1.21.1-1.6.jar` |
| **`:mc262`** | `26.2` | `>=26.2 <=26.2.99` | `mc262/build/libs/Optidum-mc26.2-1.6.jar` |
| **`:mc263`** | `26.3` | `>=26.3 <=26.3.99` | `mc263/build/libs/Optidum-mc26.3-1.6.jar` |
| **`:common`** | Shared Core | Core Library | `common/build/libs/Optidum-common-1.6.jar` |

---

## Requirements

- **Fabric Loader**: `>=0.14.0` (or newer)
- **Fabric API**
- **Sodium**: `>=0.2.0` (Required dependency across all versions)
- **Java**: `17` or newer

---

## Configuration

Optidum stores its configuration at:

```text
config/optidum.json
```

All settings can be adjusted in-game directly through **Options > Video Settings > Optidum**

---

## Building from Source

To compile all 8 version JARs and run the shared test suite:

```bash
./gradlew build
```

---

## License

Optidum is licensed under the GNU Lesser General Public License v3.0 (LGPL-3.0). See `LICENSE` for details.
