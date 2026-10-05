import { useState } from "react";
import { useForm } from "react-hook-form";
import { useNavigate } from "react-router-dom";
import { FaUserPlus } from "react-icons/fa";
import AuthLayout, { AUTH_INPUT_CLASS, AuthField } from "./AuthLayout";
import authService from "../../services/authService";
import {
  ADDRESS_VALIDATION,
  CONTACT_NUMBER_VALIDATION,
  DATE_OF_BIRTH_VALIDATION,
  EMAIL_VALIDATION,
  NAME_VALIDATION,
  PASSWORD_VALIDATION,
  REQUIRED_VALIDATION,
} from "../../utils/validation";

export default function Register({ accountType = "GUEST" }) {
  const isHost = accountType === "HOST";
  const loginPath = isHost ? "/host/login" : "/login";
  const navigate = useNavigate();
  const [requestError, setRequestError] = useState("");
  const {
    register,
    handleSubmit,
    formState: { errors, isSubmitting },
  } = useForm({ mode: "onChange", shouldFocusError: false });

  const today = new Date().toISOString().slice(0, 10);

  const onSubmit = async (values) => {
    setRequestError("");
    try {
      await authService.register(values);
      navigate(loginPath, {
        replace: true,
        state: {
          notice: `Your ${isHost ? "host " : ""}account is ready. Sign in to continue.`,
        },
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
      eyebrow={isHost ? "Host account" : "Join now"}
      title={isHost ? "Create your host account" : "Create your account"}
      description={
        isHost
          ? "Set up your host account and get ready to welcome your first guest."
          : "A few details and you are on your way to a better stay."
      }
      alternateText="Already have an account?"
      alternateLabel="Sign in"
      alternateTo={loginPath}
      audienceText={isHost ? "Are you a guest?" : "Are you a host?"}
      audienceLinkText="Register here"
      audienceTo={isHost ? "/register" : "/host/register"}
    >
      <form onSubmit={handleSubmit(onSubmit)} noValidate className="space-y-4">
        <div className="grid gap-4 sm:grid-cols-2">
          <AuthField id="name" label="Full name" error={errors.name?.message}>
            <input
              id="name"
              type="text"
              placeholder="Enter your full name"
              autoComplete="name"
              aria-invalid={Boolean(errors.name)}
              aria-describedby={errors.name ? "name-error" : undefined}
              {...register("name", NAME_VALIDATION)}
              className={AUTH_INPUT_CLASS}
            />
          </AuthField>

          <AuthField id="email" label="Email" error={errors.email?.message}>
            <input
              id="email"
              type="email"
              placeholder="you@example.com"
              autoComplete="email"
              aria-invalid={Boolean(errors.email)}
              aria-describedby={errors.email ? "email-error" : undefined}
              {...register("email", EMAIL_VALIDATION)}
              className={AUTH_INPUT_CLASS}
            />
          </AuthField>

          <AuthField id="gender" label="Gender" error={errors.gender?.message}>
            <select
              id="gender"
              aria-invalid={Boolean(errors.gender)}
              aria-describedby={errors.gender ? "gender-error" : undefined}
              {...register("gender", REQUIRED_VALIDATION("Gender"))}
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
              {...register(
                "dateOfBirth",
                DATE_OF_BIRTH_VALIDATION(today),
              )}
              className={AUTH_INPUT_CLASS}
            />
          </AuthField>

          <AuthField
            id="contactNumber"
            label="Contact number"
            error={errors.contactNumber?.message}
          >
            <div
              className={`flex rounded border-2 bg-[var(--color-white)] focus-within:ring-2 ${
                errors.contactNumber
                  ? "border-red-600 focus-within:border-red-600 focus-within:ring-red-200"
                  : "border-[var(--color-bark)] focus-within:border-[var(--color-sand)] focus-within:ring-[var(--color-sand)]"
              }`}
            >
              <span className="flex items-center border-r border-inherit px-3 text-sm text-[var(--color-graph)]">
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
                {...register("contactNumber", CONTACT_NUMBER_VALIDATION)}
                className="min-w-0 flex-1 bg-transparent px-3 py-3 text-sm text-[var(--color-bark-dark)] placeholder:text-[var(--color-graph)]/70 outline-none"
              />
            </div>
          </AuthField>

          <AuthField
            id="role"
            label="Account type"
            error={errors.role?.message}
          >
            <input
              type="hidden"
              {...register("role", {
                ...REQUIRED_VALIDATION("User Role"),
                value: accountType,
              })}
            />
            <select
              id="role"
              disabled={Boolean(accountType)}
              aria-invalid={Boolean(errors.role)}
              aria-describedby={errors.role ? "role-error" : undefined}
              className={`${AUTH_INPUT_CLASS} disabled:cursor-not-allowed disabled:opacity-60`}
              defaultValue={accountType}
            >
              <option value={accountType}>{isHost ? "Host" : "Guest"}</option>
            </select>
          </AuthField>
        </div>

        <AuthField id="address" label="Address" error={errors.address?.message}>
          <textarea
            id="address"
            rows="2"
            autoComplete="street-address"
            placeholder="Street, city, province"
            aria-invalid={Boolean(errors.address)}
            aria-describedby={errors.address ? "address-error" : undefined}
            {...register("address", ADDRESS_VALIDATION)}
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
            placeholder="Enter a password"
            autoComplete="new-password"
            aria-invalid={Boolean(errors.password)}
            aria-describedby={errors.password ? "password-error" : undefined}
            {...register("password", PASSWORD_VALIDATION)}
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
          className="flex w-full cursor-pointer items-center justify-center gap-2 rounded bg-[var(--color-sand)] px-5 py-3 text-sm font-bold text-[var(--color-bark-dark)] shadow-[var(--shadow-sm)]"
        >
          
          {isSubmitting ? "Creating account..." : "Create account"}
          <FaUserPlus aria-hidden="true" size={18} />
        </button>
      </form>
    </AuthLayout>
  );
}
