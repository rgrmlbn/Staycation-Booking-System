import { useState } from "react";
import { useQuery } from "@tanstack/react-query";
import { FaHeart, FaHome, FaPlus, FaTimes, FaUsers } from "react-icons/fa";
import amenityService from "../../services/amenityService";
import propertyService from "../../services/propertyService";
import PropertyCard from "../../components/property/PropertyCard";
import ContactSection from "../../components/sections/ContactSection";
import { AUTH_INPUT_CLASS } from "../auth/AuthLayout";
import {
  useCreateProperty,
  useMyProperties,
  useUpdateProperty,
} from "../../hooks/useProperties";
import { QUERY_KEYS } from "../../utils/constants";

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

  const handleCreateProperty = async (event) => {
    event.preventDefault();
    const form = event.currentTarget;
    const formData = new FormData(form);
    const imageFiles = formData
      .getAll("imageFiles")
      .filter((file) => file instanceof File && file.size > 0);
    const amenityIds = formData.getAll("amenityIds").map(Number);

    if (amenityIds.length === 0) {
      setFormError("Select at least one amenity.");
      return;
    }
    if (!editingProperty && imageFiles.length === 0) {
      setFormError("Choose at least one property image.");
      return;
    }
    if (
      imageFiles.some(
        (file) =>
          !file.type.startsWith("image/") || file.size > 10 * 1024 * 1024,
      )
    ) {
      setFormError("Choose image files no larger than 10 MB each.");
      return;
    }

    setFormError("");
    setIsUploadingImages(true);
    try {
      const uploadedImageUrls = await Promise.all(
        imageFiles.map((file) => propertyService.uploadPropertyImage(file)),
      );
      const propertyData = {
        title: formData.get("title").trim(),
        description: formData.get("description").trim(),
        bedrooms: Number(formData.get("bedrooms")),
        bathrooms: Number(formData.get("bathrooms")),
        maxGuests: Number(formData.get("maxGuests")),
        address: formData.get("address").trim(),
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
                startTime: formData.get("startTime"),
                durationHours: Number(formData.get("durationHours")),
                price: Number(formData.get("price")),
              },
            ],
          };

      mutation(payload, {
        onSuccess: () => {
          form.reset();
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
              onSubmit={handleCreateProperty}
              className="mt-6 grid gap-6 rounded border border-[var(--color-mocha)] bg-white p-6 shadow-[var(--shadow-sm)] sm:grid-cols-2 sm:p-8"
            >
              <h3 className="text-xl font-bold text-[var(--color-bark-dark)] sm:col-span-2">
                {editingProperty ? "Edit property" : "Create a property"}
              </h3>

              <label className="grid gap-2 text-sm font-semibold text-[var(--color-bark-dark)]">
                Property title
                <input
                  name="address"
                  defaultValue={editingProperty?.address ?? ""}
                  required
                  minLength={10}
                  maxLength={200}
                  className={`${AUTH_INPUT_CLASS} mt-0.5`}
                />
              </label>

              <label className="grid gap-2 text-sm font-semibold text-[var(--color-bark-dark)] sm:col-span-2">
                Description
                <textarea
                  name="description"
                  defaultValue={editingProperty?.description ?? ""}
                  required
                  minLength={10}
                  maxLength={200}
                  rows={3}
                  className={`${AUTH_INPUT_CLASS} mt-0.5 resize-y`}
                />
              </label>

              <label className="grid gap-2 text-sm font-semibold text-[var(--color-bark-dark)] sm:col-span-2">
                Address
                <input
                  name="address"
                  defaultValue={editingProperty?.address ?? ""}
                  required
                  minLength={10}
                  maxLength={200}
                  className={`${AUTH_INPUT_CLASS} mt-0.5`}
                />
              </label>

              <label className="grid gap-2 text-sm font-semibold text-[var(--color-bark-dark)] sm:col-span-2">
                Property images
                <input
                  name="imageFiles"
                  type="file"
                  accept="image/*"
                  multiple
                  required={!editingProperty}
                  className="mt-0.5 min-h-12 w-full cursor-pointer rounded border-2 border-dashed border-[var(--color-bark)]/40 bg-[var(--color-cream)] px-3 py-2 text-sm text-[var(--color-bark-dark)] file:mr-4 file:cursor-pointer file:rounded file:border-0 file:bg-[var(--color-sand)] file:px-4 file:py-2 file:text-sm file:font-semibold file:text-[var(--color-bark-dark)] hover:border-[var(--color-sun)] focus-visible:outline-2 focus-visible:outline-offset-2 focus-visible:outline-[var(--color-sand)]"
                />
                <span className="font-normal text-[var(--color-graph)]">
                  Select image files up to 10 MB each.
                  {editingProperty?.imageUrls?.length
                    ? ` ${editingProperty.imageUrls.length} current image${editingProperty.imageUrls.length === 1 ? "" : "s"} will be kept. Selecting new images replaces the current image set.`
                    : ""}
                </span>
              </label>

              <label className="grid gap-2 text-sm font-semibold text-[var(--color-bark-dark)]">
                Bedrooms
                <input
                  name="bedrooms"
                  type="number"
                  defaultValue={editingProperty?.bedrooms ?? ""}
                  min="1"
                  required
                  className={`${AUTH_INPUT_CLASS} mt-0.5`}
                />
              </label>
              <label className="grid gap-2 text-sm font-semibold text-[var(--color-bark-dark)]">
                Bathrooms
                <input
                  name="bathrooms"
                  type="number"
                  defaultValue={editingProperty?.bathrooms ?? ""}
                  min="1"
                  required
                  className={`${AUTH_INPUT_CLASS} mt-0.5`}
                />
              </label>
              <label className="grid gap-2 text-sm font-semibold text-[var(--color-bark-dark)]">
                Maximum guests
                <input
                  name="maxGuests"
                  type="number"
                  defaultValue={editingProperty?.maxGuests ?? ""}
                  min="1"
                  required
                  className={`${AUTH_INPUT_CLASS} mt-0.5`}
                />
              </label>

              <fieldset className="grid gap-3 sm:col-span-2">
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
                          onChange={() => setFormError("")}
                        />
                        {amenity.name}
                      </label>
                    ))}
                  </div>
                )}
              </fieldset>
              {formError && (
                <p className="text-sm text-red-700 sm:col-span-2" role="alert">
                  {formError}
                </p>
              )}

              {!editingProperty && (
                <>
                  <label className="grid gap-2 text-sm font-semibold text-[var(--color-bark-dark)]">
                    Check-in time
                    <input
                      name="startTime"
                      type="time"
                      required
                      className={`${AUTH_INPUT_CLASS} mt-0.5`}
                    />
                  </label>
                  <label className="grid gap-2 text-sm font-semibold text-[var(--color-bark-dark)]">
                    Duration (hours)
                    <input
                      name="durationHours"
                      type="number"
                      min="1"
                      defaultValue="24"
                      required
                      className={`${AUTH_INPUT_CLASS} mt-0.5`}
                    />
                  </label>
                  <label className="grid gap-2 text-sm font-semibold text-[var(--color-bark-dark)]">
                    Price (PHP)
                    <input
                      name="price"
                      type="number"
                      min="0.01"
                      step="0.01"
                      required
                      className={`${AUTH_INPUT_CLASS} mt-0.5`}
                    />
                  </label>
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
