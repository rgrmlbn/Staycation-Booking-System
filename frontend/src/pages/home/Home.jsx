import AboutSection from "./sections/AboutSection";
import ContactSection from "./sections/ContactSection";
import HeroSection from "./sections/HeroSection";
import PropertiesSection from "./sections/PropertiesSection";

export default function Home() {
  return (
    <main className="min-h-screen bg-[var(--color-cream)]">
      <HeroSection />
      <PropertiesSection />
      <AboutSection />
      <ContactSection />
    </main>
  );
}
