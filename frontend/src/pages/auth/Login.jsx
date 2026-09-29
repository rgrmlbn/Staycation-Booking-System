import { useState } from "react";
import { useForm } from "react-hook-form";
import { useLocation, useNavigate } from "react-router-dom";
import axios from "axios";
import AuthLayout, { AUTH_INPUT_CLASS, AuthField } from "./AuthLayout";

export default function Login({ audience = "guest" }) {
  const isHost = audience === "host";
  const navigate = useNavigate();
  const location = useLocation();
  const [requestError, setRequestError] = useState("");
  const {
    register,
    handleSubmit,
    formState: { errors, isSubmitting },
  } = useForm();

  const onSubmit = async (values) => {
    setRequestError("");
    try {
      const { data } = await axios.post("/api/v1/auth/login", values);
      sessionStorage.setItem("accessToken", data.accessToken);
      sessionStorage.setItem("refreshToken", data.refreshToken);
      navigate(isHost ? "/host" : "/", { replace: true });
    } catch (error) {
      setRequestError(
        error.response?.data?.message || "Unable to sign in. Please try again.",
      );
    }
  };

  return (
    <AuthLayout
      eyebrow={isHost ? "Host access" : "Welcome back"}
      title={isHost ? "Host sign in" : "Sign in"}
      description={
        isHost
          ? "Sign in to continue to your host space."
          : "Pick up where your next staycation begins."
      }
      alternateText="Don't have an account yet?"
      alternateLabel="Create an account"
      alternateTo={isHost ? "/host/register" : "/register"}
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

        <AuthField
          id="password"
          label="Password"
          error={errors.password?.message}
        >
          <input
            id="password"
            type="password"
            placeholder="Enter your password"
            autoComplete="current-password"
            aria-invalid={Boolean(errors.password)}
            aria-describedby={errors.password ? "password-error" : undefined}
            {...register("password", { required: "Password is required" })}
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
          className="flex w-full cursor-pointer items-center justify-center rounded bg-[var(--color-sand)] px-5 py-3 text-sm font-bold text-[var(--color-bark-dark)] shadow-[var(--shadow-sm)]"
        >
          {isSubmitting ? "Signing in..." : "Sign in"}
        </button>
      </form>
    </AuthLayout>
  );
}
