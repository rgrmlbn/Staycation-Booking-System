package com.spring.backend.module.user.token.service.impl;

import com.spring.backend.config.JwtProperties;
import com.spring.backend.exception.user.auth.InvalidTokenException;
import com.spring.backend.exception.user.auth.TokenExpiredException;
import com.spring.backend.exception.user.auth.TokenRevokedException;
import com.spring.backend.module.user.token.entity.RefreshToken;
import com.spring.backend.module.user.token.repository.RefreshTokenRepository;
import com.spring.backend.module.user.user.entity.UserEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.Instant;
import java.util.Base64;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RefreshTokenServiceImplTest {

    @Mock
    private JwtProperties jwtProperties;

    @Mock
    private RefreshTokenRepository refreshTokenRepository;

    @InjectMocks
    private RefreshTokenServiceImpl refreshTokenService;

    private UserEntity user;

    @BeforeEach
    void setUp() {
        user = UserEntity.builder().id(7L).build();
    }

    @Test
    @DisplayName("Stores only a hash when creating a refresh token")
    void createRefreshToken_persistsHashedTokenAndReturnsRawToken() throws Exception {
        when(jwtProperties.getRefreshTokenExpiration()).thenReturn(60_000L);
        String rawToken = refreshTokenService.createRefreshToken(user);
        ArgumentCaptor<RefreshToken> tokenCaptor = ArgumentCaptor.forClass(RefreshToken.class);

        verify(refreshTokenRepository).save(tokenCaptor.capture());
        RefreshToken stored = tokenCaptor.getValue();

        assertThat(UUID.fromString(rawToken)).isNotNull();
        assertThat(stored.getToken()).isEqualTo(hash(rawToken));
        assertThat(stored.getToken()).isNotEqualTo(rawToken);
        assertThat(stored.getUser()).isSameAs(user);
        assertThat(stored.getExpiresAt()).isAfter(Instant.now());
    }

    @Test
    @DisplayName("Returns a valid non-revoked refresh token")
    void validateRefreshToken_validToken_returnsStoredToken() throws Exception {
        String rawToken = "refresh-token";
        RefreshToken stored = RefreshToken.builder()
                .token(hash(rawToken))
                .user(user)
                .expiresAt(Instant.now().plusSeconds(60))
                .build();
        when(refreshTokenRepository.findByToken(hash(rawToken))).thenReturn(Optional.of(stored));

        assertThat(refreshTokenService.validateRefreshToken(rawToken)).isSameAs(stored);
    }

    @Test
    @DisplayName("Rejects a token hash that is not stored")
    void validateRefreshToken_unknownToken_throws() throws Exception {
        when(refreshTokenRepository.findByToken(hash("unknown"))).thenReturn(Optional.empty());

        assertThatThrownBy(() -> refreshTokenService.validateRefreshToken("unknown"))
                .isInstanceOf(InvalidTokenException.class);
    }

    @Test
    @DisplayName("Deletes expired refresh tokens before reporting expiration")
    void validateRefreshToken_expiredToken_deletesAndThrows() throws Exception {
        String rawToken = "expired-token";
        RefreshToken expired = RefreshToken.builder()
                .token(hash(rawToken))
                .expiresAt(Instant.now().minusSeconds(1))
                .build();
        when(refreshTokenRepository.findByToken(hash(rawToken))).thenReturn(Optional.of(expired));

        assertThatThrownBy(() -> refreshTokenService.validateRefreshToken(rawToken))
                .isInstanceOf(TokenExpiredException.class);

        verify(refreshTokenRepository).delete(expired);
    }

    @Test
    @DisplayName("Rejects a revoked refresh token without deleting it")
    void validateRefreshToken_revokedToken_throws() throws Exception {
        String rawToken = "revoked-token";
        RefreshToken revoked = RefreshToken.builder()
                .token(hash(rawToken))
                .expiresAt(Instant.now().plusSeconds(60))
                .revoked(true)
                .build();
        when(refreshTokenRepository.findByToken(hash(rawToken))).thenReturn(Optional.of(revoked));

        assertThatThrownBy(() -> refreshTokenService.validateRefreshToken(rawToken))
                .isInstanceOf(TokenRevokedException.class);

        verify(refreshTokenRepository, never()).delete(any());
    }

    @Test
    @DisplayName("Rotates a token by deleting the old row and creating a new one")
    void rotateRefreshToken_deletesOldAndCreatesReplacement() {
        when(jwtProperties.getRefreshTokenExpiration()).thenReturn(60_000L);
        RefreshToken oldToken = RefreshToken.builder().user(user).build();

        String replacement = refreshTokenService.rotateRefreshToken(oldToken);

        assertThat(UUID.fromString(replacement)).isNotNull();
        verify(refreshTokenRepository).delete(oldToken);
        verify(refreshTokenRepository).save(argThat(token ->
                token.getUser() == user && !token.getToken().equals(replacement)));
    }

    @Test
    @DisplayName("Revokes and deletes all tokens by user ID")
    void userTokenOperations_useUserId() {
        refreshTokenService.revokeAllByUser(user);
        refreshTokenService.deleteAllByUser(user);

        verify(refreshTokenRepository).revokeAllByUserId(user.getId());
        verify(refreshTokenRepository).deleteAllByUserId(user.getId());
    }

    private String hash(String token) throws Exception {
        byte[] bytes = MessageDigest.getInstance("SHA-256").digest(token.getBytes(StandardCharsets.UTF_8));
        return Base64.getEncoder().encodeToString(bytes);
    }
}
