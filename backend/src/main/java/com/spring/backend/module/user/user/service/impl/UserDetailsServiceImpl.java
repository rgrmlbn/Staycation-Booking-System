package com.spring.backend.module.user.user.service.impl;

import com.spring.backend.module.user.user.entity.UserEntity;
import com.spring.backend.module.user.user.repository.UserRepository;
import com.spring.backend.security.principal.UserPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService{

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {

        UserEntity user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Invalid email or password.")); // Spring Security requires this specific exception for user not found scenarios - Resource Not Found Exception is for Service Layer

        return new UserPrincipal(user);
    }
}
