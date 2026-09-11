package com.spring.backend.module.shared.util;

import com.spring.backend.module.user.user.entity.UserEntity;
import com.spring.backend.security.principal.UserPrincipal;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class OwnershipVerifier {

    public void verifyOwnershipOrAdmin(UserEntity target) {
        UserPrincipal principal = getCurrentPrincipal();

        boolean isOwner = principal.getUser().getId().equals(target.getId());
        boolean isAdmin = principal.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

        if (!isOwner && !isAdmin) {
            throw new AccessDeniedException("Access denied.");
        }
    }

    public UserEntity getCurrentUser() {
        return getCurrentPrincipal().getUser();
    }

    // Helper method to get the current authenticated principal user
    private UserPrincipal getCurrentPrincipal() {
        return (UserPrincipal) SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal();
    }
}