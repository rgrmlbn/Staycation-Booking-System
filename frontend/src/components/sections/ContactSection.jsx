import { FaEnvelope, FaMapMarkerAlt, FaPhoneAlt } from "react-icons/fa";
import { FaMessage } from "react-icons/fa6";
import { useForm } from "react-hook-form";
import { AUTH_INPUT_CLASS, AuthField } from "../../pages/auth/AuthLayout";
import {
  EMAIL_VALIDATION,
  MESSAGE_VALIDATION,
  REQUIRED_VALIDATION,
} from "../../utils/validation";

export default function ContactSection({
  eyebrow = "Contact",
  title = "Let’s help plan your next stay.",
  description = "Have a question about Roomance or need a hand with your plans? Get in touch with our team.",
}) {
  const {
    register,
    handleSubmit,
    formState: { errors },
  } = useForm({ mode: "onChange" });

  const onSubmit = ({ name, email, message }) => {
    const emailSubject = encodeURIComponent(`Contact request from ${name}`);
    const emailBody = encodeURIComponent(
      `Name: ${name}\nEmail: ${email}\n\n${message}`,
    );

    window.location.assign(
      `mailto:roomance@gmail.com?subject=${emailSubject}&body=${emailBody}`,
    );
  };

  return (
    <section
      id="contact"
      className="scroll-mt-24 border-t border-[var(--color-bark)]/10 bg-[var(--color-white)] py-16 pb-28 text-[var(--color-bark-dark)] md:py-20 md:pb-20"
    >
      <div className="container">
        <div className="max-w-2xl">
          <p className="text-xs font-bold uppercase text-[var(--color-sun-dark)]">
            {eyebrow}
          </p>
          <h2 className="mt-3 text-4xl font-bold leading-tight sm:text-5xl">
            {title}
          </h2>
          <p className="mt-4 max-w-xl text-lg leading-8 text-[var(--color-graph)]">
            {description}
          </p>
        </div>

        <div className="mt-8 grid gap-8 md:grid-cols-[0.8fr_1.2fr] md:gap-10">
          <div>
            <div className="flex flex-col gap-5">
              <a
                href="tel:+63281234567"
                className="inline-flex items-center gap-3 text-base font-semibold text-[var(--color-bark-dark)] hover:text-[var(--color-sand)] md:text-lg"
              >
                <FaPhoneAlt
                  aria-hidden="true"
                  size={18}
                  className="shrink-0 text-lg text-[var(--color-sun)]"
                />
                +63 (2) 8123-4567
              </a>
              <a
                href="mailto:roomance@gmail.com"
                className="inline-flex items-center gap-3 text-base font-semibold text-[var(--color-bark-dark)] hover:text-[var(--color-sand)] md:text-lg"
              >
                <FaEnvelope
                  aria-hidden="true"
                  size={18}
                  className="shrink-0 text-lg text-[var(--color-sun)]"
                />
                roomance@gmail.com
              </a>
              <div className="inline-flex items-center gap-3 text-base font-semibold text-[var(--color-bark-dark)] hover:text-[var(--color-sand)] md:text-lg">
                <FaMapMarkerAlt
                  aria-hidden="true"
                  size={18}
                  className="shrink-0 text-lg text-[var(--color-sun)]"
                />
                Makati City, Philippines
              </div>
            </div>
          </div>

          <form
            onSubmit={handleSubmit(onSubmit)}
            noValidate
            className="space-y-5"
          >
            <div className="grid gap-4 sm:grid-cols-2">
              <AuthField
                id="contact-name"
                label="Name"
                error={errors.name?.message}
              >
                <input
                  id="contact-name"
                  type="text"
                  autoComplete="name"
                  placeholder="Enter your full name"
                  aria-invalid={Boolean(errors.name)}
                  aria-describedby={
                    errors.name ? "contact-name-error" : undefined
                  }
                  {...register("name", REQUIRED_VALIDATION("Name"))}
                  className={AUTH_INPUT_CLASS}
                />
              </AuthField>
              <AuthField
                id="contact-email"
                label="Email"
                error={errors.email?.message}
              >
                <input
                  id="contact-email"
                  type="email"
                  placeholder="you@example.com"
                  autoComplete="email"
                  aria-invalid={Boolean(errors.email)}
                  aria-describedby={
                    errors.email ? "contact-email-error" : undefined
                  }
                  {...register("email", EMAIL_VALIDATION)}
                  className={AUTH_INPUT_CLASS}
                />
              </AuthField>
            </div>
            <AuthField
              id="contact-message"
              label="Message"
              error={errors.message?.message}
            >
              <textarea
                id="contact-message"
                rows="6"
                placeholder="Send us a message"
                aria-invalid={Boolean(errors.message)}
                aria-describedby={
                  errors.message ? "contact-message-error" : undefined
                }
                {...register("message", MESSAGE_VALIDATION)}
                className={`${AUTH_INPUT_CLASS} resize-y`}
              />
            </AuthField>
            <div className="flex flex-col items-start gap-3 sm:flex-row sm:items-center sm:justify-between">
              <p className="text-xs leading-5 text-[var(--color-graph)]">
                Submitting opens your email app with the message ready to send.
              </p>
              <button
                type="submit"
                className="flex shrink-0 cursor-pointer flex items-center justify-center gap-2 rounded bg-[var(--color-sand)] px-5 py-3 text-sm font-bold text-[var(--color-bark-dark)] shadow-[var(--shadow-sm)]"
              >
                Send Message
                <FaMessage aria-hidden="true" size={16} />
              </button>
            </div>
          </form>
        </div>
      </div>
    </section>
  );
}
