import { useMutation, useQuery, useQueryClient } from "@tanstack/react-query";
import authService from "../services/authService";
import userService from "../services/userService";
import { QUERY_KEYS, STORAGE_KEYS } from "../utils/constants";

export function useAuth() {
  const queryClient = useQueryClient();
  const hasToken = Boolean(sessionStorage.getItem(STORAGE_KEYS.accessToken));

  const currentUserQuery = useQuery({
    queryKey: QUERY_KEYS.currentUser,
    queryFn: userService.getCurrentUser,
    enabled: hasToken,
    retry: false,
  });

  const loginMutation = useMutation({
    mutationFn: authService.login,
    onSuccess: () =>
      queryClient.invalidateQueries({ queryKey: QUERY_KEYS.currentUser }),
  });

  const registerMutation = useMutation({
    mutationFn: authService.register,
  });

  const logoutMutation = useMutation({
    mutationFn: authService.logout,
    onSettled: () => {
      queryClient.removeQueries({ queryKey: QUERY_KEYS.currentUser });
    },
  });

  return {
    user: currentUserQuery.data ?? null,
    isAuthenticated: Boolean(currentUserQuery.data),
    isLoading: hasToken && currentUserQuery.isPending,
    error: currentUserQuery.error,
    login: loginMutation.mutateAsync,
    register: registerMutation.mutateAsync,
    logout: logoutMutation.mutateAsync,
    isLoggingIn: loginMutation.isPending,
    isRegistering: registerMutation.isPending,
    isLoggingOut: logoutMutation.isPending,
  };
}

export default useAuth;
