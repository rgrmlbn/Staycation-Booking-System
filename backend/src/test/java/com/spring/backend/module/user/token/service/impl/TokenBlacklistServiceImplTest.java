package com.spring.backend.module.user.token.service.impl;

import com.spring.backend.module.user.token.service.impl.TokenBlacklistServiceImpl;
import com.spring.backend.security.util.JwtUtil;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TokenBlacklistServiceImplTest {

    @Mock
    private RedisTemplate<String, String> redisTemplate;

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private ValueOperations<String, String> valueOperations;

    @InjectMocks
    private TokenBlacklistServiceImpl blacklistService;

    @Test
    @DisplayName("Stores a token in Redis until its expiration")
    void blacklist_unexpiredToken_setsExpiringRedisEntry() {
        String token = "access-token";
        when(jwtUtil.extractExpiration(token)).thenReturn(Instant.now().plusSeconds(120));
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);

        blacklistService.blacklist(token);

        verify(valueOperations).set(eq("blacklist:" + token), eq("true"), argThat(ttl ->
                !ttl.isNegative() && !ttl.isZero() && ttl.compareTo(java.time.Duration.ofSeconds(120)) <= 0));
    }

    @Test
    @DisplayName("Does not store a token whose expiration has passed")
    void blacklist_expiredToken_doesNotWriteRedisEntry() {
        String token = "expired-token";
        when(jwtUtil.extractExpiration(token)).thenReturn(Instant.now().minusSeconds(1));

        blacklistService.blacklist(token);

        verify(redisTemplate, never()).opsForValue();
    }

    @Test
    @DisplayName("Reports whether a token is present in the blacklist")
    void isBlacklisted_returnsTrueOnlyForRedisTrue() {
        when(redisTemplate.hasKey("blacklist:blocked")).thenReturn(true);
        when(redisTemplate.hasKey("blacklist:allowed")).thenReturn(false);
        when(redisTemplate.hasKey("blacklist:unknown")).thenReturn(null);

        assertThat(blacklistService.isBlacklisted("blocked")).isTrue();
        assertThat(blacklistService.isBlacklisted("allowed")).isFalse();
        assertThat(blacklistService.isBlacklisted("unknown")).isFalse();
    }
}
