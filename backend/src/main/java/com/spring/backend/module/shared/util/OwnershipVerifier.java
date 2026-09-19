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

        if (!isOwner(principal, target) && !isAdmin(principal)) {
            throw new AccessDeniedException("You do not have permission to access this resource.");
        }
    }

    public void verifyAdmin() {

        UserPrincipal principal = getCurrentPrincipal();

        if (!isAdmin(principal)) {
            throw new AccessDeniedException("You do not have permission to access this resource.");
        }
    }

    public UserEntity getCurrentUser() {
        return getCurrentPrincipal().getUser();
    }

    // Helper method to check if the current principal owns the target
    private boolean isOwner(UserPrincipal principal, UserEntity target) {
        return principal.getUser().getId().equals(target.getId());
    }

    // Helper method to check if the current principal is an admin
    private boolean isAdmin(UserPrincipal principal) {
        return principal.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
    }

    // Helper method to get the current authenticated principal
    private UserPrincipal getCurrentPrincipal() {
        return (UserPrincipal) SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal();
    }
}