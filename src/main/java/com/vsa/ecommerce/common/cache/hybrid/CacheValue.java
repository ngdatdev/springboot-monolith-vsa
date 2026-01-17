package com.vsa.ecommerce.common.cache.hybrid;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * Wrapper for cached values to support FusionCache features like Soft TTL.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CacheValue<T> implements Serializable {
    private T value;
    private long softExpiration; // timestamp in millis

    public boolean isSoftExpired() {
        return System.currentTimeMillis() > softExpiration;
    }
}
