package com.budgetgamer1503.client.config;

import com.budgetgamer1503.config.ConfigManager;
import com.budgetgamer1503.config.OptidumConfig;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.CycleButton;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

@SuppressWarnings("unused")
public class OptidumConfigScreen extends Screen {
    private final Screen parent;
    private final OptidumConfig config;
    private final List<WidgetPosition> scrollableWidgets = new ArrayList<>();
    
    private EditBox entityTickDistanceField;
    private EditBox entityTickReductionField;
    private EditBox chunkLoadDistanceField;
    private EditBox targetFPSField;
    private EditBox minRenderDistanceField;
    private EditBox maxRenderDistanceField;
    private EditBox packetCompressionField;
    private EditBox entityPoolSizeField;
    private Button scrollUpButton;
    private Button scrollDownButton;
    
    private static final int BUTTON_WIDTH = 150;
    private static final int BUTTON_HEIGHT = 20;
    private static final int FIELD_WIDTH = 80;
    private static final int FIELD_HEIGHT = 20;
    private static final int CONTENT_TOP = 40;
    private static final int CONTENT_BOTTOM_PADDING = 58;
    private static final int SPACING = 25;
    private static final int SCROLL_STEP = 28;
    
    private int scrollOffset = 0;
    private int maxScroll = 0;
    
    private record WidgetPosition(AbstractWidget widget, int baseY, boolean interactive) {
    }
    
    public OptidumConfigScreen(Screen parent) {
        super(Component.literal("Optidum Configuration"));
        this.parent = parent;
        this.config = ConfigManager.getConfig();
    }
    
    @Override
    protected void init() {
        scrollableWidgets.clear();
        int leftCol = this.width / 2 - 160;
        int rightCol = this.width / 2 + 10;
        int y = CONTENT_TOP;
        
        y = addSection(leftCol, y, "Entity Tick Optimization", "Controls distance-based entity ticking to reduce CPU work.");
        addToggle(leftCol, y, "Enabled", config.entityTickOptimization,
            value -> config.entityTickOptimization = value,
            "Turns entity tick reduction on or off.");
        entityTickDistanceField = addNumberField(rightCol, y, "Tick Distance", config.entityTickDistance,
            "Entities farther than this many blocks can tick less often.");
        y += SPACING;
        addToggle(leftCol, y, "Optimize Hostile", config.optimizeHostileEntities,
            value -> config.optimizeHostileEntities = value,
            "Allows hostile mobs far from players to tick less often.");
        addToggle(rightCol, y, "Optimize Passive", config.optimizePassiveEntities,
            value -> config.optimizePassiveEntities = value,
            "Allows animals far from players to tick less often.");
        y += SPACING;
        entityTickReductionField = addNumberField(leftCol, y, "Reduction Factor", config.entityTickReductionFactor,
            "Higher values skip more far-away entity ticks. Recommended: 2 to 4.");
        y += SPACING + 10;
        
        y = addSection(leftCol, y, "Chunk Loading Optimization", "Tracks nearby player chunks to avoid unnecessary chunk churn.");
        addToggle(leftCol, y, "Enabled", config.chunkLoadingOptimization,
            value -> config.chunkLoadingOptimization = value,
            "Turns Optidum chunk tracking helpers on or off.");
        chunkLoadDistanceField = addNumberField(rightCol, y, "Load Distance", config.smartChunkLoadDistance,
            "Preferred chunk tracking distance around each player.");
        y += SPACING;
        addToggle(leftCol, y, "View Direction", config.prioritizeViewDirection,
            value -> config.prioritizeViewDirection = value,
            "Prioritizes chunks in the direction the player is looking.");
        addToggle(rightCol, y, "Unload Distant", config.unloadDistantChunks,
            value -> config.unloadDistantChunks = value,
            "Allows distant chunk cleanup helpers to run.");
        y += SPACING + 10;
        
        y = addSection(leftCol, y, "Network Optimization", "Controls packet helper settings for integrated and server play.");
        addToggle(leftCol, y, "Enabled", config.networkOptimization,
            value -> config.networkOptimization = value,
            "Turns packet helper logic on or off.");
        packetCompressionField = addNumberField(rightCol, y, "Compression", config.packetCompressionThreshold,
            "Packets above this size can be considered for compression.");
        y += SPACING;
        addToggle(leftCol, y, "Aggregate", config.aggregateSmallPackets,
            value -> config.aggregateSmallPackets = value,
            "Buffers small packets for batched processing.");
        addToggle(rightCol, y, "Cache Packets", config.enablePacketCaching,
            value -> config.enablePacketCaching = value,
            "Keeps repeated packet data available for reuse.");
        y += SPACING + 10;
        
        y = addSection(leftCol, y, "Memory Monitoring", "Monitors memory pressure and optional object pools.");
        addToggle(leftCol, y, "Enabled", config.memoryOptimization,
            value -> config.memoryOptimization = value,
            "Turns memory monitoring and optional object pools on or off.");
        entityPoolSizeField = addNumberField(rightCol, y, "Pool Size", config.entityPoolSize,
            "Maximum objects per internal object pool.");
        y += SPACING;
        addToggle(leftCol, y, "Entity Pooling", config.entityPooling,
            value -> config.entityPooling = value,
            "Allows Optidum object pools where supported.");
        addToggle(rightCol, y, "Memory Alerts", config.enableMemoryPressureMonitoring,
            value -> config.enableMemoryPressureMonitoring = value,
            "Logs a warning when memory usage is very high.");
        y += SPACING + 10;
        
        y = addSection(leftCol, y, "Render Distance Optimization", "Adjusts render distance carefully to protect FPS.");
        addToggle(leftCol, y, "Enabled", config.renderDistanceOptimization,
            value -> config.renderDistanceOptimization = value,
            "Turns dynamic render-distance management on or off.");
        targetFPSField = addNumberField(rightCol, y, "Target FPS", config.targetFPS,
            "FPS target used by automatic render-distance logic.");
        y += SPACING;
        minRenderDistanceField = addNumberField(leftCol, y, "Min RD", config.minRenderDistance,
            "Lowest render distance Optidum may apply.");
        maxRenderDistanceField = addNumberField(rightCol, y, "Max RD", config.maxRenderDistance,
            "Highest render distance Optidum may apply.");
        y += SPACING + 10;
        
        y = addSection(leftCol, y, "Sodium Integration", "Requires Sodium and applies Sodium-focused FPS settings.");
        addToggle(leftCol, y, "Enabled", config.sodiumIntegration,
            value -> config.sodiumIntegration = value,
            "Turns Sodium integration settings on or off.");
        addToggle(rightCol, y, "Performance Mode", config.sodiumPerformanceMode,
            value -> config.sodiumPerformanceMode = value,
            "Writes Optidum's FPS-focused Sodium profile on startup.");
        y += SPACING;
        addToggle(leftCol, y, "Reduce Visuals", config.sodiumReduceVisualEffects,
            value -> config.sodiumReduceVisualEffects = value,
            "Lowers expensive visual settings such as clouds, particles, and lighting.");
        addToggle(rightCol, y, "Defer Chunks", config.sodiumDeferChunkUpdates,
            value -> config.sodiumDeferChunkUpdates = value,
            "Defers heavy chunk updates to reduce frame spikes.");
        y += SPACING;
        addToggle(leftCol, y, "Entity Culling", config.sodiumUseEntityCulling,
            value -> config.sodiumUseEntityCulling = value,
            "Lets Sodium skip rendering hidden entities.");
        addToggle(rightCol, y, "Lag Spike Guard", config.reduceRenderDistanceOnLagSpike,
            value -> config.reduceRenderDistanceOnLagSpike = value,
            "Temporarily lowers render distance after a large frame-time spike.");
        y += SPACING + 10;
        
        y = addSection(leftCol, y, "Debug & Logging", "Controls Optidum diagnostic logging.");
        addToggle(leftCol, y, "Debug Logging", config.enableDebugLogging,
            value -> config.enableDebugLogging = value,
            "Prints detailed debug logs for troubleshooting.");
        addToggle(rightCol, y, "Performance Log", config.logPerformanceMetrics,
            value -> config.logPerformanceMetrics = value,
            "Prints periodic memory and optimizer metrics.");
        y += SPACING;
        
        int viewportHeight = Math.max(1, this.height - CONTENT_TOP - CONTENT_BOTTOM_PADDING);
        maxScroll = Math.max(0, y - CONTENT_TOP - viewportHeight);
        scrollOffset = Math.min(scrollOffset, maxScroll);
        addFixedControls();
        applyScroll();
    }
    
    private int addSection(int x, int y, String title, String tooltip) {
        Button section = Button.builder(Component.literal(title), btn -> {})
            .bounds(x, y, 310, BUTTON_HEIGHT)
            .tooltip(Tooltip.create(Component.literal(tooltip)))
            .build();
        section.active = false;
        addScrollableWidget(section);
        return y + SPACING;
    }
    
    private CycleButton<Boolean> addToggle(int x, int y, String label, boolean value, Consumer<Boolean> setter, String tooltip) {
        CycleButton<Boolean> button = CycleButton.onOffBuilder(value)
            .create(x, y, BUTTON_WIDTH, BUTTON_HEIGHT, Component.literal(label), (btn, selected) -> setter.accept(selected));
        button.setTooltip(Tooltip.create(Component.literal(tooltip)));
        return addScrollableWidget(button);
    }
    
    private EditBox addNumberField(int x, int y, String label, int value, String tooltip) {
        EditBox field = new EditBox(this.font, x, y, FIELD_WIDTH, FIELD_HEIGHT, Component.literal(label));
        field.setValue(String.valueOf(value));
        field.setTooltip(Tooltip.create(Component.literal(tooltip)));
        return addScrollableWidget(field);
    }
    
    private <T extends AbstractWidget> T addScrollableWidget(T widget) {
        addRenderableWidget(widget);
        scrollableWidgets.add(new WidgetPosition(widget, widget.getY(), widget.active));
        return widget;
    }
    
    private void addFixedControls() {
        addRenderableWidget(Button.builder(
            Component.literal("Save & Close"),
            btn -> {
                saveConfig();
                this.minecraft.gui.setScreen(parent);
            }).bounds(this.width / 2 - 155, this.height - 30, 100, BUTTON_HEIGHT)
            .tooltip(Tooltip.create(Component.literal("Saves Optidum settings and returns to Video Settings.")))
            .build());
        addRenderableWidget(Button.builder(
            Component.literal("Cancel"),
            btn -> this.minecraft.gui.setScreen(parent))
            .bounds(this.width / 2 - 50, this.height - 30, 100, BUTTON_HEIGHT)
            .tooltip(Tooltip.create(Component.literal("Returns without saving text field edits.")))
            .build());
        addRenderableWidget(Button.builder(
            Component.literal("Done"),
            btn -> this.minecraft.gui.setScreen(parent))
            .bounds(this.width / 2 + 55, this.height - 30, 100, BUTTON_HEIGHT)
            .tooltip(Tooltip.create(Component.literal("Returns to Video Settings.")))
            .build());
        scrollUpButton = addRenderableWidget(Button.builder(
            Component.literal("Up"),
            btn -> scrollBy(-SCROLL_STEP))
            .bounds(this.width - 58, this.height - 54, 50, BUTTON_HEIGHT)
            .tooltip(Tooltip.create(Component.literal("Scrolls Optidum options upward.")))
            .build());
        scrollDownButton = addRenderableWidget(Button.builder(
            Component.literal("Down"),
            btn -> scrollBy(SCROLL_STEP))
            .bounds(this.width - 58, this.height - 30, 50, BUTTON_HEIGHT)
            .tooltip(Tooltip.create(Component.literal("Scrolls Optidum options downward.")))
            .build());
    }
    
    private void scrollBy(int amount) {
        if (maxScroll <= 0) {
            return;
        }
        scrollOffset = Math.max(0, Math.min(maxScroll, scrollOffset + amount));
        applyScroll();
    }
    
    private void applyScroll() {
        int viewportBottom = this.height - CONTENT_BOTTOM_PADDING;
        for (WidgetPosition position : scrollableWidgets) {
            AbstractWidget widget = position.widget();
            int y = position.baseY() - scrollOffset;
            widget.setY(y);
            boolean visible = y + widget.getHeight() >= CONTENT_TOP && y <= viewportBottom;
            widget.visible = visible;
            widget.active = visible && position.interactive();
        }
        if (scrollUpButton != null) {
            scrollUpButton.visible = maxScroll > 0;
            scrollUpButton.active = maxScroll > 0 && scrollOffset > 0;
        }
        if (scrollDownButton != null) {
            scrollDownButton.visible = maxScroll > 0;
            scrollDownButton.active = maxScroll > 0 && scrollOffset < maxScroll;
        }
    }
    
    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
        if (maxScroll > 0) {
            scrollBy((int) (-verticalAmount * SCROLL_STEP));
            return true;
        }
        return super.mouseScrolled(mouseX, mouseY, horizontalAmount, verticalAmount);
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
        this.minecraft.gui.setScreen(parent);
    }
}
