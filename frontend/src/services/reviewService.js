import api from "./api";
import { API_ENDPOINTS } from "../utils/constants";

export const reviewService = {
  async getReviews(params = {}) {
    const { data } = await api.get(API_ENDPOINTS.reviews.root, { params });
    return data;
  },

  async getPropertyReviews(id, params = {}) {
    const { data } = await api.get(
      `${API_ENDPOINTS.reviews.root}/property/${id}`,
      { params },
    );
    return data;
  },

  async getBookingReview(id) {
    const { data } = await api.get(
      `${API_ENDPOINTS.reviews.root}/booking/${id}`,
    );
    return data;
  },

  async getGuestReviews(id, params = {}) {
    const { data } = await api.get(
      `${API_ENDPOINTS.reviews.root}/guest/${id}`,
      { params },
    );
    return data;
  },

  async createReview(payload) {
    const { data } = await api.post(
      `${API_ENDPOINTS.reviews.root}/create`,
      payload,
    );
    return data;
  },

  async updateReview(id, payload) {
    const { data } = await api.put(
      `${API_ENDPOINTS.reviews.root}/update/${id}`,
      payload,
    );
    return data;
  },

  async deleteReview(id) {
    const { data } = await api.delete(
      `${API_ENDPOINTS.reviews.root}/delete/${id}`,
    );
    return data;
  },
};

export default reviewService;
