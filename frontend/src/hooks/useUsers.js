import { useMutation, useQuery, useQueryClient } from "@tanstack/react-query";
import userService from "../services/userService";
import { QUERY_KEYS } from "../utils/constants";

export function useUsers(options = {}) {
  return useQuery({
    queryKey: QUERY_KEYS.users,
    queryFn: userService.getUsers,
    ...options,
  });
}

export function useUser(id, options = {}) {
  return useQuery({
    queryKey: [...QUERY_KEYS.users, id],
    queryFn: () => userService.getUser(id),
    enabled: Boolean(id),
    ...options,
  });
}

export function useUpdateUser() {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: ({ id, ...payload }) => userService.updateUser(id, payload),
    onSuccess: () =>
      queryClient.invalidateQueries({ queryKey: QUERY_KEYS.users }),
  });
}

export function useChangePassword() {
  return useMutation({
    mutationFn: ({ id, ...payload }) =>
      userService.changePassword(id, payload),
  });
}

export function useDeleteUser() {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: userService.deleteUser,
    onSuccess: () =>
      queryClient.invalidateQueries({ queryKey: QUERY_KEYS.users }),
  });
}

export default useUsers;
