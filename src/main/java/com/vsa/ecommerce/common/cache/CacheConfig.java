package com.vsa.ecommerce.common.cache;

import com.vsa.ecommerce.common.cache.pubsub.CacheInvalidationSubscriber;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter;

/**
 * Comprehensive cache configuration for enterprise-grade caching strategy.
 * <p>
 * Components:
 * - L1 Cache: Caffeine (in-memory, <1ms, 30s TTL) → CaffeineLocalCacheService
 * - L2 Cache: Redis (distributed, 5-10ms, 5min TTL) →
 * RedisDistributedCacheService
 * - Hybrid Cache: L1 + L2 coordination → HybridCacheManagerImpl
 * - Pub/Sub: Multi-instance cache invalidation → CacheInvalidationSubscriber
 * <p>
 * Architecture:
 * 
 * <pre>
 * ┌─────────────────┐
 * │ Service Layer   │
 * └────────┬────────┘
 *          │
 *          ▼
 * ┌─────────────────┐
 * │ Hybrid Cache    │ ← Primary facade for all caching
 * └────────┬────────┘
 *          │
 *     ┌────┴────┐
 *     ▼         ▼
 * ┌──────┐  ┌──────┐
 * │  L1  │  │  L2  │
 * │(30s) │  │(5min)│
 * └──────┘  └──────┘
 *     │         │
 *     └────┬────┘
 *          │
 *          ▼
 *    ┌──────────┐
 *    │ Pub/Sub  │ ← Multi-instance invalidation
 *    └──────────┘
 * </pre>
 * <p>
 * IMPORTANT:
 * - L1 TTL (30s) MUST be shorter than L2 TTL (5min)
 * - Do NOT cache in Controllers or during transactions
 * - Only cache DTOs or immutable objects, never JPA entities
 * - Use HybridCacheManager in service layer, not Spring @Cacheable
 */
@Slf4j
@Configuration
@EnableCaching
@RequiredArgsConstructor
public class CacheConfig {

        private final CacheInvalidationSubscriber invalidationSubscriber;

        @Value("${cache.pub-sub.channel:cache-invalidation}")
        private String pubSubChannel;

        /**
         * Configure Redis message listener container for Pub/Sub.
         * Listens to cache invalidation events and triggers L1 eviction.
         */
        @Bean
        public RedisMessageListenerContainer redisMessageListenerContainer(
                        RedisConnectionFactory connectionFactory,
                        MessageListenerAdapter messageListenerAdapter) {
                RedisMessageListenerContainer container = new RedisMessageListenerContainer();
                container.setConnectionFactory(connectionFactory);
                container.addMessageListener(messageListenerAdapter, new ChannelTopic(pubSubChannel));

                log.info("Redis Pub/Sub configured - Channel: {}", pubSubChannel);
                return container;
        }

        /**
         * Message listener adapter for cache invalidation events.
         * Routes incoming messages to CacheInvalidationSubscriber.
         */
        @Bean
        public MessageListenerAdapter messageListenerAdapter() {
                return new MessageListenerAdapter(invalidationSubscriber, "handleMessage");
        }
}
