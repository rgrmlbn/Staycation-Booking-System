package com.spring.backend.module.user.auth.service.impl;

import com.spring.backend.config.LoginRateLimitProperties;
import com.spring.backend.exception.user.auth.TooManyRequestsException;
import com.spring.backend.security.util.IpExtractor;
import io.github.bucket4j.BucketConfiguration;
import io.github.bucket4j.distributed.BucketProxy;
import io.github.bucket4j.distributed.proxy.ProxyManager;
import io.github.bucket4j.distributed.proxy.RemoteBucketBuilder;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.function.Supplier;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LoginRateLimiterServiceImplTest {

    @Mock
    private ProxyManager<String> proxyManager;

    @Mock
    private IpExtractor ipExtractor;

    @Mock
    private LoginRateLimitProperties loginRateLimitProperties;

    @Mock
    private LoginRateLimitProperties.Limit emailLimit;

    @Mock
    private LoginRateLimitProperties.Limit ipLimit;

    @Mock
    private RemoteBucketBuilder<String> bucketBuilder;

    @InjectMocks
    private LoginRateLimiterServiceImpl rateLimiterService;

    @Test
    @DisplayName("Checks email and client IP buckets for every login attempt")
    void checkLimits_bothBucketsAvailable_consumesBoth() {
        configureEmailLimit();
        configureIpLimit();
        BucketProxy emailBucket = mock(BucketProxy.class);
        BucketProxy ipBucket = mock(BucketProxy.class);
        when(proxyManager.builder()).thenReturn(bucketBuilder);
        when(bucketBuilder.build(eq("rate_limit:email:guest@example.com"),
                org.mockito.ArgumentMatchers.<Supplier<BucketConfiguration>>any())).thenReturn(emailBucket);
        when(bucketBuilder.build(eq("rate_limit:ip:192.0.2.1"),
                org.mockito.ArgumentMatchers.<Supplier<BucketConfiguration>>any())).thenReturn(ipBucket);
        when(emailBucket.tryConsume(1)).thenReturn(true);
        when(ipBucket.tryConsume(1)).thenReturn(true);

        rateLimiterService.checkLimits("guest@example.com");

        verify(emailBucket).tryConsume(1);
        verify(ipBucket).tryConsume(1);
    }

    @Test
    @DisplayName("Rejects a login attempt after its email bucket is exhausted")
    void checkLimits_emailBucketExhausted_throwsBeforeIpCheck() {
        configureEmailLimit();
        BucketProxy emailBucket = mock(BucketProxy.class);
        when(proxyManager.builder()).thenReturn(bucketBuilder);
        when(bucketBuilder.build(eq("rate_limit:email:guest@example.com"),
                org.mockito.ArgumentMatchers.<Supplier<BucketConfiguration>>any())).thenReturn(emailBucket);
        when(emailBucket.tryConsume(1)).thenReturn(false);

        assertThatThrownBy(() -> rateLimiterService.checkLimits("guest@example.com"))
                .isInstanceOf(TooManyRequestsException.class);

        verify(bucketBuilder, never()).build(eq("rate_limit:ip:192.0.2.1"),
                org.mockito.ArgumentMatchers.<Supplier<BucketConfiguration>>any());
    }

    @Test
    @DisplayName("Rejects a login attempt after its client IP bucket is exhausted")
    void checkLimits_ipBucketExhausted_throws() {
        configureEmailLimit();
        configureIpLimit();
        BucketProxy emailBucket = mock(BucketProxy.class);
        BucketProxy ipBucket = mock(BucketProxy.class);
        when(proxyManager.builder()).thenReturn(bucketBuilder);
        when(bucketBuilder.build(eq("rate_limit:email:guest@example.com"),
                org.mockito.ArgumentMatchers.<Supplier<BucketConfiguration>>any())).thenReturn(emailBucket);
        when(bucketBuilder.build(eq("rate_limit:ip:192.0.2.1"),
                org.mockito.ArgumentMatchers.<Supplier<BucketConfiguration>>any())).thenReturn(ipBucket);
        when(emailBucket.tryConsume(1)).thenReturn(true);
        when(ipBucket.tryConsume(1)).thenReturn(false);

        assertThatThrownBy(() -> rateLimiterService.checkLimits("guest@example.com"))
                .isInstanceOf(TooManyRequestsException.class);
    }

    private void configureEmailLimit() {
        when(loginRateLimitProperties.getEmail()).thenReturn(emailLimit);
        when(emailLimit.getCapacity()).thenReturn(5);
        when(emailLimit.getRefillMinutes()).thenReturn(1);
    }

    private void configureIpLimit() {
        when(loginRateLimitProperties.getIp()).thenReturn(ipLimit);
        when(ipLimit.getCapacity()).thenReturn(10);
        when(ipLimit.getRefillMinutes()).thenReturn(1);
        when(ipExtractor.getClientIp()).thenReturn("192.0.2.1");
    }
}
