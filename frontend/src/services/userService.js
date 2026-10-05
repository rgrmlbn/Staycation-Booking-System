import api from "./api";
import { API_ENDPOINTS } from "../utils/constants";

export const userService = {
  async getUsers() {
    const { data } = await api.get(API_ENDPOINTS.users.root);
    return data;
  },

  async getCurrentUser() {
    const { data } = await api.get(API_ENDPOINTS.users.current);
    return data;
  },

  async getUser(id) {
    const { data } = await api.get(`${API_ENDPOINTS.users.root}/${id}`);
    return data;
  },

  async updateUser(id, payload) {
    const { data } = await api.patch(
      `${API_ENDPOINTS.users.root}/${id}/update`,
      payload,
    );
    return data;
  },

  async changePassword(id, payload) {
    const { data } = await api.patch(
      `${API_ENDPOINTS.users.root}/${id}/change-password`,
      payload,
    );
    return data;
  },

  async deleteUser(id) {
    const { data } = await api.delete(`${API_ENDPOINTS.users.root}/${id}`);
    return data;
  },
};

export default userService;
