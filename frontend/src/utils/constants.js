export const API_ENDPOINTS = {
  auth: {
    register: "/auth/register",
    login: "/auth/login",
    refresh: "/auth/refresh",
    logout: "/auth/logout",
  },
  properties: {
    summary: "/properties/summary",
    detailed: "/properties/detailed",
    byStatus: "/properties/detailed/status",
    mine: "/properties/my/detailed",
    create: "/properties/create",
    imageUploadSignature: "/properties/images/signature",
    update: "/properties/update",
    delete: "/properties/delete",
  },
  bookings: {
    root: "/bookings",
    guest: "/bookings/guest",
    host: "/bookings/host",
    create: "/bookings/create",
  },
  reviews: {
    root: "/reviews",
  },
  amenities: {
    root: "/amenities",
    create: "/amenities/create",
    update: "/amenities/update",
    delete: "/amenities/delete",
  },
  users: {
    root: "/users",
    current: "/users/current-user",
  },
};

export const STORAGE_KEYS = {
  accessToken: "accessToken",
  refreshToken: "refreshToken",
};

export const USER_ROLES = {
  guest: "GUEST",
  host: "HOST",
  admin: "ADMIN",
};

export const PROPERTY_STATUSES = {
  available: "AVAILABLE",
  removed: "REMOVED",
};

export const BOOKING_STATUSES = {
  pending: "PENDING",
  confirmed: "CONFIRMED",
  rejected: "REJECTED",
  cancelled: "CANCELLED",
  completed: "COMPLETED",
};

export const DEFAULT_PAGE = 0;
export const DEFAULT_PAGE_SIZE = 4;

export const QUERY_KEYS = {
  currentUser: ["current-user"],
  users: ["users"],
  properties: ["properties"],
  bookings: ["bookings"],
  amenities: ["amenities"],
};
