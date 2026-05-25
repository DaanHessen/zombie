package dev.daanh.zombie.service;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class CacheFactory {
    private final Map<String, Cache<?, ?>> activeCaches = new ConcurrentHashMap<>();

    @SuppressWarnings("unchecked")
    public <K, V> Cache<K, V> getOrCreateCache(String cacheName, int maxSize) {
        return (Cache<K, V>) activeCaches.computeIfAbsent(cacheName,
                name -> Caffeine.newBuilder()
                        .maximumSize(maxSize)
                        .build());
    }
}