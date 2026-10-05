import { useState } from "react";
import { useForm } from "react-hook-form";
import { useAuth } from "../../hooks/useAuth";
import { useChangePassword } from "../../hooks/useUsers";
import { PASSWORD_VALIDATION } from "../../utils/validation";

const INPUT_CLASS =
  "w-full rounded border-2 border-[var(--color-bark)] bg-white px-3.5 py-3 text-sm text-[var(--color-bark-dark)] outline-none focus:border-[var(--color-sand)] focus:ring-2 focus:ring-[var(--color-sand)] aria-[invalid=true]:border-red-600";

export default function Settings() {
  const { user } = useAuth();
  const changePassword = useChangePassword();
  const [requestError, setRequestError] = useState("");
  const [success, setSuccess] = useState("");
  const {
    register,
    handleSubmit,
    reset,
    formState: { errors, isSubmitting },
  } = useForm();

  const onSubmit = async (values) => {
    setRequestError("");
    setSuccess("");
    try {
      await changePassword.mutateAsync({ id: user.id, ...values });
      reset();
      setSuccess("Your password has been changed.");
    } catch (error) {
      setRequestError(
        error.response?.data?.message ||
          "Unable to change your password. Please try again.",
      );
    }
  };

  return (
    <main className="container min-h-[60vh] py-12 pb-28 md:py-16 md:pb-16">
      <p className="text-xs font-bold uppercase text-[var(--color-sun-dark)]">
        Account
      </p>
      <h1 className="mt-3 text-4xl font-bold text-[var(--color-bark-dark)]">
        Settings
      </h1>
      <p className="mt-3 text-base leading-7 text-[var(--color-graph)]">
        Keep your account secure by updating your password.
      </p>

      <form
        onSubmit={handleSubmit(onSubmit)}
        noValidate
        className="mt-8 grid max-w-xl gap-5"
      >
        {[
          {
            id: "currentPassword",
            label: "Current password",
            autoComplete: "current-password",
            rules: { required: "Current password is required" },
          },
          {
            id: "newPassword",
            label: "New password",
            autoComplete: "new-password",
            rules: PASSWORD_VALIDATION,
          },
          {
            id: "confirmPassword",
            label: "Confirm new password",
            autoComplete: "new-password",
            rules: {
              required: "Please confirm your new password",
              validate: (value, formValues) =>
                value === formValues.newPassword || "Passwords do not match",
            },
          },
        ].map(({ id, label, autoComplete, rules }) => (
          <div key={id}>
            <label
              htmlFor={id}
              className="mb-1.5 block text-sm font-semibold text-[var(--color-bark-dark)]"
            >
              {label}
            </label>
            <input
              id={id}
              type="password"
              autoComplete={autoComplete}
              {...register(id, rules)}
              aria-invalid={Boolean(errors[id])}
              className={INPUT_CLASS}
            />
            {errors[id] && (
              <p className="mt-1 text-xs text-red-700" role="alert">
                {errors[id].message}
              </p>
            )}
          </div>
        ))}

        {requestError && (
          <p className="text-sm text-red-700" role="alert">
            {requestError}
          </p>
        )}
        {success && (
          <p className="text-sm text-green-800" role="status">
            {success}
          </p>
        )}
        <button
          type="submit"
          disabled={isSubmitting || changePassword.isPending}
          className="min-h-12 rounded bg-[var(--color-sand)] px-5 py-3 text-sm font-bold text-[var(--color-bark-dark)] shadow-[var(--shadow-sm)] disabled:opacity-60 sm:justify-self-start"
        >
          {isSubmitting || changePassword.isPending
            ? "Updating..."
            : "Change password"}
        </button>
      </form>
    </main>
  );
}
