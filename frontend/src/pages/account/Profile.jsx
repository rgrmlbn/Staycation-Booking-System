import { useEffect, useState } from "react";
import { useForm } from "react-hook-form";
import { useQueryClient } from "@tanstack/react-query";
import { useAuth } from "../../hooks/useAuth";
import { useUpdateUser } from "../../hooks/useUsers";
import { QUERY_KEYS } from "../../utils/constants";

const INPUT_CLASS =
  "w-full rounded border-2 border-[var(--color-bark)] bg-white px-3.5 py-3 text-sm text-[var(--color-bark-dark)] outline-none focus:border-[var(--color-sand)] focus:ring-2 focus:ring-[var(--color-sand)] aria-[invalid=true]:border-red-600";

function Field({ label, name, register, error, rules, ...inputProps }) {
  return (
    <div>
      <label
        htmlFor={name}
        className="mb-1.5 block text-sm font-semibold text-[var(--color-bark-dark)]"
      >
        {label}
      </label>
      <input
        id={name}
        {...inputProps}
        {...register(name, rules)}
        aria-invalid={Boolean(error)}
        className={INPUT_CLASS}
      />
      {error && (
        <p className="mt-1 text-xs text-red-700" role="alert">
          {error.message}
        </p>
      )}
    </div>
  );
}

export default function Profile() {
  const { user, isLoading } = useAuth();
  const queryClient = useQueryClient();
  const updateUser = useUpdateUser();
  const [requestError, setRequestError] = useState("");
  const [success, setSuccess] = useState("");
  const {
    register,
    handleSubmit,
    reset,
    formState: { errors, isSubmitting },
  } = useForm();

  useEffect(() => {
    if (user) {
      reset({
        name: user.name || "",
        email: user.email || "",
        contactNumber: user.contactNumber || "",
        address: user.address || "",
      });
    }
  }, [user, reset]);

  const onSubmit = async (values) => {
    setRequestError("");
    setSuccess("");
    try {
      const updatedUser = await updateUser.mutateAsync({
        id: user.id,
        ...values,
      });
      queryClient.setQueryData(QUERY_KEYS.currentUser, updatedUser);
      setSuccess("Your profile has been updated.");
    } catch (error) {
      setRequestError(
        error.response?.data?.message ||
          "Unable to update your profile. Please try again.",
      );
    }
  };

  if (isLoading || !user) {
    return (
      <main className="container py-16" role="status">
        Loading your profile...
      </main>
    );
  }

  return (
    <main className="container min-h-[60vh] py-12 pb-28 md:py-16 md:pb-16">
      <p className="text-xs font-bold uppercase text-[var(--color-sun-dark)]">
        Account
      </p>
      <h1 className="mt-3 text-4xl font-bold text-[var(--color-bark-dark)]">
        Edit profile
      </h1>
      <p className="mt-3 text-base leading-7 text-[var(--color-graph)]">
        Update the contact details associated with your account.
      </p>

      <form
        onSubmit={handleSubmit(onSubmit)}
        noValidate
        className="mt-8 grid max-w-2xl gap-5 sm:grid-cols-2"
      >
        <Field
          label="Full name"
          name="name"
          type="text"
          autoComplete="name"
          maxLength={50}
          register={register}
          error={errors.name}
          rules={{
            required: "Name is required",
            minLength: { value: 5, message: "Name must be at least 5 characters" },
            maxLength: { value: 50, message: "Name must be no more than 50 characters" },
          }}
        />
        <Field
          label="Email"
          name="email"
          type="email"
          autoComplete="email"
          register={register}
          error={errors.email}
          rules={{
            required: "Email is required",
            pattern: {
              value: /^[^\s@]+@[^\s@]+\.[^\s@]+$/,
              message: "Provide a valid email",
            },
          }}
        />
        <Field
          label="Contact number"
          name="contactNumber"
          type="tel"
          autoComplete="tel"
          placeholder="e.g. +639123456789"
          register={register}
          error={errors.contactNumber}
          rules={{
            required: "Contact number is required",
            pattern: {
              value: /^\+?[1-9]\d{1,14}$/,
              message: "Enter a valid international phone number",
            },
          }}
        />
        <div className="sm:col-span-2">
          <label
            htmlFor="address"
            className="mb-1.5 block text-sm font-semibold text-[var(--color-bark-dark)]"
          >
            Address
          </label>
          <textarea
            id="address"
            rows={3}
            autoComplete="street-address"
            {...register("address", {
              required: "Address is required",
              minLength: {
                value: 8,
                message: "Address must be at least 8 characters",
              },
              maxLength: {
                value: 80,
                message: "Address must be no more than 80 characters",
              },
            })}
            aria-invalid={Boolean(errors.address)}
            className={`${INPUT_CLASS} resize-y`}
          />
          {errors.address && (
            <p className="mt-1 text-xs text-red-700" role="alert">
              {errors.address.message}
            </p>
          )}
        </div>

        {requestError && (
          <p className="text-sm text-red-700 sm:col-span-2" role="alert">
            {requestError}
          </p>
        )}
        {success && (
          <p className="text-sm text-green-800 sm:col-span-2" role="status">
            {success}
          </p>
        )}
        <button
          type="submit"
          disabled={isSubmitting || updateUser.isPending}
          className="min-h-12 rounded bg-[var(--color-sand)] px-5 py-3 text-sm font-bold text-[var(--color-bark-dark)] shadow-[var(--shadow-sm)] disabled:opacity-60 sm:col-span-2 sm:justify-self-start"
        >
          {isSubmitting || updateUser.isPending ? "Saving..." : "Save profile"}
        </button>
      </form>
    </main>
  );
}
