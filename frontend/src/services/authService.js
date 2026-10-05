import api from "./api";
import {
  API_ENDPOINTS,
  STORAGE_KEYS,
  USER_ROLES,
} from "../utils/constants";

function saveTokens(tokens) {
  sessionStorage.setItem(STORAGE_KEYS.accessToken, tokens.accessToken);
  sessionStorage.setItem(STORAGE_KEYS.refreshToken, tokens.refreshToken);
}

function clearTokens() {
  sessionStorage.removeItem(STORAGE_KEYS.accessToken);
  sessionStorage.removeItem(STORAGE_KEYS.refreshToken);
}

export const authService = {
  async register(payload) {
    const { data } = await api.post(API_ENDPOINTS.auth.register, payload);
    return data;
  },

  async login(credentials, audience) {
    const payload = audience ? { ...credentials, audience } : credentials;
    const { data } = await api.post(API_ENDPOINTS.auth.login, payload);
    if (!Object.values(USER_ROLES).includes(data.role)) {
      throw new Error("The server returned an invalid account role.");
    }
    saveTokens(data);
    return data;
  },

  async refresh() {
    const refreshToken = sessionStorage.getItem(STORAGE_KEYS.refreshToken);
    if (!refreshToken) {
      throw new Error("A refresh token is required.");
    }

    const { data } = await api.post(API_ENDPOINTS.auth.refresh, {
      refreshToken,
    });
    saveTokens(data);
    return data;
  },

  async logout() {
    try {
      const { data } = await api.post(API_ENDPOINTS.auth.logout);
      return data;
    } finally {
      clearTokens();
    }
  },
};

export default authService;
