package com.budgetgamer1503.optimization;

import com.budgetgamer1503.config.ConfigManager;
import com.budgetgamer1503.config.OptidumConfig;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.zip.Deflater;
import java.util.zip.Inflater;

@SuppressWarnings("unused")
public class NetworkOptimizer {
    private static final Logger LOGGER = LoggerFactory.getLogger("Optidum/NetworkOptimizer");
    
    // Packet aggregation buffer
    private static final Map<UUID, List<byte[]>> packetBuffers = new HashMap<>();
    private static final Map<UUID, Long> lastFlushTime = new HashMap<>();
    private static final long FLUSH_INTERVAL_MS = 50; // 50ms
    
    // Compression utilities
    private static final ThreadLocal<Deflater> deflater = ThreadLocal.withInitial(() -> new Deflater(Deflater.BEST_SPEED));
    private static final ThreadLocal<Inflater> inflater = ThreadLocal.withInitial(Inflater::new);
    
    // Statistics
    private static long totalPacketsProcessed = 0;
    private static long totalBytesSaved = 0;
    private static long compressedPackets = 0;
    
    public static byte[] optimizeOutgoingPacket(byte[] data, UUID connectionId) {
        OptidumConfig config = ConfigManager.getConfig();
        if (!config.networkOptimization) {
            return data;
        }
        
        totalPacketsProcessed++;
        
        // Check if packet should be compressed
        if (data.length > config.packetCompressionThreshold) {
            byte[] compressed = compressData(data);
            if (compressed.length < data.length) {
                compressedPackets++;
                totalBytesSaved += (data.length - compressed.length);
                return compressed;
            }
        }
        
        // Check if packet should be aggregated
        if (config.aggregateSmallPackets && data.length < 100) {
            bufferPacketForAggregation(data, connectionId);
            return null; // Signal that packet is buffered
        }
        
        return data;
    }
    
    public static byte[] processIncomingPacket(byte[] data, UUID ignoredConnectionId) {
        OptidumConfig config = ConfigManager.getConfig();
        if (!config.networkOptimization) {
            return data;
        }
        
        // Check if packet is compressed (simple heuristic: first byte indicates compression)
        if (data.length > 0 && (data[0] & 0x80) != 0) {
            return decompressData(data);
        }
        
        return data;
    }
    
    private static byte[] compressData(byte[] data) {
        try {
            Deflater def = deflater.get();
            def.reset();
            def.setInput(data);
            def.finish();
            
            ByteBuf output = Unpooled.buffer(data.length);
            byte[] buffer = new byte[1024];
            
            while (!def.finished()) {
                int count = def.deflate(buffer);
                output.writeBytes(buffer, 0, count);
            }
            
            byte[] result = new byte[output.readableBytes()];
            output.readBytes(result);
            
            // Add compression marker
            byte[] markedResult = new byte[result.length + 1];
            markedResult[0] = (byte) 0x80; // Compression marker
            System.arraycopy(result, 0, markedResult, 1, result.length);
            
            return markedResult;
        } catch (Exception e) {
            LOGGER.error("Failed to compress packet", e);
            return data;
        }
    }
    
    private static byte[] decompressData(byte[] data) {
        try {
            // Remove compression marker
            byte[] compressedData = Arrays.copyOfRange(data, 1, data.length);
            
            Inflater inf = inflater.get();
            inf.reset();
            inf.setInput(compressedData);
            
            ByteBuf output = Unpooled.buffer(compressedData.length * 2);
            byte[] buffer = new byte[1024];
            
            while (!inf.finished()) {
                int count = inf.inflate(buffer);
                output.writeBytes(buffer, 0, count);
            }
            
            byte[] result = new byte[output.readableBytes()];
            output.readBytes(result);
            return result;
        } catch (Exception e) {
            LOGGER.error("Failed to decompress packet", e);
            return data;
        }
    }
    
    private static void bufferPacketForAggregation(byte[] data, UUID connectionId) {
        long currentTime = System.currentTimeMillis();
        
        packetBuffers.computeIfAbsent(connectionId, k -> new ArrayList<>()).add(data);
        
        // Check if we should flush the buffer
        Long lastFlush = lastFlushTime.get(connectionId);
        if (lastFlush == null || (currentTime - lastFlush) > FLUSH_INTERVAL_MS) {
            flushBuffer(connectionId);
        }
    }
    
    private static void flushBuffer(UUID connectionId) {
        List<byte[]> buffer = packetBuffers.get(connectionId);
        if (buffer == null || buffer.isEmpty()) {
            return;
        }
        
        // Aggregate packets
        int totalSize = buffer.stream().mapToInt(arr -> arr.length).sum();
        ByteBuf aggregated = Unpooled.buffer(totalSize + 4);
        
        // Write packet count
        aggregated.writeInt(buffer.size());
        
        // Write each packet with its length
        for (byte[] packet : buffer) {
            aggregated.writeInt(packet.length);
            aggregated.writeBytes(packet);
        }
        
        OptidumConfig config = ConfigManager.getConfig();
        if (config.enableDebugLogging) {
            LOGGER.debug("Aggregated {} packets for connection {}, total size: {}", 
                buffer.size(), connectionId, aggregated.readableBytes());
        }
        
        buffer.clear();
        lastFlushTime.put(connectionId, System.currentTimeMillis());
    }
    
    public static void cleanupConnection(UUID connectionId) {
        packetBuffers.remove(connectionId);
        lastFlushTime.remove(connectionId);
    }
    
    public static void logStats() {
        OptidumConfig config = ConfigManager.getConfig();
        if (config.logPerformanceMetrics) {
            LOGGER.info("Network optimizer stats: {} packets processed, {} compressed, {} bytes saved",
                totalPacketsProcessed, compressedPackets, totalBytesSaved);
        }
    }
}