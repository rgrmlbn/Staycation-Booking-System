import api from "./api";
import { API_ENDPOINTS } from "../utils/constants";

export const propertyService = {
  async getSummaryProperties(params = {}) {
    const { data } = await api.get(API_ENDPOINTS.properties.summary, { params });
    return data;
  },

  async getProperties(params = {}) {
    const { data } = await api.get(API_ENDPOINTS.properties.detailed, { params });
    return data;
  },

  async getPropertiesByStatus(params = {}) {
    const { data } = await api.get(API_ENDPOINTS.properties.byStatus, { params });
    return data;
  },

  async getProperty(id) {
    const { data } = await api.get(`${API_ENDPOINTS.properties.detailed}/${id}`);
    return data;
  },

  async getMyProperties(params = {}) {
    const { data } = await api.get(API_ENDPOINTS.properties.mine, { params });
    return data;
  },

  async createProperty(payload) {
    const { data } = await api.post(API_ENDPOINTS.properties.create, payload);
    return data;
  },

  async updateProperty(id, payload) {
    const { data } = await api.patch(
      `${API_ENDPOINTS.properties.update}/${id}`,
      payload,
    );
    return data;
  },

  async deleteProperty(id) {
    const { data } = await api.delete(
      `${API_ENDPOINTS.properties.delete}/${id}`,
    );
    return data;
  },
};

export default propertyService;
