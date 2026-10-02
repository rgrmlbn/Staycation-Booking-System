import ContactSection from "../home/sections/ContactSection";
import HeroSection from "../home/sections/HeroSection";
import PropertiesSection from "../home/sections/PropertiesSection";
import AboutSection from "../home/sections/AboutSection";

export default function GuestPage() {
  return (
    <main className="min-h-screen bg-[var(--color-cream)]">
      <HeroSection />
      <PropertiesSection />
      <AboutSection />
      <ContactSection />
    </main>
  );
}
