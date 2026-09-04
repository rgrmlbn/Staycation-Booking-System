package com.spring.backend.module.user.token.service.impl;

import com.spring.backend.module.user.token.service.interfaces.TokenBlacklistService;
import com.spring.backend.security.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;


@Service
@RequiredArgsConstructor
public class TokenBlacklistServiceImpl implements TokenBlacklistService {

    private final RedisTemplate<String, String> redisTemplate;
    private final JwtUtil jwtUtil;

    private static final String PREFIX = "blacklist:";

    @Override
    public void blacklist(String token) {
        Instant expiry = jwtUtil.extractExpiration(token);
        Duration ttl = Duration.between(Instant.now(), expiry);
        if (!ttl.isNegative()) {
            redisTemplate.opsForValue().set(PREFIX + token, "true", ttl);
        }
    }

    @Override
    public boolean isBlacklisted(String token) {
        return Boolean.TRUE.equals(redisTemplate.hasKey(PREFIX + token));
    }
}