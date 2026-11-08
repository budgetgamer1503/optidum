# Changelog

All notable changes to **Optidum** are documented in this file.

---

## [1.6.1] - 2026-11-08

### Performance & Memory Optimizations
- **Eliminated `System.gc()` Anti-Pattern**: Removed forced Stop-The-World full garbage collection sweeps on dimension change and high memory pressure. Replaced with non-blocking active cache eviction and adaptive throttling to prevent stutter spikes.
- **Active Memory & Cache Lifecycle Manager**: Added modular `MemoryTrimCallback` listener registry in `MemoryOptimizer`. Automatically purges stale player view data, border churn records, and dimension tracking upon dimension change (`DIMENSION_CHANGE`) or critical heap usage (`CRITICAL`).
- **Zero-Allocation Hot-Loop Reflection**: Overhauled `EntityTickOptimizer` to use `ClassValue` metadata caching. Resolves entity classification (hostile, animal, ambient) and reflection handles (`getTarget`, `hurtTime`, `getId`, `getX`, `getZ`) once per class. Completely eliminates `Class.getMethods()` array allocations and string parsing in the tick loop.
- **Adaptive Memory Pressure AI Throttling**: Automatically scales mob AI reduction factor and active distance when JVM memory pressure exceeds 85%, dropping heap allocation rates without degrading nearby entity behavior.
- **Configuration Options**: Added `adaptiveThrottlingOnMemoryPressure` (`true` by default) and `forceSystemGc` (`false` by default, preserving optional legacy behavior).

### Fixed
- Fixed unbounded reflection lookup leaks when checking mob target and hurt-time fields.
- Fixed player tracking map reset behavior in `ChunkOptimizer`.

---

## [1.6.0] - 2026-09-21

### Added
- **Multi-Version Subproject Expansion**: Added dedicated build modules and standalone distribution JARs for 8 target Minecraft versions:
  - `mc1165` &rarr; **Minecraft 1.16.5** (`Optidum-mc1.16.5-1.6.jar`)
  - `mc1171` &rarr; **Minecraft 1.17.1** (`Optidum-mc1.17.1-1.6.jar`)
  - `mc1182` &rarr; **Minecraft 1.18.2** (`Optidum-mc1.18.2-1.6.jar`)
  - `mc1194` &rarr; **Minecraft 1.19.4** (`Optidum-mc1.19.4-1.6.jar`)
  - `mc1201` &rarr; **Minecraft 1.20.1** (`Optidum-mc1.20.1-1.6.jar`)
  - `mc1211` &rarr; **Minecraft 1.21.1** (`Optidum-mc1.21.1-1.6.jar`)
  - `mc262` &rarr; **Minecraft 26.2** (`Optidum-mc26.2-1.6.jar`)
  - `mc263` &rarr; **Minecraft 26.3** (`Optidum-mc26.3-1.6.jar`)
  - `common` &rarr; **Shared Core Library** (`Optidum-common-1.6.jar`)
- **Centralized Artifact Directory (`dist/`)**:
  - Added automated Gradle task `collectJars` hooked into `gradlew build` that copies all compiled distribution JARs into a single `dist/` directory.
- **Companion Compatibility Layer (`CompatibilityManager`)**:
  - Added runtime detection and coordination for popular performance mods:
    - **Titanium**: Full non-overlapping synergy (Titanium handles frustum & tile entity culling; Optidum handles AI, chunk view, and network optimization).
    - **Dynamic FPS**: Automatically yields unfocused framerate throttling to Dynamic FPS if installed.
    - **EntityCulling & More Culling**: Harmonizes entity and chunk state.
    - **Krypton & C2ME**: Synchronizes Netty flush consolidation and async chunk generation.
    - **FerriteCore & ModMenu**: Integrates cleanly into mod environments.
- **Automated Unit Test Suite (`:common:test`)**:
  - Added comprehensive JUnit 5 tests covering configuration clamping, JSON serialization, chunk packing, angle normalization, player proximity detection, entity AI throttling factors, and packet batching.

### Changed
- **Modular Multi-Project Architecture**: Restructured the project into a root umbrella build with a shared `:common` core and lightweight version modules.
- **Mandatory Sodium Dependency**: Moved Sodium to `"depends": { "sodium": ">=0.2.0" }` in `fabric.mod.json` across all version manifests.
- **Java Toolchain**: Set Java compile target to Java 17 (`release = 17`), enabling broad compatibility with standard JDK 17 environments while remaining fully forward-compatible.
- **Resilient Reflection Engine**: Abstracted entity, chunk, and network hooks using safe cached reflection with class hierarchy traversal.

---

## [1.5.0] - Prior Release
- Initial release for Minecraft 26.2 featuring AI pathfinding throttling, view direction chunk prioritization, Netty flush batching, and dynamic FPS power saver.
