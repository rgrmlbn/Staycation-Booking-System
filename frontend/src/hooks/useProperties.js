import { useMutation, useQuery, useQueryClient } from "@tanstack/react-query";
import propertyService from "../services/propertyService";
import { QUERY_KEYS } from "../utils/constants";

export function useProperties(params = {}, options = {}) {
  return useQuery({
    queryKey: [...QUERY_KEYS.properties, "list", params],
    queryFn: () => propertyService.getProperties(params),
    ...options,
  });
}

export function useProperty(id, options = {}) {
  return useQuery({
    queryKey: [...QUERY_KEYS.properties, "detail", id],
    queryFn: () => propertyService.getProperty(id),
    enabled: Boolean(id),
    ...options,
  });
}

export function useMyProperties(params = {}, options = {}) {
  return useQuery({
    queryKey: [...QUERY_KEYS.properties, "mine", params],
    queryFn: () => propertyService.getMyProperties(params),
    ...options,
  });
}

export function useCreateProperty() {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: propertyService.createProperty,
    onSuccess: () =>
      queryClient.invalidateQueries({ queryKey: QUERY_KEYS.properties }),
  });
}

export function useUpdateProperty() {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: ({ id, ...payload }) =>
      propertyService.updateProperty(id, payload),
    onSuccess: () =>
      queryClient.invalidateQueries({ queryKey: QUERY_KEYS.properties }),
  });
}

export function useDeleteProperty() {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: propertyService.deleteProperty,
    onSuccess: () =>
      queryClient.invalidateQueries({ queryKey: QUERY_KEYS.properties }),
  });
}

export default useProperties;
