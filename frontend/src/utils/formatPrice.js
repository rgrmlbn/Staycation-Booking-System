export function formatPrice(value, options = {}) {
  if (value == null || value === "") {
    return "Price unavailable";
  }

  const price = Number(value);
  if (!Number.isFinite(price)) {
    return "Price unavailable";
  }

  return new Intl.NumberFormat("en-PH", {
    style: "currency",
    currency: "PHP",
    ...options,
  }).format(price);
}

export default formatPrice;
