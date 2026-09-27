package com.spring.backend.module.user.user.service.impl;

import com.spring.backend.module.user.user.entity.UserEntity;
import com.spring.backend.module.user.user.enums.UserRole;
import com.spring.backend.module.user.user.repository.UserRepository;
import com.spring.backend.security.principal.UserPrincipal;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserDetailsServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserDetailsServiceImpl userDetailsService;

    @Test
    @DisplayName("Loads the user principal by email")
    void loadUserByUsername_userExists_returnsPrincipal() {
        UserEntity user = UserEntity.builder()
                .id(1L)
                .email("guest@example.com")
                .password("encoded-password")
                .role(UserRole.GUEST)
                .build();
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));

        UserPrincipal result = (UserPrincipal) userDetailsService.loadUserByUsername(user.getEmail());

        assertThat(result.getUser()).isSameAs(user);
        assertThat(result.getUsername()).isEqualTo(user.getEmail());
        assertThat(result.getPassword()).isEqualTo(user.getPassword());
        assertThat(result.getAuthorities()).extracting("authority").containsExactly("ROLE_GUEST");
    }

    @Test
    @DisplayName("Throws when no account matches the email")
    void loadUserByUsername_userMissing_throws() {
        when(userRepository.findByEmail("missing@example.com")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userDetailsService.loadUserByUsername("missing@example.com"))
                .isInstanceOf(UsernameNotFoundException.class)
                .hasMessage("Invalid email or password.");
    }
}
