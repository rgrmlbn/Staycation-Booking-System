import axios from "axios";
import { STORAGE_KEYS } from "../utils/constants";

export const api = axios.create({
  baseURL: import.meta.env.VITE_API_URL || "/api/v1",
  headers: {
    "Content-Type": "application/json",
  },
});

api.interceptors.request.use((config) => {
  const token = sessionStorage.getItem(STORAGE_KEYS.accessToken);
  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }
  return config;
});

let refreshPromise;

api.interceptors.response.use(
  (response) => response,
  async (error) => {
    const request = error.config;
    const isAuthRequest = request?.url?.includes("/auth/");

    if (
      error.response?.status !== 401 ||
      !request ||
      request._retry ||
      isAuthRequest
    ) {
      return Promise.reject(error);
    }

    const refreshToken = sessionStorage.getItem(STORAGE_KEYS.refreshToken);
    if (!refreshToken) {
      return Promise.reject(error);
    }

    request._retry = true;
    try {
      refreshPromise ??= axios
        .post(`${api.defaults.baseURL}/auth/refresh`, { refreshToken })
        .then(({ data }) => {
          sessionStorage.setItem(STORAGE_KEYS.accessToken, data.accessToken);
          sessionStorage.setItem(STORAGE_KEYS.refreshToken, data.refreshToken);
          return data.accessToken;
        })
        .finally(() => {
          refreshPromise = undefined;
        });

      const accessToken = await refreshPromise;
      request.headers.Authorization = `Bearer ${accessToken}`;
      return api(request);
    } catch (refreshError) {
      sessionStorage.removeItem(STORAGE_KEYS.accessToken);
      sessionStorage.removeItem(STORAGE_KEYS.refreshToken);
      return Promise.reject(refreshError);
    }
  },
);

export default api;
