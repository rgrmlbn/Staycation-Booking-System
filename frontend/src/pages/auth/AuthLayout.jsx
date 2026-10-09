import { useState } from "react";
import { FaEye, FaEyeSlash, FaSun } from "react-icons/fa";
import { Link } from "react-router-dom";

export const AUTH_INPUT_CLASS =
  "w-full rounded border-2 border-[var(--color-bark)] bg-[var(--color-white)] px-3.5 py-3 text-sm text-[var(--color-bark-dark)] placeholder:text-[var(--color-graph)]/70 outline-none focus:border-[var(--color-sand)] focus:ring-2 focus:ring-[var(--color-sand)] aria-[invalid=true]:border-red-600 aria-[invalid=true]:focus:border-red-600 aria-[invalid=true]:focus:ring-red-200";

export function AuthField({ id, label, error, children }) {
  return (
    <div className="min-w-0">
      <label
        htmlFor={id}
        className="mb-1.5 block text-sm font-semibold text-[var(--color-bark-dark)]"
      >
        {label}
      </label>
      {children}
      {error && (
        <p id={`${id}-error`} className="mt-1 text-xs text-red-700">
          {error}
        </p>
      )}
    </div>
  );
}

export function PasswordInput({ id, error, className = "", ...props }) {
  const [showPassword, setShowPassword] = useState(false);
  const resolvedClassName = className || AUTH_INPUT_CLASS;
  const describedBy = props["aria-describedby"] ||
    (error ? `${id}-error` : undefined);

  return (
    <div className="relative">
      <input
        {...props}
        id={id}
        type={showPassword ? "text" : "password"}
        aria-invalid={Boolean(error) || props["aria-invalid"]}
        aria-describedby={describedBy}
        className={`${resolvedClassName} pr-11`}
      />
      <button
        type="button"
        aria-label={showPassword ? "Hide password" : "Show password"}
        onClick={() => setShowPassword((visible) => !visible)}
        className="absolute inset-y-0 right-0 flex w-11 items-center justify-center text-[var(--color-graph)] transition-colors hover:text-[var(--color-bark-dark)] focus-visible:outline-2 focus-visible:outline-offset-0 focus-visible:outline-[var(--color-sand)]"
      >
        {showPassword ? (
          <FaEyeSlash aria-hidden="true" size={16} />
        ) : (
          <FaEye aria-hidden="true" size={16} />
        )}
      </button>
    </div>
  );
}

export default function AuthLayout({
  eyebrow,
  title,
  description,
  alternateText,
  alternateLabel,
  alternateTo,
  audienceText,
  audienceLinkText,
  audienceTo,
  children,
}) {
  return (
    <main className="grid min-h-[calc(100svh-5rem)] bg-[var(--color-cream)] lg:grid-cols-[1.1fr_0.9fr]">
      <section className="flex items-center justify-center px-5 py-10 pb-28 sm:px-10 lg:px-14 lg:py-12">
        <div className="w-full max-w-2xl">
          <p className="text-xs font-bold uppercase text-[var(--color-sun-dark)]">
            {eyebrow}
          </p>
          <h1 className="mt-2 text-3xl font-bold leading-tight text-[var(--color-bark-dark)] sm:text-4xl">
            {title}
          </h1>
          <p className="mt-2 text-sm leading-6 text-[var(--color-graph)]">
            {description}
          </p>

          <div className="mt-7">{children}</div>

          <p className="mt-6 text-sm text-[var(--color-graph)]">
            {alternateText}{" "}
            <Link
              to={alternateTo}
              className="font-bold text-[var(--color-bark-dark)] hover:text-[var(--color-sun)]"
            >
              {alternateLabel}
            </Link>
          </p>
          <p className="mt-2 text-sm text-[var(--color-graph)]">
            {audienceText}{" "}
            <Link
              to={audienceTo}
              className="font-bold text-[var(--color-bark-dark)] hover:text-[var(--color-sun)]"
            >
              {audienceLinkText}
            </Link>
          </p>
        </div>
      </section>

      <aside
        className="relative hidden min-h-[calc(100svh-5rem)] overflow-hidden bg-[var(--color-bark-dark)] text-[var(--color-white)] lg:flex lg:items-center lg:px-12 xl:px-20"
        style={{
          backgroundImage:
            "repeating-linear-gradient(135deg, rgba(255,255,255,0.035) 0px, rgba(255,255,255,0.035) 1px, transparent 1px, transparent 16px)",
        }}
      >
        <div className="relative z-10 max-w-lg">
          <div className="mb-6 flex size-12 items-center justify-center rounded bg-[var(--color-sand)] text-[var(--color-bark-dark)]">
            <FaSun aria-hidden="true" size={30} />
          </div>
          <p className="text-xs font-bold uppercase text-[var(--color-sand)]">
            A little time away goes a long way
          </p>
          <h2 className="mt-4 text-4xl font-bold leading-tight xl:text-5xl">
            Make room for a better kind of getaway.
          </h2>
          <p className="mt-5 max-w-sm text-base leading-7 text-white/75">
            Find a stay that fits your plans, whether it is a few hours or a few
            days.
          </p>
        </div>
        <div
          aria-hidden="true"
          className="absolute bottom-10 right-10 flex size-36 items-center justify-center rounded-full border border-white/15 text-white/10 xl:bottom-16 xl:right-16 xl:size-48"
        >
          <FaSun size={100} />
        </div>
      </aside>
    </main>
  );
}
