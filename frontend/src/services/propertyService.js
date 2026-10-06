import axios from "axios";
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

  async uploadPropertyImage(file) {
    const { data: signature } = await api.post(
      API_ENDPOINTS.properties.imageUploadSignature,
    );
    const uploadData = new FormData();
    uploadData.append("file", file);
    uploadData.append("api_key", signature.apiKey);
    uploadData.append("timestamp", signature.timestamp);
    uploadData.append("folder", signature.folder);
    uploadData.append("signature", signature.signature);

    const { data } = await axios.post(
      `https://api.cloudinary.com/v1_1/${encodeURIComponent(signature.cloudName)}/image/upload`,
      uploadData,
    );
    if (!data.secure_url) {
      throw new Error("Cloudinary did not return a secure image URL.");
    }
    return data.secure_url;
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
