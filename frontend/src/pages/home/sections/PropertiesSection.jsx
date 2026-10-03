import PropertyCard from "../../../components/PropertyCard";

const PROPERTIES = [
  {
    title: "Sunlit Makati Loft",
    maxGuests: 2,
    address: "Poblacion, Makati City",
    reviewScore: "4.9 / 5",
    image:
      "https://images.unsplash.com/photo-1505693416388-ac5ce068fe85?auto=format&fit=crop&w=900&q=80",
  },
  {
    title: "The Greenhouse in Tagaytay",
    maxGuests: 6,
    address: "Maharlika West, Tagaytay",
    reviewScore: "4.8 / 5",
    image:
      "https://images.unsplash.com/photo-1494526585095-c41746248156?auto=format&fit=crop&w=900&q=80",
  },
  {
    title: "Coastal Calm in Batangas",
    maxGuests: 8,
    address: "Laiya, San Juan, Batangas",
    reviewScore: "4.7 / 5",
    image:
      "https://images.unsplash.com/photo-1502672260266-1c1ef2d93688?auto=format&fit=crop&w=900&q=80",
  },
  {
    title: "Quiet Corner of Antipolo",
    maxGuests: 4,
    address: "Dela Paz, Antipolo City",
    reviewScore: "4.9 / 5",
    image:
      "https://images.unsplash.com/photo-1484154218962-a197022b5858?auto=format&fit=crop&w=900&q=80",
  },
];

export default function PropertiesSection() {
  return (
    <section
      id="properties"
      className="scroll-mt-24 bg-[var(--color-white)] py-16 pb-28 md:py-24 md:pb-24"
    >
      <div className="container">
        <div className="max-w-2xl">
          <p className="text-xs font-bold uppercase text-[var(--color-sun-dark)]">
            Find your next pause
          </p>
          <h2 className="mt-3 text-4xl font-bold leading-tight text-[var(--color-bark-dark)] sm:text-5xl">
            Places made for a little time away.
          </h2>
          <p className="mt-4 text-lg leading-8 text-[var(--color-graph)]">
            Browse stays for slow mornings, easy weekends, and everything in
            between.
          </p>
        </div>

        <div className="mt-12 grid gap-6 sm:grid-cols-2 lg:grid-cols-4">
          {PROPERTIES.map((property) => (
            <PropertyCard key={property.title} {...property} />
          ))}
        </div>
      </div>
    </section>
  );
}
