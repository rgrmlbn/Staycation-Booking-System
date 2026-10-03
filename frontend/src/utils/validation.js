const EMAIL_PATTERN = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
const PASSWORD_PATTERN =
  /^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)(?=.*[@$!%*?&]).{8,}$/;

export const EMAIL_VALIDATION = {
  required: "Email is required",
  pattern: {
    value: EMAIL_PATTERN,
    message: "Provide a valid email",
  },
};

export const NAME_VALIDATION = {
  required: "Name is required",
  minLength: {
    value: 5,
    message: "Name must be between 5 and 50 characters",
  },
  maxLength: {
    value: 50,
    message: "Name must be between 5 and 50 characters",
  },
};

export const DATE_OF_BIRTH_VALIDATION = (today) => ({
  required: "Birthdate is required",
  validate: (value) => value < today || "Provide a valid birthdate",
});

export const CONTACT_NUMBER_VALIDATION = {
  required: "Contact number is required",
  pattern: {
    value: /^9\d{9}$/,
    message: "Invalid phone number format",
  },
};

export const ADDRESS_VALIDATION = {
  required: "Address is required",
  minLength: {
    value: 8,
    message: "Address must be between 8 and 80 characters",
  },
  maxLength: {
    value: 80,
    message: "Address must be between 8 and 80 characters",
  },
};

export const PASSWORD_VALIDATION = {
  required: "Password is required",
  pattern: {
    value: PASSWORD_PATTERN,
    message:
      "Password must be at least 8 characters and include uppercase, lowercase, number, and special character",
  },
};

export const REQUIRED_VALIDATION = (label) => ({
  required: `${label} is required`,
});

export const MESSAGE_VALIDATION = {
  required: "Message is required",
  minLength: {
    value: 10,
    message: "Message must be at least 10 characters",
  },
};

export const GUEST_COUNT_VALIDATION = {
  valueAsNumber: true,
  min: {
    value: 1,
    message: "At least 1 guest is required",
  },
  max: {
    value: 6,
    message: "No more than 6 guests",
  },
};

export const ROOM_COUNT_VALIDATION = {
  valueAsNumber: true,
  min: {
    value: 1,
    message: "At least 1 room is required",
  },
  max: {
    value: 4,
    message: "No more than 4 rooms",
  },
};