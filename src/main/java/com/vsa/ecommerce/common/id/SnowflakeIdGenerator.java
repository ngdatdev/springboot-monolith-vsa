package com.vsa.ecommerce.common.id;

import lombok.extern.slf4j.Slf4j;

/**
 * Twitter Snowflake ID Generator.
 * 
 * 64-bit ID structure:
 * - 1 bit: unused (always 0)
 * - 41 bits: timestamp (milliseconds since epoch)
 * - 10 bits: machine ID (datacenter + worker)
 * - 12 bits: sequence number
 * 
 * Features:
 * - Generates unique IDs across distributed systems
 * - Time-ordered (sortable)
 * - No coordination required
 * - 4096 IDs per millisecond per machine
 * 
 * Usage:
 * 
 * <pre>
 * SnowflakeIdGenerator generator = new SnowflakeIdGenerator(1, 1);
 * Long id = generator.nextId();
 * </pre>
 */
@Slf4j
public class SnowflakeIdGenerator implements IdGenerator {

    // ==================== Constants ====================

    /**
     * Start epoch: 2024-01-01 00:00:00 UTC
     * This gives us 69 years from 2024 (until 2093)
     */
    private static final long EPOCH = 1704067200000L; // 2024-01-01T00:00:00Z

    /**
     * Bit lengths
     */
    private static final long DATACENTER_ID_BITS = 5L;
    private static final long WORKER_ID_BITS = 5L;
    private static final long SEQUENCE_BITS = 12L;

    /**
     * Max values
     */
    private static final long MAX_DATACENTER_ID = ~(-1L << DATACENTER_ID_BITS); // 31
    private static final long MAX_WORKER_ID = ~(-1L << WORKER_ID_BITS); // 31
    private static final long MAX_SEQUENCE = ~(-1L << SEQUENCE_BITS); // 4095

    /**
     * Bit shifts
     */
    private static final long WORKER_ID_SHIFT = SEQUENCE_BITS;
    private static final long DATACENTER_ID_SHIFT = SEQUENCE_BITS + WORKER_ID_BITS;
    private static final long TIMESTAMP_SHIFT = SEQUENCE_BITS + WORKER_ID_BITS + DATACENTER_ID_BITS;

    // ==================== Instance Fields ====================

    private final long datacenterId;
    private final long workerId;
    private long sequence = 0L;
    private long lastTimestamp = -1L;

    // ==================== Constructor ====================

    /**
     * Create a new Snowflake ID generator.
     * 
     * @param datacenterId Datacenter ID (0-31)
     * @param workerId     Worker ID (0-31)
     */
    public SnowflakeIdGenerator(long datacenterId, long workerId) {
        if (datacenterId > MAX_DATACENTER_ID || datacenterId < 0) {
            throw new IllegalArgumentException(
                    String.format("Datacenter ID must be between 0 and %d", MAX_DATACENTER_ID));
        }
        if (workerId > MAX_WORKER_ID || workerId < 0) {
            throw new IllegalArgumentException(
                    String.format("Worker ID must be between 0 and %d", MAX_WORKER_ID));
        }

        this.datacenterId = datacenterId;
        this.workerId = workerId;

        log.info("Snowflake ID Generator initialized: datacenterId={}, workerId={}",
                datacenterId, workerId);
    }

    // ==================== Public Methods ====================

    /**
     * Generate next unique ID.
     * Thread-safe.
     * 
     * @return 64-bit unique ID
     */
    public synchronized long nextId() {
        long timestamp = currentTimestamp();

        // Check clock moving backwards
        if (timestamp < lastTimestamp) {
            long offset = lastTimestamp - timestamp;
            if (offset <= 5) {
                // Small clock drift, wait it out
                try {
                    wait(offset << 1);
                    timestamp = currentTimestamp();
                    if (timestamp < lastTimestamp) {
                        throw new RuntimeException(
                                String.format("Clock moved backwards. Refusing to generate ID for %d ms", offset));
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    throw new RuntimeException("Interrupted while waiting for clock", e);
                }
            } else {
                throw new RuntimeException(
                        String.format("Clock moved backwards. Refusing to generate ID for %d ms", offset));
            }
        }

        // Same millisecond: increment sequence
        if (timestamp == lastTimestamp) {
            sequence = (sequence + 1) & MAX_SEQUENCE;

            // Sequence overflow: wait for next millisecond
            if (sequence == 0) {
                timestamp = waitNextMillis(lastTimestamp);
            }
        } else {
            // New millisecond: reset sequence
            sequence = 0L;
        }

        lastTimestamp = timestamp;

        // Generate ID
        return ((timestamp - EPOCH) << TIMESTAMP_SHIFT)
                | (datacenterId << DATACENTER_ID_SHIFT)
                | (workerId << WORKER_ID_SHIFT)
                | sequence;
    }

    /**
     * Parse Snowflake ID to extract components.
     */
    public static SnowflakeIdComponents parse(long id) {
        long timestamp = (id >> TIMESTAMP_SHIFT) + EPOCH;
        long datacenterId = (id >> DATACENTER_ID_SHIFT) & MAX_DATACENTER_ID;
        long workerId = (id >> WORKER_ID_SHIFT) & MAX_WORKER_ID;
        long sequence = id & MAX_SEQUENCE;

        return new SnowflakeIdComponents(timestamp, datacenterId, workerId, sequence);
    }

    // ==================== Private Methods ====================

    private long currentTimestamp() {
        return System.currentTimeMillis();
    }

    private long waitNextMillis(long lastTimestamp) {
        long timestamp = currentTimestamp();
        while (timestamp <= lastTimestamp) {
            timestamp = currentTimestamp();
        }
        return timestamp;
    }

    // ==================== Inner Class ====================

    /**
     * Parsed components of a Snowflake ID.
     */
    public record SnowflakeIdComponents(
            long timestamp,
            long datacenterId,
            long workerId,
            long sequence) {
        public String toHumanReadable() {
            return String.format(
                    "SnowflakeID[timestamp=%d, datacenter=%d, worker=%d, sequence=%d]",
                    timestamp, datacenterId, workerId, sequence);
        }
    }
}
