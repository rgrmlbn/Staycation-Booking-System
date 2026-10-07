import { useState } from "react";
import { useForm } from "react-hook-form";
import { useQuery } from "@tanstack/react-query";
import { FaHeart, FaHome, FaPlus, FaTimes, FaUsers } from "react-icons/fa";
import amenityService from "../../services/amenityService";
import propertyService from "../../services/propertyService";
import PropertyCard from "../../components/property/PropertyCard";
import ContactSection from "../../components/sections/ContactSection";
import { AUTH_INPUT_CLASS, AuthField } from "../auth/AuthLayout";
import {
  useCreateProperty,
  useMyProperties,
  useUpdateProperty,
} from "../../hooks/useProperties";
import { QUERY_KEYS } from "../../utils/constants";
import { PROPERTY_VALIDATION } from "../../utils/validation";

const PAGE_SIZE = 12;

function getErrorMessage(error) {
  return (
    error?.response?.data?.message ||
    error?.response?.data?.detail ||
    error?.message ||
    "Something went wrong. Please try again."
  );
}

export default function HostDashboard() {
  const [page, setPage] = useState(0);
  const [isCreating, setIsCreating] = useState(false);
  const [editingProperty, setEditingProperty] = useState(null);
  const [successMessage, setSuccessMessage] = useState("");
  const [formError, setFormError] = useState("");
  const [isUploadingImages, setIsUploadingImages] = useState(false);
  const {
    register,
    handleSubmit,
    reset,
    setError,
    trigger,
    formState: { errors },
  } = useForm({ mode: "onChange", shouldFocusError: false });
  const {
    data: propertiesPage,
    error: propertiesError,
    isPending: isLoadingProperties,
  } = useMyProperties({ page, size: PAGE_SIZE });
  const {
    data: amenities = [],
    error: amenitiesError,
    isPending: isLoadingAmenities,
  } = useQuery({
    queryKey: QUERY_KEYS.amenities,
    queryFn: amenityService.getAmenities,
    enabled: isCreating,
  });
  const createProperty = useCreateProperty();
  const updateProperty = useUpdateProperty();
  const properties = propertiesPage?.content;

  const handleCreateProperty = async (values) => {
    const amenityIds = (values.amenityIds ?? []).map(Number);
    if (amenityIds.length === 0) {
      setError("amenityIds", {
        type: "required",
        message: "Select at least one amenity",
      });
      return;
    }
    const imageFiles = Array.from(values.imageFiles ?? []);

    setFormError("");
    setIsUploadingImages(true);
    try {
      const uploadedImageUrls = await Promise.all(
        imageFiles.map((file) => propertyService.uploadPropertyImage(file)),
      );
      const propertyData = {
        title: values.title.trim(),
        description: values.description.trim(),
        bedrooms: Number(values.bedrooms),
        bathrooms: Number(values.bathrooms),
        maxGuests: Number(values.maxGuests),
        address: values.address.trim(),
        amenityIds,
      };
      if (uploadedImageUrls.length > 0) {
        propertyData.imageUrls = uploadedImageUrls;
      }
      const mutation = editingProperty
        ? updateProperty.mutate
        : createProperty.mutate;
      const payload = editingProperty
        ? { id: editingProperty.id, ...propertyData }
        : {
            ...propertyData,
            checkInSlots: [
              {
                startTime: values.startTime,
                durationHours: values.durationHours,
                price: values.price,
              },
            ],
          };

      mutation(payload, {
        onSuccess: () => {
          reset();
          setPage(0);
          setIsCreating(false);
          setEditingProperty(null);
          setSuccessMessage(
            editingProperty
              ? "Your property has been updated."
              : "Your property has been created.",
          );
        },
      });
    } catch (error) {
      setFormError(`Could not upload property images: ${getErrorMessage(error)}`);
    } finally {
      setIsUploadingImages(false);
    }
  };

  return (
    <main className="min-h-[calc(100svh-5rem)] bg-[var(--color-cream)] py-16 pb-28 md:py-24 md:pb-24">
      <section className="container">
        <p className="text-xs font-bold uppercase text-[var(--color-sun-dark)]">
          Host dashboard
        </p>
        <h1 className="mt-3 text-4xl font-bold leading-tight text-[var(--color-bark-dark)] sm:text-5xl">
          Welcome to your host space.
        </h1>
        <p className="mt-4 max-w-2xl text-base leading-7 text-[var(--color-graph)]">
          Manage your properties and prepare your space for your next guests.
        </p>

        <section id="my-properties" className="mt-16 scroll-mt-24">
          <div className="flex flex-wrap items-end justify-between gap-4">
            <div>
              <h2 className="text-3xl font-bold text-[var(--color-bark-dark)]">
                Your properties
              </h2>
              <p className="mt-2 text-sm text-[var(--color-graph)]">
                View and manage the places you host.
              </p>
            </div>
            <button
              type="button"
              onClick={() => {
                setSuccessMessage("");
                setEditingProperty(null);
                setFormError("");
                reset({
                  title: "",
                  description: "",
                  address: "",
                  bedrooms: "",
                  bathrooms: "",
                  maxGuests: "",
                  imageFiles: undefined,
                  amenityIds: [],
                  startTime: "",
                  durationHours: 24,
                  price: "",
                });
                setIsCreating((open) => !open);
              }}
              className="inline-flex min-h-12 items-center justify-center gap-2 rounded bg-[var(--color-sand)] px-5 py-3 text-sm font-bold text-[var(--color-bark-dark)] shadow-[var(--shadow-sm)] transition-colors hover:bg-[var(--color-sun)] focus-visible:outline-2 focus-visible:outline-offset-2 focus-visible:outline-[var(--color-bark)]"
            >
              {isCreating
                ? "Close form"
                : editingProperty
                  ? "Edit property"
                  : "Create property"}
              {isCreating ? (
                <FaTimes aria-hidden="true" size={16} />
              ) : (
                <FaPlus aria-hidden="true" size={16} />
              )}
            </button>
          </div>

          {successMessage && (
            <p className="mt-5 text-sm font-semibold text-green-800" role="status">
              {successMessage}
            </p>
          )}

          {isCreating && (
            <form
              onSubmit={handleSubmit(handleCreateProperty)}
              noValidate
              className="mt-6 grid gap-6 rounded border border-[var(--color-mocha)] bg-white p-6 shadow-[var(--shadow-sm)] sm:grid-cols-2 sm:p-8"
            >
              <h3 className="text-xl font-bold text-[var(--color-bark-dark)] sm:col-span-2">
                {editingProperty ? "Edit property" : "Create a property"}
              </h3>
              {formError && (
                <p
                  className="text-sm text-red-700 sm:col-span-2"
                  role="alert"
                >
                  {formError}
                </p>
              )}

              <AuthField id="title" label="Property title" error={errors.title?.message}>
                <input
                  id="title"
                  name="title"
                  defaultValue={editingProperty?.title ?? ""}
                  aria-invalid={Boolean(errors.title)}
                  aria-describedby={errors.title ? "title-error" : undefined}
                  {...register("title", PROPERTY_VALIDATION.title)}
                  className={`${AUTH_INPUT_CLASS} mt-0.5`}
                />
              </AuthField>

              <div className="sm:col-span-2">
                <AuthField
                  id="description"
                  label="Description"
                  error={errors.description?.message}
                >
                <textarea
                  id="description"
                  name="description"
                  defaultValue={editingProperty?.description ?? ""}
                  aria-invalid={Boolean(errors.description)}
                  aria-describedby={
                    errors.description ? "description-error" : undefined
                  }
                  {...register("description", PROPERTY_VALIDATION.description)}
                  rows={3}
                  className={`${AUTH_INPUT_CLASS} mt-0.5 resize-y`}
                />
                </AuthField>
              </div>

              <div className="sm:col-span-2">
                <AuthField id="address" label="Address" error={errors.address?.message}>
                <input
                  id="address"
                  name="address"
                  defaultValue={editingProperty?.address ?? ""}
                  aria-invalid={Boolean(errors.address)}
                  aria-describedby={errors.address ? "address-error" : undefined}
                  {...register("address", PROPERTY_VALIDATION.address)}
                  className={`${AUTH_INPUT_CLASS} mt-0.5`}
                />
                </AuthField>
              </div>

              <div className="sm:col-span-2">
                <AuthField
                  id="imageFiles"
                  label="Property images"
                  error={errors.imageFiles?.message}
                >
                <input
                  id="imageFiles"
                  name="imageFiles"
                  type="file"
                  accept="image/*"
                  multiple
                  aria-invalid={Boolean(errors.imageFiles)}
                  aria-describedby={
                    errors.imageFiles ? "imageFiles-error" : undefined
                  }
                  {...register("imageFiles", {
                    validate: (files) => {
                      const selectedFiles = Array.from(files ?? []);
                      if (!editingProperty && selectedFiles.length === 0) {
                        return "Choose at least one property image";
                      }
                      return (
                        selectedFiles.every(
                          (file) =>
                            file.type.startsWith("image/") &&
                            file.size <= PROPERTY_VALIDATION.maximumImageSize,
                        ) || "Choose image files no larger than 10 MB each"
                      );
                    },
                  })}
                  className="mt-0.5 min-h-12 w-full cursor-pointer rounded border-2 border-dashed border-[var(--color-bark)]/40 bg-[var(--color-cream)] px-3 py-2 text-sm text-[var(--color-bark-dark)] file:mr-4 file:cursor-pointer file:rounded file:border-0 file:bg-[var(--color-sand)] file:px-4 file:py-2 file:text-sm file:font-semibold file:text-[var(--color-bark-dark)] hover:border-[var(--color-sun)] focus-visible:outline-2 focus-visible:outline-offset-2 focus-visible:outline-[var(--color-sand)]"
                />
                <span className="font-normal text-[var(--color-graph)]">
                  {editingProperty?.imageUrls?.length
                    ? ` ${editingProperty.imageUrls.length} current image${editingProperty.imageUrls.length === 1 ? "" : "s"} will be kept. Selecting new images replaces the current image set.`
                    : ""}
                </span>
                </AuthField>
              </div>

              <AuthField id="bedrooms" label="Bedrooms" error={errors.bedrooms?.message}>
                <input
                  id="bedrooms"
                  name="bedrooms"
                  type="number"
                  defaultValue={editingProperty?.bedrooms ?? ""}
                  aria-invalid={Boolean(errors.bedrooms)}
                  aria-describedby={
                    errors.bedrooms ? "bedrooms-error" : undefined
                  }
                  {...register("bedrooms", PROPERTY_VALIDATION.bedrooms)}
                  className={`${AUTH_INPUT_CLASS} mt-0.5`}
                />
              </AuthField>
              <AuthField id="bathrooms" label="Bathrooms" error={errors.bathrooms?.message}>
                <input
                  id="bathrooms"
                  name="bathrooms"
                  type="number"
                  defaultValue={editingProperty?.bathrooms ?? ""}
                  aria-invalid={Boolean(errors.bathrooms)}
                  aria-describedby={
                    errors.bathrooms ? "bathrooms-error" : undefined
                  }
                  {...register("bathrooms", PROPERTY_VALIDATION.bathrooms)}
                  className={`${AUTH_INPUT_CLASS} mt-0.5`}
                />
              </AuthField>
              <AuthField
                id="maxGuests"
                label="Maximum guests"
                error={errors.maxGuests?.message}
              >
                <input
                  id="maxGuests"
                  name="maxGuests"
                  type="number"
                  defaultValue={editingProperty?.maxGuests ?? ""}
                  aria-invalid={Boolean(errors.maxGuests)}
                  aria-describedby={
                    errors.maxGuests ? "maxGuests-error" : undefined
                  }
                  {...register("maxGuests", PROPERTY_VALIDATION.maxGuests)}
                  className={`${AUTH_INPUT_CLASS} mt-0.5`}
                />
              </AuthField>

              <fieldset
                className="grid gap-3 sm:col-span-2"
                aria-describedby={
                  errors.amenityIds ? "amenityIds-error" : undefined
                }
              >
                <legend className="mb-2 text-sm font-semibold text-[var(--color-bark-dark)]">
                  Amenities
                </legend>
                {isLoadingAmenities ? (
                  <p className="text-sm text-[var(--color-graph)]" role="status">
                    Loading amenities...
                  </p>
                ) : amenitiesError ? (
                  <p className="text-sm text-red-700" role="alert">
                    Could not load amenities: {getErrorMessage(amenitiesError)}
                  </p>
                ) : amenities.length === 0 ? (
                  <p className="text-sm text-[var(--color-graph)]">
                    No amenities are available yet, so a property cannot be
                    created right now.
                  </p>
                ) : (
                  <div className="grid gap-3 sm:grid-cols-2 lg:grid-cols-3">
                    {amenities.map((amenity) => (
                      <label
                        key={amenity.id}
                        className="inline-flex min-h-12 cursor-pointer items-center gap-3 rounded border-2 border-dashed border-[var(--color-bark)]/40 bg-[var(--color-cream)] px-4 py-3 text-sm text-[var(--color-bark-dark)] transition-colors hover:border-[var(--color-sun)] has-[:checked]:border-[var(--color-sun)] has-[:checked]:bg-[var(--color-sand)]/40 has-[:focus-visible]:outline-2 has-[:focus-visible]:outline-offset-2 has-[:focus-visible]:outline-[var(--color-sand)]"
                      >
                        <input
                          type="checkbox"
                          name="amenityIds"
                          value={amenity.id}
                          className="size-5 shrink-0 accent-[var(--color-bark)]"
                          defaultChecked={editingProperty?.amenities?.some(
                            (selectedAmenity) =>
                              selectedAmenity.id === amenity.id,
                          )}
                          {...register("amenityIds", {
                            validate: (selectedIds) =>
                              selectedIds?.length > 0 ||
                              "Select at least one amenity",
                            onChange: () => trigger("amenityIds"),
                          })}
                          aria-invalid={Boolean(errors.amenityIds)}
                        />
                        {amenity.name}
                      </label>
                    ))}
                  </div>
                )}
                {errors.amenityIds && (
                  <p id="amenityIds-error" className="mt-1 text-xs text-red-700">
                    {errors.amenityIds.message}
                  </p>
                )}
              </fieldset>

              {!editingProperty && (
                <>
                  <AuthField
                    id="startTime"
                    label="Check-in time"
                    error={errors.startTime?.message}
                  >
                    <input
                      id="startTime"
                      name="startTime"
                      type="time"
                      aria-invalid={Boolean(errors.startTime)}
                      aria-describedby={
                        errors.startTime ? "startTime-error" : undefined
                      }
                      {...register(
                        "startTime",
                        PROPERTY_VALIDATION.startTime,
                      )}
                      className={`${AUTH_INPUT_CLASS} mt-0.5`}
                    />
                  </AuthField>
                  <AuthField
                    id="durationHours"
                    label="Duration (hours)"
                    error={errors.durationHours?.message}
                  >
                    <input
                      id="durationHours"
                      name="durationHours"
                      type="number"
                      defaultValue="24"
                      aria-invalid={Boolean(errors.durationHours)}
                      aria-describedby={
                        errors.durationHours
                          ? "durationHours-error"
                          : undefined
                      }
                      {...register(
                        "durationHours",
                        PROPERTY_VALIDATION.durationHours,
                      )}
                      className={`${AUTH_INPUT_CLASS} mt-0.5`}
                    />
                  </AuthField>
                  <AuthField
                    id="price"
                    label="Price (PHP)"
                    error={errors.price?.message}
                  >
                    <input
                      id="price"
                      name="price"
                      type="number"
                      step="0.01"
                      aria-invalid={Boolean(errors.price)}
                      aria-describedby={errors.price ? "price-error" : undefined}
                      {...register("price", PROPERTY_VALIDATION.price)}
                      className={`${AUTH_INPUT_CLASS} mt-0.5`}
                    />
                  </AuthField>
                </>
              )}

              {(createProperty.error || updateProperty.error) && (
                <p className="text-sm text-red-700 sm:col-span-2" role="alert">
                  Could not {editingProperty ? "update" : "create"} property:{" "}
                  {getErrorMessage(createProperty.error || updateProperty.error)}
                </p>
              )}
              <div className="flex flex-wrap gap-3 sm:col-span-2">
                <button
                  type="submit"
                  disabled={
                    createProperty.isPending ||
                    updateProperty.isPending ||
                    isUploadingImages ||
                    isLoadingAmenities ||
                    Boolean(amenitiesError) ||
                    amenities.length === 0
                  }
                  className="min-h-12 rounded bg-[var(--color-sand)] px-5 py-3 text-sm font-bold text-[var(--color-bark-dark)] shadow-[var(--shadow-sm)] cursor-pointer disabled:cursor-not-allowed disabled:opacity-50 transition-colors hover:bg-[var(--color-sun)] focus-visible:outline-2 focus-visible:outline-offset-2 focus-visible:outline-[var(--color-bark)]"
                >
                  {isUploadingImages
                    ? "Uploading images..."
                    : createProperty.isPending || updateProperty.isPending
                    ? editingProperty
                      ? "Saving..."
                      : "Creating..."
                    : editingProperty
                      ? "Save changes"
                      : "Create property"}
                </button>
                <button
                  type="button"
                  onClick={() => {
                    setIsCreating(false);
                    setEditingProperty(null);
                    setFormError("");
                  }}
                  className="min-h-12 cursor-pointer rounded border border-[var(--color-bark)]/25 px-5 py-3 text-sm font-bold text-[var(--color-bark-dark)] transition-colors hover:bg-[var(--color-cream)] focus-visible:outline-2 focus-visible:outline-offset-2 focus-visible:outline-[var(--color-bark)]"
                >
                  Cancel
                </button>
              </div>
            </form>
          )}

          {isLoadingProperties ? (
            <p className="mt-8 text-sm text-[var(--color-graph)]" role="status">
              Loading your properties...
            </p>
          ) : propertiesError ? (
            <p className="mt-8 text-sm text-red-700" role="alert">
              Could not load your properties: {getErrorMessage(propertiesError)}
            </p>
          ) : !Array.isArray(properties) ? (
            <p className="mt-8 text-sm text-red-700" role="alert">
              The properties response was not in the expected format.
            </p>
          ) : properties.length === 0 ? (
            <p className="mt-8 rounded border border-[var(--color-mocha)] bg-white p-6 text-sm text-[var(--color-graph)]">
              You have not added any properties yet. Create your first property
              to start hosting.
            </p>
          ) : (
            <>
              <div className="mt-8 grid gap-6 sm:grid-cols-2 lg:grid-cols-4">
                {properties.map((property) => (
                  <PropertyCard
                    key={property.id}
                    {...property}
                    onEdit={() => {
                      setSuccessMessage("");
                      setFormError("");
                      setEditingProperty(property);
                      reset({
                        title: property.title ?? "",
                        description: property.description ?? "",
                        address: property.address ?? "",
                        bedrooms: property.bedrooms ?? "",
                        bathrooms: property.bathrooms ?? "",
                        maxGuests: property.maxGuests ?? "",
                        imageFiles: undefined,
                        amenityIds: property.amenities?.map((amenity) =>
                          String(amenity.id),
                        ) ?? [],
                      });
                      setIsCreating(true);
                    }}
                  />
                ))}
              </div>

              {propertiesPage.totalPages > 1 && (
                <nav
                  aria-label="Your property pages"
                  className="mt-8 flex items-center justify-center gap-4"
                >
                  <button
                    type="button"
                    disabled={page === 0}
                    onClick={() => setPage((currentPage) => currentPage - 1)}
                    className="min-h-11 rounded border border-[var(--color-bark)]/20 px-4 py-2 text-sm font-semibold text-[var(--color-bark-dark)] disabled:cursor-not-allowed disabled:opacity-50"
                  >
                    Previous
                  </button>
                  <span className="text-sm text-[var(--color-graph)]">
                    Page {page + 1} of {propertiesPage.totalPages}
                  </span>
                  <button
                    type="button"
                    disabled={page + 1 >= propertiesPage.totalPages}
                    onClick={() => setPage((currentPage) => currentPage + 1)}
                    className="min-h-11 rounded border border-[var(--color-bark)]/20 px-4 py-2 text-sm font-semibold text-[var(--color-bark-dark)] disabled:cursor-not-allowed disabled:opacity-50"
                  >
                    Next
                  </button>
                </nav>
              )}
            </>
          )}
        </section>
      </section>
      <section
        id="about"
        className="scroll-mt-24 border-y border-[var(--color-bark)]/10 bg-[var(--color-cream)] py-16 pb-28 md:py-20 md:pb-20"
      >
        <div className="container">
          <p className="text-xs font-bold uppercase text-[var(--color-sun-dark)]">
            About Roomance for hosts
          </p>
          <h2 className="mt-3 max-w-2xl text-3xl font-bold leading-tight text-[var(--color-bark-dark)] sm:text-4xl">
            Make it easier for guests to feel at home.
          </h2>
          <p className="mt-4 max-w-2xl text-base leading-7 text-[var(--color-graph)]">
            Roomance helps hosts share their spaces with people looking for a
            comfortable place to pause, recharge, and spend time away.
          </p>
          <div className="mt-10 grid gap-8 border-t border-[var(--color-bark)]/15 pt-8 md:grid-cols-3">
            <article>
              <FaHome
                aria-hidden="true"
                size={28}
                className="text-2xl text-[var(--color-sun)]"
              />
              <h3 className="mt-4 text-lg font-bold text-[var(--color-bark-dark)]">
                Share your space
              </h3>
              <p className="mt-2 text-sm leading-6 text-[var(--color-graph)]">
                Introduce guests to the place you have prepared for them.
              </p>
            </article>
            <article>
              <FaUsers
                aria-hidden="true"
                size={28}
                className="text-2xl text-[var(--color-sun)]"
              />
              <h3 className="mt-4 text-lg font-bold text-[var(--color-bark-dark)]">
                Welcome new guests
              </h3>
              <p className="mt-2 text-sm leading-6 text-[var(--color-graph)]">
                Connect with people planning a short break or a longer stay.
              </p>
            </article>
            <article>
              <FaHeart
                aria-hidden="true"
                size={28}
                className="text-2xl text-[var(--color-sun)]"
              />
              <h3 className="mt-4 text-lg font-bold text-[var(--color-bark-dark)]">
                Host with care
              </h3>
              <p className="mt-2 text-sm leading-6 text-[var(--color-graph)]">
                Help every visit feel thoughtful, comfortable, and memorable.
              </p>
            </article>
          </div>
        </div>
      </section>
      <ContactSection
        eyebrow="Host support"
        title="We're here to help with your hosting."
        description="Have a question about your property or need help hosting? Get in touch with our team."
      />
    </main>
  );
}
