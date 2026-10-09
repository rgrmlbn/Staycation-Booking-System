import { useState } from "react";
import { useForm } from "react-hook-form";
import { useLocation, useNavigate } from "react-router-dom";
import { FaSignInAlt } from "react-icons/fa";
import AuthLayout, {
  AUTH_INPUT_CLASS,
  AuthField,
  PasswordInput,
} from "./AuthLayout";
import authService from "../../services/authService";
import {
  EMAIL_VALIDATION,
  REQUIRED_VALIDATION,
} from "../../utils/validation";

export default function Login({ audience = "guest" }) {
  const isHost = audience === "host";
  const isAdmin = audience === "admin";
  const navigate = useNavigate();
  const location = useLocation();
  const [requestError, setRequestError] = useState("");
  const {
    register,
    handleSubmit,
    formState: { errors, isSubmitting },
  } = useForm({ mode: "onChange", shouldFocusError: false });

  const onSubmit = async (values) => {
    setRequestError("");
    try {
      const session = await authService.login(values, audience.toUpperCase());
      const destinationByRole = {
        GUEST: "/guest/dashboard",
        HOST: "/host/dashboard",
        ADMIN: "/admin/dashboard",
      };
      navigate(destinationByRole[session.role], {
        replace: true,
      });
    } catch (error) {
      setRequestError(
        error.response?.data?.message || "Unable to sign in. Please try again.",
      );
    }
  };

  return (
    <AuthLayout
      eyebrow={isAdmin ? "Administrator access" : "Welcome back"}
      title={
        isAdmin
          ? "Admin sign in"
          : isHost
            ? "Host sign in"
            : "Guest sign in"
      }
      description={
        isAdmin
          ? "Sign in to manage the Roomance platform."
          : isHost
            ? "Sign in to continue to your host space."
            : "Pick up where your next staycation begins."
      }
      alternateText={
        isAdmin
          ? "Need a guest account?"
          : "Don't have an account yet?"
      }
      alternateLabel={isAdmin ? "Guest sign in" : "Create an account"}
      alternateTo={
        isAdmin ? "/login" : isHost ? "/host/register" : "/register"
      }
      audienceText={
        isAdmin
          ? "Need a host account?"
          : isHost
            ? "Are you a guest?"
            : "Are you a host?"
      }
      audienceLinkText={isAdmin ? "Host sign in" : "Sign in here"}
      audienceTo={isAdmin ? "/host/login" : isHost ? "/login" : "/host/login"}
    >
      {location.state?.notice && (
        <p className="mb-5 rounded border border-[var(--color-palm)]/30 bg-[var(--color-palm)]/10 px-4 py-3 text-sm text-[var(--color-palm-dark)]">
          {location.state.notice}
        </p>
      )}
      <form onSubmit={handleSubmit(onSubmit)} noValidate className="space-y-5">
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

        <AuthField
          id="password"
          label="Password"
          error={errors.password?.message}
        >
          <PasswordInput
            id="password"
            placeholder="Enter your password"
            autoComplete="current-password"
            error={errors.password?.message}
            {...register("password", REQUIRED_VALIDATION("Password"))}
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
          {isSubmitting ? "Signing in..." : "Sign in"}
          <FaSignInAlt aria-hidden="true" size={18} />
        </button>
      </form>
    </AuthLayout>
  );
}
