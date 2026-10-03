const PROPERTIES = [
  {
    id: 1,
    hostId: 101,
    hostName: "Mia Santos",
    title: "Sunlit Makati Loft",
    description:
      "A bright, thoughtfully furnished loft in the heart of Makati. Walk to neighborhood cafes, restaurants, and nightlife, then unwind in a calm and comfortable space.",
    bedrooms: 1,
    bathrooms: 1,
    maxGuests: 2,
    address: "Poblacion, Makati City",
    status: "AVAILABLE",
    imageUrls: [
      "https://images.unsplash.com/photo-1505693416388-ac5ce068fe85?auto=format&fit=crop&w=1200&q=80",
      "https://images.unsplash.com/photo-1616486338812-3dadae4b4ace?auto=format&fit=crop&w=1200&q=80",
    ],
    amenities: [
      { id: 1, name: "Wi-Fi" },
      { id: 2, name: "Air conditioning" },
      { id: 3, name: "Kitchen" },
      { id: 4, name: "Workspace" },
    ],
    checkInSlots: [
      { id: 1, startTime: "14:00:00", durationHours: 24, price: 3200 },
      { id: 2, startTime: "18:00:00", durationHours: 24, price: 3000 },
    ],
    reviewScore: 4.9,
    reviewCount: 28,
  },
  {
    id: 2,
    hostId: 102,
    hostName: "Paolo Reyes",
    title: "The Greenhouse in Tagaytay",
    description:
      "A cozy hillside retreat surrounded by greenery and cool Tagaytay air. Gather around the dining table, enjoy the garden, and take in the quiet views.",
    bedrooms: 3,
    bathrooms: 2,
    maxGuests: 6,
    address: "Maharlika West, Tagaytay",
    status: "AVAILABLE",
    imageUrls: [
      "https://images.unsplash.com/photo-1494526585095-c41746248156?auto=format&fit=crop&w=1200&q=80",
      "https://images.unsplash.com/photo-1600210492486-724fe5c67fb0?auto=format&fit=crop&w=1200&q=80",
    ],
    amenities: [
      { id: 5, name: "Garden" },
      { id: 6, name: "Free parking" },
      { id: 7, name: "Kitchen" },
      { id: 8, name: "Mountain view" },
    ],
    checkInSlots: [
      { id: 3, startTime: "15:00:00", durationHours: 24, price: 6500 },
    ],
    reviewScore: 4.8,
    reviewCount: 41,
  },
  {
    id: 3,
    hostId: 103,
    hostName: "Ana Villanueva",
    title: "Coastal Calm in Batangas",
    description:
      "Make room for slow mornings at this relaxed coastal home near the beach. There is plenty of space for a family or group of friends to spend time together.",
    bedrooms: 4,
    bathrooms: 3,
    maxGuests: 8,
    address: "Laiya, San Juan, Batangas",
    status: "AVAILABLE",
    imageUrls: [
      "https://images.unsplash.com/photo-1502672260266-1c1ef2d93688?auto=format&fit=crop&w=1200&q=80",
      "https://images.unsplash.com/photo-1600607687939-ce8a6c25118c?auto=format&fit=crop&w=1200&q=80",
    ],
    amenities: [
      { id: 9, name: "Beach access" },
      { id: 10, name: "Outdoor dining" },
      { id: 11, name: "Air conditioning" },
      { id: 12, name: "Free parking" },
    ],
    checkInSlots: [
      { id: 4, startTime: "13:00:00", durationHours: 24, price: 8500 },
      { id: 5, startTime: "17:00:00", durationHours: 24, price: 8000 },
    ],
    reviewScore: 4.7,
    reviewCount: 36,
  },
  {
    id: 4,
    hostId: 104,
    hostName: "Leo Cruz",
    title: "Quiet Corner of Antipolo",
    description:
      "A peaceful home tucked into Antipolo, with inviting interiors and a breezy patio. Explore nearby local spots or spend a restful day at home.",
    bedrooms: 2,
    bathrooms: 2,
    maxGuests: 4,
    address: "Dela Paz, Antipolo City",
    status: "AVAILABLE",
    imageUrls: [
      "https://images.unsplash.com/photo-1484154218962-a197022b5858?auto=format&fit=crop&w=1200&q=80",
      "https://images.unsplash.com/photo-1600607687920-4e2a09cf159d?auto=format&fit=crop&w=1200&q=80",
    ],
    amenities: [
      { id: 13, name: "Patio" },
      { id: 14, name: "Kitchen" },
      { id: 15, name: "Free parking" },
    ],
    checkInSlots: [
      { id: 6, startTime: "14:00:00", durationHours: 24, price: 4200 },
    ],
    reviewScore: 4.9,
    reviewCount: 19,
  },
  {
    id: 5,
    hostId: 105,
    hostName: "Bea Lim",
    title: "Calm Days in La Union",
    description:
      "A laid-back coastal escape with bright rooms and an easygoing atmosphere, close to surf breaks and the best of San Juan.",
    bedrooms: 2,
    bathrooms: 2,
    maxGuests: 5,
    address: "San Juan, La Union",
    status: "AVAILABLE",
    imageUrls: [
      "https://images.unsplash.com/photo-1600607687939-ce8a6c25118c?auto=format&fit=crop&w=1200&q=80",
    ],
    amenities: [
      { id: 16, name: "Wi-Fi" },
      { id: 17, name: "Outdoor shower" },
      { id: 18, name: "Kitchen" },
    ],
    checkInSlots: [
      { id: 7, startTime: "15:00:00", durationHours: 24, price: 5800 },
    ],
    reviewScore: 4.8,
    reviewCount: 22,
  },
  {
    id: 6,
    hostId: 106,
    hostName: "Nico Garcia",
    title: "Garden Hideaway in Cebu",
    description:
      "A comfortable home with a leafy garden, ideal for recharging after exploring the city or heading out for a day trip.",
    bedrooms: 3,
    bathrooms: 2,
    maxGuests: 6,
    address: "Lahug, Cebu City",
    status: "AVAILABLE",
    imageUrls: [
      "https://images.unsplash.com/photo-1600210492486-724fe5c67fb0?auto=format&fit=crop&w=1200&q=80",
    ],
    amenities: [
      { id: 19, name: "Garden" },
      { id: 20, name: "Wi-Fi" },
      { id: 21, name: "Air conditioning" },
    ],
    checkInSlots: [
      { id: 8, startTime: "14:00:00", durationHours: 24, price: 5200 },
    ],
    reviewScore: 4.6,
    reviewCount: 14,
  },
  {
    id: 7,
    hostId: 107,
    hostName: "Cathy Flores",
    title: "Weekend Nook in Baguio",
    description:
      "A warm, wood-accented stay for crisp Baguio mornings. Make a cup of coffee, settle in with a book, or head out to explore the city.",
    bedrooms: 2,
    bathrooms: 1,
    maxGuests: 4,
    address: "Bakakeng, Baguio City",
    status: "AVAILABLE",
    imageUrls: [
      "https://images.unsplash.com/photo-1505693416388-ac5ce068fe85?auto=format&fit=crop&w=1200&q=80",
    ],
    amenities: [
      { id: 22, name: "Fireplace" },
      { id: 23, name: "Kitchen" },
      { id: 24, name: "Free parking" },
    ],
    checkInSlots: [
      { id: 9, startTime: "13:00:00", durationHours: 24, price: 4800 },
    ],
    reviewScore: 4.9,
    reviewCount: 31,
  },
  {
    id: 8,
    hostId: 108,
    hostName: "Sam Dela Cruz",
    title: "Sunset Stay in Siargao",
    description:
      "A breezy island home with a relaxed pace and room to unwind after a day by the water. Enjoy the open-air feel and tropical surroundings.",
    bedrooms: 2,
    bathrooms: 2,
    maxGuests: 4,
    address: "General Luna, Siargao",
    status: "AVAILABLE",
    imageUrls: [
      "https://images.unsplash.com/photo-1494526585095-c41746248156?auto=format&fit=crop&w=1200&q=80",
    ],
    amenities: [
      { id: 25, name: "Wi-Fi" },
      { id: 26, name: "Outdoor dining" },
      { id: 27, name: "Beach access" },
    ],
    checkInSlots: [
      { id: 10, startTime: "14:00:00", durationHours: 24, price: 7200 },
    ],
    reviewScore: 4.7,
    reviewCount: 26,
  },
];

export default PROPERTIES;
