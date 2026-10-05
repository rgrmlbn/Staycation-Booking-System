import api from "./api";
import { API_ENDPOINTS } from "../utils/constants";

export const bookingService = {
  async getGuestBookings(params = {}) {
    const { data } = await api.get(API_ENDPOINTS.bookings.guest, { params });
    return data;
  },

  async getHostBookings(params = {}) {
    const { data } = await api.get(API_ENDPOINTS.bookings.host, { params });
    return data;
  },

  async createBooking(payload) {
    const { data } = await api.post(API_ENDPOINTS.bookings.create, payload);
    return data;
  },

  async updateBooking(id, payload) {
    const { data } = await api.patch(
      `${API_ENDPOINTS.bookings.root}/${id}`,
      payload,
    );
    return data;
  },

  async approveBooking(id) {
    const { data } = await api.patch(
      `${API_ENDPOINTS.bookings.root}/${id}/approve`,
    );
    return data;
  },

  async rejectBooking(id, reason) {
    const { data } = await api.patch(
      `${API_ENDPOINTS.bookings.root}/${id}/reject`,
      null,
      { params: { reason } },
    );
    return data;
  },

  async cancelBooking(id, reason) {
    const { data } = await api.patch(
      `${API_ENDPOINTS.bookings.root}/${id}/cancel`,
      null,
      { params: { reason } },
    );
    return data;
  },

  async completeBooking(id, reason) {
    const { data } = await api.patch(
      `${API_ENDPOINTS.bookings.root}/${id}/complete`,
      null,
      { params: reason ? { reason } : undefined },
    );
    return data;
  },
};

export default bookingService;
