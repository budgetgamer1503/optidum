# Optidum

Optidum is a client-side Fabric addon built around Sodium. It adds configurable Sodium-related settings, a Video Settings configuration screen, render-distance handling for large frame-time spikes, and several integrated-server helper systems.

## Version

Current version: 1.3

## What It Does

- Requires Sodium so Optidum can write Sodium-related configuration values.
- Applies an optional Sodium settings profile focused on lowering some rendering costs.
- Reduces selected visual settings such as clouds, particles, smooth lighting, biome blending, and entity distance scaling when performance mode is enabled.
- Detects large frame-time spikes and can temporarily reduce render distance after a cooldown.
- Keeps render-distance changes conservative to avoid frequent chunk reloads.
- Adds an Optidum config button inside Minecraft's Video Settings screen.

## Main Features

### Sodium Performance Profile

Optidum can write a Sodium config profile on startup. The profile changes settings related to:

- Deferred chunk updates
- Entity culling
- Fog occlusion
- Block face culling
- Compact vertex format
- Persistent mapping
- Chunk multidraw
- Lower CPU render-ahead pressure

### Frame-Time Spike Handling

Optidum monitors frame time. When a large spike is detected, it can lower render distance by one step after a cooldown instead of changing it continuously.

### Video Settings Menu

Open Minecraft's Video Settings screen and press the Optidum button to access the Optidum configuration menu.

### Integrated-Server Helpers

Optidum also includes helper systems for integrated-world gameplay:

- Entity tick reduction based on player distance
- Chunk tracking and cache helpers
- Network packet helper logic
- Memory usage and memory pressure monitoring

## Requirements

- Minecraft 26.1.1
- Fabric Loader 0.18.6 or newer
- Fabric API
- Sodium 0.8.0 or newer
- Java 25 or newer

## Installation

1. Install Fabric Loader for Minecraft 26.1.1.
2. Install Fabric API.
3. Install Sodium 0.8.0 or newer.
4. Place the Optidum jar in your `mods` folder.
5. Launch Minecraft and open Video Settings to configure Optidum.

## Configuration

Optidum stores its config at:

```text
config/optidum.json
```

Sodium performance options are written to Sodium's config file when Optidum starts. If you change settings in-game, restart Minecraft to ensure every Sodium-side option is fully reloaded.

## Recommended Use

For best results:

- Keep Sodium installed and updated.
- Use the Optidum button in Video Settings to tune target FPS and render-distance limits.
- Avoid setting max render distance too high if your world reloads chunks frequently.
- Leave frame-time spike render-distance handling enabled on lower-end systems.


## License

Optidum is licensed under CC-BY-4.0. See `LICENSE` for details.
