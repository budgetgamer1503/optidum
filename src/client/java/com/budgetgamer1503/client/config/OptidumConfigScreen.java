package com.budgetgamer1503.client.config;

import com.budgetgamer1503.config.ConfigManager;
import com.budgetgamer1503.config.OptidumConfig;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.CycleButton;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.network.chat.Component;

@SuppressWarnings("unused")
public class OptidumConfigScreen extends Screen {
    private final Screen parent;
    private final OptidumConfig config;
    
    private EditBox entityTickDistanceField;
    private EditBox entityTickReductionField;
    private EditBox chunkLoadDistanceField;
    private EditBox targetFPSField;
    private EditBox minRenderDistanceField;
    private EditBox maxRenderDistanceField;
    private EditBox packetCompressionField;
    private EditBox entityPoolSizeField;
    
    private static final int BUTTON_WIDTH = 150;
    private static final int BUTTON_HEIGHT = 20;
    private static final int FIELD_WIDTH = 80;
    private static final int FIELD_HEIGHT = 20;
    
    public OptidumConfigScreen(Screen parent) {
        super(Component.literal("Optidum Configuration"));
        this.parent = parent;
        this.config = ConfigManager.getConfig();
    }
    
    @Override
    protected void init() {
        int leftCol = this.width / 2 - 160;
        int rightCol = this.width / 2 + 10;
        int y = 40;
        int spacing = 25;
        
        // === Entity Tick Optimization ===
        addRenderableWidget(Button.builder(
            Component.literal("§lEntity Tick Optimization"), 
            btn -> {}).bounds(leftCol, y, 310, BUTTON_HEIGHT).build());
        y += spacing;
        
        addRenderableWidget(CycleButton.onOffBuilder(config.entityTickOptimization)
            .create(leftCol, y, BUTTON_WIDTH, BUTTON_HEIGHT, 
                Component.literal("Enabled"), 
                (btn, val) -> config.entityTickOptimization = val));
        
        entityTickDistanceField = new EditBox(this.font, rightCol, y, FIELD_WIDTH, FIELD_HEIGHT,
            Component.literal("Tick Distance"));
        entityTickDistanceField.setValue(String.valueOf(config.entityTickDistance));
        addRenderableWidget(entityTickDistanceField);
        y += spacing;
        
        addRenderableWidget(CycleButton.onOffBuilder(config.optimizeHostileEntities)
            .create(leftCol, y, BUTTON_WIDTH, BUTTON_HEIGHT,
                Component.literal("Optimize Hostile"),
                (btn, val) -> config.optimizeHostileEntities = val));
        
        addRenderableWidget(CycleButton.onOffBuilder(config.optimizePassiveEntities)
            .create(rightCol, y, BUTTON_WIDTH, BUTTON_HEIGHT,
                Component.literal("Optimize Passive"),
                (btn, val) -> config.optimizePassiveEntities = val));
        y += spacing;
        
        entityTickReductionField = new EditBox(this.font, leftCol, y, FIELD_WIDTH, FIELD_HEIGHT,
            Component.literal("Reduction Factor"));
        entityTickReductionField.setValue(String.valueOf(config.entityTickReductionFactor));
        addRenderableWidget(entityTickReductionField);
        y += spacing + 10;
        
        // === Chunk Loading Optimization ===
        addRenderableWidget(Button.builder(
            Component.literal("§lChunk Loading Optimization"),
            btn -> {}).bounds(leftCol, y, 310, BUTTON_HEIGHT).build());
        y += spacing;
        
        addRenderableWidget(CycleButton.onOffBuilder(config.chunkLoadingOptimization)
            .create(leftCol, y, BUTTON_WIDTH, BUTTON_HEIGHT,
                Component.literal("Enabled"),
                (btn, val) -> config.chunkLoadingOptimization = val));
        
        chunkLoadDistanceField = new EditBox(this.font, rightCol, y, FIELD_WIDTH, FIELD_HEIGHT,
            Component.literal("Load Distance"));
        chunkLoadDistanceField.setValue(String.valueOf(config.smartChunkLoadDistance));
        addRenderableWidget(chunkLoadDistanceField);
        y += spacing;
        
        addRenderableWidget(CycleButton.onOffBuilder(config.prioritizeViewDirection)
            .create(leftCol, y, BUTTON_WIDTH, BUTTON_HEIGHT,
                Component.literal("View Direction"),
                (btn, val) -> config.prioritizeViewDirection = val));
        
        addRenderableWidget(CycleButton.onOffBuilder(config.unloadDistantChunks)
            .create(rightCol, y, BUTTON_WIDTH, BUTTON_HEIGHT,
                Component.literal("Unload Distant"),
                (btn, val) -> config.unloadDistantChunks = val));
        y += spacing + 10;
        
        // === Network Optimization ===
        addRenderableWidget(Button.builder(
            Component.literal("§lNetwork Optimization"),
            btn -> {}).bounds(leftCol, y, 310, BUTTON_HEIGHT).build());
        y += spacing;
        
        addRenderableWidget(CycleButton.onOffBuilder(config.networkOptimization)
            .create(leftCol, y, BUTTON_WIDTH, BUTTON_HEIGHT,
                Component.literal("Enabled"),
                (btn, val) -> config.networkOptimization = val));
        
        packetCompressionField = new EditBox(this.font, rightCol, y, FIELD_WIDTH, FIELD_HEIGHT,
            Component.literal("Compression"));
        packetCompressionField.setValue(String.valueOf(config.packetCompressionThreshold));
        addRenderableWidget(packetCompressionField);
        y += spacing;
        
        addRenderableWidget(CycleButton.onOffBuilder(config.aggregateSmallPackets)
            .create(leftCol, y, BUTTON_WIDTH, BUTTON_HEIGHT,
                Component.literal("Aggregate"),
                (btn, val) -> config.aggregateSmallPackets = val));
        
        addRenderableWidget(CycleButton.onOffBuilder(config.enablePacketCaching)
            .create(rightCol, y, BUTTON_WIDTH, BUTTON_HEIGHT,
                Component.literal("Cache Packets"),
                (btn, val) -> config.enablePacketCaching = val));
        y += spacing + 10;
        
        // === Memory Optimization ===
        addRenderableWidget(Button.builder(
            Component.literal("§lMemory Optimization"),
            btn -> {}).bounds(leftCol, y, 310, BUTTON_HEIGHT).build());
        y += spacing;
        
        addRenderableWidget(CycleButton.onOffBuilder(config.memoryOptimization)
            .create(leftCol, y, BUTTON_WIDTH, BUTTON_HEIGHT,
                Component.literal("Enabled"),
                (btn, val) -> config.memoryOptimization = val));
        
        entityPoolSizeField = new EditBox(this.font, rightCol, y, FIELD_WIDTH, FIELD_HEIGHT,
            Component.literal("Pool Size"));
        entityPoolSizeField.setValue(String.valueOf(config.entityPoolSize));
        addRenderableWidget(entityPoolSizeField);
        y += spacing;
        
        addRenderableWidget(CycleButton.onOffBuilder(config.entityPooling)
            .create(leftCol, y, BUTTON_WIDTH, BUTTON_HEIGHT,
                Component.literal("Entity Pooling"),
                (btn, val) -> config.entityPooling = val));
        
        addRenderableWidget(CycleButton.onOffBuilder(config.enableGarbageCollectionOptimization)
            .create(rightCol, y, BUTTON_WIDTH, BUTTON_HEIGHT,
                Component.literal("GC Optimization"),
                (btn, val) -> config.enableGarbageCollectionOptimization = val));
        y += spacing + 10;
        
        // === Render Distance Optimization ===
        addRenderableWidget(Button.builder(
            Component.literal("§lRender Distance Optimization"),
            btn -> {}).bounds(leftCol, y, 310, BUTTON_HEIGHT).build());
        y += spacing;
        
        addRenderableWidget(CycleButton.onOffBuilder(config.renderDistanceOptimization)
            .create(leftCol, y, BUTTON_WIDTH, BUTTON_HEIGHT,
                Component.literal("Enabled"),
                (btn, val) -> config.renderDistanceOptimization = val));
        
        targetFPSField = new EditBox(this.font, rightCol, y, FIELD_WIDTH, FIELD_HEIGHT,
            Component.literal("Target FPS"));
        targetFPSField.setValue(String.valueOf(config.targetFPS));
        addRenderableWidget(targetFPSField);
        y += spacing;
        
        minRenderDistanceField = new EditBox(this.font, leftCol, y, FIELD_WIDTH, FIELD_HEIGHT,
            Component.literal("Min RD"));
        minRenderDistanceField.setValue(String.valueOf(config.minRenderDistance));
        addRenderableWidget(minRenderDistanceField);
        
        maxRenderDistanceField = new EditBox(this.font, rightCol, y, FIELD_WIDTH, FIELD_HEIGHT,
            Component.literal("Max RD"));
        maxRenderDistanceField.setValue(String.valueOf(config.maxRenderDistance));
        addRenderableWidget(maxRenderDistanceField);
        y += spacing + 10;
        
        // === Sodium Integration ===
        addRenderableWidget(Button.builder(
            Component.literal("§lSodium Integration"),
            btn -> {}).bounds(leftCol, y, 310, BUTTON_HEIGHT).build());
        y += spacing;
        
        addRenderableWidget(CycleButton.onOffBuilder(config.sodiumIntegration)
            .create(leftCol, y, BUTTON_WIDTH, BUTTON_HEIGHT,
                Component.literal("Enabled"),
                (btn, val) -> config.sodiumIntegration = val));
        
        addRenderableWidget(CycleButton.onOffBuilder(config.adjustForSodiumRendering)
            .create(rightCol, y, BUTTON_WIDTH, BUTTON_HEIGHT,
                Component.literal("Adjust Rendering"),
                (btn, val) -> config.adjustForSodiumRendering = val));
        y += spacing + 10;
        
        // === Debug & Logging ===
        addRenderableWidget(Button.builder(
            Component.literal("§lDebug & Logging"),
            btn -> {}).bounds(leftCol, y, 310, BUTTON_HEIGHT).build());
        y += spacing;
        
        addRenderableWidget(CycleButton.onOffBuilder(config.enableDebugLogging)
            .create(leftCol, y, BUTTON_WIDTH, BUTTON_HEIGHT,
                Component.literal("Debug Logging"),
                (btn, val) -> config.enableDebugLogging = val));
        
        addRenderableWidget(CycleButton.onOffBuilder(config.logPerformanceMetrics)
            .create(rightCol, y, BUTTON_WIDTH, BUTTON_HEIGHT,
                Component.literal("Performance Log"),
                (btn, val) -> config.logPerformanceMetrics = val));
        
        // === Save / Cancel / Done ===
        addRenderableWidget(Button.builder(
            Component.literal("§aSave & Close"),
            btn -> {
                saveConfig();
                this.minecraft.setScreen(parent);
            }).bounds(this.width / 2 - 155, this.height - 30, 100, BUTTON_HEIGHT).build());
        
        addRenderableWidget(Button.builder(
            Component.literal("§cCancel"),
            btn -> this.minecraft.setScreen(parent))
            .bounds(this.width / 2 - 50, this.height - 30, 100, BUTTON_HEIGHT).build());
        
        addRenderableWidget(Button.builder(
            Component.literal("§eDone"),
            btn -> this.minecraft.setScreen(parent))
            .bounds(this.width / 2 + 55, this.height - 30, 100, BUTTON_HEIGHT).build());
    }
    
    private void saveConfig() {
        config.entityTickDistance = parseInt(entityTickDistanceField, config.entityTickDistance);
        config.entityTickReductionFactor = parseInt(entityTickReductionField, config.entityTickReductionFactor);
        config.smartChunkLoadDistance = parseInt(chunkLoadDistanceField, config.smartChunkLoadDistance);
        config.targetFPS = parseInt(targetFPSField, config.targetFPS);
        config.minRenderDistance = parseInt(minRenderDistanceField, config.minRenderDistance);
        config.maxRenderDistance = parseInt(maxRenderDistanceField, config.maxRenderDistance);
        config.packetCompressionThreshold = parseInt(packetCompressionField, config.packetCompressionThreshold);
        config.entityPoolSize = parseInt(entityPoolSizeField, config.entityPoolSize);
        
        config.validate();
        ConfigManager.saveConfig();
        ConfigManager.reloadConfig();
    }
    
    private int parseInt(EditBox field, int fallback) {
        try {
            return Integer.parseInt(field.getValue());
        } catch (NumberFormatException ignored) {
            return fallback;
        }
    }
    
    @Override
    public void onClose() {
        this.minecraft.setScreen(parent);
    }
}