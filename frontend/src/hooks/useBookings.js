import { useMutation, useQuery, useQueryClient } from "@tanstack/react-query";
import bookingService from "../services/bookingService";
import { QUERY_KEYS } from "../utils/constants";

export function useBookings({ role = "guest", ...params } = {}, options = {}) {
  return useQuery({
    queryKey: [...QUERY_KEYS.bookings, role, params],
    queryFn: () =>
      role.toLowerCase() === "host"
        ? bookingService.getHostBookings(params)
        : bookingService.getGuestBookings(params),
    ...options,
  });
}

function useBookingMutation(mutationFn) {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn,
    onSuccess: () =>
      queryClient.invalidateQueries({ queryKey: QUERY_KEYS.bookings }),
  });
}

export function useCreateBooking() {
  return useBookingMutation(bookingService.createBooking);
}

export function useUpdateBooking() {
  return useBookingMutation(({ id, ...payload }) =>
    bookingService.updateBooking(id, payload),
  );
}

export function useApproveBooking() {
  return useBookingMutation(bookingService.approveBooking);
}

export function useRejectBooking() {
  return useBookingMutation(({ id, reason }) =>
    bookingService.rejectBooking(id, reason),
  );
}

export function useCancelBooking() {
  return useBookingMutation(({ id, reason }) =>
    bookingService.cancelBooking(id, reason),
  );
}

export function useCompleteBooking() {
  return useBookingMutation(({ id, reason }) =>
    bookingService.completeBooking(id, reason),
  );
}

export default useBookings;
