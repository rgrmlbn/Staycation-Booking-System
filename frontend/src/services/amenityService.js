import api from "./api";
import { API_ENDPOINTS } from "../utils/constants";

export const amenityService = {
  async getAmenities() {
    const { data } = await api.get(API_ENDPOINTS.amenities.root);
    return data;
  },

  async getAmenity(id) {
    const { data } = await api.get(`${API_ENDPOINTS.amenities.root}/${id}`);
    return data;
  },

  async createAmenity(payload) {
    const { data } = await api.post(
      API_ENDPOINTS.amenities.create,
      payload,
    );
    return data;
  },

  async updateAmenity(id, payload) {
    const { data } = await api.patch(
      `${API_ENDPOINTS.amenities.update}/${id}`,
      payload,
    );
    return data;
  },

  async deleteAmenity(id) {
    const { data } = await api.delete(
      `${API_ENDPOINTS.amenities.delete}/${id}`,
    );
    return data;
  },
};

export default amenityService;
