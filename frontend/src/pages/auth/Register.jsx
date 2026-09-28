import { useState } from "react";
import { useForm } from "react-hook-form";
import { useNavigate } from "react-router-dom";
import axios from "axios";
import AuthLayout, { AUTH_INPUT_CLASS, AuthField } from "./AuthLayout";

const PASSWORD_PATTERN = /^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)(?=.*[@$!%*?&]).{8,}$/;

export default function Register() {
  const navigate = useNavigate();
  const [requestError, setRequestError] = useState("");
  const {
    register,
    handleSubmit,
    formState: { errors, isSubmitting },
  } = useForm();

  const today = new Date().toISOString().slice(0, 10);

  const onSubmit = async (values) => {
    setRequestError("");
    try {
      await axios.post("/api/v1/auth/register", values);
      navigate("/login", {
        replace: true,
        state: { notice: "Your account is ready. Sign in to continue." },
      });
    } catch (error) {
      setRequestError(
        error.response?.data?.message ||
          "Unable to create your account. Please try again.",
      );
    }
  };

  return (
    <AuthLayout
      eyebrow="Join now"
      title="Create your account"
      description="A few details and you are on your way to a better stay."
      alternateText="Already have an account?"
      alternateLabel="Sign in"
      alternateTo="/login"
    >
      <form onSubmit={handleSubmit(onSubmit)} noValidate className="space-y-4">
        <div className="grid gap-4 sm:grid-cols-2">
          <AuthField id="name" label="Full name" error={errors.name?.message}>
            <input
              id="name"
              type="text"
              autoComplete="name"
              aria-invalid={Boolean(errors.name)}
              aria-describedby={errors.name ? "name-error" : undefined}
              {...register("name", {
                required: "Name is required",
                minLength: {
                  value: 5,
                  message: "Name must be between 5 and 50 characters",
                },
                maxLength: {
                  value: 50,
                  message: "Name must be between 5 and 50 characters",
                },
              })}
              className={AUTH_INPUT_CLASS}
            />
          </AuthField>

          <AuthField id="email" label="Email" error={errors.email?.message}>
            <input
              id="email"
              type="email"
              autoComplete="email"
              aria-invalid={Boolean(errors.email)}
              aria-describedby={errors.email ? "email-error" : undefined}
              {...register("email", {
                required: "Email is required",
                pattern: {
                  value: /^[^\s@]+@[^\s@]+\.[^\s@]+$/,
                  message: "Provide a valid email",
                },
              })}
              className={AUTH_INPUT_CLASS}
            />
          </AuthField>

          <AuthField id="gender" label="Gender" error={errors.gender?.message}>
            <select
              id="gender"
              aria-invalid={Boolean(errors.gender)}
              aria-describedby={errors.gender ? "gender-error" : undefined}
              {...register("gender", { required: "Gender is required" })}
              className={AUTH_INPUT_CLASS}
              defaultValue=""
            >
              <option value="" disabled>
                Select gender
              </option>
              <option value="MALE">Male</option>
              <option value="FEMALE">Female</option>
              <option value="OTHER">Other</option>
            </select>
          </AuthField>

          <AuthField
            id="dateOfBirth"
            label="Date of birth"
            error={errors.dateOfBirth?.message}
          >
            <input
              id="dateOfBirth"
              type="date"
              max={today}
              autoComplete="bday"
              aria-invalid={Boolean(errors.dateOfBirth)}
              aria-describedby={
                errors.dateOfBirth ? "dateOfBirth-error" : undefined
              }
              {...register("dateOfBirth", {
                required: "Birthdate is required",
                validate: (value) =>
                  value < today || "Provide a valid birthdate",
              })}
              className={AUTH_INPUT_CLASS}
            />
          </AuthField>

          <AuthField
            id="contactNumber"
            label="Contact number"
            error={errors.contactNumber?.message}
          >
            <div className="flex rounded border border-[var(--color-mocha)] bg-[var(--color-white)] focus-within:border-[var(--color-sun-dark)] focus-within:ring-2 focus-within:ring-[var(--color-sun)]/20">
              <span className="flex items-center border-r border-[var(--color-mocha)] px-3 text-sm text-[var(--color-graph)]">
                +63
              </span>
              <input
                id="contactNumber"
                type="tel"
                inputMode="numeric"
                autoComplete="tel-national"
                placeholder="9XXXXXXXXX"
                aria-invalid={Boolean(errors.contactNumber)}
                aria-describedby={
                  errors.contactNumber ? "contactNumber-error" : undefined
                }
                {...register("contactNumber", {
                  required: "Contact number is required",
                  pattern: {
                    value: /^9\d{9}$/,
                    message: "Invalid phone number format",
                  },
                })}
                className="min-w-0 flex-1 bg-transparent px-3 py-3 text-sm text-[var(--color-bark-dark)] outline-none"
              />
            </div>
          </AuthField>

          <AuthField
            id="role"
            label="Account type"
            error={errors.role?.message}
          >
            <select
              id="role"
              aria-invalid={Boolean(errors.role)}
              aria-describedby={errors.role ? "role-error" : undefined}
              {...register("role", { required: "User Role is required" })}
              className={AUTH_INPUT_CLASS}
              defaultValue=""
            >
              <option value="" disabled>
                Select account type
              </option>
              <option value="GUEST">Guest</option>
              <option value="HOST">Host</option>
            </select>
          </AuthField>
        </div>

        <AuthField id="address" label="Address" error={errors.address?.message}>
          <textarea
            id="address"
            rows="2"
            autoComplete="street-address"
            aria-invalid={Boolean(errors.address)}
            aria-describedby={errors.address ? "address-error" : undefined}
            {...register("address", {
              required: "Address is required",
              minLength: {
                value: 8,
                message: "Address must be between 8 and 80 characters",
              },
              maxLength: {
                value: 80,
                message: "Address must be between 8 and 80 characters",
              },
            })}
            className={`${AUTH_INPUT_CLASS} resize-y`}
          />
        </AuthField>

        <AuthField
          id="password"
          label="Password"
          error={errors.password?.message}
        >
          <input
            id="password"
            type="password"
            autoComplete="new-password"
            aria-invalid={Boolean(errors.password)}
            aria-describedby={errors.password ? "password-error" : undefined}
            {...register("password", {
              required: "Password is required",
              pattern: {
                value: PASSWORD_PATTERN,
                message:
                  "Password must be at least 8 characters and include uppercase, lowercase, number, and special character",
              },
            })}
            className={AUTH_INPUT_CLASS}
          />
        </AuthField>

        {requestError && (
          <p role="alert" className="text-sm text-red-700">
            {requestError}
          </p>
        )}
        <button
          type="submit"
          disabled={isSubmitting}
          className="flex w-full cursor-pointer items-center justify-center rounded bg-[var(--color-sun)] px-5 py-3 text-sm font-bold text-[var(--color-bark-dark)] shadow-[var(--shadow-sm)] hover:bg-[var(--color-sand)] disabled:cursor-wait disabled:opacity-70"
        >
          {isSubmitting ? "Creating account..." : "Create account"}
        </button>
      </form>
    </AuthLayout>
  );
}
